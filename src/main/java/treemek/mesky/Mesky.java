package treemek.mesky;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.handlers.EventHandler;
import treemek.mesky.proxy.CommonProxy;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Waypoints;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class Mesky {

	@SidedProxy(serverSide = Reference.SERVER_PROXY_CLASS, clientSide = Reference.CLIENT_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@Mod.Instance("mesky")
	public static Mesky instance;
	
	
	
	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
			
	}
		
	@Mod.EventHandler
	public static void init(FMLInitializationEvent event) {
		proxy.registerRenders();
		
		ClientCommandHandler.instance.registerCommand(new Commands());
		
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(new FishingTimer());
		MinecraftForge.EVENT_BUS.register(new BlockFlowerPlacing());
		MinecraftForge.EVENT_BUS.register(new Alerts());
		MinecraftForge.EVENT_BUS.register(new Waypoints());
	}
		
	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
	
	}
	
}
