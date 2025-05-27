package treemek.mesky.handlers.gui.macrowaypoints;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import treemek.mesky.handlers.gui.elements.ColorPicker;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.MacroButton;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.Utils;

public class MacroWaypointElement {
	TextField name;
	ColorPicker color;
	TextField x;
	TextField y;
	TextField z;
	TextField yaw;
	TextField pitch;
	MacroButton left;
	MacroButton right; 
	MacroButton back; 
	MacroButton forward; 
	MacroButton leftClick; 
	MacroButton rightClick;
	MacroButton sneak;
	DeleteButton deleteButton;
	TextField noiseLevel;
	TextField function;
	public int yPosition;
	public int xPosition;
	private double margin;
	public CheckButton enabled;
	
	public MacroWaypointElement(TextField name, ColorPicker color, TextField x, TextField y, TextField z, TextField yaw, TextField pitch, MacroButton left, MacroButton right, MacroButton back, MacroButton forward, MacroButton leftClick, MacroButton rightClick, MacroButton sneak, TextField noiseLevel, TextField function, DeleteButton deleteButton, CheckButton enabled, double inputMargin) {
		this.name = name;
		this.yPosition = name.yPosition;
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
		this.sneak = sneak;
		this.noiseLevel = noiseLevel;
		this.function = function;
		this.deleteButton = deleteButton;
		this.enabled = enabled;
		this.margin = inputMargin;
		
		this.xPosition = color.xPosition;
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
		this.enabled.yPosition = y;
		this.right.yPosition = y + inputHeight +5;
		this.left.yPosition = y + inputHeight+5;
		this.back.yPosition = y + inputHeight+5;
		this.forward.yPosition = y + inputHeight+5;
		this.leftClick.yPosition = y + inputHeight+5;
		this.rightClick.yPosition = y + inputHeight+5;
		this.sneak.yPosition = y + inputHeight+5;
		this.function.yPosition = y + inputHeight +5;
		this.noiseLevel.yPosition = y;
		this.deleteButton.yPosition = y;
	}
	
	public List<TextField> getListOfTextFields() {
		List<TextField> inputs = new ArrayList<>();
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
		buttons.add(sneak);
		buttons.add(enabled);
		return buttons;
	}
	
	public int getWidth() {
		return (enabled.xPosition + enabled.width) - xPosition;
	}
	
	public int getHeight() {
		return (name.height * 2) + 5;
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		return (mouseX >= xPosition && mouseX <= xPosition + getWidth() && mouseY >= yPosition - margin/2 && mouseY <= yPosition + getHeight() + margin/2);
	}
	
	public boolean isHigherHalfHovered(int mouseX, int mouseY) {
		return (mouseX >= xPosition && mouseX <= xPosition + getWidth() && mouseY >= yPosition - margin/2 && mouseY <= yPosition + getHeight()/2);
	}
	
	public boolean isLowerHalfHovered(int mouseX, int mouseY) {
		return (mouseX >= xPosition && mouseX <= xPosition + getWidth() && mouseY >= yPosition + getHeight()/2 && mouseY <= yPosition + getHeight() + margin/2);
	}
}
