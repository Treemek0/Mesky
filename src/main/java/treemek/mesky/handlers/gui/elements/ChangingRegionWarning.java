package treemek.mesky.handlers.gui.elements;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.ShaderGroup;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.buttons.DeleteButton;
import treemek.mesky.handlers.gui.waypoints.WaypointElement;
import treemek.mesky.handlers.gui.waypoints.WaypointsGui;
import treemek.mesky.utils.Waypoints;

public class ChangingRegionWarning extends GuiScreen{
	private GuiButton yeahButton;
	private GuiButton nevermindButton;
	public boolean showElement = false;
	public char typedChar;
	public int keyCode;
	
	
	public ChangingRegionWarning() {
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		int buttonWidth = 100;
		int buttonHeight = 20;
		int yeahX = (int) (resolution.getScaledWidth() * 0.25f) - buttonWidth/2;
		yeahButton = new GuiButton(100, yeahX, resolution.getScaledHeight()/2, buttonWidth, buttonHeight, "Yeah");
		yeahButton.enabled = false;
		
		int nevermindX = (int) (resolution.getScaledWidth() * 0.75f) - buttonWidth/2;
		nevermindButton = new GuiButton(101, nevermindX, resolution.getScaledHeight()/2, buttonWidth, buttonHeight, "Nevermind");
		nevermindButton.enabled = false;
	}
	
	public void drawElement(Minecraft mc, int mouseX, int mouseY) {
		if(showElement) {
			ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
			
			drawRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), 0xea000000);
			
			RenderHandler.drawText("Changing the region will delete unsaved changes.", resolution.getScaledWidth()/2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("Changing the region will delete unsaved changes.")/1.5f, resolution.getScaledHeight()*0.2f, 1.5f, true, 0xFFFFFFFF);
			RenderHandler.drawText("Do you still want to change?", resolution.getScaledWidth()/2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("Do you still want to change?")/2, resolution.getScaledHeight()*0.3f, 1f, true, 0xFFFFFFFF);
			
			yeahButton.drawButton(mc, mouseX, mouseY);
			nevermindButton.drawButton(mc, mouseX, mouseY);
		}
	}
	
	public void actionPerformed(GuiButton button, WaypointsGui gui) {
		if(button.equals(yeahButton)) {
			changeElementActive(false);
			gui.waypoints.clear();
			gui.region.textboxKeyTyped(typedChar, keyCode);
			gui.initGui();
		}
		if(button.equals(nevermindButton)) {
			changeElementActive(false);
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton, WaypointsGui gui) {
		if (mouseButton == 0)
        {
            GuiButton guibutton = nevermindButton;
            if (guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
            {
                guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                actionPerformed(guibutton, gui);
            }
        }
		
		if (mouseButton == 0)
        {
            GuiButton guibutton = yeahButton;
            if (guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
            {
                guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                actionPerformed(guibutton, gui);
            }
        }
	}
	
	public void changeElementActive(boolean b) {
		if(b) {
			showElement = true;
			yeahButton.enabled = true;
			nevermindButton.enabled = true;
		}else {
			showElement = false;
			yeahButton.enabled = false;
			nevermindButton.enabled = false;
		}
	}
}
