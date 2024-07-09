package treemek.mesky.features.illegal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
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
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.TouchWaypoint;
import treemek.mesky.utils.Waypoints.Waypoint;

public class EntityDetector {	
	
	public static List<TouchWaypoint> entitiesWaypoint = new ArrayList<>();
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			Iterator<TouchWaypoint> iterator = entitiesWaypoint.iterator();
	    	while (iterator.hasNext()) {
	    		TouchWaypoint waypoint = iterator.next();
	    		if(!Waypoints.touchWaypointsList.contains(waypoint)) {
	    			iterator.remove();
	    		}
	    	}
		}
	}
	
	
	@SideOnly(Side.CLIENT)
	public static void getAllEntityFromType(String type, boolean findArmorStands) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null || mc.theWorld == null) return;
		
		Iterator<TouchWaypoint> iterator = Waypoints.touchWaypointsList.iterator();
    	while (iterator.hasNext()) {
    	    TouchWaypoint waypoint = iterator.next();
    	    
    	    if(entitiesWaypoint.contains(waypoint)) {
    	    	iterator.remove();
    	    	entitiesWaypoint.remove(waypoint);
    	    }
    	}

		List<Entity> mobList = mc.theWorld.loadedEntityList;
		List<int[]> checkedEntityCoords = new ArrayList<>();
		int numberOfEntities = 0;
		for (Entity entity : mobList) {
			if(findArmorStands && !(entity instanceof EntityArmorStand)) continue;
			if(!findArmorStands && entity instanceof EntityArmorStand) continue;
			
			if (entity.getName().toLowerCase().contains(type.toLowerCase())) {
				if(!entity.isEntityAlive()) continue;
				
             	int x = (int) Math.round(entity.posX);
             	int y = (int) Math.round(entity.posY);
             	int z = (int) Math.round(entity.posZ);
             	
             	if(!checkedEntityCoords.contains(new int[]{x,z})) {           	
	             	TouchWaypoint waypoint = new TouchWaypoint(entity.getName(), "a42b22", x, y, z, 2, 5, 600 * 1000L);
	             	
	             	
	             	Waypoints.touchWaypointsList.add(waypoint);
	             	entitiesWaypoint.add(waypoint);
	             	checkedEntityCoords.add(new int[]{x,z});
	             	numberOfEntities++;
             	}
			 }
		}
		if(findArmorStands) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Found " + EnumChatFormatting.GOLD + numberOfEntities + EnumChatFormatting.WHITE + " armorstands with name '" + type + "'"));
		}else {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Found " + EnumChatFormatting.GOLD + numberOfEntities + EnumChatFormatting.WHITE + " entites of type '" + type + "'"));
		}
	}
}
