package treemek.mesky.handlers;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.Mesky;
import treemek.mesky.Reference;
import treemek.mesky.utils.Waypoints;
import treemek.mesky.utils.Waypoints.Waypoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class RenderHandler {

	private static CameraSetup camera;

	@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void cameraSetup(EntityViewRenderEvent.CameraSetup e) {
		camera = e;
    }
	
	// https://github.com/bowser0000/SkyblockMod/blob/master/src/main/java/me/Danker/utils/RenderUtils.java
	public static void drawText(String text, double x, double y, double scale, boolean outline, int Color) {
		if (text == null) return;
		
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, 1);

		String[] split = text.split("\n");
		for(String line : split) {
			if(outline){
				String noColorLine = StringUtils.stripControlCodes(line);
				fr.drawString(noColorLine, (int) Math.round(x / scale) - 1, (int) Math.round(y / scale), 0x000000, false);
				fr.drawString(noColorLine, (int) Math.round(x / scale) + 1, (int) Math.round(y / scale), 0x000000, false);
				fr.drawString(noColorLine, (int) Math.round(x / scale), (int) Math.round(y / scale) - 1, 0x000000, false);
				fr.drawString(noColorLine, (int) Math.round(x / scale), (int) Math.round(y / scale) + 1, 0x000000, false);
				fr.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), Color, false);
			}else{
				fr.drawString(line, (float) (x / scale), (float) (y / scale), Color, true);
			}
			y += fr.FONT_HEIGHT * scale;
		}

		GlStateManager.popMatrix();
		GlStateManager.color(1, 1, 1, 1);
	}
	
	// https://github.com/bowser0000/SkyblockMod/blob/master/src/main/java/me/Danker/utils/RenderUtils.java
	public static void drawHUDText(String text, double x, double y, double scale, boolean outline, int Color) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -1);
		drawText(text, x, y, scale, outline, Color);
		GlStateManager.popMatrix();
	}
	
	// https://github.com/bowser0000/SkyblockMod/blob/master/src/main/java/me/Danker/utils/RenderUtils.java
	public static void drawTitle(String text, ScaledResolution resolution, int Color, int orgScale) {
        Minecraft mc = Minecraft.getMinecraft();

        int height = resolution.getScaledHeight();
        int width = resolution.getScaledWidth();
        int drawHeight = 0;
        String[] splitText = text.split("\n");
        for (String title : splitText) {
            int textLength = mc.fontRendererObj.getStringWidth(title);

            double scale = orgScale;
            if (textLength * scale > (width * 0.9F)) {
                scale = (width * 0.9F) / (float) textLength;
            }

            int titleX = (int) ((width / 2) - (textLength * scale / 2));
            int titleY = (int) ((height * 0.55)) + (int) (drawHeight * scale);
            drawHUDText(title, titleX, titleY, scale, true, Color);
            drawHeight += mc.fontRendererObj.FONT_HEIGHT;
        }
    }

		public static void drawAlertText(String text, ScaledResolution resolution, int Color, double orgScale, int posX, int posY) {
	        Minecraft mc = Minecraft.getMinecraft();

	        int height = resolution.getScaledHeight();
	        int width = resolution.getScaledWidth();
	        int drawHeight = 0;
	        String[] splitText = text.split("\n");
	        for (String title : splitText) {
	            int textLength = mc.fontRendererObj.getStringWidth(title);

	            double scale = Math.max(orgScale, 1);
	            if (textLength * scale > (width * 0.9F)) {
	                scale = (width * 0.9F) / (float) textLength;
	            }

	            int titleX = (int) (posX - (textLength * scale / 2));
	            int titleY = (int) (posY + (int) (drawHeight * scale));
	            drawHUDText(title, titleX, titleY, scale, true, Color);
	            drawHeight += mc.fontRendererObj.FONT_HEIGHT;
	        }
	    }
	
	 public static void draw3DString(BlockPos pos, String text, int colour, float partialTicks) {
	        Minecraft mc = Minecraft.getMinecraft();
	        EntityPlayer player = mc.thePlayer;
	        double x = (pos.getX() - player.lastTickPosX) + ((pos.getX() - player.posX) - (pos.getX() - player.lastTickPosX)) * partialTicks;
	        double y = (pos.getY() - player.lastTickPosY) + ((pos.getY() - player.posY) - (pos.getY() - player.lastTickPosY)) * partialTicks;
	        double z = (pos.getZ() - player.lastTickPosZ) + ((pos.getZ() - player.posZ) - (pos.getZ() - player.lastTickPosZ)) * partialTicks;
	        RenderManager renderManager = mc.getRenderManager();

	        float f = 1.6F;
	        float f1 = 0.016666668F * f;
	        int width = mc.fontRendererObj.getStringWidth(text) / 2;
	        GlStateManager.pushMatrix();
	        GlStateManager.translate(x, y, z);
	        GL11.glNormal3f(0f, 1f, 0f);
	        GlStateManager.rotate(-renderManager.playerViewY, 0f, 1f, 0f);
	        GlStateManager.rotate(renderManager.playerViewX, 1f, 0f, 0f);
	        GlStateManager.scale(-f1, -f1, -f1);
	        GlStateManager.enableBlend();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        mc.fontRendererObj.drawString(text, -width, 0, colour);
	        GlStateManager.disableBlend();
	        GlStateManager.popMatrix();
	    }
	
	// https://github.com/bowser0000/SkyblockMod/blob/master/src/main/java/me/Danker/utils/RenderUtils.java
	public static void draw3DString(double x, double y, double z, String timerText, int color, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        double realX = (x - player.lastTickPosX) + ((x - player.posX) - (x - player.lastTickPosX)) * partialTicks;
        double realY = (y - player.lastTickPosY) + ((y - player.posY) - (y - player.lastTickPosY)) * partialTicks;
        double realZ = (z - player.lastTickPosZ) + ((z - player.posZ) - (z - player.lastTickPosZ)) * partialTicks;
        RenderManager renderManager = mc.getRenderManager();

        float f = 1.6F;
        float f1 = 0.016666668F * f;
        int width = mc.fontRendererObj.getStringWidth(timerText) / 2;
        GlStateManager.pushMatrix();
        GlStateManager.translate(realX, realY, realZ);
        GL11.glNormal3f(0f, 1f, 0f);
        GlStateManager.rotate(-renderManager.playerViewY, 0f, 1f, 0f);
        GlStateManager.rotate(renderManager.playerViewX, 1f, 0f, 0f);
        GlStateManager.scale(-f1, -f1, -f1);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        mc.fontRendererObj.drawString(timerText, -width, 0, color);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
	
	public static void draw3DString(String text, float[] coords, String colorString, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        
        int color = Integer.parseInt(colorString.replace("#", ""), 16);
        double x = coords[0];
        double y = coords[1] + 0.5f;
        double z = coords[2];
        
        double realX = (x - player.lastTickPosX) + ((x - player.posX) - (x - player.lastTickPosX)) * partialTicks;
        double realY = (y - player.lastTickPosY) + ((y - player.posY) - (y - player.lastTickPosY)) * partialTicks;
        double realZ = (z - player.lastTickPosZ) + ((z - player.posZ) - (z - player.lastTickPosZ)) * partialTicks;
        RenderManager renderManager = mc.getRenderManager();

        float f = 1.6F;
        float f1 = 0.016666668F * f;
        int width = mc.fontRendererObj.getStringWidth(text) / 2;
        GlStateManager.pushMatrix();
        GlStateManager.translate(realX, realY, realZ);
        GL11.glNormal3f(0f, 1f, 0f);
        GlStateManager.rotate(-renderManager.playerViewY, 0f, 1f, 0f);
        GlStateManager.rotate(renderManager.playerViewX, 1f, 0f, 0f);
        GlStateManager.scale(-f1, -f1, -f1);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        mc.fontRendererObj.drawString(text, -width, 0, color);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
	
	public static void draw3DBox(AxisAlignedBB aabb, String color, float partialTicks) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        Color colour = Color.decode("#" + color);

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(2);

        RenderGlobal.drawOutlinedBoundingBox(aabb, colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getAlpha());

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
	
	 // https://github.com/Moulberry/NotEnoughUpdates/blob/master/src/main/java/io/github/moulberry/notenoughupdates/miscfeatures/DwarvenMinesWaypoints.java#L261
	public static void draw3DWaypointString(String name, String color, float[] coord, float partialTicks, float scale) {
        GlStateManager.alphaFunc(516, 0.1F);
        
        GlStateManager.pushMatrix();
        
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        
        float[] coords = coord;
        
        double x = coords[0]-viewerX;
        double y = coords[1]-viewerY-viewer.getEyeHeight();
        double z = coords[2]-viewerZ;

        double distSq = x*x + y*y + z*z;
        double dist = Math.sqrt(distSq);
        if(distSq > 144) {
    		x *= 12/dist;
    		y *= 12/dist;
        	z *= 12/dist;
        }
        
        if (dist < 50) {
            scale = (float)Math.max(1, scale * (dist / 50)); // Adjust scale based on distance
        }
        
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0, viewer.getEyeHeight(), 0);
        GlStateManager.scale(scale, scale, scale);
        
        renderNametag(name, color);

        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0, -0.25f, 0);
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        double distance = player.getDistance(coords[0], coords[1], coords[2]);
        String distanceString;
        if(distance >= 1) {
        	distanceString = Math.round(distance) + "m"; // X
        }else {
        	distanceString = String.format("%.1f", distance).replace(",", ".") + "m"; // X.X 
        }
        renderNametag(distanceString, "#ffffff");
        
        GlStateManager.popMatrix();

        GlStateManager.disableLighting();
    }

    // https://github.com/Moulberry/NotEnoughUpdates/blob/master/src/main/java/io/github/moulberry/notenoughupdates/miscfeatures/DwarvenMinesWaypoints.java#L300
	public static void renderNametag(String str, String c) {
    	int color;
    	try {
    		color = Integer.parseInt(c.replace("#", ""), 16);
	        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
	        float f = 1.6F;
	        float f1 = 0.016666668F * f;
	        GlStateManager.pushMatrix();
	        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
	        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
	        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
	        GlStateManager.scale(-f1, -f1, f1);
	        GlStateManager.disableLighting();
	        GlStateManager.depthMask(false);
	        GlStateManager.disableDepth();
	        GlStateManager.enableBlend();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        Tessellator tessellator = Tessellator.getInstance();
	        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
	        int i = 0;

	        int j = fontrenderer.getStringWidth(str) / 2;
	        GlStateManager.disableTexture2D();
	        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
	        worldrenderer.pos(-j - 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
	        worldrenderer.pos(-j - 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
	        worldrenderer.pos(j + 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
	        worldrenderer.pos(j + 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
	        tessellator.draw();
	        GlStateManager.enableTexture2D();
	        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127);
	        GlStateManager.depthMask(true);

	        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, color);

	        GlStateManager.enableDepth();
	        GlStateManager.enableBlend();
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        GlStateManager.popMatrix();
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void drawRectWithFrame(int left, int top, int right, int bottom, int color, int frameWidth)
    {
        Minecraft.getMinecraft().ingameGUI.drawRect(left, top, right, bottom, new Color(0, 0, 0,255).getRGB());
        Minecraft.getMinecraft().ingameGUI.drawRect(left+frameWidth, top+frameWidth, right-frameWidth, bottom-frameWidth, color);
    }
    
    public static void drawCircle(int x, int y, int radius, int color) {
    	 float alpha = (color >> 24 & 255) / 255.0F;
         float red = (color >> 16 & 255) / 255.0F;
         float green = (color >> 8 & 255) / 255.0F;
         float blue = (color & 255) / 255.0F;

         GL11.glPushMatrix();
         GL11.glColor3f(red, green, blue);

         ResourceLocation circle = new ResourceLocation(Reference.MODID, "gui/circle.png");
         Minecraft.getMinecraft().renderEngine.bindTexture(circle);
         Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, radius, radius, radius, radius);
         GL11.glColor3f(1, 1, 1);
         GL11.glPopMatrix();
    }
    
	
    // Calculate opacity based on elapsed time
    public static float getlinearInterpolation(long elapsedTime, long totalTime) {
        float progress = (float) elapsedTime / totalTime;
        return progress;
    }
	
    // Calculate opacity based on elapsed time and the total time for the animation
    public static float calculateOpacity(long elapsedTime, long totalTime, long maxOpacityTime, long maxOpacityEnd) {
        if (elapsedTime < maxOpacityTime) {
            // Increase opacity gradually
            return (float) elapsedTime / maxOpacityTime;
        } 
        if(elapsedTime > maxOpacityEnd) {
            // Decrease opacity gradually towards the end of the display duration
            long remainingTime = totalTime - elapsedTime;
            return (float) remainingTime / (totalTime - maxOpacityEnd);
        }
		return 1;
    }
}
