package treemek.mesky.handlers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import treemek.mesky.Mesky;
import treemek.mesky.Reference;
import treemek.mesky.features.illegal.Freelook;
import treemek.mesky.handlers.gui.elements.ButtonWithToolkit;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Utils;
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
	
	public static void drawToolkit(GuiButton btn, int mouseX, int mouseY) {
        double textScale = Math.max(0.66f, Math.min(1, getTextScale(btn.height / 2)));
        double textWidth = getTextWidth(btn.displayString, textScale);
        double textHeight = getTextHeight(textScale);
        int startY = (int) (mouseY - textHeight * 1.5f);
        
        int width = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
        

        mouseX = Math.min(mouseX, (int) (width - (textWidth + 10)));
        
        drawRect(mouseX + 2, startY, (int) (mouseX + textWidth + 10),
                 (int) (startY + textHeight * 1.5f), new Color(10, 10, 10, 200).getRGB());

        RenderHandler.drawText(btn.displayString,
            mouseX + 2 + (textWidth + 10) / 2 - textWidth / 2,
            startY + (textHeight * 1.5f) / 2 - textHeight / 2,
            textScale, btn.enabled, 0xFFFFFF);
	}
	
	public static void drawImage(int x, int y, int width, int height, ResourceLocation location) {
		Minecraft.getMinecraft().renderEngine.bindTexture(location);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
	}
	
	public static void drawLine(double startX, double startY, double startZ, double endX, double endY, double endZ, int color, boolean depth, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        if(!depth) {
        	GlStateManager.disableDepth();
        }else {
        	GlStateManager.enableDepth();
        }


        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        Color c = new Color(color);
        GlStateManager.color(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, 1.0f);

        Minecraft mc = Minecraft.getMinecraft();
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
	    double interpX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
	    double interpY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
	    double interpZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;

	    double realStartX = startX - interpX;
	    double realStartY = startY - interpY;
	    double realStartZ = startZ - interpZ;
	    double realEndX = endX - interpX;
	    double realEndY = endY - interpY;
	    double realEndZ = endZ - interpZ;

	    Tessellator tessellator = Tessellator.getInstance();
	    tessellator.getWorldRenderer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
	    tessellator.getWorldRenderer().pos(realStartX, realStartY, realStartZ).endVertex();
	    tessellator.getWorldRenderer().pos(realEndX, realEndY, realEndZ).endVertex();
	    tessellator.draw();

        //GlStateManager.enableDepth();
	    GlStateManager.color(1, 1, 1);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
	
	public static void drawLine2D(float x1, float y1, float x2, float y2, float line_width, int color) {
	    GlStateManager.pushMatrix();
	    GlStateManager.disableTexture2D();
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
	    GlStateManager.disableDepth();
	    GlStateManager.depthMask(false);
	    GL11.glLineWidth(line_width);
	    
	    Color c = new Color(color);
	    float r = c.getRed() / 255f;
	    float g = c.getGreen() / 255f;
	    float b = c.getBlue() / 255f;
	    float a = c.getAlpha() / 255f;

	    GL11.glColor4f(r, g, b, a);

	    Tessellator tess = Tessellator.getInstance();
	    WorldRenderer buffer = tess.getWorldRenderer();
	    buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
	    buffer.pos(x1, y1, 0).endVertex();
	    buffer.pos(x2, y2, 0).endVertex();
	    tess.draw();

	    GL11.glLineWidth(1.0f);
	    GL11.glColor4f(1,1,1,1);
	    GlStateManager.enableTexture2D();
	    GlStateManager.enableDepth();
	    GlStateManager.depthMask(true);
	    GlStateManager.disableBlend();
	    GlStateManager.popMatrix();
	}

	
	// https://github.com/bowser0000/SkyblockMod/blob/master/src/main/java/me/Danker/utils/RenderUtils.java
	public static void drawText(String text, double x, double y, double scale, boolean outline, int defaultColor) {
		if (text == null) return;
		
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, 1);
		GL11.glTranslated(0, 0, 0);
		
		int alpha = (defaultColor >> 24) & 0xFF;
		
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		if(outline){
		    String tempText = text.replace(EnumChatFormatting.BLACK.toString(), "§Z");
		    tempText = ColorUtils.replaceMinecraftTextColorWith(tempText, EnumChatFormatting.BLACK);
			String shadowText = tempText.replace("§Z", EnumChatFormatting.DARK_GRAY.toString());
			GlStateManager.color(1, 1, 1, 1);
			int outline_color = (alpha << 24) | 0x000000;
			
			fr.drawString(shadowText, (float) (x / scale - 1), (float) (y / scale), outline_color, false);
			fr.drawString(shadowText, (float) (x / scale + 1), (float) (y / scale), outline_color, false);
			fr.drawString(shadowText, (float) (x / scale), (float) (y / scale - 1), outline_color, false);
			fr.drawString(shadowText, (float) (x / scale), (float) (y / scale + 1), outline_color, false);
			fr.drawString(text, (float) (x / scale), (float) (y / scale), defaultColor, false);
		}else{
			GlStateManager.color(1, 1, 1, 1);
			fr.drawString(text, (float) (x / scale), (float) (y / scale), defaultColor, true);
		}
		
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.popMatrix();
	}
	
	// https://github.com/bowser0000/SkyblockMod/blob/master/src/main/java/me/Danker/utils/RenderUtils.java
	public static void drawTextWithSpliting(String text, double x, double y, double scale, boolean outline, int defaultColor) {
		if (text == null) return;
		
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, 1);
		GL11.glTranslated(0, 0, 0);
		
		String[] split = text.split("<br>");
		for(String line : split) {
			if(outline){
			    String tempText = text.replace(EnumChatFormatting.BLACK.toString(), "§Z");
			    tempText = ColorUtils.replaceMinecraftTextColorWith(tempText, EnumChatFormatting.BLACK);
				String shadowText = tempText.replace("§Z", EnumChatFormatting.DARK_GRAY.toString());
				GlStateManager.color(1, 1, 1, 1);
				fr.drawString(shadowText, (float) (x / scale - 1), (float) (y / scale), 0x000000, false);
				fr.drawString(shadowText, (float) (x / scale + 1), (float) (y / scale), 0x000000, false);
				fr.drawString(shadowText, (float) (x / scale), (float) (y / scale - 1), 0x000000, false);
				fr.drawString(shadowText, (float) (x / scale), (float) (y / scale + 1), 0x000000, false);
				fr.drawString(text, (float) (x / scale), (float) (y / scale), defaultColor, false);
			}else{
				fr.drawString(text, (float) (x / scale), (float) (y / scale), defaultColor, true);
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
        String[] splitText = text.split("<br>");
        for (String title : splitText) {
            int textLength = mc.fontRendererObj.getStringWidth(title);

            double scale = orgScale;
            if (textLength * scale > (width * 0.9F)) {
                scale = (width * 0.9F) / (float) textLength;
            }

            double titleX = ((width / 2) - (textLength * scale / 2));
            double titleY = ((height * 0.55)) + (drawHeight * scale);
            drawHUDText(title, titleX, titleY, scale, true, Color);
            drawHeight += mc.fontRendererObj.FONT_HEIGHT;
        }
    }
	
    public static void drawModalRectWithCustomSizedTexture(double x, double y, float u, float v, float width, float height, float textureWidth, float textureHeight)
    {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex((double)(u * f), (double)((v + (float)height) * f1)).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((double)((u + (float)width) * f), (double)((v + (float)height) * f1)).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((double)((u + (float)width) * f), (double)(v * f1)).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex((double)(u * f), (double)(v * f1)).endVertex();
        tessellator.draw();
    }

		public static void drawAlertText(String text, ScaledResolution resolution, int Color, double orgScale, int posX, int posY) {
	        Minecraft mc = Minecraft.getMinecraft();

	        int height = resolution.getScaledHeight();
	        int width = resolution.getScaledWidth();
	        int drawHeight = 0;
            
	        String[] splitText = text.split("<br>");
	        for (String title : splitText) {
	        	double scale = 1;
	            int textLength = mc.fontRendererObj.getStringWidth(ColorUtils.removeTextFormatting(title));

	            if (textLength * 1 > (width * 0.9F)) {
	                scale = (width * 0.9F) / (float) textLength;
	            }
	            
	            scale *= orgScale;

	            double titleX = posX - (textLength * scale / 2);
	            double titleY = posY + (drawHeight * scale);
	            drawText(title, titleX, titleY, scale, true, Color);
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
	        float playerViewX = Minecraft.getMinecraft().getRenderManager().playerViewX;
	        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) playerViewX *= -1;
        	GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        	GlStateManager.rotate(playerViewX, 1.0F, 0.0F, 0.0F);
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
        float playerViewX = Minecraft.getMinecraft().getRenderManager().playerViewX;
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) playerViewX *= -1;
    	GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
    	GlStateManager.rotate(playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f1, -f1, -f1);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        mc.fontRendererObj.drawString(timerText, -width, 0, color);
        
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
	
	public static void draw3DImage(double x, double y, double z, int textureOffsetX, int textureOffsetY, double width, double height, ResourceLocation location, int color, boolean rotateUpDown, float partialTicks) {
	    Minecraft mc = Minecraft.getMinecraft();
	    
	    Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
	    double interpX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
	    double interpY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
	    double interpZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;

	    double realX = x - interpX;
	    double realY = y - interpY;
	    double realZ = z - interpZ;


	    GlStateManager.pushMatrix();
	    GlStateManager.translate(realX, realY, realZ);
	    GL11.glNormal3f(0f, 1f, 0f);
	    
	    RenderManager renderManager = mc.getRenderManager();
	    float playerViewX = renderManager.playerViewX;
	    if (mc.gameSettings.thirdPersonView == 2) {
	        playerViewX *= -1;
	    }

	    GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
	    if(rotateUpDown) GlStateManager.rotate(playerViewX, 1.0F, 0.0F, 0.0F);
	    
	    GlStateManager.translate(width/2, height/2, 0);
	    GlStateManager.scale(-1, -1, -1);
	    GlStateManager.enableBlend();
	    GlStateManager.disableCull();
	    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	    
	    Color c = new Color(color);
	    GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
	    mc.renderEngine.bindTexture(location);
	    
	    Tessellator tessellator = Tessellator.getInstance();
	    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
	    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	    
	    float f2 = 1.0F / (float)width;
	    float f3 = 1.0F / (float)height;
	    
	    worldRenderer.pos(0.0D, height, 0.0D).tex(textureOffsetX * f2, (textureOffsetY + height) * f3).endVertex();
	    worldRenderer.pos(width, height, 0.0D).tex((textureOffsetX + width) * f2, (textureOffsetY + height) * f3).endVertex();
	    worldRenderer.pos(width, 0.0D, 0.0D).tex((textureOffsetX + width) * f2, textureOffsetY * f3).endVertex();
	    worldRenderer.pos(0.0D, 0.0D, 0.0D).tex(textureOffsetX * f2, textureOffsetY * f3).endVertex();
	    tessellator.draw();
	    
	    GlStateManager.enableCull();
	    GlStateManager.disableBlend();
	    GlStateManager.popMatrix();
	}
	
	public static void draw3DImageWithLockedRotation(double x, double y, double z, int textureOffsetX, int textureOffsetY, double width, double height, ResourceLocation location, int color, double yaw, double pitch, float partialTicks) {
	    Minecraft mc = Minecraft.getMinecraft();
	    
	    Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
	    double interpX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
	    double interpY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
	    double interpZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;

	    double realX = x - interpX;
	    double realY = y - interpY;
	    double realZ = z - interpZ;


	    GlStateManager.pushMatrix();
	    GlStateManager.translate(realX, realY, realZ);
	    GL11.glNormal3f(0f, 1f, 0f);
	    
	    RenderManager renderManager = mc.getRenderManager();

	    GlStateManager.rotate((float) -yaw, 0.0F, 1.0F, 0.0F);
	    GlStateManager.rotate((float) pitch, 1.0F, 0.0F, 0.0F);
	    GlStateManager.translate(width/2, height/2, 0);
	    GlStateManager.scale(-1, -1, -1);
	    GlStateManager.enableBlend();
	    GlStateManager.disableCull();
	    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	    
	    Color c = new Color(color);
	    GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
	    mc.renderEngine.bindTexture(location);
	    
	    Tessellator tessellator = Tessellator.getInstance();
	    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
	    worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	    
	    float f2 = 1.0F / (float)width;
	    float f3 = 1.0F / (float)height;
	    
	    worldRenderer.pos(0.0D, height, 0.0D).tex(textureOffsetX * f2, (textureOffsetY + height) * f3).endVertex();
	    worldRenderer.pos(width, height, 0.0D).tex((textureOffsetX + width) * f2, (textureOffsetY + height) * f3).endVertex();
	    worldRenderer.pos(width, 0.0D, 0.0D).tex((textureOffsetX + width) * f2, textureOffsetY * f3).endVertex();
	    worldRenderer.pos(0.0D, 0.0D, 0.0D).tex(textureOffsetX * f2, textureOffsetY * f3).endVertex();
	    tessellator.draw();
	    
	    GlStateManager.enableCull();
	    GlStateManager.disableBlend();
	    GlStateManager.popMatrix();
	}

	public static void draw3DStringWithShadow(double x, double y, double z, String timerText, int color, int shadowColor, float partialTicks, float scale) {
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
        
        float playerViewX = Minecraft.getMinecraft().getRenderManager().playerViewX;
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) playerViewX *= -1;
    	GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
    	GlStateManager.rotate(playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f1, -f1, -f1);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
		int j = mc.fontRendererObj.getStringWidth(timerText) / 2;
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        Color shadowC = new Color(shadowColor, true);
        
        GlStateManager.scale(scale, scale, scale);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(-j - 1, -1, 0.0D).color(shadowC.getRed(), shadowC.getGreen(), shadowC.getBlue(), shadowC.getAlpha()).endVertex();
        worldrenderer.pos(-j - 1, 8, 0.0D).color(shadowC.getRed(), shadowC.getGreen(), shadowC.getBlue(), shadowC.getAlpha()).endVertex();
        worldrenderer.pos(j + 1, 8, 0.0D).color(shadowC.getRed(), shadowC.getGreen(), shadowC.getBlue(), shadowC.getAlpha()).endVertex();
        worldrenderer.pos(j + 1, -1, 0.0D).color(shadowC.getRed(), shadowC.getGreen(), shadowC.getBlue(), shadowC.getAlpha()).endVertex();
        tessellator.draw();
        
        GlStateManager.enableTexture2D();
        mc.fontRendererObj.drawString(timerText, -width, 0, color);
        
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
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
        float playerViewX = Minecraft.getMinecraft().getRenderManager().playerViewX;
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) playerViewX *= -1;
    	GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
    	GlStateManager.rotate(playerViewX, 1.0F, 0.0F, 0.0F);
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
        
        if(Minecraft.getMinecraft().theWorld == null) return;
        
        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        if(!Minecraft.getMinecraft().theWorld.isBlockLoaded(new BlockPos(realX, realY, realZ))) return;
        
        
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
	public static void draw3DWaypointString(String name, String color, float[] coords, float partialTicks, float scale) {
        GlStateManager.alphaFunc(516, 0.1F);
        
        GlStateManager.pushMatrix();
        
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();

        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks + viewer.getEyeHeight();
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        
        double x = coords[0]-viewerX;
        double y = coords[1]-viewerY;
        double z = coords[2]-viewerZ;
        
        double distSq = x*x + y*y + z*z;
        double dist = Math.sqrt(distSq);
        if (dist < 50 && scale > 1) {
            scale = (float)Math.max(1, scale * (dist / 50)); // Adjust scale based on distance
        }
        
        if(dist > 32) {
    		x *= 32/dist;
    		y *= 32/dist;
        	z *= 32/dist;
        }
        
        if(dist > 12) {
        	scale *= (float)((Math.min(32, dist) / 12));
        }
        
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0, viewer.getEyeHeight(), 0);
        GlStateManager.scale(scale, scale, scale);
        
        renderNametag(name, color);

        float playerViewX = Minecraft.getMinecraft().getRenderManager().playerViewX;
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) playerViewX *= -1;
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0, -0.25f, 0);
        GlStateManager.rotate(-playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        double distance = player.getDistance(coords[0], coords[1], coords[2]);
        String distanceString = (distance >= 1)?Math.round(distance) + "m":String.format("%.1f", distance).replace(",", ".") + "m";
        
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
	        
	        float playerViewX = Minecraft.getMinecraft().getRenderManager().playerViewX;
	        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) playerViewX *= -1;
        	GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        	GlStateManager.rotate(playerViewX, 1.0F, 0.0F, 0.0F);
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
	
	public static void drawRect(double left, double top, double right, double bottom, int color)
    {
        if (left < right)
        {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
        worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void drawRectWithFrame(int left, int top, int right, int bottom, int color, int frameWidth)
    {
        Minecraft.getMinecraft().ingameGUI.drawRect(left, top, right, bottom, new Color(0, 0, 0,255).getRGB());
        Minecraft.getMinecraft().ingameGUI.drawRect(left+frameWidth, top+frameWidth, right-frameWidth, bottom-frameWidth, color);
    }
    
    public static void drawCircleWithBorder(int x, int y, int radius, int color) {
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
    
    public static String trimStringToWidth(String text, double width, boolean reverse, double scale) {
        StringBuilder stringbuilder = new StringBuilder();
        double currentWidth = 0;
        int index = reverse ? text.length() - 1 : 0;
        int increment = reverse ? -1 : 1;
        boolean inFormattingCode = false;
        boolean boldFlag = false;


        for (int i = index; i >= 0 && i < text.length() && currentWidth < width; i += increment) {
            char c = text.charAt(i);
            double charWidth = (double)Minecraft.getMinecraft().fontRendererObj.getCharWidth(c) * scale;
            
            if (inFormattingCode) {
                inFormattingCode = false;

                if (c == 'l' || c == 'L') {
                    boldFlag = true;
                } else if (c == 'r' || c == 'R') {
                    boldFlag = false;
                }
            } else if (charWidth < 0) {
                inFormattingCode = true;
            } else {
                currentWidth += boldFlag ? charWidth + scale : charWidth;

                if (currentWidth >= width) {
                    break;
                }

                if (reverse) {
                    stringbuilder.insert(0, c);
                } else {
                    stringbuilder.append(c);
                }
            }
        }

        return stringbuilder.toString();
    }
    
    public static String trimStringToWidthWithColors(String text, double width, boolean reverse, double scale) {
        StringBuilder stringbuilder = new StringBuilder();
        double currentWidth = 0;
        int index = reverse ? text.length() - 1 : 0;
        int increment = reverse ? -1 : 1;
        boolean inFormattingCode = false;


        for (int i = index; i >= 0 && i < text.length() && currentWidth < width; i += increment) {
            char c = text.charAt(i);
            double charWidth = (double)Minecraft.getMinecraft().fontRendererObj.getCharWidth(c) * scale;

            if(charWidth > 0) {
            	if(inFormattingCode) {
            		inFormattingCode = false;
            	}else {
            		currentWidth += charWidth;
            	}
            }else {
            	inFormattingCode = true;
            }
            
            if (currentWidth >= width) {
                break;
            }

            if (reverse) {
                stringbuilder.insert(0, c);
            } else {
                stringbuilder.append(c);
            }
        }

        return stringbuilder.toString();
    }
    
    public static String trimWordsToWidth(String text, int width, boolean reverse, float scale) {
        StringBuilder stringbuilder = new StringBuilder();
        float currentWidth = 0;
        int index = reverse ? text.length() - 1 : 0;
        int increment = reverse ? -1 : 1;
        boolean inFormattingCode = false;
        boolean boldFlag = false;
        int lastSpaceIndex = -1;

        for (int i = index; i >= 0 && i < text.length(); i += increment) {
            char c = text.charAt(i);
            float charWidth = (float)Minecraft.getMinecraft().fontRendererObj.getCharWidth(c) * scale;

            if (inFormattingCode) {
                inFormattingCode = false;

                if (c == 'l' || c == 'L') {
                    boldFlag = true;
                } else if (c == 'r' || c == 'R') {
                    boldFlag = false;
                }
            } else if (charWidth < 0) {
                inFormattingCode = true;
            } else {
                currentWidth += charWidth;
                if (boldFlag) {
                    currentWidth += 1;
                }
            }

            if (c == ' ') {
                lastSpaceIndex = i;
            }

            if (currentWidth >= width) {
                break;
            }

            if (reverse) {
                stringbuilder.insert(0, c);
            } else {
                stringbuilder.append(c);
            }
        }

        if (currentWidth > width && lastSpaceIndex != -1) {
            // If we exceeded the width, trim back to the last space
            if (reverse) {
                return text.substring(lastSpaceIndex + 1);
            } else {
                return stringbuilder.substring(0, stringbuilder.length() - (stringbuilder.length() - lastSpaceIndex));
            }
        }

        return stringbuilder.toString();
    }
    
    public static List<String> splitWordsToWidth(String text, int width, boolean reverse, float scale) {
        List<String> splitStrings = new ArrayList<>();

        StringBuilder stringbuilder = new StringBuilder();
        float currentWidth = 0;
        int index = reverse ? text.length() - 1 : 0;
        int increment = reverse ? -1 : 1;
        boolean inFormattingCode = false;
        boolean boldFlag = false;
        int lastSpaceIndex = -1;

        // Last active formatting so next lines inherit colors
        EnumChatFormatting activeFormat = null;
        for (int i = index; i >= 0 && i < text.length(); i += increment) {
            char c = text.charAt(i);
            float charWidth = (float) Minecraft.getMinecraft().fontRendererObj.getCharWidth(c) * scale;

            if (inFormattingCode) {
                inFormattingCode = false;
                stringbuilder.append(c);

                // track format
                if (c == 'l' || c == 'L') {
                    boldFlag = true;
                    activeFormat = EnumChatFormatting.BOLD;
                } else if (c == 'r' || c == 'R') {
                    boldFlag = false;
                    activeFormat = null;
                } else if ("0123456789AaBbCcDdEeFfKkMmNnOo".indexOf(c) != -1) {
                    activeFormat = ColorUtils.getColorFromCode(c + ""); // overwrite with last color
                }
                continue;
            }

            if(charWidth < 0) {
                inFormattingCode = true;
                stringbuilder.append(c);
                continue;
            }

            // normal char
            currentWidth += charWidth;
            if (boldFlag) currentWidth += 1;

            if (c == ' ') {
                lastSpaceIndex = stringbuilder.length();
            }

            if (currentWidth >= width) {
            	stringbuilder.append(text.substring(i));
                break;
            }

            if (reverse) {
                stringbuilder.insert(0, c);
            } else {
                stringbuilder.append(c);
            }
        }

        if (currentWidth > width && lastSpaceIndex != -1) {
            String a = stringbuilder.substring(0, lastSpaceIndex);
            String b = stringbuilder.substring(lastSpaceIndex + 1);
            
            if (reverse) {
                splitStrings.add(b);
                if(activeFormat != null) a = activeFormat + a;
                splitStrings.addAll(splitWordsToWidth(a, width, reverse, scale));
            } else {
                splitStrings.add(a);
                if(activeFormat != null) b = activeFormat + b;
                splitStrings.addAll(splitWordsToWidth(b, width, reverse, scale));
            }
        } else {
            splitStrings.add(stringbuilder.toString());
        }

        return splitStrings;
    }

    public static double getResolutionScale() {
    	Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
    	int screenHeight = screenSize.height;
    	int screenWidth = screenSize.width;
    	
    	ScaledResolution wind = new ScaledResolution(Minecraft.getMinecraft());
    	int windowHeight = wind.getScaledHeight();
    	int windowWidth = wind.getScaledWidth();
    	
    	float heightScale = ((float)windowHeight / screenHeight) * 2;
    	float widthScale = ((float)windowWidth / screenWidth) * 2;
    	return (heightScale + widthScale) / 2;
    }
    
    public static double getTextScale(double height) {
    	return height / Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
    }
    
    // Gets text scale based on width
    public static double getTextScale(String text, double width) {
    	return width / Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
    }
    
    public static double getTextWidth(String t, double scale) {
    	return Minecraft.getMinecraft().fontRendererObj.getStringWidth(t) * scale;
    }
    
    public static double getTextHeight(double scale) {
    	return Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * scale;
    }
    
    public static int getMouseX() {
    	ScaledResolution wind = new ScaledResolution(Minecraft.getMinecraft());
    	
    	int i = Mouse.getEventX() * wind.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
        
        return i;
    }
    
    public static int getMouseY() {
    	ScaledResolution wind = new ScaledResolution(Minecraft.getMinecraft());
        int j = wind.getScaledHeight() - Mouse.getEventY() * wind.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
        
        return j;
    }
}
