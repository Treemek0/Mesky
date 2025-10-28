package treemek.mesky.handlers.gui.keyaction;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.KeybindButton;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.Keybind;

public class KeyActionElement {
	int x, y = 0;
	double yD = 0;
	static int width, height = 0;
	KeybindButton keybindBtn;
	TextField command;
	DeleteButton delete;
	
	GuiScreen gui;
	
	public KeyActionElement(int startY, int counter, Keybind keybind, String commandString) {
		gui = Minecraft.getMinecraft().currentScreen;
		
		x = gui.width / 12;
		width = gui.width - x*2;
		
		height = (int) (1.25f * (((gui.height / 25) < 12)?12:(gui.height / 25)));
		this.y = (int) (startY + counter*(height*1.2f));
		yD = y;
		
		int elementsHeight = height - height/3;
		int keybindWidth = Math.min(width/6, 110);
		int elementsY = y + height/2 - elementsHeight/2;
		
		keybindBtn = new KeybindButton(0, getKeybindX(), elementsY, keybindWidth, elementsHeight, "Set keys for keybind", keybind, 3);

		int commandX = getCommandFieldX();
		int commandWidth = x + width - commandX - width / 8;
		command = new TextField(0, commandX, elementsY, commandWidth, elementsHeight);
		command.setText(commandString);
		
		delete = new DeleteButton(0, x + width - height, y + height/4, height/2, height/2, "Delete action");
	}
	

	public void updateData(int startY, int counter) {
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		
		x = gui.width / 12;
		width = gui.width - x*2;
		
		height = (int) (1.25f * (((gui.height / 25) < 12)?12:(gui.height / 25)));
		this.y = (int) (startY + counter*(height*1.2f));
		yD = y;
		
		int elementsHeight = height - height/3;
		int keybindWidth = Math.min(width/6, 110);
		int elementsY = y + height/2 - elementsHeight/2;
		
		keybindBtn.xPosition = getKeybindX();
		keybindBtn.yPosition = elementsY;
		keybindBtn.width = keybindWidth;
		keybindBtn.height = elementsHeight;
		
		int commandX = getCommandFieldX();
		int commandWidth = x + width - commandX - width / 8;
		
		command.update(getCommandFieldX(), elementsY, commandWidth, elementsHeight);
		
		delete.update(x + width - height, y + height/4, height/2, height/2);
	}
	
	public void smoothlyUpdateY(int startY, int counter) {
		int destY = (int) (startY + counter*(height*1.2f));
		
		double k = Math.signum((destY - yD)) * (240f/Minecraft.getDebugFPS());
		if (Math.abs(destY - yD) > height * 2) k *= 4;

		if(Math.abs(k) > Math.abs(yD - destY)) {
			yD = destY;
		}else {
			yD += k;
		}
		
		y = (int) yD;
		int elementsHeight = height - height/3;
		int keybindWidth = Math.min(width/6, 110);
		int elementsY = y + height/2 - elementsHeight/2;
		
		keybindBtn.xPosition = getKeybindX();
		keybindBtn.yPosition = elementsY;
		keybindBtn.width = keybindWidth;
		keybindBtn.height = elementsHeight;
		
		int commandX = getCommandFieldX();
		int commandWidth = x + width - commandX - width / 8;
		
		command.update(getCommandFieldX(), elementsY, commandWidth, elementsHeight);
		
		delete.update(x + width - height, y + height/4, height/2, height/2);
	}
	
	public int getCommandFieldX() {
		int keybindWidth = Math.min(width/6, 110);
		return x + width / 8 + keybindWidth + 10;
	}
	
	public int getKeybindX() {
		return x + width / 8;
	}
	
	public void draw() {
		RenderHandler.drawRectWithFrame(x - 5, y, x + width + 5, y + height, new Color(15, 15, 15,255).getRGB(), 1);
		
		int mouseX = Mouse.getX() * gui.width / Minecraft.getMinecraft().displayWidth;
	    int mouseY = gui.height - Mouse.getY() * gui.height / Minecraft.getMinecraft().displayHeight - 1;
		
		keybindBtn.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
		command.drawTextBox();
		delete.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
	}
	
	public boolean mousePressed(int mouseX, int mouseY) {
		boolean clicked = false;
		
		if(keybindBtn.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
			clicked  = true;
		}
		
		if(command.mouseClicked(mouseX, mouseY, 0)) {
			clicked = true;
		}
			
		
		if(clicked) {
			return true;
		}
		
		return false;
	}
	
	public void keyTyped(char typedChar, int keyCode) {
		int cursor = command.getCursorPosition();
		command.textboxKeyTyped(typedChar, keyCode);
		
		if(!command.getText().startsWith("/")) {
			command.setText("/" + command.getText());
			command.setCursorPosition(cursor);
		}
	}
	
	public void updateScreen() {
		command.updateCursorCounter();
	}
}
