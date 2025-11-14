package treemek.mesky.handlers;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import treemek.mesky.handlers.ApiHandler.ApiType;

public class ItemsHandler {
	public static class Item {
	    public final String id;
	    public final String name;
	    public final String category;
	    public final String rarity;
	    public final Integer npc_sell_price;

	    public Item(String id, String name, String category, String rarity, Integer npc_sell_price) {
	        this.id = id;
	        this.name = name;
	        this.category = category;
	        this.rarity = rarity;
	        this.npc_sell_price = npc_sell_price;
	    }
	}
	
	public static int getPotionMetadata(String type) {
       if (type == null) {
           return -1;
       }

       // Standardize the input string to lowercase and replace spaces/underscores for lookup
       String key = type.toLowerCase().replace(" ", "_");

       switch (key) {
           case "water":
           case "water_bottle": return 0;
           case "awkward": return 16;
           case "thick": return 32;
           case "mundane": return 64;

           case "regeneration": return 8193;
           case "swiftness": return 8194;
           case "fire_resistance": return 8195;
           case "poison": return 8196;
           case "healing": return 8197;
           case "night_vision": return 8198;
           case "weakness": return 8200;
           case "strength": return 8201;
           case "slowness": return 8202;
           case "harming": return 8204;
           case "water_breathing": return 8205;
           case "invisibility": return 8206;
           case "leaping": return 8267;
           
           case "regeneration_splash": return 16385; 
           case "swiftness_splash": return 16386;
           case "fire_resistance_splash": return 16387;
           case "poison_splash": return 16388;
           case "healing_splash": return 16389;
           case "night_vision_splash": return 16390;
           case "weakness_splash": return 16392;
           case "strength_splash": return 16393;
           case "slowness_splash": return 16394;
           case "harming_splash": return 16396;
           case "water_breathing_splash": return 16397;
           case "invisibility_splash": return 16398;
           case "leaping_splash": return 16459;
           default: return -1;
       }
   }
	
    public static int getSpawnEggType(String type) {
    	if (type == null) {
			return -1; 
		}

        switch (type) {
            case "Creeper": return 50;
            case "Skeleton": return 51;
            case "Spider": return 52;
            case "Giant": return 53;
            case "Zombie": return 54;
            case "Slime": return 55;
            case "Ghast": return 56;
            case "PigZombie": return 57;
            case "Enderman": return 58;
            case "CaveSpider": return 59;
            case "Silverfish": return 60;
            case "Blaze": return 61;
            case "LavaSlime": return 62; // Magma Cube
            case "EnderDragon": return 63;
            case "WitherBoss": return 64;
            case "Bat": return 65;
            case "Witch": return 66;
            case "Endermite": return 67;
            case "Guardian": return 68;
            case "Pig": return 90;
            case "Sheep": return 91;
            case "Cow": return 92;
            case "Chicken": return 93;
            case "Squid": return 94;
            case "Wolf": return 95;
            case "Mooshroom": return 96;
            case "SnowMan": return 97;
            case "Ocelot": return 98;
            case "IronGolem": return 99;
            case "Horse": return 100;
            case "Rabbit": return 101;
            case "PolarBear": return 102;
            case "Llama": return 103;
            case "Parrot": return 105;
            case "Villager": return 120;
            default: return -1;
        }
    }
    
	public static final Map<String, Item> itemIds = new HashMap<>();

	public static void reloadItemIdMapping() {
	    JsonObject json = ApiHandler.fetchApi(ApiType.ITEMS);
	    if (json == null || !json.has("items")) return;

	    JsonArray items = json.getAsJsonArray("items");
	    for (JsonElement element : items) {
	        JsonObject item = element.getAsJsonObject();
	        if (!item.has("id") || !item.has("name")) continue;

	        String id = item.get("id").getAsString();
	        String name = item.get("name").getAsString();
	        String category = item.has("category") ? item.get("category").getAsString() : null;
	        String rarity = item.has("tier") ? item.get("tier").getAsString() : null;
	        Integer npc_sell_price = item.has("npc_sell_price") ? item.get("npc_sell_price").getAsInt() : null;

	        itemIds.put(id, new Item(id, name, category, rarity, npc_sell_price));
	    }
	}
	
	public static Item getItemFromId(String id) {
		return itemIds.get(id);
	}
}
