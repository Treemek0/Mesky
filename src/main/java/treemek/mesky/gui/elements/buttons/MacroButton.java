package treemek.mesky.handlers.gui.elements.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class MacroButton extends GuiButton{

	String buttonText;
	public boolean isFull;
	
	
	public MacroButton(int buttonId, int x, int y, int width, int height, String buttonText, boolean isFull) {
		super(buttonId, x, y, width, height, buttonText);
		this.buttonText = buttonText;
		this.isFull = isFull;
	}
	
	
	ResourceLocation unclickedLeftClick = new ResourceLocation(Reference.MODID, "gui/unclicked_LeftClick.png");
	ResourceLocation clickedLeftClick = new ResourceLocation(Reference.MODID, "gui/clicked_LeftClick.png");
	
	ResourceLocation unclickedRightClick = new ResourceLocation(Reference.MODID, "gui/unclicked_RightClick.png");
	ResourceLocation clickedRightClick = new ResourceLocation(Reference.MODID, "gui/clicked_RightClick.png");
	
	ResourceLocation unclickedLeft = new ResourceLocation(Reference.MODID, "gui/unclicked_left.png");
	ResourceLocation clickedLeft = new ResourceLocation(Reference.MODID, "gui/clicked_left.png");
	
	ResourceLocation unclickedRight = new ResourceLocation(Reference.MODID, "gui/unclicked_right.png");
	ResourceLocation clickedRight = new ResourceLocation(Reference.MODID, "gui/clicked_right.png");
	
	ResourceLocation unclickedBack = new ResourceLocation(Reference.MODID, "gui/unclicked_back.png");
	ResourceLocation clickedBack = new ResourceLocation(Reference.MODID, "gui/clicked_back.png");
	
	ResourceLocation unclickedForward = new ResourceLocation(Reference.MODID, "gui/unclicked_forward.png");
	ResourceLocation clickedForward = new ResourceLocation(Reference.MODID, "gui/clicked_forward.png");
	
	ResourceLocation unclickedSneak = new ResourceLocation(Reference.MODID, "gui/unclicked_sneak.png");
	ResourceLocation clickedSneak = new ResourceLocation(Reference.MODID, "gui/sneak.png");
	
	public boolean isFull() {
		return isFull;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(isFull) {
			switch (this.id) {
			case 0:
				mc.renderEngine.bindTexture(clickedLeftClick);
				break;
			case 1:
				mc.renderEngine.bindTexture(clickedRightClick);
				break;
			case 2:
				mc.renderEngine.bindTexture(clickedLeft);
				break;
			case 3:
				mc.renderEngine.bindTexture(clickedRight);
				break;
			case 4:
				mc.renderEngine.bindTexture(clickedBack);
				break;
			case 5:
				mc.renderEngine.bindTexture(clickedForward);
				break;
			case 6:
				mc.renderEngine.bindTexture(clickedSneak);
				break;
			}
			
		}else {
			switch (this.id) {
			case 0:
				mc.renderEngine.bindTexture(unclickedLeftClick);
				break;
			case 1:
				mc.renderEngine.bindTexture(unclickedRightClick);
				break;
			case 2:
				mc.renderEngine.bindTexture(unclickedLeft);
				break;
			case 3:
				mc.renderEngine.bindTexture(unclickedRight);
				break;
			case 4:
				mc.renderEngine.bindTexture(unclickedBack);
				break;
			case 5:
				mc.renderEngine.bindTexture(unclickedForward);
				break;
			case 6:
				mc.renderEngine.bindTexture(unclickedSneak);
				break;
			}
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
