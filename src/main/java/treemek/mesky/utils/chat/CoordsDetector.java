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
import treemek.mesky.config.SettingsConfig;

public class CoordsDetector {
	String sender;
	
	@SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
		if(Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.isRemote && event.type == 0) {
	        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
	        
	        String sender = getSender(message);
	        
	        if(sender != null) {
	        	if(sender.equals(Minecraft.getMinecraft().thePlayer.getName())) return;
	        }
	        if(!SettingsConfig.CoordsDetection.isOn) return;
	    	
	        if (message.contains("x:") && message.contains("y:") && message.contains("z:")) {
	            String[] messageWords = message.split(" ");
	            Float x = 0f, y = 0f, z = 0f;
	            for (int i = 0; i < messageWords.length; i++) {
	                try {
	                	if (messageWords[i].equals("x:")) {
	                		x = Float.parseFloat(getCoordinateValue(messageWords[i + 1]));
	                	}
	                	if (messageWords[i].equals("y:")) {
	                		y = Float.parseFloat(getCoordinateValue(messageWords[i + 1]));
	                	}
	                	if (messageWords[i].equals("z:")) {
	                		z = Float.parseFloat(getCoordinateValue(messageWords[i + 1]));
	                	}
	                    
	                } catch (Exception e) {
	                    // Handle parsing errors
	                    return;
	                }
	            }
	            sendChatQuestion(x,y,z, sender);
	        }else {
	        	if(message.matches(".*\\d.*")) { // has number in it
	        		String[] messageWords = message.split(" ");
	        		Float x = null, y = null, z = null;
	        		for (int i = 0; i < messageWords.length; i++) {
	                    try {
	                    	if (isNumeric(messageWords[i])) {
	                    		if(x == null) {
	                    			x = Float.parseFloat(getCoordinateValue(messageWords[i]));
	                    			if(messageWords.length >= i+3) {
		                    			if (isNumeric(messageWords[i+1])) {
		                    				y = Float.parseFloat(getCoordinateValue(messageWords[i+1]));
		                    				if (isNumeric(messageWords[i+2])) {
		                        				z = Float.parseFloat(getCoordinateValue(messageWords[i+2]));
		                        				System.out.println(messageWords.length + " " + (i+3));
		                        				if(messageWords.length > (i + 3)) {
			                        				if (isNumeric(messageWords[i+3])) {
			                            				// idk who puts 4 numbers in row like that in message but that isnt coords
			                        					x = null;
			                        					y = null;
			                        					z = null;
			                        					i += 4;
			                            			}else{
			                            				sendChatQuestion(x,y,z, sender);
			                            			}
		                        				}else {
		                        					sendChatQuestion(x,y,z, sender);
		                        				}
		                        			}
		                    			}
	                    			}
	                    		}
	                    		
	                    	}
	                    } catch (Exception e) {
	                        // Handle parsing errors
	                    	e.printStackTrace();
	                        return;
	                    }
	                }
	        	}
	        }
		}
    }

    private boolean haveSender(String message) {
    	if(!message.contains(":")) return false;
    	int colonIndex = message.replace("ቾ", "").replace("⚒", "").indexOf(':');
    	if(colonIndex-2 < 0) return true; // it means that the nick is one letter long :O
    	if(message.length() < 3 || colonIndex < 3) return false;
        Character beforeColon = message.charAt(colonIndex-1);
        Character before2times = message.charAt(colonIndex-2);
        if(beforeColon.equals('x') && before2times.equals(' ')) return false; // no sender (probably servers/mod message)
        return true;
	}

	private void sendChatQuestion(Float x, Float y, Float z, String sender) {
        ChatComponentText detectionMessage = new ChatComponentText(EnumChatFormatting.YELLOW + "Detected coords: " + EnumChatFormatting.GOLD + x + ", " + y + ", " + z);
        
        if(sender == null) sender = "fromChat" + Math.round(Math.random()*1000);
        
        ChatStyle temp = new ChatStyle();
        temp.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky tempwaypoint " + sender + " E66758 " + x + " " + y + " " + z + " " + SettingsConfig.MarkWaypointTime));
        temp.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Create a temporary waypoint")));
        temp.setColor(EnumChatFormatting.RED); // Set the color of the button text
        
        ChatComponentText clickableMessage = new ChatComponentText(" [MARK]");
        clickableMessage.setChatStyle(temp); 
        
        detectionMessage.appendSibling(clickableMessage);
        
        ChatStyle normal = new ChatStyle();
        normal.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky waypoint " + sender + " E66758 " + x + " " + y + " " + z));
        normal.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Adds waypoint")));
        normal.setColor(EnumChatFormatting.DARK_RED); // Set the color of the button text
        
        ChatComponentText clickableNormal = new ChatComponentText(" [ADD WAYPOINT]");
        clickableNormal.setChatStyle(normal); 
        
        detectionMessage.appendSibling(clickableNormal);
        
        Minecraft.getMinecraft().thePlayer.addChatMessage(detectionMessage);
		
	}
    
    // napraw self message sending afjoanfibghuanf

	private String getCoordinateValue(String coordinate) {
        return coordinate.endsWith(",") ? coordinate.substring(0, coordinate.length() - 1) : coordinate;
    }
    
    private String getSender(String message) {
        // Extract sender's name from the message (assuming it's at the start before ":")
    	String regex = "[ቾ⚒]";
    	message = message.replaceAll(regex, "");
        int colonIndex = message.indexOf(':');
        if (colonIndex != -1) {
        	int spaceIndexBeforeColon = message.lastIndexOf(' ', colonIndex);
            if (spaceIndexBeforeColon != -1) {
	            String senderName = message.substring(spaceIndexBeforeColon + 1, colonIndex).trim();
	            return senderName;
            }
        }
        return null;
    }
    
    
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
}
