package treemek.mesky;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.GuiLocationConfig;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.gui.GUI;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.FriendsLocations;
import treemek.mesky.utils.Locations.Location;
import treemek.mesky.utils.Waypoints;

public class Commands extends CommandBase{

	
	
	@Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0) {
		// instead of message there should be gui with settings
			GuiHandler.GuiType = 1;
		}
		
		// Command arguments ./mesky [args]
    	if(args.length > 0){
    		String command = args[0].toLowerCase();
    		System.out.println(command);
    		
    		if(command.equals("reload")) {
    			ConfigHandler.reloadConfig();
    		}
    		if(command.equals("fishing")) {
    			FishingTimer.isText3d = !FishingTimer.isText3d;
    		}
    		if(command.equals("pickaxeslot")) {
    			if(args.length >= 2) {
    				try {
    					int slot = Integer.parseInt(args[1]);
    					if(slot <= 9 && slot >= 0) {
    						SettingsConfig.GhostPickaxeSlot = slot;
    						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Slot set to: " + slot));
    					}else {
    						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Slot have to be between 0 and 9"));
    					}
					} catch (Exception e) {
						e.printStackTrace();
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Error: " + e.toString()));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky pickaxeSlot <slot_number>"));
    			}
    		}
			if(command.equals("waypoint")) {
    			if(args.length >= 6) {
    				String name = args[1];
					try {
						String color = args[2].replace("#", "").substring(0, 6);
						Float x = Float.parseFloat(args[3]);
						Float y = Float.parseFloat(args[4]);
						Float z = Float.parseFloat(args[5]);
						Waypoints.addWaypoint(name, color, x, y, z);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added waypoint: " + EnumChatFormatting.DARK_PURPLE + name + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						e.printStackTrace();
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Error: " + e.toString()));
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint <name> <hex_color> <x> <y> <z>"));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint <name> <hex_color> <x> <y> <z>"));
    			}
    		}
			if(command.equals("tempwaypoint")) {
    			if(args.length >= 7) {
    				String name = args[1];
					try {
						String color = args[2].replace("#", "").substring(0, 6);
						Float x = Float.parseFloat(args[3]);
						Float y = Float.parseFloat(args[4]);
						Float z = Float.parseFloat(args[5]);
						Long time = Long.parseLong(args[6]) * 1000;
						Waypoints.addTemporaryWaypoint(name, color, x, y, z, 2, time);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added temporary waypoint: " + EnumChatFormatting.DARK_PURPLE + name + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						e.printStackTrace();
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Error: " + e.toString()));
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky tempwaypoint <name> <hex_color> <x> <y> <z> <time [s]>"));
					}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky tempwaypoint <name> <hex_color> <x> <y> <z> <time [s]>"));
    			}
    		}
			if(command.equals("friend")) {
    			if(args.length >= 2) {
	    			String nick = args[1];
	    			Location location = Location.NONE;
					try {
						location = FriendsLocations.getLocationForPlayer(nick);
					} catch (Exception e) {
						e.printStackTrace();
					}
	    			if(location == Location.NONE) {
	    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("We don't have a location for this nickname. Either you didn't have this mod when you added this friend or you were outside Skyblock."));
	    			}else {
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(nick + ": " + location.name()));
	    			}
    			}else {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky friend <nick>"));
    			}
			}
			if(command.equals("set")) {
				if(args.length >= 4) {
					int x;
					int y;
					try {
						x = Integer.parseInt(args[2]);
						y = Integer.parseInt(args[3]);
					} catch (Exception e) {
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You didn't put numbers correctly /mesky set <setting_name> <x> <y>"));
						return;
					}
					if(args[1].equals("bonzoMask")) {
						GuiLocationConfig.bonzoMaskTimer = new float[] {x,y};
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Set " + EnumChatFormatting.DARK_PURPLE + args[1]  + EnumChatFormatting.WHITE + " to " + EnumChatFormatting.GOLD + "x: " + x + "%, y: " + y + "%"));
					}
					if(args[1].equals("fishingTimer")) {
						GuiLocationConfig.fishingTimer = new float[] {x,y};
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Set " + EnumChatFormatting.DARK_PURPLE + args[1]  + EnumChatFormatting.WHITE + " to " + EnumChatFormatting.GOLD + "x: " + x + "%, y: " + y + "%"));
					}
					if(args[1].equals("spiritMask")) {
						GuiLocationConfig.spiritMaskTimer = new float[] {x,y};
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Set " + EnumChatFormatting.DARK_PURPLE + args[1]  + EnumChatFormatting.WHITE + " to " + EnumChatFormatting.GOLD + "x: " + x + "%, y: " + y + "%"));
					}
					ConfigHandler.saveSettingsLocations();
				}else {
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command (x and y are % of screen): /mesky set <nameOfSetting> <x> <y>"));
				}
			}
			if(command.equals("hideplayers")) {
				SettingsConfig.HidePlayers = !SettingsConfig.HidePlayers;
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
