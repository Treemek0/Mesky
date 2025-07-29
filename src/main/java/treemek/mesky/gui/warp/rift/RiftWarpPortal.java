package treemek.mesky.handlers.gui.warp.rift;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.warp.rift.RiftWarpGui.RiftStack;
import treemek.mesky.listeners.GuiOpenListener.PadLock;
import treemek.mesky.utils.Utils;

public class RiftWarpPortal {
	double xPosition = 0;
	double yPosition = 0;
	int width = 0;
	int height = 0;
	
	boolean enabled = false;
	
	boolean hovered = false;
	
	PadLock lock = PadLock.LOCKED;
	
	double scaleOnHover = 1.2;
	public String name;
	int slot = 0;
	
	public RiftWarpPortal(double x, double y, int slot, String name) {
		this.xPosition = x;
		this.yPosition = y;
		this.slot = slot;
		this.name = name;
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		height = (int) (sr.getScaledHeight_double() / 960 * 35);
		width = height;
	}
	
	public RiftWarpPortal(double x, double y, int slot, String name, PadLock lock) {
		this.xPosition = x;
		this.yPosition = y;
		this.slot = slot;
		this.name = name;
		
		this.lock = lock;
		this.enabled = lock.isUnlocked();
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		height = (int) (sr.getScaledHeight_double() / 960 * 35);
		width = height;
	}
	
	ResourceLocation portal = new ResourceLocation(Reference.MODID, "gui/warp/portal.png");
	ResourceLocation portal_white = new ResourceLocation(Reference.MODID, "gui/warp/portal_white.png");
	
	public void drawPortal(int mouseX, int mouseY, float islandX, float islandY, double islandScale) {
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
	    
	    if(!enabled) {
 			GL11.glColor4f(0.3f, 0.3f, 0.3f, 0.8f);
 		}
	    
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
	        RenderHandler.drawText(name, 5, 5, textScale, true, 0xFFFFFF);
	    }

	    Minecraft.getMinecraft().renderEngine.bindTexture(portal);
	    RenderHandler.drawModalRectWithCustomSizedTexture(x, y, 0, 0, w, h, w, h);
	    
	    GL11.glColor4f(1f, 1f, 1f, 1f);
	}
	
	private boolean isHovered(int mouseX, int mouseY, float x, float y, float w, float h) {
	    return enabled && mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
	}


	public void mouseReleased(int mouseX, int mouseY, Runnable doClick) {
		if(hovered) {
			System.out.println("Clicked portal: " + name + ", hovered=" + hovered + ", slot=" + slot);

			doClick.run();
		}
		
	}
}
