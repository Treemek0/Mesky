package treemek.mesky.handlers.gui.elements;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.sliders.ColorBrightnessSlider;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;

public class ColorPicker extends GuiTextField{

	private int color = 0xffffff;
	private boolean isPickerOpened = false;
	GuiTextField colorTextField;
	ColorBrightnessSlider brightnessSlider;
	
	boolean colorImageClicked = false;
	
	private static final ExecutorService executorService = Executors.newFixedThreadPool(1); // no more than 1 new thread will be open at the time
	
	private int oldYposition = 0;
	
	private static boolean turnOff = false;
	
	ResourceLocation colorPicker = new ResourceLocation(Reference.MODID, "gui/colorPicker.png");
	ResourceLocation chooserCircle = new ResourceLocation(Reference.MODID, "gui/empty_circle.png");
	
	private int left;
	private int top;
	private int right;
	private int bottom;
	private static BufferedImage colorWheelImage;
	private int pickerSize;
	
	int[] colorCoords = new int[] {0,0};
	float brightness = 0;
	
	public ColorPicker(int buttonId, int x, int y, int size) {
		super(buttonId, Minecraft.getMinecraft().fontRendererObj, x, y, size, size);
		
		if(colorWheelImage == null) {
			try {
				colorWheelImage = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(colorPicker).getInputStream());
			} catch (IOException e) {
				Utils.writeError(e);
			}
		}
		
		updatePosition();
		
		brightnessSlider = new ColorBrightnessSlider(1, left + 4, top+1+pickerSize+6, pickerSize-8, 20, 0);
		
		colorTextField = new GuiTextField(1, Minecraft.getMinecraft().fontRendererObj, left+1, top+1+pickerSize+6 + 24, pickerSize - 2, 20);
        colorTextField.setMaxStringLength(12);
        colorTextField.setCanLoseFocus(true);
	}
	
	private void updatePosition() {
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		
		pickerSize = Math.max(13, resolution.getScaledWidth()/6); // 13 colors in width
		
		int heightOfRect = pickerSize + 20 + 2 + 22 + 10;
		
		left = xPosition+width+2;
		top = yPosition - heightOfRect/2;
		right = left + pickerSize + 2;
		bottom = top + heightOfRect;
		if(bottom > resolution.getScaledHeight()) {
			bottom = resolution.getScaledHeight();
			top = bottom - heightOfRect;
		}
	}
	
	private void updateElementsPositions() {
		colorTextField.yPosition = top+1+pickerSize+6 + 24;
		brightnessSlider.yPosition =  top+1+pickerSize+6;
	}
	
	@Override
	public void setTextColor(int p_146193_1_) {
		colorTextField.setTextColor(p_146193_1_);
	}
	
	@Override
	public void setText(String c) {
		if(!c.startsWith("#")) c = "#" + c;
		
		colorTextField.setText(c);
		
		try {
			this.color = Integer.parseInt(c.replace("#", ""), 16);
		} catch (Exception e) {
			this.color = 0xFFFFFF;
		}
		
		executorService.submit(() -> {
			colorCoords = ColorUtils.findClosestColor(color, colorWheelImage, true);
		
			brightness = ColorUtils.findBrightnessAdjustmentFactor(color, colorCoords[0], colorCoords[1], colorWheelImage);
			brightnessSlider.setValue(1-brightness);
		});
	}
	
	@Override
	public void drawTextBox() {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); 
		RenderHandler.drawCircle(xPosition, yPosition, width, color);
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		
		if(turnOff) {
			isPickerOpened = false;
		}
		
		if(oldYposition != yPosition) {
			isPickerOpened = false;
			
			updatePosition();
			updateElementsPositions(); // its in different function becuase colorTextField is initiazed when updatePosition() is first called (i know that i could just do null test)
		}
		
		oldYposition = yPosition;
		
		if(isPickerOpened) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 1);
			
			drawRect(xPosition + width - 2, yPosition, left, yPosition + height, 0xFF000000);
			drawRect(left, top, right, bottom, 0xFF000000);
			
			float brightness = (float) brightnessSlider.getValue();
			GL11.glColor4f(brightness, brightness, brightness, 1.0F); 
			Minecraft.getMinecraft().renderEngine.bindTexture(colorPicker); 
			drawModalRectWithCustomSizedTexture(left + 1, top + 1, 0, 0, pickerSize, pickerSize, pickerSize, pickerSize);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			int circleSize = pickerSize/15;
			int screenXc = left + 1 + (colorCoords[0] * pickerSize) / colorWheelImage.getWidth();
	        int screenYc = top + 1 + (colorCoords[1] * pickerSize) / colorWheelImage.getHeight();
			Minecraft.getMinecraft().renderEngine.bindTexture(chooserCircle);
			drawModalRectWithCustomSizedTexture(screenXc - circleSize/2, screenYc - circleSize/2, 0, 0, circleSize, circleSize, circleSize, circleSize);
			
			brightnessSlider.drawSlider();
			
			colorTextField.drawTextBox();
			
			GL11.glPopMatrix();
		}
	}
	
	@Override
	public void updateCursorCounter() {
		colorTextField.updateCursorCounter();
	}
	
	public boolean mouseClick(int mouseX, int mouseY, int buttonId) {
		boolean isPressed = mouseX >= xPosition && mouseX < xPosition + width && mouseY >= yPosition && mouseY < yPosition + height;
		
		if(!isPickerOpened) {
			if(isPressed) {
				turnOff = false;
				isPickerOpened = true;
				colorTextField.setText("#" + getColorString());
			}
			
			return isPressed;
		}else {
			if(mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom) {
				if(colorWheelImage != null && isInsideImage(mouseX, mouseY)) {
					colorImageClicked = true;
					int newColor = getColorFromImageWithMouseClick(mouseX, mouseY);
					
					if(newColor != color) { // its because coordinates could go outside colors and so do circle
						color = newColor;
					
						int relativeX = mouseX - (left + 1);
					    int relativeY = mouseY - (top + 1);
					    int imageX = (relativeX * colorWheelImage.getWidth()) / pickerSize;
					    int imageY = (relativeY * colorWheelImage.getHeight()) / pickerSize;
						colorCoords = new int[] { imageX, imageY };
						
						colorTextField.setText("#" + getColorString());
					}
				}
				
				if(brightnessSlider.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
					color = getColorFromImageWithImageCoords(colorCoords[0], colorCoords[1]);
					colorTextField.setText("#" + getColorString());
				}
				
				colorTextField.mouseClicked(mouseX, mouseY, buttonId);
				return true;
			}else {
				isPickerOpened = false;
				colorTextField.setFocused(false);
				return isPressed;
			}
		}
	}
	
	public void mouseClickMoved(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		boolean isPressed = mouseX >= xPosition && mouseX < xPosition + width && mouseY >= yPosition && mouseY < yPosition + height;
		
		if(isPickerOpened) {
			if(brightnessSlider.mouseClickMoved(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) {
				color = getColorFromImageWithImageCoords(colorCoords[0], colorCoords[1]);
				colorTextField.setText("#" + getColorString());
			}
			
			if(mouseX >= left && mouseX < right && mouseY >= top && mouseY < bottom) {
				if(colorWheelImage != null && isInsideImage(mouseX, mouseY) && colorImageClicked) {
					int newColor = getColorFromImageWithMouseClick(mouseX, mouseY);
					
					if(newColor != color) { // its because coordinates could go outside colors and so do circle
						color = newColor;
						
						int relativeX = mouseX - (left + 1);
					    int relativeY = mouseY - (top + 1);
					    int imageX = (relativeX * colorWheelImage.getWidth()) / pickerSize;
					    int imageY = (relativeY * colorWheelImage.getHeight()) / pickerSize;
						colorCoords = new int[] { imageX, imageY };
						
						colorTextField.setText("#" + getColorString());
					}
				}
			}
		}
	}
	
	public void mouseReleased(int mouseX, int mouseY) {
		brightnessSlider.mouseReleased(mouseX, mouseY);
		colorImageClicked = false;
	}
	
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		if(colorTextField.isFocused()) {
			int oldColor = color;
			int oldCursor = colorTextField.getCursorPosition();
			
			try {
				colorTextField.textboxKeyTyped(typedChar, keyCode);
				String c = colorTextField.getText().replace("#", "").substring(0, 6);
				color = Integer.parseInt(c, 16);
				
				colorCoords = ColorUtils.findClosestColor(color, colorWheelImage, true);
				brightness = ColorUtils.findBrightnessAdjustmentFactor(color, colorCoords[0], colorCoords[1], colorWheelImage);
			} catch (Exception e) {
				color = oldColor;
			}
			
			if(!colorTextField.getText().startsWith("#")) {
				
				String text = colorTextField.getText().replace("#", "");
				text = "#" + text;
				colorTextField.setText(text);
				colorTextField.setCursorPosition(Math.min(text.length(), oldCursor));
			}
		}
	}
	
	private boolean isInsideImage(int mouseX, int mouseY) {
        return mouseX >= left+1 && mouseX < left+1 + pickerSize && mouseY >= top+1 && mouseY < top+1 + pickerSize;
    }

	public int getColor() {
		return color;
	}
	
	public String getColorString() {
		return String.format("%06X", color);
	}
	
	private int getColorFromImageWithMouseClick(int mouseX, int mouseY) {
	    // Calculate relative coordinates within the image
	    int relativeX = mouseX - (left + 1);
	    int relativeY = mouseY - (top + 1);

	    // Ensure the coordinates are within the image boundaries
	    if (relativeX < 0 || relativeX >= pickerSize || relativeY < 0 || relativeY >= pickerSize) {
	        return 0; // Return a default color or handle error
	    }

	    // Calculate the coordinates within the BufferedImage
	    int imageX = (relativeX * colorWheelImage.getWidth()) / pickerSize;
	    int imageY = (relativeY * colorWheelImage.getHeight()) / pickerSize;
	    
	    int rgb = colorWheelImage.getRGB(imageX, imageY);
	    
	    // Mask out the alpha bits (ARGB to RGB)
	    int rgbWithoutAlpha = rgb & 0x00FFFFFF;
	    
	    if(rgbWithoutAlpha == 0x000000) return color;

	    return ColorUtils.adjustBrightness(rgbWithoutAlpha, (float) brightnessSlider.getValue());
	}

	private int getColorFromImageWithImageCoords(int mouseX, int mouseY) {
	    int rgb = colorWheelImage.getRGB(mouseX, mouseY);
	    
	    // Mask out the alpha bits (ARGB to RGB)
	    int rgbWithoutAlpha = rgb & 0x00FFFFFF;
	    
	    if(rgbWithoutAlpha == 0x000000) return color;

	    return ColorUtils.adjustBrightness(rgbWithoutAlpha, (float) brightnessSlider.getValue());
	}
	
	public static void turnOff() {
		turnOff = true;
	}
}
