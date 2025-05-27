package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import treemek.mesky.Mesky;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.MovementUtils.Movement;
import treemek.mesky.utils.manager.CameraManager;

public class MiningUtils {
	
	public static boolean isMining = false;
	private static List<double[]> blocks = new ArrayList<>();
	private static double[] currentMiningBlock;
	
	private static HashMap playersLookingAtUs = new HashMap<EntityPlayer, Integer>();
	
	public static List<MiningPath> miningPaths = new ArrayList<>();
	
	public static class MiningPath {
		public List<double[]> coordinatesList;
		
		public MiningPath(List<double[]> coordinatesList) {
			this.coordinatesList = coordinatesList;
		}
	}
	
	public static void startMining() {
		KeyBinding.setKeyBindState(MovementUtils.leftClick.getKeyCode(), false); 
		isMining = true;
		
		InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
		if(inventory.getCurrentItem() != null) {
			String currentId = Utils.getSkyblockId(inventory.getCurrentItem());
	        if(!currentId.equals("GEMSTONE_GAUNTLET") && !currentId.contains("DRILL")) {
	        	boolean hasItem = false;
	        	for (int i = 0; i < 9; i++) {
			        ItemStack item = inventory.mainInventory[i];
			        if(item == null || inventory.currentItem == i) continue;
			        String id = Utils.getSkyblockId(item);
			        if(id.equals("GEMSTONE_GAUNTLET") || id.contains("DRILL")) {
			        	Minecraft.getMinecraft().thePlayer.inventory.currentItem = i;
			        	hasItem = true;
			        	break;
			        }
				}
	        	
	        	if(!hasItem) {
	        		Utils.addMinecraftMessageWithPrefix("No drill in inventory");
	        		stopMining();
	        		return;
	        	}
	        }
		}
		
		if(Utils.isAnyPlayerVisibleBesideNames(new String[] {"Goblin"}) != null) {
			// is player already
			MovementUtils.addMovement(new Movement(() -> {
				Utils.executeCommand("warp island");
			}, 1000));
			MovementUtils.addMovement(new Movement(() -> {
				Utils.executeCommand("mesky miningmacro");
			}, 5000));
		}
		
		playersLookingAtUs = new HashMap<EntityPlayer, Integer>();
		
		int MiningSpeedFromTab = getMiningSpeedFromTab();
		if(MiningSpeedFromTab != -1) {
			Utils.debug("Found mining speed in tab: " + MiningSpeedFromTab);
			SettingsConfig.MiningSpeed.number = (double)MiningSpeedFromTab;
			ConfigHandler.saveSettings();
		}
		
		KeyBinding.onTick(MovementUtils.rightClick);
		
		List<double[]> visibleBlocks = findVisibleMiningBlocks();
		if(visibleBlocks.isEmpty()) return;
		blocks = Utils.sortBlocksByProximity(visibleBlocks, visibleBlocks.get(0));
		if(!blocks.isEmpty()) {
			Utils.addMinecraftMessageWithPrefix("Started macro mining with mining speed: " + EnumChatFormatting.DARK_PURPLE + SettingsConfig.MiningSpeed.number.intValue() + EnumChatFormatting.WHITE + ", its used to detect if something went wrong to set your mining speed use " + EnumChatFormatting.GOLD + "/mesky miningspeed <?>");
			Utils.addMinecraftMessage("To stop mining move. click or type " + EnumChatFormatting.GOLD + "/mesky stopmining");
			currentMiningBlock = blocks.get(0);
			mineBlock(currentMiningBlock);
			antyBug_lastUpdateTimestamp = System.currentTimeMillis();
		}else {
			stopMining();
		}
	}
	
	public static void stopMining() {
		isMining = false;
		blocks.clear();
		currentMiningBlock = null;
		KeyBinding.setKeyBindState(MovementUtils.leftClick.getKeyCode(), false);
	}

	private int UPDATE_INTERVAL = 2500; // 2.5s (checking if players are nearby)
	private long lastUpdateTimestamp = 0;
	
	private static long antyBug_lastUpdateTimestamp = 0;
	private static boolean currentlyMining;
	
	@SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.END && isMining) {
			if(currentMiningBlock != null && MovementUtils.leftClick.isKeyDown()) {
				long currentTime = System.currentTimeMillis();
				
				IBlockState blockState = Utils.getBlockState(Utils.getBlockPos(currentMiningBlock));
				if(!isMiningBlock(blockState)) {
					skipToNextBlock();
				}
				
				int antyBug_UPDATE_INTERVAL = Math.max(2000, getBreakingTime(getBlockHardness(blockState)) * 100); // ticks to destroy block * 50 (for miliseconds) and * 2 because ping
				if(currentTime - antyBug_lastUpdateTimestamp >= antyBug_UPDATE_INTERVAL && isMining) {
					if(currentlyMining) {
						skipToNextBlock();
					}
				}
				
				
		        if (currentTime - lastUpdateTimestamp  >= UPDATE_INTERVAL) { // detecting player
		        	lastUpdateTimestamp = currentTime;
		        	
		        	EntityPlayer visiblePlayer = Utils.isAnyPlayerVisibleBesideNames(new String[] {"Goblin"});
		        	
		            if(visiblePlayer != null) {
		            	Utils.sendDiscordWebhook("Saw player " + visiblePlayer.getName());
		            	int m = (int) playersLookingAtUs.getOrDefault(visiblePlayer, 0);
		            	playersLookingAtUs.put(visiblePlayer, m+1);
		            	
		            	if(m+1 > 3) { // player stays in way
		            		Utils.sendDiscordWebhook("Player " + visiblePlayer.getName() + " stayed in view for too long teleporting to new lobby");
		            		
		            		MovementUtils.addMovement(new Movement(() -> {
		        				Utils.executeCommand("warp island");
		        			}, 1000));
		        			MovementUtils.addMovement(new Movement(() -> {
		        				Utils.executeCommand("mesky miningmacro");
		        			}, 5000));
		        			return;
		            	}
		            	
		            	if(m == 0) { // first time seeing
			            	float[] rotation = RotationUtils.getPlayerRotationToLookAtEntity(visiblePlayer);
			            	RotationUtils.rotateCurveTo(RotationUtils.getNeededYawFromMinecraftRotation(rotation[0]), RotationUtils.getNeededPitchFromMinecraftRotation(rotation[1]), 0.5f, false);
			            	KeyBinding.setKeyBindState(MovementUtils.leftClick.getKeyCode(), false);
			            	
			            	RotationUtils.addTask(() -> {
				            	new Thread(() -> {
				            		for (int i = 0; i < 10; i++) {
										try {
											Thread.sleep(100);
											
											KeyBinding.setKeyBindState(MovementUtils.shift, true);
											
											Thread.sleep(100);
											
											KeyBinding.setKeyBindState(MovementUtils.shift, false);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
				            		
				            		mineBlock(currentMiningBlock);
				            		
				            	}).start();
			            	});
		            	}
		            }
		        }
			}
		}
	}
	
	public static List<double[]> findVisibleMiningBlocks() {
		int searchRadius = 5; // 
		
	    List<double[]> miningBlocks = new ArrayList<>();

	    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	    WorldClient world = Minecraft.getMinecraft().theWorld;
	    
	    if (player == null || world == null) return new ArrayList<>();
	    
	    Vec3 playerEyes = player.getPositionEyes(1.0f);
	    Vec3 lookVector = player.getLook(1.0f);
	    float reachDistance = 20.25F; // 4.5^2

	    // Loop over all blocks in a radius around the player
	    for (int y = -searchRadius; y <= searchRadius; y++) {
	    	for (int x = -searchRadius; x <= searchRadius; x++) {
	            for (int z = -searchRadius; z <= searchRadius; z++) {
	                BlockPos blockPos = player.getPosition().add(x, y, z);
	                IBlockState block = world.getBlockState(blockPos);

	                if (isMiningBlock(block) || block == Blocks.bedrock) {
	                    double[] result = Utils.getRaycastToBlock(blockPos, false);

	                    if (result != null) {
	                        if (Utils.distance(playerEyes.xCoord, playerEyes.yCoord, playerEyes.zCoord, result[0], result[1], result[2]) <= reachDistance) { // Check if the block is within reach
	                            miningBlocks.add(result);
	                        }
	                    }
	                }
	            }
	        }
	    }

	    return miningBlocks;
	}

	 private static boolean isMiningBlock(IBlockState state) {
	        Block block = state.getBlock();
	        int meta = block.getMetaFromState(state);

	        return block == Blocks.prismarine || 
	               (block == Blocks.wool && (meta == 3 || meta == 7)) || // Light blue wool (11), Gray wool (7)
	               (block == Blocks.stone && meta == 4); // Polished Diorite
	    }
	 
	 private static int getBlockHardness(IBlockState state) {
		    Block block = state.getBlock();
		    int meta = block.getMetaFromState(state);

		    // Mithril variants
		    if (block == Blocks.prismarine) return 800;        // Prismarine
		    if (block == Blocks.wool && meta == 7) return 500;  // Gray Wool
		    if (block == Blocks.wool && meta == 11) return 1500; // Light Blue Wool

		    // Titanium
		    if (block == Blocks.stone && meta == 4) return 2000; // Polished Granite

		    // Public Island vanilla blocks
		    if (block == Blocks.stone) return 15;
		    if (block == Blocks.cobblestone) return 20;
		    if (block == Blocks.end_stone) return 30;
		    if (block == Blocks.obsidian) return 500;
		    if (block == Blocks.netherrack) return 4;
		    if (block == Blocks.ice) return 5;

		    // Ores
		    if (block == Blocks.coal_ore) return 30;
		    if (block == Blocks.iron_ore) return 30;
		    if (block == Blocks.gold_ore) return 30;
		    if (block == Blocks.lapis_ore) return 30;
		    if (block == Blocks.redstone_ore) return 30;
		    if (block == Blocks.emerald_ore) return 30;
		    if (block == Blocks.diamond_ore) return 30;
		    if (block == Blocks.quartz_ore) return 30;

		    return 100; // Default fallback
		}

	 
	 private static int getBreakingTime(int hardness) {
		 return (hardness*30)/SettingsConfig.MiningSpeed.number.intValue(); // time in ticks
	 }
	
	public static void mineBlock(double[] pos) {
		InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
		boolean hasItem = false;
		
		if(inventory.getCurrentItem() != null) {
			String id = Utils.getSkyblockId(inventory.getCurrentItem());
	        if(id.equals("GEMSTONE_GAUNTLET") || id.contains("DRILL")) {
	        	hasItem = true;
	        }
		}
		
		if(!hasItem) {
			for (int i = 0; i < 9; i++) {
		        ItemStack item = inventory.mainInventory[i];
		        if(item == null || inventory.currentItem == i) continue;
		        String id = Utils.getSkyblockId(item);
		        if(id.equals("GEMSTONE_GAUNTLET") || id.contains("DRILL")) {
		        	hasItem = true;
		        	Minecraft.getMinecraft().thePlayer.inventory.currentItem = i;
		        	break;
		        }
			}
		}
		
		if(hasItem) {
			RotationUtils.clearAllRotations();
			float[] rotation = RotationUtils.getPlayerRotationToLookAtVector(pos[0], pos[1], pos[2]);
			RotationUtils.rotateCurveTo(RotationUtils.getNeededYawFromMinecraftRotation(rotation[0]), RotationUtils.getNeededPitchFromMinecraftRotation(rotation[1]), 0.2f, true);
			KeyBinding.setKeyBindState(MovementUtils.leftClick.getKeyCode(), true); 
			
			RotationUtils.addTask(() -> {
				antyBug_lastUpdateTimestamp = System.currentTimeMillis();
				currentlyMining = true;
				BlockPos lookedAt = Utils.getBlockLookingAt(4.5f);
				if(lookedAt != null) {
					if(lookedAt.distanceSq(Utils.getBlockPos(pos)) != 0) {
						skipToNextBlock();
					}
				}else {
					skipToNextBlock();
				}
			});
		}else {
			stopMining();
			Utils.addMinecraftMessageWithPrefix("No mining item in slotbar");
		}
	}
	
	private static void skipToNextBlock() {
		currentlyMining = false;
		final double[] oldCurrentBlock = currentMiningBlock;
		
		blocks.remove(currentMiningBlock);
		
		if(!blocks.isEmpty()) {
			currentMiningBlock = blocks.get(0);
			if(Minecraft.getMinecraft().theWorld.getBlockState(Utils.getBlockPos(currentMiningBlock)) == Blocks.bedrock) {
				skipToNextBlock();
				return;
			}
			mineBlock(currentMiningBlock);
		}else {
			List<double[]> visibleBlocks = findVisibleMiningBlocks();
			if(visibleBlocks.isEmpty()) {
				MovementUtils.addMiniMovement(new Movement(() -> {
					blocksListIsEmpty(oldCurrentBlock, findVisibleMiningBlocks());
            	}, 5000));
			}else {
				blocksListIsEmpty(oldCurrentBlock, visibleBlocks);
			}
		}
	}
	
	private static void blocksListIsEmpty(double[] oldCurrentBlock, List<double[]> visibleBlocks) {
		if(oldCurrentBlock == null) oldCurrentBlock = visibleBlocks.get(0);
		blocks = Utils.sortBlocksByProximity(visibleBlocks, oldCurrentBlock);
		if(!blocks.isEmpty()) {
			currentMiningBlock = blocks.get(0);
			if(Minecraft.getMinecraft().theWorld.getBlockState(Utils.getBlockPos(currentMiningBlock)) == Blocks.bedrock) {
				skipToNextBlock();
				return;
			}
			mineBlock(currentMiningBlock);
		}else {
			stopMining();
		}
	}
	
	private static int getMiningSpeedFromTab() {
	    Collection<NetworkPlayerInfo> players = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();

	    for (NetworkPlayerInfo info : players) {
	        String name = info.getDisplayName() != null
	            ? info.getDisplayName().getUnformattedText()
	            : info.getGameProfile().getName();
	        
	        if (name.contains("Mining Speed")) {
	        	try {
	        		String speed = name.substring(name.indexOf('\u2E15') + 1);
	        		Utils.debug(EnumChatFormatting.DARK_PURPLE + "Found mining speed: " + speed);
	        		return Integer.parseInt(speed);
				} catch (Exception e) {
					Utils.writeError(e);
				}
	            
	        }
	    }

	    return -1; // Not found
	}

	public static void miningmacroPath(int path) {
		Utils.addMinecraftMessageWithPrefix("Executing macro mining path "+ path + ", to change path use /mesky miningmacro changepath");
		
		if(path == 1) {
			MovementUtils.addMovement(new Movement(new BlockPos(0, 166, -11), MovementUtils::useAOTVto, 3000));
			MovementUtils.addMovement(new Movement(new BlockPos(8, 188, -6), MovementUtils::useAOTVto, 1500));
			MovementUtils.addMovement(new Movement(new BlockPos(23, 229, 5), MovementUtils::useAOTVto, 1500));
			return;
		}
		if(path == 2) {
			MovementUtils.addMovement(new Movement(new BlockPos(0, 166, -11), MovementUtils::useAOTVto, 3000));
			MovementUtils.addMovement(new Movement(new BlockPos(0, 182, 23), MovementUtils::useAOTVto, 1500));
			MovementUtils.addMovement(new Movement(new BlockPos(0, 181, 34), MovementUtils::useAOTVto, 1500));
			MovementUtils.addMovement(new Movement(new BlockPos(8, 161, 66), MovementUtils::useAOTVto, 1500));
			MovementUtils.addMovement(new Movement(new BlockPos(8, 161, 106), MovementUtils::useAOTVto, 1500));
			MovementUtils.addMovement(new Movement(new BlockPos(9, 176, 139), MovementUtils::useAOTVto, 1500));
			MovementUtils.addMovement(new Movement(new BlockPos(11, 225, 129), MovementUtils::useAOTVto, 1500));
			return;
		}
		
		if(path > 2) {
			path = path-3;
			if(path < miningPaths.size()) {
				MiningPath list = miningPaths.get(path);
				for (int i = 0; i < list.coordinatesList.size(); i++) {
					double[] coords = list.coordinatesList.get(i);
					int time = (i == 0)?3000:1500;
					MovementUtils.addMovement(new Movement(new BlockPos(coords[0], coords[1], coords[2]), MovementUtils::useAOTVto, time));
				}
				
				return;
			}else {
				Utils.addMinecraftMessage(EnumChatFormatting.RED + "There's no path:" + path + ", executing default path 1");
			}
		}
	
		MovementUtils.addMovement(new Movement(new BlockPos(0, 166, -11), MovementUtils::useAOTVto, 3000));
		MovementUtils.addMovement(new Movement(new BlockPos(8, 188, -6), MovementUtils::useAOTVto, 1500));
		MovementUtils.addMovement(new Movement(new BlockPos(23, 229, 5), MovementUtils::useAOTVto, 1500));
	}
	
	@SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
		String message = event.message.getUnformattedText();
		if(message.contains(":")) return;
		if(!isMining) return;
		
		if(ColorUtils.removeMinecraftTextColor(message).contains("Mining Speed Boost is now available!")) {
			Utils.debug("Detected drill ability");
			KeyBinding.setKeyBindState(MovementUtils.leftClick.getKeyCode(), false); 
			
			MovementUtils.addMovement(new Movement(() -> {
				KeyBinding.onTick(MovementUtils.rightClick);
			}, 1000));
			
		}
		
		if(ColorUtils.removeMinecraftTextColor(message).contains("You used your Mining Speed Boost Pickaxe Ability!")) {
			KeyBinding.setKeyBindState(MovementUtils.leftClick.getKeyCode(), true); 
		}
		
		if(message.contains("Evacuating to Hub...")) {
			MovementUtils.addMovement(new Movement(() -> {
				Utils.executeCommand("mesky miningmacro");
			}, 5000));
		}
	}
	
	@SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (event.button != -1 && !event.buttonstate) {
            int button = event.button; // 0 = left click, 1 = right click, 2 = middle click
            

            if (button == 0) {
            	stopMining();
            }
        }
    }
	
	@SubscribeEvent
    public void onMouseMoved(MouseEvent event) {
		if(isMining && !CameraManager.lockCamera) {
			if (event.dx != 0 || event.dy != 0) {
				stopMining();
			}
		}
	}
	
    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
    	if (event.world.isRemote && event.entity instanceof EntityPlayer && isMining) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player == Minecraft.getMinecraft().thePlayer) {
            	stopMining();
            	
            	if(Location.checkTabLocation() == Location.HUB) {
            		Utils.addMinecraftMessageWithPrefix("Transfered to Hub while mining, coming back in 5s (move to cancel)");
            		MovementUtils.addMovement(new Movement(() -> {
        				Utils.executeCommand("mesky miningmacro");
        			}, 5000));
            	}
            }
        }
    }
}
