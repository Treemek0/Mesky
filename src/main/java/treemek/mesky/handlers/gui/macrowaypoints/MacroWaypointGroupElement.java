package treemek.mesky.handlers.gui.macrowaypoints;

import java.util.ArrayList;
import java.util.List;
// TODO
public class MacroWaypointGroupElement {
	private List<MacroWaypointElement> list = new ArrayList<>();

	public MacroWaypointGroupElement() {
		
	}
	
	public void addElement(MacroWaypointElement elem) {
		list.add(elem);
	}
	
	public MacroWaypointElement getElement(int i) {
		return list.get(i);
	}
}
