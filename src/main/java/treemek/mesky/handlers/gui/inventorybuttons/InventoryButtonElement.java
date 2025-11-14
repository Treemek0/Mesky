package treemek.mesky.handlers.gui.inventorybuttons;

import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.elements.buttons.ListBox;
import treemek.mesky.handlers.gui.elements.buttons.ListBox.Option;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.InventoryButtons;
import treemek.mesky.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.utils.InventoryButtons.InventoryButton;
import treemek.mesky.utils.InventoryButtons.Parameters;

public class InventoryButtonElement {
		public ResourceLocation empty = new ResourceLocation(Reference.MODID, "gui/add.png");
		private InventoryButton button;
		
		private TextField itemField;
		private TextField commandField;
		private ListBox background;
		private DeleteButton delete;
		
		public int id;
		
		int textFieldHeight = 15;
		
		int left = 0, right = 0, top = 0, bottom = 0;
		
		int x, y, size = 0;
		int openedWidth = 100; 
		int openedHeight = 80;
		
		public transient boolean wasHovered = false;
		public transient long lastStartedHovering = 0;
		private boolean opened = false;
		
		public InventoryButtonElement(InventoryButton button, int x, int y, int size, int id) {
			this.x = x;
			this.y = y;
			this.size = size;
			this.button = button;
			this.id = id;
			
			itemField = new TextField(0, 0, 0, openedWidth - 4, textFieldHeight);
			itemField.setCanLoseFocus(true);
			if(button != null) setItem(button.itemString);
			
			commandField = new TextField(0, 0, 0, openedWidth - 4, textFieldHeight);
			commandField.setCanLoseFocus(true);
			if(button != null) setCommand(button.command);
			
			int bg_id = (button == null)?1:button.imageBackgroundID;
			background = new ListBox(0, 0, 0, openedWidth - 4, textFieldHeight, "", new ArrayList<>(Arrays.asList(new Option("Transparent", "0"), new Option("Opaque", "1"), new Option("Darker", "2"), new Option("Half transparent", "3"), new Option("Inventory slot", "4"), new Option("Borders", "5"))), String.valueOf(bg_id));
			
			delete = new DeleteButton(0, 0, 0, 10, 10, "");
		}
		
		public void changeID(int ID) {
			Parameters p = InventoryButtons.getInventoryButtonPosition(ID);
			
			x = p.x;
			y = p.y;
			size = p.width;
			this.id = ID;
		}
		
		private void changeElementsPosition(int tabX, int tabY) {
			itemField.xPosition = tabX + 2;
			itemField.yPosition = tabY + 2;
			
			commandField.xPosition = tabX + 2;
			commandField.yPosition = tabY + 4 + textFieldHeight;
			
			background.xPosition = tabX + 2;
			background.yPosition = tabY + 2 + (2 + textFieldHeight)*2;
			
			delete.xPosition = tabX + openedWidth - 12;
			delete.yPosition = tabY + openedHeight - 12;
		}

		public void setItem(String item) {
			if(item != null) {
				itemField.setText(item);
				button.itemString = item;
			}
		}
		
		public void setCommand(String command) {
			if(command != null) {
				commandField.setText(command);
				button.command = command;
			}
		}
		
		public void setButton(InventoryButton btn) {
			button = btn;
			if(btn != null) {
				setItem(btn.itemString);
				setCommand(btn.command);
			}
		}
		
		
		
		public InventoryButton getButton() {
			return button;
		}
		
		public boolean isHovered(int mouseX, int mouseY) {
			int btnX = InventoryButtons.guiLeft + x;
			int btnY = InventoryButtons.guiTop + y;
			
			return (mouseX > btnX && mouseX < btnX + size && mouseY > btnY && mouseY < btnY + size);
		}
		
		public boolean isHoveredTab(int mouseX, int mouseY) {
			boolean isHovered = (isOpened() && mouseX > InventoryButtons.guiLeft + left && mouseX < InventoryButtons.guiLeft + right && mouseY > InventoryButtons.guiTop + top && mouseY < InventoryButtons.guiTop + bottom);
			boolean isMouseOverListBox = false;
			
			if(background.isOpened()) {
				isMouseOverListBox = (mouseX > background.xPosition && mouseX < background.xPosition + background.width && mouseY > background.yPosition && mouseY < background.endY);
			}
			
			return isHovered || isMouseOverListBox;
		}
		
		public void render(int mouseX, int mouseY, boolean drawPlus) {
			boolean isHovered = isHovered(mouseX, mouseY);
			
			GlStateManager.pushMatrix();
            GlStateManager.translate(InventoryButtons.guiLeft, InventoryButtons.guiTop, 0);
			
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GL11.glColor4f(1, 1, 1, 1);
            
            if(button != null) {
            	button.imageBackgroundID = Integer.parseInt(background.getCurrentArgument());
            	
            	InventoryButtons.renderButton(button, new Parameters(x,y,size,size), isHovered);
            }else {
            	if(drawPlus) {
            		InventoryButtons.drawBackground(1, new Parameters(x,y,size,size), isHovered);
            		
            		RenderHandler.drawImage(x, y, size, size, empty);
            	}else {
            		InventoryButtons.drawBackground(1, new Parameters(x,y,size,size), isHovered);
            		
            		RenderHandler.drawRect(x + size/2 - 1, y + size/2 - 1, x + size/2 + 1, y + size/2 + 1, 0xFFFFFFFF);
            	}
            }
            
			GlStateManager.enableLighting();
			RenderHelper.disableStandardItemLighting();
	        GlStateManager.depthMask(true);
	        GlStateManager.popMatrix();
		}
		
		public void renderHolding(int X, int Y) {
			GlStateManager.pushMatrix();
            GlStateManager.translate(InventoryButtons.guiLeft, InventoryButtons.guiTop, 0);
			
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GL11.glColor4f(1, 1, 1, 1);
            
            if(button != null) {
            	InventoryButtons.renderButton(button, new Parameters(X,Y,size,size), false);
            }
            
			GlStateManager.enableLighting();
			RenderHelper.disableStandardItemLighting();
	        GlStateManager.depthMask(true);
	        GlStateManager.popMatrix();
		}
		
		public void renderOpened(int mouseX, int mouseY) {
			GlStateManager.pushMatrix();
            GlStateManager.translate(InventoryButtons.guiLeft, InventoryButtons.guiTop, 0);
            
            int screenX = InventoryButtons.guiLeft + x;
            int screenY = InventoryButtons.guiTop + y;
            
            int rightDistance = Minecraft.getMinecraft().currentScreen.width - (screenX + size + openedWidth);
            int leftDistance = screenX - openedWidth;
            int bottomDistance = Minecraft.getMinecraft().currentScreen.height - (screenY + openedHeight) + size;
            int topDistance = screenY - openedHeight;
            
            if(rightDistance > 0) {
        		left = x + size;
        		right = x + size + openedWidth;
            }else {
        		left = x - openedWidth;
        		right = x;
            }
            
            if(bottomDistance > 0) {
        		bottom = y + openedHeight;
        		top = y;
            }else {
        		bottom = y + size;
        		top = y + size - openedHeight;
            }
            
            changeElementsPosition(InventoryButtons.guiLeft + left, InventoryButtons.guiTop + top);
            RenderHandler.drawRectWithFrame(left, top, right, bottom, 0xFF202020, 1);
            
			GlStateManager.popMatrix();
			
			commandField.drawTextBox();
			itemField.drawTextBox();
			delete.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
			
			background.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
		}
		
		public void open() {
			this.opened = true;
		}

		public void close() {
			this.opened = false;
			
			background.closeList();
		}
		
		public boolean isOpened() {
			return this.opened;
		}

		public boolean interactWithTab(int mouseX, int mouseY, int buttonId) {
			itemField.mouseClicked(mouseX, mouseY, buttonId);
			commandField.mouseClicked(mouseX, mouseY, buttonId);
			background.mousePressed(mouseX, mouseY, buttonId);
			
			if(delete.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) return true;
			return false;
		}
		
		public void loseFocus() {
			itemField.setFocused(false);
			commandField.setFocused(false);
		}
		
		public boolean keyTyped(char typedChar, int keyCode) {
			if(itemField.textboxKeyTyped(typedChar, keyCode)) {
				button.itemString = itemField.getText();
				button.updateItem();
				return true;
			}
			if(commandField.textboxKeyTyped(typedChar, keyCode)) {
				if(!commandField.getText().startsWith("/")) {
					int cursorPos = commandField.getCursorPosition();
					commandField.setText("/" + commandField.getText());
					commandField.setCursorPosition(cursorPos+1);
				}
				
				button.command = commandField.getText();
				return true;
			}
			
			return false;
		}
		
		public void updateCounters() {
			commandField.updateCursorCounter();
			itemField.updateCursorCounter();
		}
}
