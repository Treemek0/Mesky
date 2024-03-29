package treemek.mesky;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import treemek.mesky.config.ConfigHandler;
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
    		if(args[0].equalsIgnoreCase("reload")) {
    			ConfigHandler.reloadConfig();
    		}else if(args[0].equalsIgnoreCase("fishing")) {
    			FishingTimer.isText3d = !FishingTimer.isText3d;
    		}else if(args[0].equalsIgnoreCase("friend")) {
    			String nick = args[1];
    			Location location = FriendsLocations.getLocationForPlayer(nick);
    			if(location == Location.NONE) {
    				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("We don't have a location for this nickname. Either you didn't have this mod when you added this friend or you were outside Skyblock."));
    			}else {
				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(nick + ": " + location.name()));
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
