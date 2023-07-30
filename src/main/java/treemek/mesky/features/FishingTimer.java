package treemek.mesky.features;

import java.text.DecimalFormat;
import java.util.Timer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import treemek.mesky.Reference;
import treemek.mesky.utils.Rendering;

public class FishingTimer extends GuiScreen{

	private boolean isFishing = false;
    private EntityFishHook fishingHook = null;
    private float fishingTimer = 0;
    public static boolean isText3d = true;

   
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
    public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END && isFishing) {
                if(fishingHook == null){
                	// Player just started fishing (detecting our bobber)
                    for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
                        if (entity instanceof EntityFishHook && ((EntityFishHook) entity).angler == Minecraft.getMinecraft().thePlayer) {
                            fishingHook = (EntityFishHook) entity;
                            fishingTimer = 0;
                            break;
                        }
                    }
                }else{
                	// Player is already fishing (bobber is already detected)
                    if(fishingHook.isDead) {
                        // No bobber = no fishing
                        isFishing = false;
                        fishingHook = null;
                    }else{
                    	// Yes Bobber = yes fishing (updating timer)
        
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
    	if (fishingHook != null && isText3d) {
          // Render the fishing timer on the screen
          renderFishingTimer(event.partialTicks);
          
      }
        
    }
    // Method to render the fishing timer on the screen
    private void renderFishingTimer(float partialTicks) {
      // Get the Minecraft instance
      Minecraft mc = Minecraft.getMinecraft();
      FontRenderer fontRenderer = mc.fontRendererObj;
      
   // Draw the timer text on the screen
      String timerText = String.format(java.util.Locale.US, "%.1f", fishingTimer) + "s";
      Rendering.draw3DString(fishingHook.posX, fishingHook.posY + 0.4f, fishingHook.posZ, timerText, 0xbfbfbf, partialTicks);

    }
    
    
    //
    //	2d text with bobber img
    //
    
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (fishingHook != null && !isText3d) {
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
        int posX = resolution.getScaledWidth() / 10;
        int posY = resolution.getScaledHeight() / 3;
        
     // Draw the timer text on the screen
        String timerText = String.format(java.util.Locale.US, "%.1f", fishingTimer) + "s";
        int textWidth = fontRenderer.getStringWidth(timerText);
        fontRenderer.drawStringWithShadow(timerText, posX, posY, 0xFFFFFF);
        
        
        
        
        ResourceLocation textureLocation = new ResourceLocation(Reference.MODID, "textures/bobber.png");
        Minecraft.getMinecraft().renderEngine.bindTexture(textureLocation);
        drawModalRectWithCustomSizedTexture((int)(posX - textWidth), posY - 5, 0, 0, 12, 17, 12, 17);
        
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


