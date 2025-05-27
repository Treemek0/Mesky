package treemek.mesky.handlers.gui.elements.buttons;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class PlayButton extends GuiButton{
	
	public PlayButton(int buttonId, int x, int y, int size){
		super(buttonId, x, y, size, size, "");
	}
	
	ResourceLocation play = new ResourceLocation(Reference.MODID, "gui/play.png");
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		
		 this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         if(enabled && this.hovered) {
        	 GL11.glColor3f(0.4f, 0.4f, 0.4f);
        	 mc.renderEngine.bindTexture(play);
     		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
     		GL11.glColor3f(1, 1, 1);
         }else {
        	 mc.renderEngine.bindTexture(play);
        	 zLevel = 1;
     		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
         }
	}
	
	
}
