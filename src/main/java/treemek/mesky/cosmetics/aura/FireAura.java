package treemek.mesky.cosmetics.aura;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;
import treemek.mesky.cosmetics.CosmeticHandler;

public class FireAura extends ModelBase{
	private ModelRenderer fire;
	private ModelRenderer fire2;
	
	private float fireY = 0;
	private float fireRotation = 0;


	
	public FireAura() {
		this.textureWidth = 112;
        this.textureHeight = 68;
		
	 	this.fire = new ModelRenderer(this, 0, 0);
        this.fire.setTextureOffset(0, 0);
        this.fire.addBox(-14, 2, -14, 28, 40, 28); // head
        
        this.fire2 = new ModelRenderer(this, 0, 0);
        this.fire2.setTextureOffset(0, 0);
        this.fire2.addBox(-14, 2, -14, 28, 40, 28); // head
	}
	
	// TODO everything
	
//	@SubscribeEvent
//	public void onRenderPlayer(RenderPlayerEvent.Post event)
//	{
//		EntityPlayer player = event.entityPlayer;
//		
//		if (player.equals(Minecraft.getMinecraft().thePlayer) && !player.isInvisible()) // Should render wings onto this player?
//		{
//			float scale = (event.renderer.getMainModel().isChild)?0.5f:1;
//			renderAura(player, scale);
//		}
//	}


	private void renderAura(EntityPlayer player, float partialTicks) {
		GL11.glPushMatrix();
		
		float f11 = (System.currentTimeMillis() % 1000) / 1000F * (float) (Math.PI*2);
		
		fireRotation += 0.01f;
		if(fireRotation >= 360) fireRotation = 0;
		
		fire.offsetY =  pixelsToCordinats((float)(Math.sin(f11)*2));
		fire2.offsetY = pixelsToCordinats((float)(Math.sin(f11)*2));
		
		fire.rotateAngleY = degreeToRadian(fireRotation);
		fire2.rotateAngleY = degreeToRadian(fireRotation + 90);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/fire.png"));
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		fire.render(0.0625f);
		fire2.render(0.0625f);
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		
		GL11.glPopMatrix();
	}
	
	
	public float degreeToRadian(float a) {
		return a * ((float)Math.PI / 180F);
	}
	
	public float pixelsToCordinats(float pixel) {
		return pixel*0.0625f;
	}
	
}
