package treemek.mesky.mixins;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
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
import treemek.mesky.handlers.soundHandler.SoundsHandler;
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
	            
	            RenderHandler.drawImage(x, y, 16, 16, LockSlot.connected_slot);
	            
	            Slot hovered = LockSlot.hoveredSlot;
	            if (hovered != null && LockSlot.connectedSlots.containsKey(hovered.getSlotIndex())) {
	            	if(hovered.inventory != null && hovered.inventory == Minecraft.getMinecraft().thePlayer.inventory) {
	        		
		        		Slot connected_slot = LockSlot.getSlotByInventoryIndex(gui.inventorySlots, LockSlot.connectedSlots.get(hovered.getSlotIndex()));
		        		if(connected_slot != null && connected_slot == slot) {
		            		if(connected_slot.slotNumber >= gui.inventorySlots.inventorySlots.size() - 40) {
			            		if(connected_slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) {
			            			RenderHandler.drawRect(x, y, x + 16, y + 16, 0x65ef3b1a);
			            			GlStateManager.color(1, 1, 1, 1);
			            		}
		            		}
		        		}
	            	}
	            }
	            
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
    
    	float line_width = 3f;
    	
        if(LockSlot.connectingSlot_1 != null) {
        	Slot connecting_slot = LockSlot.getSlotByInventoryIndex(gui.inventorySlots, LockSlot.connectingSlot_1);
        	RenderHandler.drawLine2D(mouseX, mouseY, connecting_slot.xDisplayPosition + guiLeft + 8, connecting_slot.yDisplayPosition + guiTop + 8, line_width, 0xef3b1a);
        }
    }
    
    @Inject(method = "drawScreen", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V",shift = At.Shift.AFTER))
    private void onDrawBeforeTooltip(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
    	if(!SettingsConfig.LockSlots.isOn) return;

    	GuiContainer gui = (GuiContainer)(Object)this;
    
    	float line_width = 3f;
    	
        Slot hovered = this.getSlotAtPosition(mouseX, mouseY);
        LockSlot.hoveredSlot = hovered;
        
        if(gui.inventorySlots.inventorySlots.size() > 45) return;
        if (hovered != null && LockSlot.connectedSlots.containsKey(hovered.getSlotIndex())) {
    		if(hovered.inventory != Minecraft.getMinecraft().thePlayer.inventory) return;
    		
    		Slot connected_slot = LockSlot.getSlotByInventoryIndex(gui.inventorySlots, LockSlot.connectedSlots.get(hovered.getSlotIndex()));
    		if(connected_slot == null) return;
    		if(connected_slot.slotNumber < gui.inventorySlots.inventorySlots.size() - 40) return;
    		if(connected_slot.inventory != Minecraft.getMinecraft().thePlayer.inventory) return;
    		
    		float center1X = hovered.xDisplayPosition + guiLeft + 8;
    		float center1Y = hovered.yDisplayPosition + guiTop + 8;
    		float center2X = connected_slot.xDisplayPosition + guiLeft + 8;
    		float center2Y = connected_slot.yDisplayPosition + guiTop + 8;
    		float halfSize = 8f;

    		float[] start = clipLineToRect(center1X, center1Y, center2X, center2Y, halfSize);
    		float[] end = clipLineToRect(center2X, center2Y, center1X, center1Y, halfSize);

    		RenderHandler.drawLine2D(start[0], start[1], end[0], end[1], line_width, 0xFFef3b1a);
    		GlStateManager.enableBlend();
    		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
    	}
    }
    
    private static float[] clipLineToRect(float x1, float y1, float x2, float y2, float halfSize) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        float absDX = Math.abs(dx);
        float absDY = Math.abs(dy);

        float scale = (absDX > absDY) ? (halfSize / absDX) : (halfSize / absDY);

        return new float[] {x1 + dx * scale, y1 + dy * scale};
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
            if(!GUI.isShiftKeyDown()) SoundsHandler.playSound("mesky:block", 1, 2);
            ci.cancel();
        }
        
        if(hovered != null && LockSlot.connectedSlots.containsKey(hovered.getSlotIndex()) && GUI.isShiftKeyDown()) {
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

