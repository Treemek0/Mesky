package treemek.mesky.config;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.SerializedName;

import akka.actor.ActorSystem.Settings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import treemek.mesky.utils.Keybind;

public class SettingsConfig {
	public static class Setting {
		public Boolean isOn = null;
		
		public Double number = null;
		
		public List<Integer> intList;
		
		public String text = null;
		
		public Float[] position = null;
		public Float scale = null;
		
		public Keybind keybind;
		
		public Setting(boolean isOn, Float[] position, Float scale) {
			this.isOn = isOn;
			this.position = position;
			this.scale = scale;
		}
		
		public Setting(boolean isOn) {
			this.isOn = isOn;
		}
		
		public Setting(double number) {
			this.number = number;
		}
		
		public Setting(double number, Float[] position, Float scale) {
			this.number = number;
			this.position = position;
			this.scale = scale;
		}
		
		public Setting(String text, Float[] position, Float scale) {
			this.text = text;
			this.position = position;
			this.scale = scale;
		}
		
		public Setting(String text) {
			this.text = text;
		}
		
		public Setting(Keybind keybind) { 
			this.keybind = keybind;
		}
		
		public Setting(String text, Double number, Float[] position, Float scale) {
			this.text = text;
			this.number = number;
			this.position = position;
			this.scale = scale;
		}
	}
	
	public static Setting AotvZoom = new Setting(true);
	public static Setting AotvZoomMaxDistance = new Setting(57);
	public static Setting AotvZoomMultiplayer = new Setting(0.7f);
	public static Setting AotvZoomSmoothness = new Setting(0.1f);
	public static Setting AotvZoomColor = new Setting("FF0000");

	public static final Setting LockSlots = new Setting(true);
	public static final Setting LockSlotsKeybind = new Setting(new Keybind(Keyboard.KEY_L));
	public static Setting dropItem_filter = new Setting("CHAT");

	public static Setting CustomWarpMenu = new Setting(true);
	public static Setting CustomWarpMenuLockableIslands = new Setting(true);
	public static Setting CustomWarpMenuScaling = new Setting(1);

	public static Setting CustomRiftWarpMenu = new Setting(true);
	public static Setting CustomRiftWarpMenuHoverScaling = new Setting(true);
	public static Setting CustomRiftWarpMenuScaling = new Setting(1);

	public static Setting GhostPickaxe = new Setting(false);
	public static Setting GhostPickaxeSlot = new Setting(5);
	public static Setting GhostKeybind = new Setting(new Keybind(Keyboard.KEY_G));

	public static Setting BlockFlowerPlacing = new Setting(false);

	public static Setting FishingTimer = new Setting(false, new Float[]{20f, 20f}, 1f);
	public static Setting FishingTimerIs3d = new Setting(false);
	public static Setting FishingTimer3dScale = new Setting(2);
	public static Setting FishingTimer3dY = new Setting(0.6f);
	public static Setting FishingTimer3dBackgroundColor = new Setting("000000");
	public static Setting FishingTimer3dColor = new Setting("bfbfbf");
	public static Setting FishingTimer3dRenderImage = new Setting(true);

	public static Setting BonzoTimer = new Setting(false, new Float[]{40f, 50f}, 1f);
	public static Setting SpiritTimer = new Setting(false, new Float[]{40f, 60f}, 1f);

	public static Setting GhostBlocks = new Setting(false);
	public static Setting AntyGhostBlocks = new Setting(false);
	public static Setting HidePlayers = new Setting(false);

	public static Setting CoordsDetection = new Setting(false);
	public static Setting coordsDetection_filter = new Setting("CHAT");
	public static Setting AutoMarkCoords = new Setting(false);
	public static Setting CoordsDetectionIgnoreSelf = new Setting(false);

	public static Setting FreeLook = new Setting(false);
	public static Setting FreeLookKeybind = new Setting(new Keybind(Keyboard.KEY_LMENU));
	public static Setting FreeRotate = new Setting(false);
	public static Setting FreeLookClampAngles = new Setting(true);
	public static Setting FreeLookToogle = new Setting(false);

	public static Setting NickMentionDetection = new Setting(false);
	public static Setting NickMentionDetectionColor = new Setting(EnumChatFormatting.AQUA.name());

	public static Setting JawbusDetection = new Setting(false);
	public static Setting JawbusDetectionWaypoint = new Setting(false);
	public static Setting JawbusNotifyParty = new Setting(false);
	public static Setting JawbusPlayerDeathDetection = new Setting(false);
	public static Setting JawbusPlayerDeathDetectionSoundVolume = new Setting(1);

	public static Setting AutoFish = new Setting(false);
	public static Setting KillSeaCreatures = new Setting(false);
	public static Setting AutoThrowHook = new Setting(false);
	public static Setting AutoFishAntyAfk = new Setting(true);

	public static Setting HoldingItemSize = new Setting(2);
	public static Setting HoldingItemOffsetX = new Setting(0);
	public static Setting HoldingItemOffsetY = new Setting(0);

	public static Setting ScrollbarSpeed = new Setting(30);
	public static Setting ScrollbarSmoothness = new Setting(0.02);

	public static Setting MarkWaypointTime = new Setting(180);
	public static Setting MarkWaypointRadius = new Setting(2);
	public static Setting EntityDetectorWaypointTouchRadius = new Setting(10);
	public static Setting EntityDetectorWaypointLifeTime = new Setting(300);

	public static Setting AlertsIgnoreSelf = new Setting(true);

	public static Setting LegendarySeaCreaturesNotification = new Setting(false);
	public static Setting WaterHydraNotification = new Setting(true);
	public static Setting PlhlegblastNotification = new Setting(true);
	public static Setting SeaEmperorNotification = new Setting(true);
	public static Setting ThunderNotification = new Setting(true);
	public static Setting AbyssalMinerNotification = new Setting(true);
	public static Setting PhantomFisherNotification = new Setting(true);
	public static Setting GrimReaperNotification = new Setting(true);
	public static Setting YetiNotification = new Setting(true);
	public static Setting ReindrakeNotification = new Setting(true);
	public static Setting GreatSharkNotification = new Setting(true);

	public static Setting SharkCounter = new Setting(false);

	public static Setting MiningSpeed = new Setting(1500);
	public static Setting MiningMacroPath = new Setting(1);

	public static Setting DiscordWebHook = new Setting("");

	public static Setting customChat = new Setting(false, new Float[]{50.0f, 50.0f}, 1f);
	public static Setting customChatKeybind = new Setting(new Keybind(Keyboard.KEY_Y));
	public static Setting customChatWidth = new Setting(1);
	public static Setting customChatHeight = new Setting(1);
	public static Setting customChatTextScale = new Setting(1);
	public static Setting customChatOpacity = new Setting(0.7f);
	public static Setting customChatFadeStart = new Setting(3000);
	public static Setting customChatFadeDuration = new Setting(1000);
	public static Setting customChatToggle = new Setting(false);
	public static Setting customChatRightPacing = new Setting(false);

	public static Setting sendingToServer_filter = new Setting("CHAT");
	public static Setting warping_filter = new Setting("CHAT");
	public static Setting wasKilledBy_filter = new Setting("CHAT");
	public static Setting playingOnProfile_filter = new Setting("CHAT");
	public static Setting fireSale_filter = new Setting("CHAT");
	public static Setting killCombo_filter = new Setting("CHAT");
	public static Setting eventEXP_filter = new Setting("CHAT");

}

