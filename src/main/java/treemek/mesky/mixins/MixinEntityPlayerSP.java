package treemek.mesky.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.LockSlot;
import treemek.mesky.utils.Utils;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    @Inject(method = "dropOneItem", at = @At("HEAD"), cancellable = true)
    private void onDropOneItem(boolean dropAll, CallbackInfoReturnable<EntityItem> cir) {
    	if(!SettingsConfig.LockSlots.isOn) return;
        EntityPlayer player = (EntityPlayer)(Object)this;
        
        int slot = player.inventory.currentItem;
        if (LockSlot.lockedSlots.containsKey(slot)) {
            cir.cancel();;  // Cancel dropping by returning null EntityItem
        }
    }
}
