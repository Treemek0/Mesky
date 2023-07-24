package treemek.mesky;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import treemek.mesky.handlers.gui.SettingsGUI;
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
    		// ./mesky version
    		if(args[0].equalsIgnoreCase("version")){
    			sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "v" + Reference.VERSION));
    			return;
    		}
    		if(args[0].equalsIgnoreCase("addAlert")){
    			if(args.length == 3) {
    				String[] alert = {args[1], args[2]};
    				Alerts.alerts.add(alert);
    				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added: /nTrigger: " + args[1] + "/nText: " + args[2]));
    				return;
    			}else{
    				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Wrong use of command /mesky addAlert [triggerMessage] [alertShown]"));
    				return;
    			}
    		}
    		if(args[0].equalsIgnoreCase("waypoint")){
    			if(args.length == 6){
    				Waypoints.addData(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]);
    				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added waypoint " + args[1]));
    			}else {
    				sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Dumb fuck"));
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
