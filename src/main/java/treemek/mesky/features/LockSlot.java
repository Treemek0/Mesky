package treemek.mesky.features;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.soundHandler.SoundsHandler;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.chat.ChatFilter;

public class LockSlot {

	public static final KeyBinding KEY = new KeyBinding("Lock slot", Keyboard.KEY_L, "Mesky");
	
	public static Map<Integer, Boolean> lockedSlots = new HashMap<>();
	
	public static Map<Integer, Integer> connectedSlots = new LinkedHashMap<>();
	// TODO:
	// .CONTAINSKEY(SLOT) TO DRAW FRAME AROUND IT BECAUSE EVERY SLOT CONNECTED HAS KEY IN IT
	// .CONTAINS & .GET(HOVERED_SLOT) FOR LINE  
	
	public static boolean dropKeyPressed = false;
	
	public static Integer connectingSlot_1 = null;
	// IF NOT NULL DRAW A LINE FROM IT TO MOUSE
	
	long keyPressed = 0;
	Integer keyPressed_slot = null;
	
	public static long nothingHappeningForSave = 0;
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
	    if (event.phase != TickEvent.Phase.END) return;

	    if(Minecraft.getMinecraft().gameSettings.keyBindDrop.isKeyDown()) {
	    	dropKeyPressed = true;
	    }else {
	    	dropKeyPressed = false;
	    }
	    
	    Minecraft mc = Minecraft.getMinecraft();
	    if (!(mc.currentScreen instanceof GuiContainer)) return;
	    
	    if(changedLocks) {
	    	nothingHappeningForSave++;
	    	
	    	if(nothingHappeningForSave > 60) {
	    		ConfigHandler.saveSlotLocks(lockedSlots, connectedSlots);
	    		changedLocks = false;
	    		nothingHappeningForSave = 0;
	    	}
	    }
	    
	    if (Keyboard.isKeyDown(KEY.getKeyCode())) {
    		GuiContainer gui = (GuiContainer) mc.currentScreen;
	        Slot hovered = gui.getSlotUnderMouse();

	        if(gui.inventorySlots.inventorySlots.size() > 45) return;
	        
	        if (hovered != null && hovered.inventory == mc.thePlayer.inventory) {
	        	if(keyPressed < 10 && connectingSlot_1 == null) {
	        		if(keyPressed_slot == null) {
			        	keyPressed_slot = hovered.getSlotIndex();
			    	}else {
			    		if(hovered.getSlotIndex() != keyPressed_slot) {
			    			keyPressed_slot = hovered.getSlotIndex();
			    			keyPressed = 0;
			    		}
			    	}
	        	}
	        	
	    		if(keyPressed >= 10 && connectingSlot_1 == null) {
		        	int slot = hovered.getSlotIndex();
		        	
		        	removeConnectedSlot(slot);
		        	connectingSlot_1 = slot;
		        	SoundsHandler.playSound("mesky:bop", 0.5f, 0.2f);
	    		}
	        }
    		
    		keyPressed++;
	    }else {
	    	if(keyPressed > 10 && connectingSlot_1 != null) {
	    		GuiContainer gui = (GuiContainer) mc.currentScreen;
		        Slot hovered = gui.getSlotUnderMouse();

		        if (hovered != null && hovered.inventory == mc.thePlayer.inventory) {
		        	int slot = hovered.getSlotIndex();
		        	changedLocks = true;
		        	
		        	if(slot == connectingSlot_1 || (connectingSlot_1 < 9 && slot < 9) || (connectingSlot_1 > 8 && slot > 8)) {
		        		connectingSlot_1 = null;
		        		return;
		        	}
		        	
		        	connectedSlots.put(connectingSlot_1, slot);
		        	connectedSlots.put(slot, connectingSlot_1);
		        	
		        	SoundsHandler.playSound("mesky:bop", 1f, 0.7f);
		        	
		        	Utils.debug("Connected: " + connectingSlot_1 + " -> " + slot);
		        	
		        	connectingSlot_1 = null;
		        }else {
		        	connectingSlot_1 = null;
		        }
	    	}
	    	
	    	keyPressed = 0;
	    	keyPressed_slot = null;
	    }
	}
	
	private static void removeConnectedSlot(int slotIndex) {
	    if (connectedSlots.containsKey(slotIndex)) {
	        connectedSlots.remove(slotIndex);


	        List<Integer> toRemove = new ArrayList<>();
	        for (Map.Entry<Integer, Integer> entry : connectedSlots.entrySet()) {
	            if (entry.getValue() == slotIndex) {
	                toRemove.add(entry.getKey());
	            }
	        }

	        for (Integer key : toRemove) {
	            connectedSlots.remove(key);
	        }

	        for (Map.Entry<Integer, Integer> entry : new HashMap<>(connectedSlots).entrySet()) {
	            if (toRemove.contains(entry.getValue())) {
	                Integer from = entry.getValue();
	                Integer to = entry.getKey();
	                connectedSlots.put(from, to);
	                toRemove.remove(from);
	            }
	        }
	    }
	}

	
	public static ResourceLocation getLockTexture(Boolean b) {
		if(!b) {
			return lock;
		}else {
			return lock_red;
		}
	}

	boolean dropPressed = false;
	
	@SubscribeEvent
	public void onKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
	    Minecraft mc = Minecraft.getMinecraft();
	    if (!(mc.currentScreen instanceof GuiContainer)) return;
	    if(!SettingsConfig.LockSlots.isOn) return;
	    
	    
	    int key = Keyboard.getEventKey();
	    if (key == Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode() && Keyboard.getEventKeyState()) { // dropping Q in inv
	        GuiContainer gui = (GuiContainer) mc.currentScreen;
	        Slot hovered = gui.getSlotUnderMouse();
	        
	        if (hovered != null && hovered.inventory == mc.thePlayer.inventory) {
	            int slot = hovered.getSlotIndex();
	            
	            if (lockedSlots.containsKey(slot)) {
	            	if(!dropPressed) {
	            		ChatComponentText dropMessage = new ChatComponentText(EnumChatFormatting.RED + "[Mesky] \u26A0 You cannot drop this item. Unlock the slot first.");
	            		ChatStyle style = new ChatStyle();
	            		style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Unlock slot in inventory using [KEY " + Keyboard.getKeyName(KEY.getKeyCode()) + "]")));
	            		dropMessage.setChatStyle(style);
	            		ChatFilter.checkFilterAndSend(SettingsConfig.dropItem_filter, dropMessage);
	            		SoundsHandler.playSound("mesky:block", 1, 0.1f);
	            	}
	            	dropPressed = true;
	                event.setCanceled(true); // block before drop happens
	            }
	        }
	        
	        return;
	    }
	    
	    if (key == Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode() && !Keyboard.getEventKeyState()) {
	    	dropPressed = false;
	    }
	    	
	    
	    if (key == KEY.getKeyCode() && SettingsConfig.LockSlots.isOn && !Keyboard.getEventKeyState()) { // locking
	    	if(keyPressed < 10) {
	    		GuiContainer gui = (GuiContainer) mc.currentScreen;
		        Slot hovered = gui.getSlotUnderMouse();
	
		        if (hovered != null && hovered.inventory == mc.thePlayer.inventory) {
		        	int slot = hovered.getSlotIndex();
		        	changedLocks = true;
		        	
		            if (lockedSlots.containsKey(slot) && lockedSlots.get(slot)) { // removing (3 tap)
		                lockedSlots.remove(slot);
		                SoundsHandler.playSound("mesky:tap", 1, 0.3f);
		            } else {
		            	if(!lockedSlots.containsKey(slot)) { // gray lock (1 tap)
		            		lockedSlots.put(slot, false);
		                	SoundsHandler.playSound("mesky:bop");
		            	}else { // red lock (2 tap)
		            		lockedSlots.put(slot, true);
		            		SoundsHandler.playSound("mesky:bop", 1, 2);
		            	}
		            }
		        }
	    	}
	    	
	    	return;
	    }
	    
	    for (int i = 0; i < 9; i++) {
	        if (key == Minecraft.getMinecraft().gameSettings.keyBindsHotbar[i].getKeyCode()) {
	            if(lockedSlots.containsKey(i)) {
	            	if(lockedSlots.get(i)) {
	            		event.setCanceled(true);
	            		return;
	            	}
	            }   
	            
	            GuiContainer gui = (GuiContainer) mc.currentScreen;
		        Slot hovered = gui.getSlotUnderMouse();

		        if (hovered != null && hovered.inventory == mc.thePlayer.inventory) {
		            int slot = hovered.getSlotIndex();
		            
		            if (lockedSlots.containsKey(slot)) {
		            	if(lockedSlots.get(slot)) {
		            		event.setCanceled(true); // block before drop happens
		            	}
		            }
		        }
	            
	            return;
	        }
	    }
	}
	
	public static Slot getSlotByInventoryIndex(Container container, int inventoryIndex) {
        for (Slot slot : container.inventorySlots) {
        	if(slot.slotNumber < container.inventorySlots.size() - 40) continue;
            if (slot.getSlotIndex() == inventoryIndex && slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) {
                return slot;
            }
        }
        return null;
    }
	
	private static ResourceLocation lock = new ResourceLocation(Reference.MODID, "gui/lock.png");
	private static ResourceLocation lock_red = new ResourceLocation(Reference.MODID, "gui/lock_red.png");
	public static ResourceLocation connected_slot = new ResourceLocation(Reference.MODID, "gui/connected_slot.png");

	public static boolean changedLocks = false;


}
