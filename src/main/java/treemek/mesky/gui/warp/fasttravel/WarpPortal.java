package treemek.mesky.handlers.gui.warp.fasttravel;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Utils;

public class WarpPortal {
	double xPosition = 0;
	double yPosition = 0;
	int width = 0;
	int height = 0;
	String command;
	
	boolean enabled = false;
	boolean visible = true;
	
	boolean hovered = false;
	
	double scaleOnHover = 1.2;
	private String name;
	
	public WarpPortal(double x, double y, String command, String name) {
		this.xPosition = x;
		this.yPosition = y;
		this.command = command;
		this.name = name;
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		height = (int) (sr.getScaledHeight_double() / 960 * 35);
		width = height;
	}
	
	public WarpPortal(double x, double y, String command, String name, boolean visible) {
		this.xPosition = x;
		this.yPosition = y;
		this.command = command;
		this.name = name;
		
		this.visible = visible;
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		height = (int) (sr.getScaledHeight_double() / 960 * 35);
		width = height;
	}
	
	ResourceLocation portal = new ResourceLocation(Reference.MODID, "gui/warp/portal.png");
	ResourceLocation portal_white = new ResourceLocation(Reference.MODID, "gui/warp/portal_white.png");
	
	public void drawPortal(int mouseX, int mouseY, float islandX, float islandY, double islandScale) {
		if(visible) {
		    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		    GlStateManager.enableBlend();
		    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
	
		    float centerX = (float) xPosition;
		    float centerY = (float) yPosition;
	
		    float scaledX = islandX + (centerX - islandX) * (float) islandScale;
		    float scaledY = islandY + (centerY - islandY) * (float) islandScale;
		    
		    float w = width * (float) islandScale;
		    float h = height * (float) islandScale;
	
		    float x = scaledX - w / 2;
		    float y = scaledY - h / 2;
	
		    hovered = isHovered(mouseX, mouseY, x, y, w, h);
		    
		    if (hovered) {
		        double diffW = w * scaleOnHover - w;
		        double diffH = h * scaleOnHover - h;
	
		        x -= diffW / 2;
		        y -= diffH / 2;
		        w += diffW;
		        h += diffH;
	
		        Minecraft.getMinecraft().renderEngine.bindTexture(portal_white);
		        RenderHandler.drawModalRectWithCustomSizedTexture(x - 1, y - 1, 0, 0, w + 2, h + 2, w + 2, h + 2);
		        
		        double textScale = RenderHandler.getTextScale(height);
		        String text = (name != null)?name:command.substring(5).trim().toUpperCase();
		        RenderHandler.drawText(text, 5, 5, textScale, true, 0xFFFFFF);
		    }
	
		    Minecraft.getMinecraft().renderEngine.bindTexture(portal);
		    RenderHandler.drawModalRectWithCustomSizedTexture(x, y, 0, 0, w, h, w, h);
		}
	}
	
	private boolean isHovered(int mouseX, int mouseY, float x, float y, float w, float h) {
	    return visible && enabled && mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
	}


	public void mouseReleased(int mouseX, int mouseY) {
		if(hovered) {
			Utils.executeCommand(command);
		}
		
	}
}
