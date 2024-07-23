package treemek.mesky.handlers.gui.elements.sliders;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Utils;

public class SettingSlider extends GuiButton{

	String sliderText;
	private Setting setting;
	private boolean clicked;
	private double sliderPrecision;
	public double max;
	public double min;
	
	public SettingSlider(int buttonId, int width, int height, String sliderText, Setting setting, double sliderPrecision, double min, double max) {
		super(buttonId, 0, 0, width, height, sliderText);
		this.sliderText = sliderText;
		this.setting = setting;
		this.sliderPrecision = sliderPrecision;
		this.min = min;
		this.max = max;
	}
	
	ResourceLocation slider = new ResourceLocation(Reference.MODID, "gui/scrollbar.png");
	
	public double getValue() {
		setting.number = Math.min(max, Math.max(min, setting.number));
		return setting.number;
	}
	
	public void setValue(double value) {
		setting.number = Math.min(max, Math.max(min, value));
	}

	
	// slider but function is called for button because there will be no need to change it in settings (since slider have extends GuiButton)
	@Override
	public void drawButton(Minecraft mc, int x, int y) {
		xPosition = x;
		yPosition = y;
		
		drawRect(xPosition, yPosition, xPosition + width, yPosition + height, new Color(8, 7, 10, 150).getRGB()); // bg
		
		mc.renderEngine.bindTexture(slider);
		int sliderWidth = Math.max(1, width/10);
		float sliderPercent = (float) ((setting.number-min)/(max-min));
		int sliderPosition = (int) (xPosition + (sliderPercent * (width - sliderWidth)));
		drawModalRectWithCustomSizedTexture(sliderPosition, yPosition, 0, 0, sliderWidth, height, sliderWidth, height);
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		
		float textY = yPosition + ((height / 2) - ((defaultFontHeight * scaleFactor) / 2));
		double roundedCurrent = Math.round(setting.number * 100.0) / 100.0;
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
		ConfigHandler.saveSettings();
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
