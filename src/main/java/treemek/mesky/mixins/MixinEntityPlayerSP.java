package treemek.mesky.mixins;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.LockSlot;
import treemek.mesky.handlers.soundHandler.SoundsHandler;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.chat.ChatFilter;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    @Inject(method = "dropOneItem", at = @At("HEAD"), cancellable = true)
    private void onDropOneItem(boolean dropAll, CallbackInfoReturnable<EntityItem> cir) {
    	if(!SettingsConfig.LockSlots.isOn) return;
        EntityPlayer player = (EntityPlayer)(Object)this;
        
        int slot = player.inventory.currentItem;
        if (LockSlot.lockedSlots.containsKey(slot)) {
        	if(!LockSlot.dropKeyPressed) {
        		ChatComponentText dropMessage = new ChatComponentText(EnumChatFormatting.RED + "[Mesky] \u26A0 You cannot drop this item. Unlock the slot first.");
        		ChatStyle style = new ChatStyle();
        		style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Unlock slot in inventory using [KEY " + SettingsConfig.LockSlotsKeybind.keybind.getKeysAsString() + "]")));
        		dropMessage.setChatStyle(style);
        		ChatFilter.checkFilterAndSend(SettingsConfig.dropItem_filter, dropMessage);
        		SoundsHandler.playSound("mesky:block", 1, 0.1f);
        	}
            cir.cancel();  // cancel dropping by returning null EntityItem
        }
    }
}
