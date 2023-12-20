package treemek.mesky.utils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class HypixelCheck {
	
	public static boolean isOnHypixel() {
		if(Minecraft.getMinecraft().isSingleplayer() || Minecraft.getMinecraft().thePlayer.getClientBrand() == null || !Minecraft.getMinecraft().thePlayer.getClientBrand().toLowerCase().contains("hypixel")) {
			return false;
		}else {
			return true;
		}
	}
	
}
