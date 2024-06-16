package treemek.mesky.utils.chat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.config.SettingsConfig;

public class NickMentionDetector {
	
	// /pl doesnt work
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChat(ClientChatReceivedEvent event) {
//		if(Minecraft.getMinecraft().theWorld.isRemote && event.type == 0) {
//			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("was message"));
//			if(event.isCanceled()) return;
//			if(!SettingsConfig.NickMentionDetection) return;
//			
//			
//	        String message = event.message.getFormattedText();
//	        IChatComponent chatComponent = event.message;
//	        
//	        String[] splitMessage = splitMessageAndAuthor(message);
//	        
//	        System.out.println(Minecraft.getMinecraft().thePlayer.getGameProfile().getName().toString());
//	        
//	        if(splitMessage[1].contains(Minecraft.getMinecraft().thePlayer.getGameProfile().getName())) {
//		        List<IChatComponent> siblings = chatComponent.getSiblings();
//		  
//		        IChatComponent newChatComponent = new ChatComponentText("");
//		        
//		        if(siblings.size() > 0) {
//		        	newChatComponent = getMessageWithNickMention(siblings, chatComponent);
//		        }else {
//		        	ChatStyle click = new ChatStyle();
//		            click.setChatClickEvent(chatComponent.getChatStyle().getChatClickEvent());
//		            newChatComponent = getMessageWithNickMention(message).setChatStyle(click);
//		            System.out.println(newChatComponent);		        }
//		        
//		        if(!newChatComponent.equals(new ChatComponentText(""))) {
//			        event.setCanceled(true);
//					Minecraft.getMinecraft().thePlayer.addChatMessage(newChatComponent);
//		        }
//	        }
//		}
	}
	
	public IChatComponent getMessageWithNickMention(String message){
		// without siblings
		String author = splitMessageAndAuthor(message)[0];
		String onlyMessage = splitMessageAndAuthor(message)[1];
		
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("onlyString"));
		
		String nick = Minecraft.getMinecraft().thePlayer.getGameProfile().getName();
		
		ChatComponentText component = new ChatComponentText(author);
		for (Integer i : findWordPositions(onlyMessage, nick)) {
			component.appendText(onlyMessage.substring(0, i));
			component.appendText(EnumChatFormatting.DARK_AQUA + nick);
			onlyMessage = onlyMessage.substring(i + nick.length());
		}

		if(onlyMessage.length() > 0) {
			component.appendText(onlyMessage);
		}
		
		return component;
	}
	
	public IChatComponent getMessageWithNickMention(List<IChatComponent> siblings, IChatComponent messageComponent){
		IChatComponent chatComponent = new ChatComponentText("");
		
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("siblings"));
		
		String nick = Minecraft.getMinecraft().thePlayer.getGameProfile().getName();
		boolean hadColon = false;
		String fullMessage = messageComponent.getFormattedText();
		
		for (int i = 0; i < siblings.size(); i++) {
			IChatComponent current = siblings.get(i);
				
			String message = current.getFormattedText();
			ChatComponentText component = new ChatComponentText("");
			
			
			
			if(i == 0 && !fullMessage.startsWith(message)) {
				// idk why it skipped first part of message lul
				String skippedPart = fullMessage.substring(0, fullMessage.indexOf(message));
				List<Integer> positions = findWordPositions(skippedPart, nick);
				if(!positions.isEmpty()) {
					for (Integer p : positions) {
						if(hadColon || !fullMessage.contains(":")) {
							component.appendText(skippedPart.substring(0, p));
							component.appendText(EnumChatFormatting.DARK_AQUA + skippedPart.substring(p + nick.length()));
							skippedPart = skippedPart.substring(p + nick.length());
						}else {
							hadColon = true;
						}
					}
					
					if(skippedPart.length() > 0) {
						component.appendText(skippedPart);
					}
				}else {
					component.appendText(skippedPart);
				}
			}
			
			List<Integer> positions = findWordPositions(message, nick);
			if(!positions.isEmpty()) {
				for (Integer p : positions) {
					if(hadColon || !fullMessage.contains(":")) {
						// idk how tf does that work when i substring from message and then try to get position from already substringed message IT SHOULD GIVE DIFFERENT RESOULT
						component.appendText(message.substring(0, p));
						component.appendText(EnumChatFormatting.DARK_AQUA + message.substring(p + nick.length()));
						message = message.substring(p + nick.length());
					}else {
						hadColon = true;
					}
				}
				
				if(message.length() > 0) {
					component.appendText(message);
				}
			}else {
				component.appendText(message);
			}
		
			
			
			ChatStyle click = new ChatStyle();
            click.setChatClickEvent(current.getChatStyle().getChatClickEvent());
            component.setChatStyle(click);
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
    	int colonIndex = message.replace("ቾ", "").replace("⚒️", "").indexOf(':');
    	if(colonIndex-2 < 0) return true; // it means that the nick is one letter long :O
    	if(message.length() < 3 || colonIndex < 3) return false;
        if(StringUtils.stripControlCodes(message).startsWith("[")) return true;
        return false;
	}
	
	
	private String[] splitMessageAndAuthor(String message) {
		if(haveSender(message)) {
			Integer p = (message.indexOf(":")+1 > 0)? message.indexOf(":")+1 : 0;
			String author = message.substring(0, p);
			String onlyMessage = message.substring(p);
			return new String[] {author,onlyMessage};
		}else {
			return new String[] {"", message};
		}
	}
}
