package treemek.mesky.handlers;

import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class HypixelCheck {

	public static boolean isOnHypixel = false;
	
	public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		String serverIP = event.manager.getRemoteAddress().toString();
	
		if(serverIP.startsWith("/")) serverIP = serverIP.substring(1);
		if(serverIP.contains(":")) serverIP = serverIP.split(":")[0];
    
		isOnHypixel = false;
    
        if(serverIP.equalsIgnoreCase("hypixel.net")) isOnHypixel = true;
        if(serverIP.equalsIgnoreCase("mc.hypixel.net")) isOnHypixel = true;
    
	}
	
}
