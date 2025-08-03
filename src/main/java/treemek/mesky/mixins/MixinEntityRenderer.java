package treemek.mesky.mixins;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.features.LockSlot;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.manager.CameraManager;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F", opcode = Opcodes.GETFIELD))
    private float rotationYawModifier(Entity entity) {
        return CameraManager.lockCamera ? CameraManager.getYaw() : entity.rotationYaw;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationYaw:F", opcode = Opcodes.GETFIELD))
    private float prevRotationYawModifier(Entity entity) {
        return CameraManager.lockCamera ? CameraManager.getYaw() : entity.prevRotationYaw;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationPitch:F", opcode = Opcodes.GETFIELD))
    private float rotationPitchModifier(Entity entity) {
        return CameraManager.lockCamera ? CameraManager.getPitch() : entity.rotationPitch;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationPitch:F", opcode = Opcodes.GETFIELD))
    private float prevRotationPitchModifier(Entity entity) {
        return CameraManager.lockCamera ? CameraManager.getPitch() : entity.prevRotationPitch;
    }
}
