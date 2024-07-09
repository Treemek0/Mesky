package treemek.mesky.handlers.gui.elements;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.utils.Waypoints;

public class ScrollBar extends Gui{
	private int maxBottomScroll; // how much you can scroll down (whole area height to scroll)
	private float ScrollOffset;
    private float targetScrollOffset;
	private int wholeSize;
	public int scrollbarHeight;
	public int scrollbarWidth;
	public int x;
	public int y;
	
	private static final int SCROLL_SPEED = 15;
    private static final float SMOOTH_SCROLLING_SPEED = 0.02f;

	public ScrollBar(int maxBottomScroll, int width, int height, int x, int y) {
		this.maxBottomScroll = maxBottomScroll;
		this.scrollbarHeight = height;
		this.scrollbarWidth = width;
		this.x = x;
		this.y = y;
		this.ScrollOffset = 0;
        this.targetScrollOffset = 0;
	}
	
	public ScrollBar() {
		// if dont want to give 0,0,0,0
	}
	
	
    public void handleMouseInput(int scroll) throws IOException {      
        if (scroll != 0) {
        	if(targetScrollOffset < maxBottomScroll && scroll < 0) return;
            targetScrollOffset -= scroll > 0 ? -SCROLL_SPEED : SCROLL_SPEED;
            targetScrollOffset = Math.min(0, targetScrollOffset); // cant go over 0, so you cant scroll up when at first waypoint 
            // This is so you cant scroll limitless, it takes every waypoint height with their margin and removes visible inputs height so you can scroll max to how much of inputs isnt visible
            targetScrollOffset = Math.max(targetScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
        }
    }
    
    public void updateMaxBottomScroll(int maxBottomScroll) {
        this.maxBottomScroll = maxBottomScroll;
        targetScrollOffset = Math.max(targetScrollOffset, maxBottomScroll);
    }
    
    public int updateOffsetToMouseClick(int mouseY){
    	float precentOfScrollbar = (float)(mouseY - y) / ((y + scrollbarHeight) - y);
		targetScrollOffset = (int) ((float)maxBottomScroll * precentOfScrollbar);
		targetScrollOffset = Math.max(targetScrollOffset, maxBottomScroll);
		return (int) ScrollOffset;
    }
    
    public int getOffset() {
    	ScrollOffset = Math.max(ScrollOffset, maxBottomScroll);
    	return (int) ScrollOffset;
    }
    
    public void updateScrollBar(int width, int height, int x, int y) {
    	this.scrollbarHeight = height;
		this.scrollbarWidth = width;
		this.x = x;
		this.y = y;
    }
    
    public void renderScrollBar() {	
    	if(maxBottomScroll != 0) { // dont render if doesnt needed
    		updateScrollOffset();
    		
	        int scrollbar_height = (int) Math.max(scrollbarWidth * 2.857, Math.abs((scrollbarHeight - (scrollbarHeight / 20)) / Math.max(1, Math.abs(maxBottomScroll) / 10)));
	        
	        int scrollbarBG_endPosition = y + scrollbarHeight - scrollbar_height;

	        float scrollbar_percent = (maxBottomScroll != 0)?ScrollOffset / maxBottomScroll:0; // if maxBottom scroll is 0 then it cant be divided because x/0 = NaN
	        int scrollbar_positionY = (int)(y + (scrollbar_percent * (scrollbarBG_endPosition - y)));
	        scrollbar_positionY = (int) Math.max(y, Math.min(scrollbar_positionY, scrollbarBG_endPosition)); // scrollbar cant go past start and end positions (its because of bugs when changing resolution)
	       
        	drawRect(x, y, x + scrollbarWidth, y + scrollbarHeight, new Color(8, 7, 10, 150).getRGB());
        	
        	ResourceLocation scrollbar = new ResourceLocation(Reference.MODID, "/gui/scrollbar.png");
        	Minecraft.getMinecraft().getTextureManager().bindTexture(scrollbar);
        	drawModalRectWithCustomSizedTexture(x, scrollbar_positionY, 0, 0, (int) scrollbarWidth, scrollbar_height, scrollbarWidth, scrollbar_height);
        }
    }
    
    public void drawScrollBar() {
    	renderScrollBar();
    }
    
    private void updateScrollOffset() {
        ScrollOffset += (targetScrollOffset - ScrollOffset) * SMOOTH_SCROLLING_SPEED;
    }
    
}
