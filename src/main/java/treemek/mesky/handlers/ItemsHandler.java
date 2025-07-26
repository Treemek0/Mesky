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
