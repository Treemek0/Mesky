package treemek.mesky.handlers.gui.elements;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.sliders.ColorBrightnessSlider;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;

public class SettingColorPicker extends ColorPicker{

	private Setting setting;
	private String text;
	
	public SettingColorPicker(int buttonId, int size, String text, Setting setting) {
		super(buttonId, 0, 0, size);
		this.setting = setting;
		this.text = text;

        setText(setting.text);
	}
	
	@Override
	public void setText(String c) {
		super.setText(c);
		
		setting.text = getColorString();
	}
	
	public void drawTextField(int x, int y) {
		xPosition = x;
		yPosition = y;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); 
		RenderHandler.drawCircleWithBorder(xPosition, yPosition, width, ColorUtils.getColorInt(setting.text));
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		
		if(turnOff) {
			isPickerOpened = false;
		}
		
		if(oldYposition != yPosition) {
			isPickerOpened = false;
			
			updatePosition();
			updateElementsPositions(); // its in different function becuase colorTextField is initiazed when updatePosition() is first called (i know that i could just do null test)
		}
		
		float defaultFontHeight = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		
		float textY = y + ((height / 2) - ((defaultFontHeight * scaleFactor) / 2));
		RenderHandler.drawText(text, x + (width*1.25), textY, scaleFactor, true, 0x3e91b5);
		
		oldYposition = yPosition;
	}
	
	public void drawPickerOpened() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		super.drawPickerOpened();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}

	
	public boolean mouseClick(int mouseX, int mouseY, int buttonId) {
		boolean isPressed = mouseX >= xPosition && mouseX < xPosition + width && mouseY >= yPosition && mouseY < yPosition + height;
		
		if(!isPickerOpened) {
			if(isPressed) {
				color = ColorUtils.getColorInt(setting.text);
			}
		}
		
		boolean b = super.mouseClick(mouseX, mouseY, buttonId);
		setting.text = getColorString();
		
		return b;
	}
	
	public void mouseClickMoved(int mouseX, int mouseY) {
		super.mouseClickMoved(mouseX, mouseY, 0, 0);
		
		setting.text = getColorString();
	}
	
	public void keyTyped(char typedChar, int keyCode) {
		try {
			super.keyTyped(typedChar, keyCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setting.text = getColorString();
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height;
	}
}
