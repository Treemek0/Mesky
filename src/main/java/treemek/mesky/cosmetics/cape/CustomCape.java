package treemek.mesky.cosmetics.cape;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;
import treemek.mesky.cosmetics.CosmeticHandler;

public class CustomCape {
	
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post event)
	{
		EntityPlayer player = event.entityPlayer;
		
		if (player.equals(Minecraft.getMinecraft().thePlayer) && !player.isInvisible()) // Should render wings onto this player?
		{
			float scale = (event.renderer.getMainModel().isChild)?0.5f:1;
			if(CosmeticHandler.Cape.isOn) {
				
				ResourceLocation location = new ResourceLocation(Reference.MODID, CosmeticHandler.CapeTexture.text);
				renderCape(player, event.partialRenderTick, scale, location, event.renderer);
			}
		}
	}
	
	public void renderCape(EntityPlayer entity, float partialTicks, float scale, ResourceLocation location, RenderPlayer playerRenderer) {
	    if (!entity.isInvisible() && location != null) {
	        GlStateManager.pushMatrix();

	        
	        GlStateManager.popMatrix();
	    }
	}

	
	
	protected float interpolateRotation(float par1, float par2, float par3)
    {
        float f;

        for (f = par2 - par1; f < -180.0F; f += 360.0F)
        {
            ;
        }

        while (f >= 180.0F)
        {
            f -= 360.0F;
        }

        return par1 + par3 * f;
    }
	
	protected float handleRotationFloat(Entity livingBase, float partialTicks)
    {
        return (float)livingBase.ticksExisted + partialTicks;
    }
}
