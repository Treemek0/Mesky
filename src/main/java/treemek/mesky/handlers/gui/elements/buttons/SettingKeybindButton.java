package treemek.mesky.handlers.gui.elements.buttons;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.GuiButtonRunnable;
import treemek.mesky.utils.Keybind;
import treemek.mesky.utils.Utils;

public class SettingKeybindButton extends GuiButtonRunnable{

	boolean isChoosing = false;
	
	Setting setting;
	
	public int allHeight = 0;
	
	int maxKeys = 0;
	
	List<Integer> lastKeys = new ArrayList<>();
	long lastKeysTime = 0;
	String lastKeyString;
	
	public SettingKeybindButton(int buttonId, int heightIn, String text, Setting setting, int maxKeys) {
		super(buttonId, 0, 0, heightIn*4, heightIn, text);
		this.setting = setting;
		
		this.maxKeys = maxKeys;
	}

	public Keybind getKeybind(){
		return setting.keybind;
	}
	
	@Override
	public void drawButton(Minecraft mc, int x, int y) {
		xPosition = x;
		yPosition = y;
		
		allHeight = height;
		
		float brightness = 1;
		float r = 0;
		
		int mouseX = Mouse.getX() * mc.currentScreen.width / mc.displayWidth;
		int mouseY = mc.currentScreen.height - Mouse.getY() * mc.currentScreen.height / mc.displayHeight - 1;
		if(isHovered(mouseX, mouseY)) {
			brightness = 0.65f;
		}
		
		if(isChoosing) {
			updatePressedKeys();
			
			brightness -= 0.4f;
			r = 0.3f;
		}
		
		GL11.glColor3f(brightness + r, brightness, brightness);
		drawButtonForegroundLayer(xPosition, yPosition);
		GL11.glColor3f(1, 1, 1);
		
		String keyString = (lastKeys.isEmpty()) ? setting.keybind.getKeysAsString() : lastKeyString;
		
		double textScale = RenderHandler.getTextScale(height/2);
		double textWidth = RenderHandler.getTextWidth(keyString, textScale);
		if(textWidth > width - 8) {
			textScale = RenderHandler.getTextScale(keyString, width - 8);
			textWidth = RenderHandler.getTextWidth(keyString, textScale);
		}
		
		double textHeight = RenderHandler.getTextHeight(textScale);
		
		RenderHandler.drawText(keyString, xPosition + width/2 - textWidth/2, yPosition + height/2 - textHeight/2, textScale, true, 0xFFFFFF);
		
		double displayScale = RenderHandler.getTextScale(height/2);
		double displayHeight = RenderHandler.getTextHeight(displayScale);
		RenderHandler.drawText(displayString, x + width*1.2, y + height/2 - displayHeight/2, displayScale, true, 0x3e91b5);
	}
	
	private boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean isPressed = super.mousePressed(mc, mouseX, mouseY);
		
		if(isPressed) {
			if(!isChoosing) {
				setting.keybind.clearKeys();
				lastKeys.clear();
				isChoosing = true;
			}else {
				isChoosing = false;
				lastKeys.clear();
			}
		}
		
		return isPressed;
	}
	
	public void updatePressedKeys() {
		for (int key = 0; key < Keyboard.KEYBOARD_SIZE; key++) {
			if(key == Keyboard.KEY_ESCAPE) continue;
			
		    if (Keyboard.isKeyDown(key)) {
		    	if(setting.keybind.getSize() < maxKeys) {
		    		if(setting.keybind.containsKey(key)) continue;
		    		
		    		setting.keybind.addKey(key);
		    		
		    		lastKeys.clear();
		    	}
		    }else {
		    	if(setting.keybind.containsKey(key)) {
		    		if(lastKeys.isEmpty() || isOverTime()) {
		    			lastKeys = setting.keybind.getKeys();
		    			lastKeysTime = System.currentTimeMillis();
		    			lastKeyString = setting.keybind.getKeysAsString();
		    		}
		    		
		    		setting.keybind.removeKey(key);
		    	}
		    }
		}
		
		if(setting.keybind.isEmpty()) {
			if(!isOverTime() && !lastKeys.isEmpty()) {
				isChoosing = false;
				
				setting.keybind.setKeys(lastKeys);
				lastKeys.clear();
			}else {
				lastKeys.clear();
			}
		}else {
			if(isOverTime()) {
				lastKeys.clear();
			}
		}
	}
	
	private boolean isOverTime() {
		return System.currentTimeMillis() - lastKeysTime > 200;
	}
	
	@Override
	public void drawButtonForegroundLayer(int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(buttonTextures);
		
		this.drawTexturedModalRect(x, y, 0, 46 + 20, this.width / 2, this.height);
        this.drawTexturedModalRect(x + this.width / 2, y, 200 - this.width / 2, 46 + 20, this.width / 2, this.height);
	}
}
