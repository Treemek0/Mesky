package treemek.mesky.utils.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;

public class CoordsDetector {
	@SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
		if(Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.isRemote && event.type == 0) {
			String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
	        
	        if(!SettingsConfig.CoordsDetection.isOn) return;
	        
	        String sender = getSender(event.message.getFormattedText());
	        
	        if(sender != null) {
	        	if(getNameOfSender(sender).equals(Minecraft.getMinecraft().thePlayer.getName())) return;
	        }

	        if ((message.contains("x:") || message.contains("X:")) && (message.contains("y:") || message.contains("Y:")) && (message.contains("z:") || message.contains("Z:"))) {
	            String[] messageWords = message.split(" ");
	            Float x = 0f, y = 0f, z = 0f;
	            for (int i = 0; i < messageWords.length; i++) {
	                try {
	                	if (messageWords[i].equalsIgnoreCase("x:")) {
	                		x = Float.parseFloat(getCoordinateValue(messageWords[i + 1]));
	                	}
	                	if (messageWords[i].equalsIgnoreCase("y:")) {
	                		y = Float.parseFloat(getCoordinateValue(messageWords[i + 1]));
	                	}
	                	if (messageWords[i].equalsIgnoreCase("z:")) {
	                		z = Float.parseFloat(getCoordinateValue(messageWords[i + 1]));
	                	}
	                    
	                } catch (Exception e) {
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
	                    	if (isNumeric(getCoordinateValue(messageWords[i].replaceFirst("x", "").replaceFirst("X", "")))) {
	                    		if(x == null) {
	                    			x = Float.parseFloat(getCoordinateValue(messageWords[i].replaceFirst("x", "").replaceFirst("X", "")));
	                    			if(messageWords.length >= i+3) {
		                    			if (isNumeric(getCoordinateValue(messageWords[i+1].replaceFirst("y", "").replaceFirst("Y", "")))) {
		                    				y = Float.parseFloat(getCoordinateValue(messageWords[i+1].replaceFirst("y", "").replaceFirst("Y", "")));
		                    				if (isNumeric(getCoordinateValue(messageWords[i+2].replaceFirst("z", "").replaceFirst("Z", "")))) {
		                        				z = Float.parseFloat(getCoordinateValue(messageWords[i+2].replaceFirst("z", "").replaceFirst("Z", "")));
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
        ChatComponentText detectionMessage = new ChatComponentText(EnumChatFormatting.YELLOW + "@#");
        
        if(sender == null) sender = "" + ColorUtils.getRandomMinecraftColor() + x + EnumChatFormatting.WHITE + "/" + ColorUtils.getRandomMinecraftColor() + y + EnumChatFormatting.WHITE + "/" + ColorUtils.getRandomMinecraftColor() + z;
        
        
        String color = ColorUtils.getColorString(ColorUtils.getColorIntFromEnumChatFormatting(ColorUtils.getMostUsedColor(sender)));
        if(SettingsConfig.AutoMarkCoords.isOn) {
			Float radius = SettingsConfig.MarkWaypointRadius.number.floatValue();
			Long lifeTime = SettingsConfig.MarkWaypointTime.number.longValue();
			Waypoints.addTouchWaypoint(sender, color, x, y, z, 2, radius, lifeTime * 1000L);
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added mark (" + radius + "m, " + lifeTime + "s): " + EnumChatFormatting.DARK_PURPLE + sender + " " + EnumChatFormatting.GOLD + x + " " + y + " " + z));
        }
        
        ChatStyle mark = new ChatStyle();
        mark.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky mark \"" + sender + "\" " + + x + " " + y + " " + z + " " + color));
        mark.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Create a temporary waypoint")));
        mark.setColor(EnumChatFormatting.RED); // Set the color of the button text
        
        ChatComponentText clickableMark = new ChatComponentText(" [MARK]");
        clickableMark.setChatStyle(mark); 
        
        detectionMessage.appendSibling(clickableMark);
        
        ChatStyle add = new ChatStyle();
        add.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky waypoint \"" + sender + "\" " + color + " " + x + " " + y + " " + z + " 1"));
        add.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Adds waypoint")));
        add.setColor(EnumChatFormatting.DARK_RED); // Set the color of the button text
        
        ChatComponentText clickableAdd = new ChatComponentText(" [ADD WAYPOINT]");
        clickableAdd.setChatStyle(add); 
        
        detectionMessage.appendSibling(clickableAdd);
        
        ChatStyle copy = new ChatStyle();
        copy.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky copy " + x + " " + y + " " + z));
        copy.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copies coordinates to clipboard")));
        copy.setColor(EnumChatFormatting.DARK_GREEN); // Set the color of the button text
        
        ChatComponentText clickableCopy = new ChatComponentText(" [COPY]");
        clickableCopy.setChatStyle(copy); 
        
        detectionMessage.appendSibling(clickableCopy);
        
        Utils.addMinecraftMessage(EnumChatFormatting.YELLOW + "@# Detected coords: " + EnumChatFormatting.GOLD + x + ", " + y + ", " + z);
        Minecraft.getMinecraft().thePlayer.addChatMessage(detectionMessage);
		
	}
    
    // napraw self message sending afjoanfibghuanf

	public static String getCoordinateValue(String coordinate) {
        return coordinate.endsWith(",") ? coordinate.substring(0, coordinate.length() - 1) : coordinate;
    }
    
    private String getSender(String m) {
    	String regex = "[\u127BE\u2692\u2672\u24B7]";
    	String message = m.replaceAll(regex, "");
    	int colonIndex = message.indexOf(':');
    	try {
    		if (colonIndex != -1) {
            	if(message.charAt(colonIndex-3) == ']') {
            		// removing guild tags
            		message = message.substring(0, colonIndex);
            		Utils.debug(message);
            		colonIndex = message.lastIndexOf('[');
            		int nameIndexBeforeColon = message.indexOf('[');
    	            if (nameIndexBeforeColon != -1) {
    		            String senderName = message.substring(nameIndexBeforeColon - 2, colonIndex).trim();
    		            return senderName;
    	            }else {
    	            	nameIndexBeforeColon = message.indexOf('>');
    		            String senderName = message.substring(nameIndexBeforeColon + 1, colonIndex).trim();
    		            return senderName;
    	            }
            	}else {
    	        	int spaceIndexBeforeColon = message.indexOf('[');
    	            if (spaceIndexBeforeColon != -1) {
    		            String senderName = message.substring(spaceIndexBeforeColon - 2, colonIndex).trim();
    		            return senderName;
    	            }
            	}
            }
    		return null;
		} catch (Exception e) {
			return null;
		}
    }
    
    private String getNameOfSender(String message) {
    	message = StringUtils.stripControlCodes(message);
    	
        if (message.indexOf("\u127E") != -1) {
            message = message.substring(0, message.indexOf("\u127E") - 1);
        }
        
        if (message.indexOf("\u2692") != -1) {
            message = message.substring(0, message.indexOf("\u2692") - 1);
        }
    	
    	message = message.trim();
    	
    	int spaceIndexBeforeColon = message.lastIndexOf(' ');
        if (spaceIndexBeforeColon != -1) {
            String senderName = message.substring(spaceIndexBeforeColon + 1).trim();
            Utils.debug("Sender name: " + senderName);
            return senderName;
        }
        
        Utils.debug("Sender name: " + message);
    	return message;
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
