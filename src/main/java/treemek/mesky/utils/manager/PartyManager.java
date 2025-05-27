package treemek.mesky.utils.manager;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PartyManager {
	public static boolean isInParty = false;
	
	@SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
		if(event.type != 2) {
			String message = event.message.getUnformattedText();
			
			if(message.startsWith("Party >")) isInParty = true;
			
			if(message.contains(":")) return;
			if(message.contains("invited") && message.contains("to the party! They have 60 seconds to accept.")) isInParty = true;
			if(message.contains("You have joined") && message.contains("party!")) isInParty = true;
			
			if(message.contains("The party was disbanded")) isInParty = false;
			if(message.contains("You left the party.")) isInParty = false;
			if(message.contains("has disbanded the party!")) isInParty = false;
		}
	}
}
