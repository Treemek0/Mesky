package treemek.mesky.config;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import akka.actor.ActorSystem.Settings;
import net.minecraft.client.settings.KeyBinding;

public class SettingsConfig {
	public static class Setting {
		public Boolean isOn = null;
		public Double number = null;
		public String text = null;
		public Float[] position = null;
		public Float scale = null;
		public KeyBinding keyboardKey;
		
		public Setting(boolean isOn, Float[] position, Float scale) {
			this.isOn = isOn;
			this.position = position;
			this.scale = scale;
		}
		
		public Setting(double number, Float[] position, Float scale) {
			this.number = number;
		}
		
		public Setting(String text, Float[] position, Float scale) {
			this.text = text;
			this.position = position;
			this.scale = scale;
		}
		
		public Setting(String text, Double number, Float[] position, Float scale) {
			this.text = text;
			this.number = number;
			this.position = position;
			this.scale = scale;
		}
	}
	
	public static Setting GhostPickaxe = new Setting(false, null, null);
	
    public static Setting GhostPickaxeSlot = new Setting(5, null, null);

    public static Setting BlockFlowerPlacing = new Setting(false, null, null);

    public static Setting FishingTimer = new Setting(false, new Float[]{20f, 20f}, 1f);
    public static Setting FishingTimerIs3d = new Setting(false, null, null);

    public static Setting BonzoTimer = new Setting(false, new Float[]{40f,50f}, 1f);

    public static Setting SpiritTimer = new Setting(false, new Float[]{40f,60f}, 1f);

    public static Setting GhostBlocks = new Setting(false, null, null);

    public static Setting AntyGhostBlocks = new Setting(false, null, null);

    public static Setting HidePlayers = new Setting(false, null, null);

    public static Setting CoordsDetection = new Setting(false, null, null);
    
    public static Setting FreeLook = new Setting(false, null, null);
    
    public static Setting NickMentionDetection = new Setting(false, null, null);

	public static Setting JawbusDetection = new Setting(false, null, null);
	
	public static Setting AutoFish = new Setting(false, null, null);
	public static Setting KillSeaCreatures = new Setting(false, null, null);
	public static Setting AutoThrowHook = new Setting(false, null, null);

	public static Setting HoldingItemSize = new Setting(2, null, null);
	
	public static Setting ScrollbarSpeed = new Setting(30, null, null);
	public static Setting ScrollbarSmoothness = new Setting(0.02, null, null);
	
	public static Setting MarkWaypointTime = new Setting(180, null, null);
	public static Setting EntityDetectorWaypointTouchRadius = new Setting(10, null, null);
	public static Setting EntityDetectorWaypointLifeTime = new Setting(300, null, null);
}

