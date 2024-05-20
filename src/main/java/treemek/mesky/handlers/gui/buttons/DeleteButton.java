package treemek.mesky.handlers.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class DeleteButton extends GuiButton{

	int x;
	int y;
	
	
	public DeleteButton(int buttonId, int x, int y, int width, int height, String buttonText){
		super(buttonId, x, y, width, height, buttonText);
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y = y;
	}
	
	ResourceLocation delete = new ResourceLocation(Reference.MODID, "gui/delete.png");
	ResourceLocation delete_hovered = new ResourceLocation(Reference.MODID, "gui/delete_hovered.png");
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		
		 this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         if(this.hovered) {
        	 mc.renderEngine.bindTexture(delete_hovered);
     		drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
         }else {
        	 mc.renderEngine.bindTexture(delete);
        	 zLevel = 1;
     		drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
     		
         }
	}
	
	
}
