package treemek.mesky.features;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Alerts;
import treemek.mesky.utils.Utils;

public class MaskTimer extends GuiScreen {

	float BonzoCooldownSeconds = 0;
	boolean BonzoMaskActivated = false;
	float SpiritCooldownSeconds = 0;
	boolean SpiritMaskActivated = false;
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		
        String message = event.message.getUnformattedText();
        ItemStack mask = Minecraft.getMinecraft().thePlayer.getCurrentArmor(3);
        
        if(message.contains(":")) return; // only from server not players
        
		if(message.contains("Bonzo's Mask") && message.contains("saved your life!") && SettingsConfig.BonzoTimer) {
			if(mask != null) {
				String bonzoLore = StringUtils.join(Utils.getItemLore(mask), " ");
				
					String[] bonzoLoreSplit = bonzoLore.split(" ");
					for (int i = bonzoLoreSplit.length; i == 0; i--){
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(bonzoLoreSplit[i]));
					    if (bonzoLoreSplit[i].equalsIgnoreCase("Cooldown:")) {
					       BonzoCooldownSeconds = Integer.parseInt(bonzoLoreSplit[i-1]);
					       BonzoMaskActivated = true;
					       Alerts.DisplayCustomAlerts(bonzoLoreSplit[i-1], 1000);
					       break;
					}
					
                }
			}
		}
		
		if(message.contains("Your Spirit Mask saved your life!") && SettingsConfig.SpiritTimer) {
			if (mask != null) {
				SpiritCooldownSeconds = 30;
				SpiritMaskActivated = true;
				Alerts.DisplayCustomAlerts("Spirit Mask", 1000);
			}
		}
	}
	
	@SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.START) {
			if(SpiritCooldownSeconds > 0) {
				SpiritCooldownSeconds -= 0.05;
			}else {
				SpiritMaskActivated = false;
			}
			if(BonzoCooldownSeconds > 0) {
				BonzoCooldownSeconds -= 0.05;
			}else {
				BonzoMaskActivated = false;
			}
		}
	}
	
	@SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (BonzoMaskActivated) {
            renderBonzoMaskTimer(event.resolution, event.partialTicks);
        }
        if (SpiritMaskActivated) {
            renderSpiritMaskTimer(event.resolution, event.partialTicks);
        }
    }
	
	
	
	private void renderBonzoMaskTimer(ScaledResolution resolution, float partialTicks) {
		String cooldownTimer = Math.round(BonzoCooldownSeconds) + "s";
		
		// Calculate the position to render the timer
        int x = resolution.getScaledWidth() / 10;
        int y = resolution.getScaledHeight() / 3 - 10;
		
        
		ResourceLocation textureLocation = new ResourceLocation(Reference.MODID, "textures/Bonzo_Head.png");
        Minecraft.getMinecraft().renderEngine.bindTexture(textureLocation);
        drawModalRectWithCustomSizedTexture(x, y - 5, 0, 0, 17, 17, 17, 17);
        
        RenderHandler.drawText(cooldownTimer, x + 17, y, 1, false, 0xFFFFFF);
	}
	
	private void renderSpiritMaskTimer(ScaledResolution resolution, float partialTicks) {
		String cooldownTimer = Math.round(SpiritCooldownSeconds) + "s";
		
		// Calculate the position to render the timer
        int x = resolution.getScaledWidth() / 10;
        int y = resolution.getScaledHeight() / 3 - 20;
		
		ResourceLocation textureLocation = new ResourceLocation(Reference.MODID, "textures/Spirit_Mask.png");
        Minecraft.getMinecraft().renderEngine.bindTexture(textureLocation);
        drawModalRectWithCustomSizedTexture(x, y - 5, 0, 0, 17, 17, 17, 17);
        
        RenderHandler.drawText(cooldownTimer, x + 17, y, 1, false, 0xFFFFFF);
	}
}
