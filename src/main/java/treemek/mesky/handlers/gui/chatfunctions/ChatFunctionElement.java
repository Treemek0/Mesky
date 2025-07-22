package treemek.mesky.handlers.gui.chatfunctions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.EditButton;
import treemek.mesky.handlers.gui.elements.textFields.TextField;

public class ChatFunctionElement {
	TextField trigger;
	public TextField function;
	DeleteButton deleteButton;
	CheckButton onlyParty;
	CheckButton ignorePlayers;
	CheckButton isEqual;
	CheckButton enabled;
	public int yPosition;
	public double yPositionD;
	public int xPosition;
	public Integer elementHeight = null;
	public Integer elementWidth = null;
	private double margin;
	

	
	public ChatFunctionElement(TextField trigger, TextField function, DeleteButton deleteButton, CheckButton onlyParty, CheckButton ignorePlayers, CheckButton isEqual, CheckButton enabled, double inputMargin) {
		this.trigger = trigger;
		this.yPosition = trigger.yPosition;
		this.yPositionD = yPosition;
		this.function = function;
		this.deleteButton = deleteButton;
		this.onlyParty = onlyParty;
		this.ignorePlayers = ignorePlayers;
		this.isEqual = isEqual;
		this.enabled = enabled;
		this.margin = inputMargin;
	}
	
	public void updateYposition(double yD) {
		this.yPositionD = yD;
		this.yPosition = (int) yD;
		this.trigger.yPosition = (int) yD;
		this.function.yPosition = (int) yD;
		this.deleteButton.yPosition = (int) yD;
		this.onlyParty.yPosition = (int) yD;
		this.ignorePlayers.yPosition = (int) yD;
		this.isEqual.yPosition = (int) yD;
		this.enabled.yPosition = (int) yD;
	}
	
	public List<TextField> getListOfTextFields() {
		List<TextField> inputs = new ArrayList<>();
		inputs.add(trigger);
		inputs.add(function);
		return inputs;
	}
	
	public List<GuiButton> getListOfButtons() {
		List<GuiButton> buttons = new ArrayList<>();
		buttons.add(enabled);
		buttons.add(deleteButton);
		buttons.add(onlyParty);
		buttons.add(ignorePlayers);
		buttons.add(isEqual);
		return buttons;
	}
	
	public int getWidth() {
		if(elementWidth == null) {
			int lowestX = 10000000;
			int highestX = 0;
			
			List<GuiButton> buttons = getListOfButtons();
			for (int i = 0; i < buttons.size(); i++) {
				if(buttons.get(i).xPosition < lowestX) lowestX = buttons.get(i).xPosition;
				if(buttons.get(i).xPosition + buttons.get(i).width > highestX) {
					highestX = buttons.get(i).xPosition + buttons.get(i).width;
				}
			}
			
			List<TextField> inputs = getListOfTextFields();
			for (int i = 0; i < inputs.size(); i++) {
				if(inputs.get(i).xPosition < lowestX) lowestX = inputs.get(i).xPosition;
				if(inputs.get(i).xPosition + inputs.get(i).width > highestX) {
					highestX = inputs.get(i).xPosition + inputs.get(i).width;
				}
			}
			
			int width = highestX - lowestX;
			
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
