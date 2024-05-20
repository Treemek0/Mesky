package treemek.mesky.config;

import com.google.gson.annotations.SerializedName;

public class SettingsConfig {
	@SerializedName("Ghost Pickaxe")
	public static boolean GhostPickaxe = false;

	@SerializedName("BlockFlowerPlacing")
    public static boolean BlockFlowerPlacing = false;

    @SerializedName("FishingTimer")
    public static boolean FishingTimer = false;

    @SerializedName("BonzoTimer")
    public static boolean BonzoTimer = false;

    @SerializedName("SpiritTimer")
    public static boolean SpiritTimer = false;

    @SerializedName("GhostBlocks")
    public static boolean GhostBlocks = false;
    
    @SerializedName("HidePlayers")
    public static boolean HidePlayers = false;
    
}
