package treemek.mesky.handlers.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.gui.GuiScreen;
import treemek.mesky.handlers.gui.elements.buttons.ListBox;
import treemek.mesky.handlers.gui.elements.buttons.ListBox.Option;
import treemek.mesky.handlers.gui.elements.sliders.AlphaSlider;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;

public class TestGui extends GuiScreen {
	AlphaSlider test;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		test.drawSlider();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
		test = new AlphaSlider(0, 0, 0, 200, 20, 1);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		test.mousePressed(mc, mouseX, mouseY);
	}
}
