package treemek.mesky.features;

import java.text.DecimalFormat;

import java.util.Timer;

import org.lwjgl.opengl.GL11;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.RenderHandler;

public class FishingTimer extends GuiScreen{
	
	// this shit is also used in AutoFish so if changing change there also
	public static boolean isFishing = false;
	public static boolean isInWater = false;
    public static EntityFishHook fishingHook = null;
    private float fishingTimer = 0;
    public static boolean isText3d = false;

   
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.entityLiving == Minecraft.getMinecraft().thePlayer) {
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() == Items.fishing_rod) {
                    if (!isFishing) {
                        // Telling other scripts that we started fishing
                        isFishing = true;
                       
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    	Entity entity = event.entity;
    	if(entity instanceof EntityFishHook) {
    		if(((EntityFishHook)entity).angler == Minecraft.getMinecraft().thePlayer){
    			isFishing = true;
    			fishingHook = (EntityFishHook) entity;
        		fishingTimer = 0;
    		}
    	}
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END && isFishing) {
			if(Minecraft.getMinecraft().theWorld == null) return;
			if(Minecraft.getMinecraft().theWorld.loadedEntityList == null) {
				isFishing = false;
                fishingHook = null;
				return;
			}
			
            if(fishingHook == null){
            	// Player just started fishing (detecting our bobber)
                for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
                    if (entity instanceof EntityFishHook) {
                    	if(((EntityFishHook)entity).angler == Minecraft.getMinecraft().thePlayer){
                    		fishingHook = (EntityFishHook) entity;
                    		fishingTimer = 0;
                    		break;
                    	}
                    }
                }
            }else{
            	// Player is already fishing (bobber is already detected)
            	
                if(fishingHook.isDead || !Minecraft.getMinecraft().theWorld.loadedEntityList.contains((Entity)fishingHook)) {
                    // No bobber = no fishing
                    isFishing = false;
                    fishingHook = null;
                    isInWater = false;
                }else{
                	// Yes Bobber = yes fishing (updating timer)
                	
                	if(fishingHook.caughtEntity != null && !(fishingHook.caughtEntity instanceof EntityArmorStand) && fishingHook.caughtEntity instanceof EntityLivingBase) {
                		isInWater = false;
                	}else {
                		AxisAlignedBB boundingBox = fishingHook.getEntityBoundingBox();

                		if (Minecraft.getMinecraft().theWorld.isAABBInMaterial(boundingBox, Material.water) || Minecraft.getMinecraft().theWorld.isAABBInMaterial(boundingBox, Material.lava))
                        {
                            isInWater = true;
                        }else {
                        	isInWater = false;
                        }
                	}
                	
                	fishingTimer = (fishingHook.ticksExisted/20f);
            
                }
            }
        }
    }
    
    //
    // 3d text
    //

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
    	if (fishingHook != null && isText3d && SettingsConfig.FishingTimer.isOn) {
          // Render the fishing timer on the screen
          renderFishingTimer3D(event.partialTicks);
      }
        
    }
    // Method to render the fishing timer on the screen
    private void renderFishingTimer3D(float partialTicks) {
      // Get the Minecraft instance
      Minecraft mc = Minecraft.getMinecraft();
      FontRenderer fontRenderer = mc.fontRendererObj;
      
   // Draw the timer text on the screen
      String timerText = String.format(java.util.Locale.US, "%.1f", fishingTimer);
      RenderHandler.draw3DString(fishingHook.posX, fishingHook.posY + 0.6f, fishingHook.posZ, timerText, 0xbfbfbf, partialTicks);

    }
    
    
    //
    //	2d text with bobber img
    //
    
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (fishingHook != null && !isText3d && SettingsConfig.FishingTimer.isOn) {
            // Render the fishing timer on the screen
            renderFishingTimer(event.resolution, event.partialTicks);
            
        }
    }
    
    

    // Method to render the fishing timer on the screen
    private void renderFishingTimer(ScaledResolution resolution, float partialTicks) {
        // Get the Minecraft instance
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRendererObj;

        // Calculate the position to render the timer
        float x = resolution.getScaledWidth() * (SettingsConfig.FishingTimer.position[0]/100);
        float y = resolution.getScaledHeight() * (SettingsConfig.FishingTimer.position[1]/100);
        Float scale = SettingsConfig.FishingTimer.scale;
        
        String timerText = String.format(java.util.Locale.US, "%.1f", fishingTimer) + "s";
        RenderHandler.drawText(timerText, x + (15*scale), y + (5*scale), scale, true, 0xFFFFFF);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Reference.MODID, "textures/bobber.png"));
        drawModalRectWithCustomSizedTexture((int)x, (int)(y), 0, 0, (int)(12 * scale), (int)(17 * scale), (int)(12 * scale), (int)(17 * scale));
    }
    
    
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    	isFishing = false;
		fishingHook = null;
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
    	isFishing = false;
		fishingHook = null;
    }
    
   

	
	
}


