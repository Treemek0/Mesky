package treemek.mesky.handlers.gui.elements.buttons;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.textFields.SettingTextField;

public class FoldableSettingButton extends GuiButton{

	String buttonText;
	Setting setting;
	public int foldHeight;
	public List<Object> hiddenObjects;
	
	public FoldableSettingButton(int buttonId, int width, int height, String buttonText, Setting ghostPickaxe, List<Object> hiddenObjects) {
		super(buttonId, 0, 0, width, height, buttonText);
		this.buttonText = buttonText;
		this.hiddenObjects = hiddenObjects;
		this.setting = ghostPickaxe;
		
		for (Object guiButton : hiddenObjects) {
			if(guiButton instanceof GuiButton) {
				((GuiButton)guiButton).height *= 0.9;
				((GuiButton)guiButton).width *= 0.9;
			}
			
			if(guiButton instanceof GuiTextField) {
				((GuiTextField)guiButton).height *= 0.9;
				((GuiTextField)guiButton).width *= 0.9;
			}
		}
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
		
		foldHeight = height;
		
		if(isFull()) {
			for (int i = 0; i < hiddenObjects.size(); i++) {
				if(hiddenObjects.get(i) instanceof GuiButton) {
					GuiButton button = (GuiButton) hiddenObjects.get(i);
					button.drawButton(mc, xPosition + 5, yPosition + foldHeight + 2);
					foldHeight += button.height + 2;
				}
				if(hiddenObjects.get(i) instanceof GuiTextField) {
					SettingTextField input = (SettingTextField) hiddenObjects.get(i);
					input.drawTextField(xPosition + 5, yPosition + foldHeight + 2);
					foldHeight += input.height + 5;
				}
			}
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
