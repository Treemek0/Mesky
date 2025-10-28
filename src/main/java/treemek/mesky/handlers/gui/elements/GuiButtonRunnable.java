package treemek.mesky.handlers.gui.elements;

import net.minecraft.client.gui.GuiButton;

public class GuiButtonRunnable extends ButtonWithToolkit{

	private Runnable runnable;
	
	public GuiButtonRunnable(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}

	public void set_runnable(Runnable runnable) {
		this.runnable = runnable;
	}
	
	protected void run_runnable() {
		if(runnable != null) {
			runnable.run();
		}
	}
}
