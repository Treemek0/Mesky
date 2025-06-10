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
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
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
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.MovementUtils;
import treemek.mesky.utils.RotationUtils;
import treemek.mesky.utils.Utils;

public class AutoFish {
	
	int rightClick = Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode();
	int leftClick = Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode();
	int shift = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
	BlockPos fishingPlace;
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
	private String lastMessage = "";
    private static final long UPDATE_INTERVAL = 2500;
	
	int fishedNothing = 0;
	Entity fishedItem;
	
	boolean detectSeaCreature = false;
    
    // make entity killing from entitylastjoin event in detection (not that important but yk)
    
    // if blocked then casting hook will unblock it
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.entityLiving == Minecraft.getMinecraft().thePlayer && !FishingTimer.isFishing) {
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                if (isFishingRod(Minecraft.getMinecraft().thePlayer.getHeldItem())) {
                	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                	
                    if (blokEverything) {
                    	if(!Locations.getRegion().contains("Carnival") && Locations.currentLocation != Location.KUUDRA && Locations.currentLocation != Location.CATACOMBS) {
                    		blokEverything = false;
                    	}
                    	
                        lastBlockPos = player.getPosition();
                    }else {
                    	if(Locations.getRegion().contains("Carnival") || Locations.currentLocation == Location.KUUDRA || Locations.currentLocation == Location.CATACOMBS) {
                    		blokEverything = true;
                    	}
                    }
                    
                    if(!MovementUtils.isCurrentlyMoving()) fishingPlace = Utils.playerPosition();
                    originalRotation = new float[] {player.rotationYaw, player.rotationPitch};
                }
            }
        }
    }
    
    float previousHealth = 20;
    
    private boolean isFishingRod(ItemStack item) {
    	if(item == null) return false;
    	if(item.getItem() == null) return false;
    	if(item.getItem() == Items.fishing_rod) {
	        	String itemId = Utils.getSkyblockId(item);
	        	 
	        	if(itemId != null) { 
	        		if(!itemId.equals("SOUL_WHIP") && !itemId.equals("FLAMING_FLAY") && !itemId.equals("GRAPPLING_HOOK")) {
	        			return true;
	        		}
	        	}

    	}
    	
    	return false;
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
					MovementUtils.stopMoving();
            		if(fishingCycle != null && fishingCycle.isAlive())  fishingCycle.interrupt();
            		RotationUtils.clearAllRotations();
				}
				
				float currentHealth = Minecraft.getMinecraft().thePlayer.getHealth();
			    if (previousHealth > currentHealth && SettingsConfig.KillSeaCreatures.isOn) {
			    	 previousHealth = currentHealth;
			    	 
			    	 if(attacker == null && !blokEverything) {
						Utils.debug("lost hearts");
						if(Location.checkTabLocation() == Location.CRIMSON_ISLE){
							if(Minecraft.getMinecraft().thePlayer.isInLava()) {
								int number_of_magmaLords = 0;
								if(Utils.getSkyblockId(Minecraft.getMinecraft().thePlayer.getCurrentArmor(0)).equals("MAGMA_LORD_BOOTS")) number_of_magmaLords++; 
								if(Utils.getSkyblockId(Minecraft.getMinecraft().thePlayer.getCurrentArmor(1)).equals("MAGMA_LORD_LEGGINGS")) number_of_magmaLords++; 
								if(Utils.getSkyblockId(Minecraft.getMinecraft().thePlayer.getCurrentArmor(2)).equals("MAGMA_LORD_CHESTPLATE")) number_of_magmaLords++;
								if(Utils.getSkyblockId(Minecraft.getMinecraft().thePlayer.getCurrentArmor(3)).equals("MAGMA_LORD_HELMET")) number_of_magmaLords++;
								
								if(number_of_magmaLords < 2) {
									return; // probably from fire (even if not who fishes in lava without resistance)
								}
							}
						} // im not checking other island for burning because who tf doesnt have fire talisman and i dont have things to check accesory bag
						
						Utils.debug("player isnt in lava or is immune");
						if(killingCreatures) return; // is already killing
						attacker = detectAttackerSeaCreature();
						if(attacker != null) killAttackingSeaCreature(attacker);
					}
			    }else {
			    	previousHealth = currentHealth;
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
            	
            	if(distance > 10f && !MovementUtils.isCurrentlyMoving()) {
            		blokEverything = true;
            		MovementUtils.stopMoving();
            		if(fishingCycle != null && fishingCycle.isAlive())  fishingCycle.interrupt();
            		RotationUtils.clearAllRotations();
            		if(FishingTimer.fishingHook != null && SettingsConfig.AutoFish.isOn) {
            			Alerts.DisplayCustomAlert("Stopped AutoFish (distance moved > 10)", 1000, new Float[] {50f, 50f}, 2f);
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
			
			if(ColorUtils.removeMinecraftTextColor(lastMessage).equals("The Golden Fish escapes your hook but looks weakened.")) {
				throwNewHook();
			}
		}
	}
	
	@SubscribeEvent
    public void onPacketReceived(PlaySoundEvent event) {
        if (Minecraft.getMinecraft().theWorld != null) {
        	if(FishingTimer.isFishing && FishingTimer.isInLiquid && SettingsConfig.AutoFish.isOn) {
	            if (event.name.equals("game.player.swim.splash")) {
	            	if(FishingTimer.fishingHook.ticksExisted < 10) return;
	            	
	            	if(event.sound.getVolume() != 0.25f) return;
	            	
	            	float[] soundPosition = new float[] {Math.round(event.sound.getXPosF()*10)/10f, Math.round(event.sound.getYPosF()*10)/10f, Math.round(event.sound.getZPosF()*10)/10f};
	            	
	            	Utils.debug("Splash distance: " + (soundPosition[0] - Math.round(FishingTimer.fishingHook.posX*10)/10f) + " " + (soundPosition[1] - Math.round(FishingTimer.fishingHook.posY*10)/10f) + " " + (soundPosition[2] - Math.round(FishingTimer.fishingHook.posZ*10)/10f));
	            	
	            	if(soundPosition[0] - Math.round(FishingTimer.fishingHook.posX*10)/10f <= 0.25f) {
	            		if(soundPosition[1] - Math.round(FishingTimer.fishingHook.posY*10)/10f <= 0.25f) {
	            			if(soundPosition[2] - Math.round(FishingTimer.fishingHook.posZ*10)/10f <= 0.25f) {
				        		
	            				boolean hasArmorStand = Minecraft.getMinecraft().theWorld.loadedEntityList.stream().anyMatch(e -> e.getCustomNameTag().contains("!!!") || e.getCustomNameTag().contains("0.1"));

            					if(!hasArmorStand) return;
	            				
				                if(lastMessage.contains("swims back beneath the lava...") && !lastMessage.contains(":")){ // golden fish
				                	Utils.debug("Golden fish splash");
				                	lastMessage = "";
				                	return;
				                }

				                if(isFishingRod(Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem())) {
				                	fishingRodSlot = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
				                }
				                
								readyToFish();
								Alerts.DisplayCustomAlert("AutoFish", 500, new Float[] {50f, 20f}, 2f);
			                }
		            	}
	            	}
	            }
        	}
        }
	}
	
	public void antyAfk() {
		if(!SettingsConfig.AutoFishAntyAfk.isOn) {
			Alerts.DisplayCustomAlert("AFK", 3000, 1, new Float[] {50f, 50f}, 2, new ResourceLocation(Reference.MODID, "pululu"), 1);
			return;
		}
		
		if(fishingCycle != null && fishingCycle.isAlive()) fishingCycle.interrupt(); 
		if(antyAfkCycle != null && antyAfkCycle.isAlive()) antyAfkCycle.interrupt(); 
		
		antyAfkCycle = new Thread(() -> {
			try {
				Thread.sleep(50);
				if(FishingTimer.isFishing) {
					KeyBinding.onTick(rightClick);
				}
				
				long delay = 200;
				int randomInt = 25 + new Random().nextInt(6); // 25 - 30
				
				float pitch = RotationUtils.getNeededPitchFromMinecraftRotation(70) + Utils.getRandomizedMinusOrPlus(new Random().nextInt(5));
				float rotationTime = (((delay+50) * randomInt) + 10);
				Alerts.DisplayCustomAlert("AntyAFK", (int) rotationTime, new Float[] {50f, 40f}, 2f);
				
				float addYaw = Utils.getRandomizedMinusOrPlus(new Random().nextInt(20));
				
				RotationUtils.rotateBezierCurve(80, pitch, 40 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10)), pitch + Utils.getRandomizedMinusOrPlus(new Random().nextInt(2)), 1f, true);
				RotationUtils.rotateBezierCurve(-160 + addYaw, 0, -70 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10)), Utils.getRandomizedMinusOrPlus(new Random().nextInt(30)), 0.6f, true);
				RotationUtils.rotateBezierCurve(160 - addYaw/2, 0, 70 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10)), Utils.getRandomizedMinusOrPlus(new Random().nextInt(30)), 0.6f, true);
				RotationUtils.rotateBezierCurve(-160 + addYaw/2, 0, -70 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10)), Utils.getRandomizedMinusOrPlus(new Random().nextInt(30)), 0.6f, true);
				RotationUtils.rotateBezierCurve(160 - addYaw, 0, 70 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10)), Utils.getRandomizedMinusOrPlus(new Random().nextInt(30)), 0.6f, true);
				RotationUtils.rotateBezierCurve(-160 + addYaw, 0, -70 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10)), Utils.getRandomizedMinusOrPlus(new Random().nextInt(30)), 0.6f, true);
				RotationUtils.rotateBezierCurve(160 - addYaw/2, 0, 70 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10)), Utils.getRandomizedMinusOrPlus(new Random().nextInt(30)), 0.6f, true);
				RotationUtils.rotateBezierCurve(-160 + addYaw/2, 0, -70 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10)), Utils.getRandomizedMinusOrPlus(new Random().nextInt(30)), 0.6f, true);
				RotationUtils.rotateBezierCurve(160 - addYaw, 0, 70 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10)), Utils.getRandomizedMinusOrPlus(new Random().nextInt(30)), 0.6f, true);
				RotationUtils.rotateBezierCurve(-80, -pitch, -40 + Utils.getRandomizedMinusOrPlus(new Random().nextInt(10)), -pitch + Utils.getRandomizedMinusOrPlus(new Random().nextInt(2)), 1f, true);
				
				RotationUtils.addTask(() -> {
					Utils.debug("blokEverything is" + blokEverything);
					if(!blokEverything) {
						if(fishingCycle != null && fishingCycle.isAlive()) fishingCycle.interrupt(); 

						fishingCycle = new Thread(() -> {
							throwNewHook();
						});
						
						fishingCycle.start();
					}
				});
				
				for (int i = 0; i < randomInt; i++) {
					Thread.sleep(delay + Math.round((new Random().nextFloat()*50)));
					
					if(RotationUtils.isListEmpty()) {
						return;
					}
					
					if (Thread.currentThread().isInterrupted()) {
						RotationUtils.clearAllRotations();
						Utils.debug("Reset all rotations - antyafk");
						return;
					}
					
		            KeyBinding.onTick(leftClick);
		            
		            if(blokEverything) {
						return;
					}
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
        		
        		if (Thread.currentThread().isInterrupted()) return;
        		
        		if(FishingTimer.fishingHook != null && FishingTimer.isInLiquid) {
        			originalRotation = new float[] {Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.rotationPitch};
				}
        		
        		fishedItem = null;
        		if(FishingTimer.isFishing && !blokEverything) {
        			hookPos = FishingTimer.fishingHook.getPosition();
        			KeyBinding.onTick(rightClick);
        		}
        		
                detectSeaCreature = true;
                new Thread(() -> {
                	try {
						Thread.sleep(500);
						if(FishingTimer.isFishing) Thread.sleep(500); // if bad internet (1000ms)
						if(FishingTimer.isFishing) Thread.sleep(500); // (1500ms)
						if(FishingTimer.isFishing) Thread.sleep(500); // (2000ms)
						
						if(detectSeaCreature) {
							if((!lastMessage.contains("GOOD CATCH!") && !lastMessage.contains("GREAT CATCH!") && !lastMessage.contains("TROPHY FISH!")) || lastMessage.contains(":")) {
								fishedNothing++;
								Utils.debug("fishedItem == null, ++");
							}else {
								Utils.debug("fishedItem == null, but got chat messsage");;
								lastMessage = "";
								fishedNothing = 0;
							}
							
							detectSeaCreature = false;
							if(fishedNothing >= 2) {
								fishedNothing = 0;
								antyAfk();
							}else {
								throwNewHook();
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        });
		if(!blokEverything) fishingCycle.start();
	}
	
	
	// something isnt working (idk what because i didnt debuged it)
	public void killSeaCreature(Entity seaCreature) {
        try {
			if(SettingsConfig.KillSeaCreatures.isOn && !blokEverything) {
				InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
	            for (int i = 0; i < 9; i++) {
	                ItemStack item = inventory.mainInventory[i];
	                if (item != null && item.getItem() == Items.iron_sword) {
	                	List<String> itemLore = Utils.getItemLore(item);
	                	
	                	if(itemLore == null || itemLore.size() == 0) continue; 
	                	if(!Utils.containsWord(itemLore, "Wither Impact")) continue;
	                	
	                	if(seaCreature == null) {
	                		throwNewHook();
	                		return;
	                	}
	                	
	                	Utils.debug(seaCreature.getClass() + " " + seaCreature.getName());
	                	
	                	if (Thread.currentThread().isInterrupted()) return;
	                	
	                	if(Minecraft.getMinecraft().thePlayer.rotationPitch < 88) {
	                		RotationUtils.rotateCurve(0, RotationUtils.getNeededPitchFromMinecraftRotation(90), 0.1f, true);
	                	}
	                	
	                	killingCreatures = true;
	                	if(seaCreature.getName().contains("Objective:")) return;
	                	Alerts.DisplayCustomAlert("Killing " + seaCreature.getName(), 1500, new Float[] {50f, 20f}, 2f);
	                	
	                	Minecraft.getMinecraft().thePlayer.inventory.currentItem = i;
	                	
	                	Thread.sleep(150 + Math.round((new Random().nextFloat()*10)));
	                	
	                	long delay = 120 + Math.round((new Random().nextFloat()/2*100));
	                	
	                	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
	                	
	                	int antyWrongDetection = 0;
	                	while(seaCreature.isEntityAlive() && antyWrongDetection < 20) {
	                		if (Thread.currentThread().isInterrupted()) {
	                			killingCreatures = false;
	                			return;
	                		}
                			if(Minecraft.getMinecraft().thePlayer.inventory.currentItem != i) { killingCreatures = false; blokEverything = true; MovementUtils.stopMoving(); return; }
                			if(Minecraft.getMinecraft().thePlayer.rotationPitch > 88) {
	                			if(seaCreature.getDistance(player.posX, player.posY, player.posZ) < 7) {
	                				killingCreatures = true;
	                				KeyBinding.onTick(rightClick);
	                			}else {
	                				killingCreatures = false;
	                			}
                			}else {
                				RotationUtils.clearAllRotations();
                				RotationUtils.rotateCurve(0, RotationUtils.getNeededPitchFromMinecraftRotation(90), 0.1f, true);
                			}
                			
                			Thread.sleep(delay + Math.round((new Random().nextFloat()*10)));
                			antyWrongDetection++;
	        			}
	                	
	                	// because sometimes these small slimes dont die and i dont really want to make system for that lol
            			if(Minecraft.getMinecraft().thePlayer.rotationPitch > 88) {
            				KeyBinding.onTick(rightClick);
            				Thread.sleep(delay + Math.round((new Random().nextFloat()*10)));
            			}

	                    break;
	                }
	            }
	            
	            killingCreatures = false;
            	throwNewHook();
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
				if (Thread.currentThread().isInterrupted()) return;
				if(Utils.playerPosition().distanceSq(fishingPlace) != 0) {
					MovementUtils.quietlyMovePlayerToWithoutRotation(fishingPlace);
				}
				
				if(Math.abs(Minecraft.getMinecraft().thePlayer.rotationYaw - originalRotation[0]) > 5 || Math.abs(Minecraft.getMinecraft().thePlayer.rotationPitch - originalRotation[1]) > 2) {
					float noise = Utils.getRandomizedMinusOrPlus(new Random().nextFloat());
					RotationUtils.rotateCurve(RotationUtils.getNeededYawFromMinecraftRotation(originalRotation[0] + noise), RotationUtils.getNeededPitchFromMinecraftRotation(originalRotation[1] + noise/2), 0.2f, true);
				}

				
				Minecraft.getMinecraft().thePlayer.inventory.currentItem = fishingRodSlot;
				
				
				Thread.sleep(750 + Math.round((new Random().nextFloat()*1000)/2));
				
				if(Minecraft.getMinecraft().thePlayer.inventory.currentItem != fishingRodSlot) {
					blokEverything = true;
					MovementUtils.stopMoving();
					return;
				}
				
				if (Thread.currentThread().isInterrupted()) return;
				if(!FishingTimer.isFishing) {
					Alerts.DisplayCustomAlert("Auto hook", 1000, new Float[] {50f, 20f}, 2f);
					KeyBinding.onTick(rightClick);
				}else {
					Alerts.DisplayCustomAlert("Bugged Auto hook", 1000, new Float[] {50f, 20f}, 2f);
					KeyBinding.onTick(rightClick);
					Thread.sleep(500);
					KeyBinding.onTick(rightClick);
				}
				
				
				Thread.sleep(3500);
				if (Thread.currentThread().isInterrupted()) return;
				if(!FishingTimer.isInLiquid && FishingTimer.isFishing) {
					Alerts.DisplayCustomAlert("Hook blocked", 500, new Float[] {50f, 20f}, 2f);
					
					attacker = detectAttackerSeaCreature();
					if(attacker != null) killAttackingSeaCreature(attacker);
					
					RotationUtils.rotateStraight(0, -2, 0.1f, false);
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
//	
//	public Entity detectNewSeaCreature() {
//		List<Entity> entities = Minecraft.getMinecraft().theWorld.loadedEntityList;
//		entities.sort(Comparator.comparingInt(entity -> entity.ticksExisted));
//		
//		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
//		
//		for (int i = 0; i < entities.size(); i++) {
//			Entity entity = entities.get(i);
//			
//			if(entity.ticksExisted < 10) {
//				if(entity instanceof EntityPlayer) continue;
//				if(entity instanceof EntityXPOrb) continue;
//				if(entity instanceof EntityFishHook) continue;
//				if(entity instanceof EntityItem) continue;
//				if(entity instanceof EntityArmorStand) continue;
//				if(entity instanceof IBossDisplayData) continue;
//				if(!(entity instanceof EntityLivingBase)) continue;
//				
//				EntityLivingBase entityLiving = (EntityLivingBase) entity;
//				if(entityLiving != null && entityLiving.getLastAttacker() != null) {
//					if(entityLiving.getLastAttacker() == Minecraft.getMinecraft().thePlayer) {
//						Utils.debug("lastAttacker is player");
//						fishedNothing = 0;
//						return entity;
//					}
//				}
//				
//				
//				double posY = (entity.posY - player.posY < 3)?entity.posY:player.posY;
//				if(entity.getDistance(player.posX, posY, player.posZ) < 5) {
//					Utils.debug("distanceToPlayer = 0");
//					fishedNothing = 0;
//					return entity;
//				}
//				
//				if(hookPos != null) {
//					double hookPosY = (entity.posY - hookPos.getY() < 3)?entity.posY:hookPos.getY();
//					if(entity.getDistance(hookPos.getX(), hookPosY, hookPos.getZ()) < 5) {
//						Utils.debug("distanceToHook = 0");
//						fishedNothing = 0;
//						return entity;
//					}
//				}
//			
//			}
//		}
//		
//		if(fishedItem == null) {
//			if((!lastMessage.contains("GOOD CATCH!") && !lastMessage.contains("GREAT CATCH!") && !lastMessage.contains("TROPHY FISH!")) || lastMessage.contains(":")) {
//				fishedNothing++;
//				Utils.debug("fishedItem == null, ++");
//			}else {
//				Utils.debug("fishedItem == null, but got chat messsage");;
//				lastMessage = "";
//				fishedNothing = 0;
//			}
//		}else {
//			Utils.debug("fishedItem != null: " + fishedItem.getName());
//			fishedItem = null;
//			fishedNothing = 0;
//		}
//		
//		if(!SettingsConfig.AutoFishAntyAfk.isOn) {
//			fishedNothing = 0;
//		}
//		
//		if(fishedNothing >= 2) {
//			fishedNothing = 0;
//			antyAfk();
//		}
//		return null;
//	}
	
	@SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(!SettingsConfig.AutoFish.isOn) return;
    	Entity entity = event.entity;
		if(entity instanceof EntityArmorStand) return;
		
    	if(entity instanceof EntityItem || entity instanceof EntityLivingBase) {
	    	if(hookPos != null) {
				double hookPosY = (entity.posY - hookPos.getY() < 3)?entity.posY:hookPos.getY();
				if(entity.getDistance(hookPos.getX(), hookPosY, hookPos.getZ()) < 5) {
					if(detectSeaCreature) {
						fishedItem = entity;
						detectSeaCreature = false;
						
						Utils.debug("Detected new entity: " + entity.getName());
						fishedNothing = 0;
						
						if(entity instanceof EntityLivingBase) {
							Utils.debug(((EntityLivingBase)entity).getName() + " new entity");;
							if(((EntityLivingBase)entity).getName().contains("Objective:")) return;
							if(fishingCycle != null && fishingCycle.isAlive()) fishingCycle.interrupt(); 
							
							fishingCycle = new Thread(() -> {
								killSeaCreature(entity);
							});
							
							fishingCycle.start();
						}else {
							fishingCycle = new Thread(() -> {
								throwNewHook();
							});
							
							fishingCycle.start();
						}
					}
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
			if(((EntityLivingBase)entity).getName().contains("Objective:")) continue;
			
			double posY = (entity.posY - player.posY < 5)?entity.posY:player.posY;
			double distance = entity.getDistance(player.posX, posY, player.posZ);
			
			EntityLivingBase entityLiving = (EntityLivingBase) entity;
			if(entityLiving != null && entityLiving.getLastAttacker() != null) {
				if(entityLiving.getLastAttacker() == Minecraft.getMinecraft().thePlayer) {
					if(distance < 15) {
						return entity;
					}
					
					return entity;
				}
			}
			
			
			if(distance < 6) {
				return entity;
			}
			
			if(entity instanceof EntityWither && distance < 15) {
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
		                	
		                	if(itemLore == null || itemLore.size() == 0) continue; 
		                	if(!Utils.containsWord(itemLore, "Wither Impact")) continue;
		                	
		                	Thread.sleep(500);
		                	if(killingCreatures) {
		                		attacker = null;
		                		return;
		                	}
		                	
		                	Utils.debug("attacker: " + entity.getClass() + " " + entity.getName());
		                	
		                	if(antyAfkCycle != null && antyAfkCycle.isAlive()) antyAfkCycle.interrupt(); // stop antyafk
		                	
		                	if(Minecraft.getMinecraft().thePlayer.rotationPitch < 88) {
		                		RotationUtils.rotateCurve(0, RotationUtils.getNeededPitchFromMinecraftRotation(90), 0.1f, true);
		                	}
		                	
		                	killingCreatures = true;
		                	if(entity.getName().contains("Objective:")) return;
		                	Alerts.DisplayCustomAlert("Killing attacking " + entity.getName(), 1500, new Float[] {50f, 20f}, 2f);
		                	Thread.sleep(Math.round((new Random().nextFloat()*100)));
		                	
		                	Minecraft.getMinecraft().thePlayer.inventory.currentItem = i;
		                	
		                	long delay = 150 + Math.round((new Random().nextFloat()/2*100));
		                	
		                	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		                	
		                	int antyWrongDetection = 0;
		                	while(entity.isEntityAlive() && antyWrongDetection < 20) {
		                			Thread.sleep(delay + Math.round((new Random().nextFloat()*10)));
		                			antyWrongDetection++;
		                			
		                			if(Minecraft.getMinecraft().thePlayer.inventory.currentItem != i) { killingCreatures = false; attacker = null; blokEverything = true; MovementUtils.stopMoving(); return; }
		                			if(Minecraft.getMinecraft().thePlayer.rotationPitch < 88) { RotationUtils.clearAllRotations(); RotationUtils.rotateCurve(0, RotationUtils.getNeededPitchFromMinecraftRotation(90), 0.1f, true); continue; }
		                			
		                			if(entity.getDistance(player.posX, player.posY, player.posZ) < 7) {
		                				KeyBinding.onTick(rightClick);
		                			}else {
		                				MovementUtils.quietlyMovePlayerToWithoutRotation(entity.getPosition());
		                			}
		        			}
		                	break;
		                }
		            }
		            
		            MovementUtils.stopMoving();
		            attacker = null;
    				killingCreatures = false;
                	throwNewHook();
				}else {
					attacker = null;
					throwNewHook();
				}
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
		}).start();
	}
}
