package treemek.mesky.features;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Utils;

public class AotvOverlay {
	float changedFov = 1;
	float currentFov = 1;
	boolean shouldChangeFov = false;
	
	Integer teleportDistance;
	
	Minecraft mc = Minecraft.getMinecraft();
	
	static Vec3 lookingAtVector;
	
	@SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
		currentFov += (changedFov - currentFov) * SettingsConfig.AotvZoomSmoothness.number * event.partialTicks;
		if(Minecraft.getMinecraft().thePlayer == null || !Minecraft.getMinecraft().thePlayer.isSneaking()) {
			changedFov = 1;
			shouldChangeFov = false;
			return;
		}
		
		ItemStack itemHolding = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();
		if(itemHolding == null) {
			changedFov = 1;
			currentFov = 1;
			shouldChangeFov = false;
			teleportDistance = null;
			return;
		}
		
		String id = Utils.getSkyblockId(itemHolding);
		if(id == null) return;
		if(!id.equals("ASPECT_OF_THE_VOID")) {
			changedFov = 1;
			currentFov = 1;
			shouldChangeFov = false;
			teleportDistance = null;
			return;
		}
		
		if (teleportDistance == null) {
		    List<String> lore = Utils.getItemLore(itemHolding);
		    for (String line : lore) {
		        Matcher m = Pattern.compile("(\\d+) blocks away").matcher(ColorUtils.removeTextFormatting(line));
		        if (m.find()) {
		            teleportDistance = Integer.parseInt(m.group(1));
		            break;
		        }
		    }
		    
		    if(teleportDistance == null) { // no shift teleport
		    	changedFov = 1;
				currentFov = 1;
				shouldChangeFov = false;
		    	return;
		    }
		}
		
		MovingObjectPosition mPos = Utils.getMovingObjectPositionLookingAt(200, true);
		if(mPos == null) {
			changedFov = 1;
			return;
		}
		
		lookingAtVector = mPos.hitVec;
		
		float distance = (float) Math.sqrt(lookingAtVector.squareDistanceTo(mc.thePlayer.getPositionEyes(event.partialTicks)));
		BlockPos pos = mPos.getBlockPos();
		Block block = mc.theWorld.getBlockState(pos).getBlock();
		Block blockAbove = mc.theWorld.getBlockState(pos.up(1)).getBlock();
		Block block2Above = mc.theWorld.getBlockState(pos.up(2)).getBlock();
		
		float f = MathHelper.clamp_float(Math.min(57, distance) / SettingsConfig.AotvZoomMaxDistance.number.floatValue(), 0f, 1f);

		shouldChangeFov = true;
		changedFov = 1 - f * SettingsConfig.AotvZoomMultiplayer.number.floatValue();

	    AxisAlignedBB box = block.getSelectedBoundingBox(mc.theWorld, pos).expand(0.01, 0.01, 0.01)
		        .offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
		
	    if(Math.floor(distance) <= teleportDistance) {
		    int color = Integer.parseInt(SettingsConfig.AotvZoomColor.text, 16);
		    float r = (float)((color >> 16) & 0xFF) / 255;
		    float g = (float)((color >> 8) & 0xFF) / 255;
		    float b = (float)(color & 0xFF) / 255;
		    
			if((blockAbove != Blocks.air && blockAbove != Blocks.water && blockAbove != Blocks.flowing_water && blockAbove != Blocks.lava && blockAbove != Blocks.flowing_lava) || (block2Above != Blocks.air && block2Above != Blocks.water && block2Above != Blocks.flowing_water && block2Above != Blocks.lava && block2Above != Blocks.flowing_lava)) {
			    GlStateManager.disableTexture2D();
			    GlStateManager.enableBlend();
			    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			    GlStateManager.color(r, g, b, 1F);
			    RenderGlobal.drawSelectionBoundingBox(box);
			    GlStateManager.enableTexture2D();
			    GlStateManager.disableBlend();
				return;
			}
			
		    RenderHandler.drawFilledBoundingBox(box, r, g, b, 0.5f);
	    }
	}
	
	@SubscribeEvent
	public void onFovModifier(EntityViewRenderEvent.FOVModifier event) {
		if(!shouldChangeFov) return;

	    event.setFOV(event.getFOV() * currentFov); // zoom in
	}
	
	public static Vec3 getLookingAtVector() {
		return lookingAtVector;
	}
}
