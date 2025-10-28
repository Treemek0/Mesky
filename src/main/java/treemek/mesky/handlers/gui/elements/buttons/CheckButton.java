package treemek.mesky.handlers.gui.elements.buttons;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.GuiButtonRunnable;

public class CheckButton extends GuiButtonRunnable{
	private boolean isFull;
	
	
	public CheckButton(int buttonId, int x, int y, int width, int height, String toolkitText, boolean isFull) {
		super(buttonId, x, y, width, height, toolkitText);
		this.isFull = isFull;
	}
	
	
	ResourceLocation empty = new ResourceLocation(Reference.MODID, "gui/check-empty.png");
	ResourceLocation check = new ResourceLocation(Reference.MODID, "gui/check.png");
	
	public boolean isFull() {
		return isFull;
	}
	
	public void setFull(boolean a) {
		isFull = a;
	}
	
	public void update(int x, int y, int size) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = this.height = size;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	    GlStateManager.enableBlend();
	    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		
	    isHovered(mouseX, mouseY); // for hovered toolkit
	    
		if(isFull) {
			mc.renderEngine.bindTexture(check);
		}else {
			mc.renderEngine.bindTexture(empty);
		}
		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
		
		super.drawButton(mc, mouseX, mouseY);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		
		if (isHovered(mouseX, mouseY)) {
			isFull = !isFull;
			run_runnable();
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
	
	private boolean isHovered(int mouseX, int mouseY) {
		this.hovered = mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height;
		return hovered;
	}
	
}
