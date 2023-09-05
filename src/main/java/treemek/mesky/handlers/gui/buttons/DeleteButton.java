package treemek.mesky.handlers.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;

public class DeleteButton extends GuiButton{

	int x;
	int y;
	
	
	public DeleteButton(int buttonId, int x, int y, int width, int height, String buttonText){
		super(buttonId, x, y, width, height, buttonText);
		// TODO Auto-generated constructor stub
		this.x = x;
		this.y = y;
	}
	
	ResourceLocation delete = new ResourceLocation(Reference.MODID, "gui/delete.png");
	
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(delete);
		drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
	}
	
	
}
