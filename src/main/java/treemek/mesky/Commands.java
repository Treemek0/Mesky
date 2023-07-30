package treemek.mesky;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import treemek.mesky.handlers.Config;
import treemek.mesky.handlers.gui.GUI;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Waypoints;

public class Commands extends CommandBase{

	public static boolean opengui = false;
	
	@Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0) {
		// instead of message there should be gui with settings
			opengui = true;
		}
		System.out.println(args.length);
		
		// Command arguments ./mesky [args]
    	if(args.length > 0){
    		if(args[0].equalsIgnoreCase("alert")){
    			if(args.length > 1) {
		    		if(args[1].equalsIgnoreCase("add")){
		    			if(args.length == 5) {
		    				Alerts.addAlert(args[2], args[3], Float.parseFloat(args[4]));
		    				return;
		    			}else{
		    				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Wrong use of command /mesky alert add [triggerMessage] [alertShown] [alertTime]"));
		    				return;
		    			}
		    		}
		    		if(args[1].equalsIgnoreCase("delete")){
		    			if(args.length == 3) {
		    				Alerts.deleteAlert(args[2]);
		    				return;
		    			}else{
		    				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Wrong use of command /mesky alert delete [triggerMessage]"));
		    				return;
		    			}
		    		}
		    		if(args[1].equalsIgnoreCase("list")){
		    				Alerts.showAlert();
		    				return;
		    		}
    			}else {
    				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Wrong use of command /mesky alert [add/delete/list]"));
    			}
    		}
    		if(args[0].equalsIgnoreCase("reload")) {
    			Config.reloadConfig();
    		}
    		if(args[0].equalsIgnoreCase("waypoint")){
	    		if(args[1].equalsIgnoreCase("add")){
	    			if(args.length == 6){
	    				Waypoints.addWaypoint(args[2], Float.parseFloat(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
	    			}else {
	    				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Dumb fuck"));
	    				return;
	    			}
	    		}
	    		if(args[1].equalsIgnoreCase("delete")){
	    			if(args.length == 3) {
	    				Waypoints.deleteWaypoint(Integer.parseInt(args[2]));
	    			}else{
	    				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Wrong use of command /mesky waypoint delete [waypoint number]"));
	    			}
	    		}
	    		if(args[1].equalsIgnoreCase("list")){
    				Waypoints.showList();
    				return;
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
