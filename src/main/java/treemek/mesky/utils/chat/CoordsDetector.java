package treemek.mesky.utils.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CoordsDetector {
	String sender;
	
	@SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
        
        if (message.contains("x:") && message.contains("y:") && message.contains("z:")) {
        	if(!haveSender(message)) return; // only detect coords from players
        	if(isMessageYours(message)) return; 
            String[] messageWords = message.split(" ");
            int x = 0, y = 0, z = 0;
            for (int i = 0; i < messageWords.length; i++) {
                try {
                	if (messageWords[i].equals("x:")) {
                		x = Integer.parseInt(getCoordinateValue(messageWords[i + 1]));
                	}
                	if (messageWords[i].equals("y:")) {
                		y = Integer.parseInt(getCoordinateValue(messageWords[i + 1]));
                	}
                	if (messageWords[i].equals("z:")) {
                		z = Integer.parseInt(getCoordinateValue(messageWords[i + 1]));
                	}
                    
                } catch (Exception e) {
                    // Handle parsing errors
                    return;
                }
            }
            sendChatQuestion(x,y,z);
        }
    }

    private boolean haveSender(String message) {
    	int colonIndex = message.replace("ቾ", "").replace("⚒️", "").indexOf(':');
        Character beforeColon = message.charAt(colonIndex-1);
        Character before2times = message.charAt(colonIndex-2);
        if(beforeColon.equals('x') && before2times.equals(' ')) return false; // no sender (probably servers/mod message)
        return true;
	}

	private void sendChatQuestion(int x, int y, int z) {
    	// Create a clickable message with the button
    	System.out.println("Sending" + x + " " + y + " " + z);
        ChatComponentText detectionMessage = new ChatComponentText(EnumChatFormatting.YELLOW + "Detected coords: " + EnumChatFormatting.GOLD + x + ", " + y + ", " + z);
        
        // Create a chat style for the button
        ChatStyle click = new ChatStyle();
        click.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky waypoint " + sender + " E66758 " + x + " " + y + " " + z));
        click.setColor(EnumChatFormatting.RED); // Set the color of the button text
        // Apply the style to the button text
        
        ChatComponentText clickableMessage = new ChatComponentText(" [ADD WAYPOINT]");
        clickableMessage.setChatStyle(click); 
        
        detectionMessage.appendSibling(clickableMessage);
        // Send the clickable message to the player
        Minecraft.getMinecraft().thePlayer.addChatMessage(detectionMessage);
		
	}
    
    // napraw self message sending afjoanfibghuanf

	private String getCoordinateValue(String coordinate) {
        return coordinate.endsWith(",") ? coordinate.substring(0, coordinate.length() - 1) : coordinate;
    }
    
    private boolean isMessageYours(String message) {
        // Extract sender's name from the message (assuming it's at the start before ":")
        int colonIndex = message.replace("ቾ", "").replace("⚒️", "").indexOf(':');
        if (colonIndex != -1) {
        	int spaceIndexBeforeColon = message.lastIndexOf(' ', colonIndex);
            if (spaceIndexBeforeColon != -1) {
	            String senderName = message.substring(spaceIndexBeforeColon + 1, colonIndex).trim();
	            sender = senderName;
	            return senderName.equals(Minecraft.getMinecraft().thePlayer.getName());
            }
        }
        return false;
    }
    
    
}
