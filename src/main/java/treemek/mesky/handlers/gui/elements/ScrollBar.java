package treemek.mesky.handlers.gui.elements;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Waypoints;

public class ScrollBar{
	private int maxBottomScroll; // how much you can scroll down (whole area height to scroll)
	private float ScrollOffset;
    private float targetScrollOffset;
    
	private int visibleHeight;
	private int contentHeight;
	
	public int scrollbarHeight;
	public int scrollbarWidth;
	private int headHeight;
	private int headY;
	private boolean canScroll = true;
	public int x;
	public int y;
	
	private int SCROLL_SPEED = SettingsConfig.ScrollbarSpeed.number.intValue();
	private float SMOOTH_SCROLLING_SPEED = SettingsConfig.ScrollbarSmoothness.number.floatValue();
	
	private boolean smoothScrolling = true;
	private boolean isScrolling = false; // for checking if is currently changing scrolloffset
	
	public ScrollBar(int visibleHeight, int contentHeight, int width, int height, int x, int y) {
		this.visibleHeight = visibleHeight;
		this.contentHeight = contentHeight;
		updateMaxBottomScroll();
		this.scrollbarHeight = height;
		this.scrollbarWidth = width;
		this.x = x;
		this.y = y;
		this.ScrollOffset = 0;
        this.targetScrollOffset = 0;
	}
	
	public ScrollBar() {
		// if dont want to give 0,0,0,0
		this.ScrollOffset = 0;
		this.targetScrollOffset = 0;
		this.maxBottomScroll = 0;
	}
	
	public ScrollBar(boolean smoothScrolling) {
		// if dont want to give 0,0,0,0
		this.smoothScrolling = smoothScrolling;
		this.ScrollOffset = 0;
		this.targetScrollOffset = 0;
		this.maxBottomScroll = 0;
	}
	
	public void setCanScroll(boolean b) {
		canScroll = b;
	}
	
	public boolean isScrolling() {
		return isScrolling;
	}
	
	private int eventButton;
    public void handleMouseInput() throws IOException {    
    	int scroll = Mouse.getEventDWheel();
    	ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
    	
    	int i = Mouse.getEventX() * resolution.getScaledWidth() / Minecraft.getMinecraft().displayWidth;
        int j = resolution.getScaledHeight() - Mouse.getEventY() * resolution.getScaledHeight() / Minecraft.getMinecraft().displayHeight - 1;
        int k = Mouse.getEventButton();

    	
        if (scroll != 0 && canScroll) {
        	if(targetScrollOffset < maxBottomScroll && scroll < 0) return;
            targetScrollOffset -= scroll > 0 ? -SCROLL_SPEED : SCROLL_SPEED;
            targetScrollOffset = Math.min(0, targetScrollOffset); // cant go over 0, so you cant scroll up when at first waypoint 
            // This is so you cant scroll limitless, it takes every waypoint height with their margin and removes visible inputs height so you can scroll max to how much of inputs isnt visible
            targetScrollOffset = Math.max(targetScrollOffset, maxBottomScroll); // so scrolloffset doesnt go below maxbottomscroll
        }
        
        if (Mouse.getEventButtonState())
        {
            this.eventButton = k;
            mouseClick(i, j);
        }
        else if (k != -1)
        {
        	this.eventButton = -1;
            mouseReleased();
        }
        else if (this.eventButton != -1)
        {
            mouseClickMove(i, j);
        }
    }
    
    private boolean clicked = false;
    private int offsetY = 0;
    private void mouseClick(int mouseX, int mouseY) {
    	if(mouseX >= x && mouseX <= x + scrollbarWidth && mouseY >= y && mouseY <= y + scrollbarHeight) {
    		if(mouseY >= headY && mouseY <= headY + headHeight) {
    			clicked = true;
    			offsetY = mouseY - headY;
    		}else {
    			updateOffsetToMouseClick(mouseY);
    		}
    		
    	}
    }
    
    private void mouseClickMove(int mouseX, int mouseY) {
    	if(clicked) {
    		moveHeadTo(MathHelper.clamp_int(mouseY - offsetY, y, y + scrollbarHeight));
    	}
    }
    
    private void moveHeadTo(int newHeadY) {
        // Ensure newHeadY is within valid bounds
        int scrollbarBG_endPosition = y + scrollbarHeight - headHeight;
        newHeadY = Math.max(y, Math.min(newHeadY, scrollbarBG_endPosition));

        // Calculate the scrollbar percentage based on the new headY position
        float scrollbar_percent = (float)(newHeadY - y) / (scrollbarBG_endPosition - y);

        // Update ScrollOffset based on the new scrollbar percentage
        setOffset(scrollbar_percent * maxBottomScroll);
        
        Utils.debug(newHeadY + " " + offsetY + " " + scrollbar_percent + " " + (scrollbar_percent * maxBottomScroll));
    }

    
    private void mouseReleased() {
    	clicked = false;
    	offsetY = 0;
    }
    
    public void updateVisibleHeight(int visibleHeight) {
    	this.visibleHeight = visibleHeight;
    	updateMaxBottomScroll();
    }
    
    public void updateContentHeight(int contentHeight) {
    	this.contentHeight = contentHeight;
    	updateMaxBottomScroll();
    }
    
    private void updateMaxBottomScroll() {
    	maxBottomScroll = Math.min(-(contentHeight - visibleHeight), 0);
    	targetScrollOffset = Math.max(targetScrollOffset, maxBottomScroll);
    }
    
    // Update scrollbar position to click
    private int updateOffsetToMouseClick(int mouseY){
    	float precentOfScrollbar = (float)(mouseY - y) / ((y + scrollbarHeight) - y);
		targetScrollOffset = (int) ((float)maxBottomScroll * precentOfScrollbar);
		targetScrollOffset = Math.max(targetScrollOffset, maxBottomScroll);
		return (int) ScrollOffset;
    }
    
    public void setOffset(float offset) {
    	ScrollOffset = Math.max(offset, maxBottomScroll);
    	targetScrollOffset = ScrollOffset;
    }
    
    public int getOffset() {
    	ScrollOffset = Math.max(ScrollOffset, maxBottomScroll);
    	return (int) ScrollOffset;
    }
    
    public float getOffset_float() {
    	ScrollOffset = Math.max(ScrollOffset, maxBottomScroll);
    	return ScrollOffset;
    }
    
    // Update scrollbar width, height and position
    public void updateScrollBar(int width, int height, int x, int y) {
    	this.scrollbarHeight = height;
		this.scrollbarWidth = width;
		this.x = x;
		this.y = y;
		SCROLL_SPEED = SettingsConfig.ScrollbarSpeed.number.intValue();
		SMOOTH_SCROLLING_SPEED = SettingsConfig.ScrollbarSmoothness.number.floatValue();
    }
    
    ResourceLocation scrollbar = new ResourceLocation(Reference.MODID, "gui/scrollbar.png");
    
    public void drawScrollBar() {	
    	if(maxBottomScroll != 0) { // dont render if doesnt needed
    		if((targetScrollOffset != ScrollOffset)) updateScrollOffset();
    		isScrolling = (ScrollOffset != targetScrollOffset);
    		
	        headHeight = (int) (((float)visibleHeight / contentHeight) * scrollbarHeight);

	        int scrollbarBG_endPosition = y + scrollbarHeight - headHeight;

	        float scrollbar_percent = (maxBottomScroll != 0) ? ScrollOffset / maxBottomScroll : 0; // if maxBottom scroll is 0 then it cant be divided because x/0 = NaN
	        int scrollbar_positionY = (int)(y + (scrollbar_percent * (scrollbarBG_endPosition - y)));
	        headY = (int) Math.max(y, Math.min(scrollbar_positionY, scrollbarBG_endPosition)); // scrollbar cant go past start and end positions (its because of bugs when changing resolution)
	        
        	Gui.drawRect(x, y, x + scrollbarWidth, y + scrollbarHeight, new Color(8, 7, 10, 150).getRGB());
        	
        	if(clicked) {
        		GL11.glColor3f(0.1f, 0.1f, 0.1f);
        	}
        	
        	Minecraft.getMinecraft().getTextureManager().bindTexture(scrollbar);
        	Gui.drawModalRectWithCustomSizedTexture(x, headY, 0, 0, (int) scrollbarWidth, headHeight, scrollbarWidth, headHeight);
        	GL11.glColor3f(1,1,1);
        }
    }

    // Smooth function
    private void updateScrollOffset() {
    	if(smoothScrolling) {
	    	if (Math.abs(targetScrollOffset - ScrollOffset) * SMOOTH_SCROLLING_SPEED < (SCROLL_SPEED * SMOOTH_SCROLLING_SPEED)/25) {
	            ScrollOffset = targetScrollOffset; // Snap to target
	    	}else {
	    		ScrollOffset += (targetScrollOffset - ScrollOffset) * SMOOTH_SCROLLING_SPEED;
	    	}
    	}else {
    		ScrollOffset = targetScrollOffset;
    	}
    	
    	ScrollOffset = getOffset_float();
    }

	public void resetOffset() {
		setOffset(0);
		
	}
    
}
