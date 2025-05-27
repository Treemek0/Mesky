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
	public int xPosition;
	public Float[] position;
	public Float scale;
	private double margin;
	private double inputHeight;
	
	public AlertElement(TextField trigger, TextField display, TextField time, ListBox sound, Slider volume, Slider pitch, DeleteButton deleteButton, EditButton editButton, CheckButton onlyParty, CheckButton ignorePlayers, CheckButton isEqual, Float[] position, Float scale, CheckButton enabled, double inputMargin) {
		this.trigger = trigger;
		this.yPosition = trigger.yPosition;
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
	
	public void updateYposition(int y, int inputHeight) {
		this.yPosition = y;
		this.trigger.yPosition = y;
		this.display.yPosition = y;
		this.time.yPosition = y;
		this.sound.yPosition = (int) (y + inputHeight + 5);
		this.volume.yPosition = (int) (y + inputHeight + 5);
		this.pitch.yPosition = (int) (y + inputHeight + 5);
		this.deleteButton.yPosition = y;
		this.editButton.yPosition = y;
		this.onlyParty.yPosition = y;
		this.ignorePlayers.yPosition = y;
		this.isEqual.yPosition = y;
		this.enabled.yPosition = y;
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
		buttons.add(editButton);
		buttons.add(onlyParty);
		buttons.add(ignorePlayers);
		buttons.add(isEqual);
		buttons.add(enabled);
		buttons.add(volume);
		buttons.add(pitch);
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
