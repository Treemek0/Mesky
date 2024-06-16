package treemek.mesky.handlers.gui;

import java.awt.Color;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.buttons.CheckButton;
import treemek.mesky.handlers.gui.buttons.CosmeticCheckButton;

public class CosmeticsGui extends GuiScreen {
	 
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		drawRect(0, 0, width, height, new Color(33, 33, 33,255).getRGB());
		
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
	    
	    int checkSize = ((height / 25) < 12)?12:(height / 25);
        
        int checkX = 5;
        int previewSize = checkSize*4;
        int firstY = height / 3;
        int secondY = firstY + previewSize + 20;

        
        this.buttonList.add(new CosmeticCheckButton(0, checkX, firstY, previewSize, previewSize, "Dragon Wings", (CosmeticHandler.WingsType == 1)?true:false, new ResourceLocation(Reference.MODID, "textures/fireWings_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(1, checkX + ((previewSize+5)), firstY, previewSize, previewSize, "Angel Wings", (CosmeticHandler.WingsType == 2)?true:false, new ResourceLocation(Reference.MODID, "textures/angelWings_preview.png"), checkSize));
        
        this.buttonList.add(new CosmeticCheckButton(11, checkX, secondY, previewSize, previewSize, "Gentelmen Hat", (CosmeticHandler.HatType == 1)?true:false, new ResourceLocation(Reference.MODID, "textures/gentelmenHat_preview.png"), checkSize));
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
            case 11:
                if(CosmeticHandler.HatType == 1) CosmeticHandler.HatType = 0;
                else CosmeticHandler.HatType = 1;
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
