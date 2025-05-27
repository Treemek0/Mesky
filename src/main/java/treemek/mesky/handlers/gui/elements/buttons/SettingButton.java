package treemek.mesky.handlers.gui.elements.buttons;

import net.minecraft.client.Minecraft;
import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Utils;

public class SettingButton extends GuiButton{

	String buttonText;
	public Setting setting;
	int color = 0x3e91b5;
	public int allHeight = 0;
	boolean illegal = false;
	
	public SettingButton(int buttonId, int height, String buttonText, Setting setting) {
		super(buttonId, 0, 0, height*2, height, buttonText);
		this.buttonText = buttonText;
		this.setting = setting;
	}
	
	public SettingButton(int buttonId, int height, String buttonText, Setting setting, boolean illegal) {
		super(buttonId, 0, 0, height*2, height, buttonText);
		this.buttonText = buttonText;
		this.setting = setting;
		this.illegal = illegal;
	}
	
	public SettingButton(int buttonId, int height, String buttonText, Setting setting, int color) {
		super(buttonId, 0, 0, height*2, height, buttonText);
		this.buttonText = buttonText;
		this.setting = setting;
		this.color = color;
	}
	
	public SettingButton(int buttonId, int height, int x, int y, String buttonText, Setting setting) {
		super(buttonId, x, y, height*2, height, buttonText);
		this.buttonText = buttonText;
		this.setting = setting;
	}
	
	ResourceLocation off = new ResourceLocation(Reference.MODID, "gui/off-switch.png");
	ResourceLocation on = new ResourceLocation(Reference.MODID, "gui/on-switch.png");
	
	ResourceLocation warning = new ResourceLocation(Reference.MODID, "gui/warning.png");
	
	public boolean isFull() {
		return setting.isOn;
	}
	
	public void changeFull() {
         setting.isOn = !setting.isOn;
         ConfigHandler.saveSettings();
	}
	
	@Override
	public void drawButton(Minecraft mc, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		
		ScaledResolution resolution = new ScaledResolution(mc);
		
		if(isFull()) {
			mc.renderEngine.bindTexture(on);
		}else {
			mc.renderEngine.bindTexture(off);
		}
		
		GL11.glColor3f(1, 1, 1);
		drawModalRectWithCustomSizedTexture(xPosition, yPosition + height/4, 0, 0, width/2, height/2, width/2, height/2);
		
		if(illegal) {
			mc.renderEngine.bindTexture(warning);
			drawModalRectWithCustomSizedTexture((int) (xPosition - height*0.75f), yPosition + height/4, 0, 0, height/2, height/2, height/2, height/2);
		}
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		
		
		float textY = y + ((height / 2) - ((defaultFontHeight * scaleFactor) / 2));
		
		allHeight = height;
		
		String text = RenderHandler.trimWordsToWidth(buttonText, (int) (resolution.getScaledWidth()*0.9f - x - (width*1.30) - 10), false, scaleFactor);
		
		if(text.equals(buttonText)) {
			RenderHandler.drawText(buttonText, x + width/2 + 5, textY, scaleFactor, true, color);
		}else {
			String oldText = buttonText;
			int newHeight = allHeight;
			while (!text.equals(oldText)) {
				RenderHandler.drawText(text, x + width/2 + 5, textY, scaleFactor, true, color);
				oldText = oldText.substring(text.length());
				text = RenderHandler.trimWordsToWidth(oldText, (int) (resolution.getScaledWidth()*0.9f - x - (width*1.30) - 10), false, scaleFactor);
				
				if(text.split(" ").length == 1 || text.length() == 0) {
					String newText = RenderHandler.trimStringToWidth(oldText, (int) (resolution.getScaledWidth()*0.9f - x - (width*1.30) - 10), false, scaleFactor);
					
					if(newText.length() > 0) {
						text = newText;
					}else {
						break;
					}
				}
				
				textY += height + 1;
				newHeight += height + 1;
			}

			RenderHandler.drawText(text, x + width/2 + 5, textY, scaleFactor, true, color);
			
			allHeight = newHeight;
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (mouseX >= this.xPosition && mouseX <= this.xPosition + width && mouseY >= this.yPosition && mouseY <= this.yPosition + height) {
			changeFull();
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
}
