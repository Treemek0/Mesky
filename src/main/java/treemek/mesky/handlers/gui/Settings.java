package treemek.mesky.handlers.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.buttons.CheckButton;

public class Settings extends GuiScreen {
	 
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Mesky");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Settings", titleX, titleY, scale, true, 0x3e91b5);
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
        
        int checkX = (int)(width / 4);
        int centerY = height / 2;
        int buttonWidth = 20;
        int buttonHeight = 20;
        
        
        this.buttonList.add(new CheckButton(0, checkX, centerY - (buttonHeight / 2), buttonWidth, buttonHeight, "Ghost Blocks", SettingsConfig.GhostBlocks));
        this.buttonList.add(new CheckButton(1, checkX, centerY + 30 - (buttonHeight / 2), buttonWidth, buttonHeight, "Fishing timer", SettingsConfig.FishingTimer));
        this.buttonList.add(new CheckButton(2, checkX, centerY + 60 - (buttonHeight / 2), buttonWidth, buttonHeight, "Bonzo's Mask timer", SettingsConfig.BonzoTimer));
        this.buttonList.add(new CheckButton(3, checkX, centerY + 90 - (buttonHeight / 2), buttonWidth, buttonHeight, "Spirit Mask timer", SettingsConfig.SpiritTimer));
        this.buttonList.add(new CheckButton(4, checkX, centerY + 120 - (buttonHeight / 2), buttonWidth, buttonHeight, "Ghost Pickaxe", SettingsConfig.GhostPickaxe));
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
        	case 0:
            	SettingsConfig.GhostBlocks = !SettingsConfig.GhostBlocks;
            	SettingsConfig.GhostPickaxe = false;
                break;
            case 1:
                SettingsConfig.FishingTimer = !SettingsConfig.FishingTimer;
                break;
            case 2:
            	SettingsConfig.BonzoTimer = !SettingsConfig.BonzoTimer;
                break;
            case 3:
                SettingsConfig.SpiritTimer = !SettingsConfig.SpiritTimer;
                break;
            case 4:
            	SettingsConfig.GhostPickaxe = !SettingsConfig.GhostPickaxe;
            	SettingsConfig.GhostBlocks = false;
                break;
        }
        ConfigHandler.saveSettings();
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
}
