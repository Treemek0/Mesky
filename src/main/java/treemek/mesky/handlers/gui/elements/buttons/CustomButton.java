package treemek.mesky.handlers.gui.elements.buttons;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.ButtonWithToolkit;

public class CustomButton extends ButtonWithToolkit{
	
	public CustomButton(int buttonId, int x, int y, int width, int height, String buttonText, ResourceLocation texture){
		super(buttonId, x, y, width, height, buttonText);
		this.texture = texture;
	}
	
	ResourceLocation texture;
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		
		 this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		 mc.renderEngine.bindTexture(texture);
         if(enabled && this.hovered) GL11.glColor3f(0.7f, 0.7f, 0.7f);
         drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
         GL11.glColor3f(1, 1, 1);
         
         super.drawButton(mc, mouseX, mouseY);
	}
	
	public void update(int x, int y, int width, int height) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = width;
		this.height = height;
	}
}
