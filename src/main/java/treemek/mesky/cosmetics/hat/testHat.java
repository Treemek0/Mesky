package treemek.mesky.cosmetics.hat;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Mesky;
import treemek.mesky.cosmetics.CosmeticHandler;

public class testHat extends ModelBase{
	
	private final ModelRenderer hat;
	
	public testHat() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.hat = new ModelRenderer(this, 0, 0);
        this.hat.addBox(-4F, 0, -4F, 8, 8, 8);
        this.hat.addBox(-6f, 0, -6f, 12, 2, 12);
    }
	

	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post event)
	{
		EntityPlayer player = event.entityPlayer;
		
		
		
		if (player.equals(Minecraft.getMinecraft().thePlayer) && !player.isInvisible()) // Should render wings onto this player?
		{
			if(CosmeticHandler.HatType == 1) {
				renderHat(player, event.partialRenderTick, event);
			}
		}
	}
	
	
	private void renderHat(EntityPlayer player, float partialTicks, Post event) {
		GL11.glPushMatrix();
        
        ModelRenderer head = event.renderer.getMainModel().bipedHead;
        ModelRenderer body = event.renderer.getMainModel().bipedBody;
        
        float playerHeight = (player.isSneaking())?player.height*0.85f:player.height; // if sneaking then height - 25%
        
        // moving rotation point down (to head rotation point)
        GlStateManager.translate(0, playerHeight-(0.4f), 0);
        
        float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;

        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
        
        // vertical movement of mouse (front and back rotation)
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);

        // returning back for rendering
        GlStateManager.translate(0, 0.4f, 0);
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glColor3f(0.1f, 0.1f, 0.1f);
        hat.render(0.0625f);
        
        
        GL11.glPopMatrix();
    }

}
