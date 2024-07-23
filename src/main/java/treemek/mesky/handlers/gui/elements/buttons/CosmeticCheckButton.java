package treemek.mesky.handlers.gui.elements.buttons;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.handlers.RenderHandler;

public class CosmeticCheckButton extends GuiButton{
	String buttonText;
	ResourceLocation image;
	int checkSize;
	public Setting setting;
	private int cosmeticId;
	
	public CosmeticCheckButton(int buttonId, int x, int y, int width, int height, String buttonText, Setting setting, int cosmeticId, ResourceLocation image, int checkSize) {
		super(buttonId, x, y, width, height, buttonText);
		this.xPosition = x;
		this.yPosition = y;
		this.buttonText = buttonText;
		this.setting = setting;
		this.cosmeticId = cosmeticId;
		this.image = image;
		this.checkSize = checkSize;
	}
	
	
	ResourceLocation empty = new ResourceLocation(Reference.MODID, "gui/check-empty.png");
	ResourceLocation check = new ResourceLocation(Reference.MODID, "gui/check.png");
	
	public boolean isFull() {
		return setting.number == cosmeticId;
	}
	
	public void changeFull() {
		if(setting.number == cosmeticId) setting.number = 0D;
        else setting.number = (double) cosmeticId;
		ConfigHandler.saveSettings();
	}
	
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        
        int left = xPosition;
        int top = yPosition;
		int right = xPosition + width;
        int bottom = yPosition + height;
		
        if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height) {
            hovered = true;
        } else {
            hovered = false;
        }
        
        drawRect(left-1, top-1, right+1, bottom+1, new Color(1, 1, 1, 255).getRGB());
        
		drawRect(left, top, right, bottom, new Color(28, 28, 28, 255).getRGB());
		
		// Reset color and blending state before drawing buttons
	    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

		
		if(isFull()) {
			mc.renderEngine.bindTexture(check);
		}else {
			mc.renderEngine.bindTexture(empty);
		}
		// drawing button
		int checkX = xPosition + (width/2) - (checkSize/2);
		int checkY = (int)(yPosition + (height*0.1));
		drawModalRectWithCustomSizedTexture(checkX, checkY, 0, 0, checkSize, checkSize, checkSize, checkSize);
		
		
		// drawing image
		mc.renderEngine.bindTexture(image);
		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
	}
	

	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if(hovered) {
			changeFull();
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
	
	
}
