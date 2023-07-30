package treemek.mesky;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import treemek.mesky.cosmetics.wings.AngelWings;
import treemek.mesky.cosmetics.wings.FireWings;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.features.MaskTimer;
import treemek.mesky.features.illegal.GhostBlock;
import treemek.mesky.handlers.Config;
import treemek.mesky.handlers.EventHandler;
import treemek.mesky.proxy.CommonProxy;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Waypoints;

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
		Config.reloadConfig();
		
		
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(new FishingTimer());
		MinecraftForge.EVENT_BUS.register(new BlockFlowerPlacing());
		MinecraftForge.EVENT_BUS.register(new Alerts());
		MinecraftForge.EVENT_BUS.register(new Waypoints());
		MinecraftForge.EVENT_BUS.register(new GhostBlock());
		MinecraftForge.EVENT_BUS.register(new MaskTimer());
		MinecraftForge.EVENT_BUS.register(new FireWings());
		MinecraftForge.EVENT_BUS.register(new AngelWings());
	}
		
	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
	
	}
	
}