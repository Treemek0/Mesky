package treemek.mesky.handlers.gui;

import org.fusesource.jansi.Ansi.Color;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.buttons.CheckButton;
import treemek.mesky.handlers.gui.buttons.MeskyButton;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;

public class GUI extends GuiScreen {
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Mesky");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height * 0.35) / scale);

        RenderHandler.drawText("Mesky", titleX, titleY, scale, true, 0x3e91b5);
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
        int centerX = width / 2;
        int centerY = height / 2;
        int buttonWidth = 150;
        int buttonHeight = 20;
        
        this.buttonList.add(new MeskyButton(0, centerX - buttonWidth / 2, centerY - 20, buttonWidth, buttonHeight, "Settings"));
        this.buttonList.add(new MeskyButton(1, centerX - buttonWidth / 2, centerY + 10, buttonWidth, buttonHeight, "Waypoints"));
        this.buttonList.add(new MeskyButton(2, centerX - buttonWidth / 2, centerY + 40, buttonWidth, buttonHeight, "Alerts"));
        this.buttonList.add(new MeskyButton(4, centerX - buttonWidth / 2, centerY + 70, buttonWidth, buttonHeight, "Chat Functions"));
        this.buttonList.add(new MeskyButton(5, 0, height - buttonHeight, buttonWidth, buttonHeight, "Gui Locations"));
        
        this.buttonList.add(new MeskyButton(3, width - buttonWidth, height - buttonHeight, buttonWidth, buttonHeight, "Cosmetics"));
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
            	// Button 1 clicked
            	Minecraft.getMinecraft().displayGuiScreen(new Settings());
                break;
            case 1:
            	Minecraft.getMinecraft().displayGuiScreen(new WaypointsGui());
				Location.checkTabLocation();
				WaypointsGui.region.setText(Locations.currentLocationText);
                break;
            case 2:
            	Minecraft.getMinecraft().displayGuiScreen(new AlertsGui());
                break;
            case 3:
            	Minecraft.getMinecraft().displayGuiScreen(new CosmeticsGui());
                break;
            case 4:
            	Minecraft.getMinecraft().displayGuiScreen(new ChatFunctionsGui());
                break;
            case 5:
            	Minecraft.getMinecraft().displayGuiScreen(new GuiLocations());
                break;
        }
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
}
