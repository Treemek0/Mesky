package treemek.mesky.utils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class HypixelCheck {
	
	public static boolean isOnHypixel() {
		if(Minecraft.getMinecraft().thePlayer == null) return false;
		if(Minecraft.getMinecraft().isSingleplayer()) return false;
		if(Minecraft.getMinecraft().thePlayer.getClientBrand() == null) return false;
		if(!Minecraft.getMinecraft().thePlayer.getClientBrand().toLowerCase().contains("hypixel"))	return false;
		
		return true;
	}
}
