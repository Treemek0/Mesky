package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.RenderHandler;
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
	
	public static class MacroWaypoint {
		public Waypoint waypoint;
		public Float yaw;
		public Float pitch;
		public Boolean left;
		public Boolean right;
		public Boolean back;
		public Boolean forward;
		public boolean leftClick;
		public boolean rightClick;
		public Float noiseLevel;
		public AxisAlignedBB boundingBox;
		public String function;

		
		public MacroWaypoint(Waypoint waypoint, Float yaw, Float pitch, boolean left, boolean right, boolean back, boolean forward, boolean leftClick, boolean rightClick, Float noiseLevel, String function) {
			this.waypoint = waypoint;
			this.yaw = yaw;
			this.pitch = pitch;
			this.left = left;
			this.right = right;
			this.back = back;
			this.forward = forward;	
			this.leftClick = leftClick;
			this.rightClick = rightClick;
			this.noiseLevel = noiseLevel;
			this.function = function;
			
			BlockPos waypointPos = new BlockPos(waypoint.coords[0], waypoint.coords[1], waypoint.coords[2]);
			float x = (float) Math.floor(waypointPos.getX());
            float y = (float) Math.floor(waypointPos.getY());
            float z = (float) Math.floor(waypointPos.getZ());
        	this.boundingBox = new AxisAlignedBB(x + 1.001, y + 1.001, z + 1.001, x - 0.001, y - 0.001, z - 0.001);
		}
	}
	
	public static List<MacroWaypoint> waypointsList = new ArrayList<MacroWaypoint>();
	
	
	public static MacroWaypoint addMacroWaypoint(String name, String color, float x, float y, float z, Float yaw, Float pitch, boolean left, boolean right, boolean back, boolean forward, boolean leftClick, boolean rightClick, Float noiseLevel) {
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	color = color.replace("#", "");
    	Waypoint waypoint;
		waypoint = new Waypoint(name, color, x, y, z,  Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld));

		
		MacroWaypoint macroWaypoint = new MacroWaypoint(waypoint, yaw, pitch, left, right, back, forward, leftClick, rightClick, noiseLevel, "");
    	waypointsList.add(macroWaypoint);
    	doneMacro.add(macroWaypoint);
		return macroWaypoint;
    }
	
	@SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!waypointsList.isEmpty()) {
            for (MacroWaypoint macroWaypoint : waypointsList) {
            	Waypoint waypoint = macroWaypoint.waypoint;
            	if(waypoint.world.equals(Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld))) {
                	// when im gonna be unbanned then check for world types
                    RenderHandler.draw3DString(waypoint.name, waypoint.coords, waypoint.color, event.partialTicks);
                    
                    try {
                    	
                    	RenderHandler.draw3DBox(macroWaypoint.boundingBox, waypoint.color, event.partialTicks);
					} catch (Exception e) {
						e.printStackTrace();
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
        	
        	for (int i = 0; i < waypointsList.size(); i++) {
        		MacroWaypoint macroWaypoint = waypointsList.get(i);

        		if(macroWaypoint.waypoint.world.equals(Utils.getWorldIdentifier(Minecraft.getMinecraft().theWorld))) {
	            	int y = player.getPosition().getY();
	            	
	            	AxisAlignedBB playerBoundingBox = player.getEntityBoundingBox();
	            	
	            	
					if(playerBoundingBox.minX > macroWaypoint.boundingBox.minX && playerBoundingBox.maxX < macroWaypoint.boundingBox.maxX && playerBoundingBox.minZ > macroWaypoint.boundingBox.minZ && playerBoundingBox.maxZ < macroWaypoint.boundingBox.maxZ && y > macroWaypoint.boundingBox.minY - 0.999 && y < macroWaypoint.boundingBox.maxY - 0.001) {
						if(doneMacro.contains(macroWaypoint)) continue;
						doneMacro.add(macroWaypoint);
						new Thread(() -> {
							try {
								if(!macroWaypoint.function.equals("/")) {
									Minecraft.getMinecraft().thePlayer.sendChatMessage(macroWaypoint.function);
								}
								KeyBinding.unPressAllKeys();
								if(macroWaypoint.yaw != null && macroWaypoint.pitch != null) {
									RotationUtils.clearAllRotations();
									RotationUtils.rotateCurveToWithControlableNoise(RotationUtils.getNeededYawFromMinecraftRotation(macroWaypoint.yaw), RotationUtils.getNeededPitchFromMinecraftRotation(macroWaypoint.pitch), 0.5f, macroWaypoint.noiseLevel);
									Thread.sleep(1500);
								}
								
								KeyBinding.setKeyBindState(left, macroWaypoint.left);
								KeyBinding.setKeyBindState(right, macroWaypoint.right);
								KeyBinding.setKeyBindState(back, macroWaypoint.back);
								KeyBinding.setKeyBindState(forward, macroWaypoint.forward);
								KeyBinding.setKeyBindState(leftClick, macroWaypoint.leftClick);
								KeyBinding.setKeyBindState(rightClick, macroWaypoint.rightClick);
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
	
	public static List<MacroWaypoint> GetLocationWaypoints() {
    	List<MacroWaypoint> LocationWaypointsList = new ArrayList<>();
		Location.checkTabLocation();
		
		for (MacroWaypoint macroWaypoint : waypointsList) {
        	Waypoint waypoint = macroWaypoint.waypoint;
        	if(waypoint.world.equals(Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld))){
	        	LocationWaypointsList.add(new MacroWaypoint(new Waypoint(waypoint.name, waypoint.color, waypoint.coords[0], waypoint.coords[1], waypoint.coords[2], waypoint.world), macroWaypoint.yaw, macroWaypoint.pitch, macroWaypoint.left, macroWaypoint.right, macroWaypoint.back, macroWaypoint.forward, macroWaypoint.leftClick, macroWaypoint.rightClick, macroWaypoint.noiseLevel, macroWaypoint.function)); 
        	}
		}
		return LocationWaypointsList;
    }
    
    public static List<MacroWaypoint> GetWaypointsWithoutLocation() {
    	List<MacroWaypoint> LocationWaypointsList = new ArrayList<>();
		Location.checkTabLocation();
		
		for (MacroWaypoint macroWaypoint : waypointsList) {
        	Waypoint waypoint = macroWaypoint.waypoint;
        	if(!waypoint.world.equals(Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld))){
	        	LocationWaypointsList.add(new MacroWaypoint(new Waypoint(waypoint.name, waypoint.color, waypoint.coords[0], waypoint.coords[1], waypoint.coords[2], waypoint.world), macroWaypoint.yaw, macroWaypoint.pitch, macroWaypoint.left, macroWaypoint.right, macroWaypoint.back, macroWaypoint.forward, macroWaypoint.leftClick, macroWaypoint.rightClick, macroWaypoint.noiseLevel, macroWaypoint.function)); 
        	}
		}
		return LocationWaypointsList;
    }
    
    public static void deleteWaypointFromLocation(int number) {
    	int numberInLocation = 0;
    	
    	for (int i = 0; i < waypointsList.size(); i++) {
    		Waypoint waypoint = waypointsList.get(i).waypoint;
    		if(waypoint.world.equals(Utils.getWorldIdentifierWithRegionTextField(Minecraft.getMinecraft().theWorld))){
				if(numberInLocation == number) {
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Deleted macro waypoint: " + waypointsList.get(i).waypoint.name));
    				waypointsList.remove(i);
    				ConfigHandler.SaveMacroWaypoint(waypointsList);
    				break;
    			}else {
    				numberInLocation++;
    			}
    		}
    	}
    }
}
