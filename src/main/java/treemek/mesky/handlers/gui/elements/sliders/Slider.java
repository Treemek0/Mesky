package treemek.mesky.handlers.gui.elements.sliders;

import java.awt.Color;

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
	
	ResourceLocation slider = new ResourceLocation(Reference.MODID, "gui/scrollbar.png");
	private boolean clicked;
	
	public double getValue() {
		current = Math.min(max, Math.max(min, current));
		return current;
	}
	
	public void setValue(double value) {
		current = Math.min(max, Math.max(min, value));
	}

	public void drawSlider(Minecraft mc, int mouseX, int mouseY) {
		drawRect(xPosition, yPosition, xPosition + width, yPosition + height, new Color(8, 7, 10, 150).getRGB()); // bg
		
		mc.renderEngine.bindTexture(slider);
		int sliderWidth = Math.max(1, width/10);
		float sliderPercent = (float) ((current-min)/(max-min));
		int sliderPosition = (int) (xPosition + (sliderPercent * (width - sliderWidth)));
		drawModalRectWithCustomSizedTexture(sliderPosition, yPosition, 0, 0, sliderWidth, height, sliderWidth, height);
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		
		float textY = yPosition + ((height / 2) - ((defaultFontHeight * scaleFactor) / 2));
		double roundedCurrent = Math.round(current * 100.0) / 100.0;
		RenderHandler.drawText(sliderText + " (" + roundedCurrent + ")", xPosition + (width*1.25), textY, scaleFactor, true, 0x3e91b5);
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
