package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class KeyActions {
	public static class KeyAction {
		public Keybind keybind;
		public String command;
		public boolean enabled = true;
		
		public KeyAction(Keybind keybind, String command) {
			this.keybind = keybind;
			this.command = command;
		}
	}
	
	public static List<KeyAction> actions = new ArrayList<>();
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
	    if (event.phase != TickEvent.Phase.END) return;
	    if(Minecraft.getMinecraft().currentScreen != null) return;
	    
	    int highestKeyCount = 0;
	    KeyAction highestKeyKeyAction = null;
	    
	    for (KeyAction keyAction : actions) {
			if(keyAction.keybind.wasKeybindPressed()) { // prioritize higher key counts so if user has (KEY_L) and (KEY_L && KEY_CTRL) and pressed L and CTRL then it should execute action with higher key count
				if(keyAction.keybind.getSize() > highestKeyCount) {
					highestKeyKeyAction = keyAction;
					highestKeyCount = keyAction.keybind.getSize();
				}
			}
		}
	    
	    if(highestKeyKeyAction != null) {
	    	Utils.debug("Keybind: " + highestKeyKeyAction.command);
	    	Utils.executeCommand(highestKeyKeyAction.command);
	    }
	}

	public static List<KeyAction> getActions() {
		return actions;
	}
}
