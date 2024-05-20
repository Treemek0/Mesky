package treemek.mesky;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.cosmetics.wings.AngelWings;
import treemek.mesky.cosmetics.wings.FireWings;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.features.HidePlayers;
import treemek.mesky.features.MaskTimer;
import treemek.mesky.features.illegal.GhostBlock;
import treemek.mesky.features.illegal.GhostPickaxe;
import treemek.mesky.features.illegal.macro.PumpkinFarm;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.proxy.CommonProxy;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.ChatFunctions;
import treemek.mesky.utils.FriendsLocations;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.chat.CoordsDetector;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, clientSideOnly = true)
public class Mesky {

	public static String configDirectory;
	
	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		configDirectory = event.getModConfigurationDirectory().toString();
	}
		
	@Mod.EventHandler
	public static void init(FMLInitializationEvent event) {
		ClientCommandHandler.instance.registerCommand(new Commands());
		ConfigHandler.reloadConfig();
		
		MinecraftForge.EVENT_BUS.register(new HypixelCheck());
		MinecraftForge.EVENT_BUS.register(new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new FishingTimer());
		MinecraftForge.EVENT_BUS.register(new BlockFlowerPlacing());
		MinecraftForge.EVENT_BUS.register(new Alerts());
		MinecraftForge.EVENT_BUS.register(new ChatFunctions());
		MinecraftForge.EVENT_BUS.register(new FriendsLocations());
		MinecraftForge.EVENT_BUS.register(new Waypoints());
		MinecraftForge.EVENT_BUS.register(new GhostBlock());
		MinecraftForge.EVENT_BUS.register(new GhostPickaxe());
		MinecraftForge.EVENT_BUS.register(new MaskTimer());
		MinecraftForge.EVENT_BUS.register(new FireWings());
		MinecraftForge.EVENT_BUS.register(new AngelWings());
		MinecraftForge.EVENT_BUS.register(new PumpkinFarm());
		MinecraftForge.EVENT_BUS.register(new HidePlayers());
		MinecraftForge.EVENT_BUS.register(new CoordsDetector());
		
		ClientRegistry.registerKeyBinding(PumpkinFarm.L_KEY);
		ClientRegistry.registerKeyBinding(PumpkinFarm.R_KEY);
		ClientRegistry.registerKeyBinding(GhostBlock.GKEY);
	}
		
	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
	
	}
	
}