package treemek.mesky.handlers.gui.elements.buttons;

import net.minecraft.client.Minecraft;
import java.lang.reflect.Field;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.handlers.RenderHandler;

public class SettingButton extends GuiButton{

	String buttonText;
	Setting setting;
	
	public SettingButton(int buttonId, int width, int height, String buttonText, Setting setting) {
		super(buttonId, 0, 0, width, height, buttonText);
		this.buttonText = buttonText;
		this.setting = setting;
	}
	
	
	ResourceLocation empty = new ResourceLocation(Reference.MODID, "gui/check-empty.png");
	ResourceLocation check = new ResourceLocation(Reference.MODID, "gui/check.png");
	
	public boolean isFull() {
		return setting.isOn;
	}
	
	public void changeFull() {
         setting.isOn = !setting.isOn;
	}
	
	@Override
	public void drawButton(Minecraft mc, int x, int y) {
		this.xPosition = x;
		this.yPosition = y;
		
		if(isFull()) {
			mc.renderEngine.bindTexture(check);
		}else {
			mc.renderEngine.bindTexture(empty);
		}
		
		drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		
		float textY = y + ((height / 2) - ((defaultFontHeight * scaleFactor) / 2));
		RenderHandler.drawText(buttonText, x + (width*1.25), textY, scaleFactor, true, 0x3e91b5);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (mouseX >= this.xPosition && mouseX <= this.xPosition + width && mouseY >= this.yPosition && mouseY <= this.yPosition + height) {
			changeFull();
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
}
