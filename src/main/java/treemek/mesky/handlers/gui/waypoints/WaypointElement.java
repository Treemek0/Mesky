package treemek.mesky.handlers.gui.waypoints;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;

public class WaypointElement {
	GuiTextField name;
	GuiTextField color;
	GuiTextField x;
	GuiTextField y;
	GuiTextField z;
	DeleteButton deleteButton;
	public int yPosition;
	public int xPosition;
	public Integer elementHeight = null;
	public Integer elementWidth = null;
	
	public WaypointElement(GuiTextField name, GuiTextField color, GuiTextField x, GuiTextField y, GuiTextField z, DeleteButton deleteButton) {
		this.name = name;
		this.color = color;
		this.x = x;
		this.y = y;
		this.z = z;
		this.deleteButton = deleteButton;
		
	}
	
	public void updateYposition(int y) {
		this.yPosition = y;
		this.name.yPosition = y;
		this.color.yPosition = y;
		this.x.yPosition = y;
		this.y.yPosition = y;
		this.z.yPosition = y;
		this.deleteButton.yPosition = y;
	}
	
	public List<GuiTextField> getListOfTextFields() {
		List<GuiTextField> inputs = new ArrayList<>();
		inputs.add(name);
		inputs.add(color);
		inputs.add(x);
		inputs.add(y);
		inputs.add(z);
		return inputs;
	}
	
	public List<GuiButton> getListOfButtons() {
		List<GuiButton> buttons = new ArrayList<>();
		buttons.add(deleteButton);
		return buttons;
	}
	
	
	public int getWidth() {
		if(elementWidth == null) {
			int lowestX = 10000000;
			int highestX = 0;
			int highestX_width = 0;
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
			
			int width = lowestX + highestX + highestX_width;
			
			xPosition = lowestX;
			elementWidth = width;
			return width;
		}else {
			return elementWidth;
		}
	}
	
	public int getHeight() {
		if(elementHeight == null) {
		int highestHeight = 0;
			
			List<GuiButton> buttons = getListOfButtons();
			for (int i = 0; i < buttons.size(); i++) {
				if(buttons.get(i).height > highestHeight) {
					highestHeight = buttons.get(i).height;
				}
			}
			
			List<GuiTextField> inputs = getListOfTextFields();
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
		return (mouseX >= xPosition && mouseX <= xPosition + elementWidth && mouseY >= yPosition && mouseY <= yPosition + elementHeight);
	}
}
