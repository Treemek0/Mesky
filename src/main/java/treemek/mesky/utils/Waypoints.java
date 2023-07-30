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
import treemek.mesky.handlers.Config;
import treemek.mesky.utils.Alerts.Alert;

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
    	player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added waypoint: "));
		player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Name: " + name));
		player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Coords: " + "x: " + x + ", y: " + y + ", z: " + z));
    	waypointsList.add(new Waypoint(name, x, y, z, Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName()));
        Config.SaveWaypoint(waypointsList);
    }
    
    public static void deleteWaypoint(int number) {
    	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Deleted waypoint: " + waypointsList.get(number).getName()));
		waypointsList.remove(number);
		Config.SaveWaypoint(waypointsList);
    }
    
    public static void showList() {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Waypoints list:"));
        for (int i = 0; i < waypointsList.size(); i++) {
        	if(waypointsList.get(i).getWorld().equals(Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName())){
        		player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.DARK_AQUA + "Number: " + EnumChatFormatting.WHITE + i));
        		player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.DARK_AQUA + "Name: " + EnumChatFormatting.WHITE + waypointsList.get(i).getName()));
	        	player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.DARK_AQUA + "Coords: " + EnumChatFormatting.WHITE + "x: " + waypointsList.get(i).getCoords()[0] + ", y: " + waypointsList.get(i).getCoords()[1] + ", z: " + waypointsList.get(i).getCoords()[2]));
	        	player.addChatMessage(new ChatComponentText(""));
        	}
        }
		Config.SaveWaypoint(waypointsList);
    }

   
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!waypointsList.isEmpty()) {
            for (Waypoint waypoint : waypointsList) {
                if(waypoint.getWorld().equals(Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName())){
                	// when im gonna be unbanned then check for world types
                    Rendering.draw3DWaypointString(waypoint, event.partialTicks);
                    
                    float x = Math.round(waypoint.getCoords()[0]);
                    float y = Math.round(waypoint.getCoords()[1]);
                    float z = Math.round(waypoint.getCoords()[2]);
                    AxisAlignedBB aabb = new AxisAlignedBB(x + 1.01, y - 1.01, z + 1.01, x - 0.01, y + 0.01, z - 0.01);
                    Rendering.draw3DBox(aabb, "#3e91b5", event.partialTicks);
                }
            }
        }
    }
}
