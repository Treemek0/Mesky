package treemek.mesky.handlers.gui.elements.buttons;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.GuiButtonRunnable;
import treemek.mesky.utils.Keybind;
import treemek.mesky.utils.Utils;

public class KeybindButton extends GuiButtonRunnable{

	Keybind keybind;
	boolean isChoosing = false;
	
	List<Integer> lastKeys = new ArrayList<>();
	long lastKeysTime = 0;
	String lastKeyString;
	
	int maxKeys = 0;
	private int text_color = 0xFFFFFF;
	
	public KeybindButton(int buttonId, int x, int y, int widthIn, int heightIn, String text, Keybind keybind, int maxKeys) {
		super(buttonId, x, y, widthIn, heightIn, text);
		this.keybind = keybind;
		this.maxKeys = maxKeys;
	}

	public Keybind getKeybind() {
		return keybind;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		float brightness = 1;
		float r = 0;

		this.hovered = isHovered(mouseX, mouseY);
		
		if(hovered) {
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
		
		String keyString = (lastKeys.isEmpty()) ? keybind.getKeysAsString() : lastKeyString;
		
		double textScale = RenderHandler.getTextScale(height/2);
		double textWidth = RenderHandler.getTextWidth(keyString, textScale);
		if(textWidth > width - 8) {
			textScale = RenderHandler.getTextScale(keyString, width - 8);
			textWidth = RenderHandler.getTextWidth(keyString, textScale);
		}
		
		double textHeight = RenderHandler.getTextHeight(textScale);
		
		RenderHandler.drawText(keyString, xPosition + width/2 - textWidth/2, yPosition + height/2 - textHeight/2, textScale, true, text_color);
		
		super.drawButton(mc, mouseX, mouseY);
	}
	
	private boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
	}

	
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean isPressed = super.mousePressed(mc, mouseX, mouseY);
		
		if(isPressed) {
			if(!isChoosing) {
				keybind.clearKeys();
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
		    	if(keybind.getSize() < maxKeys) {
		    		if(keybind.containsKey(key)) continue;
		    		
		    		keybind.addKey(key);
		    		
		    		lastKeys.clear();
		    	}
		    }else {
		    	if(keybind.containsKey(key)) {
		    		if(lastKeys.isEmpty() || isOverTime()) {
		    			lastKeys = keybind.getKeys();
		    			lastKeysTime = System.currentTimeMillis();
		    			lastKeyString = keybind.getKeysAsString();
		    		}
		    		
		    		keybind.removeKey(key);
		    	}
		    }
		}
		
		if(keybind.isEmpty()) {
			if(!isOverTime() && !lastKeys.isEmpty()) {
				isChoosing = false;
				
				keybind.setKeys(lastKeys);
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
	    Minecraft mc = Minecraft.getMinecraft();
	    mc.getTextureManager().bindTexture(buttonTextures);

	    GlStateManager.pushMatrix();
	    GlStateManager.scale(1.0F, (float)this.height / 20F, 1.0F);

	    int scaledY = (int)(y / ((float)this.height / 20F));
	    this.drawTexturedModalRect(x, scaledY, 0, 46 + 20, this.width / 2, 20);
	    this.drawTexturedModalRect(x + this.width / 2, scaledY, 200 - this.width / 2, 46 + 20, this.width / 2, 20);

	    GlStateManager.popMatrix();
	}

	public void setTextColor(int color) {
		text_color = color;
	}
}
