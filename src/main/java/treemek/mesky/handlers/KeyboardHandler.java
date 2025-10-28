package treemek.mesky.handlers;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import treemek.mesky.utils.Utils;

public class KeyboardHandler {
	private static Set<Integer> pressedKeys = new HashSet<>();

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
	    int key = Keyboard.getEventKey();
	    if (key == Keyboard.KEY_NONE) return;

	    boolean state = Keyboard.getEventKeyState();
	    boolean hardware = Keyboard.isKeyDown(key);

	    if (state && hardware) {
	        pressedKeys.add(key);
	    } else if (!state && !hardware) {
	        pressedKeys.remove(key);
	    }
	}
	
	public static boolean isKeyDown(int key) {
		return pressedKeys.contains(key);
	}
}
