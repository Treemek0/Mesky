package treemek.mesky;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.gui.GUI;
import treemek.mesky.utils.Alerts;
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
    		if(args[0].equalsIgnoreCase("reload")) {
    			ConfigHandler.reloadConfig();
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
