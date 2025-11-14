package treemek.mesky.handlers.gui.cosmetics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Mesky;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.cosmetics.CosmeticHandler;
import treemek.mesky.handlers.GuiHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.alerts.AlertElement;
import treemek.mesky.handlers.gui.elements.buttons.FileSelectorButton;
import treemek.mesky.handlers.gui.elements.textFields.SettingTextField;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.utils.ImageCache;
import treemek.mesky.utils.Utils;
import treemek.mesky.utils.ImageCache.Cache;

public class CustomCapeGui  extends GuiScreen{
	
	TextField folderPathField;
	SettingTextField animationFreqField;
	
	int amounthOfAnimations = 0;
	private int inputHeight;
	
	public CustomCapeGui() {
		ImageCache.removeFolderImagesFromCache(CosmeticHandler.CustomCapeTexture.text); // remove all images from cache on opening gui
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawRect(0, 0, width, height, new Color(33, 33, 33,255).getRGB());
		
		float scale = (float) ((height*0.1f) / mc.fontRendererObj.FONT_HEIGHT) / 2;

        int textLength = mc.fontRendererObj.getStringWidth("Custom Cape");
        int titleX = (int) ((width / 2) - (textLength * scale / 2));
        int titleY = (int) (height * 0.05f);

        RenderHandler.drawText("Custom Cape", titleX, titleY, scale, true, 0x3e91b5);
		
        float scaleForTexts = (float) RenderHandler.getTextScale(inputHeight/2);
        float fontHeight = fontRendererObj.FONT_HEIGHT * scaleForTexts;

        RenderHandler.drawText("Folder path", 2, height/4 - fontHeight - 1, scaleForTexts, true, 0xFFFFFFFF);
		folderPathField.drawTextBox();
		
		if(amounthOfAnimations > 1) {
			RenderHandler.drawText("Animation frequency [Every" + EnumChatFormatting.AQUA + " X " + EnumChatFormatting.WHITE + "ms]" + EnumChatFormatting.GRAY + " (Found " + amounthOfAnimations + " animations)", 2, (int) (height/4 + fontHeight + inputHeight * 2f) - fontHeight - 1, scaleForTexts, true, 0xFFFFFFFF);
			animationFreqField.drawTextField(1, (int) (height/4 + fontHeight + inputHeight * 2f));
		}else {
			RenderHandler.drawText(EnumChatFormatting.GRAY + " (Found " + amounthOfAnimations + " animations)", 2, (int) (height/4 + fontHeight + inputHeight * 2f) - fontHeight - 1, scaleForTexts, true, 0xFFFFFFFF);
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void initGui() {
		if(CosmeticHandler.CustomCapeTexture.text.isEmpty()) {
			CosmeticHandler.CustomCapeTexture.text = Mesky.configDirectory;
		}
		
		inputHeight = ((height / 25) < 12)?12:(height / 25);
		
		folderPathField = new TextField(1, 1, height/4, width/2, inputHeight);
		folderPathField.setMaxStringLength(256);
		folderPathField.setText(CosmeticHandler.CustomCapeTexture.text);
		
		FileSelectorButton fileSelector = new FileSelectorButton(0, 1 + width/2 + 11, height/4, inputHeight, folderPathField);
		fileSelector.setOnlyDirectories(true);
		fileSelector.setFilter(new ArrayList<>(Arrays.asList(".png", ".jpg", ".jpeg")));
		
		this.buttonList.add(fileSelector);
		
		amounthOfAnimations = getAmountOfAnimationsInFolder(getCapePath());
		animationFreqField = new SettingTextField(2, "", width/8, inputHeight, CosmeticHandler.CustomCapeFrequency, 16, true, false);
		super.initGui();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(folderPathField.isFocused()) {
			if(GuiScreen.isKeyComboCtrlV(keyCode)) {
				String clipboard = GuiScreen.getClipboardString();
				
				folderPathField.writeText(clipboard.replace("\\", "/"));
			}else {
				folderPathField.textboxKeyTyped(typedChar, keyCode);
			}
			
			CosmeticHandler.CustomCapeTexture.text = folderPathField.getText();
			
			amounthOfAnimations = getAmountOfAnimationsInFolder(getCapePath());
		}
		
		if(animationFreqField.isFocused()) {
			animationFreqField.keyTyped(typedChar, keyCode);
		}
		
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (mouseX >= folderPathField.xPosition && mouseX <= folderPathField.xPosition + folderPathField.width && mouseY >= folderPathField.yPosition && mouseY <= folderPathField.yPosition + folderPathField.height) {
			folderPathField.mouseClicked(mouseX, mouseY, mouseButton);
		}else {
			folderPathField.setCursorPositionZero();
			folderPathField.setFocused(false);
		}
		
		if(animationFreqField.isHovered(mouseX, mouseY)) {
			animationFreqField.mouseClicked(mouseX, mouseY, mouseButton);
		}else {
			animationFreqField.setCursorPositionZero();
			animationFreqField.setFocused(false);
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void updateScreen() {
		folderPathField.updateCursorCounter();
		animationFreqField.updateCursorCounter();
	}
	
	public static void iterateImagesInFolder(String folderPath, String errorWhere) {
        Path folder = Paths.get(folderPath);
        Pattern pattern = Pattern.compile("cape\\d+\\.(png|jpg|jpeg)");
        int a = 0;
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.{png,jpg,jpeg}")) {
            for (Path entry : stream) {
            	if (pattern.matcher(entry.getFileName().toString()).matches()) {
            		a++;
            		downloadImageFromFilesWithoutFormatSaving(entry.toString(), errorWhere);
            	}
            }
            
            if(a == 0) {
            	Utils.writeError("Theres no cape files in: " + folderPath);
            }
            CosmeticHandler.CustomCapeTexture.number = (double) a;
        } catch (IOException e) {
            Utils.writeError(e);
        }
    }
	
	public static String removeFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1) return filePath;
        return filePath.substring(0, lastDotIndex);
    }

	private static void downloadImageFromFilesWithoutFormatSaving(final String path, final String errorWhere) {
    	new Thread(new Runnable() {
            private BufferedImage bufferedimage;
            private ResourceLocation resourceLocation;
            
			public void run() {
		    	try {
		    		File imageFile = new File(path);
		    		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(imageFile))) {
	                    bufferedimage = ImageIO.read(inputStream);
	                }
		    		
		            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
		                public void run() {
		                    try {
		                    	resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(path, new DynamicTexture(bufferedimage));
		                    	ImageCache.bufferedCache.put(removeFileExtension(path), new Cache(bufferedimage, resourceLocation));
		                    }catch (Exception e) {
		                    	if(Minecraft.getMinecraft().thePlayer != null) {
                            		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + e.getLocalizedMessage()));
                            	}
		                    }
		                }
		            });
		        } catch (Exception e) {
		        	Utils.addMinecraftMessage(EnumChatFormatting.BOLD.AQUA + "[Mesky]" + EnumChatFormatting.WHITE + " in " + EnumChatFormatting.AQUA + errorWhere + EnumChatFormatting.WHITE + ": " + EnumChatFormatting.BOLD.RED + e.getLocalizedMessage());
		        }
            }
    	}).start();
    }

	@Override
    public void onGuiClosed() {
		CosmeticHandler.CustomCapeTexture.text = getCapePath();
		iterateImagesInFolder(CosmeticHandler.CustomCapeTexture.text, "CustomCape");
		ConfigHandler.saveSettings();
		GuiHandler.GuiType = new CosmeticsGui();
	}
	
	public String getCapePath() {
		String folderPath = folderPathField.getText();

		if(folderPath.endsWith(".png") || folderPath.endsWith(".jpg") || folderPath.endsWith(".jpeg")) {
			File file = new File(folderPath);
			if(file != null && file.getParent() != null) {
				folderPath = file.getParent();
			}
		}

		if(folderPath.endsWith("\\") || folderPath.endsWith("/")) folderPath = folderPath.substring(0, folderPath.length()-1);
		
		if(Utils.systemUsesRightSlashes()) {
			folderPath = folderPath.replace("\\", "/");
		}else {
			folderPath = folderPath.replace("/", "\\");
		}
		
		return folderPath;
	}
	
	public int getAmountOfAnimationsInFolder(String folderPath) {
		Path folder = Paths.get(folderPath);
        Pattern pattern = Pattern.compile("cape\\d+\\.(png|jpg|jpeg)");
        int a = 0;
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.{png,jpg,jpeg}")) {
            for (Path entry : stream) {
            	if (pattern.matcher(entry.getFileName().toString()).matches()) {
            		a++;
            	}
            }
        } catch (IOException e) {
        }
        
        return a;
	}
}
