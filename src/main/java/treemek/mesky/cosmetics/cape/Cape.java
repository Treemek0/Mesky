package treemek.mesky.cosmetics.cape;

import java.awt.image.BufferedImage;
import java.io.File;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.utils.ImageCache;
import treemek.mesky.utils.Utils;

public class Cape extends ModelBase{
	
	private ModelRenderer bipedCape;
	
	public Cape() {
		this.bipedCape = new ModelRenderer(this, 0, 0);
        this.bipedCape.setTextureSize(64, 32);
        this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, 0.0625f);
	}
	
	int currentAnimation = 0;
	int renderTick = 0;
	
	// TODO custom cape and test with high res texture
	
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post event)
	{
		EntityPlayer player = event.entityPlayer;
		
		if (player.equals(Minecraft.getMinecraft().thePlayer) && !player.isInvisible()) // Should render wings onto this player?
		{
			float scale = (event.renderer.getMainModel().isChild)?0.5f:1;
			renderTick++;
			float ticksInSeconds = Minecraft.getMinecraft().getDebugFPS();
			
			switch (CosmeticHandler.CapeType.number.intValue()) {
				case 1: {
					float animationFrequency = 0.2f;
					float amountOfAnimations = 5;
					
					if(renderTick > animationFrequency * ticksInSeconds) {
						renderTick = 0;
						currentAnimation++;
					}
					
					if(currentAnimation > amountOfAnimations) {
						currentAnimation = 0;
					}
					
					// https://minecraft.novaskin.me/skin/5210543441/MINECON-Live-2019-Founder-s-Cape
					ResourceLocation location = new ResourceLocation(Reference.MODID, "textures/models/capes/gold_creeper/cape" + currentAnimation + ".png");
					renderCape(player, event.partialRenderTick, scale, location, event.renderer);
					break;
				}
				case 2: {
					float animationFrequency = 0f;
					float amountOfAnimations = 0;
					
					if(renderTick > animationFrequency * ticksInSeconds) {
						renderTick = 0;
						currentAnimation++;
					}
					
					if(currentAnimation > amountOfAnimations) {
						currentAnimation = 0;
					}
					
					// https://minecraft.novaskin.me/skin/4589233036.png
					ResourceLocation location = new ResourceLocation(Reference.MODID, "textures/models/capes/bagno/cape" + currentAnimation + ".png");
					renderCape(player, event.partialRenderTick, scale, location, event.renderer);
					break;
				}
				case 3: {
					float animationFrequency = 0f;
					float amountOfAnimations = 0;
					
					if(renderTick > animationFrequency * ticksInSeconds) {
						renderTick = 0;
						currentAnimation++;
					}
					
					if(currentAnimation > amountOfAnimations) {
						currentAnimation = 0;
					}
					
					// https://minecraft.novaskin.me/skin/5684533241/Cross-Christian-Bible-Cape-Elytra
					ResourceLocation location = new ResourceLocation(Reference.MODID, "textures/models/capes/cross/cape" + currentAnimation + ".png");
					renderCape(player, event.partialRenderTick, scale, location, event.renderer);
					break;
				}
				case 4: {
					float animationFrequency = 5f;
					float amountOfAnimations = 1;
					
					if(renderTick > animationFrequency * ticksInSeconds) {
						renderTick = 0;
						currentAnimation++;
					}
					
					if(currentAnimation > amountOfAnimations) {
						currentAnimation = 0;
					}
					
					// https://minecraft.novaskin.me/skin/5345665204/Minecraft-Bee-Cape-Elytra
					ResourceLocation location = new ResourceLocation(Reference.MODID, "textures/models/capes/bee/cape" + currentAnimation + ".png");
					renderCape(player, event.partialRenderTick, scale, location, event.renderer);
					break;
				}
				case 5: {
					float animationFrequency = CosmeticHandler.CustomCapeFrequency.number.floatValue();
					float amountOfAnimations = CosmeticHandler.CustomCapeTexture.number.intValue() - 1;
					
					if(renderTick > animationFrequency * ticksInSeconds) {
						renderTick = 0;
						currentAnimation++;
					}
					
					if(currentAnimation > amountOfAnimations) {
						currentAnimation = 0;
					}
					
					// Custom
					String folderPath = CosmeticHandler.CustomCapeTexture.text;
					
					String filePath;
					if(folderPath.contains("\\")) {
						filePath = folderPath + "\\cape" + currentAnimation;
					}else {
						filePath = folderPath + "/cape" + currentAnimation;
					}
					
					BufferedImage buff = ImageCache.bufferedTextureCache.get(filePath);
					if(buff != null) {
						ResourceLocation location = ImageCache.resourceLocationCache.get(buff);
						if(location != null) {
							renderCape(player, event.partialRenderTick, scale, location, event.renderer);
							break;
						}
					}
				}
			}
		}
	}
	
	public void renderCape(EntityPlayer player, float partialTicks, float scale, ResourceLocation location, RenderPlayer playerRenderer) {
	    if (!player.isInvisible() && location != null) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            playerRenderer.bindTexture(location);
            
            if (player.isSneaking()){ // its from ModelPlayer and i was using its cape, but some mod was changing player renderer so correct class function wasnt being called, thats why i had to change to my own model and copy it here 
                this.bipedCape.rotationPointY = 2.0F;
            }else{
                this.bipedCape.rotationPointY = 0.0F;
            }
            
            double d0 = player.prevChasingPosX + (player.chasingPosX - player.prevChasingPosX) * (double)partialTicks - (player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks);
            double d1 = player.prevChasingPosY + (player.chasingPosY - player.prevChasingPosY) * (double)partialTicks - (player.prevPosY + (player.posY - player.prevPosY) * (double)partialTicks);
            double d2 = player.prevChasingPosZ + (player.chasingPosZ - player.prevChasingPosZ) * (double)partialTicks - (player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks);
            float f = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
            double d3 = (double)MathHelper.sin(f * (float)Math.PI / 180.0F);
            double d4 = (double)(-MathHelper.cos(f * (float)Math.PI / 180.0F));
            float f1 = (float)d1 * 10.0F;
            f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;

            if (f2 < 0.0F)
            {
                f2 = 0.0F;
            }

            float f4 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks;
            f1 = f1 + MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

            if (player.isSneaking())
            {
                f1 += 25.0F;
            }
            
            double rotate = 0;
    		
    		if(player.isRiding() && player.ridingEntity instanceof EntityHorse) {
    			EntityHorse horse = (EntityHorse) player.ridingEntity;
    			if(horse.isHorseSaddled()) {
    				rotate = Utils.interpolate360(horse.prevRenderYawOffset, horse.renderYawOffset, partialTicks);
    			}else {
    				rotate = Utils.getPlayerRidingWithoutControlRotation((EntityLivingBase) player.ridingEntity, partialTicks);
    			}
    		}else if(player.isRiding() && player.ridingEntity instanceof EntityPig) {
    			EntityPig pig = (EntityPig) player.ridingEntity;
    			if(pig.canRiderInteract()) {
    				rotate = Utils.interpolate360(pig.prevRenderYawOffset, pig.renderYawOffset, partialTicks);
    			}else {
    				rotate = Utils.getPlayerRidingWithoutControlRotation((EntityLivingBase) player.ridingEntity, partialTicks);
    			}
    		}else {
    			rotate = Utils.interpolate360(player.prevRenderYawOffset, player.renderYawOffset, partialTicks);
    		}
    		
    		GL11.glPushMatrix();
    		GL11.glScaled(-scale, -scale, scale);
    		GL11.glRotated(0, 1, 0, 0);
    		GL11.glRotated(180 + rotate, 0, 1, 0); // Rotate the wings to be with the player.
    		
    		if(player.isSneaking()) GlStateManager.translate(0, 0, -0.072);
    		GlStateManager.translate(0.0F, -1.42F, 0.17F);
            GlStateManager.rotate(Math.min(6.0F + f2 / 2.0F + f1, 95), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
            
            this.bipedCape.render(0.0625f);
            GlStateManager.popMatrix();
            
            GL11.glPopMatrix();
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
