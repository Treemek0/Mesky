package treemek.mesky.handlers.gui.alerts;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.EditButton;
import treemek.mesky.handlers.gui.elements.buttons.ListBox;
import treemek.mesky.handlers.gui.elements.sliders.Slider;
import treemek.mesky.handlers.gui.elements.textFields.TextField;

public class AlertElement {
	TextField trigger;
	public TextField display;
	TextField time;
	ListBox sound;
	Slider volume;
	Slider pitch;
	DeleteButton deleteButton;
	EditButton editButton;
	CheckButton onlyParty;
	CheckButton ignorePlayers;
	CheckButton isEqual;
	CheckButton enabled;
	public int yPosition;
	public double yPositionD;
	public int xPosition;
	public Float[] position;
	public Float scale;
	private double margin;
	private double inputHeight;
	
	public AlertElement(TextField trigger, TextField display, TextField time, ListBox sound, Slider volume, Slider pitch, DeleteButton deleteButton, EditButton editButton, CheckButton onlyParty, CheckButton ignorePlayers, CheckButton isEqual, Float[] position, Float scale, CheckButton enabled, double inputMargin) {
		this.trigger = trigger;
		this.yPosition = trigger.yPosition;
		this.yPositionD = yPosition;
		this.display = display;
		this.time = time;
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
		this.deleteButton = deleteButton;
		this.editButton = editButton;
		this.onlyParty = onlyParty;
		this.ignorePlayers = ignorePlayers;
		this.isEqual = isEqual;
		this.position = position;
		this.scale = scale;
		this.enabled = enabled;
		this.margin = inputMargin;
		
		this.xPosition = onlyParty.xPosition - 1;
	}
	
	public void updateYposition(double yD, int inputHeight) {
		this.yPositionD = yD;
		this.yPosition = (int) yD;
		this.trigger.yPosition = (int) yD;
		this.display.yPosition = (int) yD;
		this.time.yPosition = (int) yD;
		this.sound.yPosition = (int) ((int) yD + inputHeight + 5);
		this.volume.yPosition = (int) ((int) yD + inputHeight + 5);
		this.pitch.yPosition = (int) ((int) yD + inputHeight + 5);
		this.deleteButton.yPosition = (int) yD;
		this.editButton.yPosition = (int) yD;
		this.onlyParty.yPosition = (int) yD;
		this.ignorePlayers.yPosition = (int) yD;
		this.isEqual.yPosition = (int) yD;
		this.enabled.yPosition = (int) yD;
	}
	
	public List<TextField> getListOfTextFields() {
		List<TextField> inputs = new ArrayList<>();
		inputs.add(trigger);
		inputs.add(display);
		inputs.add(time);
		return inputs;
	}
	
	public List<GuiButton> getListOfButtons() {
		List<GuiButton> buttons = new ArrayList<>();
		buttons.add(deleteButton);
		buttons.add(onlyParty);
		buttons.add(ignorePlayers);
		buttons.add(isEqual);
		buttons.add(enabled);
		buttons.add(volume);
		buttons.add(pitch);
		buttons.add(editButton);
		return buttons;
	}
	
	public int getWidth() {
		return (enabled.xPosition + enabled.width) - xPosition + 2;
	}
	
	public int getHeight() {
		return (trigger.height * 2) + 5;
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
