package treemek.mesky.handlers.gui.alerts;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.EditButton;

public class AlertElement {
	GuiTextField trigger;
	public GuiTextField display;
	GuiTextField time;
	DeleteButton deleteButton;
	EditButton editButton;
	CheckButton onlyParty;
	CheckButton ignorePlayers;
	CheckButton isEqual;
	public int yPosition;
	public int xPosition;
	public int[] position;
	public Float scale;
	public Integer elementHeight = null;
	public Integer elementWidth = null;
	
	
	public AlertElement(GuiTextField trigger, GuiTextField display, GuiTextField time, DeleteButton deleteButton, EditButton editButton, CheckButton onlyParty, CheckButton ignorePlayers, CheckButton isEqual, int[] position, Float scale) {
		this.trigger = trigger;
		this.display = display;
		this.time = time;
		this.deleteButton = deleteButton;
		this.editButton = editButton;
		this.onlyParty = onlyParty;
		this.ignorePlayers = ignorePlayers;
		this.isEqual = isEqual;
		this.position = position;
		this.scale = scale;
	}
	
	public void updateYposition(int y) {
		this.yPosition = y;
		this.trigger.yPosition = y;
		this.display.yPosition = y;
		this.time.yPosition = y;
		this.deleteButton.yPosition = y;
		this.editButton.yPosition = y;
		this.onlyParty.yPosition = y;
		this.ignorePlayers.yPosition = y;
		this.isEqual.yPosition = y;
	}
	
	public List<GuiTextField> getListOfTextFields() {
		List<GuiTextField> inputs = new ArrayList<>();
		inputs.add(trigger);
		inputs.add(display);
		inputs.add(time);
		return inputs;
	}
	
	public List<GuiButton> getListOfButtons() {
		List<GuiButton> buttons = new ArrayList<>();
		buttons.add(deleteButton);
		buttons.add(editButton);
		buttons.add(onlyParty);
		buttons.add(ignorePlayers);
		buttons.add(isEqual);
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
