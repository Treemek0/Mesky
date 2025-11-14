package treemek.mesky.handlers.gui.elements.buttons;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.GuiButtonRunnable;
import treemek.mesky.utils.Utils;

public class HelpButton extends GuiButtonRunnable{

	String[] helpText;
	double textWidth = 0;
	
	public HelpButton(int buttonId, int x, int y, int widthIn, int heightIn, String[] helpText) {
		super(buttonId, x, y, widthIn, heightIn, null);
		
		setHelpText(helpText);
	}
	
	public void setHelpText(String[] text) {
		this.helpText = text;
		
		double textScale = Math.max(0.66f, Math.min(0.8f, RenderHandler.getTextScale(height / 2)));
		
		double highestWidth = 0;
		for (String string : text) {
			double tWidth = RenderHandler.getTextWidth(string, textScale);
			
			if(tWidth > highestWidth) {
				highestWidth = tWidth;
			}
		}
		
		textWidth = highestWidth;
	}

	ResourceLocation location = new ResourceLocation(Reference.MODID, "gui/help.png");
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		
		if(hovered) {
			GL11.glColor3f(0.7f, 0.7f, 0.7f);
		}
		
		RenderHandler.drawImage(xPosition, yPosition, width, height, location);
		GL11.glColor3f(1, 1, 1);
		super.drawButton(mc, mouseX, mouseY);
	}
	
	public void drawToolkit(int mouseX, int mouseY) {
		double textScale = Math.max(0.66f, Math.min(0.8f, RenderHandler.getTextScale(height / 2)));
        double textHeight = RenderHandler.getTextHeight(textScale);
        int startY = (int) (mouseY - textHeight * 1.5f);
        
        int screenWidth = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();

        int tX = Math.min(mouseX, (int) (screenWidth - (textWidth + 10)));
        
        double lineHeight = textHeight * 1.5f;
        
        drawRect(tX + 2, startY, (int) (tX + textWidth + 10),
                 (int) (startY + lineHeight*helpText.length), new Color(10, 10, 10, 200).getRGB());

        for (int i = 0; i < helpText.length; i++) {
        	RenderHandler.drawText(helpText[i],
                    mouseX + 2 + (textWidth + 10) / 2 - textWidth / 2,
                    startY + lineHeight*i + lineHeight/2 - textHeight/2,
                    textScale, true, 0xFFFFFF);
		}
	}
}
