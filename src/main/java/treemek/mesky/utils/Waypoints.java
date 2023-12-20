package treemek.mesky.utils;

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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Alerts.Alert;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Waypoints.Waypoint;

public class Waypoints {

	// <String> name, <World> world, <int[]> coords
	public static List<Waypoint> waypointsList = new ArrayList<Waypoint>();

    // Waypoint class definition
    public static class Waypoint {
        private String name;
        private float[] coords;
        private String world;

        public Waypoint(String name, float x, float y, float z, String world) {
            this.name = name;
            this.coords = new float[]{x, y, z};
            this.world = world;
        }

        public String getName() {
            return name;
        }

        public float[] getCoords() {
            return coords;
        }
    

        public String getWorld() {
            return world;
        }
    }

    // Method to add data
    public static void addWaypoint(String name, float x, float y, float z) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	if(!HypixelCheck.isOnHypixel()) {
    		waypointsList.add(0, new Waypoint(name, x, y, z, Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName()));
    	}else{
    		Location.checkTabLocation();
    		if(Locations.currentLocationText != null) {
    			waypointsList.add(0, new Waypoint(name, x, y, z, Locations.currentLocationText));
    		}   		
    	}
		ConfigHandler.SaveWaypoint(waypointsList);
    }
    
    public static void deleteWaypoint(int number) {
    	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Deleted waypoint: " + waypointsList.get(number).getName()));
		waypointsList.remove(number);
		ConfigHandler.SaveWaypoint(waypointsList);
    }
   
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!waypointsList.isEmpty()) {
        	Location.checkTabLocation();
            for (Waypoint waypoint : waypointsList) {
            	if((!HypixelCheck.isOnHypixel() && waypoint.getWorld().equals(Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName())) || (HypixelCheck.isOnHypixel() && waypoint.getWorld().equals(Locations.currentLocationText))) {
                	// when im gonna be unbanned then check for world types
                    RenderHandler.draw3DWaypointString(waypoint, event.partialTicks);
                    
                    float x = (float) Math.floor(waypoint.getCoords()[0]);
                    float y = (float) Math.floor(waypoint.getCoords()[1]);
                    float z = (float) Math.floor(waypoint.getCoords()[2]);
                    AxisAlignedBB aabb = new AxisAlignedBB(x + 1.001, y + 1.001, z + 1.001, x - 0.001, y - 0.001, z - 0.001);
                    RenderHandler.draw3DBox(aabb, "#3e91b5", event.partialTicks);
                }
            }
        }
    }
    
    public static List<Waypoint> GetLocationWaypoints() {
    	List<Waypoint> LocationWaypointsList = new ArrayList<Waypoint>();
		Location.checkTabLocation();
		
		for (Waypoint waypoint : Waypoints.waypointsList) {
	        if(!HypixelCheck.isOnHypixel()) {
	    		if(waypoint.world.equals(Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName())){
	        	LocationWaypointsList.add(new Waypoint(waypoint.getName(), waypoint.getCoords()[0], waypoint.getCoords()[1], waypoint.getCoords()[2], Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName()));
	    		}
	    	}else{
	    		if(Locations.currentLocationText != null) {
	    			if(waypoint.world.equalsIgnoreCase(Locations.currentLocationText)) {
	    			LocationWaypointsList.add(new Waypoint(waypoint.getName(), waypoint.getCoords()[0], waypoint.getCoords()[1], waypoint.getCoords()[2], Locations.currentLocationText));
	    			}
	    		}   		
	    	}
		}
		return LocationWaypointsList;
    }
    
    public static List<Waypoint> GetWaypointsWithoutLocation() {
    	List<Waypoint> LocationWaypointsList = new ArrayList<Waypoint>();
		Location.checkTabLocation();
		
		for (Waypoint waypoint : Waypoints.waypointsList) {
	        if(!HypixelCheck.isOnHypixel()) {
	    		if(waypoint.getWorld() != Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName()){
	        	LocationWaypointsList.add(new Waypoint(waypoint.getName(), waypoint.getCoords()[0], waypoint.getCoords()[1], waypoint.getCoords()[2],  waypoint.getWorld()));
	    		}
	    	}else{
	    		if(Locations.currentLocationText != null) {
	    			if(!waypoint.world.equalsIgnoreCase(Locations.currentLocationText)) {
	    			LocationWaypointsList.add(new Waypoint(waypoint.getName(), waypoint.getCoords()[0], waypoint.getCoords()[1], waypoint.getCoords()[2], waypoint.getWorld()));
	    			}
	    		}   		
	    	}
		}
		return LocationWaypointsList;
    }
}
