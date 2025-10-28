package treemek.mesky.utils.chat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import treemek.mesky.Mesky;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.handlers.gui.elements.Popup;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.chat.customChat.CustomChat;

public class ChatFilter {
	public static CustomChat chat;
	public static Popup popup;
	
	int oldWidth = 0;
	int oldHeight = 0;
	
	public ChatFilter() {
		chat = new CustomChat(Minecraft.getMinecraft());
		popup = new Popup(1500, true);
	}

	long lineNumber = 0;
	Map<Long, Setting> hiddenLines = new HashMap<>(); 

	@SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
		if(Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.isRemote && event.type == 0) {
			lineNumber++;
			
			if(hiddenLines.containsKey(lineNumber)) {
				checkFilter(event, hiddenLines.get(lineNumber));
				hiddenLines.remove(lineNumber);
			}
			
			String message = ColorUtils.removeTextFormatting(event.message.getFormattedText());
			
			if(!message.contains(":")) {
				if(message.contains("Sending to server ")) {
					checkFilter(event, SettingsConfig.sendingToServer_filter);
					return;
				}
				
				if(message.contains(" was killed by ")) {
					checkFilter(event, SettingsConfig.wasKilledBy_filter);
					return;
				}
				
				if(message.contains("FIRE SALE") && message.contains("Selling an item for a limited time!")) {
					checkFilter(event, SettingsConfig.fireSale_filter);
					checkLine(lineNumber+1, SettingsConfig.fireSale_filter);
					return;
				}
				
				if(message.contains("Warping...") || message.contains("Warping you to your SkyBlock island...")) {
					checkFilter(event, SettingsConfig.warping_filter);
					return;
				}
				
				if(message.contains("+") && message.contains("Kill Combo")) {
					checkFilter(event, SettingsConfig.killCombo_filter);
					return;
				}
				
				if(message.contains("You earned") && message.contains("Event EXP")) {
					checkFilter(event, SettingsConfig.eventEXP_filter);
					return;
				}
			}else {
				if(message.contains("You are playing on profile: ") && message.indexOf("You are playing on profile: ") == 0) {
					checkFilter(event, SettingsConfig.playingOnProfile_filter);
					return;
				}
				
				if(message.contains("Profile ID: ") && message.indexOf("Profile ID: ") == 0) {
					checkFilter(event, SettingsConfig.playingOnProfile_filter);
					return;
				}
			}
			
			if(Mesky.debug) {
				event.message.appendText(EnumChatFormatting.RED + " [" + lineNumber + "]");
			}
		}
	}
	
	private void checkLine(long line, Setting setting) {
		if(line > this.lineNumber) {
			hiddenLines.put(line, setting);
		}
	}
	
	private void checkFilter(ClientChatReceivedEvent event, Setting setting) {
		if(setting.text.equals("SEPARATE")) {
			if(SettingsConfig.customChat.isOn) {
				event.setCanceled(true);
				chat.addChatMessage(event.message);
			}
		}else if(setting.text.equals("HIDDEN")) {
			event.setCanceled(true);
		}
	}
	
	public static void checkFilterAndSend(Setting setting, String comp) {
		if(setting.text.equals("SEPARATE")) {
			if(SettingsConfig.customChat.isOn) {
				chat.addChatMessage(comp);
			}
		}else if(setting.text.equals("POPUP")) {
			popup.showPopup(comp);
		}else if(setting.text.equals("CHAT")) {
			Utils.addMinecraftMessage(comp);
		}
	}
	
	public static void checkFilterAndSend(Setting setting, IChatComponent comp) {
		if(setting.text.equals("SEPARATE")) {
			if(SettingsConfig.customChat.isOn) {
				chat.addChatMessage(comp);
			}
		}else if(setting.text.equals("POPUP")) {
			popup.showPopup(comp.getFormattedText());
		}else if(setting.text.equals("CHAT")) {
			Utils.addMinecraftMessage(comp);;
		}
	}
	
	@SubscribeEvent
	public void drawSeparateChat(RenderGameOverlayEvent.Chat event) {		
		chat.fadeStart = SettingsConfig.customChatFadeStart.number.longValue();
		chat.fadeDuration = SettingsConfig.customChatFadeDuration.number.longValue();
		chat.rightPacing = SettingsConfig.customChatRightPacing.isOn;
		
		chat.changeSize(SettingsConfig.customChatWidth.number.floatValue(), SettingsConfig.customChatHeight.number.floatValue());
		chat.setTextScale(SettingsConfig.customChatTextScale.number.floatValue());
		chat.changePosition(SettingsConfig.customChat.position[0], SettingsConfig.customChat.position[1]);
		chat.drawChat();
	}
	
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
        	if(Minecraft.getMinecraft().currentScreen == null || Minecraft.getMinecraft().currentScreen == chat) {
	        	if (SettingsConfig.customChatKeybind.keybind.isKeybindDown()) {
	        	    chat.openChat();
	        	} else {
	        		if(!SettingsConfig.customChatToggle.isOn) {
	        			chat.closeChat();
	        		}
	        	}
        	}
        }
    }
    
    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event) {
    	if(event.phase == TickEvent.Phase.START) return;
		popup.drawPopup(event.renderTickTime); // the problem is with rendering on small screen (add wraping to next line if too small) and also components with multiple siblings dont render :P
    }
    
	public static void updateClamp() {
		chat.changeSize(SettingsConfig.customChatWidth.number.floatValue(), SettingsConfig.customChatHeight.number.floatValue());
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		chat.update(sr);
		
		int chatWidth = chat.getChatWidth(sr.getScaledWidth());
		int chatHeight = chat.getVisiblePreviewChatHeight(sr.getScaledHeight());
		
		float x = Math.max(0, Math.min(chat.x, (float)(sr.getScaledWidth() - chatWidth))/sr.getScaledWidth() * 100);
		float y = Math.max(chatHeight, Math.min(chat.y, sr.getScaledHeight() - chatHeight)) / (float)sr.getScaledHeight() * 100f;

		SettingsConfig.customChat.position = new Float[] { x, y };
	}
	
	private List<ChatLine> getChatLines(GuiNewChat chatGUI) {
	    try {
	        Field field = GuiNewChat.class.getDeclaredField("chatLines");
	        field.setAccessible(true);
	        return (List<ChatLine>) field.get(chatGUI);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}
}
