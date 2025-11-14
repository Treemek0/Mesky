package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;

public class Keybind {
	List<Integer> keys = new ArrayList<>();
	
	private transient boolean wasPressed = false;
	private transient int releasedTick = -1;
	
	private transient boolean wasntPressed = false;
	private transient int pressedTick = -1;
	
	private String keyBindString = "";
	
	public Keybind(int key) {
		keys.add(key);
		
		createString();
	}
	
	public Keybind(int[] keysArray) {
		for (int i = 0; i < keysArray.length; i++) {
			keys.add(keysArray[i]);
		}
		
		createString();
	}
	
	public Keybind(List<Integer> keys) {
		this.keys = keys;
		
		createString();
	}
	
	public Keybind() { }
	
	public List<Integer> getKeys() {
		return new ArrayList<>(keys);
	}
	
	public boolean isKeybindDown() {
		if(keys.isEmpty()) return false;
		
		boolean isPressed = true;
		for (Integer key : keys) {
			if(!Keyboard.isKeyDown(key)) {
				isPressed = false;
			}
		}
		
		return isPressed;
	}
	
	/**
	 * Checks if the keybind was released this tick. Use with tick based events only
	 * 
	 * @return True if the keybind was just released, false otherwise
	 */
	public boolean wasKeybindReleased() {
		int tick = Minecraft.getMinecraft().ingameGUI.getUpdateCounter();
		
		if(releasedTick == tick) {
			return true;
		}
		
		if(isKeybindDown()) {
			wasPressed = true;
		}else {
			if(wasPressed) {
				releasedTick = tick;
				wasPressed = false;
				return true;
			}
			
			wasPressed = false;
		}
		
		return false;
	}
	/**
	 * Checks if the keybind was pressed this tick. Use with tick based events only
	 * 
	 * @return True if the keybind was just pressed, false otherwise
	 */
	public boolean wasKeybindPressed() {
	    int tick = Minecraft.getMinecraft().ingameGUI.getUpdateCounter();
	    
	    if (pressedTick == tick) { 
	        return true;
	    }

	    if (isKeybindDown()) {
	    	if(wasntPressed) {
	    		pressedTick = tick;
	    		wasntPressed = false;
	    		return true;
	    	}
	    }else {
	    	wasntPressed = true;
	    }
	    
	    return false;
	}
	
	public void setKeys(List<Integer> keys) {
		this.keys = new ArrayList<>(keys);
		createString();
	}
	
	public String getKeysAsString() {
		return keyBindString;
	}
	
	public Integer getKey(int index) {
		if(keys.isEmpty()) return null;
		return keys.get(index);
	}
	
	public void addKey(int key) {
		keys.add(key);
		createString();
	}
	
	public void removeKey(int key) {
		keys.remove((Integer)key);
		createString();
	}
	
	public boolean containsKey(int key) {
		return keys.contains((Integer)key);
	}

	public void clearKeys() {
		keys.clear();
	}
	
	public int getSize() {
		return keys.size();
	}
	
	public boolean isEmpty() {
		return keys.isEmpty();
	}
	
	private void createString(){
		
		if(keys.isEmpty()) {
			keyBindString = "NONE";
			return;
		}
		
		String keyBind = "";
		for (int i = 0; i < keys.size(); i++) {
			keyBind += Keyboard.getKeyName(keys.get(i).intValue());
			if(i < keys.size() - 1) keyBind += " + ";
		}
		
		this.keyBindString = keyBind;
	}

}
