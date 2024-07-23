package treemek.mesky.handlers.gui.elements.sliders;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class Slider extends GuiButton{

	String buttonText;
	public double min;
	public double max;
	public double current;
	
	public Slider(int buttonId, int x, int y, int width, int height, String buttonText, double min, double max) {
		super(buttonId, x, y, width, height, buttonText);
		this.buttonText = buttonText;
		this.min = min;
		this.max = max;
	}
	
	ResourceLocation slider = new ResourceLocation(Reference.MODID, "gui/slider.png");
	
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
		int sliderPosition = (int) (xPosition + (current/max * (width - sliderWidth)));
		drawModalRectWithCustomSizedTexture(sliderPosition, yPosition, 0, 0, sliderWidth, height, sliderWidth, height);
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		
		float textY = yPosition + ((height / 2) - ((defaultFontHeight * scaleFactor) / 2));
		RenderHandler.drawText(buttonText, xPosition + (width*1.25), textY, scaleFactor, true, 0x3e91b5);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height) {
			int percent = (mouseX - xPosition)/width;
			current = Math.min(max, Math.max(min,max * percent));
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
	
}
