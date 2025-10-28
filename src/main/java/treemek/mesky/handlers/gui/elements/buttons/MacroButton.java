package treemek.mesky.handlers.gui.elements.buttons;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Utils;

public class MacroButton extends GuiButton{

	String buttonText;
	public boolean isFull;
	
	
	public MacroButton(int buttonId, int x, int y, int width, int height, String buttonText, boolean isFull) {
		super(buttonId, x, y, width, height, buttonText);
		this.buttonText = buttonText;
		this.isFull = isFull;
	}
	
	
	ResourceLocation clickedLeftClick = new ResourceLocation(Reference.MODID, "gui/clicked_LeftClick.png");
	ResourceLocation clickedRightClick = new ResourceLocation(Reference.MODID, "gui/clicked_RightClick.png");
	ResourceLocation clickedLeft = new ResourceLocation(Reference.MODID, "gui/clicked_left.png");
	ResourceLocation clickedRight = new ResourceLocation(Reference.MODID, "gui/clicked_right.png");
	ResourceLocation clickedBack = new ResourceLocation(Reference.MODID, "gui/clicked_back.png");
	ResourceLocation clickedForward = new ResourceLocation(Reference.MODID, "gui/clicked_forward.png");
	ResourceLocation clickedSneak = new ResourceLocation(Reference.MODID, "gui/sneak.png");
	
	public boolean isFull() {
		return isFull;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
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
		
		if(!isFull()) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1, 1, 1, 0.3f);
		}
		
		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height) {
			isFull = !isFull;
		}
		return super.mousePressed(mc, mouseX, mouseY);
	}
	
}
