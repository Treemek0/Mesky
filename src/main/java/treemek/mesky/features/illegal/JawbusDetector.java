package treemek.mesky.features.illegal;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Alerts.AlertRenderInfo;
import treemek.mesky.utils.Waypoints.TemporaryWaypoint;
import treemek.mesky.utils.Waypoints.Waypoint;

public class JawbusDetector {
	
	public static Map<TemporaryWaypoint, Entity> detectedJawbuses = new HashMap<>();
	Long waypointLifeTime = 300 * 1000L; // seconds * 1000 [ms]
	
	
	private static Map<String, BlockPos> playerLocations = new HashMap<>();
    private long lastUpdateTimestamp = 0;
    private static final long UPDATE_INTERVAL = 2500; // updating players locations every X seconds (i didnt wanted to update it every tick)
	
	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			if(!SettingsConfig.JawbusDetection.isOn) return;
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.thePlayer == null || mc.theWorld == null) return;
			if(Locations.currentLocation == Location.CRIMSON_ISLE) {
				List<Entity> mobList = mc.theWorld.loadedEntityList;
				
				
				for (Entity entity : mobList) {
	                if (entity instanceof EntityIronGolem) {
	                	if(!entity.isEntityAlive()) return;
	                	Float x = (float) Math.round(entity.posX);
	                	Float y = (float) Math.round(entity.posY);
	                	Float z = (float) Math.round(entity.posZ);
	                    if(!detectedJawbuses.containsValue(entity)) {
	                    	if(SettingsConfig.JawbusDetectionWaypoint.isOn) {
		                    	TemporaryWaypoint waypoint = Waypoints.addTemporaryWaypoint(EnumChatFormatting.RED + "! " + EnumChatFormatting.BLUE + "Lord Jawbus" + EnumChatFormatting.RED + " !", "9cbed7", x, y, z, 3, waypointLifeTime);
			                    detectedJawbuses.put(waypoint, entity);
			                    Alerts.DisplayCustomAlert(EnumChatFormatting.GOLD + "Detected " + EnumChatFormatting.BLUE + "Lord Jawbus", 3000, 10, new Float[] {50f, 40f}, 3, new ResourceLocation(Reference.MODID, "alarm"), 1);
			                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BLUE + "Detected Jawbus: " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
			                    
			                    if(SettingsConfig.JawbusNotifyParty.isOn) {
			                    	Utils.writeToPartyMinecraft("Jawbus: x: " + x + ", y: " + y + ", z: " + z);
			                    }
	                    	}else {
	                    		TemporaryWaypoint waypoint = new TemporaryWaypoint(EnumChatFormatting.RED + "! " + EnumChatFormatting.BLUE + "Lord Jawbus" + EnumChatFormatting.RED + " !", "9cbed7", x, y, z, 3, System.currentTimeMillis(), waypointLifeTime);
	                    		detectedJawbuses.put(waypoint, entity);
	                    		Alerts.DisplayCustomAlert(EnumChatFormatting.GOLD + "Detected " + EnumChatFormatting.BLUE + "Lord Jawbus", 3000, 10, new Float[] {50f, 40f}, 3, new ResourceLocation(Reference.MODID, "alarm"), 1);
	                    	}
	                    }
	                }
	            }
				
				Iterator<Entry<TemporaryWaypoint, Entity>> iterator = detectedJawbuses.entrySet().iterator();
				EntityPlayerSP player = mc.thePlayer;
			    while (iterator.hasNext()) {
			        Entry<TemporaryWaypoint, Entity> entry = iterator.next();
			        TemporaryWaypoint waypoint = entry.getKey();
			        Entity entity = entry.getValue();
			        
			        if(!mobList.contains(entity)) {
			        	if (Minecraft.getMinecraft().thePlayer.getDistance(waypoint.coords[0], player.posY, waypoint.coords[2]) <= 40) {
				            iterator.remove();
				            if(Waypoints.temporaryWaypointsList.contains(waypoint)) Waypoints.temporaryWaypointsList.remove(waypoint);
				            return;
				        }
			        }
			        
			        
			        if (System.currentTimeMillis() - waypoint.startTime >= waypoint.lifeTime) {
			            iterator.remove();
			        }
			    }
			    
			    
			    long currentTime = System.currentTimeMillis();
		        if (currentTime - lastUpdateTimestamp >= UPDATE_INTERVAL) {
		            lastUpdateTimestamp = currentTime;
		            List<EntityPlayer> playerList = Minecraft.getMinecraft().theWorld.playerEntities;
		            updatePlayerLocations(playerList);
		        }
			    
			}
		}
	}
	
	
    private void updatePlayerLocations(List<EntityPlayer> playerEntities) {
    	if(!SettingsConfig.JawbusPlayerDeathDetection.isOn) return;
    	
        for (EntityPlayer player : playerEntities) {
            String playerName = player.getName();
            BlockPos coords = player.getPosition();
            
            
            if(!Minecraft.getMinecraft().thePlayer.getName().equals(playerName)) {
	            if(!playerLocations.containsKey(playerName)) {
	            	playerLocations.put(playerName, coords);
	            }else {
	            	playerLocations.replace(playerName, coords);
	            }
            }
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
    	try {
    		if(!SettingsConfig.JawbusDetection.isOn) return;
	    	if(!SettingsConfig.JawbusPlayerDeathDetection.isOn) return;
	    	String message = event.message.getUnformattedText();
	    	if(message.contains(":")) return;
	    	
	    	if(message.contains("You have angered a legendary creature... Lord Jawbus has arrived.")) {
	    		if(!SettingsConfig.JawbusDetectionWaypoint.isOn && SettingsConfig.JawbusNotifyParty.isOn) {
	    			BlockPos pos =  Utils.playerPosition();
	    			Utils.writeToPartyMinecraft("Jawbus: x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ());
	    			Utils.debug("Caught own jawbus");
	    		}
	    	}
	    	
	        if (message.contains("was killed by Lord Jawbus.")) {
	            String playerName = message.substring(2, message.indexOf("was killed by")).trim();
	            BlockPos playerLocation = playerLocations.get(playerName);
	            
	            if(!detectedJawbuses.isEmpty()) return;
	            
	            if (playerLocation != null) {
	            	if(playerLocation.getZ() > -430 && playerLocation.getX() > -365 && playerLocation.getX() < -355) return; // player location is at spawn
	            	
	                ChatStyle temp = new ChatStyle();
	                temp.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky tempwaypoint \"" + playerName + "\" E66758 " + playerLocation.getX() + " " + playerLocation.getY() + " " + playerLocation.getZ() + " " + SettingsConfig.MarkWaypointTime.number.intValue()));
	                temp.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Create a temporary waypoint (3min)")));
	                temp.setColor(EnumChatFormatting.RED); // Set the color of the button text
	                
	                ChatComponentText clickableMessage = new ChatComponentText(" [Quick mark]");
	                clickableMessage.setChatStyle(temp); 
	                
	                Alerts.DisplayCustomAlert("", 1, 3, new Float[] {50f, 50f}, 3, new ResourceLocation(Reference.MODID, "alarm"), 3);
	                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + playerName + EnumChatFormatting.AQUA + " last rendered position: " + EnumChatFormatting.GOLD + playerLocation.getX() + " " + playerLocation.getY() + " " + playerLocation.getZ()).appendSibling(clickableMessage));
	            }else {
	            	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "No data about location of: " + EnumChatFormatting.DARK_AQUA + playerName));
	            }
	        }
    	} catch (Exception e) {
    		if(Minecraft.getMinecraft().thePlayer != null) {
    			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(e.toString()));
    			e.printStackTrace();
    		}
		}
    }
    
    public static BlockPos getPlayerLastSeenLocation(String playerName) {
    	return playerLocations.get(playerName);
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
    	if (event.world.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player == Minecraft.getMinecraft().thePlayer) {
            	detectedJawbuses.clear();
                playerLocations.clear();
            }
        }
    }
    
    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
        detectedJawbuses.clear();
        playerLocations.clear();
    }
}
