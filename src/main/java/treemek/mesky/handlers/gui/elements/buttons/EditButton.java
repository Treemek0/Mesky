package treemek.mesky.handlers.gui.elements.buttons;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class EditButton extends GuiButton{
	
	public EditButton(int buttonId, int x, int y, int width, int height, String buttonText){
		super(buttonId, x, y, width, height, buttonText);
	}
	
	ResourceLocation delete = new ResourceLocation(Reference.MODID, "gui/edit.png");
	ResourceLocation delete_hovered = new ResourceLocation(Reference.MODID, "gui/edit_hovered.png");
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		
		 this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		 if(this.enabled) {
	         if(this.hovered) {
	        	 mc.renderEngine.bindTexture(delete_hovered);
	     		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
	         }else {
	        	 mc.renderEngine.bindTexture(delete);
	        	 zLevel = 1;
	     		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
	         }
		 }else {
			 GL11.glPushMatrix();
			 GL11.glColor3f(0.4f, 0.4f, 0.4f);
			 mc.renderEngine.bindTexture(delete);
     		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
     		GL11.glPopMatrix();
		 }
	}
	
	
}
