package treemek.mesky.handlers.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class ButtonWithToolkit extends GuiButton{

	private long timeHovered = 0;
	
	protected long hovered_hold_time = 1250;
	
	public ButtonWithToolkit(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(!hovered) {
			timeHovered = System.currentTimeMillis();
		}
	}
	
	public boolean shouldShowTooltip() {
	    return enabled && hovered && System.currentTimeMillis() - timeHovered  >= hovered_hold_time && (displayString == null || displayString.length() > 0);
	}
	
	public void changeTimeForToolkitToShow(long t) {
		hovered_hold_time = t;
	}

}
