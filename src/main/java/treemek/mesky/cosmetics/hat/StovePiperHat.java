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

public class StovePiperHat extends ModelBase{
	
	private final ModelRenderer hat;
	
	public StovePiperHat() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.hat = new ModelRenderer(this, 0, 0);
        this.hat.addBox(-4F, 0, -4F, 8, 8, 8);
        this.hat.addBox(-6f, 0, -6f, 12, 2, 12);
        this.hat.addBox(-4f, 2, -4f, 8, 1, 8);

    }
	

	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post event)
	{
		EntityPlayer player = event.entityPlayer;
		
		
		
		if (player.equals(Minecraft.getMinecraft().thePlayer) && !player.isInvisible()) // Should render wings onto this player?
		{
			if(CosmeticHandler.HatType.number == 1) {
				float scale = (event.renderer.getMainModel().isChild)?0.5f:1;
				renderHat(player, event.partialRenderTick, scale);
			}
		}
	}
	
	
	private void renderHat(EntityPlayer player, float partialTicks, float scale) {
		GL11.glPushMatrix();
        
        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.scale(scale, scale, scale);
        
        float playerHeight = (player.isSneaking())?1.80f*0.85f:1.80f; // if sneaking then height - 25%
        float modelToHeadRotationPointHeight = 0.40f;
        
        if(player.getCurrentArmor(3) != null) {
        	playerHeight += pixelsToCordinats(1.7f);
        	modelToHeadRotationPointHeight += pixelsToCordinats(1.7f);
        }
        
        if(scale == 0.5f) { // idk why but when player is child then just scaling puts hat too low
        	playerHeight += 0.3f;
        	modelToHeadRotationPointHeight += 0.3f;
        }
        
        // moving rotation point down (to head rotation point)
        GlStateManager.translate(0, playerHeight-modelToHeadRotationPointHeight, 0);
        
        float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;

        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);      
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F); // vertical movement of mouse (front and back rotation)

        // returning back for rendering
        GlStateManager.translate(0, modelToHeadRotationPointHeight, 0);
        
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glColor3f(0.1f, 0.1f, 0.1f);
        GL11.glEnable(GL11.GL_CULL_FACE);
        
        hat.cubeList.get(0).render(tessellator.getWorldRenderer(), 0.0625f);
        hat.cubeList.get(1).render(tessellator.getWorldRenderer(), 0.0625f);
   
        GL11.glColor3f(0.9f, 0.9f, 0.9f);
        GlStateManager.scale(1.15f, 0.9f, 1.15f);
        hat.cubeList.get(2).render(tessellator.getWorldRenderer(), 0.0625f);
        
        GL11.glColor3f(1f, 1f, 1f);
        
        GL11.glPopMatrix();
    }
	
	public float pixelsToCordinats(float pixel) {
		return pixel*0.0625f;
	}

}
