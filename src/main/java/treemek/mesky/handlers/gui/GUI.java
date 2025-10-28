package treemek.mesky.handlers.gui;

import java.io.IOException;

import org.fusesource.jansi.Ansi.Color;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.alerts.AlertsGui;
import treemek.mesky.handlers.gui.chatfunctions.ChatFunctionsGui;
import treemek.mesky.handlers.gui.cosmetics.CosmeticsGui;
import treemek.mesky.handlers.gui.elements.buttons.CheckButton;
import treemek.mesky.handlers.gui.elements.buttons.MeskyButton;
import treemek.mesky.handlers.gui.keyaction.KeyActionGui;
import treemek.mesky.handlers.gui.macrowaypoints.MacroWaypointsGui;
import treemek.mesky.handlers.gui.settings.SettingsGUI;
import treemek.mesky.handlers.gui.sounds.SoundGui;
import treemek.mesky.handlers.gui.waypoints.WaypointsGui;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.Locations.Location;

public class GUI extends GuiScreen {
	
	 private ShaderGroup blurShader;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		
        double scale = RenderHandler.getTextScale(height/15);
        double textLength = RenderHandler.getTextWidth("Mesky", scale);
        int titleX = (int) ((width / 2) - textLength/2);
        int titleY = 10;

        RenderHandler.drawText("Mesky", titleX, titleY, scale, true, 0x3e91b5);
        
	    super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
	    super.initGui();
	    
        int centerX = width / 2;
        int centerY = height / 2;
        int buttonWidth = (int) (width / 5.5f);
        int buttonHeight = (int) Math.min(16, (height / 15f));
        
        if (blurShader == null) {
	        try {
	            blurShader = new ShaderGroup(Minecraft.getMinecraft().getTextureManager(), Minecraft.getMinecraft().getResourceManager(), Minecraft.getMinecraft().getFramebuffer(), new net.minecraft.util.ResourceLocation("minecraft", "shaders/post/blur.json"));
	            blurShader.createBindFramebuffers(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
	            Minecraft.getMinecraft().entityRenderer.loadShader(new net.minecraft.util.ResourceLocation("minecraft", "shaders/post/blur.json"));
	        } catch (JsonSyntaxException | IOException e) {
	            e.printStackTrace();
	        }
        }
        
        
        this.buttonList.add(new MeskyButton(0, centerX - buttonWidth / 2, getButtonY(0, buttonHeight), buttonWidth, buttonHeight, "Settings"));
        this.buttonList.add(new MeskyButton(1, centerX - buttonWidth / 2, getButtonY(1, buttonHeight), buttonWidth, buttonHeight, "Waypoints"));
        this.buttonList.add(new MeskyButton(2, centerX - buttonWidth / 2, getButtonY(2, buttonHeight), buttonWidth, buttonHeight, "Alerts"));
        this.buttonList.add(new MeskyButton(3, centerX - buttonWidth / 2, getButtonY(3, buttonHeight), buttonWidth, buttonHeight, "Chat Functions"));
        this.buttonList.add(new MeskyButton(4, centerX - buttonWidth / 2, getButtonY(4, buttonHeight), buttonWidth, buttonHeight, "Macro Waypoints"));
        this.buttonList.add(new MeskyButton(5, centerX - buttonWidth / 2, getButtonY(5, buttonHeight), buttonWidth, buttonHeight, "Key actions"));
        
        this.buttonList.add(new MeskyButton(-1, 0, height - buttonHeight, buttonWidth, buttonHeight, "Gui Locations"));
        this.buttonList.add(new MeskyButton(-3, 0, height - buttonHeight*2, buttonWidth, buttonHeight, "Sounds"));
        this.buttonList.add(new MeskyButton(-2, width - buttonWidth, height - buttonHeight, buttonWidth, buttonHeight, "Cosmetics"));
	}
	
	private int getButtonY(int i, int btn_height) {
        int startY = height / 2 - btn_height*2;
        
        int allButtonsCount = 6;
        
        if((startY + (btn_height * 1.5f * allButtonsCount) + btn_height) > height){
        	startY = (int) (height - ((btn_height * 1.5f * allButtonsCount) + btn_height));
        }
        
        return (int) (startY + (btn_height * 1.5f * i));
	}
	
	@Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
        case -1:
        	GuiHandler.GuiType = new GuiLocations();
            break;
        case -2:
        	GuiHandler.GuiType = new CosmeticsGui();
            break;
        case -3:
        	GuiHandler.GuiType = new SoundGui();
            break;
        case 0:
        	GuiHandler.GuiType = new SettingsGUI();
            break;
        case 1:
            GuiHandler.GuiType = new WaypointsGui();
            break;
        case 2:
        	GuiHandler.GuiType = new AlertsGui();
            break;
        case 3:
        	GuiHandler.GuiType = new ChatFunctionsGui();
            break;     
        case 4:
        	GuiHandler.GuiType = new MacroWaypointsGui();
            break;
        case 5:
        	GuiHandler.GuiType = new KeyActionGui();
        	break;
        }
    }
	
	@Override
	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}
	
   @Override
    public void onGuiClosed() {
        if (blurShader != null) {
            Minecraft.getMinecraft().entityRenderer.stopUseShader();
        }
        super.onGuiClosed();
    }
	
	
}
