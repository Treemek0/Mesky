package treemek.mesky.cosmetics.pets;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Mesky;
import treemek.mesky.Reference;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.utils.Utils;

public class Cat extends ModelBase{

	private ModelRenderer head;
	private ModelRenderer nose;
	private ModelRenderer leftEar;
	private ModelRenderer rightEar;
	private ModelRenderer body;
	private ModelRenderer tail;
	private ModelRenderer leftBackLeg;
	private ModelRenderer rightBackLeg;
	private ModelRenderer leftFrontLeg;
	private ModelRenderer rightFrontLeg;
	
	// addBox translation and GL11 translation is different in rotation
	// addBox translate box location so rotation point is still in the same 0,0,0 place
	// GL11 translate rotation point with himself, because we translate whole GL11 not just box
	
	public Cat() {
        this.textureWidth = 44;
        this.textureHeight = 27;

        // ModelRenderer to gl11 (x * 0.0625) [because of render(0.0625)]
        
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setTextureOffset(0, 0);
        this.head.addBox(-2.5F, 0, 0, 5, 5, 5); // head

        this.nose = new ModelRenderer(this, 0, 10);
        this.nose.setTextureOffset(0, 10);
        this.nose.addBox(-1.5F, 0, 5, 3, 2, 1); // nose
        
        this.leftEar = new ModelRenderer(this, 0, 13);
        this.leftEar.setTextureOffset(0, 13);
        this.leftEar.addBox(0.5F, 5, 1, 2, 1, 1); // L ear

        this.rightEar = new ModelRenderer(this, 0, 15);
        this.rightEar.setTextureOffset(0, 15);
        this.rightEar.addBox(-2.5F, 5, 1, 2, 1, 1); // R ear
        
        this.body = new ModelRenderer(this, 20, 0);
        this.body.setTextureOffset(20, 0);
        this.body.addBox(-2F, 0, -5F, 4, 4, 8); // body

        this.tail = new ModelRenderer(this, 20, 12);
        this.tail.setTextureOffset(0, 10);
        this.tail.addBox(-0.5F, 0, 0, 1, 1, 8); // tail
        
        this.leftBackLeg = new ModelRenderer(this, 20, 14);
        this.leftBackLeg.setTextureOffset(0, 19);
        this.leftBackLeg.addBox(0.3F, 0, 0, 2, 5, 2); // left back leg


        this.rightBackLeg = new ModelRenderer(this, 28, 14);
        this.rightBackLeg.setTextureOffset(8, 19);
        this.rightBackLeg.addBox(-2.3F, 0, 0, 2, 5, 2); // right back leg

        this.leftFrontLeg = new ModelRenderer(this, 36, 14);
        this.leftFrontLeg.setTextureOffset(16, 19);
        this.leftFrontLeg.addBox(0.5F, 0, 0, 2, 6, 2); // left front leg 
    
        this.rightFrontLeg = new ModelRenderer(this, 36, 14);
        this.rightFrontLeg.setTextureOffset(24, 19);
        this.rightFrontLeg.addBox(-2.5F, 0, 0, 2, 6, 2); // right front leg 
	}
	

	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post event)
	{
		EntityPlayer player = event.entityPlayer;
		
		if (player.equals(Minecraft.getMinecraft().thePlayer) && !player.isInvisible()) // Should render wings onto this player?
		{
			float scale = (event.renderer.getMainModel().isChild)?0.5f:1;

			switch (CosmeticHandler.PetType.number.intValue()) {
			case 1:
				renderCat(player, event.partialRenderTick, scale, new ResourceLocation(Reference.MODID, "textures/models/cat/CalicoCat.png"));
				break;
			case 2:
				renderCat(player, event.partialRenderTick, scale, new ResourceLocation(Reference.MODID, "textures/models/cat/GrayCat.png"));
				break;
			case 3:
				renderCat(player, event.partialRenderTick, scale, new ResourceLocation(Reference.MODID, "textures/models/cat/BlackCat.png"));
				break;
			case 4:
				renderCat(player, event.partialRenderTick, scale, new ResourceLocation(Reference.MODID, "textures/models/cat/RudyCat.png"));
				break;
			}

		}
	}
	
	double motionYforce = 0;
	double tailForce = 0;
	private void renderCat(EntityPlayer player, float partialTicks, float scale, ResourceLocation textureLocation) {
		GL11.glPushMatrix();
		
		// gl11 to ModelRenderer (x / 0.0625)

        GlStateManager.scale(scale, scale, scale);
        
        
        
        float playerHeight = (player.isSneaking())?1.87f*0.85f:1.87f; // if sneaking then height - 25%
        float modelToHeadRotationPointHeight = 0.45f; // from calculation it should be 0.47 (0.4 being head height if player height 1.80) but it was a little off for some reason
        
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
        GlStateManager.translate(0, playerHeight-modelToHeadRotationPointHeight, 0);
        
        float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        
        GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);      
        GlStateManager.rotate(pitch - 10, 1.0F, 0.0F, 0.0F); // vertical movement of mouse (front and back rotation)

        // returning back for rendering
        GlStateManager.translate(0, modelToHeadRotationPointHeight, 0.1);
        
        GL11.glEnable(GL11.GL_CULL_FACE);
        Minecraft.getMinecraft().renderEngine.bindTexture(textureLocation);
        
        
        float f11 = (System.currentTimeMillis() % 1000) / 1000F * (float) (Math.PI*2);
        // -------
        // head
        if(CosmeticHandler.AllowCatFallingAnimation.isOn) {
        	double targetMotionYforce = Math.max(-30, (player.motionY * 10)) * Utils.influenceBasedOnDistanceFromAToB(pitch, 0, 45) + (Math.sin(f11*2)*2 * (1-Utils.influenceBasedOnDistanceFromAToB(player.motionY, 0, 0.5f)));
    		motionYforce += (targetMotionYforce - motionYforce) * 0.02f;
        }else {
        	motionYforce = 0;
        }
		head.rotateAngleZ = degreeToRadian((float)(Math.sin(f11)*2 * (Utils.influenceBasedOnDistanceFromAToB(player.motionY, 0, 0.2f))));
        head.rotateAngleX = degreeToRadian((float) (25 + motionYforce));
        head.offsetY = pixelsToCordinats(1);
        head.offsetZ = pixelsToCordinats(2);
        head.render(0.0625f); // head

        copyHeadRotation(this.head, this.nose);
        nose.render(0.0625f); // nose

        copyHeadRotation(this.head, this.leftEar);
        leftEar.render(0.0625f); // L ear

        copyHeadRotation(this.head, this.rightEar);
        rightEar.render(0.0625f); // R ear

        // ------
        // body
        
        body.render(0.0625f); // body
        
        float xtailRotation = degreeToRadian((float) (180 + ((Math.sin(f11)*20))));
        tail.rotateAngleX = xtailRotation;
        tail.offsetY = 0.25f;
        tail.offsetZ = -0.25f;
        tail.render(0.0625f); // tail
        
        // -------
        // back legs
        
        float xBackLegRotation = degreeToRadian((float) (180 + 20 - motionYforce));
        float zBackLegRotation = degreeToRadian(15);
		leftBackLeg.rotateAngleX = xBackLegRotation;
		leftBackLeg.rotateAngleZ = zBackLegRotation;
		leftBackLeg.offsetY = 0.110f;
		leftBackLeg.offsetZ = -0.191f;
        leftBackLeg.render(0.0625f); // left back leg

        rightBackLeg.rotateAngleX = xBackLegRotation; // back/front
        rightBackLeg.rotateAngleZ = -zBackLegRotation; // right/left
        rightBackLeg.offsetY = 0.110f;
        rightBackLeg.offsetZ = -0.191f;
        rightBackLeg.render(0.0625f); // right back leg
        
        // -------
        // front legs
        
        float xFrontLegRotation = degreeToRadian((float) (90 + 20 + motionYforce/3));
        float yFrontLegRotation = degreeToRadian(25);
        leftFrontLeg.rotateAngleX = xFrontLegRotation; // top and bottom of leg are on Y axis not Z
        leftFrontLeg.rotateAngleY = yFrontLegRotation;
        leftFrontLeg.offsetY = pixelsToCordinats(2.5f);
        leftFrontLeg.offsetZ = pixelsToCordinats(1);
        leftFrontLeg.render(0.0625f);
        
        rightFrontLeg.rotateAngleX = xFrontLegRotation; // top and bottom of leg are on Y axis not Z
        rightFrontLeg.rotateAngleY = -yFrontLegRotation;
        rightFrontLeg.offsetY = pixelsToCordinats(2.5f);
        rightFrontLeg.offsetZ = pixelsToCordinats(1);
        rightFrontLeg.render(0.0625f);
        
        GL11.glColor3f(1f, 1f, 1f);
        GL11.glPopMatrix();
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
	
	public float degreeToRadian(float a) {
		return a * ((float)Math.PI / 180F);
	}
	
	
	public float pixelsToCordinats(float pixel) {
		return pixel*0.0625f;
	}


}
