package treemek.mesky.handlers.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.features.MaskTimer;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.chat.ChatFilter;
import treemek.mesky.utils.Alerts.Alert;

public class GuiLocations extends GuiScreen {
	
	List<GuiLocation> locations = new ArrayList<>();
	GuiLocation currentlyDragged;
	int offsetX = 0;
	int offsetY = 0;
		
	public class GuiLocation {
		Setting setting;
		Float[] position;
		Float scale;
		private float orgWidth;
		private float orgHeight;
		float width;
		float height;
		boolean ignoreScale = false;
		Boolean isOn;
		
		float[] AABB_min;
		float[] AABB_max;
		
		public GuiLocation(Setting setting, int width, int height) {
			this.setting = setting;
			this.scale = (float) (setting.scale * RenderHandler.getResolutionScale());
			this.position = setting.position;
			this.orgWidth = width;
			this.orgHeight = height;
			this.width = width * this.scale;
			this.height = height * this.scale;
		}
		
		public GuiLocation(boolean isOn, Setting setting, int width, int height) {
			this.setting = setting;
			this.scale = (float) (setting.scale * RenderHandler.getResolutionScale());
			this.position = setting.position;
			this.orgWidth = width;
			this.orgHeight = height;
			this.width = width * this.scale;
			this.height = height * this.scale;
			this.isOn = isOn;
		}
		
		public GuiLocation(Setting setting, int width, int height, boolean ignoreScale) {
			this.setting = setting;
			this.scale = (float) (setting.scale * RenderHandler.getResolutionScale());
			this.position = setting.position;
			this.orgWidth = width;
			this.orgHeight = height;
			this.width = width * this.scale;
			this.height = height * this.scale;
			this.ignoreScale = ignoreScale;
		}
		
		public void setPosition(Float[] newPosition) {
			if(newPosition != null) {
				if(AABB_min != null && AABB_max != null) {
					ScaledResolution sr = new ScaledResolution(mc);
					float deltaX = (newPosition[0] - position[0])/100 * sr.getScaledWidth();
					float deltaY = (newPosition[1] - position[1])/100 * sr.getScaledHeight();
					AABB_min[0] += deltaX;
					AABB_min[1] += deltaY;
					AABB_max[0] += deltaX;
					AABB_max[1] += deltaY;
				}
				
				setting.position = newPosition;
				position = newPosition;
			}
		}
		
		public void setScale(Float newScale) {
			if(newScale != null && !ignoreScale) {
				scale = (float) (newScale * RenderHandler.getResolutionScale());
				setting.scale = newScale;
				width = orgWidth * this.scale;
				height = orgHeight * this.scale;
			}
		}
			
		public boolean isOn() {
			if(isOn != null) return isOn;
			return setting.isOn;
		}
	}
	
	
		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			drawDefaultBackground();
	        
			// idfk how to do it man
			if(SettingsConfig.FishingTimer.isOn && !SettingsConfig.FishingTimerIs3d.isOn) FishingTimer.render2D(99.9f);;
			if(SettingsConfig.BonzoTimer.isOn) MaskTimer.renderBonzoMaskTimer(320f);
			if(SettingsConfig.SpiritTimer.isOn) MaskTimer.renderSpiritMaskTimer(30f);
	        if(SettingsConfig.customChat.isOn) ChatFilter.chat.drawPreviewChat(null, null);
			
			if(currentlyDragged != null) {
				int x = 0;
				int y = 0;
				if(currentlyDragged.AABB_max != null && currentlyDragged.AABB_min != null) {
					x = (int) currentlyDragged.AABB_min[0];
	            	y = (int) currentlyDragged.AABB_min[1];
				}else {
					x = Math.round(width * (currentlyDragged.position[0] / 100));
	            	y = Math.round(height * (currentlyDragged.position[1] / 100));
				}
	            Float scale = (float)Math.round(currentlyDragged.scale * 100) / 100;
				int fontHeight = fontRendererObj.FONT_HEIGHT;
	            
				if(!currentlyDragged.ignoreScale) {
					RenderHandler.drawText("x: " + x + ", y: " + y, x, y - (2*fontHeight) - 10, 0.7, true, 0xffffff);
					RenderHandler.drawText("scale: " + scale, x, y - fontHeight - 5, 0.7, true, 0xffffff);
				}else {
					RenderHandler.drawText("x: " + x + ", y: " + y, x, y - fontHeight - 5, 0.7, true, 0xffffff);
				}
			}
			
		    super.drawScreen(mouseX, mouseY, partialTicks);
		}
	
		@Override
		public void initGui() {
		    super.initGui();
		    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
		    
		    locations.add(new GuiLocation(SettingsConfig.FishingTimer.isOn && !SettingsConfig.FishingTimerIs3d.isOn, SettingsConfig.FishingTimer, fontRenderer.getStringWidth("99.9s") + 15, 17));
	        locations.add(new GuiLocation(SettingsConfig.BonzoTimer, fontRenderer.getStringWidth("320s") + 17, 17));
	        locations.add(new GuiLocation(SettingsConfig.SpiritTimer, fontRenderer.getStringWidth("30s") + 17, 17));
	        
	        ChatFilter.chat.update(new ScaledResolution(mc));
	        GuiLocation customChat = new GuiLocation(SettingsConfig.customChat, ChatFilter.chat.getChatWidth(width), ChatFilter.chat.getVisiblePreviewChatHeight(height), true);
	        customChat.AABB_min = new float[] { ChatFilter.chat.x, ChatFilter.chat.y - ChatFilter.chat.getVisiblePreviewChatHeight(height) };
	        customChat.AABB_max = new float[] { ChatFilter.chat.x + ChatFilter.chat.getChatWidth(width), ChatFilter.chat.y };
	        locations.add(customChat);
		}
		
		@Override
		public boolean doesGuiPauseGame() {
			return false;
		}
		
		@Override
	    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
	        for (GuiLocation location : locations) {
	            float x = (width * (location.position[0] / 100));
	            float y = (height * (location.position[1] / 100));
	            
	            if(!location.setting.isOn) continue;

	            if(location.AABB_max != null && location.AABB_min != null) {
	            	if (mouseX >= location.AABB_min[0] && mouseX <= location.AABB_max[0] && mouseY >= location.AABB_min[1] && mouseY <= location.AABB_max[1]) {
		                currentlyDragged = location;
		                offsetX = (int) (mouseX - x);
		                offsetY = (int) (mouseY - y);
		            }
	            }else {
					if (mouseX >= x && mouseX <= x + location.width && mouseY >= y && mouseY <= y + location.height) {
		                currentlyDragged = location;
		                offsetX = (int) (mouseX - x);
		                offsetY = (int) (mouseY - y);
		            }
	            }
	            
	        }
	    }

		@Override
		protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
	    {
			if (currentlyDragged != null) {
				Float x = ((mouseX - offsetX) / (float) width) * 100;
				Float y = ((mouseY - offsetY) / (float) height) * 100;

				if(currentlyDragged.AABB_max != null && currentlyDragged.AABB_min != null) { // only works for chat
					x = Math.max(0, Math.min(x, (width - (currentlyDragged.AABB_max[0] - currentlyDragged.AABB_min[0]))/width * 100));
					y = Math.max((currentlyDragged.AABB_max[1] - currentlyDragged.AABB_min[1])/height * 100, Math.min(y, 100));
				}else {
					x = Math.max(0, Math.min(x, (width - currentlyDragged.width)/width * 100));
					y = Math.max(0, Math.min(y, (height - currentlyDragged.height)/height * 100));
				}
				
	            Float[] newPosition = {x, y};
	            currentlyDragged.setPosition(newPosition);
			}
	    }
		
		protected void mouseReleased(int mouseX, int mouseY, int state)
	    {
	        if(currentlyDragged != null) {
	        	locations.set(locations.indexOf(currentlyDragged), currentlyDragged);
	        	ConfigHandler.saveSettings();
	        	currentlyDragged = null;
	        	offsetX = 0;
	        	offsetY = 0;
	        }
	    }

		@Override
	    public void handleMouseInput() throws IOException {
	        super.handleMouseInput();
	        int scroll = Mouse.getEventDWheel();
	        
	        if (scroll != 0 && currentlyDragged != null) {
	            float SCROLL_SPEED = (currentlyDragged.scale < 3)?0.05f:0.1f;
	        	Float newScale = Math.max(0.5f, currentlyDragged.setting.scale - (scroll > 0 ? -SCROLL_SPEED : SCROLL_SPEED));
	        	currentlyDragged.setScale(((float)Math.floor(newScale * 100)) / 100);
	        }
		}
}
