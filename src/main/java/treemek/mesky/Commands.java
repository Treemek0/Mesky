package treemek.mesky;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.features.illegal.EntityDetector;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.gui.GUI;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.FriendsLocations;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.MacroWaypoints;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;

public class Commands extends CommandBase{

	@Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0) {
		// instead of message there should be gui with settings
			GuiHandler.GuiType = new GUI();
		}
		
		// Command arguments ./mesky [args]
    	if(args.length > 0){
    		String command = args[0].toLowerCase();
    		
    		
    		if(command.equals("reload")) {
    			ConfigHandler.reloadConfig();
    		}
			if(command.equals("waypoint")) {
    			if(args.length >= 6) {
    				String name = args[1];
					try {
						String color = args[2].replace("#", "");
						Float x = Float.parseFloat(args[3]);
						Float y = Float.parseFloat(args[4]);
						Float z = Float.parseFloat(args[5]);
						Waypoints.addWaypoint(name, color, x, y, z);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added waypoint: " + EnumChatFormatting.DARK_PURPLE + name + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint <name> <hex_color> <x> <y> <z>"));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint <name> <hex_color> <x> <y> <z>"));
    			}
    		}
			if(command.equals("macrowaypoint")) {
    			if(args.length >= 10) {
    				String name = args[1];
    				EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
					try {
						String color = args[2].replace("#", "");
						Float x = (float) player.posX;
						Float y = (float) player.posY + 0.5f;
						Float z = (float) player.posZ;
						Float yaw = player.rotationYaw;
						Float pitch = player.rotationPitch;
						Float noiseLevel = Float.parseFloat(args[3]);
						Boolean leftClick = Boolean.parseBoolean(args[4]);
						Boolean rightClick = Boolean.parseBoolean(args[5]);
						Boolean left = Boolean.parseBoolean(args[6]);
						Boolean right = Boolean.parseBoolean(args[7]);
						Boolean back = Boolean.parseBoolean(args[8]);
						Boolean forward = Boolean.parseBoolean(args[9]);
						MacroWaypoints.addMacroWaypoint(name, color, x, y, z, yaw, pitch, left, right, back, forward, leftClick, rightClick, noiseLevel);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added macro waypoint: " + EnumChatFormatting.DARK_PURPLE + name + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint <name> <hex_color> <noiseLevel> <leftClick true/false> <rightClick true/false> <leftMove true/false> <rightMove true/false> <backMove true/false> <forwardMove true/false>"));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint <name> <hex_color> <noiseLevel> <leftClick true/false> <rightClick true/false> <leftMove true/false> <rightMove true/false> <backMove true/false> <forwardMove true/false>"));
    			}
    		}
			if(command.equals("tempwaypoint")) {
    			if(args.length >= 7) {
    				String name = args[1];
					try {
						String color = args[2].replace("#", "");
						Float x = Float.parseFloat(args[3]);
						Float y = Float.parseFloat(args[4]);
						Float z = Float.parseFloat(args[5]);
						Long time = Long.parseLong(args[6]) * 1000;
						Waypoints.addTemporaryWaypoint(name, color, x, y, z, 2, time);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added temporary waypoint (" + time/1000 + "s): " + EnumChatFormatting.DARK_PURPLE + name + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky tempwaypoint <name> <hex_color> <x> <y> <z> <time [s]>"));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky tempwaypoint <name> <hex_color> <x> <y> <z> <time [s]>"));
    			}
    		}
			if(command.equals("touchwaypoint")) {
    			if(args.length >= 7) {
    				String name = args[1];
					try {
						String color = args[2].replace("#", "");
						Float x = Float.parseFloat(args[3]);
						Float y = Float.parseFloat(args[4]);
						Float z = Float.parseFloat(args[5]);
						Float radius = Float.parseFloat(args[6]);
						Long lifeTime = null;
						if(args.length >= 8) {
							lifeTime = Long.parseLong(args[7]);
						}
						Waypoints.addTouchWaypoint(name, color, x, y, z, 2, radius, lifeTime);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added touch waypoint (" + radius + "m): " + EnumChatFormatting.DARK_PURPLE + name + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						Utils.writeError(e);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky touchwaypoint <name> <hex_color> <x> <y> <z> <touch radius>"));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky touchwaypoint <name> <hex_color> <x> <y> <z> <touch radius>"));
    			}
    		}
			if(command.equals("friend")) {
    			if(args.length >= 2) {
	    			String nick = args[1];
					try {
						FriendsLocations.getInfoForPlayer(nick);
					} catch (Exception e) {
						e.printStackTrace();
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky friend <nick>"));
    			}
			}
			if(command.equals("hideplayers")) {
				SettingsConfig.HidePlayers.isOn = !SettingsConfig.HidePlayers.isOn;
			}
			if(command.equals("find")) {
				if(args.length >= 3) {
					try {
						boolean onlyArmorStands = false;
						if(args[1].equalsIgnoreCase("true")) {
							onlyArmorStands = true;
						}
						
						StringBuilder entityType = new StringBuilder();
				        for (int i = 2; i < args.length; i++) { // combining all args after args[1] to single String
				            entityType.append(args[i]);
				            if (i < args.length - 1) {
				                entityType.append(" "); // adds space between words
				            }
				        }
						
						EntityDetector.getAllEntityFromType(entityType.toString(), onlyArmorStands);
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}else {
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command /mesky find <onlyArmorStands? <true>/<false>> <name of entity>"));
				}
			}
			if(command.equals("findclear")) {
				EntityDetector.clearEntityWaypoints();
			}
			if(command.equals("whatentity")) {
				Utils.writeMinecraftMessage(EnumChatFormatting.AQUA + "Looked at entity: ");
				Utils.writeMinecraftMessage(EnumChatFormatting.GOLD + EntityDetector.whatEntity());
			}
			if(command.equals("region")) {
				Utils.writeMinecraftMessage("Current region: " + Locations.getRegion());
			}
			if(command.equals("setfriend")) {
				if(args.length >= 9) {
					String name = args[1];
					String location = args[5];
					int day = 0, month = 0, year = 0, x = 0, y = 0, z = 0;
					try {
						day = Integer.parseInt(args[2]);
					} catch (Exception e) {
						Utils.writeError(args[2] + " isn't a number (Day)");
					}
					try {
						month = Integer.parseInt(args[3]);
					} catch (Exception e) {
						Utils.writeError(args[3] + " isn't a number (Month)");
					}
					try {
						year = Integer.parseInt(args[4]);
					} catch (Exception e) {
						Utils.writeError(args[4] + " isn't a number (Year)");
					}
					try {
						x = Integer.parseInt(args[6]);
					} catch (Exception e) {
						Utils.writeError(args[6] + " isn't a number (x)");
					}
					try {
						y = Integer.parseInt(args[7]);
					} catch (Exception e) {
						Utils.writeError(args[7] + " isn't a number (y)");
					}
					try {
						z = Integer.parseInt(args[8]);
					} catch (Exception e) {
						Utils.writeError(args[8] + " isn't a number (z)");
					}
					
					FriendsLocations.setCustomInfoForPlayer(name, day, month, year, location, x, y, z);
					Utils.writeMinecraftMessage(EnumChatFormatting.AQUA + "Added " + name + " to friends list:");
					Utils.writeMinecraftMessage(EnumChatFormatting.GOLD + "Date: " + day + " " + month + " " + year);
					Utils.writeMinecraftMessage(EnumChatFormatting.GOLD + "Location: " + location);
					Utils.writeMinecraftMessage(EnumChatFormatting.GOLD + "Coords: " + x + " " + y + " " + z);
				}else {
					Utils.writeMinecraftMessage("Wrong use of command (/mesky setfriend <name> <day> <month (number)> <year> <location> <x> <y> <z>");
				}
			}
    	}
    }

	@Override
    public String getCommandName() {
        return "mesky";
    }
	@Override
    public String getCommandUsage(ICommandSender sender) {
        return "Mesky settings";
    }
	 @Override
	 public boolean canCommandSenderUseCommand(ICommandSender sender) {
	     return true;
	 }
	 @Override
	 public int getRequiredPermissionLevel() {
	     return 0;
	 }
	
}
