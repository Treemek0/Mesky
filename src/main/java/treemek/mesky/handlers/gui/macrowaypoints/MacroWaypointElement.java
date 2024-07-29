package treemek.mesky.handlers.gui.macrowaypoints;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import treemek.mesky.handlers.gui.elements.ColorPicker;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.MacroButton;

public class MacroWaypointElement {
	GuiTextField name;
	ColorPicker color;
	GuiTextField x;
	GuiTextField y;
	GuiTextField z;
	GuiTextField yaw;
	GuiTextField pitch;
	MacroButton left;
	MacroButton right; 
	MacroButton back; 
	MacroButton forward; 
	MacroButton leftClick; 
	MacroButton rightClick;
	DeleteButton deleteButton;
	GuiTextField noiseLevel;
	GuiTextField function;
	public int yPosition;
	public int xPosition;
	public Integer elementHeight = null;
	public Integer elementWidth = null;
	
	public MacroWaypointElement(GuiTextField name, ColorPicker color, GuiTextField x, GuiTextField y, GuiTextField z, GuiTextField yaw, GuiTextField pitch, MacroButton left, MacroButton right, MacroButton back, MacroButton forward, MacroButton leftClick, MacroButton rightClick, GuiTextField noiseLevel, GuiTextField function, DeleteButton deleteButton) {
		this.name = name;
		this.color = color;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.right = right;
		this.left = left;
		this.back = back;
		this.forward = forward;
		this.leftClick = leftClick;
		this.rightClick = rightClick;
		this.noiseLevel = noiseLevel;
		this.function = function;
		this.deleteButton = deleteButton;
	}
	
	public void updateYposition(int y, int inputHeight) {
		this.yPosition = y;
		this.name.yPosition = y;
		this.color.yPosition = y;
		this.x.yPosition = y;
		this.y.yPosition = y;
		this.z.yPosition = y;
		this.yaw.yPosition = y;
		this.pitch.yPosition = y;
		this.right.yPosition = y + inputHeight +5;
		this.left.yPosition = y + inputHeight+5;
		this.back.yPosition = y + inputHeight+5;
		this.forward.yPosition = y + inputHeight+5;
		this.leftClick.yPosition = y + inputHeight+5;
		this.rightClick.yPosition = y + inputHeight+5;
		this.function.yPosition = y + inputHeight +5;
		this.noiseLevel.yPosition = y;
		this.deleteButton.yPosition = y;
	}
	
	public List<GuiTextField> getListOfTextFields() {
		List<GuiTextField> inputs = new ArrayList<>();
		inputs.add(name);
		inputs.add(x);
		inputs.add(y);
		inputs.add(z);
		inputs.add(yaw);
		inputs.add(pitch);
		inputs.add(noiseLevel);
		inputs.add(function);
		return inputs;
	}
	
	public List<GuiButton> getListOfButtons(){
		List<GuiButton> buttons = new ArrayList<>();
		buttons.add(deleteButton);
		buttons.add(left);
		buttons.add(right);
		buttons.add(back);
		buttons.add(forward);
		buttons.add(leftClick);
		buttons.add(rightClick);
		return buttons;
	}
	
	public int getWidth() {
		if(elementWidth == null) {
			int lowestX = 10000000;
			int highestX = 0;
			int highestX_width = 0;
			
			if(color.xPosition < lowestX) lowestX = color.xPosition;
			if(color.xPosition > highestX) {
				highestX = color.xPosition;
				highestX_width = color.width;
			}
			
			List<GuiButton> buttons = getListOfButtons();
			for (int i = 0; i < buttons.size(); i++) {
				if(buttons.get(i).xPosition < lowestX) lowestX = buttons.get(i).xPosition;
				if(buttons.get(i).xPosition > highestX) {
					highestX = buttons.get(i).xPosition;
					highestX_width = buttons.get(i).width;
				}
			}
			
			List<GuiTextField> inputs = getListOfTextFields();
			for (int i = 0; i < inputs.size(); i++) {
				if(inputs.get(i).xPosition < lowestX) lowestX = inputs.get(i).xPosition;
				if(inputs.get(i).xPosition > highestX) {
					highestX = inputs.get(i).xPosition;
					highestX_width = inputs.get(i).width;
				}
			}
			
			int width = (highestX - lowestX) + highestX_width;
			
			xPosition = lowestX;
			elementWidth = width;
			return width;
		}else {
			return elementWidth;
		}
	}
	
	public int getHeight() {
		if(elementHeight == null) {
			int lowestY = 10000000;
			int highestY = 0;
			int highestY_height = 0;
			
//			if(color.yPosition < lowestY) lowestY = color.yPosition;
//			if(color.yPosition > highestY) {
//				highestY = color.yPosition;
//				highestY_height = color.height;
//			}
			
			List<GuiButton> buttons = getListOfButtons();
			for (int i = 0; i < buttons.size(); i++) {
				if(buttons.get(i).yPosition < lowestY) lowestY = buttons.get(i).yPosition;
				if(buttons.get(i).yPosition > highestY) {
					highestY = buttons.get(i).yPosition;
					highestY_height = buttons.get(i).height;
				}
			}
			
			List<GuiTextField> inputs = getListOfTextFields();
			for (int i = 0; i < inputs.size(); i++) {
				if(inputs.get(i).yPosition < lowestY) lowestY = inputs.get(i).yPosition;
				if(inputs.get(i).yPosition > highestY) {
					highestY = inputs.get(i).yPosition;
					highestY_height = inputs.get(i).height;
				}
			}
			
			int height = (highestY - lowestY) + highestY_height;

			elementHeight = height;
			return height;
		}else {
			return elementHeight;
		}
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		getWidth();
		getHeight();
		return (mouseX >= xPosition && mouseX <= xPosition + elementWidth && mouseY >= yPosition && mouseY <= yPosition + elementHeight);
	}
}
