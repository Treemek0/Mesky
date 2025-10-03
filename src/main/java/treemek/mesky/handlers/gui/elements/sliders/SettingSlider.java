package treemek.mesky.handlers.gui.elements.sliders;

import java.awt.Color;
import java.awt.MouseInfo;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.GuiButtonRunnable;
import treemek.mesky.handlers.gui.elements.textFields.SettingTextField;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.Utils;

public class SettingSlider extends GuiButtonRunnable{

	String sliderText;
	TextField textField;
	private Setting setting;
	private boolean clicked;
	private double sliderPrecision;
	public double max;
	public double min;
	int color = 0x3e91b5;
	public int allHeight = 0;
	
	public SettingSlider(int buttonId, int width, int height, String sliderText, Setting setting, double sliderPrecision, double min, double max) {
		super(buttonId, 0, 0, width, height, sliderText);
		this.sliderText = sliderText;
		this.setting = setting;
		
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		this.textField = new TextField(0, 0, 0, resolution.getScaledHeight() / 10, height - height/4);
		textField.setText(setting.number + "");
		
		this.sliderPrecision = sliderPrecision;
		this.min = min;
		this.max = max;
	}
	
	public SettingSlider(int buttonId, int width, int height, String sliderText, Setting setting, double sliderPrecision, double min, double max, Runnable runnable) {
		super(buttonId, 0, 0, width, height, sliderText);
		this.sliderText = sliderText;
		this.setting = setting;
		
		set_runnable(runnable);
		
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		this.textField = new TextField(0, 0, 0, resolution.getScaledHeight() / 10, height - height/4);
		textField.setText(setting.number + "");
		
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
		value =  Math.round(value  / sliderPrecision) * sliderPrecision;
		
		int decimalPlaces = (int) Math.abs(Math.log10(sliderPrecision));
		double roundedCurrent = Math.round(value * Math.pow(10, decimalPlaces+1)) / Math.pow(10, decimalPlaces+1);
		setting.number = Math.min(max, Math.max(min, roundedCurrent));
		textField.setText(setting.number + "");
		
		run_runnable();
	}

	
	// slider but function is called for button because there will be no need to change it in settings (since slider have extends GuiButton)
	@Override
	public void drawButton(Minecraft mc, int x, int y) {
		xPosition = x;
		yPosition = y;
		
		int sliderHeight = height - height / 3; // This is the scaled height (0.75 * height)
		int yOffset = (height - sliderHeight) / 2; // This calculates the difference and centers it
		
		drawRect(xPosition, yPosition + yOffset, xPosition + width, yPosition + yOffset + sliderHeight, new Color(8, 7, 10, 150).getRGB()); // bg
		
		ScaledResolution resolution = new ScaledResolution(mc);
		
		mc.renderEngine.bindTexture(slider);
		int sliderWidth = Math.min(height/5, width/10);
		float sliderPercent = (float) ((setting.number-min)/(max-min));
		int sliderPosition = (int) (xPosition + (sliderPercent * (width - sliderWidth)));
		drawModalRectWithCustomSizedTexture(sliderPosition, yPosition + yOffset, 0, 0, sliderWidth, sliderHeight, sliderWidth, sliderHeight);
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		
		float textY = (float) (yPosition + (height / 2) - RenderHandler.getTextHeight(scaleFactor)/2);
		
		allHeight = height;
		
		String text = RenderHandler.trimWordsToWidth(sliderText, (int) (resolution.getScaledWidth()*0.9f - x - (width*1.30) - (resolution.getScaledHeight() / 10) - 10), false, scaleFactor);
		
		if(text.equals(sliderText)) {
			RenderHandler.drawText(sliderText, x + (width*1.25), textY, scaleFactor, true, color);
		}else {
			String oldText = sliderText;
			int newHeight = allHeight;
			while (!text.equals(oldText)) {
				RenderHandler.drawText(text, x + (width*1.25), textY, scaleFactor, true, color);
				oldText = oldText.substring(text.length());
				text = RenderHandler.trimWordsToWidth(oldText, (int) (resolution.getScaledWidth()*0.9f - x - (width*1.30) - (resolution.getScaledHeight() / 10) - 10), false, scaleFactor);
				
				if(text.split(" ").length == 1 || text.length() == 0) {
					String newText = RenderHandler.trimStringToWidth(oldText, (int) (resolution.getScaledWidth()*0.9f - x - (width*1.30) - (resolution.getScaledHeight() / 10) - 10), false, scaleFactor);
					
					if(newText.length() > 0) {
						text = newText;
					}else {
						break;
					}
				}
				
				textY += height + 1;
				newHeight += height + 1;
			}

			RenderHandler.drawText(text, x + (width*1.25), textY, scaleFactor, true, color);
			
			allHeight = newHeight;
		}
		
		int textWidth = (int) RenderHandler.getTextWidth(text, scaleFactor);
		textField.xPosition = (int) (xPosition + (width*1.25) + (textWidth) + 5);
		textField.yPosition = (int) (textY - (((height - height/4) / 2) - ((defaultFontHeight * scaleFactor) / 2)));
		textField.drawTextBox();
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		textField.mouseClicked(mouseX, mouseY, mouseButton);
		
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height) {
			clicked = true;
			int mX = Mouse.getX();
		    ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		    float guiX = (float) mX * scaledResolution.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
			
			float percent = (float)(guiX - xPosition)/width;
			double rawValue = Utils.getPrecentAverage((float)min, (float)max, percent);
			double adjustedValue = Math.round(rawValue  / sliderPrecision) * sliderPrecision;
			
			setValue(adjustedValue);
		}else {
			if(!textField.isFocused()) { // clicked somewhere else
				setValue(Double.parseDouble(textField.getText())); // correcting the number
			}
		}
		
		return super.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY)
    {
		clicked = false;
		ConfigHandler.saveSettings();
    }
	
	@Override
	public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
		if(clicked) {
			int mX = Mouse.getX();
		    ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		    float guiX = (float) mX * scaledResolution.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
			
			float percent = (float)(guiX - xPosition)/width;
			double rawValue = Utils.getPrecentAverage((float)min, (float)max, percent);
			double adjustedValue = Math.round(rawValue  / sliderPrecision) * sliderPrecision;
			
			setValue(adjustedValue);
		}
	}
	
	public void keyTyped(char typedChar, int keyCode){
		if(keyCode == Keyboard.KEY_RETURN) {
			setValue(Double.parseDouble(textField.getText()));
		}
		
		if(!isDigit(typedChar, keyCode)) return;
		
		textField.textboxKeyTyped(typedChar, keyCode);
	}
	
	private boolean isDigit(char typedChar, int keyCode) {
		if(Character.isDigit(typedChar)) {
			return true;
		}else {
			if(keyCode == 14 || keyCode == 203 || keyCode == 205 || keyCode == 211) {
				return true;
			}else if((keyCode == Keyboard.KEY_A || keyCode == Keyboard.KEY_C || keyCode == Keyboard.KEY_V) && isCtrlKeyDown()){
				return true;
			}else if(keyCode == Keyboard.KEY_PERIOD && !textField.getText().contains(".")){
				return true;
			}else {
				return false;
			}
		}
	}
	
	public static boolean isCtrlKeyDown(){
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }
}
