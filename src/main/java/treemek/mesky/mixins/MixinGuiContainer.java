package treemek.mesky.mixins;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.LockSlot;
import org.spongepowered.asm.mixin.injection.At;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.GUI;
import treemek.mesky.utils.Utils;

@Mixin(GuiContainer.class)
public class MixinGuiContainer {
    @Shadow private int guiLeft;
    @Shadow private int guiTop;
    @Shadow public Container inventorySlots;
    
    @Inject(method = "drawSlot", at = @At("RETURN"))
    private void afterDrawSlot(Slot slot, CallbackInfo ci) {
    	if(!SettingsConfig.LockSlots.isOn) return;
    	
    	GuiContainer gui = (GuiContainer)(Minecraft.getMinecraft().currentScreen);
    	
    	if(slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) {    	
	        if (LockSlot.lockedSlots.containsKey(slot.getSlotIndex())) {
	            int x = slot.xDisplayPosition;
	            int y = slot.yDisplayPosition;
	
	            GlStateManager.pushMatrix();
	            GlStateManager.translate(0, 0, 400);
	            GlStateManager.color(1, 1, 1, 1);
	            GlStateManager.enableAlpha();
	            GlStateManager.enableBlend();
	            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	            GlStateManager.disableLighting();
	            GlStateManager.depthMask(false);
	            RenderHandler.drawImage(x, y, 16, 16, LockSlot.getLockTexture(LockSlot.lockedSlots.get(slot.getSlotIndex())));
	            GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
				GlStateManager.translate(0, 0, -400);
				GlStateManager.popMatrix();
	        }
	        
	        if(slot.getStack() == null && slot.getBackgroundSprite() != null) {
	        	if(gui.inventorySlots.inventorySlots.size() > 45) return;
	        	if (LockSlot.connectedSlots.containsKey(slot.getSlotIndex())) {
		            int x = slot.xDisplayPosition;
		            int y = slot.yDisplayPosition;
		
		            GlStateManager.pushMatrix();
		            GlStateManager.translate(0, 0, 400);
		            GlStateManager.color(1, 1, 1, 1);
		            GlStateManager.enableAlpha();
		            GlStateManager.enableBlend();
		            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		            GlStateManager.disableLighting();
		            GlStateManager.depthMask(false);
		            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		            GlStateManager.enableAlpha();
		            
		            RenderHandler.drawImage(x, y, 16, 16, LockSlot.connected_slot);
		            
		            GlStateManager.depthMask(true);
					GlStateManager.disableBlend();
					GlStateManager.translate(0, 0, -400);
					GlStateManager.popMatrix();
		        }
	        }
    	}
    }
    
    @Inject(method = "drawSlot", at = @At(
    	    value = "INVOKE",
    	    target = "Lnet/minecraft/client/renderer/entity/RenderItem;renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"
    	))
    private void beforeRenderItemOverlay(Slot slot, CallbackInfo ci) {
    	if(!SettingsConfig.LockSlots.isOn) return;
    	GuiContainer gui = (GuiContainer)(Minecraft.getMinecraft().currentScreen);
    	if(gui.inventorySlots.inventorySlots.size() > 45) return;
    	
    	if(slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) {    
	    	if (LockSlot.connectedSlots.containsKey(slot.getSlotIndex())) {
	            int x = slot.xDisplayPosition;
	            int y = slot.yDisplayPosition;
	
	            GlStateManager.pushMatrix();
	            GlStateManager.translate(0, 0, 400);
	            GlStateManager.color(1, 1, 1, 1);
	            GlStateManager.enableAlpha();
	            GlStateManager.enableBlend();
	            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	            GlStateManager.disableLighting();
	            GlStateManager.depthMask(false);
	            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	            GlStateManager.enableAlpha();
	            
	            RenderHandler.drawImage(x, y, 16, 16, LockSlot.connected_slot);
	            
	            GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
				GlStateManager.translate(0, 0, -400);
				GlStateManager.popMatrix();
	        }
    	}
    }
    
    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void onDrawScreenEnd(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
    	if(!SettingsConfig.LockSlots.isOn) return;

    	GuiContainer gui = (GuiContainer)(Object)this;
    	
        if(LockSlot.connectingSlot_1 != null) {
        	Slot connecting_slot = LockSlot.getSlotByInventoryIndex(gui.inventorySlots, LockSlot.connectingSlot_1);
        	RenderHandler.drawLine2D(mouseX, mouseY, connecting_slot.xDisplayPosition + guiLeft + 8, connecting_slot.yDisplayPosition + guiTop + 8, 0xef3b1a);
        }
    	
        Slot hovered = this.getSlotAtPosition(mouseX, mouseY);
        
        if(gui.inventorySlots.inventorySlots.size() > 45) return;
        if (hovered != null && LockSlot.connectedSlots.containsKey(hovered.getSlotIndex())) {
    		if(hovered.inventory != Minecraft.getMinecraft().thePlayer.inventory) return;
    		
    		Slot connected_slot = LockSlot.getSlotByInventoryIndex(gui.inventorySlots, LockSlot.connectedSlots.get(hovered.getSlotIndex()));
    		if(connected_slot == null) return;
    		if(connected_slot.slotNumber < gui.inventorySlots.inventorySlots.size() - 40) return;
    		if(connected_slot.inventory != Minecraft.getMinecraft().thePlayer.inventory) return;
    		
    		float x1 = hovered.xDisplayPosition + guiLeft + 8;
    		float y1 = hovered.yDisplayPosition + guiTop + 8;
    		float x2 = connected_slot.xDisplayPosition + guiLeft + 8;
    		float y2 = connected_slot.yDisplayPosition + guiTop + 8;

    		// Compute direction vector
    		float dx = x2 - x1;
    		float dy = y2 - y1;
    		float len = (float) Math.sqrt(dx * dx + dy * dy);

    		// Normalize and scale
    		float offsetX = (dx / len) * 8f;
    		float offsetY = (dy / len) * 8f;

    		// Adjust endpoints
    		float startX = x1 + offsetX;
    		float startY = y1 + offsetY;
    		float endX = x2 - offsetX;
    		float endY = y2 - offsetY;

    		// Draw line from edge to edge
    		RenderHandler.drawLine2D(startX, startY, endX, endY, 0xef3b1a);
        }
    }
    
    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    private void onCloseGui(CallbackInfo ci) {
    	Object self = (Object) this;
        if (self instanceof GuiInventory && LockSlot.changedLocks) {
            ConfigHandler.saveSlotLocks(LockSlot.lockedSlots, LockSlot.connectedSlots);
            LockSlot.changedLocks = false;
            LockSlot.nothingHappeningForSave = 0;
        }
    }
    
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onClick(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
    	if(!SettingsConfig.LockSlots.isOn) return;
    	
    	GuiContainer gui = (GuiContainer)(Object)this;
        Slot hovered = this.getSlotAtPosition(mouseX, mouseY);
        
        
        if (hovered != null && LockSlot.lockedSlots.containsKey(hovered.getSlotIndex()) && LockSlot.lockedSlots.get(hovered.getSlotIndex())) {
        	if(hovered.inventory != Minecraft.getMinecraft().thePlayer.inventory) return;
            Utils.debug("Click on locked slot blocked: " + hovered.getSlotIndex() + " " + hovered.slotNumber);
            ci.cancel();
        }
        
        if(hovered != null && LockSlot.connectedSlots.containsKey(hovered.getSlotIndex()) && GUI.isShiftKeyDown()) {
        	Utils.debug("size: " + gui.inventorySlots.inventorySlots.size());
        	if(gui.inventorySlots.inventorySlots.size() > 45) return;
        	if(hovered.inventory != Minecraft.getMinecraft().thePlayer.inventory) return;
        	
        	Slot not_hovered = LockSlot.getSlotByInventoryIndex(gui.inventorySlots, LockSlot.connectedSlots.get(hovered.getSlotIndex()));
        	
        	ci.cancel();
        	int hoveredId = hovered.slotNumber;
        	int not_hoveredId = not_hovered.slotNumber;

        	int hotbarIndex_hovered = hoveredId - 36;
        	if (hotbarIndex_hovered >= 0 && hotbarIndex_hovered < 9) {
        		if (not_hovered.slotNumber >= 5 && not_hovered.slotNumber <= 8) {
        		    ItemStack hovered_item = hovered.getStack();
        		    if(hovered_item != null) {
	        		    if (!(hovered_item.getItem() instanceof ItemArmor)) {
	        		        return;
	        		    }
	        		    
	        		    ItemArmor armor = (ItemArmor) hovered_item.getItem();
	        		    int armorType = armor.armorType; // 0=helmet,1=chestplate,2=leggings,3=boots
	        		    if (not_hovered.slotNumber != 5 + armorType) {
	        		        return; // wrong armor piece for this slot
	        		    }
        		    }
        		}

        		
        		Minecraft.getMinecraft().playerController.windowClick(gui.inventorySlots.windowId, not_hoveredId, hotbarIndex_hovered, 2, Minecraft.getMinecraft().thePlayer);
        	}
        	
        	int hotbarIndex_not_hovered = not_hoveredId - 36;
        	if (hotbarIndex_not_hovered >= 0 && hotbarIndex_not_hovered < 9) {
        		if (hovered.slotNumber >= 5 && hovered.slotNumber <= 8) {
        		    ItemStack not_hovered_item = not_hovered.getStack();
        		    if(not_hovered_item != null) {
	        		    if (!(not_hovered_item.getItem() instanceof ItemArmor)) {
	        		        return;
	        		    }
	        		    
	        		    ItemArmor armor = (ItemArmor) not_hovered_item.getItem();
	        		    int armorType = armor.armorType; // 0=helmet,1=chest,2=legs,3=boots
	        		    if (hovered.slotNumber != 5 + armorType) {
	        		        return; // wrong armor piece for this slot
	        		    }
        		    }
        		}
        		
        		Minecraft.getMinecraft().playerController.windowClick(gui.inventorySlots.windowId, hoveredId, hotbarIndex_not_hovered, 2, Minecraft.getMinecraft().thePlayer);
        	}
        }
    }
    
    @Shadow
    protected Slot getSlotAtPosition(int x, int y) {
        return null;
    }
    
    
}

