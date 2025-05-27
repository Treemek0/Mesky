package treemek.mesky.handlers.gui.elements.sliders;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Utils;

public class Slider extends GuiButton{

	String sliderText;
	public double min;
	public double max;
	public double current;
	private double sliderPrecision;
	
	public Slider(int buttonId, int x, int y, int width, int height, String sliderText, double min, double max, double precision) {
		super(buttonId, x, y, width, height, sliderText);
		this.sliderText = sliderText;
		this.min = min;
		this.max = max;
		this.sliderPrecision = precision;
	}
	
	ResourceLocation slider = new ResourceLocation(Reference.MODID, "gui/sliderHand.png");
	private boolean clicked;
	
	public double getValue() {
		current = Math.min(max, Math.max(min, current));
		return current;
	}
	
	public void setValue(double value) {
		current = Math.min(max, Math.max(min, value));
	}

	
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		drawRect(xPosition, yPosition, xPosition + width, yPosition + height, new Color(8, 7, 10, 150).getRGB()); // background
		
		mc.renderEngine.bindTexture(slider);
		int sliderWidth = Math.min(height/3, width/10);
		float sliderPercent = (float) ((current-min)/(max-min));
		int sliderPosition = (int) (xPosition + (sliderPercent * (width - sliderWidth)));
		GL11.glColor3f(1, 1, 1);
		drawModalRectWithCustomSizedTexture(sliderPosition, yPosition, 0, 0, sliderWidth, height, sliderWidth, height); // slider hand
		
		double roundedCurrent = Math.round(current * 100.0) / 100.0;
		
		double scaleFactor = Math.min(RenderHandler.getTextScale(height/2), RenderHandler.getTextScale(sliderText + " (" + roundedCurrent + ")", width/2.4 - 2));
		

		int textWidth = (int) (mc.fontRendererObj.getStringWidth(sliderText + " (" + roundedCurrent + ")") * scaleFactor);
		if(textWidth < width/2) {
			double textY = yPosition + ((height / 2) - (RenderHandler.getTextHeight(scaleFactor) / 2));
			
			int textX =  sliderPosition - textWidth - 2;
			if(sliderPercent < 0.5) {
				textX = sliderPosition + sliderWidth + 2;
			}
			
			RenderHandler.drawText(sliderText + " (" + roundedCurrent + ")", textX, textY, scaleFactor, true, 0x3e91b5);
		}else {
			double textY = yPosition - scaleFactor;
			int textX = xPosition;
			
			RenderHandler.drawText(sliderText + " (" + roundedCurrent + ")", textX, textY, scaleFactor, true, 0x3e91b5);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height) {
			clicked = true;
			float percent = (float)(mouseX - xPosition)/width;
			double rawValue = Utils.getPrecentAverage((float)min, (float)max, percent);
			double adjustedValue = Math.round(rawValue  / sliderPrecision) * sliderPrecision;
			
			setValue(adjustedValue);
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY)
    {
		clicked = false;
    }
	
	public void mouseClickMoved(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(clicked) {
			float percent = (float)(mouseX - xPosition)/width;
			double rawValue = Utils.getPrecentAverage((float)min, (float)max, percent);
			double adjustedValue = Math.round(rawValue  / sliderPrecision) * sliderPrecision;
			
			setValue(adjustedValue);
		}
	}
	
}
