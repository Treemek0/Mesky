package treemek.mesky.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import treemek.mesky.Commands;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.handlers.gui.AlertsGui;
import treemek.mesky.handlers.gui.ChatFunctionsGui;
import treemek.mesky.handlers.gui.CosmeticsGui;
import treemek.mesky.handlers.gui.GUI;
import treemek.mesky.handlers.gui.Settings;
import treemek.mesky.handlers.gui.WaypointsGui;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;

public class GuiHandler{
	
	public static int GuiType = 0;
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		// for opening gui, because i have to wait 1 tick before opening it
		switch(GuiType) {
			case 1:
				Minecraft.getMinecraft().displayGuiScreen(new GUI());
	            GuiType = 0;
	            break;
			case 2:
	            Minecraft.getMinecraft().displayGuiScreen(new Settings());
	            GuiType = 0;
	            break;
			case 3:
				Minecraft.getMinecraft().displayGuiScreen(new WaypointsGui());
				Location.checkTabLocation();
				WaypointsGui.region.setText(Locations.currentLocationText);
	            GuiType = 0;
	            break;
			case 4:
				Minecraft.getMinecraft().displayGuiScreen(new AlertsGui());
	            GuiType = 0;
	            break;
			case 5:
				Minecraft.getMinecraft().displayGuiScreen(new CosmeticsGui());
	            GuiType = 0;
	            break;
			case 6:
				Minecraft.getMinecraft().displayGuiScreen(new ChatFunctionsGui());
	            GuiType = 0;
	            break;
		}
	}
	
	
}
