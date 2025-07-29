package treemek.mesky.handlers.gui.elements.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class MeskyButton extends GuiButton{

	int x;
	int y;
	String buttonText;
	
	
	public MeskyButton(int buttonId, int x, int y, int width, int height, String buttonText) {
		super(buttonId, x, y, width, height, buttonText);
		this.x = x;
		this.y = y;
		this.buttonText = buttonText;
	}
	
	
	ResourceLocation empty = new ResourceLocation(Reference.MODID, "gui/check-empty.png");
	ResourceLocation check = new ResourceLocation(Reference.MODID, "gui/check.png");
	
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;
            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }

            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 0x3e91b5;
            }

            double scale = Math.min(RenderHandler.getTextScale(buttonText, width*0.75f), RenderHandler.getTextScale(height/2.22f));
            
            RenderHandler.drawText(buttonText, xPosition + width/2 - RenderHandler.getTextWidth(buttonText, scale)/2, yPosition + height/2 - RenderHandler.getTextHeight(scale)/2, scale, true, j);
        }
	}
	


	
}
