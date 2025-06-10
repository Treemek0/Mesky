package treemek.mesky.handlers.gui.waypoints;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.AddButton;
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
	boolean enabled;
	boolean opened;
	int yPosition;
	int xPosition;
	int height = 0;
	
	public WaypointGroupElement(String name, List<WaypointElement> list) {
		this.name = name;
		this.list = list;
		this.addWaypoint = new AddButton(0, 0, 0, 0, 0, "Add Waypoint");
		this.delete = new DeleteButton(0, 0, 0, 0, 0, "");
		this.move = new HoldButton(0, 0, 0, 0, 0, "");
		this.yPosition = 0;
		this.nameField = new TextField(0, 0, 0, 0, 0);
		this.nameField.setColoredField(true);
		this.nameField.setMaxStringLength(512);
		this.nameField.setCanLoseFocus(true);
		this.nameField.setVisible(false);
		this.nameField.setText(name);
	}
	
	public void switchOpened() {
		opened = !opened;
	}
	
	public void updateYposition(int y, int inputHeight, int inputMargin, WaypointElement holdingElement) {
		this.yPosition = y;
		updateElements(y, inputHeight, inputMargin);
		int h = 0;
		
		y += inputHeight + inputMargin;
		h += inputHeight + inputMargin;
		if(this.list.isEmpty()) h += inputHeight;
		for (WaypointElement waypointElement : this.list) {
			if(holdingElement != waypointElement) waypointElement.updateYposition(y);
			y += inputHeight + inputMargin;
			h += inputHeight + inputMargin;
		}
		
		height = h;
	}
	
	public void updateYpositionSmooth(int y, int inputHeight, int inputMargin, WaypointElement holdingElement) {
    	int orgY = y;
    	
    	int k = (int) Math.signum((y - yPosition));
		if (Math.abs(y - yPosition) > height * 2) k *= 4;
    	
		this.yPosition += k;
		updateElements(yPosition, inputHeight, inputMargin);
		int h = 0;
		
		y += inputHeight + inputMargin;
		h += inputHeight + inputMargin;
		for (WaypointElement element : list) {
			if (element != holdingElement) {
				if(yPosition == orgY) {
					int l = (int) Math.signum((y - element.yPosition));
					if (Math.abs(y - element.yPosition) > element.getHeight() * 2) l *= 4;
					
					element.updateYposition(element.yPosition + l);
				}else {
					element.updateYposition(yPosition + h);
				}
			}
			
			y += inputHeight + inputMargin;
			h += inputHeight + inputMargin;
		}
		
		if(this.list.isEmpty()) h += inputHeight;
		
		height = h;
	}
	
	
	private void updateElements(int y, int inputHeight, int inputMargin) {
		ScaledResolution wind = new ScaledResolution(Minecraft.getMinecraft());
    	int windowHeight = wind.getScaledHeight();
    	int windowWidth = wind.getScaledWidth();
		
		this.addWaypoint.update(windowWidth/20, y+(inputHeight+inputMargin/2)/2 - inputHeight/2, inputHeight, inputHeight);
		this.move.update(windowWidth/20 - inputHeight/2 - inputHeight/3, y+(inputHeight+inputMargin/2)/2 - inputHeight/4, inputHeight/2, inputHeight/2);
		this.delete.update((int) (windowWidth*0.85f), y+(inputHeight+inputMargin/2)/2 - inputHeight/2, inputHeight, inputHeight);
		
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
}
