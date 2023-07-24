package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Waypoints {

	// <String> name, <World> world, <int[]> coords
	
	public static List<Object[]> waypoints = new ArrayList<Object[]>();


    // Method to add data
    public static void addData(String waypoint, int x, int y, int z, String world) {
    	waypoints.add(new Object[]{world, new int[]{x,y,z}, waypoint});
    }

       
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
    	if(waypoints.size() > 0) {
			for (int i = 0; i < waypoints.size(); i++) {
				if(waypoints.get(i)[0].toString().contains("tak")) {
					Rendering.draw3DWaypointString(waypoints.get(i), event.partialTicks);
				}
			}
    	}
    }
}
