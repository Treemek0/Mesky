package treemek.mesky.handlers.gui.elements.buttons;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class CheckButton extends GuiButton{

	String buttonText;
	private boolean isFull;
	
	
	public CheckButton(int buttonId, int x, int y, int width, int height, String buttonText, boolean isFull) {
		super(buttonId, x, y, width, height, buttonText);
		this.buttonText = buttonText;
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
		
		if(isFull) {
			mc.renderEngine.bindTexture(check);
		}else {
			mc.renderEngine.bindTexture(empty);
		}
		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		
		float textY = yPosition + ((height / 2) - ((defaultFontHeight * scaleFactor) / 2));
		RenderHandler.drawText(buttonText, xPosition + (width*1.25), textY, scaleFactor, true, 0x3e91b5);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height) {
			isFull = !isFull;
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
	
}
