package treemek.mesky.handlers.gui.warp.rift;

import java.awt.Color;
import java.awt.Polygon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.gui.warp.fasttravel.WarpIsland;
import treemek.mesky.listeners.GuiOpenListener.FastTravel;
import treemek.mesky.listeners.GuiOpenListener.PadLock;
import treemek.mesky.listeners.GuiOpenListener.RiftTravel;
import treemek.mesky.utils.Utils;

public class RiftWarpGui extends GuiScreen{
	List<RiftWarpIsland> islands;
	private ContainerChest inventorySlots;
	
	public static class RiftStack {
		PadLock lock;
		int slot;
		
		
		public RiftStack(PadLock lock, int slot) {
			this.lock = lock;
			this.slot = slot;
		}
	}
	
	Map<RiftTravel, RiftStack> unlocked_map;
	int containerId = 0;
	
	RiftWarpIsland hoveredIsland = null;
	private WarpIsland hub;
	
	public RiftWarpGui(Map<RiftTravel, RiftStack> unlocked_map, ContainerChest inventorySlots) {
		this.unlocked_map = unlocked_map;
		this.containerId = inventorySlots.windowId;
		this.inventorySlots = inventorySlots;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, 0, width, height, new Color(33, 33, 33, 210).getRGB());
		
		for (RiftWarpIsland warpIsland : islands) {
			if(hoveredIsland == warpIsland) continue;
			warpIsland.drawIsland(mouseX, mouseY, false);
			
			if(hoveredIsland == null && warpIsland.isHovered(mouseX, mouseY)) {
				hoveredIsland = warpIsland;
			}
		}
		
		if(hoveredIsland != null) {
			if(!hoveredIsland.isHovered(mouseX, mouseY)) {
				hoveredIsland.drawIsland(mouseX, mouseY, false);
				hoveredIsland = null;
			}else {
				hoveredIsland.drawIsland(mouseX, mouseY, true);
			}
		}
		
		hub.drawIsland(mouseX, mouseY, hub.isHovered(mouseX, mouseY));
	}
	
	@Override
	public void initGui() {
		islands = new ArrayList<>();
		
		double p = (double)height / 960;
		
		RiftStack wizard = unlocked_map.getOrDefault(RiftTravel.WizardTower, new RiftStack(PadLock.LOCKED, 0));
		RiftStack lagoon = unlocked_map.getOrDefault(RiftTravel.Lagoon, new RiftStack(PadLock.LOCKED, 0));
		RiftStack dreadfarm = unlocked_map.getOrDefault(RiftTravel.Dreadfarm, new RiftStack(PadLock.LOCKED, 0));
		RiftStack plaza = unlocked_map.getOrDefault(RiftTravel.Plaza, new RiftStack(PadLock.LOCKED, 0));
		RiftStack coloseum = unlocked_map.getOrDefault(RiftTravel.Coloseum, new RiftStack(PadLock.LOCKED, 0));
		RiftStack slayer = unlocked_map.getOrDefault(RiftTravel.Slayer, new RiftStack(PadLock.LOCKED, 0));
		RiftStack mountaintop = unlocked_map.getOrDefault(RiftTravel.Mountaintop, new RiftStack(PadLock.LOCKED, 0));
		
		RiftWarpIsland mainIsland = new RiftWarpIsland(width/2 - 575*p, height/2- 300*p, 1150*p, 700*p, "Main island", new ResourceLocation(Reference.MODID, "gui/warp/rift_main.png"), Arrays.asList(new RiftWarpPortal(585*p, 180*p, wizard.slot, "Wizard Tower", wizard.lock), new RiftWarpPortal(865*p, 350*p, lagoon.slot, "Lagoon Hut", lagoon.lock), new RiftWarpPortal(290*p, 465*p, dreadfarm.slot, "Dreadfarm", dreadfarm.lock), new RiftWarpPortal(300*p, 345*p, plaza.slot, "Plaza", plaza.lock), new RiftWarpPortal(666*p, 444*p, coloseum.slot, "Coloseum", coloseum.lock), new RiftWarpPortal(375*p, 70*p, mountaintop.slot, "Mountaintop", mountaintop.lock)));
		Polygon mainIsland_hitbox = new Polygon();
		mainIsland_hitbox.addPoint((int)(25 * p), (int)(640 * p));
		mainIsland_hitbox.addPoint((int)(70 * p), (int)(450 * p));
		mainIsland_hitbox.addPoint((int)(370 * p), (int)(10 * p));
		mainIsland_hitbox.addPoint((int)(800 * p), (int)(35 * p));
		mainIsland_hitbox.addPoint((int)(1150 * p), (int)(230 * p));
		mainIsland_hitbox.addPoint((int)(1150 * p), (int)(700 * p));
		mainIsland_hitbox.addPoint(0, (int)(700 * p));
		mainIsland.hitbox = mainIsland_hitbox;
		islands.add(mainIsland);
		
		RiftWarpIsland slayerIsland = new RiftWarpIsland(mainIsland.xPosition - 50*p, mainIsland.yPosition - 35*p, 325*p, 420*p, "Slayer island", new ResourceLocation(Reference.MODID, "gui/warp/rift_slayer.png"), Arrays.asList(new RiftWarpPortal(130*p, 210*p, slayer.slot, "Slayer", slayer.lock)));
		Polygon slayerIsland_hitbox = new Polygon();
		slayerIsland_hitbox.addPoint(0, 0);
		slayerIsland_hitbox.addPoint((int)(325*p), 0);
		slayerIsland_hitbox.addPoint((int) (310*p), (int) (240*p));
		slayerIsland_hitbox.addPoint((int) (110*p), (int) (300*p));
		slayerIsland_hitbox.addPoint(0, (int) (180*p));
		slayerIsland.hitbox = slayerIsland_hitbox;
		slayerIsland.enabled = slayer.lock;
		islands.add(slayerIsland);
		
		hub = new WarpIsland(10*p, height-60*p, 50*p, 50*p, "Hub", new ResourceLocation(Reference.MODID, "gui/warp/hub_small.png"), "/warp hub");
		hub.enabled = PadLock.UNLOCKED;
		hub.scaleOnTick /= SettingsConfig.CustomWarpMenuScaling.number;
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		for (RiftWarpIsland warpIsland : islands) {
			if(!warpIsland.enabled.isUnlocked()) return;
			
			for (RiftWarpPortal portal : warpIsland.portals) {
				portal.mouseReleased(mouseX, mouseY, () -> clickSlot(portal.slot, 0, 0));
			}
		}
		
		hub.mouseReleased(mouseX, mouseY);
	}
	
	private void clickSlot(int slot, int button, int mode) {
	    Minecraft mc = Minecraft.getMinecraft();
	    mc.playerController.windowClick(containerId, slot, button, mode, mc.thePlayer);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(typedChar == 'e') {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
		
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
