package treemek.mesky.handlers.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.buttons.CheckButton;

public class CosmeticsGui extends GuiScreen {
	 
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Mesky");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Cosmetics", titleX, titleY, scale, true, 0x3e91b5);
        
        int previewX = (int)(width / 4);
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
        
        int checkX = (int)(width / 3);
        int centerY = height / 2;
        int buttonWidth = 20;
        int buttonHeight = 20;
        
        
        this.buttonList.add(new CheckButton(0, checkX, centerY - (buttonHeight / 2), buttonWidth, buttonHeight, "Dragon Wings", (CosmeticHandler.WingsType == 1)?true:false));
        this.buttonList.add(new CheckButton(1, checkX, centerY + 30 - (buttonHeight / 2), buttonWidth, buttonHeight, "Angel Wings", (CosmeticHandler.WingsType == 2)?true:false));
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
        	case 0:
        		if(CosmeticHandler.WingsType == 1) CosmeticHandler.WingsType = 0;
                else CosmeticHandler.WingsType = 1;
                break;
            case 1:
                if(CosmeticHandler.WingsType == 2) CosmeticHandler.WingsType = 0;
                else CosmeticHandler.WingsType = 2;
                break;
        }
        refreshGui();
        ConfigHandler.saveSettings();
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void refreshGui() {
		buttonList.clear();
		initGui();
	}
}
