package treemek.mesky.handlers.gui.elements.sliders;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Utils;

public class ColorBrightnessSlider extends GuiButton{

	public double min = 0;
	public double max = 1;
	public double current = 0;
	private double sliderPrecision = 0.01f;
	
	public ColorBrightnessSlider(int buttonId, int x, int y, int width, int height, double current) {
		super(buttonId, x, y, width, height, "");
		this.current = Math.min(max, Math.max(min, current));
	}
	
	private boolean clicked;
	ResourceLocation slider = new ResourceLocation(Reference.MODID, "gui/scrollbar.png");
	ResourceLocation backgroundSlider = new ResourceLocation(Reference.MODID, "gui/brightnessSlider.png");
	
	public double getValue() {
		current = Math.min(max, Math.max(min, current));
		return 1-current; // white should be on left (0) but its 1 in algorithm and its easier
	}
	
	public void setValue(double value) {
		current = Math.min(max, Math.max(min, value));
	}

	public void drawSlider() {
		Minecraft mc = Minecraft.getMinecraft();
		GL11.glPushMatrix();
		
		mc.renderEngine.bindTexture(backgroundSlider);
		drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
		
		GL11.glColor3f(1, 0, 0);
		mc.renderEngine.bindTexture(slider);

		int sliderWidth = Math.max(1, width/25);
		float sliderPercent = (float) ((current-min)/(max-min));
		int sliderPosition = (int) (xPosition + (sliderPercent * (width - sliderWidth)));
		drawModalRectWithCustomSizedTexture(sliderPosition, yPosition, 0, 0, sliderWidth, height, sliderWidth, height);
		
		GL11.glPopMatrix();
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height) {
			clicked = true;
			float percent = (float)(mouseX - xPosition)/width;
			double rawValue = Utils.getPrecentAverage((float)min, (float)max, percent);
			double adjustedValue = Math.round(rawValue  / sliderPrecision) * sliderPrecision;
			
			setValue(adjustedValue);
		}
		
		return super.mousePressed(mc, mouseX, mouseY);
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY)
    {
		clicked = false;
    }
	
	public boolean mouseClickMoved(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if(clicked) {
			float percent = (float)(mouseX - xPosition)/width;
			double rawValue = Utils.getPrecentAverage((float)min, (float)max, percent);
			double adjustedValue = Math.round(rawValue  / sliderPrecision) * sliderPrecision;
			
			setValue(adjustedValue);
			return true;
		}else {
			return false;
		}
	}
	
}
