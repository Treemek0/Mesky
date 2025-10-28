package treemek.mesky.handlers.gui.warp.fasttravel;

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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;
import treemek.mesky.handlers.gui.elements.Popup;
import treemek.mesky.listeners.GuiOpenListener.FastTravel;
import treemek.mesky.listeners.GuiOpenListener.PadLock;
import treemek.mesky.utils.Utils;

public class WarpGui extends GuiScreen{
	List<WarpIsland> islands;
	
	Map<FastTravel, PadLock> unlocked_map;
	
	public static Popup popup = new Popup(1000);
	
	WarpIsland hoveredIsland = null;
	
	public WarpGui(Map<FastTravel, PadLock> unlocked_map) {
		this.unlocked_map = unlocked_map;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, 0, width, height, new Color(33, 33, 33, 210).getRGB());
		
		for (WarpIsland warpIsland : islands) {
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
		
		popup.drawPopup(partialTicks);
	}
	
	PadLock defaultLock = PadLock.UNDISCOVERED;
	
	@Override
	public void initGui() {
		popup = new Popup(1000);
		command_cooldown = 0;
		
		islands = new ArrayList<>();
		
		double p = (double)height / 960;
		
		WarpIsland private_island = new WarpIsland(20*p, height-175*p, 130*p, 155*p, "Private Island", new ResourceLocation(Reference.MODID, "gui/warp/private_island.png"), new WarpPortal(65*p, 75*p, "/warp home", "Private Island"));
		private_island.enabled = unlocked_map.getOrDefault(FastTravel.PRIVATE_ISLAND, defaultLock);
		islands.add(private_island);
		
		WarpIsland garden = new WarpIsland(20*p, height-320*p, 200*p, 125*p, "Garden", new ResourceLocation(Reference.MODID, "gui/warp/garden.png"), new WarpPortal(100*p, 80*p, "/warp garden", "Garden"));
		islands.add(garden);
		
		WarpIsland jerry_island = new WarpIsland(width-200*p, height-140*p, 200*p, 140*p, "Jerry Island", new ResourceLocation(Reference.MODID, "gui/warp/jerry_island.png"), new WarpPortal(150*p, 80*p, "/warp jerry", "Jerry Island"));
		jerry_island.enabled = unlocked_map.getOrDefault(FastTravel.JERRYS_WORKSHOP, defaultLock);
		islands.add(jerry_island);
		
		WarpIsland galatea = new WarpIsland(width/5, height/2-40*p, 180*p, 160*p, "Galatea", new ResourceLocation(Reference.MODID, "gui/warp/galatea.png"), new WarpPortal(135*p, 55*p, "/warp galatea", "Galatea"));
		galatea.enabled = unlocked_map.getOrDefault(FastTravel.GALATEA, PadLock.UNDISCOVERED);
		islands.add(galatea);
		
		WarpIsland park = new WarpIsland(galatea.xPosition + 150*p, galatea.yPosition + 45*p, 160*p, 160*p, "Park", new ResourceLocation(Reference.MODID, "gui/warp/park.png"), Arrays.asList(new WarpPortal(60*p, 35*p, "/warp jungle", "Jungle"), new WarpPortal(125*p, 125*p, "/warp park", "Park")));
		park.enabled = unlocked_map.getOrDefault(FastTravel.THE_PARK, defaultLock);
		int[][] parkPoints = {
			    {30, 35},
			    {100, 5},
			    {160, 110},
			    {135, 160},
			    {0, 160},
			    {0, 80}
			};
		
		Polygon park_hitbox = makeHitbox(parkPoints, p);
		park.hitbox = park_hitbox;
		islands.add(park);
		
		WarpIsland hub = new WarpIsland(park.xPosition + 140*p, park.yPosition + 135*p, 320*p, 270*p, "Hub", new ResourceLocation(Reference.MODID, "gui/warp/hub.png"), Arrays.asList(new WarpPortal(220*p, 110*p, "/warp hub", "Hub"), new WarpPortal(120*p, 125*p, "/warp museum", "Museum"), new WarpPortal(55*p, 50*p, "/warp castle", "Castle"), new WarpPortal(160*p, 40*p, "/warp crypt", "Crypt"), new WarpPortal(150*p, 230*p, "/warp da", "Dark Auction"), new WarpPortal(175*p, 150*p, "/warp wizard_tower", "Wizard")));
		hub.enabled = unlocked_map.getOrDefault(FastTravel.SKYBLOCK_HUB, defaultLock);
		hub.scaleOnHover = 1.1f;
		hub.setInstantHoveredState(true);
		int[][] hubPoints = {
		    {50, 30},
		    {120, 30},
		    {220, 5},
		    {310, 50},
		    {310, 260},
		    {115, 260},
		    {5, 110},
		    {5, 55}
		};

		Polygon hub_hitbox = makeHitbox(hubPoints, p);
		hub.hitbox = hub_hitbox;
		islands.add(hub);
		
		WarpIsland spiders_den = new WarpIsland(park.xPosition + 190*p, galatea.yPosition + 25*p, 200*p, 150*p, "Spider's den", new ResourceLocation(Reference.MODID, "gui/warp/spiders_den.png"), Arrays.asList(new WarpPortal(120*p, 120*p, "/warp spider", "Spider's den"), new WarpPortal(45*p, 80*p, "/warp arachne", "Arachne"), new WarpPortal(157*p, 55*p, "/warp top", "Top of Nest")));
		spiders_den.enabled = unlocked_map.getOrDefault(FastTravel.SPIDERS_DEN, defaultLock);
		islands.add(spiders_den);
		
		WarpIsland bayou = new WarpIsland(hub.xPosition + 410*p, hub.yPosition + 190*p, 160*p, 150*p, "Bayou Island", new ResourceLocation(Reference.MODID, "gui/warp/bayou.png"), new WarpPortal(15*p, 105*p, "/warp bayou", "Bayou"));
		bayou.enabled = unlocked_map.getOrDefault(FastTravel.BACKWATER_BAYOU, defaultLock);
		islands.add(bayou);
		
		WarpIsland barn = new WarpIsland(hub.xPosition + 330*p, hub.yPosition + 50*p, 90*p, 100*p, "Barn", new ResourceLocation(Reference.MODID, "gui/warp/barn.png"), new WarpPortal(15*p, 60*p, "/warp barn", "Barn"));
		barn.enabled = unlocked_map.getOrDefault(FastTravel.THE_BARN, defaultLock);
		islands.add(barn);
		
		WarpIsland gold_mine = new WarpIsland(hub.xPosition + 290*p, hub.yPosition - 85*p, 100*p, 100*p, "Barn", new ResourceLocation(Reference.MODID, "gui/warp/gold_mine.png"), new WarpPortal(40*p, 65*p, "/warp gold", "Gold mine"));
		gold_mine.enabled = unlocked_map.getOrDefault(FastTravel.GOLD_MINE, defaultLock);
		islands.add(gold_mine);
		
		WarpIsland farming_island = new WarpIsland(barn.xPosition + 90*p, gold_mine.yPosition + 10*p, 180*p, 210*p, "Farming island", new ResourceLocation(Reference.MODID, "gui/warp/farming_island.png"), Arrays.asList(new WarpPortal(20*p, 115*p, "/warp desert", "Desert"), new WarpPortal(165*p, 70*p, "/warp trapper", "Trapper")));
		farming_island.enabled = unlocked_map.getOrDefault(FastTravel.THE_BARN, defaultLock);
		islands.add(farming_island);
		
		WarpIsland end = new WarpIsland(galatea.xPosition + 110*p, galatea.yPosition - 220*p, 240*p, 240*p, "The end", new ResourceLocation(Reference.MODID, "gui/warp/end.png"), Arrays.asList(new WarpPortal(175*p, 150*p, "/warp end", "End"), new WarpPortal(80*p, 95*p, "/warp drag", "Dragon's Nest"), new WarpPortal(115*p, 195*p, "/warp void", "Void")));
		end.enabled = unlocked_map.getOrDefault(FastTravel.THE_END, defaultLock);
		islands.add(end);
		
		WarpIsland crimson_isle = new WarpIsland(spiders_den.xPosition + 140*p, spiders_den.yPosition - 460*p, 460*p, 390*p, "Crimson Isle", new ResourceLocation(Reference.MODID, "gui/warp/crimson_isle.png"), Arrays.asList(new WarpPortal(65*p, 345*p, "/warp isle", "Crimson Isle"), new WarpPortal(290*p, 150*p, "/warp skull", "Skull"), new WarpPortal(320*p, 190*p, "/warp tomb", "Smoldering Tomb")));
		crimson_isle.enabled = unlocked_map.getOrDefault(FastTravel.CRIMSON_ISLE, defaultLock);
		crimson_isle.scaleOnHover = 1.1f;
		islands.add(crimson_isle);
		
		WarpIsland deep_caverns = new WarpIsland(gold_mine.xPosition + 90*p, spiders_den.yPosition - 80*p, 135*p, 160*p, "Dwarves mines", new ResourceLocation(Reference.MODID, "gui/warp/dwarves_mines.png"), Arrays.asList(new WarpPortal(40*p, 90*p, "/warp deep", "Deep Caverns"), new WarpPortal(115*p, 50*p, "/warp dwarves", "Dwarves mines", unlocked_map.getOrDefault(FastTravel.DWARVEN_MINES, PadLock.LOCKED).isUnlocked()), new WarpPortal(115*p, 90*p, "/warp forge", "Forge", unlocked_map.getOrDefault(FastTravel.DWARVEN_MINES, PadLock.LOCKED).isUnlocked()), new WarpPortal(85*p, 65*p, "/warp basecamp", "Base camp", unlocked_map.getOrDefault(FastTravel.DWARVEN_MINES, PadLock.LOCKED).isUnlocked()), new WarpPortal(100*p, 115*p, "/warp crystals", "Crystal Hollows", unlocked_map.getOrDefault(FastTravel.CRYSTAL_HOLLOWS, PadLock.LOCKED).isUnlocked()), new WarpPortal(75*p, 140*p, "/warp nucleus", "Nucleus", unlocked_map.getOrDefault(FastTravel.CRYSTAL_HOLLOWS, PadLock.LOCKED).isUnlocked())));
		deep_caverns.enabled = unlocked_map.getOrDefault(FastTravel.DEEP_CAVERNS, defaultLock);
		deep_caverns.scaleOnHover = 1.3f;
		islands.add(deep_caverns);
		
		WarpIsland dungeon_hub = new WarpIsland(hub.xPosition - 80*p, hub.yPosition + 200*p, 44*p, 100*p, "Dungeon Hub", new ResourceLocation(Reference.MODID, "gui/warp/dungeon_hub.png"), new WarpPortal(19*p, 75*p, "/warp dh", "Dungeon Hub"));
		dungeon_hub.enabled = unlocked_map.getOrDefault(FastTravel.DUNGEON_HUB, defaultLock);
		islands.add(dungeon_hub);
		
		WarpIsland rift = new WarpIsland(hub.xPosition - 200*p, hub.yPosition + 200*p, 44*p, 100*p, "Rift", new ResourceLocation(Reference.MODID, "gui/warp/rift.png"), new WarpPortal(19*p, 75*p, "/warp rift", "Rift"));
		rift.enabled = unlocked_map.getOrDefault(FastTravel.RIFT_DIMENSION, defaultLock);
		islands.add(rift);
		
		ScaledResolution scaled = new ScaledResolution(Minecraft.getMinecraft());
		int scaleFactor = scaled.getScaleFactor();
		int targetY = (int) ((hub.yPosition + 110 * p) * scaleFactor);
		int targetX = (int) ((hub.xPosition + 180 * p) * scaleFactor);
		
		Mouse.setCursorPosition(targetX, scaled.getScaledHeight() * scaleFactor - targetY);
	}

	
	long command_cooldown = 0;
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if(System.currentTimeMillis() - command_cooldown < 1000) return;
		
		for (WarpIsland warpIsland : islands) {
			if(warpIsland.mouseReleased(mouseX, mouseY)) {
				command_cooldown = System.currentTimeMillis();
			};
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(typedChar == 'e') {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
		
		super.keyTyped(typedChar, keyCode);
	}
	
	public Polygon makeHitbox(int[][] points, double scale) {
	    Polygon hitbox = new Polygon();
	    for (int[] point : points) {
	        hitbox.addPoint((int)(point[0] * scale), (int)(point[1] * scale));
	    }
	    return hitbox;
	}

	

	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
