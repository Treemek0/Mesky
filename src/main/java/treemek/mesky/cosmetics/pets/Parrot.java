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
import net.minecraft.item.ItemSkull;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.utils.ImageCache;
import treemek.mesky.utils.Utils;
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
	private ModelRenderer tail;
	
	private Boolean isSkinSlim = null;
	
	public Parrot() {
		this.textureHeight = 64;
		this.textureWidth = 32;
		
		// /mesky downloadtocache C:\Users\macie\OneDrive\Pulpit\Java\Mesky\run\config\parrot.png
		
		this.head = new ModelRenderer(this, 0, 0); // head
        this.head.setTextureOffset(8, 9);
        this.head.addBox(-1f, 0, 0, 2, 3, 2);
        
        this.head.setTextureOffset(16, 9);
        this.head.addBox(-1f, 3f, 0, 2, 1, 4); 

        this.hair = new ModelRenderer(this, 0, 0);
        this.hair.setTextureOffset(4, 16);
        this.hair.addBox(0f, 4, -2.5f, 0, 4, 4);
        
        this.beak = new ModelRenderer(this, 0, 0);
        this.beak.setTextureOffset(0, 16);
        this.beak.addBox(-0.5f, 1.2f, 2, 1, 2, 1); // back
        
        this.beak.setTextureOffset(0, 19);
        this.beak.addBox(-0.5f, 1.6f, 2.9f, 1, 2, 1); // front
        
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setTextureOffset(8, 0);
        this.body.addBox(-1.5f, 0, 0, 3, 6, 3); // 4.5X
        
        this.leftWing = new ModelRenderer(this, 0, 0);
        this.leftWing.setTextureOffset(0, 0);
        this.leftWing.addBox(-1.5f, 0, 0, 1, 5, 3);
        
        this.rightWing = new ModelRenderer(this, 0, 0);
        this.rightWing.setTextureOffset(0, 8);
        this.rightWing.addBox(-2.5f, 0, 0, 1, 5, 3);
        
        
        this.leftLeg = new ModelRenderer(this, 0, 0);
        this.leftLeg.setTextureOffset(20, 0);
        this.leftLeg.addBox(-1.5f, 0, 0, 1, 3, 0);
        
        this.rightLeg = new ModelRenderer(this, 0, 0);
        this.rightLeg.setTextureOffset(22, 0);
        this.rightLeg.addBox(0.5f, 0, 0, 1, 3, 0);
        
        this.leftFeet = new ModelRenderer(this, 0, 0);
        this.leftFeet.setTextureOffset(20, 3);
        this.leftFeet.addBox(-1.5f, 0, 0, 1, 0, 1);
        
        this.rightFeet = new ModelRenderer(this, 0, 0);
        this.rightFeet.setTextureOffset(22, 3);
        this.rightFeet.addBox(0.5f, 0, 0, 1, 0, 1);
        
       this.tail = new ModelRenderer(this, 0, 0);
       this.tail.setTextureOffset(0, 24);
       this.tail.addBox(-1.5f, 0, 0, 3, 1, 4);
	}
	
	
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post event)
	{
		EntityPlayer player = event.entityPlayer;
		
		if (player.equals(Minecraft.getMinecraft().thePlayer) && !player.isInvisible()) // Should render wings onto this player?
		{
			if(CosmeticHandler.PetType.number == 5) {
				float scale = (event.renderer.getMainModel().isChild)?0.5f:1;
				
				renderParrot(event, event.partialRenderTick, scale, new ResourceLocation(Reference.MODID, "textures/models/parrot/red.png"));
			}
		}
	}
	
	private void renderParrot(Post event, float partialTicks, float scale, ResourceLocation textureLocation) {
		GL11.glPushMatrix();
		
		EntityPlayer player = event.entityPlayer;
		
		float addictionalScale = 0.9f;
		
		GlStateManager.scale(addictionalScale, addictionalScale, addictionalScale);
		GlStateManager.scale(scale, scale, scale);
		
		
        float playerHeight = (player.isSneaking())?1.89f*0.85f:1.89f; // if sneaking then height - 25%
        float modelToHeadRotationPointHeight = 0.47f; // from calculation it should be 0.47 (0.4 being head height if player height 1.80) but it was a little off for some reason
        
        if(player.getCurrentArmor(3) != null) {
        	if(player.getCurrentArmor(3).getItem() instanceof ItemSkull) {
	        	playerHeight += pixelsToCordinats(1.7f);
	        	modelToHeadRotationPointHeight += pixelsToCordinats(1.7f);
        	}else {
        		playerHeight += pixelsToCordinats(1.2f);
	        	modelToHeadRotationPointHeight += pixelsToCordinats(1.2f);
        	}
        }
        
        if(scale == 0.5f) { // idk why but when player is child then just scaling puts cat too low
        	playerHeight += 0.3f;
        	modelToHeadRotationPointHeight += 0.3f;
        }
        
        // moving rotation point down (to head rotation point)
        GlStateManager.translate(0, (playerHeight-modelToHeadRotationPointHeight) / addictionalScale, 0);
        
        float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;

        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);      
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F); // vertical movement of mouse (front and back rotation)

        
        GlStateManager.translate(0, modelToHeadRotationPointHeight / addictionalScale, pixelsToCordinats(2)/addictionalScale);
        
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureLocation);
        
        float f11 = (System.currentTimeMillis() % 1000) / 1000F * (float) (Math.PI * 2);
        
		head.offsetY = pixelsToCordinats(7f); // 7.2 from 30 angle but its 1.2 pixel down in torso
		head.rotateAngleZ = degreeToRadian((float) (Math.sin(f11 + Math.PI/2)*2.5));
		head.rotateAngleY = degreeToRadian((float) (Math.sin(f11 + Math.PI/4)*5));
		// head offsetZ is +3 to body
		head.render(0.0625f);
		
		copyHeadRotation(head, hair);
		hair.rotateAngleX += degreeToRadian(20);
		hair.render(0.0625f);
		
		copyHeadRotation(head, beak);
		beak.render(0.0625f);
		
		
		body.offsetY = pixelsToCordinats(3);
		body.offsetZ = pixelsToCordinats(-3);
		body.rotateAngleX = degreeToRadian(30);
		body.render(0.0625f);
		
		tail.offsetY = pixelsToCordinats(3.5f);
		tail.offsetZ = pixelsToCordinats(-2);
		tail.rotateAngleX = degreeToRadian(160);
		tail.render(0.0625f);
		 
		float WingRotation = (float) (210 + Math.abs(Math.sin(f11)*5));
		float wingZ = 2f;
		float wingY = 5.9f;
		leftWing.rotateAngleX = degreeToRadian(WingRotation);
		leftWing.offsetX = pixelsToCordinats(3f);
		leftWing.offsetZ = pixelsToCordinats(wingZ);
		leftWing.offsetY = pixelsToCordinats(wingY);
		leftWing.render(0.0625f);
		
		rightWing.rotateAngleX = degreeToRadian(WingRotation);
		rightWing.offsetX = 0;
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
	
	public static void copyHeadRotation(ModelRenderer source, ModelRenderer dest)
    {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        // it copies head rotation, but if we would just give each model correct offsets then they would rotate around their pivot point
        // so we copy head offset and give models remaining offset in addBox(), so if ear had Y = 6, X = 3 offset then we take 6-headOffsetY & 3-headOffsetX and assign to addBox
        // and thanks to that models have head rotation point because i dont really understand minecraft model rotation points
        dest.offsetX = source.offsetX;
        dest.offsetY = source.offsetY;
        dest.offsetZ = source.offsetZ;
    }
}
