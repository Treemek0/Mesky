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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Mesky;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.features.HidePlayers;
import treemek.mesky.features.illegal.JawbusDetector;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.SettingColorPicker;
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
				scrollbar.updateVisibleHeight(categoryHeight);
				scrollbar.updateContentHeight(categoryContentHeight);
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
        
        int warningLength = (int) RenderHandler.getTextWidth(" - May be bannable", scale/3);
        int warningHeight = (int) RenderHandler.getTextHeight(scale/3);
        int warningY = (int) (height*0.15f - warningHeight);
        RenderHandler.drawText(" - May be bannable", (int)(width*0.9f) - warningLength, warningY, scale/3, true, 0xffd42a);
        mc.renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "gui/warning.png"));
		drawModalRectWithCustomSizedTexture((int)(width*0.9f) - warningLength - warningHeight, warningY, 0, 0, warningHeight, warningHeight, warningHeight, warningHeight);
        
        scrollbar.drawScrollBar();
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
	    List<Object> generalSub = new ArrayList<>();
	    generalSub.add(new SettingButton(6, checkSize, "Hide Players", SettingsConfig.HidePlayers));
	    if(Mesky.debug) generalSub.add(new SettingButton(6, checkSize, "Nick detection", SettingsConfig.NickMentionDetection));
	    
	    List<Object> freelookFoldable = new ArrayList<>();
	    freelookFoldable.add(new SettingButton(2, checkSize, "Ignore walkable blocks collision (some mods destroys view)", SettingsConfig.FreeLookIgnoreWalkableBlocksCollision));
	    freelookFoldable.add(new SettingButton(2, checkSize, "Lock camera, unlock rotation", SettingsConfig.FreeRotate));
	    freelookFoldable.add(new SettingButton(2, checkSize, "Block inverted angles", SettingsConfig.FreeLookClampAngles));
	    freelookFoldable.add(new SettingButton(2, checkSize, "Toogle", SettingsConfig.FreeLookToogle));
	    generalSub.add(new FoldableSettingButton(2, checkSize, "Freelook", SettingsConfig.FreeLook, freelookFoldable, true));
	    generalSub.add(new SettingButton(8, checkSize, "Block flower weapons from placing", SettingsConfig.BlockFlowerPlacing));
	   
	    // >>>>
	    
	    List<Object> timerSub = new ArrayList<>();
	    timerSub.add(new SettingButton(4, checkSize, "Bonzo's Mask timer", SettingsConfig.BonzoTimer));
	    timerSub.add(new SettingButton(5, checkSize, "Spirit Mask timer", SettingsConfig.SpiritTimer));

	    // >>>>
	    
	    List<Object> chatSub = new ArrayList<>();
	    List<Object> coordsDetectionFoldable = new ArrayList<>();
	    coordsDetectionFoldable.add(new SettingButton(2, checkSize, "Auto mark coords", SettingsConfig.AutoMarkCoords));
	    chatSub.add(new FoldableSettingButton(7, checkSize, "Coords Detection", SettingsConfig.CoordsDetection, coordsDetectionFoldable));

	    // >>>>
	    
	    List<Object> advancedToolsSub = new ArrayList<>();
	    advancedToolsSub.add(new SettingButton(0, checkSize, "Anty Ghost Blocks", SettingsConfig.AntyGhostBlocks));
	    advancedToolsSub.add(new SettingButton(0, checkSize, "Ghost Block keybind", SettingsConfig.GhostBlocks, true));

	    List<Object> ghostpickaxeFoldable = new ArrayList<>();
	    ghostpickaxeFoldable.add(new SettingSlider(41849, 2 * checkSize, checkSize, "Ghost Pickaxe slot", SettingsConfig.GhostPickaxeSlot, 1, 1, 9));
	    advancedToolsSub.add(new FoldableSettingButton(1, checkSize, "Ghost Pickaxe keybind", SettingsConfig.GhostPickaxe, ghostpickaxeFoldable, true));

	    List<SubCategory> utility = new ArrayList<>();

	    utility.add(new SubCategory("General", generalSub));
	    utility.add(new SubCategory("Timers", timerSub));
	    utility.add(new SubCategory("Chat detection", chatSub)); 
	    utility.add(new SubCategory("Advanced Tools", advancedToolsSub));

	    categories.add(new Category("Utility", utility));
	}

	
	private void fishingCategory(int checkSize) {
		List<SubCategory> fishing = new ArrayList<>();
		
		List<Object> fishingSub = new ArrayList<>();
		
		List<Object> fishingTimerFoldable = new ArrayList<>();
		List<Object> fishingTimer3dFoldable = new ArrayList<>();
		fishingTimer3dFoldable.add(new SettingColorPicker(1, checkSize, "Timer background color", SettingsConfig.FishingTimer3dBackgroundColor));
		fishingTimer3dFoldable.add(new SettingColorPicker(1, checkSize, "Timer text color", SettingsConfig.FishingTimer3dColor));
		fishingTimer3dFoldable.add(new SettingSlider(1, 2*checkSize, checkSize, "Timer scale", SettingsConfig.FishingTimer3dScale, 0.1f, 0.5f, 5));
		fishingTimer3dFoldable.add(new SettingSlider(1, 2*checkSize, checkSize, "Timer y position", SettingsConfig.FishingTimer3dY, 0.1f, 0, 5));
		fishingTimer3dFoldable.add(new SettingButton(1, checkSize, "Render bobber image", SettingsConfig.FishingTimer3dRenderImage));
		fishingTimerFoldable.add(new FoldableSettingButton(1, checkSize, "Render timer in 3D", SettingsConfig.FishingTimerIs3d, fishingTimer3dFoldable));
		fishingSub.add(new FoldableSettingButton(3, checkSize, "Fishing timer", SettingsConfig.FishingTimer, fishingTimerFoldable));
		fishingSub.add(new SettingButton(0, checkSize, "Fishing festival shark counter", SettingsConfig.SharkCounter));
		
		List<Object> seaCreaturesNotificationFoldable = new ArrayList<>();
		seaCreaturesNotificationFoldable.add(new SettingButton(0, checkSize, "Water Hydra", SettingsConfig.WaterHydraNotification));
		seaCreaturesNotificationFoldable.add(new SettingButton(0, checkSize, "Sea Emperor", SettingsConfig.SeaEmperorNotification));
		seaCreaturesNotificationFoldable.add(new SettingButton(0, checkSize, "Phantom Fisher", SettingsConfig.PhantomFisherNotification));
		seaCreaturesNotificationFoldable.add(new SettingButton(0, checkSize, "Grim Reaper", SettingsConfig.GrimReaperNotification));
		seaCreaturesNotificationFoldable.add(new SettingButton(0, checkSize, "Abyssal Miner", SettingsConfig.AbyssalMinerNotification));
		seaCreaturesNotificationFoldable.add(new SettingButton(0, checkSize, "Thunder", SettingsConfig.ThunderNotification));
		seaCreaturesNotificationFoldable.add(new SettingButton(0, checkSize, "Plhlegblast", SettingsConfig.PlhlegblastNotification));
		seaCreaturesNotificationFoldable.add(new SettingButton(0, checkSize, "Yeti", SettingsConfig.YetiNotification));
		seaCreaturesNotificationFoldable.add(new SettingButton(0, checkSize, "Reindrake", SettingsConfig.ReindrakeNotification));
		seaCreaturesNotificationFoldable.add(new SettingButton(0, checkSize, "Great White Shark", SettingsConfig.GreatSharkNotification));
		fishingSub.add(new FoldableSettingButton(0, checkSize, "Notify party about your " + EnumChatFormatting.GOLD + "LEGENDARY" + EnumChatFormatting.WHITE + "+" + EnumChatFormatting.RESET + " fishing catches", SettingsConfig.LegendarySeaCreaturesNotification, seaCreaturesNotificationFoldable));
		
        // ==================================================================================
		
		List<Object> illegalSub = new ArrayList<>();
		
		List<Object> autoFishFoldable = new ArrayList<>();
        autoFishFoldable.add(new SettingButton(-1, checkSize, "Kill Sea Creatures (with Wither Impact)", SettingsConfig.KillSeaCreatures));
        autoFishFoldable.add(new SettingButton(-1, checkSize, "Auto throw hook", SettingsConfig.AutoThrowHook));
        autoFishFoldable.add(new SettingButton(-1, checkSize, "AntyAfk (bugged in water)", SettingsConfig.AutoFishAntyAfk));
        illegalSub.add(new FoldableSettingButton(3, checkSize, "AutoFish", SettingsConfig.AutoFish, autoFishFoldable, true));
	
        List<Object> jawbusFoldable = new ArrayList<>();
        jawbusFoldable.add(new SettingButton(-1, checkSize, "Notify party with coords", SettingsConfig.JawbusNotifyParty));
        jawbusFoldable.add(new SettingButton(-1, checkSize, "Jawbus coords and waypoint", SettingsConfig.JawbusDetectionWaypoint, true));
        jawbusFoldable.add(new SettingButton(-1, checkSize, "Player death from jawbus detection", SettingsConfig.JawbusPlayerDeathDetection, true));
        illegalSub.add(new FoldableSettingButton(2, checkSize, "Jawbus Detection", SettingsConfig.JawbusDetection, jawbusFoldable));

        // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        
		fishing.add(new SubCategory("Fishing Tools", fishingSub));
		fishing.add(new SubCategory("Advanced Fishing", illegalSub));
		categories.add(new Category("Fishing", fishing));
	}
	
	private void MeskyCategory(int checkSize) {
		List<SubCategory> mesky = new ArrayList<>();
		
		List<Object> scrollbarSub = new ArrayList<>();
        scrollbarSub.add(new SettingSlider(-1,  5*checkSize, checkSize, "Scrollbar speed", SettingsConfig.ScrollbarSpeed, 1, 1, 500));
        scrollbarSub.add(new SettingSlider(-1,  5*checkSize, checkSize, "Scrollbar smoothness", SettingsConfig.ScrollbarSmoothness, 0.005, 0.005, 0.2));
	
        List<Object> waypointsSub = new ArrayList<>();
        if(SettingsConfig.CoordsDetection.isOn || SettingsConfig.JawbusDetection.isOn) {
			waypointsSub.add(new SettingSlider(2,  5*checkSize, checkSize, "Time for [Mark] waypoint to disappear [s]", SettingsConfig.MarkWaypointTime, 1, 10, 600));
        	waypointsSub.add(new SettingSlider(2,  5*checkSize, checkSize, "Touch radius for [Mark] waypoint", SettingsConfig.MarkWaypointRadius, 0.5f, 0, 15));
        }
        waypointsSub.add(new SettingSlider(2,  5*checkSize, checkSize, "Time for EntityDetector waypoint to disappear [s]", SettingsConfig.EntityDetectorWaypointLifeTime, 1, 5, 600));
        waypointsSub.add(new SettingSlider(2,  5*checkSize, checkSize, "Touch radius of EntityDetector waypoint", SettingsConfig.EntityDetectorWaypointTouchRadius, 1, 1, 30));
        
		mesky.add(new SubCategory("Scrollbar", scrollbarSub));
		mesky.add(new SubCategory("Waypoints", waypointsSub));
		categories.add(new Category("Mesky", mesky));
	}
	
    public static void buttonClicked(GuiButton button) {
		Setting setting = null;
		if(button instanceof SettingButton){
			setting = ((SettingButton)button).setting;
		}
		
		if(button instanceof FoldableSettingButton){
			setting = ((FoldableSettingButton)button).setting;
		}
		
		if(setting != null) {
        	if(setting == SettingsConfig.GhostBlocks) {
            	SettingsConfig.GhostPickaxe.isOn = false;
        	}
        	if(setting == SettingsConfig.GhostPickaxe) {
            	SettingsConfig.GhostBlocks.isOn = false;
        	}
        	if(setting == SettingsConfig.HidePlayers) {
            	HidePlayers.resetPlayersSize();
        	}
        	
        	if(setting == SettingsConfig.JawbusDetectionWaypoint) {
        		JawbusDetector.detectedJawbuses.clear();
        	}
		}
    }
	
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (int i = 0; i < categories.size(); i++) {
			Category category = categories.get(i);
				
			int categoryHeight = (int) (height * 0.075f) - 1;
			int categoryWidth =  (int) (width * 0.2f);
			float x = width * 0.1f;
			
			
			if (mouseX >= x && mouseX <= x + categoryWidth && mouseY >= category.y && mouseY <= category.y + categoryHeight) {
				if(openedCategory == i) {
					category.animationForText.reset();
					category.animationForText.setEnabled(true);
				}
				
				openedCategory = i;
				scrollbar.resetOffset();
				
			}
		}
		
		float topY = height*0.15f;
		for (SubCategory sub : categories.get(openedCategory).list) {
			if(mouseX < topY) break;
			
			sub.mouseClicked(mouseX, mouseY, mouseButton); // so that buttons will click because mouseClicked only is detected in here so i have to
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
    	scrollbar.handleMouseInput();
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
