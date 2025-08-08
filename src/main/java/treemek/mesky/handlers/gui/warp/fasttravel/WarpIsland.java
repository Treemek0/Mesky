package treemek.mesky.handlers.gui.warp.fasttravel;

import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import scala.actors.threadpool.Arrays;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.listeners.GuiOpenListener.PadLock;
import treemek.mesky.utils.Utils;

public class WarpIsland {
	double xPosition = 0;
	double yPosition = 0;
	double width = 0;
	double height = 0;
	String islandToWarp;
	ResourceLocation imgLocation;
	private List<WarpPortal> portals = new ArrayList<>();
	
	float scaleOnHover = 1.2f;
	private boolean hovered = false;
	private BufferedImage buffered_img;

	String command;
	
	public PadLock enabled = PadLock.UNLOCKED;
	
	Polygon hitbox;
	
	public double scaleOnTick = 0.1f;
	float final_scale = 1;
	float scale = 1;
	
	public void setInstantHoveredState(boolean state) {
		hovered = state;
		
		if(state) {
			final_scale = 1;
			scale = 1;
		}else {
			final_scale = scaleOnHover;
			scale = scaleOnHover;
		}
	}
	
	public WarpIsland(double x, double y, double width, double height, String name, ResourceLocation imgLocation, List<WarpPortal> portals) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = width;
		this.height = height;
		this.islandToWarp = name;
		this.imgLocation = imgLocation;
		for (WarpPortal warpPortal : portals) {
			warpPortal.xPosition += x;
			warpPortal.yPosition += y;
		}
		
		this.portals = portals;
	}
	
	public WarpIsland(double x, double y, double width, double height, String name, ResourceLocation imgLocation, WarpPortal portal) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = width;
		this.height = height;
		this.islandToWarp = name;
		this.imgLocation = imgLocation;
		this.portals.clear();
		
		portal.xPosition += x;
		portal.yPosition += y;
		this.portals.add(portal);
		
		this.command = portal.command;
	}
	
	public WarpIsland(double x, double y, double width, double height, String name, ResourceLocation imgLocation, String command) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = width;
		this.height = height;
		this.islandToWarp = name;
		this.imgLocation = imgLocation;
		this.portals.clear();

		
		this.command = command;
	}
	
	public void drawIsland(int mouseX, int mouseY, boolean hovered) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
	    
	    if(!SettingsConfig.CustomWarpMenuLockableIslands.isOn) {
	    	enabled = PadLock.UNLOCKED;
	    }
	    
 		if(!enabled.isUnlocked()) {
 			GL11.glColor3f(0.1f, 0.1f, 0.1f);
 		}
	    
	    float x = (float) xPosition;
	    float y = (float) yPosition;
	    float w = (float) width;
	    float h = (float) height;
	    
	    double diffW = 0;
    	double diffH = 0;
	    
	    if(hovered && enabled.isUnlocked()) {
	    	final_scale = scaleOnHover;
	    }else {
	    	final_scale = 1;
	    }
	    
	    scale += (final_scale - scale) * scaleOnTick * SettingsConfig.CustomWarpMenuScaling.number * (240f/Minecraft.getDebugFPS());
	    
	    diffW = width * scale - width;
    	diffH = height * scale - height;
    	
    	x -= diffW /2;
    	w += diffW;
    	
    	y -= diffH /2;
    	h += diffH;
	    
	    Minecraft.getMinecraft().renderEngine.bindTexture(imgLocation);
 		RenderHandler.drawModalRectWithCustomSizedTexture(x, y, 0, 0, w, h, w, h);
 		
 		boolean isPortalHovered = false;
 		
 		for (WarpPortal warpPortal : portals) {
 			warpPortal.enabled = hovered && enabled.isUnlocked();
 			warpPortal.drawPortal(mouseX, mouseY, x + w / 2, y + h / 2, scale, isPortalHovered);
 			if(!isPortalHovered) isPortalHovered = warpPortal.hovered;
		}
 		
 		GL11.glColor3f(1, 1, 1);
 		
 		if(hovered) {
	 		if(enabled == PadLock.LOCKED) {
	 			double p = (double)Minecraft.getMinecraft().displayHeight / 960;
	 			
	    		double textScale = RenderHandler.getTextScale(p*10);
	    		double isTextWidth = RenderHandler.getTextWidth(islandToWarp, textScale);
	    		double textWidth = RenderHandler.getTextWidth("LOCKED", textScale);
	    		double textHeight = RenderHandler.getTextHeight(textScale);
	    		RenderHandler.drawText(islandToWarp, x + width/2 - isTextWidth/2, y + height/2 - textHeight/2, textScale, true, 0xFFFFFF);
		        RenderHandler.drawText("LOCKED", x + width/2 - textWidth/4, y + height/2 + textHeight/2, textScale/2, true, 0xFFFFFF);
	 		}
	 		
	 		if(enabled == PadLock.WRONG_VERSION) {
	 			double p = (double)Minecraft.getMinecraft().displayHeight / 960;
	 			
	    		double textScale = RenderHandler.getTextScale(p*10);
	    		double isTextWidth = RenderHandler.getTextWidth(islandToWarp, textScale);
	    		double textWidth = RenderHandler.getTextWidth("REQUIRED 1.21.5+", textScale);
	    		double textHeight = RenderHandler.getTextHeight(textScale);
	    		RenderHandler.drawText(islandToWarp, x + width/2 - isTextWidth/2, y + height/2 - textHeight/2, textScale, true, 0xFFFFFF);
		        RenderHandler.drawText("REQUIRED 1.21.5+", x + width/2 - textWidth/4, y + height/2 + textHeight/2, textScale/2, true, 0xFFFFFF);
	 		}
	 		
	 		if(enabled == PadLock.WRONG_SEASON) {
	 			double p = (double)Minecraft.getMinecraft().displayHeight / 960;
	 			
	    		double textScale = RenderHandler.getTextScale(p*10);
	    		double isTextWidth = RenderHandler.getTextWidth(islandToWarp, textScale);
	    		double textWidth = RenderHandler.getTextWidth("WRONG SEASON", textScale);
	    		double textHeight = RenderHandler.getTextHeight(textScale);
	    		RenderHandler.drawText(islandToWarp, x + width/2 - isTextWidth/2, y + height/2 - textHeight/2, textScale, true, 0xFFFFFF);
		        RenderHandler.drawText("WRONG SEASON", x + width/2 - textWidth/4, y + height/2 + textHeight/2, textScale/2, true, 0xFFFFFF);
	 		}
	 		
	 		if(enabled == PadLock.UNDISCOVERED) {
	 			double p = (double)Minecraft.getMinecraft().displayHeight / 960;
	 			
	    		double textScale = RenderHandler.getTextScale(p*10);
	    		double isTextWidth = RenderHandler.getTextWidth(islandToWarp, textScale);
	    		double textWidth = RenderHandler.getTextWidth("UNDISCOVERED", textScale);
	    		double textHeight = RenderHandler.getTextHeight(textScale);
	    		RenderHandler.drawText(islandToWarp, x + width/2 - isTextWidth/2, y + height/2 - textHeight/2, textScale, true, 0xFFFFFF);
		        RenderHandler.drawText("UNDISCOVERED", x + width/2 - textWidth/4, y + height/2 + textHeight/2, textScale/2, true, 0xFFFFFF);
	 		}
 		}
 		
	}

	public boolean isHovered(int mouseX, int mouseY) {
	    double scale = (hovered && enabled.isUnlocked()) ? scaleOnHover : 1.0;

	    float w = (float) (width * scale);
	    float h = (float) (height * scale);

	    float x = (float) (xPosition - (w - width) / 2.0);
	    float y = (float) (yPosition - (h - height) / 2.0);

	    if (hitbox == null) {
	        hovered = mouseX >= x && mouseY >= y && mouseX < x + w && mouseY < y + h;
	    } else {
	        Polygon scaledHitbox = new Polygon();
	        for (int i = 0; i < hitbox.npoints; i++) {
	            int scaledX = (int) (x + (hitbox.xpoints[i] * scale));
	            int scaledY = (int) (y + (hitbox.ypoints[i] * scale));
	            scaledHitbox.addPoint(scaledX, scaledY);
	        }
	        hovered = scaledHitbox.contains(mouseX, mouseY);
	    }

	    return hovered;
	}

	public boolean mouseReleased(int mouseX, int mouseY) {
		if(!enabled.isUnlocked()) return false;
		
		for (WarpPortal warpPortal : portals) {
			if(warpPortal.mouseReleased(mouseX, mouseY)) {
				return true;
			};
		}
		
		if(command != null && hovered) {
			Utils.executeCommand(command);
			return true;
		}
		
		return false;
	}
}
