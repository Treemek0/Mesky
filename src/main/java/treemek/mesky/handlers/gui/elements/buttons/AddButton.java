package treemek.mesky.handlers.gui.elements.buttons;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class AddButton extends GuiButton{
	
	public AddButton(int buttonId, int x, int y, int width, int height, String buttonText){
		super(buttonId, x, y, width, height, buttonText);
	}
	
	ResourceLocation button = new ResourceLocation(Reference.MODID, "gui/add.png");
	ResourceLocation button_hovered = new ResourceLocation(Reference.MODID, "gui/add_hovered.png");
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		
		 this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         if(enabled && this.hovered) {
        	 mc.renderEngine.bindTexture(button_hovered);
     		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
         }else {
        	 mc.renderEngine.bindTexture(button);
        	 zLevel = 1;
     		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
         }
	}
	
	public void update(int x, int y, int width, int height) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = width;
		this.height = height;
	}
}
