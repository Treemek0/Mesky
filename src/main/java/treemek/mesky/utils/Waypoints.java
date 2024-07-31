package treemek.mesky.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.waypoints.WaypointsGui;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Waypoints.Waypoint;

public class Waypoints {

	// <String> name, <World> world, <int[]> coords
	public static List<Waypoint> waypointsList = new ArrayList<Waypoint>();
	public static List<TemporaryWaypoint> temporaryWaypointsList = new ArrayList<TemporaryWaypoint>();
	public static List<TouchWaypoint> touchWaypointsList = new ArrayList<TouchWaypoint>();

    // Waypoint class definition
    public static class Waypoint {
        public String name;
        public float[] coords;
        public String world;
        public String color;

        public Waypoint(String name, String color, float x, float y, float z, String world) {
            this.name = name;
            this.coords = new float[]{x, y, z};
            this.world = world;
            this.color = ColorUtils.fixColor(color);
        }
    }
    
    public static class TemporaryWaypoint {
        public String name;
        public float[] coords;
        public String world;
        public String color;
        public float scale;
        public long startTime;
        public long lifeTime;

        public TemporaryWaypoint(String name, String color, float x, float y, float z, float scale, long startTime, long lifeTime) {
            this.name = name;
            this.coords = new float[]{x, y, z};
            this.scale = scale;
            this.color = ColorUtils.fixColor(color);
            this.startTime = startTime;
            this.lifeTime = lifeTime;
        } 
    }
    
    public static class TouchWaypoint {
        public String name;
        public float[] coords;
        public String color;
        public float scale;
		public float touchRadius;
		public Long startTime;
        public Long lifeTime;

        public TouchWaypoint(String name, String color, float x, float y, float z, float scale, float touchRadius, Long lifeTime) {
            this.name = name;
            this.coords = new float[]{x, y, z};
            this.scale = scale;
            this.color = ColorUtils.fixColor(color);
            this.touchRadius = touchRadius;
            if(lifeTime != null) {
	            this.startTime = System.currentTimeMillis();
	            this.lifeTime = lifeTime;
            }
        }
        
    }

    public static Waypoint addWaypoint(String name, String color, float x, float y, float z) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	color = color.replace("#", "");
    	
    	Waypoint waypoint;
		waypoint = new Waypoint(name, color, x, y, z, Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld));
		waypointsList.add(0, waypoint);
		ConfigHandler.SaveWaypoint(waypointsList);
		return waypoint;
    }
    
    public static TemporaryWaypoint addTemporaryWaypoint(String name, String color, float x, float y, float z, float scale, long lifeTime) {
    	TemporaryWaypoint waypoint = new TemporaryWaypoint(name, color, x, y, z, scale, System.currentTimeMillis(), lifeTime);
    	temporaryWaypointsList.add(waypoint);
		return waypoint;
    }
    
    public static TouchWaypoint addTouchWaypoint(String name, String color, float x, float y, float z, float scale, float touchRadius, Long lifeTime) {
    	TouchWaypoint waypoint = new TouchWaypoint(name, color, x, y, z, scale, touchRadius, lifeTime);
    	touchWaypointsList.add(waypoint);
		return waypoint;
    }
    
    public static void deleteWaypoint(int number) {
    	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Deleted waypoint: " + EnumChatFormatting.GOLD + waypointsList.get(number).name));
		waypointsList.remove(number);
		ConfigHandler.SaveWaypoint(waypointsList);
    }
   
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!waypointsList.isEmpty()) {
            for (Waypoint waypoint : waypointsList) {
            	if(waypoint.world.equals(Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld))) {
                    waypoint.color = ColorUtils.fixColor(waypoint.color);
                    RenderHandler.draw3DWaypointString(waypoint.name, waypoint.color, waypoint.coords, event.partialTicks, 1);  
                    
                    try {
                    	BlockPos waypointPos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
                    	float x = (float) Math.floor(waypointPos.getX());
                        float y = (float) Math.floor(waypointPos.getY());
                        float z = (float) Math.floor(waypointPos.getZ());
                    	AxisAlignedBB aabb = new AxisAlignedBB(x + 1.001, y + 1.001, z + 1.001, x - 0.001, y - 0.001, z - 0.001);
                    	RenderHandler.draw3DBox(aabb, waypoint.color, event.partialTicks);
					} catch (Exception e) {
						e.printStackTrace();
					}
                    
                }
            }
        }
        if (!temporaryWaypointsList.isEmpty()) {
        	Iterator<TemporaryWaypoint> iterator = temporaryWaypointsList.iterator();
        	while (iterator.hasNext()) {
        	    TemporaryWaypoint waypoint = iterator.next();
                waypoint.color = ColorUtils.fixColor(waypoint.color);
        		RenderHandler.draw3DWaypointString(waypoint.name, waypoint.color, waypoint.coords, event.partialTicks, waypoint.scale);  
                
                try {
                	BlockPos waypointPos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
                	float x = (float) Math.floor(waypointPos.getX());
                    float y = (float) Math.floor(waypointPos.getY());
                    float z = (float) Math.floor(waypointPos.getZ());
                	AxisAlignedBB aabb = new AxisAlignedBB(x + 1.001, y + 1.001, z + 1.001, x - 0.001, y - 0.001, z - 0.001);
                	RenderHandler.draw3DBox(aabb, waypoint.color, event.partialTicks);
				} catch (Exception e) {
					e.printStackTrace();
				}
               
                if (System.currentTimeMillis() - waypoint.startTime >= waypoint.lifeTime) {
		            iterator.remove();
		        }
            }
        }
        
        if (!touchWaypointsList.isEmpty()) {
        	Iterator<TouchWaypoint> iterator = touchWaypointsList.iterator();
        	while (iterator.hasNext()) {
        	    TouchWaypoint waypoint = iterator.next();
                waypoint.color = ColorUtils.fixColor(waypoint.color);
        		RenderHandler.draw3DWaypointString(waypoint.name, waypoint.color, waypoint.coords, event.partialTicks, waypoint.scale);  
                
                try {
                	BlockPos waypointPos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
                	float x = (float) Math.floor(waypointPos.getX());
                    float y = (float) Math.floor(waypointPos.getY());
                    float z = (float) Math.floor(waypointPos.getZ());
                	AxisAlignedBB aabb = new AxisAlignedBB(x + 1.001, y + 1.001, z + 1.001, x - 0.001, y - 0.001, z - 0.001);
                	RenderHandler.draw3DBox(aabb, waypoint.color, event.partialTicks);
				} catch (Exception e) {
					e.printStackTrace();
				}
               
                if (Minecraft.getMinecraft().thePlayer.getDistance(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]) <= waypoint.touchRadius) {
		            iterator.remove();
		        }
                
                if(waypoint.lifeTime != null) {
	                if (System.currentTimeMillis() - waypoint.startTime >= waypoint.lifeTime) {
			            iterator.remove();
			        }
                }
            }
        }
    }
    
    public static List<Waypoint> GetLocationWaypoints() {
    	List<Waypoint> LocationWaypointsList = new ArrayList<Waypoint>();
		Location.checkTabLocation();
		
		for (Waypoint waypoint : Waypoints.waypointsList) {
    		if(waypoint.world.equals(Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld))){
    			LocationWaypointsList.add(new Waypoint(waypoint.name, waypoint.color, waypoint.coords[0], waypoint.coords[1], waypoint.coords[2], waypoint.world));
    		}
		}
		return LocationWaypointsList;
    }
    
    public static List<Waypoint> GetWaypointsWithoutLocation() {
    	List<Waypoint> LocationWaypointsList = new ArrayList<Waypoint>();
		
		for (Waypoint waypoint : Waypoints.waypointsList) {
			if(!waypoint.world.equals(Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld))){
    			LocationWaypointsList.add(new Waypoint(waypoint.name, waypoint.color, waypoint.coords[0], waypoint.coords[1], waypoint.coords[2], waypoint.world));
    		}
		}
		return LocationWaypointsList;
    }
    
    public static void deleteWaypointFromLocation(int number) {
    	int numberInLocation = 0;
    	
    	for (int i = 0; i < waypointsList.size(); i++) {
    		Waypoint waypoint = waypointsList.get(i);
			if(waypoint.world.equals(Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld))){
				if(numberInLocation == number) {
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Deleted waypoint: " + waypointsList.get(i).name));
    				waypointsList.remove(i);
    				ConfigHandler.SaveWaypoint(waypointsList);
    				break;
    			}else {
    				numberInLocation++;
    			}
    		}
	    	
    	}
    }
	
    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
    	if (event.world.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player == Minecraft.getMinecraft().thePlayer) {
            	temporaryWaypointsList.clear();
        		touchWaypointsList.clear();
            }
        }
    }
    
    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
    	temporaryWaypointsList.clear();
		touchWaypointsList.clear();
    }
}
