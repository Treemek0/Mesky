package treemek.mesky.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.gui.warp.InvisibleGuiChest;
import treemek.mesky.handlers.gui.warp.fasttravel.WarpGui;
import treemek.mesky.handlers.gui.warp.rift.RiftWarpGui;
import treemek.mesky.handlers.gui.warp.rift.RiftWarpGui.RiftStack;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Utils;

public class GuiOpenListener {
	
	enum Menu {
		FastTravel, Rift
	}
	
	public enum PadLock {
		UNLOCKED(true), LOCKED(false), WRONG_VERSION(false);
		
		private final boolean unlocked;
		PadLock(boolean unlocked){
			this.unlocked = unlocked;
		}
		
		public boolean isUnlocked() {
	        return unlocked;
	    }
	}
	
	public enum FastTravel {
	    PRIVATE_ISLAND("Private Island"),
	    SKYBLOCK_HUB("SkyBlock Hub"),
	    DUNGEON_HUB("Dungeon Hub"),
	    THE_BARN("The Barn"),
	    THE_PARK("The Park"),
	    SPIDERS_DEN("Spider's Den"),
	    THE_END("The End"),
	    CRIMSON_ISLE("Crimson Isle"),
	    GOLD_MINE("Gold Mine"),
	    DEEP_CAVERNS("Deep Caverns"),
	    DWARVEN_MINES("Dwarven Mines"),
	    CRYSTAL_HOLLOWS("Crystal Hollows"),
	    JERRYS_WORKSHOP("Jerry's Workshop"),
	    RIFT_DIMENSION("The Rift"),
	    BACKWATER_BAYOU("Backwater Bayou"),
	    GALATEA("Galatea");

	    private final String displayName;

	    FastTravel(String displayName) {
	        this.displayName = displayName;
	    }

	    public String getDisplayName() {
	        return displayName;
	    }

	    @Override
	    public String toString() {
	        return displayName;
	    }
	}
	
	public enum RiftTravel {
	    WizardTower("The Intruder"),
		Lagoon("The Gill-Man"),
		Dreadfarm("The Baba Yaga"),
		Plaza("The Bankster"),
		Coloseum("The Gooey"),
		Slayer("The Prince"),
		Mountaintop("The 7th Sin");

	    private final String displayName;

	    RiftTravel(String displayName) {
	        this.displayName = displayName;
	    }

	    public String getDisplayName() {
	        return displayName;
	    }

	    @Override
	    public String toString() {
	        return displayName;
	    }
	}
	
	private int ticksWaiting = 0;
	private Menu waitingForChest = null;
	private int containterId = 0;
	
	@SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
		if (SettingsConfig.CustomWarpMenu.isOn && HypixelCheck.isOnHypixel() && event.gui instanceof GuiChest) {
            IInventory chestInventory = ((ContainerChest) ((GuiChest) event.gui).inventorySlots).getLowerChestInventory();
            
            String currentMenu = determineOpenendGuiName(chestInventory);

            if (currentMenu.equals("Fast Travel")) {
            	 waitingForChest = Menu.FastTravel;
                 ticksWaiting = 0;
                 
                 event.gui = new InvisibleGuiChest(Minecraft.getMinecraft().thePlayer.inventory, chestInventory);
            } else if (currentMenu.equals("Porhtal")) {
            	waitingForChest = Menu.Rift;
                ticksWaiting = 0;
                
                event.gui = new InvisibleGuiChest(Minecraft.getMinecraft().thePlayer.inventory, chestInventory);
            	containterId = Minecraft.getMinecraft().thePlayer.openContainer.windowId;
            }
        }
    }
	
    public static String determineOpenendGuiName(IInventory chestInventory) {
        if (chestInventory.hasCustomName()) {
            return chestInventory.getDisplayName().getUnformattedText();
        }
        
        return "";
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (waitingForChest == null || event.phase != TickEvent.Phase.END) return;

        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChest)) {
            waitingForChest = null;
            return;
        }

        GuiChest gui = (GuiChest) Minecraft.getMinecraft().currentScreen;
        IInventory inv = ((ContainerChest) gui.inventorySlots).getLowerChestInventory();
        
        boolean hasItems = false;
        if(waitingForChest == Menu.FastTravel && inv.getStackInSlot(Math.min(inv.getSizeInventory(), 36)) != null) {
            hasItems = true;
        }
        
        if(waitingForChest == Menu.Rift && inv.getStackInSlot(Math.min(inv.getSizeInventory(), 18)) != null) {
            hasItems = true;
        }

        if (hasItems || ticksWaiting > 10) {
        	if(waitingForChest == Menu.FastTravel) {
        		Map<FastTravel, PadLock> unlocked_map = new HashMap<>();
        		
        		for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack stack = inv.getStackInSlot(i);

					for (FastTravel island : FastTravel.values()) {
		                if (stack.getDisplayName().contains(island.getDisplayName())) {
		                	if((island == FastTravel.GALATEA || island == FastTravel.THE_PARK) && !Utils.isVersionAtLeast("1.21.4")) {
		                		unlocked_map.put(island, PadLock.WRONG_VERSION);
			                    break;
							}else {
			                    PadLock unlocked = PadLock.LOCKED;
			                    List<String> lore = Utils.getItemLore(stack);
			                    for (int j = lore.size() - 1; j >= 0; j--) {
			                    	String loreLine = lore.get(j);
			                    	
			                        if (loreLine.contains("to warp!") || loreLine.contains("You are here!")) {
			                            unlocked = PadLock.UNLOCKED;
			                            break;
			                        }
			                    }
			                    
			                    unlocked_map.put(island, unlocked);
			                    break;
							}
			            }
					}
				}

        		Minecraft.getMinecraft().displayGuiScreen(new WarpGui(unlocked_map));
        	}else if(waitingForChest == Menu.Rift) {
        		Map<RiftTravel, RiftStack> unlocked_map = new HashMap<>();
        		
        		for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack stack = inv.getStackInSlot(i);

					for (RiftTravel island : RiftTravel.values()) {
		                if (stack.getDisplayName().contains(island.getDisplayName())) {
			                    PadLock unlocked = PadLock.LOCKED;
			                    List<String> lore = Utils.getItemLore(stack);
			                    for (int j = lore.size() - 1; j >= 0; j--) {
			                    	String loreLine = lore.get(j);
			                        if (loreLine.contains("Click to teleport!")) {
			                            unlocked = PadLock.UNLOCKED;
			                            break;
			                        }
			                    }
			                    
			                    unlocked_map.put(island, new RiftStack(unlocked, i));
			                    break;
			            }
					}
				}
        		
        		Minecraft.getMinecraft().displayGuiScreen(new RiftWarpGui(unlocked_map, (ContainerChest) ((GuiChest) gui).inventorySlots));
        	}
            
            waitingForChest = null;
        }

        ticksWaiting++;
    }
}
