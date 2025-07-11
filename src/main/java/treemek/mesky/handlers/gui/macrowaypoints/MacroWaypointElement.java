package treemek.mesky.handlers.gui.macrowaypoints;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
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
	public double yPositionD;
	private Integer elementWidth;
	
	public MacroWaypointElement(TextField name, ColorPicker color, TextField x, TextField y, TextField z, TextField yaw, TextField pitch, MacroButton left, MacroButton right, MacroButton back, MacroButton forward, MacroButton leftClick, MacroButton rightClick, MacroButton sneak, TextField noiseLevel, TextField function, DeleteButton deleteButton, CheckButton enabled, double inputMargin) {
		this.name = name;
		this.yPosition = name.yPosition;
		this.yPositionD = yPosition;
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
	
	public void updateYposition(double yD, int inputHeight) {
		this.yPositionD = yD;
		this.yPosition = (int) yD;
		this.name.yPosition = (int) yD;
		this.color.yPosition = (int) yD;
		this.x.yPosition = (int) yD;
		this.y.yPosition = (int) yD;
		this.z.yPosition = (int) yD;
		this.yaw.yPosition = (int) yD;
		this.pitch.yPosition = (int) yD;
		this.enabled.yPosition = (int) yD;
		this.right.yPosition = (int) yD + inputHeight +5;
		this.left.yPosition = (int) yD + inputHeight+5;
		this.back.yPosition = (int) yD + inputHeight+5;
		this.forward.yPosition = (int) yD + inputHeight+5;
		this.leftClick.yPosition = (int) yD + inputHeight+5;
		this.rightClick.yPosition = (int) yD + inputHeight+5;
		this.sneak.yPosition = (int) yD + inputHeight+5;
		this.function.yPosition = (int) yD + inputHeight +5;
		this.noiseLevel.yPosition = (int) yD;
		this.deleteButton.yPosition = (int) yD;
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
		if(elementWidth == null) {
			ScaledResolution wind = new ScaledResolution(Minecraft.getMinecraft());
	    	int height = wind.getScaledHeight();
	    	int width = wind.getScaledWidth();
	    	int inputHeight = ((height / 25) < 12)?12:(height / 25);
	    	
			int lowestX = width / 20 - inputHeight;
			int highestX = (int) (width*0.9 + inputHeight*2);
			
			xPosition = lowestX;
			elementWidth = highestX - lowestX;
			return width;
		}else {
			return elementWidth;
		}
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
