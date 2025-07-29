package treemek.mesky.handlers.gui.elements.warnings;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ChatComponentText;
import treemek.mesky.handlers.RenderHandler;

public class CloseWarning extends GuiScreen{
	private GuiButton yeah;
	private GuiButton nevermind;
	public boolean showElement = false;
	
	public CloseWarning() {
		ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
		int buttonWidth = 100;
		int buttonHeight = 20;
		int yeahX = (int) (resolution.getScaledWidth() * 0.25f) - buttonWidth/2;
		yeah = new GuiButton(0, yeahX, resolution.getScaledHeight()/2, buttonWidth, buttonHeight, "Yeah");
		yeah.enabled = false;
		
		int nevermindX = (int) (resolution.getScaledWidth() * 0.75f) - buttonWidth/2;
		nevermind = new GuiButton(1, nevermindX, resolution.getScaledHeight()/2, buttonWidth, buttonHeight, "Nevermind");
		nevermind.enabled = false;
	}
	
	public void drawElement(Minecraft mc, int mouseX, int mouseY) {
		if(showElement) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 100);
			ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
			
			drawRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), 0xea000000);
			
			RenderHandler.drawText("Close without saving?", resolution.getScaledWidth()/2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("Close Without saving?"), resolution.getScaledHeight()*0.3f, 2, true, 0xFFFFFFFF);
			
			yeah.drawButton(mc, mouseX, mouseY);
			nevermind.drawButton(mc, mouseX, mouseY);
			GL11.glPopMatrix();
		}
	}
	
	public void actionPerformed(GuiButton button) {
		if(button.equals(yeah)) {
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
		if(button.equals(nevermind)) {
			changeElementActive(false);
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0)
        {
            GuiButton guibutton = nevermind;
            if (guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
            {
                guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                actionPerformed(guibutton);
            }
        }
		
		if (mouseButton == 0)
        {
            GuiButton guibutton = yeah;
            if (guibutton.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
            {
                guibutton.playPressSound(Minecraft.getMinecraft().getSoundHandler());
                actionPerformed(guibutton);
            }
        }
	}
	
	public void changeElementActive(boolean b) {
		if(b) {
			showElement = true;
			nevermind.enabled = true;
			yeah.enabled = true;
		}else {
			showElement = false;
			nevermind.enabled = false;
			yeah.enabled = false;
		}
	}
}
