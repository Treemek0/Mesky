package treemek.mesky.handlers.gui.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Mesky;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.handlers.gui.elements.ScrollBar;
import treemek.mesky.handlers.gui.elements.buttons.FileSelectorButton;
import treemek.mesky.handlers.gui.elements.textFields.TextField;
import treemek.mesky.handlers.soundHandler.SoundsHandler;
import treemek.mesky.utils.ImageCache;
import treemek.mesky.utils.ImageUploader;
import treemek.mesky.utils.Utils;

public class ImageGalleryGui extends GuiScreen{
	FileSelectorButton importImage;
	TextField field;
	
	String oldText = "";
	
	ScrollBar scrollbar; // TODO
	
	List<ImageGalleryElement> elements = new ArrayList<>();
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		if(!field.getText().equals(oldText)) {
			ImageUploader.uploadImage((new File(field.getText())));
			oldText = "";
			field.setText("");
			initGui();
		}
		
		importImage.drawButton(mc, mouseX, mouseY);
		
		for (ImageGalleryElement imageGalleryElement : elements) {
			imageGalleryElement.draw(mouseX, mouseY);
		}
	}
	
	@Override
	public void initGui() {
		field = new TextField(0, 0, 0, 0, 0);
		field.setMaxStringLength(512);
		field.setText(oldText);
		
		scrollbar = new ScrollBar();
		
		importImage = new FileSelectorButton(-1, 0, 0, 100, 20, field, "Import image");
		importImage.setFilter(Arrays.asList(".png", ".jpg", ".jpeg"));
		
		elements.clear();
		
		int i = 0;
		for (File image : ImageUploader.getAllImportedImages()) {
			String name = image.getName().substring(0, image.getName().lastIndexOf("."));
			String format = image.getName().substring(image.getName().lastIndexOf("."));
			
			ResourceLocation texture = ImageUploader.getResourceLocation("mesky:" + name);
			elements.add(new ImageGalleryElement(texture, i, "mesky:" + name, format));
			i++;
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		importImage.mousePressed(mc, mouseX, mouseY);
		
		for (ImageGalleryElement imageGalleryElement : elements) {
			imageGalleryElement.name.mouseClicked(mouseX, mouseY, mouseButton);
			
			if(imageGalleryElement.mouseClicked(mouseX, mouseY)) {
				try {
					String address = imageGalleryElement.name.getText().substring(6);
					
					ImageCache.deleteFromImageCache(address);
					Files.delete(new File(Mesky.configDirectory + "/mesky/images/" + address + imageGalleryElement.format).toPath());
					initGui();
					return;
				
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (ImageGalleryElement imageGalleryElement : elements) {
			imageGalleryElement.textboxKeyTyped(typedChar, keyCode);
		}
		
		super.keyTyped(typedChar, keyCode);
	}
}
