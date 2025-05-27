package treemek.mesky.handlers.gui.elements.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class SwitchButton extends GuiButton{
	String buttonText;
	private boolean state;
	
	
	public SwitchButton(int buttonId, int x, int y, int height, String buttonText, boolean isFull) {
		super(buttonId, x, y, height*2, height, buttonText);
		this.buttonText = buttonText;
		this.state = isFull;
	}
	
	
	ResourceLocation off = new ResourceLocation(Reference.MODID, "gui/off-switch.png");
	ResourceLocation on = new ResourceLocation(Reference.MODID, "gui/on-switch.png");
	
	public boolean getState() {
		return state;
	}
	
	public void setState(boolean a) {
		state = a;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(state) {
			mc.renderEngine.bindTexture(on);
		}else {
			mc.renderEngine.bindTexture(off);
		}
		
		drawModalRectWithCustomSizedTexture(xPosition + width/4, yPosition + height/4, 0, 0, width/2, height/2, width/2, height/2);
		
		float defaultFontHeight = mc.fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (height / defaultFontHeight) / 2;
		
		float textY = yPosition + ((height / 2) - ((defaultFontHeight * scaleFactor) / 2));
		RenderHandler.drawText(buttonText, xPosition + width, textY, scaleFactor, true, 0x3e91b5);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height) {
			state = !state;
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
}
