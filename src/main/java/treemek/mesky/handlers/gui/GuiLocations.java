package treemek.mesky.handlers.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.GuiLocationConfig;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.buttons.CheckButton;

public class GuiLocations extends GuiScreen {
	
	List<GuiLocation> locations = new ArrayList<>();
	GuiLocation currentlyDragged;
	
	public class GuiLocation {
		float[] position;
		int width;
		int height;
		
		public GuiLocation(float[] positon, int width, int height) {
			this.position = positon;
			this.width = width;
			this.height = height;
		}
		
		public void setPosition(float[] newPosition) {
			System.arraycopy(newPosition, 0, this.position, 0, newPosition.length);
		}
		
	}
	
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		// idfk how to do it man
		renderFishingTimer();
		renderBonzoMask();
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
	    
	    locations.add(new GuiLocation(GuiLocationConfig.fishingTimer, fontRenderer.getStringWidth("99.9s") + 15, 17));
        locations.add(new GuiLocation(GuiLocationConfig.bonzoMaskTimer, fontRenderer.getStringWidth("320s") + 17, 17));
        locations.add(new GuiLocation(GuiLocationConfig.spiritMaskTimer, fontRenderer.getStringWidth("30s") + 17, 17));
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (GuiLocation location : locations) {
            int x = (int) (width * (location.position[0] / 100));
            int y = (int) (height * (location.position[1] / 100));
            
            if (mouseX >= x && mouseX <= x + location.width && mouseY >= y && mouseY <= y + location.height) {
                currentlyDragged = location;
            }
        }
    }
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		if (currentlyDragged != null) {
            float[] newPosition = { (mouseX / (float) width) * 100, (mouseY / (float) height) * 100 };
            currentlyDragged.setPosition(newPosition);

		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if(currentlyDragged != null) {
        	locations.set(locations.indexOf(currentlyDragged), currentlyDragged);
        	currentlyDragged = null;
        }
    }

	public void renderFishingTimer() {
		float x = width * (GuiLocationConfig.fishingTimer[0] / 100);
		float y = height * (GuiLocationConfig.fishingTimer[1] / 100);
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
		fontRenderer.drawStringWithShadow("99.9s", x + 15, y, 0xFFFFFF);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/bobber.png"));
        drawModalRectWithCustomSizedTexture((int)x, (int)(y - 5), 0, 0, 12, 17, 12, 17);
	}
	
	public void renderBonzoMask() {
		float x = width * (GuiLocationConfig.bonzoMaskTimer[0] / 100);
		float y = height * (GuiLocationConfig.bonzoMaskTimer[1] / 100);
		RenderHandler.drawText("320s", x + 17, y, 1, false, 0xFFFFFF);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/Bonzo_Head.png"));
        drawModalRectWithCustomSizedTexture((int)x, (int)y - 5, 0, 0, 17, 17, 17, 17);
	}
	
	public void renderSpiritMask() {
		float x = width * (GuiLocationConfig.spiritMaskTimer[0] / 100);
		float y = height * (GuiLocationConfig.spiritMaskTimer[1] / 100);
		RenderHandler.drawText("30s", x + 17, y, 1, false, 0xFFFFFF);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/Spirit_Head.png"));
        drawModalRectWithCustomSizedTexture((int)x, (int)y - 5, 0, 0, 17, 17, 17, 17);
	}
	
}
