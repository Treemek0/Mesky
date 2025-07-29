package treemek.mesky.handlers.gui.waypoints;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.AddButton;
import treemek.mesky.handlers.gui.elements.buttons.ArrowButton;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.HoldButton;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.Utils;

public class WaypointGroupElement {
	String name;
	List<WaypointElement> list;
	AddButton addWaypoint;
	DeleteButton delete;
	HoldButton move;
	TextField nameField;
	CheckButton enabled;
	ArrowButton opened;
	int yPosition;
	double yPositionD;
	int xPosition;
	int height = 0;
	public String world;
	
	public WaypointGroupElement(String name, List<WaypointElement> list, String world) {
		if(name.endsWith(" %" + world)) name = name.substring(0, name.lastIndexOf(" %" + world));
		this.name = name;
		
		this.world = world;
		this.list = list;
		this.addWaypoint = new AddButton(0, 0, 0, 0, 0, "Add Waypoint");
		this.delete = new DeleteButton(0, 0, 0, 0, 0, "Delete group");
		this.move = new HoldButton(0, 0, 0, 0, 0, "Move group");
		this.yPosition = 0;
		this.yPositionD = 0;
		this.nameField = new TextField(0, 0, 0, 0, 0);
		this.nameField.setColoredField(true);
		this.nameField.setMaxStringLength(512);
		this.nameField.setCanLoseFocus(true);
		this.nameField.setVisible(false);
		this.nameField.setText(name);
		this.enabled = new CheckButton(0, 0, 0, 0, 0, "", true);
		this.opened = new ArrowButton(0, 0, 0, 0, 0, "", true);
	}
	
	public void setEnabled(boolean b) {
		enabled.setFull(b);
	}
	
	public void switchOpened() {
		opened.switchOpened();
	}
	
	public void updateYposition(int y, int inputHeight, int inputMargin, WaypointElement holdingElement) {
		this.yPosition = y;
		this.yPositionD = y;
		updateElements(y, inputHeight, inputMargin);
		int h = 0;
		
		y += inputHeight + inputMargin;
		h += inputHeight + inputMargin/2;

		if(opened.isOpened()) {
			if(this.list.isEmpty()) h += inputHeight;
			for (WaypointElement waypointElement : this.list) {
				if(holdingElement != waypointElement) waypointElement.updateYposition(y);
				y += inputHeight + inputMargin;
				h += inputHeight + inputMargin;
			}
		}
		
		height = h;
	}
	
	public void updateYpositionSmooth(int y, int inputHeight, int inputMargin, WaypointElement holdingElement) {
		ScaledResolution wind = new ScaledResolution(Minecraft.getMinecraft());
    	double windowHeight = wind.getScaledHeight();
		
		int orgY = y;
    	
    	double k = Math.signum((y - yPositionD)) * (240f/Minecraft.getDebugFPS());
		if (Math.abs(y - yPositionD) > height * 2) k *= 4;

		
		if(Math.abs(k) > Math.abs(yPositionD - y)) {
			this.yPositionD = y;
		}else {
			this.yPositionD += k;
		}
		
		this.yPosition = (int) yPositionD;
		
		updateElements(yPosition, inputHeight, inputMargin);
		int h = 0;
		
		y += inputHeight + inputMargin;
		h += inputHeight + inputMargin/2;
		if(opened.isOpened()) {
			for (WaypointElement element : list) {
				if (element != holdingElement) {
					if(yPosition == orgY) {
						double l = Math.signum((y - element.yPositionD)) * (240f/Minecraft.getDebugFPS());
						if (Math.abs(y - element.yPositionD) > element.getHeight() * 2) l *= 4;
						
						if(Math.abs(l) > Math.abs(element.yPositionD - y)) {
							element.updateYposition(y);
						}else {
							element.updateYposition(element.yPositionD + l);
						}
					}else {
						element.updateYposition(yPosition + h + inputMargin/2);
					}
				}
				
				y += inputHeight + inputMargin;
				h += inputHeight + inputMargin;
			}
			if(this.list.isEmpty()) h += inputHeight;
		}
		
		height = h;
	}
	
	
	private void updateElements(int y, int inputHeight, int inputMargin) {
		ScaledResolution wind = new ScaledResolution(Minecraft.getMinecraft());
    	int windowWidth = wind.getScaledWidth();
		
		this.addWaypoint.update(windowWidth/20, y+(inputHeight+inputMargin/2)/2 - inputHeight/2, inputHeight, inputHeight);
		this.move.update(windowWidth/20 - inputHeight/2 - inputHeight/3, y+(inputHeight+inputMargin/2)/2 - inputHeight/4, inputHeight/2, inputHeight/2);
		this.delete.update((int) (windowWidth*0.85f), y+(inputHeight+inputMargin/2)/2 - inputHeight/2, inputHeight, inputHeight);
		this.enabled.update((int)(windowWidth * 0.9f), y+(inputHeight+inputMargin/2)/2 - inputHeight/2, inputHeight);
		this.opened.update((int) (windowWidth * 0.9f) + inputHeight, y+(inputHeight+inputMargin/2)/2 - inputHeight/2, inputHeight);
		
        double checksScale = RenderHandler.getTextScale("Players", windowWidth/20); // "Players" is the longest in Alerts and i want all to have the same scale
		this.nameField.update(xPosition, (int) (yPosition + ((inputHeight + inputMargin/2)/2 - RenderHandler.getTextHeight(checksScale)/2) - 3), windowWidth/4, inputHeight);
	}
	
	
	public boolean isHovered(int mouseX, int mouseY) {
		return (mouseY >= yPosition && mouseY <= yPosition + height);
	}
	
	public boolean isHigherHalfHovered(int mouseX, int mouseY) {
		return (mouseY >= yPosition && mouseY <= yPosition + height/2);
	}
	
	public boolean isLowerHalfHovered(int mouseX, int mouseY) {
		return (mouseY >= yPosition + height/2 && mouseY <= yPosition + height);
	}

	public void changeName(String name) {
		this.name = name;
		this.nameField.setText(name);
	}
}
