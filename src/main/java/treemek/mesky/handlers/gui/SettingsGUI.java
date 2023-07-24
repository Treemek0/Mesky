package treemek.mesky.handlers.gui;

import org.fusesource.jansi.Ansi.Color;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class SettingsGUI extends GuiScreen {

	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		 // Calculate center positions
        int centerX = width / 2;
        int centerY = height / 2;
        
        
        int titleWidth = fontRendererObj.getStringWidth("Mesky");
        fontRendererObj.drawString("Mesky", centerX - titleWidth / 2, centerY * 0.25f, 0x3e91b5, true);
       
 
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
        int centerX = width / 2;
        int centerY = height / 2;
        int buttonWidth = 150;
        int buttonHeight = 20;

        this.buttonList.add(new GuiButton(0, centerX - buttonWidth / 2, centerY + 20, buttonWidth, buttonHeight, "Settings"));
        this.buttonList.add(new GuiButton(1, centerX - buttonWidth / 2, centerY + 50, buttonWidth, buttonHeight, "Locations"));
        this.buttonList.add(new GuiButton(2, centerX - buttonWidth / 2, centerY + 80, buttonWidth, buttonHeight, "Alerts"));
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                // Button 1 clicked
            	System.out.println("button1");
                break;
            case 1:
                // Button 2 clicked
                break;
            case 2:
                // Button 3 clicked
                break;
        }
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	
	
}
