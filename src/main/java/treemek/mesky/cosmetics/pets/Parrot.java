package treemek.mesky.cosmetics.pets;

import java.lang.reflect.Field;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;
import treemek.mesky.cosmetics.CosmeticHandler;
import net.minecraft.client.model.ModelBiped;


public class Parrot extends ModelBase{
	private ModelRenderer head;
	private ModelRenderer body;
	private ModelRenderer beak;
	private ModelRenderer leftLeg;
	private ModelRenderer rightLeg;
	private ModelRenderer leftFeet;
	private ModelRenderer rightFeet;
	private ModelRenderer rightWing;
	private ModelRenderer leftWing;
	private ModelRenderer beakFront;
	private ModelRenderer hair;

	private Boolean isSkinSlim = null;
	
	public Parrot() {
		
		this.head = new ModelRenderer(this, 0, 0); // head
        this.head.setTextureOffset(0, 0);
        this.head.addBox(5, 0, 0, 2, 3, 2);
        this.head.addBox(5, 3, 0, 2, 1, 4); 

        this.hair = new ModelRenderer(this, 0, 0);
        this.hair.setTextureOffset(0, 0);
        this.hair.addBox(6, 0, 0, 0, 4, 4);
        
        this.beak = new ModelRenderer(this, 0, 0);
        this.beak.setTextureOffset(0, 0);
        this.beak.addBox(5.5f, 0, 0, 1, 2, 1); // back
        this.beak.addBox(5.5f, 1, 1, 1, 1, 1); // front
        
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setTextureOffset(0, 0);
        this.body.addBox(4.5f, 0, 0, 3, 6, 3);
        
        this.leftWing = new ModelRenderer(this, 0, 0);
        this.leftWing.setTextureOffset(0, 0);
        this.leftWing.addBox(0, 0, 0, 1, 5, 3);
        
        this.rightWing = new ModelRenderer(this, 0, 0);
        this.rightWing.setTextureOffset(0, 0);
        this.rightWing.addBox(-1, 0, 0, 1, 5, 3);
        
        
        
        this.leftLeg = new ModelRenderer(this, 0, 0);
        this.leftLeg.setTextureOffset(0, 0);
        this.leftLeg.addBox(6f, 0, 0, 1, 3, 0);
        
        this.rightLeg = new ModelRenderer(this, 0, 0);
        this.rightLeg.setTextureOffset(0, 0);
        this.rightLeg.addBox(4.5f, 0, 0, 1, 3, 0);
        
        this.leftFeet = new ModelRenderer(this, 0, 0);
        this.leftFeet.setTextureOffset(0, 0);
        this.leftFeet.addBox(6f, 0, 0, 1, 0, 1);
        
        this.rightFeet = new ModelRenderer(this, 0, 0);
        this.rightFeet.setTextureOffset(0, 0);
        this.rightFeet.addBox(4.5f, 0, 0, 1, 0, 1);
        
       
	}
	
//	
//	@SubscribeEvent
//	public void onRenderPlayer(RenderPlayerEvent.Post event)
//	{
//		EntityPlayer player = event.entityPlayer;
//		
//		if (player.equals(Minecraft.getMinecraft().thePlayer) && !player.isInvisible()) // Should render wings onto this player?
//		{
//			float scale = (event.renderer.getMainModel().isChild)?0.5f:1;
//			
//			renderParrot(event, event.partialRenderTick, scale, null);
//		}
//	}
//	
	
	private void renderParrot(Post event, float partialTicks, float scale, ResourceLocation textureLocation) {
		GL11.glPushMatrix();
		
		EntityPlayer player = event.entityPlayer;
		
		GlStateManager.scale(scale, scale, scale);
		
		
		float shoulderHeight = (player.isSneaking())?1.42f*0.85f:1.42f; // if sneaking then height - 25%
    	if(((AbstractClientPlayer)event.entityPlayer).getSkinType().equals("slim")) {
    		shoulderHeight -= pixelsToCordinats(0.5f);
    	}
        
    	GlStateManager.translate(0, shoulderHeight, 0);
    	
        float yaw = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);      

        GlStateManager.translate(0, -pixelsToCordinats(6), 0); // rotation point to body
        if(player.isSneaking()) {
    		GL11.glRotated(22.9, 1, 0, 0);
    	}

        //setAngles(event.renderer.getMainModel().bipedLeftArm);
        
        GlStateManager.translate(0, pixelsToCordinats(6), 0);
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
		head.offsetY = pixelsToCordinats(7f); // 7.2 from 30 angle but its 1.2 pixel down in torso
		// head offsetZ is +3 to body
		head.render(0.0625f);
		
		hair.offsetY = pixelsToCordinats(11f);
		hair.offsetZ = pixelsToCordinats(-1);
		hair.rotateAngleX = degreeToRadian(20);
		hair.render(0.0625f);
		
		
		beak.offsetY = pixelsToCordinats(8.2f);
		beak.offsetZ = pixelsToCordinats(2);
		beak.render(0.0625f);
		
		
		body.offsetY = pixelsToCordinats(3);
		body.offsetZ = pixelsToCordinats(-3);
		body.rotateAngleX = degreeToRadian(30);
		body.render(0.0625f);
		
		
		 float f11 = (System.currentTimeMillis() % 1000) / 1000F * (float) (Math.PI * 2);
		 
		float WingRotation = (float) (180 + Math.abs(Math.sin(f11)*40));
		float wingZ = 2f;
		float wingY = 5.9f;
		leftWing.rotateAngleX = degreeToRadian(WingRotation);
		leftWing.rotateAngleZ = degreeToRadian((float)Math.abs(Math.sin(f11+ Math.PI/2)*80));
		leftWing.offsetX = pixelsToCordinats(7.5f);
		leftWing.offsetZ = pixelsToCordinats(wingZ);
		leftWing.offsetY = pixelsToCordinats(wingY);
		leftWing.render(0.0625f);
		
		rightWing.rotateAngleX = degreeToRadian(WingRotation);
		rightWing.rotateAngleZ = degreeToRadian((float)-Math.abs(Math.sin(f11+ Math.PI/2)*80));
		rightWing.offsetX = pixelsToCordinats(4.5f);
		rightWing.offsetZ = pixelsToCordinats(wingZ);
		rightWing.offsetY = pixelsToCordinats(wingY);
		rightWing.render(0.0625f);
		
		
		
		leftLeg.offsetZ = pixelsToCordinats(-1.5f);
		leftLeg.render(0.0625f);
		
		rightLeg.offsetZ = pixelsToCordinats(-1.5f);
		rightLeg.render(0.0625f);
		
		leftFeet.offsetZ = pixelsToCordinats(-1.5f);
		leftFeet.render(0.0625f);
		
		rightFeet.offsetZ = pixelsToCordinats(-1.5f);
		rightFeet.render(0.0625f);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glPopMatrix();
	}
	
	public float degreeToRadian(float a) {
		return a * ((float)Math.PI / 180F);
	}
	
	public float radiansTodegree(float a) {
		return a / ((float)Math.PI / 180F);
	}
	
	public float pixelsToCordinats(float pixel) {
		return pixel*0.0625f;
	}
	
	private float interpolate(float yaw1, float yaw2, float percent)
	{
		float f = (yaw1 + (yaw2 - yaw1) * percent) % 360;

		if (f < 0)
		{
			f += 360;
		}

		return f;
	}

	public void setAngles(ModelRenderer source)
    {
        GL11.glRotated(radiansTodegree(source.rotateAngleX), 1, 0, 0);
        GL11.glRotated(radiansTodegree(source.rotateAngleY), 0, 1, 0);
        GL11.glRotated(radiansTodegree(-source.rotateAngleZ), 0, 0, 1);
    }
	
}
