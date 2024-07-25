package treemek.mesky.handlers.gui.settings;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

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
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.FoldableSettingButton;
import treemek.mesky.handlers.gui.elements.buttons.SettingButton;
import treemek.mesky.handlers.gui.elements.sliders.SettingSlider;
import treemek.mesky.handlers.gui.elements.sliders.Slider;
import treemek.mesky.handlers.gui.elements.textFields.SettingTextField;
import treemek.mesky.handlers.gui.waypoints.WaypointElement;

public class SettingsGUI extends GuiScreen {
	static List<Category> categories = new ArrayList<>();
	
	private ShaderGroup blurShader;
	public Integer openedCategory = 0;
	
	ScrollBar scrollbar = new ScrollBar();
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		int centerY = height / 3;
		int checkX_2nd = (int) (width*0.3);
		
		scrollbar.updateScrollBar((int) Math.min(20, (width * 0.025)), (int)(height - (height * 0.15f)), (int)(width * 0.9), (int) (height * 0.15f));
		int offset = scrollbar.getOffset();
		
		
		for (int i = 0; i < categories.size(); i++) {
			int y = (int) (height*0.15f + (i * height*0.075f));
			
			Category category = categories.get(i);
			
			
			
			if(category == categories.get(openedCategory)) {
				category.drawCategory(width, height, y, offset, true);
				
				int CategoryTopY = (int)(height * 0.2f);
				int categoryHeight = height - CategoryTopY;
				int categoryContentHeight = ((category.list.get(category.list.size()-1).y - offset) + category.list.get(category.list.size()-1).subHeight) - CategoryTopY;
				scrollbar.updateMaxBottomScroll(categoryContentHeight - categoryHeight);
			}else {
				category.drawCategory(width, height, y, offset, false);
			}
		}
			
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scale = (float) ((height*0.1f) / defaultFontHeight) / 2;

        int textLength = mc.fontRendererObj.getStringWidth("Settings");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) (height * 0.05f);

        //drawRect((int)(width * 0.1f), titleY - 5, (int)(width*0.9f), (int)(height * 0.15f), 0x70151515);
        RenderHandler.drawText("Settings", titleX, titleY, scale, true, 0x3e91b5);
        
        scrollbar.renderScrollBar();
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
    	
	    utilityCategory(checkSize);
	    fishingCategory(checkSize);
	    MeskyCategory(checkSize);
	    
	    if(openedCategory > categories.size()-1) {
	    	openedCategory = 0;
	    }
	}
	
	private void utilityCategory(int checkSize) {
		List<Object> timerSub = new ArrayList<>();
        timerSub.add(new SettingButton(4, checkSize, "Bonzo's Mask timer", SettingsConfig.BonzoTimer));
        timerSub.add(new SettingButton(5, checkSize, "Spirit Mask timer", SettingsConfig.SpiritTimer));
        
	    List<Object> utilitySub = new ArrayList<>();
        utilitySub.add(new SettingButton(6, checkSize, "Hide Players", SettingsConfig.HidePlayers));
		utilitySub.add(new SettingButton(7, checkSize, "Coords Detection", SettingsConfig.CoordsDetection));
		utilitySub.add(new SettingButton(8, checkSize, "Block flower weapons from placing", SettingsConfig.BlockFlowerPlacing));
		//utilitySub.add(new SettingButton(8,  checkSize, checkSize, "Nick mention detection", SettingsConfig.NickMentionDetection));
		//utilitySub.add(new SettingButton(8,  checkSize, checkSize, "Anty ghost blocks", SettingsConfig.AntyGhostBlocks));
		
		List<Object> illegalSub = new ArrayList<>();
	    illegalSub.add(new SettingButton(0, checkSize, "Ghost Blocks", SettingsConfig.GhostBlocks));
        List<Object> ghostpickaxeFoldable = new ArrayList<>();
        ghostpickaxeFoldable.add(new SettingSlider(41849, 2*checkSize, checkSize, "Ghost Pickaxe slot", SettingsConfig.GhostPickaxeSlot, 1, 1, 9));
        //ghostpickaxeFoldable.add(new SettingTextField(1,  "Ghost Pickaxe slot", checkSize*2, checkSize, SettingsConfig.GhostPickaxeSlot, 1, true, false));
        illegalSub.add(new FoldableSettingButton(1, checkSize, "Ghost Pickaxe", SettingsConfig.GhostPickaxe, ghostpickaxeFoldable));
        illegalSub.add(new SettingButton(2, checkSize, "Freelook", SettingsConfig.FreeLook));
        
        List<SubCategory> utility = new ArrayList<>();
        
		utility.add(new SubCategory(utilitySub));
		utility.add(new SubCategory("Timers", timerSub));
		utility.add(new SubCategory("Illegal", illegalSub));
		
		categories.add(new Category("Utility", utility));
	}
	
	private void fishingCategory(int checkSize) {
		List<SubCategory> fishing = new ArrayList<>();
		
		List<Object> illegalSub = new ArrayList<>();
		
		List<Object> autoFishFoldable = new ArrayList<>();
        autoFishFoldable.add(new SettingButton(-1, checkSize, "Kill Sea Creatures", SettingsConfig.KillSeaCreatures));
        autoFishFoldable.add(new SettingButton(-1, checkSize, "Auto throw hook", SettingsConfig.AutoThrowHook));
        illegalSub.add(new FoldableSettingButton(3, checkSize, "AutoFish", SettingsConfig.AutoFish, autoFishFoldable));
	
        illegalSub.add(new SettingButton(2, checkSize, "Jawbus Detection", SettingsConfig.JawbusDetection));
        
        // ==================================================================================
        
		List<Object> fishingSub = new ArrayList<>();
		
		List<Object> fishingTimerFoldable = new ArrayList<>();
		fishingTimerFoldable.add(new SettingButton(1, checkSize, "Render timer in 3D", SettingsConfig.FishingTimerIs3d));
		fishingSub.add(new FoldableSettingButton(3, checkSize, "Fishing timer", SettingsConfig.FishingTimer, fishingTimerFoldable));
		
		fishing.add(new SubCategory("Normal", fishingSub));
		fishing.add(new SubCategory("Illegal", illegalSub));
		categories.add(new Category("Fishing", fishing));
	}
	
	private void MeskyCategory(int checkSize) {
		List<SubCategory> mesky = new ArrayList<>();
		
		List<Object> scrollbarSub = new ArrayList<>();
        scrollbarSub.add(new SettingSlider(-1,  5*checkSize, checkSize, "Scrollbar speed", SettingsConfig.ScrollbarSpeed, 1, 1, 100));
        scrollbarSub.add(new SettingSlider(-1,  5*checkSize, checkSize, "Scrollbar smoothness", SettingsConfig.ScrollbarSmoothness, 0.005, 0.005, 0.1));
	
        List<Object> waypointsSub = new ArrayList<>();
        if(SettingsConfig.CoordsDetection.isOn || SettingsConfig.JawbusDetection.isOn) waypointsSub.add(new SettingSlider(2,  5*checkSize, checkSize, "Time for [Mark] waypoint to disappear [s]", SettingsConfig.MarkWaypointTime, 1, 10, 600));
        waypointsSub.add(new SettingSlider(2,  5*checkSize, checkSize, "Time for EntityDetector waypoint to disappear [s]", SettingsConfig.EntityDetectorWaypointLifeTime, 1, 5, 600));
        waypointsSub.add(new SettingSlider(2,  5*checkSize, checkSize, "Touch radius of EntityDetector waypoint", SettingsConfig.EntityDetectorWaypointTouchRadius, 1, 1, 30));
        
		mesky.add(new SubCategory("Scrollbar", scrollbarSub));
		mesky.add(new SubCategory("Waypoints", waypointsSub));
		categories.add(new Category("Mesky", mesky));
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
		for (int i = 0; i < categories.size(); i++) {
			Category category = categories.get(i);
				
			int categoryHeight = (int) (height * 0.075f) - 1;
			int categoryWidth =  (int) (width * 0.2f);;
			float x = width * 0.1f;
			
			
			if (mouseX >= x && mouseX <= x + categoryWidth && mouseY >= category.y && mouseY <= category.y + categoryHeight) {
				openedCategory = i;
			}

		}
		
		float topY = height*0.15f;
		for (SubCategory sub : categories.get(openedCategory).list) {
			if(mouseX < topY) break;
			
			sub.mouseClicked(mouseX, mouseY, mouseButton); // so that buttons will click because mouseClicked only is detected in here so i have to
		}
		
		if(mouseX >= scrollbar.x && mouseX <= scrollbar.x + scrollbar.scrollbarWidth && mouseY >= scrollbar.y && mouseY <= scrollbar.y + scrollbar.scrollbarHeight) {
			scrollbar.updateOffsetToMouseClick(mouseY);
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
		for (SubCategory sub : categories.get(openedCategory).list) {
			sub.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		}
    }
	
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
		for (SubCategory sub : categories.get(openedCategory).list) {
			sub.mouseReleased(mouseX, mouseY, state);
		}
    }
	
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (SubCategory sub : categories.get(openedCategory).list) {
			sub.keyTyped(typedChar, keyCode); // so that buttons will click because mouseClicked only is detected in here so i have to
		}
		
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        int SCROLL_SPEED = height / 50;
        
        if (scroll != 0) {
        	scrollbar.handleMouseInput(scroll);
        }
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
        
        openedCategory = 0;
        ConfigHandler.saveSettings();
        super.onGuiClosed();
    }
}
