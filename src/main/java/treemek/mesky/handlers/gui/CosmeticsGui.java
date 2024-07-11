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
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.CosmeticCheckButton;

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
	    buttonList.clear();
	    
	    int checkSize = ((height / 25) < 12)?12:(height / 25);
        
        int checkX = 5;
        int previewSize = checkSize*4;
        int firstY = height / 3;
        int secondY = firstY + previewSize + 20;
        int thirdY = secondY + previewSize + 20;
        
        
        this.buttonList.add(new CosmeticCheckButton(0, checkX, firstY, previewSize, previewSize, "Dragon Wings", (CosmeticHandler.WingsType.number == 1)?true:false, new ResourceLocation(Reference.MODID, "textures/fireWings_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(1, checkX + ((previewSize+5)), firstY, previewSize, previewSize, "Angel Wings", (CosmeticHandler.WingsType.number == 2)?true:false, new ResourceLocation(Reference.MODID, "textures/angelWings_preview.png"), checkSize));
        
        this.buttonList.add(new CosmeticCheckButton(11, checkX, secondY, previewSize, previewSize, "Gentelmen Hat", (CosmeticHandler.HatType.number == 1)?true:false, new ResourceLocation(Reference.MODID, "textures/gentelmenHat_preview.png"), checkSize));
        
        this.buttonList.add(new CosmeticCheckButton(21, checkX, thirdY, previewSize, previewSize, "CalicoCat", (CosmeticHandler.PetType.number == 1)?true:false, new ResourceLocation(Reference.MODID, "textures/cat_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(22, checkX + ((previewSize+5)), thirdY, previewSize, previewSize, "GrayCat", (CosmeticHandler.PetType.number == 2)?true:false, new ResourceLocation(Reference.MODID, "textures/grayCat_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(23, checkX + ((previewSize+5)*2), thirdY, previewSize, previewSize, "BlackCat", (CosmeticHandler.PetType.number == 3)?true:false, new ResourceLocation(Reference.MODID, "textures/blackCat_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(24, checkX + ((previewSize+5)*3), thirdY, previewSize, previewSize, "RudyCat", (CosmeticHandler.PetType.number == 4)?true:false, new ResourceLocation(Reference.MODID, "textures/rudyCat_preview.png"), checkSize));
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
        	case 0:
        		if(CosmeticHandler.WingsType.number == 1) CosmeticHandler.WingsType.number = 0;
                else CosmeticHandler.WingsType.number = 1;
                break;
            case 1:
                if(CosmeticHandler.WingsType.number == 2) CosmeticHandler.WingsType.number = 0;
                else CosmeticHandler.WingsType.number = 2;
                break;
            case 11:
                if(CosmeticHandler.HatType.number == 1) CosmeticHandler.HatType.number = 0;
                else CosmeticHandler.HatType.number = 1;
                break;
            case 21:
                if(CosmeticHandler.PetType.number == 1) CosmeticHandler.PetType.number = 0;
                else CosmeticHandler.PetType.number = 1;
                break;
            case 22:
                if(CosmeticHandler.PetType.number == 2) CosmeticHandler.PetType.number = 0;
                else CosmeticHandler.PetType.number = 2;
                break;
            case 23:
                if(CosmeticHandler.PetType.number == 3) CosmeticHandler.PetType.number = 0;
                else CosmeticHandler.PetType.number = 3;
                break;
            case 24:
                if(CosmeticHandler.PetType.number == 4) CosmeticHandler.PetType.number = 0;
                else CosmeticHandler.PetType.number = 4;
                break;
        }
        initGui();
        ConfigHandler.saveSettings();
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
