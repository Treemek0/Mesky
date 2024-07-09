package treemek.mesky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.pathfinding.PathFinder;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.cosmetics.aura.FireAura;
import treemek.mesky.cosmetics.hat.StovePiperHat;
import treemek.mesky.cosmetics.pets.Cat;
import treemek.mesky.cosmetics.pets.Parrot;
import treemek.mesky.cosmetics.wings.AngelWings;
import treemek.mesky.cosmetics.wings.FireWings;
import treemek.mesky.features.AntyGhostBlocks;
import treemek.mesky.features.BlockFlowerPlacing;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.features.HidePlayers;
import treemek.mesky.features.MaskTimer;
import treemek.mesky.features.illegal.AutoFish;
import treemek.mesky.features.illegal.EntityDetector;
import treemek.mesky.features.illegal.Freelook;
import treemek.mesky.features.illegal.GhostBlock;
import treemek.mesky.features.illegal.GhostPickaxe;
import treemek.mesky.features.illegal.JawbusDetector;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.proxy.IProxy;
import treemek.mesky.proxy.ClientProxy;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.ChatFunctions;
import treemek.mesky.utils.FriendsLocations;
import treemek.mesky.utils.HypixelCheck;
import treemek.mesky.utils.MacroWaypoints;
import treemek.mesky.utils.PathfinderUtils;
import treemek.mesky.utils.RotationUtils;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.chat.CoordsDetector;
import treemek.mesky.utils.chat.NickMentionDetector;
import treemek.mesky.utils.manager.RecordHeadMovement;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, clientSideOnly = true)
public class Mesky {

	public static String configDirectory;
	//public static IProxy proxy;
	
	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		configDirectory = event.getModConfigurationDirectory().toString();
		
		//proxy = new ClientProxy();
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
		MinecraftForge.EVENT_BUS.register(new RenderHandler());
		MinecraftForge.EVENT_BUS.register(new ChatFunctions());
		MinecraftForge.EVENT_BUS.register(new FriendsLocations());
		MinecraftForge.EVENT_BUS.register(new Waypoints());
		MinecraftForge.EVENT_BUS.register(new GhostBlock());
		MinecraftForge.EVENT_BUS.register(new GhostPickaxe());
		MinecraftForge.EVENT_BUS.register(new MaskTimer());
		MinecraftForge.EVENT_BUS.register(new FireWings());
		MinecraftForge.EVENT_BUS.register(new AngelWings());
		MinecraftForge.EVENT_BUS.register(new StovePiperHat());
		MinecraftForge.EVENT_BUS.register(new HidePlayers());
		MinecraftForge.EVENT_BUS.register(new CoordsDetector());
		MinecraftForge.EVENT_BUS.register(new AntyGhostBlocks());
		MinecraftForge.EVENT_BUS.register(new NickMentionDetector());
		MinecraftForge.EVENT_BUS.register(new JawbusDetector());
		MinecraftForge.EVENT_BUS.register(new Freelook());
		MinecraftForge.EVENT_BUS.register(new Cat());
		MinecraftForge.EVENT_BUS.register(new FireAura());
		MinecraftForge.EVENT_BUS.register(new Parrot());
		MinecraftForge.EVENT_BUS.register(new EntityDetector());
		MinecraftForge.EVENT_BUS.register(new AutoFish());
		MinecraftForge.EVENT_BUS.register(new RotationUtils());
		MinecraftForge.EVENT_BUS.register(new RecordHeadMovement());
		MinecraftForge.EVENT_BUS.register(new MacroWaypoints());
		//MinecraftForge.EVENT_BUS.register(new PathfinderUtils());
		
		ClientRegistry.registerKeyBinding(GhostBlock.GKEY);
		ClientRegistry.registerKeyBinding(Freelook.KEY);
		
		//if(Minecraft.getMinecraft().getSession().getUsername().equals("Treemek")){
			//ClientRegistry.registerKeyBinding(PathfinderUtils.PathFindKey);
			ClientRegistry.registerKeyBinding(RecordHeadMovement.HeadRecorder);
			ClientRegistry.registerKeyBinding(RecordHeadMovement.HeadPlayer);
		//}
	}
		
	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
	
	}
	
}