package treemek.mesky.handlers.gui.elements;

import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import java.util.Queue;
import java.util.LinkedList;
import treemek.mesky.Reference;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Utils;

public class Popup {
	public class PopupElement {
		String text = "";
		long time = 0;
		
		private PopupElement(String text, long time) {
			this.text = text;
			this.time = time;
		}
	}
	
	private int scale = 1;
	private long showTime = 1000;
	private long SHOW_INTERVAL = 0;
	
	boolean shouldOverride = false;
	
	public Popup(long showTime) {
		this.showTime = showTime;
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		width = Math.min(150, Math.max(60, sr.getScaledWidth() / 4));
		height = width / 3;
		
		x = sr.getScaledWidth();
	}
	
	public Popup(long showTime, boolean shouldOverride) {
		this.showTime = showTime;
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		width = Math.min(150, Math.max(60, sr.getScaledWidth() / 4));
		height = width / 3;
		
		this.shouldOverride = shouldOverride;
		
		x = sr.getScaledWidth();
	}
	
	private boolean isShown = false;
	private boolean isHiding = false;
	Queue<PopupElement> popupText = new LinkedList<>();
	private PopupElement currentPopup = null;
	private double x = 0;
	private double width = 0;
	private double height = 0;

	private int multiplier = 1;
	
	public void drawPopup(float partialTicks) {
		if(isShown) {
			if(currentPopup == null) {
				currentPopup= popupText.poll();
				
				if(currentPopup == null) {
					isShown = false;
					isHiding = false;
					return;
				}
			}
			
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			
			double textScale = RenderHandler.getTextScale(sr.getScaledHeight()/50);
			double textWidth = RenderHandler.getTextWidth(currentPopup.text, textScale);
			double textHeight = RenderHandler.getTextHeight(textScale);
			
			double nwidth = Math.max(sr.getScaledWidth()/4, textWidth/0.8f);
			
			if(nwidth != width) { // resizing
				x = sr.getScaledWidth();
			}
			
			width = nwidth;
			
			int sidesWidth = (int) (height * 0.15);
			
			RenderHandler.drawRectWithFrame((int)x, 0, (int)(x + width), (int)height, 0xFF2a2f32, 1);
			RenderHandler.drawText(currentPopup.text, x + width/2 - textWidth/2, height/2 - textHeight/2, textScale, true, 0xFFFFFF);
			
			if(!isHiding) {
				if(x > sr.getScaledWidth() - width) {
					x -= Math.min(width/5, width / 50 * partialTicks);
					SHOW_INTERVAL = System.currentTimeMillis();
				}else {
					if(System.currentTimeMillis() - SHOW_INTERVAL >= currentPopup.time) {
						isHiding = true;
					}
				}
			}
			
			if(isHiding) {
				if(x < sr.getScaledWidth()) {
					x += width / 50 * partialTicks * multiplier;
				}else {
					isHiding = false;
					multiplier = 1;
					currentPopup = popupText.poll();
					
					if(currentPopup == null) {
						isShown = false;
					}
				}
			}
			
			x = Math.max(x, sr.getScaledWidth() - width);
		}
	}
	
	public void showPopup(String text) {
		isShown = true;
		
		if(shouldOverride) {
			if(currentPopup != null) {
				isHiding = true;
				multiplier = 3;
			}
			popupText.clear();
		}
		
		popupText.add(new PopupElement(text, showTime));
		
	}
	
	public void showPopup(String text, long time) {
		isShown = true;
		
		if(shouldOverride) {
			if(currentPopup != null) {
				isHiding = true;
				multiplier = 3;
			}
			
			popupText.clear();
		}
		
		popupText.add(new PopupElement(text, showTime));
	}
	
	public void resetPopups() {
		popupText.clear();
	}
}
