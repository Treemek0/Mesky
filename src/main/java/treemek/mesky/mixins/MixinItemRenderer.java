package treemek.mesky.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import treemek.mesky.config.SettingsConfig;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

	@Inject(method = "renderItem", at = @At("HEAD"))
    private void onRenderItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transform, CallbackInfo ci) {
        if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(
                SettingsConfig.HoldingItemSize.number,
                SettingsConfig.HoldingItemSize.number,
                SettingsConfig.HoldingItemSize.number
            );
        }
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void afterRenderItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transform, CallbackInfo ci) {
        if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON) {
            GlStateManager.popMatrix();
        }
    }
}
