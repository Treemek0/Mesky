package treemek.mesky.mixins;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.LockSlot;
import treemek.mesky.handlers.RenderHandler;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderHotbarItem", at = @At("TAIL"))
    private void afterRenderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player, CallbackInfo ci) {
    	if(!SettingsConfig.LockSlots.isOn) return;
    	
        if (LockSlot.lockedSlots.containsKey(index)) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 400);

            GL11.glColor4f(1f, 1f, 1f, 1f);

            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableAlpha();

            RenderHandler.drawImage(xPos, yPos, 16, 16, LockSlot.getLockTexture(LockSlot.lockedSlots.get(index)));

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}

