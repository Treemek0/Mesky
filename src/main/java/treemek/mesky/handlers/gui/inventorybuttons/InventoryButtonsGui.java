package treemek.mesky.handlers.gui.inventorybuttons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.HelpButton;
import treemek.mesky.utils.InventoryButtons;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.InventoryButtons.InventoryButton;
import treemek.mesky.utils.InventoryButtons.Parameters;

public class InventoryButtonsGui extends InventoryEffectRenderer{
	public InventoryButtonsGui() {
		super(Minecraft.getMinecraft().thePlayer.inventoryContainer);
        this.allowUserInput = true;
	}

	private class PressedButton {
		long pressedTime = 0;
		InventoryButtonElement pressedButton = null;
		
		public PressedButton(InventoryButtonElement btn) {
			pressedButton = btn;
			pressedTime = System.currentTimeMillis();
		}
	}
	
	PressedButton pressed = null;
	InventoryButtonElement holdingButton = null;
	int holdingX, holdingY = 0;
	InventoryButtonElement opened = null;
	List<InventoryButtonElement> elements = new ArrayList<>();
	
	HelpButton help;
	String[] helpLines = new String[] {
		    EnumChatFormatting.BLUE + "Inventory buttons nbt tags:",
		    EnumChatFormatting.GOLD + "When using spawn_egg / potion, you can use:",
		    EnumChatFormatting.LIGHT_PURPLE + "[type=...] " + EnumChatFormatting.RESET + "to specify which type you want (you can use their ID or names)",
		    "",
		    EnumChatFormatting.GOLD + "You can also color armor/blocks with:",
		    EnumChatFormatting.LIGHT_PURPLE + "[hex=??????] " + EnumChatFormatting.RESET + "and give correct HEX value",
		    "",
		    EnumChatFormatting.GOLD + "When using skull, you can set its skin:",
		    EnumChatFormatting.LIGHT_PURPLE + "[skin=" + EnumChatFormatting.GREEN + "<PLAYER_NICK>" + EnumChatFormatting.LIGHT_PURPLE + "] "
		};
	
	/** The old x position of the mouse pointer */
    private float oldMouseX;
    /** The old y position of the mouse pointer */
    private float oldMouseY;

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.updateActivePotionEffects();
        
        for (InventoryButtonElement inventoryButtonElement : elements) {
			inventoryButtonElement.updateCounters();
		}
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.elements.clear();
        
        for (int i = 0; i <= 55; i++) {
			
			if(InventoryButtons.buttons.containsKey(i)) {
				InventoryButton invButton = InventoryButtons.buttons.get(i);
				Parameters p = InventoryButtons.getInventoryButtonPosition(i);
				
				elements.add(new InventoryButtonElement(invButton, p.x, p.y, p.width, i));
			}else {
				Parameters p = InventoryButtons.getInventoryButtonPosition(i);
				
				elements.add(new InventoryButtonElement(null, p.x, p.y, p.width, i));
			}
        }
        
        help = new HelpButton(-1, 0, InventoryButtons.buttonSize*2, InventoryButtons.buttonSize, InventoryButtons.buttonSize, helpLines);
        
        super.initGui();
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        // nothing because its just crafting text
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
        
        int mX = (opened != null && opened.isHoveredTab(mouseX, mouseY))?-1:mouseX;
        int mY = (opened != null && opened.isHoveredTab(mouseX, mouseY))?-1:mouseY;
        
        for (InventoryButtonElement inventoryButtonElement : elements) {
        	if(inventoryButtonElement == holdingButton) continue;
			inventoryButtonElement.render(mX, mY, holdingButton == null);
			
			if(inventoryButtonElement.isOpened()) {
				opened = inventoryButtonElement;
			}
		}
        
        help.drawButton(mc, mouseX, mouseY);
        
        if(help.shouldShowTooltip()) {
        	help.drawToolkit(mouseX, mouseY);
        }
        
        if(opened != null) {
        	opened.renderOpened(mouseX, mouseY);
        }
        
        if(holdingButton != null) {
        	GlStateManager.pushMatrix();
        	GlStateManager.translate(0, 0, 150);
        	holdingButton.renderHolding(holdingX, holdingY);
        	GlStateManager.popMatrix();
        }
    	
    	GL11.glColor4f(1, 1, 1, 1);
    	
    	if(pressed != null && pressed.pressedButton.getButton() != null) {
	    	if(System.currentTimeMillis() - pressed.pressedTime > 500) {
	    		holdingButton = pressed.pressedButton;
	    		pressed.pressedButton.close();
	    		pressed = null;
	    	}else {
	    		holdingButton = null;
	    	}
    	}
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(inventoryBackground);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.mc.thePlayer);
    }

    /**
     * Draws player. Args: xPos, yPos, scale, mouseX, mouseY, entityLiving
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
        }

        if (button.id == 1)
        {
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
        }
    }
    
    int offsetX = 0;
    int offsetY = 0;
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	InventoryButtonElement hovered = null;
    	
    	for (InventoryButtonElement inventoryButtonElement : elements) {
    		if(inventoryButtonElement.isHovered(mouseX, mouseY)) {
    			hovered = inventoryButtonElement;
    		}else {
				if(inventoryButtonElement.isHoveredTab(mouseX, mouseY)) {
					if(inventoryButtonElement.interactWithTab(mouseX, mouseY, 0)) { // delete
						inventoryButtonElement.setButton(null);
						inventoryButtonElement.close();
					};
					
					return;
				}else {
					inventoryButtonElement.close();
					inventoryButtonElement.loseFocus();
				}
    		}
    	}
    	
    	if(hovered != null) {
    		pressed = new PressedButton(hovered);
    		
    		offsetX = (mouseX- InventoryButtons.guiLeft) - hovered.x;
    		offsetY = (mouseY- InventoryButtons.guiTop) - hovered.y;
    		
    		holdingX = mouseX - InventoryButtons.guiLeft - offsetX;
    		holdingY = mouseY - InventoryButtons.guiTop - offsetY;
    	}
    }
    
    private InventoryButtonElement getHoveredButton(int mouseX, int mouseY) {
    	InventoryButtonElement hovered = null;
    	
    	for (InventoryButtonElement inventoryButtonElement : elements) {
    		if(inventoryButtonElement.isHovered(mouseX, mouseY)) hovered = inventoryButtonElement;
    		if(inventoryButtonElement.isOpened()) return null;
    	}
    	
    	return hovered;
	}

	@Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    	for (InventoryButtonElement inventoryButtonElement : elements) {
			if(inventoryButtonElement.keyTyped(typedChar, keyCode)) return;
		}
    	
    	super.keyTyped(typedChar, keyCode);
    }
    
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    	if(holdingButton == null) {
	    	InventoryButtonElement hovered = getHoveredButton(mouseX, mouseY);
	    	if(pressed != null) {
		    	if(hovered == null || hovered != pressed.pressedButton) {
		    		pressed = null;
		    	}
	    	}else {
	    		if(hovered != null) pressed = new PressedButton(hovered);
	    	}
    	}else {
    		holdingX = mouseX - InventoryButtons.guiLeft - offsetX;
    		holdingY = mouseY - InventoryButtons.guiTop - offsetY;
    	}
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
    	if(pressed != null) {
	    	if(System.currentTimeMillis() - pressed.pressedTime < 500 || pressed.pressedButton.getButton() == null) {
		    	for (InventoryButtonElement inventoryButtonElement : elements) {
		    		boolean buttonHovered = inventoryButtonElement.isHovered(mouseX, mouseY);
		    		boolean tabHovered = inventoryButtonElement.isHoveredTab(mouseX, mouseY);
		    		
					if(buttonHovered) {
						if(inventoryButtonElement.getButton() == null) {
							inventoryButtonElement.setButton(new InventoryButton("stone", 1, "/"));
						}
						
						if(inventoryButtonElement.isOpened()) {
							inventoryButtonElement.close();
						}else {
							inventoryButtonElement.open();
						}
					}
				}
	    	}
    	}else {
    		if(holdingButton != null) {
    			InventoryButtonElement hovered = getHoveredButton(mouseX, mouseY);
    			
    			if(hovered == null) {
    				holdingButton = null;
    				return;
    			}
    			
    			// swap buttons
    			int hoveredId = hovered.id;
    			hovered.changeID(holdingButton.id);
    			holdingButton.changeID(hoveredId);
    		}
    	}
    	
    	pressed = null;
    	holdingButton = null;
    	
    	opened = null;
    	for (InventoryButtonElement inventoryButtonElement : elements) {
			if(inventoryButtonElement.isOpened()) opened = inventoryButtonElement;
		}
    }
    
    @Override
    public void onGuiClosed() {
    	save();
    	super.onGuiClosed();
    }
    
    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
    	save();
    	super.onResize(mcIn, w, h);
    }
    
    private void save() {
    	Map<Integer, InventoryButton> map = new LinkedHashMap<>();
    	for (InventoryButtonElement inventoryButtonElement : elements) {
    		if(inventoryButtonElement.getButton() != null) {
    			map.put(inventoryButtonElement.id, inventoryButtonElement.getButton());
    		}
		}
    	
    	InventoryButtons.buttons = map;
    	ConfigHandler.SaveInventoryButtons(InventoryButtons.buttons);
    }
}
