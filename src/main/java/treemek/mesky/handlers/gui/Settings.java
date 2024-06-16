package treemek.mesky.handlers.gui;

import java.awt.Color;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.features.HidePlayers;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.buttons.CheckButton;

public class Settings extends GuiScreen {

    
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		int centerY = height / 3;
		int checkX_2nd = (int) (width*0.3);
		
		drawRect(0, 0, width, height, new Color(33, 33, 33,255).getRGB());
		
		
//		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
//		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		int rectWidth = (int) Math.max(mc.fontRendererObj.getStringWidth("Bonzo's Mask timer") + 5, (int) (width*0.25));

		
		RenderHandler.drawRectWithFrame(1, centerY - 5, rectWidth, height - 5, new Color(28, 28, 28,255).getRGB(), 1); // 1st row
		RenderHandler.drawRectWithFrame(checkX_2nd-5, centerY - 5, checkX_2nd + rectWidth, height - 5, new Color(28, 28, 28,255).getRGB(), 1);
		
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Settings");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Settings", titleX, titleY, scale, true, 0x3e91b5);
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
	    int inputHeight = ((height / 25) < 12)?12:(height / 25);

		int centerY = height / 3;
	    int checkX = 5;
        
        
        this.buttonList.add(new CheckButton(0, checkX, centerY, inputHeight, inputHeight, "Ghost Blocks", SettingsConfig.GhostBlocks));
        this.buttonList.add(new CheckButton(1, checkX, centerY + (inputHeight + 10), inputHeight, inputHeight, "Fishing timer", SettingsConfig.FishingTimer));
        this.buttonList.add(new CheckButton(2, checkX, centerY + (inputHeight + 10)*2, inputHeight, inputHeight, "Bonzo's Mask timer", SettingsConfig.BonzoTimer));
        this.buttonList.add(new CheckButton(3, checkX, centerY + (inputHeight + 10)*3, inputHeight, inputHeight, "Spirit Mask timer", SettingsConfig.SpiritTimer));
        this.buttonList.add(new CheckButton(4, checkX, centerY + (inputHeight + 10)*4, inputHeight, inputHeight, "Ghost Pickaxe", SettingsConfig.GhostPickaxe));
        this.buttonList.add(new CheckButton(5, checkX, centerY + (inputHeight + 10)*5, inputHeight, inputHeight, "Hide Players", SettingsConfig.HidePlayers));
        //this.buttonList.add(new CheckButton(6, checkX, centerY + (inputHeight + 10)*6, inputHeight, inputHeight, "Anty Ghost Blocks", SettingsConfig.AntyGhostBlocks));
        
        // 2nd row
        int checkX_2nd = (int) (width*0.3);
        this.buttonList.add(new CheckButton(7, checkX_2nd, centerY, inputHeight, inputHeight, "Coords Detection", SettingsConfig.CoordsDetection));
        this.buttonList.add(new CheckButton(8, checkX_2nd, centerY + (inputHeight + 10), inputHeight, inputHeight, "Jawbus Detection", SettingsConfig.JawbusDetection));
        //this.buttonList.add(new CheckButton(9, checkX_2nd, centerY + (inputHeight + 10), inputHeight, inputHeight, "Nick Mention Detection", SettingsConfig.NickMentionDetection));
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
            case 5:
            	SettingsConfig.HidePlayers = !SettingsConfig.HidePlayers;
            	HidePlayers.resetHeight();
                break;
            case 6:
            	SettingsConfig.AntyGhostBlocks = !SettingsConfig.AntyGhostBlocks;
                break;
            case 7:
            	SettingsConfig.CoordsDetection = !SettingsConfig.CoordsDetection;
                break;
            case 8:
            	SettingsConfig.JawbusDetection = !SettingsConfig.JawbusDetection;
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
