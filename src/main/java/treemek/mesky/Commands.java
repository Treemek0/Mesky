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
    		
    		if(command.equals("reload")) {
    			ConfigHandler.reloadConfig();
    		}
    		if(command.equals("fishing")) {
    			FishingTimer.isText3d = !FishingTimer.isText3d;
    		}
			if(command.equals("waypoint")) {
    			if(args[1] != null && args[2] != null && args[3] != null && args[4] != null && args[5] != null) {
    				String name = args[1];
					try {
						String color = args[2].replace("#", "").substring(0, 6);
						int x = Integer.parseInt(args[3]);
						int y = Integer.parseInt(args[4]);
						int z = Integer.parseInt(args[5]);
						Waypoints.addWaypoint(name, color, x, y, z);
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added waypoint: " + EnumChatFormatting.DARK_PURPLE + name + EnumChatFormatting.GOLD + x + " " + y + " " + z));
					} catch (Exception e) {
						e.printStackTrace();
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Error: " + e.toString()));
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Correct way of using this command: /mesky waypoint <name> <hex_color> <x> <y> <z>"));
					}
    			}
    		}
			if(command.equals("friend")) {
    			if(args[1] != null) {
	    			String nick = args[1];
	    			Location location = FriendsLocations.getLocationForPlayer(nick);
	    			if(location == Location.NONE) {
	    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("We don't have a location for this nickname. Either you didn't have this mod when you added this friend or you were outside Skyblock."));
	    			}else {
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(nick + ": " + location.name()));
	    			}
    			}
			}
			if(command.equals("set")) {
				if(args[1] != null && args[2] != null && args[3] != null) {
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
