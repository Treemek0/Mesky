package treemek.mesky.handlers.gui.settings;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ChatComponentText;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.features.HidePlayers;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.FoldableSettingButton;
import treemek.mesky.handlers.gui.elements.buttons.SettingButton;
import treemek.mesky.handlers.gui.elements.textFields.SettingTextField;

public class SettingsGUI extends GuiScreen {
	static List<Category> categories = new ArrayList<>();
	
	private ShaderGroup blurShader;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		int centerY = height / 3;
		int checkX_2nd = (int) (width*0.3);
		
		for (int i = 0; i < categories.size(); i++) {
			int y = (int) (height*0.15f + (i * height*0.075f));
			categories.get(i).drawCategory(width, height, y);
		}
			
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scale = (float) ((height*0.1f) / defaultFontHeight) / 2;

        int textLength = mc.fontRendererObj.getStringWidth("Settings");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) (height * 0.05f);

        //drawRect((int)(width * 0.1f), titleY - 5, (int)(width*0.9f), (int)(height * 0.15f), 0x70151515);
        RenderHandler.drawText("Settings", titleX, titleY, scale, true, 0x3e91b5);
	}
	
	@Override
	public void initGui() {
	    categories.clear();
	    
	    if (blurShader == null) {
	        try {
	            blurShader = new ShaderGroup(Minecraft.getMinecraft().getTextureManager(), Minecraft.getMinecraft().getResourceManager(), Minecraft.getMinecraft().getFramebuffer(), new net.minecraft.util.ResourceLocation("minecraft", "shaders/post/blur.json"));
	            blurShader.createBindFramebuffers(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
	            Minecraft.getMinecraft().entityRenderer.loadShader(new net.minecraft.util.ResourceLocation("minecraft", "shaders/post/blur.json"));
	        } catch (JsonSyntaxException | IOException e) {
	            e.printStackTrace();
	        }
        }
	    
	    
	    int checkSize = ((height / 25) < 12)?12:(height / 25);

		int centerY = height / 3;
	    int checkX = 5;
	    
    	// category contains list with all subcategories
    	// subcategory contains buttons
    	
    	// Illegal category
	    List<Object> ghostSub = new ArrayList<>();
	    List<Object> illegalFishingSub = new ArrayList<>();
	    List<SubCategory> illegal = new ArrayList<>();
	    
        ghostSub.add(new SettingButton(0, checkSize, checkSize, "Ghost Blocks", SettingsConfig.GhostBlocks));
        
        List<Object> ghostpickaxeFoldable = new ArrayList<>();
        ghostpickaxeFoldable.add(new SettingTextField(1,  "Ghost Pickaxe slot", checkSize*2, checkSize, SettingsConfig.GhostPickaxeSlot, 1, true, false));
        ghostSub.add(new FoldableSettingButton(1,  checkSize, checkSize, "Ghost Pickaxe", SettingsConfig.GhostPickaxe, ghostpickaxeFoldable));
        
        List<Object> autoFishFoldable = new ArrayList<>();
        autoFishFoldable.add(new SettingButton(-1,  checkSize, checkSize, "Kill Sea Creatures", SettingsConfig.KillSeaCreatures));
        autoFishFoldable.add(new SettingButton(-1,  checkSize, checkSize, "Auto throw hook", SettingsConfig.AutoThrowHook));
        illegalFishingSub.add(new FoldableSettingButton(3,  checkSize, checkSize, "AutoFish", SettingsConfig.AutoFish, autoFishFoldable));
        
        illegalFishingSub.add(new SettingButton(2,  checkSize, checkSize, "Jawbus Detection", SettingsConfig.JawbusDetection));
        
        illegal.add(new SubCategory("Ghost blocking", ghostSub));
        illegal.add(new SubCategory("Illegal fishing", illegalFishingSub));
        
        categories.add(new Category("Illegal", illegal, false));
        
        
        // Normal category
	    List<Object> timerSub = new ArrayList<>();
	    List<Object> utilitySub = new ArrayList<>();
	    List<SubCategory> normal = new ArrayList<>();
        timerSub.add(new SettingButton(3, checkSize, checkSize, "Fishing timer", SettingsConfig.FishingTimer));
        timerSub.add(new SettingButton(4,  checkSize, checkSize, "Bonzo's Mask timer", SettingsConfig.BonzoTimer));
        timerSub.add(new SettingButton(5,  checkSize, checkSize, "Spirit Mask timer", SettingsConfig.SpiritTimer));
        utilitySub.add(new SettingButton(6,  checkSize, checkSize, "Hide Players", SettingsConfig.HidePlayers));
		utilitySub.add(new SettingButton(7,  checkSize, checkSize, "Coords Detection", SettingsConfig.CoordsDetection));
		//utilitySub.add(new SettingButton(8,  checkSize, checkSize, "Anty ghost blocks", SettingsConfig.AntyGhostBlocks));
		
		normal.add(new SubCategory("Timers", timerSub));
		normal.add(new SubCategory("Utilities", utilitySub));
		
		categories.add(new Category("Normal", normal, true));
	}
	
    protected static void buttonClicked(GuiButton button) {
        switch (button.id) {
        	case 0:
            	SettingsConfig.GhostPickaxe.isOn = false;
                break;
            case 1:
            	SettingsConfig.GhostBlocks.isOn = false;
                break;
            case 6:
            	HidePlayers.resetPlayersSize();
                break;
        }
    }
	
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (Category category : categories) {
			int categoryHeight = (int) (height * 0.075f) - 1;
			int categoryWidth =  (int) (width * 0.2f);;
			float x = width * 0.1f;
			float topY = height*0.15f;
			
			if (mouseX >= x && mouseX <= x + categoryWidth && mouseY >= topY && mouseY <= topY + (categories.size()*categoryHeight)) {
				category.isOpened = false; // turns off all categories
			}
			
			if (mouseX >= x && mouseX <= x + categoryWidth && mouseY >= category.y && mouseY <= category.y + categoryHeight) {
				category.isOpened = true;
			}
			
			
			if(category.isOpened) {
				for (SubCategory sub : category.list) {
					sub.mouseClicked(mouseX, mouseY, mouseButton); // so that buttons will click because mouseClicked only is detected in here so i have to
				}
			}
		}
		
		
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (Category category : categories) {
			if(category.isOpened) {
				for (SubCategory sub : category.list) {
					sub.keyTyped(typedChar, keyCode); // so that buttons will click because mouseClicked only is detected in here so i have to
				}
			}
		}
		
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
    public void onGuiClosed() {
        if (blurShader != null) {
            Minecraft.getMinecraft().entityRenderer.stopUseShader();
        }
        
        ConfigHandler.saveSettings();
        super.onGuiClosed();
    }
}
