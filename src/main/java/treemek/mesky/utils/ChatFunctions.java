package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.utils.Alerts.Alert;

public class ChatFunctions {
	
	public static List<ChatFunction> chatFunctionsList = new ArrayList<>();
	
	public static class ChatFunction {
		private String triggerMessage;
        private String function;
        public Boolean enabled;
        public Boolean onlyParty;
        public Boolean ignorePlayers;
        public Boolean isEqual;
        public Boolean ignoreSender;
        
        public ChatFunction(String trigger, String function, boolean onlyParty, boolean ignorePlayers, boolean isEqual, boolean enabled) {
	        this.triggerMessage = trigger;
	        this.function = function;
	        this.onlyParty = onlyParty;
	        this.ignorePlayers = ignorePlayers;
	        this.isEqual = isEqual;
	        this.enabled = enabled;
        }
        
        public void fixNulls() {
        	if(triggerMessage == null) triggerMessage = "";
        	if(function == null) function = "";
        	if(enabled == null) enabled = true;
        	if(onlyParty == null) onlyParty = false;
        	if(ignorePlayers == null) ignorePlayers = false;
        	if(isEqual == null) isEqual = false;
        }
        
        public String getTrigger() {
            return triggerMessage;
        }

        public String getFunction() {
            return function;
        }
        
        public boolean getOnlyParty() {
        	return onlyParty;
        }
        
        public boolean getIgnorePlayers() {
        	return ignorePlayers;
        }
        public boolean getIsEqual() {
        	return isEqual;
        }

		public boolean isEnabled() {
			return enabled;
		}
    }
	
	// Method to add data
    public static void addChatFunction(String trigger, String function, boolean onlyParty, boolean ignorePlayers, boolean isEqual) {
    	chatFunctionsList.add(new ChatFunction(trigger, function, onlyParty, ignorePlayers, isEqual, true));
		ConfigHandler.SaveChatFunction(chatFunctionsList);
    }
    
 // Method to add data
    public static void deleteChatFunction(int id) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Deleted: " + chatFunctionsList.get(id).getTrigger()));
		chatFunctionsList.remove(id);
		ConfigHandler.SaveChatFunction(chatFunctionsList);
		return;
    }
    
    

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		// Alerts
		if(Minecraft.getMinecraft().thePlayer == null) return;
		String message = event.message.getUnformattedText();
		String nickname = Minecraft.getMinecraft().thePlayer.getName();
		String nonColorMessage = StringUtils.stripControlCodes(message);
		String onlyNonColorMessage = nonColorMessage;
		boolean autor = false;
		
		if(nonColorMessage.contains(": ")){
			onlyNonColorMessage = nonColorMessage.substring(nonColorMessage.indexOf(":") + 1);
			String[] beforeMessage = nonColorMessage.split(":")[0].split(" ");
			
			for (String string : beforeMessage) {
				if(string.equals(nickname) || string.equals(nickname + ":")) autor = true;
			}
		}
		
		
		for(int i = 0; i < chatFunctionsList.size(); i++) {
			if(!chatFunctionsList.get(i).enabled) continue;
			if(chatFunctionsList.get(i).triggerMessage.equals("")) continue;
			if(chatFunctionsList.get(i).getIsEqual()) {
				if(onlyNonColorMessage.equals(chatFunctionsList.get(i).getTrigger())) {
					if(chatFunctionsList.get(i).getOnlyParty() && !nonColorMessage.startsWith("Party >")) continue;
					if(chatFunctionsList.get(i).getIgnorePlayers() && nonColorMessage.contains(": ")) continue;
					if(autor) return;
					Utils.executeCommand(chatFunctionsList.get(i).function);
					System.out.println("[Mesky] Executed from chatFunction: " + chatFunctionsList.get(i).function);
				}
			}else {
				if(message.contains(chatFunctionsList.get(i).getTrigger())) {
					if(chatFunctionsList.get(i).getOnlyParty() && !nonColorMessage.startsWith("Party >")) continue;
					if(chatFunctionsList.get(i).getIgnorePlayers() && nonColorMessage.contains(": ")) continue;
					if(autor) return; // ignores messages written by yourself
					Utils.executeCommand(chatFunctionsList.get(i).function);
					System.out.println("[Mesky] Executed from chatFunction: " + chatFunctionsList.get(i).function);
				}
			}
		}
	}
	
}
