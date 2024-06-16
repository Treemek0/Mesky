package treemek.mesky.features.illegal;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.TemporaryWaypoint;
import treemek.mesky.utils.Waypoints.Waypoint;

public class JawbusDetector {
	
	Map<TemporaryWaypoint, Entity> detectedJawbuses = new HashMap<>();
	Long waypointLifeTime = 300 * 1000L; // seconds * 1000 [ms]
	
	
	private Map<String, BlockPos> playerLocations = new HashMap<>();
    private long lastUpdateTimestamp = 0;
    private static final long UPDATE_INTERVAL = 2500; // updating players locations every X seconds (i didnt wanted to update it every tick)
	
	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
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
	                    	TemporaryWaypoint waypoint = Waypoints.addTemporaryWaypoint(EnumChatFormatting.RED + "! " + EnumChatFormatting.BLUE + "Lord Jawbus" + EnumChatFormatting.RED + " !", "9cbed7", x, y, z, 3, waypointLifeTime);
		                    detectedJawbuses.put(waypoint, entity);
		                    Alerts.DisplayCustomAlerts(EnumChatFormatting.GOLD + "Detected" + EnumChatFormatting.BLUE + "Lord Jawbus", 1500, new int[] {50,40}, 3);
		                    mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BLUE + "Detected Jawbus: " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
	                    }
	                }
	            }
				
				Iterator<Entry<TemporaryWaypoint, Entity>> iterator = detectedJawbuses.entrySet().iterator();
			    while (iterator.hasNext()) {
			        Entry<TemporaryWaypoint, Entity> entry = iterator.next();
			        TemporaryWaypoint waypoint = entry.getKey();
			        Entity entity = entry.getValue();
			        
			        if(!entity.isEntityAlive() && !mobList.contains(entity)) {
			        	Waypoints.temporaryWaypointsList.remove(waypoint);
			        }
			        
			        if(!Waypoints.temporaryWaypointsList.contains(waypoint)) {
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
        for (EntityPlayer player : playerEntities) {
            String playerName = player.getName();
            BlockPos coords = player.playerLocation;
            
            if(!playerLocations.containsKey(playerName)) {
            	playerLocations.put(playerName, coords);
            }else {
            	playerLocations.replace(playerName, coords);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
            if (event.source.getDeathMessage(player) != null && event.source.getDeathMessage(player).getUnformattedText().contains("was killed by Lord Jawbus.")) {
                String playerName = player.getName();
                BlockPos playerLocation = playerLocations.get(playerName);
                
                if(playerLocation.getZ() > -430 && playerLocation.getX() > -365 && playerLocation.getX() < -355) playerLocation = null; // player location is at spawn
                
                if (playerLocation != null) {
                    ChatStyle temp = new ChatStyle();
                    temp.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky tempwaypoint " + playerName + " E66758 " + playerLocation.getX() + " " + playerLocation.getY() + " " + playerLocation.getZ() + " " + 30));
                    temp.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Create a temporary waypoint (3min)")));
                    temp.setColor(EnumChatFormatting.RED); // Set the color of the button text
                    
                    ChatComponentText clickableMessage = new ChatComponentText(" [Quick mark]");
                    clickableMessage.setChatStyle(temp); 
                    
                    
                	
                    player.addChatMessage(new ChatComponentText(player.getDisplayNameString() + " last loaded player location (can be wrong): " + playerLocation.toString()).appendSibling(clickableMessage));
                }
            }
        }
    }
	
    
	@SubscribeEvent
    public void onPlayerJoin(PlayerLoggedInEvent event) {
		if(event.player != null && Minecraft.getMinecraft().thePlayer != null && event.player.getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
	        detectedJawbuses.clear();
	        playerLocations.clear();
		}
    }

	@SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		if(event.player != null && Minecraft.getMinecraft().thePlayer != null && event.player.getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
	        detectedJawbuses.clear();
	        playerLocations.clear();
		}else {
			if(playerLocations.containsKey(event.player.getName())) {
				playerLocations.remove(event.player.getName());
			}
		}
    }
}
