package treemek.mesky.handlers.gui.cosmetics;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.CosmeticCheckButton;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
import treemek.mesky.handlers.gui.elements.buttons.SettingButton;

public class CosmeticsGui extends GuiScreen {
	 
	ScrollBar scrollbar = new ScrollBar();
	private SettingButton allowFallingAnimation;
	private MeskyButton customizeCustomCape;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
        
		drawRect(0, 0, width, height, new Color(33, 33, 33,255).getRGB());
		
        double scale = 3;
        int textLength = mc.fontRendererObj.getStringWidth("Cosmetics");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) ((height / 4) / scale);

        RenderHandler.drawText("Cosmetics", titleX, titleY, scale, true, 0x3e91b5);
        
        int previewX = (int)(width / 4);
        
        scrollbar.updateScrollBar((int)Math.min(20, (width * 0.025)), (height - (height / 3) - 10), (int)(width * 0.95), height/3);
        updateYPosition();
        scrollbar.renderScrollBar();
        
        int screenHeight = Display.getHeight(); // something with GL11.glViewport or someshit that it has different sizes
		int screenWidth = Display.getWidth();
		int bottomOfText = (int) ((float)((float)(titleY + (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * scale))/height) * screenHeight);
		int topOfTheRectFromBottom = screenHeight - bottomOfText;
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(0, 0, screenWidth, topOfTheRectFromBottom);
	    super.drawScreen(mouseX, mouseY, partialTicks);
	    
	    if(CosmeticHandler.PetType.number != 0) {
	    	allowFallingAnimation.drawButton(mc, allowFallingAnimation.xPosition, allowFallingAnimation.yPosition);
	    }
	    if(CosmeticHandler.CapeType.number == 5) customizeCustomCape.drawButton(mc, mouseX, mouseY);
	    
	    GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    buttonList.clear();
	    
	    int checkSize = ((height / 25) < 12)?12:(height / 25);
        
        int checkX = 6;
        int previewSize = checkSize*4;
        int wingsY = height / 4;
        int hatY = wingsY + previewSize + 20;
        int petsY = hatY + previewSize + 20;
        int capesY = petsY + previewSize + 20;
        
        
        this.buttonList.add(new CosmeticCheckButton(0, checkX, wingsY, previewSize, previewSize, "Dragon Wings", CosmeticHandler.WingsType, 1, new ResourceLocation(Reference.MODID, "textures/fireWings_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(1, checkX + ((previewSize+5)), wingsY, previewSize, previewSize, "Angel Wings", CosmeticHandler.WingsType, 2, new ResourceLocation(Reference.MODID, "textures/angelWings_preview.png"), checkSize));
        
        this.buttonList.add(new CosmeticCheckButton(11, checkX, hatY, previewSize, previewSize, "Gentelmen Hat", CosmeticHandler.HatType, 1, new ResourceLocation(Reference.MODID, "textures/gentelmenHat_preview.png"), checkSize));
        
        this.buttonList.add(new CosmeticCheckButton(21, checkX, petsY, previewSize, previewSize, "CalicoCat", CosmeticHandler.PetType, 1, new ResourceLocation(Reference.MODID, "textures/cat_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(22, checkX + ((previewSize+5)), petsY, previewSize, previewSize, "GrayCat", CosmeticHandler.PetType, 2, new ResourceLocation(Reference.MODID, "textures/grayCat_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(23, checkX + ((previewSize+5)*2), petsY, previewSize, previewSize, "BlackCat", CosmeticHandler.PetType, 3, new ResourceLocation(Reference.MODID, "textures/blackCat_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(24, checkX + ((previewSize+5)*3), petsY, previewSize, previewSize, "RudyCat", CosmeticHandler.PetType, 4, new ResourceLocation(Reference.MODID, "textures/rudyCat_preview.png"), checkSize));
        allowFallingAnimation = new SettingButton(20, checkSize, checkX + ((previewSize+5)*4), petsY + previewSize/2 - checkSize/2, "Allow falling animation", CosmeticHandler.AllowCatFallingAnimation);
        
        this.buttonList.add(new CosmeticCheckButton(31, checkX, capesY, previewSize, previewSize, "Cape", CosmeticHandler.CapeType, 1, new ResourceLocation(Reference.MODID, "textures/golden_cape_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(32, checkX + ((previewSize+5)), capesY, previewSize, previewSize, "Cape", CosmeticHandler.CapeType, 2, new ResourceLocation(Reference.MODID, "textures/bagno_cape_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(33, checkX + ((previewSize+5)*2), capesY, previewSize, previewSize, "Cape", CosmeticHandler.CapeType, 3, new ResourceLocation(Reference.MODID, "textures/cross_cape_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(34, checkX + ((previewSize+5)*3), capesY, previewSize, previewSize, "Cape", CosmeticHandler.CapeType, 4, new ResourceLocation(Reference.MODID, "textures/bee_cape_preview.png"), checkSize));
        this.buttonList.add(new CosmeticCheckButton(35, checkX + ((previewSize+5)*4), capesY, previewSize, previewSize, "Cape", CosmeticHandler.CapeType, 5, new ResourceLocation(Reference.MODID, "textures/custom_cape_preview.png"), checkSize));
        customizeCustomCape = new MeskyButton(36, checkX + ((previewSize+5)*4), capesY + previewSize - previewSize/5, previewSize, previewSize/5, "Customize");
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 36) { // custom cape
			GuiHandler.GuiType = new CustomCapeGui();
		}
		
		super.actionPerformed(button);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(mouseX >= scrollbar.x && mouseX <= scrollbar.x + scrollbar.scrollbarWidth && mouseY >= scrollbar.y && mouseY <= scrollbar.y + scrollbar.scrollbarHeight) {
			scrollbar.updateOffsetToMouseClick(mouseY);
		}
		
		if(CosmeticHandler.PetType.number != 0) {
			allowFallingAnimation.mousePressed(mc, mouseX, mouseY);
		}
		
		if(CosmeticHandler.CapeType.number == 5 && customizeCustomCape.mousePressed(mc, mouseX, mouseY)) {
			actionPerformed(customizeCustomCape);
			return;
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        
        if (scroll != 0) {
        	scrollbar.handleMouseInput(scroll);
        }
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private void updateYPosition(){
		int checkSize = ((height / 25) < 12)?12:(height / 25);
		int previewSize = checkSize*4;
        int wingsY = height / 4;
        int hatY = wingsY + previewSize + 20;
        int petsY = hatY + previewSize + 20;
        int capesY = petsY + previewSize + 20;
        
		scrollbar.updateMaxBottomScroll((capesY + previewSize) - height);
		int ScrollOffset = scrollbar.getOffset();
		
		int positionY = (int) (height / 3 + ScrollOffset);
		
		allowFallingAnimation.yPosition = petsY + previewSize/2 - checkSize/2 + ScrollOffset;
		customizeCustomCape.yPosition = capesY + previewSize - previewSize/5 + ScrollOffset;
		
		for (GuiButton guibutton : buttonList) {
			CosmeticCheckButton button = (CosmeticCheckButton) guibutton;
			if(button.setting == CosmeticHandler.WingsType) {
				button.yPosition = wingsY + ScrollOffset;
				continue;
			}
			
			if(button.setting == CosmeticHandler.HatType) {
				button.yPosition = hatY + ScrollOffset;
				continue;
			}
			
			if(button.setting == CosmeticHandler.PetType) {
				button.yPosition = petsY + ScrollOffset;
				continue;
			}
			
			if(button.setting == CosmeticHandler.CapeType) {
				button.yPosition = capesY + ScrollOffset;
				continue;
			}
		}
	}
}
