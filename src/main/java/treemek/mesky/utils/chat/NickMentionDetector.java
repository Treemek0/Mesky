package treemek.mesky.utils.chat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Utils;

public class NickMentionDetector {
	
	// crimson isle doestroys everything
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChat(ClientChatReceivedEvent event) {
		if(Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.isRemote && (event.type == 0 || event.type == 1)) {
			if(event.isCanceled()) return;
			if(!SettingsConfig.NickMentionDetection.isOn) return;
			
	        String message = event.message.getFormattedText();
	        IChatComponent chatComponent = event.message;
	        
	        
	        if(!message.contains(":")) return;
	        String[] splitMessage = splitMessageAndAuthor(message);
	        
	        
	        if(splitMessage[1].contains(Minecraft.getMinecraft().thePlayer.getGameProfile().getName())) {
		        List<IChatComponent> siblings = chatComponent.getSiblings();
		  
		        IChatComponent newChatComponent = new ChatComponentText("");
		        
		        if(siblings.size() > 0) {
		        	newChatComponent = getMessageWithNickMention(siblings, chatComponent);
		        }else {
		        	newChatComponent =  getMessageWithNickMention(message);
		        	
		        	if(chatComponent.getChatStyle().getChatClickEvent() != null) {
			        	ChatStyle click = new ChatStyle();
			            click.setChatClickEvent(chatComponent.getChatStyle().getChatClickEvent());
			            newChatComponent = newChatComponent.setChatStyle(click);
		        	}	        
	        	}
      	
		        
		        if(!newChatComponent.equals(new ChatComponentText(""))) {
			        event.message = newChatComponent;
		        }
	        }
		}
	}
	
	public IChatComponent getMessageWithNickMention(String message){
		// without siblings
		String author = splitMessageAndAuthor(message)[0];
		String onlyMessage = splitMessageAndAuthor(message)[1];
		
		String nick = Minecraft.getMinecraft().thePlayer.getGameProfile().getName();
		
		String componentText = author;
		for (Integer i : findWordPositions(onlyMessage, nick)) {
			componentText += onlyMessage.substring(0, i) + ColorUtils.getEnumFromColorName(SettingsConfig.NickMentionDetectionColor.text) + "@" + nick;
			onlyMessage = onlyMessage.substring(i + nick.length());
		}

		if(onlyMessage.length() > 0) {
			componentText += onlyMessage;
		}
		
		IChatComponent component = new ChatComponentText(componentText);
		return component;
	}
	
	public IChatComponent getMessageWithNickMention(List<IChatComponent> siblings, IChatComponent messageComponent){
		IChatComponent chatComponent = new ChatComponentText("");
		
		
		String nick = Minecraft.getMinecraft().thePlayer.getGameProfile().getName();
		boolean hadColon = false;
		String fullMessage = messageComponent.getFormattedText();
		
		
		for (int i = 0; i < siblings.size(); i++) {
			IChatComponent current = siblings.get(i);
			String message = current.getFormattedText();
			EnumChatFormatting color = EnumChatFormatting.RESET;
			
			String componentText = "";
			
			if(i == 0 && !fullMessage.startsWith(message)) {
				// idk why it skipped first part of message lul
				String skippedPart = fullMessage.substring(0, fullMessage.indexOf(message));
				List<Integer> positions = findWordPositions(skippedPart, nick);
				if(!positions.isEmpty()) {
					for (Integer p : positions) {
						if(hadColon || !fullMessage.contains(":")) {
							skippedPart = skippedPart.replace(String.valueOf('\u127E'), "").replace(String.valueOf('\u2692'), "");
							componentText += color + skippedPart.substring(0, p) + ColorUtils.getEnumFromColorName(SettingsConfig.NickMentionDetectionColor.text) + "@" + nick;
							skippedPart = skippedPart.substring(p + nick.length());
						}else {
							hadColon = true;
						}
					}
					
					if(skippedPart.length() > 0) {
						componentText += color + skippedPart;
					}
				}else {
					componentText += color + skippedPart;
				}
			}
			
			List<Integer> positions = findWordPositions(message, nick);
			if(!positions.isEmpty()) {
				for (Integer p : positions) {
					if(hadColon || !fullMessage.contains(":")) {
						// idk how tf does that work when i substring from message and then try to get position from already substringed message IT SHOULD GIVE DIFFERENT RESOULT
						message = message.replace(String.valueOf('\u127E'), "").replace(String.valueOf('\u2692'), "");
						componentText += color + message.substring(0, p) + ColorUtils.getEnumFromColorName(SettingsConfig.NickMentionDetectionColor.text) + "@" + nick;
						message = message.substring(p + nick.length());
					}else {
						hadColon = true;
					}
				}
				
				if(message.length() > 0) {
					componentText += color + message;
				}
			}else {
				componentText += color + message;
			}
		
			IChatComponent component = new ChatComponentText(componentText);
			
			if(current.getChatStyle().getChatClickEvent() != null) {
				ChatStyle click = new ChatStyle();
	            click.setChatClickEvent(current.getChatStyle().getChatClickEvent());
	            component.setChatStyle(click);
			}
			
            chatComponent.appendSibling(component);
		}
		
		return chatComponent;
	}
	
	public static List<Integer> findWordPositions(String text, String word) {
        List<Integer> positions = new ArrayList<>();
        int index = text.indexOf(word);
        
        while (index >= 0) {
            positions.add(index);
            text = text.substring(index + word.length());
            index = text.indexOf(word);
        }
        
        return positions;
    }
	
	private boolean haveSender(String message) {
    	if(!message.contains(":")) return false;
    	if(!HypixelCheck.isOnHypixel() && message.contains(":")) return true;
    	int colonIndex = message.replace(String.valueOf('\u127E'), "").replace(String.valueOf('\u2692'), "").indexOf(':');
    	if(colonIndex-2 < 0) return true; // it means that the nick is one letter long :O
    	if(message.length() < 3 || colonIndex < 3) return false;
        if(StringUtils.stripControlCodes(message).startsWith("[")) return true;
        return false;
	}
	
	
	private String[] splitMessageAndAuthor(String message) {
		if(haveSender(message)) {
			Integer p = (message.indexOf(":")+1 > 0)? message.indexOf(":")+1 : 0;
			String author = message.substring(0, p);
			String onlyMessage = message.substring(p).replace(String.valueOf('\u127E'), "").replace(String.valueOf('\u2692'), "");
			return new String[] {author,onlyMessage};
		}else {
			return new String[] {"", message};
		}
	}
}
