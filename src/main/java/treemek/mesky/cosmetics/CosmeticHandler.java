package treemek.mesky.cosmetics;

import treemek.mesky.config.SettingsConfig.Setting;

public class CosmeticHandler {
	public static Setting WingsType = new Setting(0, null, null);
	public static Setting AuraType = new Setting(0, null, null);
	public static Setting HatType = new Setting(0, null, null);
	public static Setting PetType = new Setting(0, null, null);
	public static Setting CapeType = new Setting(0, null, null);
	
	public static Setting CustomCapeTexture = new Setting("", 0D, null, null);
	public static Setting CustomCapeFrequency = new Setting(0.5, null, null);
	public static Setting AllowCatFallingAnimation = new Setting(false, null, null);
}
