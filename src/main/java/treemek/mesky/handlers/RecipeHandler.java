package treemek.mesky.handlers;

import java.io.File;
import java.io.Writer;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import scala.util.parsing.input.Reader;
import treemek.mesky.Mesky;
import treemek.mesky.utils.Utils;

public class RecipeHandler {

public class ItemRecipe {
    private String id;
    private Map<String, String> recipe;

    public String getId() {
        return id;
    }

    public String getItem(int slot) {
        String entry = recipe.get(String.valueOf(slot));
        return entry == null ? null : entry.split(":")[0];
    }

    public int getItemAmount(int slot) {
        String entry = recipe.get(String.valueOf(slot));
        return (entry == null || !entry.contains(":")) ? 1 : Integer.parseInt(entry.split(":")[1]);
    }

    public Map<String, String> getRecipe() {
        return recipe;
    }
}
	
	private static Map<String, ItemRecipe> recipes = new HashMap<>();
	public static void reloadRecipes() {
		Map<String, ItemRecipe> loaded_recipes = new HashMap<>();
	    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	    try {
	        // Load recipes.json containing list of filenames
	        ResourceLocation listLoc = new ResourceLocation("mesky", "recipes.json");
	        IResource listRes = Minecraft.getMinecraft().getResourceManager().getResource(listLoc);

	        List<String> fileNames;
	        try (InputStreamReader reader = new InputStreamReader(listRes.getInputStream())) {
	            fileNames = GSON.fromJson(reader, new TypeToken<List<String>>(){}.getType());
	        }

	        if (fileNames == null) return;

	        for (String fileName : fileNames) {
	            ResourceLocation jsonLoc = new ResourceLocation("mesky", "recipes/" + fileName);
	            IResource jsonRes = Minecraft.getMinecraft().getResourceManager().getResource(jsonLoc);
	            try (InputStreamReader isr = new InputStreamReader(jsonRes.getInputStream())) {
	                ItemRecipe recipe = GSON.fromJson(isr, ItemRecipe.class);
	                if (recipe != null && recipe.getId() != null) {
	                    loaded_recipes.put(recipe.getId(), recipe);
	                }
	            }
	        }
	        
	        recipes = loaded_recipes;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
	
	public static ItemRecipe getItemRecipe(String id) {
		return recipes.get(id);
	}
}
