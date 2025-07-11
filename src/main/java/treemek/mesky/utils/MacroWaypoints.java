package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.macrowaypoints.MacroWaypointElement;
import treemek.mesky.handlers.gui.waypoints.WaypointsGui;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Waypoints.TemporaryWaypoint;
import treemek.mesky.utils.Waypoints.Waypoint;

public class MacroWaypoints {
	
	int rightClick = Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode();
	int leftClick = Minecraft.getMinecraft().gameSettings.keyBindAttack.getKeyCode();
	int right = Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode();
	int left = Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode();
	int back = Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode();
	int forward = Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode();
	int sneak = Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode();
	
	public static Map<String, MacroWaypointGroup> waypointsList = new LinkedHashMap<>();
	
	public static MacroWaypoint MacroActive = null;
	
	public static class MacroWaypointGroup {
		public List<MacroWaypoint> list = new ArrayList<>();
		public boolean enabled;
		public boolean opened;
		public String world;
		
		public MacroWaypointGroup(List<MacroWaypoint> list, String world, boolean enabled, boolean opened) {
			this.list = list;
			this.world = world;
			this.enabled = enabled;
			this.opened = opened;
		}
	}
	
	public static class MacroWaypoint {
		public Waypoint waypoint;
		public Float yaw;
		public Float pitch;
		public Boolean left;
		public Boolean right;
		public Boolean back;
		public Boolean forward;
		public Boolean leftClick;
		public Boolean rightClick;
		public Boolean sneak;
		public Float noiseLevel;
		public AxisAlignedBB boundingBox;
		public String function;
		public Boolean enabled;
		private AxisAlignedBB collisionBox;

		
		public MacroWaypoint(Waypoint waypoint, Float yaw, Float pitch, boolean left, boolean right, boolean back, boolean forward, boolean leftClick, boolean rightClick, boolean sneak, Float noiseLevel, String function, boolean enabled) {
			this.waypoint = waypoint;
			this.yaw = yaw;
			this.pitch = pitch;
			this.left = left;
			this.right = right;
			this.back = back;
			this.forward = forward;	
			this.leftClick = leftClick;
			this.rightClick = rightClick;
			this.sneak = sneak;
			this.noiseLevel = noiseLevel;
			this.function = function;
			this.enabled = enabled;
			
			BlockPos waypointPos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
			float x = (float) Math.floor(waypointPos.getX());
            float y = (float) Math.floor(waypointPos.getY());
            float z = (float) Math.floor(waypointPos.getZ());
        	this.boundingBox = new AxisAlignedBB(x + 1.001, y + 1.001, z + 1.001, x - 0.001, y - 0.001, z - 0.001);
        	this.collisionBox = new AxisAlignedBB(x + 0.4, y + 1, z + 0.4, x + 0.6, y, z + 0.6);
		}
		
		public void fixNulls() {
        	if(waypoint == null) {
        		waypoint = new Waypoint("", "ffffff", 0, 0, 0, "null", 1, true);
        		BlockPos waypointPos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
    			float x = (float) Math.floor(waypointPos.getX());
                float y = (float) Math.floor(waypointPos.getY());
                float z = (float) Math.floor(waypointPos.getZ());
            	this.boundingBox = new AxisAlignedBB(x + 1.001, y + 1.001, z + 1.001, x - 0.001, y - 0.001, z - 0.001);
        	}
        	
        	// yaw & pitch can be null
        	if(left == null) left = false;
        	if(right == null) right = false;
        	if(back == null) back = false;
        	if(forward == null) forward = false;
        	if(leftClick == null) leftClick = false;
        	if(rightClick == null) rightClick = false;
        	if(sneak == null) sneak = false;
        	if(enabled == null) enabled = true;
        	if(noiseLevel == null) noiseLevel = 1f;
        	if(function == null) function = "/";
        	if(collisionBox == null) this.collisionBox = new AxisAlignedBB(Math.floor(waypoint.coords[0]) + 0.4, Math.floor(waypoint.coords[1]) + 1, Math.floor(waypoint.coords[2]) + 0.4, Math.floor(waypoint.coords[0]) + 0.6, Math.floor(waypoint.coords[1]), Math.floor(waypoint.coords[2]) + 0.6);
        }
	}
	
	
	public static MacroWaypoint addMacroWaypoint(String name, String color, float x, float y, float z, Float yaw, Float pitch, boolean left, boolean right, boolean back, boolean forward, boolean leftClick, boolean rightClick, boolean sneak, Float noiseLevel) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	color = color.replace("#", "");
    	String world = Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld);
    	Waypoint waypoint;
		waypoint = new Waypoint(name, color, x, y, z, world, 1, true);

		
		MacroWaypoint macroWaypoint = new MacroWaypoint(waypoint, yaw, pitch, left, right, back, forward, leftClick, rightClick, sneak, noiseLevel, "", true);
		waypointsList.computeIfAbsent(null, k -> new MacroWaypointGroup(new ArrayList<>(), world, true, true)).list.add(0, macroWaypoint);
    	doneMacro.add(macroWaypoint);
		return macroWaypoint;
    }
	
	public static MacroWaypoint addMacroWaypoint(String name, String color, float x, float y, float z, Float yaw, Float pitch, boolean left, boolean right, boolean back, boolean forward, boolean leftClick, boolean rightClick, boolean sneak, Float noiseLevel, String group) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	color = color.replace("#", "");
    	String world = Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld);
    	Waypoint waypoint;
		waypoint = new Waypoint(name, color, x, y, z,  world, 1, true);

		
		MacroWaypoint macroWaypoint = new MacroWaypoint(waypoint, yaw, pitch, left, right, back, forward, leftClick, rightClick, sneak, noiseLevel, "", true);
		waypointsList.computeIfAbsent(group, k -> new MacroWaypointGroup(new ArrayList<>(), world, true, true)).list.add(0, macroWaypoint);
    	doneMacro.add(macroWaypoint);
		return macroWaypoint;
    }
	
	@SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!waypointsList.isEmpty()) {
        	for (MacroWaypointGroup macroWaypointGroup : waypointsList.values()) {
	            for (MacroWaypoint macroWaypoint : macroWaypointGroup.list) {
	            	if(!macroWaypoint.enabled) continue;
	            	Waypoint waypoint = macroWaypoint.waypoint;
	
	            	if(waypoint.world.equals(Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld))) {
	                	// when im gonna be unbanned then check for world types
	                    RenderHandler.draw3DString(ColorUtils.getColoredText(waypoint.name), waypoint.coords, waypoint.color, event.partialTicks);
	                    
	                    try {
	                    	RenderHandler.draw3DImage(macroWaypoint.boundingBox.minX + 0.5f, macroWaypoint.boundingBox.minY + 0.5f, macroWaypoint.boundingBox.minZ + 0.5f, 0, 0, 0.5f, 0.5f, new ResourceLocation(Reference.MODID, "gui/tools.png"), ColorUtils.getColorInt(waypoint.color), false, event.partialTicks);
	                    	RenderHandler.draw3DBox(macroWaypoint.boundingBox, waypoint.color, event.partialTicks);
						} catch (Exception e) {
							e.printStackTrace();
						}
	                    
	                }
	            }
        	}
        }
	}
	
	public static List<MacroWaypoint> doneMacro = new ArrayList<>();
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity == Minecraft.getMinecraft().thePlayer) {
        	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        	
        	for (MacroWaypointGroup macroWaypointGroup : waypointsList.values()) {
	            for (MacroWaypoint macroWaypoint : macroWaypointGroup.list) {
	        		if(!macroWaypoint.enabled) continue;
	
	        		if(macroWaypoint.waypoint.world.equals(Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld))) {
		            	int y = player.getPosition().getY();
		            	
		            	AxisAlignedBB playerBoundingBox = player.getEntityBoundingBox();
		            	
		            	
						if(playerBoundingBox.intersectsWith(macroWaypoint.collisionBox)) {
							if(doneMacro.contains(macroWaypoint)) continue;
							doneMacro.add(macroWaypoint);
							new Thread(() -> {
								try {
									MacroActive = null;
									KeyBinding.unPressAllKeys();
									Thread.sleep(100);
									
									if(!macroWaypoint.function.equals("/")) {
										Utils.executeCommand(macroWaypoint.function);
									}
									
									if(macroWaypoint.yaw != null && macroWaypoint.pitch != null) {
										RotationUtils.clearAllRotations();
										RotationUtils.rotateCurveToWithControlableNoise(RotationUtils.getNeededYawFromMinecraftRotation(macroWaypoint.yaw), RotationUtils.getNeededPitchFromMinecraftRotation(macroWaypoint.pitch), 0.5f, macroWaypoint.noiseLevel);
										if(isAnyKeybindPressed(macroWaypoint)) MacroActive = macroWaypoint;
										RotationUtils.addTask(() -> {
											KeyBinding.setKeyBindState(left, macroWaypoint.left);
											KeyBinding.setKeyBindState(right, macroWaypoint.right);
											KeyBinding.setKeyBindState(back, macroWaypoint.back);
											KeyBinding.setKeyBindState(forward, macroWaypoint.forward);
											KeyBinding.setKeyBindState(leftClick, macroWaypoint.leftClick);
											KeyBinding.setKeyBindState(rightClick, macroWaypoint.rightClick);
											KeyBinding.setKeyBindState(sneak, macroWaypoint.sneak);
										});
									}else {
										KeyBinding.setKeyBindState(left, macroWaypoint.left);
										KeyBinding.setKeyBindState(right, macroWaypoint.right);
										KeyBinding.setKeyBindState(back, macroWaypoint.back);
										KeyBinding.setKeyBindState(forward, macroWaypoint.forward);
										KeyBinding.setKeyBindState(leftClick, macroWaypoint.leftClick);
										KeyBinding.setKeyBindState(rightClick, macroWaypoint.rightClick);
										KeyBinding.setKeyBindState(sneak, macroWaypoint.sneak);
										if(isAnyKeybindPressed(macroWaypoint)) MacroActive = macroWaypoint;
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}).start();
						}else {
							if(doneMacro.contains(macroWaypoint)) {
								doneMacro.remove(macroWaypoint);
							}
						}
	        		}
	            }
			}
        }
	}
	
	public static Map<String, MacroWaypointGroup> GetLocationWaypoints() {
	    Map<String, MacroWaypointGroup> groupedWaypoints = new LinkedHashMap<>();
	    Location.checkTabLocation();
	    String currentWorld = Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld);

	    for (Entry<String, MacroWaypointGroup> entry : waypointsList.entrySet()) {
	    	if(!entry.getValue().world.equals(Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld))) continue;
	    	
	        List<MacroWaypoint> filteredWaypoints = new ArrayList<>();
	        for (MacroWaypoint macroWaypoint : entry.getValue().list) {
	            Waypoint waypoint = macroWaypoint.waypoint;
	            if (waypoint.world.equals(currentWorld)) {
	                Waypoint newWaypoint = new Waypoint(waypoint.name, waypoint.color,waypoint.coords[0], waypoint.coords[1], waypoint.coords[2],waypoint.world, 1, true);

	                filteredWaypoints.add(new MacroWaypoint(
	                    newWaypoint,
	                    macroWaypoint.yaw, macroWaypoint.pitch,
	                    macroWaypoint.left, macroWaypoint.right,
	                    macroWaypoint.back, macroWaypoint.forward,
	                    macroWaypoint.leftClick, macroWaypoint.rightClick,
	                    macroWaypoint.sneak, macroWaypoint.noiseLevel,
	                    macroWaypoint.function, macroWaypoint.enabled
	                ));
	            }
	        }

            MacroWaypointGroup newGroup = new MacroWaypointGroup(filteredWaypoints, entry.getValue().world, entry.getValue().enabled, entry.getValue().opened);
            groupedWaypoints.put(entry.getKey(), newGroup);
	    }

	    return groupedWaypoints;
	}

    
	public static Map<String, MacroWaypointGroup> GetWaypointsWithoutLocation() {
	    Map<String, MacroWaypointGroup> groupedWaypoints = new LinkedHashMap<>();
	    String currentWorld = Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld);

	    for (Entry<String, MacroWaypointGroup> entry : waypointsList.entrySet()) {
	    	if(entry.getValue().world.equals(Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld))) continue;
	    	
	        List<MacroWaypoint> filteredWaypoints = new ArrayList<>();
	        for (MacroWaypoint macroWaypoint : entry.getValue().list) {
	            Waypoint waypoint = macroWaypoint.waypoint;
	            if (!waypoint.world.equals(currentWorld)) {
	                Waypoint newWaypoint = new Waypoint(waypoint.name, waypoint.color,waypoint.coords[0], waypoint.coords[1], waypoint.coords[2],waypoint.world, 1, true);

	                filteredWaypoints.add(new MacroWaypoint(
	                    newWaypoint,
	                    macroWaypoint.yaw, macroWaypoint.pitch,
	                    macroWaypoint.left, macroWaypoint.right,
	                    macroWaypoint.back, macroWaypoint.forward,
	                    macroWaypoint.leftClick, macroWaypoint.rightClick,
	                    macroWaypoint.sneak, macroWaypoint.noiseLevel,
	                    macroWaypoint.function, macroWaypoint.enabled
	                ));
	            }
	        }

            MacroWaypointGroup newGroup = new MacroWaypointGroup(filteredWaypoints, entry.getValue().world, entry.getValue().enabled, entry.getValue().opened);
            groupedWaypoints.put(entry.getKey(), newGroup);
	    }

	    return groupedWaypoints;
	}
    
	
	private static boolean isAnyKeybindPressed(MacroWaypoint macroWaypoint) {
		if(macroWaypoint.forward || macroWaypoint.back || macroWaypoint.left || macroWaypoint.right || macroWaypoint.leftClick || macroWaypoint.sneak) return true;
		
		return false;
	}
}
