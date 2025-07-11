package treemek.mesky.handlers.gui.elements.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Utils;

public class ArrowButton extends GuiButton{

	String buttonText;
	private boolean isOpened;
	
	
	public ArrowButton(int buttonId, int x, int y, int width, int height, String buttonText, boolean isFull) {
		super(buttonId, x, y, width, height, buttonText);
		this.buttonText = buttonText;
		this.isOpened = isFull;
	}
	
	
	ResourceLocation arrowRight = new ResourceLocation(Reference.MODID, "gui/arrow_right.png");
	ResourceLocation arrowDown = new ResourceLocation(Reference.MODID, "gui/arrow_down.png");
	
	public boolean isOpened() {
		return isOpened;
	}
	
	public void setOpened(boolean a) {
		isOpened = a;
	}
	
	public void switchOpened() {
		isOpened = !isOpened;
	}
	
	public void update(int x, int y, int size) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = this.height = size;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(isOpened()) {
			mc.renderEngine.bindTexture(arrowDown);
		}else {
			mc.renderEngine.bindTexture(arrowRight);
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
			return true;
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
	
}
