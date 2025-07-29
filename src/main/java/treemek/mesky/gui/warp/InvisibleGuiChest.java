package treemek.mesky.handlers.gui.warp;

import java.awt.Color;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;

public class InvisibleGuiChest extends GuiChest {
    public InvisibleGuiChest(IInventory upper, IInventory lower) {
        super(upper, lower);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	drawRect(0, 0, width, height, new Color(33, 33, 33, 210).getRGB());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // Skip rendering
    }
}
