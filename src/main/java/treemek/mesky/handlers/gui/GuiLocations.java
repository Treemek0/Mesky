package treemek.mesky.handlers.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Alerts.Alert;

public class GuiLocations extends GuiScreen {
	
	List<GuiLocation> locations = new ArrayList<>();
	GuiLocation currentlyDragged;
	
	public class GuiLocation {
		Setting setting;
		Float[] position;
		Float scale;
		private float orgWidth;
		private float orgHeight;
		float width;
		float height;
		
		public GuiLocation(Setting setting, int width, int height) {
			this.setting = setting;
			this.scale = setting.scale;
			this.position = setting.position;
			this.orgWidth = width;
			this.orgHeight = height;
			this.width = width * this.scale;
			this.height = height * this.scale;
			
		}
		
		public void setPosition(Float[] newPosition) {
			if(newPosition != null) {
				setting.position = newPosition;
				position = newPosition;
			}
		}
		
		public void setScale(Float newScale) {
			if(newScale != null) {
				setting.scale = scale;
				scale = newScale;
				width = orgWidth * newScale;
				height = orgHeight * newScale;
			}
		}
		
	}
	
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		// idfk how to do it man
		if(SettingsConfig.FishingTimer.isOn) renderFishingTimer();
		if(SettingsConfig.BonzoTimer.isOn) renderBonzoMask();
		if(SettingsConfig.SpiritTimer.isOn) renderSpiritMask();
        
		if(currentlyDragged != null) {
			int x = Math.round(width * (currentlyDragged.position[0] / 100));
            int y = Math.round(height * (currentlyDragged.position[1] / 100));
            Float scale = (float)Math.round(currentlyDragged.scale * 100) / 100;
			int fontHeight = fontRendererObj.FONT_HEIGHT;
            
			RenderHandler.drawText("x: " + x + ", y: " + y, x, y - (2*fontHeight) - 10, 0.7, true, 0xffffff);
			RenderHandler.drawText("scale: " + scale, x, y - fontHeight - 5, 0.7, true, 0xffffff);
		}
		
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
	    
	    locations.add(new GuiLocation(SettingsConfig.FishingTimer, fontRenderer.getStringWidth("99.9s") + 15, 17));
        locations.add(new GuiLocation(SettingsConfig.BonzoTimer, fontRenderer.getStringWidth("320s") + 17, 17));
        locations.add(new GuiLocation(SettingsConfig.SpiritTimer, fontRenderer.getStringWidth("30s") + 17, 17));
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
            
            if (mouseX >= x && mouseX <= x + location.width && mouseY >= y && mouseY <= y + location.height) {
                currentlyDragged = location;
            }
        }
    }

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if (currentlyDragged != null) {
			mouseX = (int) Math.min(mouseX, width - currentlyDragged.width);
			mouseY = (int) Math.min(mouseY, height - currentlyDragged.height);
			
			Float x = (mouseX / (float) width) * 100;
			Float y = (mouseY / (float) height) * 100;
			
            Float[] newPosition = { x, y};
            currentlyDragged.setPosition(newPosition);
		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if(currentlyDragged != null) {
        	locations.set(locations.indexOf(currentlyDragged), currentlyDragged);
        	ConfigHandler.saveSettings();
        	currentlyDragged = null;
        }
    }

	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        
        if (scroll != 0 && currentlyDragged != null) {
            float SCROLL_SPEED = (currentlyDragged.scale < 3)?0.05f:0.1f;
        	Float newScale = Math.max(0.5f, currentlyDragged.scale - (scroll > 0 ? -SCROLL_SPEED : SCROLL_SPEED));
        	currentlyDragged.setScale(((float)Math.round(newScale * 100)) / 100);
        }
	}
	
	public void renderFishingTimer() {
		Float scale = SettingsConfig.FishingTimer.scale;
		float x = width * (SettingsConfig.FishingTimer.position[0] / 100);
		float y = height * (SettingsConfig.FishingTimer.position[1] / 100);
		RenderHandler.drawText("99.9s", x + (15*scale), y + (5*scale), scale, true, 0xFFFFFF);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/bobber.png"));
        drawModalRectWithCustomSizedTexture((int)x, (int)(y), 0, 0, (int)(12 * scale), (int)(17 * scale), (int)(12 * scale), (int)(17 * scale));
	}
	
	public void renderBonzoMask(){
		Float scale = SettingsConfig.BonzoTimer.scale;
		float x = width * (SettingsConfig.BonzoTimer.position[0] / 100);
		float y = height * (SettingsConfig.BonzoTimer.position[1] / 100);
		RenderHandler.drawText("320s", x + (17*scale), y + (5*scale), scale, true, 0xFFFFFF);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/Bonzo_Head.png"));
        drawModalRectWithCustomSizedTexture((int)x, (int)y, 0, 0, (int)(17*scale), (int)(17*scale), (int)(17*scale), (int)(17*scale));
	}
	
	public void renderSpiritMask() {
		Float scale = SettingsConfig.SpiritTimer.scale;
		float x = width * (SettingsConfig.SpiritTimer.position[0] / 100);
		float y = height * (SettingsConfig.SpiritTimer.position[1] / 100);
		RenderHandler.drawText("30s", x + (17*scale), y + (5*scale), scale, true, 0xFFFFFF);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/Spirit_Mask.png"));
        drawModalRectWithCustomSizedTexture((int)x, (int)y, 0, 0, (int)(17*scale), (int)(17*scale), (int)(17*scale), (int)(17*scale));
	}
	
}
