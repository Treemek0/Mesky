package treemek.mesky.handlers.gui.buttons;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class CosmeticCheckButton extends GuiButton{

	int x;
	int y;
	String buttonText;
	public boolean isFull;
	ResourceLocation image;
	
	
	public CosmeticCheckButton(int buttonId, int x, int y, int width, int height, String buttonText, boolean isFull, ResourceLocation image) {
		super(buttonId, x, y, width, height, buttonText);
		this.x = x;
		this.y = y;
		this.buttonText = buttonText;
		this.isFull = isFull;
		this.image = image;
	}
	
	
	ResourceLocation empty = new ResourceLocation(Reference.MODID, "gui/check-empty.png");
	ResourceLocation check = new ResourceLocation(Reference.MODID, "gui/check.png");
	
	public boolean isFull() {
		return isFull;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		
		
		// Query the texture width
        int imageSize = width * 4;
        
        int left = (x + width/2) - (imageSize/2) - 10;
        int top = y - 10;
		int right = (x + width/2) + (imageSize/2) + 10;
        int bottom = y + imageSize + 10;
		
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
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

		
		if(isFull) {
			mc.renderEngine.bindTexture(check);
		}else {
			mc.renderEngine.bindTexture(empty);
		}
		drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
		
		mc.renderEngine.bindTexture(image);
		
		
        

		drawModalRectWithCustomSizedTexture(x + width/2 - imageSize/2, y, 0, 0, imageSize, imageSize, imageSize, imageSize);
		
	}
	

	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if(hovered) {
			if(isFull) { 
				isFull = false;
			}else {
				isFull = true;
			}
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
	
	
}
