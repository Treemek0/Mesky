package treemek.mesky.features.illegal;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.RotationUtils;
import treemek.mesky.utils.Utils;

public class AutoFish {
	
	int rightClick = Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode();
	int leftClick = Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode();
	int shift = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
	float[] lastFishingHeadRotation = null;
	BlockPos lastBlockPos;
	BlockPos hookPos;
	
	int fishingRodSlot;
	private float[] originalRotation = new float[] {0f, 0f};
	Entity attacker = null;

	private boolean killingCreatures;
	private boolean blokEverything = true;
	
	private Thread fishingCycle;
	private Thread antyAfkCycle;
	
	private long lastUpdateTimestamp = 0;
	private String lastMessage;
    private static final long UPDATE_INTERVAL = 2500;
	
    // make entity killing from entitylastjoin event in detection (not that important but yk)
    
    // if blocked then casting hook will unblock it
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.entityLiving == Minecraft.getMinecraft().thePlayer && !FishingTimer.isFishing) {
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() == Items.fishing_rod) {
                    if (blokEverything) {
                        blokEverything = false;
                        lastBlockPos = Minecraft.getMinecraft().thePlayer.getPosition();
                    }
                    
                    originalRotation = new float[] {Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.rotationPitch};
                }
            }
        }
    }
    
	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if(Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) return;
		if (event.phase == TickEvent.Phase.START) {
			if(SettingsConfig.AutoFish.isOn) {
				Minecraft mc = Minecraft.getMinecraft();
				
				if(!FishingTimer.isFishing) return;
			
				if(!JawbusDetector.detectedJawbuses.isEmpty()) { // blok autofish if jawbus
					blokEverything = true;
            		if(fishingCycle != null && fishingCycle.isAlive())  fishingCycle.interrupt();
            		RotationUtils.clearAllRotations();
				}
				
				if(Minecraft.getMinecraft().thePlayer.hurtTime > 0 && attacker == null && !blokEverything) { // if missed sea creature then it will attack us and we can detect that
					if(killingCreatures) return; // is already killing
					attacker = detectAttackerSeaCreature();
					if(attacker != null) killAttackingSeaCreature(attacker);
				}
			}
		}
		
		long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTimestamp >= UPDATE_INTERVAL) { // every X time we check distance moved from the last time and if to big then we detect that its probably player that was moving and blok everything
            lastUpdateTimestamp = currentTime;
            
            if(lastBlockPos != null && !blokEverything) {
            	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            	double distanceSq = lastBlockPos.distanceSq(player.posX, lastBlockPos.getY(), player.posZ);
            	double distance = Math.sqrt(distanceSq);
            	
            	if(distance > 10f) {
            		blokEverything = true;
            		if(fishingCycle != null && fishingCycle.isAlive())  fishingCycle.interrupt();
            		RotationUtils.clearAllRotations();
            		if(FishingTimer.fishingHook != null) {
            			Alerts.DisplayCustomAlerts("Stopped AutoFish (distance moved > 10)", 1000, 0, new int[] {50, 50}, 2f);
            		}
            	}
            }
            
            lastBlockPos = Minecraft.getMinecraft().thePlayer.getPosition();
        }
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		if(event.type == 0 || event.type == 1) {
			lastMessage = event.message.getUnformattedText();
		}
	}
	
	@SubscribeEvent
    public void onPacketReceived(PlaySoundEvent event) {
        if (Minecraft.getMinecraft().theWorld != null) {
        	if(FishingTimer.isFishing && FishingTimer.isInWater && SettingsConfig.AutoFish.isOn) {
        		if(FishingTimer.fishingHook.ticksExisted < 10) return;
	            if (event.name.equals("game.player.swim.splash")) {
	            	
	                if(lastMessage.contains("swims back beneath the lava...") && !lastMessage.contains(":")){ // golden fish
	                	return;
	                }
	            	
	            	
	            	fishingRodSlot = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
					readyToFish();
					Alerts.DisplayCustomAlerts("AutoFish", 500, 0, new int[] {50, 20}, 2f);
	            }
        	}
        }
	}
	
	
	private void antyAfk() {
		if(fishingCycle != null && fishingCycle.isAlive()) fishingCycle.interrupt(); 
		if(antyAfkCycle != null && antyAfkCycle.isAlive()) antyAfkCycle.interrupt(); 
		
		antyAfkCycle = new Thread(() -> {
			try {
				Thread.sleep(50);
				if(FishingTimer.isFishing) {
					KeyBinding.onTick(rightClick);
				}
				Alerts.DisplayCustomAlerts("AntyAFK", 7000, 0, new int[] {50, 40}, 2f);
				long delay = 150 + Math.round((new Random().nextFloat()*100));
				int randomInt = 15 + new Random().nextInt(11); // 15 - 25
				
				float yawControlPoint = Utils.getRandomizedMinusOrPlus(80 + new Random().nextInt(10));
				float pitch = -120 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10));
				RotationUtils.rotateBezierCurveTo(0, pitch, yawControlPoint, -70, 1.25f, true);
				RotationUtils.rotateBezierCurveTo(0, -pitch, -yawControlPoint, 70, 1.25f, true);
				boolean isShifting = false;

				for (int i = 0; i < randomInt; i++) {
					Thread.sleep(delay + Math.round((new Random().nextFloat()*10)));
		            KeyBinding.onTick(leftClick);
		            if(isShifting) {
		            	KeyBinding.setKeyBindState(shift, false);
		            	isShifting = false;
		            }else {
		            	KeyBinding.setKeyBindState(shift, true);
		            	isShifting = true;
		            }
		            
		            if(blokEverything) {
						KeyBinding.setKeyBindState(shift, false);
						return;
					}
				}
				
				RotationUtils.clearAllRotations();
				KeyBinding.setKeyBindState(shift, false);
				if(!blokEverything) {
			    	throwNewHook();
				}
			} catch (InterruptedException e) {
                e.printStackTrace();
            }
		});
		
		antyAfkCycle.start();
	}


	public void readyToFish() {
		if(fishingCycle != null && fishingCycle.isAlive()) fishingCycle.interrupt(); 
		if(antyAfkCycle != null && antyAfkCycle.isAlive()) antyAfkCycle.interrupt(); 
		
		fishingCycle = new Thread(() -> {
            try {
            	float randomTime = (new Random().nextFloat()+0.4f);
        		
        		long endTime = (long)Math.round(randomTime * 100);
        		Thread.sleep(endTime);
        		
        		if(FishingTimer.fishingHook != null && FishingTimer.isInWater) {
        			originalRotation = new float[] {Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.rotationPitch};
				}
        		
        		fishedItem = null;
        		if(FishingTimer.isFishing && !blokEverything) {
        			hookPos = FishingTimer.fishingHook.getPosition();
        			KeyBinding.onTick(rightClick);
        		}
                killSeaCreature();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        });
		if(!blokEverything) fishingCycle.start();
	}
	
	
	// something isnt working (idk what because i didnt debuged it)
	
	public void killSeaCreature() {
        try {
			if(SettingsConfig.KillSeaCreatures.isOn && !blokEverything) {
				InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
	            for (int i = 0; i < 9; i++) {
	                ItemStack item = inventory.mainInventory[i];
	                if (item != null && item.getItem() == Items.iron_sword) {
	                	List<String> itemLore = Utils.getItemLore(item);
	                	
	                	if(itemLore == null || itemLore.size() == 0) return; 
	                	if(!Utils.containsWord(itemLore, "Wither Impact")) return;
	                	
	                	Thread.sleep(400);
	                	
	                	Entity seaCreature = detectNewSeaCreature();
	                	
	                	if(seaCreature == null) {
	                		throwNewHook();
	                		return;
	                	}
	                	
	                	if(Minecraft.getMinecraft().thePlayer.rotationPitch < 88) {
	                		RotationUtils.rotateCurveTo(0, RotationUtils.getNeededPitchFromMinecraftRotation(90), 0.1f, true);
	                	}
	                	
	                	killingCreatures = true;
	                	Alerts.DisplayCustomAlerts("Killing " + seaCreature.getName(), 1500, 0, new int[] {50, 20}, 2f);
	                	
	                	Minecraft.getMinecraft().thePlayer.inventory.currentItem = i;
	                	
	                	Thread.sleep(150 + Math.round((new Random().nextFloat()*10)));
	                	
	                	
	                	long delay = 120 + Math.round((new Random().nextFloat()/2*100));
	                	
	                	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	                	
	                	int antyWrongDetection = 0;
	                	while(seaCreature.isEntityAlive() && antyWrongDetection < 20) {
	                			if(Minecraft.getMinecraft().thePlayer.inventory.currentItem != i) { blokEverything = true; return; }
	                			if(Minecraft.getMinecraft().thePlayer.rotationPitch > 88) {
		                			if(seaCreature.getDistance(player.posX, player.posY, player.posZ) < 7) {
		                				killingCreatures = true;
		                				KeyBinding.onTick(rightClick);
		                			}else {
		                				killingCreatures = false;
		                			}
	                			}else {
	                				RotationUtils.clearAllRotations();
	                				RotationUtils.rotateCurveTo(0, RotationUtils.getNeededPitchFromMinecraftRotation(90), 0.1f, true);
	                			}
	                			
	                			Thread.sleep(delay + Math.round((new Random().nextFloat()*10)));
	                			antyWrongDetection++;
	        			}
	                	
	                	// because sometimes these small slimes dont die and i dont really want to make system for that lol
            			if(Minecraft.getMinecraft().thePlayer.rotationPitch > 88) {
            				KeyBinding.onTick(rightClick);
            				Thread.sleep(delay + Math.round((new Random().nextFloat()*10)));
            			}

	    				killingCreatures = false;
	                	throwNewHook();
	                    break;
	                }
	            }
			}else {
				throwNewHook();
			}
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

	
	// idk if it works
	private void throwNewHook() {
		try {
			if(SettingsConfig.AutoThrowHook.isOn) {
				if(Math.abs(Minecraft.getMinecraft().thePlayer.rotationYaw - originalRotation[0]) > 5 || Math.abs(Minecraft.getMinecraft().thePlayer.rotationPitch - originalRotation[1]) > 2) {
					float noise = Utils.getRandomizedMinusOrPlus(new Random().nextFloat());
					RotationUtils.rotateCurveTo(RotationUtils.getNeededYawFromMinecraftRotation(originalRotation[0] + noise*2), RotationUtils.getNeededPitchFromMinecraftRotation(originalRotation[1] + noise), 0.2f, true);
				}
				
				Minecraft.getMinecraft().thePlayer.inventory.currentItem = fishingRodSlot;
				
				
				Thread.sleep(750 + Math.round((new Random().nextFloat()*1000)/2));
				
				if(Minecraft.getMinecraft().thePlayer.inventory.currentItem != fishingRodSlot) {
					blokEverything = true;
					return;
				}
				
				if(!FishingTimer.isFishing) {
					Alerts.DisplayCustomAlerts("Auto hook", 1000, 0, new int[] {50, 20}, 2f);
					KeyBinding.onTick(rightClick);
				}else {
					Alerts.DisplayCustomAlerts("Bugged Auto hook", 1000, 0, new int[] {50, 20}, 2f);
					KeyBinding.onTick(rightClick);
					Thread.sleep(500);
					KeyBinding.onTick(rightClick);
				}
				
				
				Thread.sleep(3500);
				if(!FishingTimer.isInWater && FishingTimer.isFishing) {
					Alerts.DisplayCustomAlerts("Hook blocked", 500, 0, new int[] {50, 20}, 2f);
					RotationUtils.rotateStraightTo(0, -2, 0.1f, false);
					KeyBinding.onTick(rightClick);
					Thread.sleep(500);
					if(!FishingTimer.isFishing) {
						KeyBinding.onTick(rightClick);
					}
				}
	    	}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	int fishedNothing = 0;
	Entity fishedItem;
	public Entity detectNewSeaCreature() {
		List<Entity> entities = Minecraft.getMinecraft().theWorld.loadedEntityList;
		entities.sort(Comparator.comparingInt(entity -> entity.ticksExisted));
		
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			
			if(entity.ticksExisted < 10) {
				if(entity instanceof EntityPlayer) continue;
				if(entity instanceof EntityXPOrb) continue;
				if(entity instanceof EntityFishHook) continue;
				if(entity instanceof EntityItem) continue;
				if(entity instanceof EntityArmorStand) continue;
				
				if(!(entity instanceof EntityLivingBase)) continue;
				
				EntityLivingBase entityLiving = (EntityLivingBase) entity;
				if(entityLiving != null && entityLiving.getLastAttacker() != null) {
					if(entityLiving.getLastAttacker() == Minecraft.getMinecraft().thePlayer) {
						fishedNothing = 0;
						return entity;
					}
				}
				
				
				double posY = (entity.posY - player.posY < 3)?entity.posY:player.posY;
				if(entity.getDistance(player.posX, posY, player.posZ) < 5) {
					fishedNothing = 0;
					return entity;
				}
				
				if(hookPos != null) {
					double hookPosY = (entity.posY - hookPos.getY() < 3)?entity.posY:hookPos.getY();
					if(entity.getDistance(hookPos.getX(), hookPosY, hookPos.getZ()) < 5) {
						fishedNothing = 0;
						return entity;
					}
				}
			
			}
		}
		
		if(fishedItem == null) {
			if((!lastMessage.contains("GOOD CATCH!") && !lastMessage.contains("GREAT CATCH!") && !lastMessage.contains("TROPHY FISH!")) || lastMessage.contains(":")) {
				fishedNothing++;
			}else {
				lastMessage = "";
				fishedNothing = 0;
			}
		}else {
			fishedItem = null;
			fishedNothing = 0;
		}
		
		if(fishedNothing >= 2) {
			fishedNothing = 0;
			antyAfk();
		}
		return null;
	}
	
	@SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(!SettingsConfig.AutoFish.isOn) return;
    	Entity entity = event.entity;
    	if(entity instanceof EntityItem) {
	    	if(hookPos != null) {
				double hookPosY = (entity.posY - hookPos.getY() < 3)?entity.posY:hookPos.getY();
				if(entity.getDistance(hookPos.getX(), hookPosY, hookPos.getZ()) < 2) {
					fishedItem = entity;
				}
			}
    	}
    }
	
	public Entity detectAttackerSeaCreature() {
		List<Entity> entities = Minecraft.getMinecraft().theWorld.loadedEntityList;
		
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			
			if(entity instanceof EntityPlayer) continue;
			if(entity instanceof EntityArmorStand) continue;
			if(entity instanceof EntityItem) continue;
			if(entity instanceof EntityXPOrb) continue;
			if(entity instanceof EntityFishHook) continue;
			if(!(entity instanceof EntityLivingBase)) continue;
			
			EntityLivingBase entityLiving = (EntityLivingBase) entity;
			if(entityLiving != null && entityLiving.getLastAttacker() != null) {
				if(entityLiving.getLastAttacker() == Minecraft.getMinecraft().thePlayer) {
					return entity;
				}
			}
			
			double posY = (entity.posY - player.posY < 5)?entity.posY:player.posY;
			if(entity.getDistance(player.posX, posY, player.posZ) < 5) {
				return entity;
			}
		}
		return null;
	}
	
	
	public void killAttackingSeaCreature(Entity entity) {
		new Thread(() -> {
	        try {
				if(SettingsConfig.KillSeaCreatures.isOn) {
					InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
		            for (int i = 0; i < 9; i++) {
		                ItemStack item = inventory.mainInventory[i];
		                if (item != null && item.getItem() == Items.iron_sword) {
		                	List<String> itemLore = Utils.getItemLore(item);
		                	
		                	if(itemLore == null || itemLore.size() == 0) return; 
		                	if(!Utils.containsWord(itemLore, "Wither Impact")) return;
		                	
		                	Thread.sleep(500);
		                	if(killingCreatures) return;
		                	
		                	if(Minecraft.getMinecraft().thePlayer.rotationPitch < 88) {
		                		RotationUtils.rotateCurveTo(0, RotationUtils.getNeededPitchFromMinecraftRotation(90), 0.1f, true);
		                	}
		                	
		                	killingCreatures = true;
		                	Alerts.DisplayCustomAlerts("Killing attacking " + entity.getName(), 1500, 0, new int[] {50, 20}, 2f);
		                	Thread.sleep(Math.round((new Random().nextFloat()*100)));
		                	
		                	Minecraft.getMinecraft().thePlayer.inventory.currentItem = i;
		                	
		                	long delay = 150 + Math.round((new Random().nextFloat()/2*100));
		                	
		                	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		                	
		                	int antyWrongDetection = 0;
		                	while(entity.isEntityAlive() && antyWrongDetection < 20) {
		                			Thread.sleep(delay + Math.round((new Random().nextFloat()*10)));
		                			antyWrongDetection++;
		                			
		                			if(Minecraft.getMinecraft().thePlayer.inventory.currentItem != i) { blokEverything = true; return; }
		                			if(Minecraft.getMinecraft().thePlayer.rotationPitch < 88) { RotationUtils.clearAllRotations(); RotationUtils.rotateCurveTo(0, RotationUtils.getNeededPitchFromMinecraftRotation(90), 0.1f, true); continue; }
		                			
		                			if(entity.getDistance(player.posX, player.posY, player.posZ) < 7) {
		                				KeyBinding.onTick(rightClick);
		                			}
		        			}
		                	attacker = null;
		    				killingCreatures = false;
		                	throwNewHook();
		                    break;
		                }
		            }
				}else {
					throwNewHook();
				}
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
		}).start();
	}
}
