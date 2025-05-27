package treemek.mesky.features;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.utils.Utils;

public class SeaCreaturesDetection {
	public static int gwsCounter = 0;
	public static int tigerCounter = 0;
	public static int blueCounter = 0;
	public static int nurseCounter = 0;
	
	private String lastMessage = "";
	
	@SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
		if(event.type == 2) return;
		String message = event.message.getUnformattedText();
		if(message.contains(":")) return;
		
		if(SettingsConfig.LegendarySeaCreaturesNotification.isOn) {
			if(message.contains("The Water Hydra has come to test your strength.") && SettingsConfig.WaterHydraNotification.isOn) Utils.writeToPartyMinecraft("@# Fished-up Water Hydra");
			if(message.contains("The Sea Emperor arises from the depths.") && SettingsConfig.SeaEmperorNotification.isOn) Utils.writeToPartyMinecraft("@# Fished-up Sea Emperor");
			if(message.contains("WOAH! A Plhlegblast appeared.") && SettingsConfig.PlhlegblastNotification.isOn) Utils.writeToPartyMinecraft("@# Fished-up Plhlegblast");
			if(message.contains("You hear a massive rumble as Thunder emerges.") && SettingsConfig.ThunderNotification.isOn) Utils.writeToPartyMinecraft("@# Fished-up Thunder");
			if(message.contains("An Abyssal Miner breaks out of the water!") && SettingsConfig.AbyssalMinerNotification.isOn) Utils.writeToPartyMinecraft("@# Fished-up Abyssal Miner");
			if(message.contains("The spirit of a long lost Phantom Fisher has come to haunt you.") && SettingsConfig.PhantomFisherNotification.isOn) Utils.writeToPartyMinecraft("@# Fished-up Phantom Fisher");
			if(message.contains("This can't be! The manifestation of death himself!") && SettingsConfig.GrimReaperNotification.isOn) Utils.writeToPartyMinecraft("@# Fished-up Grim Reaper");
			if(message.contains("What is this creature!?") && SettingsConfig.YetiNotification.isOn) Utils.writeToPartyMinecraft("@# Fished-up Yeti");
			if(message.contains("A Reindrake forms from the depths.") && SettingsConfig.ReindrakeNotification.isOn) Utils.writeToPartyMinecraft("@# Fished-up Reindrake");
			if(message.contains("Hide no longer, a Great White Shark has tracked your scent and thirsts for your blood!") && SettingsConfig.GreatSharkNotification.isOn) Utils.writeToPartyMinecraft("@# Fished-up Great Shark");
		}
		
		if(SettingsConfig.SharkCounter.isOn) {
			if(message.contains("The festival is now underway! Break out your fishing rods and watch out for sharks!")) resetCounter();
			if(message.contains("The festival has concluded! Time to dry off and repair your rods!")) {
				int allCounter = gwsCounter + tigerCounter + blueCounter + nurseCounter;
				Utils.addMinecraftMessageWithPrefix(EnumChatFormatting.BOLD.AQUA + "This Fishing festival you fished-up: " + EnumChatFormatting.DARK_AQUA + allCounter + " sharks");
				if(gwsCounter > 0) Utils.addMinecraftMessage(EnumChatFormatting.GOLD + "@# Great White Sharks: " + gwsCounter);
				if(tigerCounter > 0) Utils.addMinecraftMessage(EnumChatFormatting.DARK_PURPLE + "@# Tiger Sharks: " + tigerCounter);
				if(blueCounter > 0) Utils.addMinecraftMessage(EnumChatFormatting.BLUE + "@# Blue Sharks: " + blueCounter);
				if(nurseCounter > 0) Utils.addMinecraftMessage(EnumChatFormatting.GREEN + "@# Nurse Sharks: " + nurseCounter);
				resetCounter();
			}
			
			if(message.contains("Hide no longer, a Great White Shark has tracked your scent and thirsts for your blood!")) gwsCounter++;
			if(message.contains("A striped beast bounds from the depths, the wild Tiger Shark!")) tigerCounter++;
			if(message.contains("You spot a fin as blue as the water it came from, it's a Blue Shark.")) blueCounter++;
			if(message.contains("A tiny fin emerges from the water, you've caught a Nurse Shark.")) nurseCounter++;
			
			if(message.contains("It's a Double Hook!")) {
				if(lastMessage.contains("Hide no longer, a Great White Shark has tracked your scent and thirsts for your blood!")) gwsCounter++;
				if(lastMessage.contains("A striped beast bounds from the depths, the wild Tiger Shark!")) tigerCounter++;
				if(lastMessage.contains("You spot a fin as blue as the water it came from, it's a Blue Shark.")) blueCounter++;
				if(lastMessage.contains("A tiny fin emerges from the water, you've caught a Nurse Shark.")) nurseCounter++;
			}
		}
		
		lastMessage = message;
	}
	
	private static void resetCounter() {
		gwsCounter = 0;
		tigerCounter = 0;
		blueCounter = 0;
		nurseCounter = 0;
	}
}
