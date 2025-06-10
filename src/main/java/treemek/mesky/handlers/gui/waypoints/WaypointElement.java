package treemek.mesky.handlers.gui.waypoints;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import treemek.mesky.handlers.gui.elements.ColorPicker;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.sliders.Slider;
import treemek.mesky.handlers.gui.elements.textFields.TextField;

public class WaypointElement {
	TextField name;
	ColorPicker color;
	Slider scale;
	TextField x;
	TextField y;
	TextField z;
	DeleteButton deleteButton;
	CheckButton enabled;
	public int yPosition;
	public int xPosition;
	public Integer elementHeight = null;
	public Integer elementWidth = null;
	private double margin;
	
	public WaypointElement(TextField name, ColorPicker color, Slider scale, TextField x, TextField y, TextField z, DeleteButton deleteButton, CheckButton enabled, double inputMargin) {
		this.name = name;
		this.yPosition = name.yPosition;
		this.color = color;
		this.scale = scale;
		this.x = x;
		this.y = y;
		this.z = z;
		this.deleteButton = deleteButton;
		this.margin = inputMargin;
		this.enabled = enabled;
	}
	
	public void updateYposition(int y) {
		this.yPosition = y;
		this.name.yPosition = y;
		this.color.yPosition = y;
		this.scale.yPosition = y;
		this.enabled.yPosition = y;
		this.x.yPosition = y;
		this.y.yPosition = y;
		this.z.yPosition = y;
		this.deleteButton.yPosition = y;
	}
	
	public List<TextField> getListOfTextFields() {
		List<TextField> inputs = new ArrayList<>();
		inputs.add(name);
		inputs.add(x);
		inputs.add(y);
		inputs.add(z);
		return inputs;
	}
	
	public List<GuiButton> getListOfButtons() {
		List<GuiButton> buttons = new ArrayList<>();
		buttons.add(deleteButton);
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
		if(elementHeight == null) {
			int highestHeight = 0;
			
			if(color.height > highestHeight) {
				highestHeight = color.height;
			}
		
			List<GuiButton> buttons = getListOfButtons();
			for (int i = 0; i < buttons.size(); i++) {
				if(buttons.get(i).height > highestHeight) {
					highestHeight = buttons.get(i).height;
				}
			}
			
			List<TextField> inputs = getListOfTextFields();
			for (int i = 0; i < inputs.size(); i++) {
				if(inputs.get(i).height > highestHeight) {
					highestHeight = inputs.get(i).height;
				}
			}
			
			elementHeight = highestHeight;
			return highestHeight;
		}else {
			return elementHeight;
		}
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		getWidth();
		getHeight();
		return (mouseX >= xPosition && mouseX <= xPosition + elementWidth && mouseY >= yPosition - margin/2 && mouseY <= yPosition + elementHeight + margin/2);
	}
	
	public boolean isHigherHalfHovered(int mouseX, int mouseY) {
		getWidth();
		getHeight();
		return (mouseX >= xPosition && mouseX <= xPosition + elementWidth && mouseY >= yPosition - margin/2 && mouseY <= yPosition + elementHeight/2);
	}
	
	public boolean isLowerHalfHovered(int mouseX, int mouseY) {
		getWidth();
		getHeight();
		return (mouseX >= xPosition && mouseX <= xPosition + elementWidth && mouseY >= yPosition + elementHeight/2 && mouseY <= yPosition + elementHeight + margin/2);
	}
}
