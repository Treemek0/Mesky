package treemek.mesky.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.inventory.GuiInventory;

@Mixin(GuiInventory.class)
public class MixinGuiInventory {
    @Inject(method = "drawGuiContainerForegroundLayer", at = @At("HEAD"), cancellable = true)
    private void onDrawForeground(int mouseX, int mouseY, CallbackInfo ci) {
        ci.cancel(); // stops text "Crafting" from rendering
    }
}
