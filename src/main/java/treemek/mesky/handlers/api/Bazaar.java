package treemek.mesky.handlers.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import treemek.mesky.handlers.ApiHandler;
import treemek.mesky.handlers.ItemsHandler;
import treemek.mesky.handlers.ItemsHandler.Item;
import treemek.mesky.handlers.ApiHandler.ApiType;
import treemek.mesky.handlers.RecipeHandler;
import treemek.mesky.handlers.RecipeHandler.ItemRecipe;
import treemek.mesky.utils.ColorUtils;
import treemek.mesky.utils.Utils;

public class Bazaar {
	public static enum BuyType {
		INSTA_BUY("INSTA"), ORDER_BUY("ORDER");
		
		public final String code;
		BuyType(String code) {
	        this.code = code;
	    }
	}
	
	public static enum SellType {
		INSTA_SELL("INSTA"), ORDER_SELL("ORDER");
		
		public final String code;
		SellType(String code) {
	        this.code = code;
	    }
	}
	
	
	public static Float getInstaBuyPrice(JsonObject item) {
	    if (item == null) return null;
	
	    return item.getAsJsonObject("quick_status").get("buyPrice").getAsFloat();
	}
	
	public static Float getInstaSellPrice(JsonObject item) {
	    if (item == null) return null;
	
	    return item.getAsJsonObject("quick_status").get("sellPrice").getAsFloat();
	}
	
	public static Float getSellPrice(JsonObject item, SellType type) {
		if(type == SellType.INSTA_SELL) {
			return getInstaSellPrice(item);
		}
		
		if(type == SellType.ORDER_SELL) {
			return getTopSellOrderPrice(item);
		}
		
		return 0f;
	}
	
	public static Float getSellPrice(JsonObject item, SellType type, float tax) {
		if(type == SellType.INSTA_SELL) {
			return getInstaSellPrice(item) * (1 - tax/100);
		}
		
		if(type == SellType.ORDER_SELL) {
			return getTopSellOrderPrice(item);
		}
		
		return 0f;
	}
	
	public static Float getBuyPrice(JsonObject item, BuyType type) {
		if(type == BuyType.INSTA_BUY) {
			return getInstaBuyPrice(item);
		}
		
		if(type == BuyType.ORDER_BUY) {
			return getTopBuyOrderPrice(item);
		}
		
		return 0f;
	}
	
	public static Float getBuyPrice(JsonObject item, BuyType type, float tax) {
		if(type == BuyType.INSTA_BUY) {
			return getInstaBuyPrice(item) * (1 + tax/100);
		}
		
		if(type == BuyType.ORDER_BUY) {
			return getTopBuyOrderPrice(item);
		}
		
		return 0f;
	}
	
	public static Integer getBuyVolume(JsonObject item) {
	    if (item == null) return null;
	
	    return item.getAsJsonObject("quick_status").get("buyVolume").getAsInt();
	}
	
	public static Integer getSellVolume(JsonObject item) {
	    if (item == null) return null;
	
	    return item.getAsJsonObject("quick_status").get("sellVolume").getAsInt();
	}
	
	public static Integer getBuyOrders(JsonObject item) {
	    if (item == null) return null;
	
	    return item.getAsJsonObject("quick_status").get("buyOrders").getAsInt();
	}
	
	public static Integer getSellOrders(JsonObject item) {
	    if (item == null) return null;
	
	    return item.getAsJsonObject("quick_status").get("sellOrders").getAsInt();
	}
	
	// first (best) buy order price
	public static Float getTopBuyOrderPrice(JsonObject item) {
	    if (item == null) return null;
	
	    for (JsonElement e : item.getAsJsonArray("buy_summary")) {
	        if (e.isJsonObject()) {
	            return e.getAsJsonObject().get("pricePerUnit").getAsFloat();
	        }
	    }
	    
	    return null;
	}
	
	// first (best) sell order price
	public static Float getTopSellOrderPrice(JsonObject item) {
	    if (item == null) return null;
	
	    for (JsonElement e : item.getAsJsonArray("sell_summary")) {
	        if (e.isJsonObject()) {
	            return e.getAsJsonObject().get("pricePerUnit").getAsFloat();
	        }
	    }
	    return null;
	}
	
	public static JsonObject getBazaarItem(String itemId, JsonObject bazaar) {
	    if (bazaar == null || !bazaar.has("products")) return null;

	    return bazaar.getAsJsonObject("products").getAsJsonObject(itemId);
	}
	
	public static JsonObject getBaazar() {
		return ApiHandler.fetchApi(ApiType.BAZAAR);
	}
	
	public static Map<String, JsonObject> getItemsWithStringInID(String s, JsonObject bazaar) {
	    Map<String, JsonObject> items = new HashMap<>();
	    
	    if (bazaar == null || !bazaar.has("products")) return items;

	    JsonObject products = bazaar.getAsJsonObject("products");

	    for (Map.Entry<String, JsonElement> entry : products.entrySet()) {
	        String itemId = entry.getKey();
	        if (itemId.contains(s) && entry.getValue().isJsonObject()) {
	            items.put(itemId, entry.getValue().getAsJsonObject());
	        }
	    }

	    return items;
	}

	private static Map<String, Float> craftCostMap = new HashMap<>();
	private static List<String> alreadyTriedCraftingRecipe = new ArrayList<>();
	
	public static void clearCraftCostMap() {
		craftCostMap.clear();
	}
	
	public static void getHighestProfitCrafts(float tax, BuyType buy, SellType sell, long minProfit) {
		craftCostMap.clear();
		JsonObject bazaar = getBaazar();
		if (bazaar == null || !bazaar.has("products")) return;

		JsonObject products = bazaar.getAsJsonObject("products");
		
		int profitItemsCount = 0;
		try {
		    for (Map.Entry<String, JsonElement> entry : products.entrySet()) {
		    	if(Thread.interrupted()) return;
		        String itemId = entry.getKey();
		        float[] craft = getCraftProfit(itemId, bazaar, tax, buy, sell, false);
		        
		        if(craft[0] > minProfit) {
		        	String message =  EnumChatFormatting.WHITE + " | " + EnumChatFormatting.YELLOW + "Sell price: " + Utils.formatNumber(craft[1])
		        	+ EnumChatFormatting.WHITE + " | " + EnumChatFormatting.LIGHT_PURPLE + "Profit: " + EnumChatFormatting.RED + EnumChatFormatting.BOLD + Utils.formatNumber(craft[0]) + EnumChatFormatting.RESET
		        	+ EnumChatFormatting.WHITE + " | " + EnumChatFormatting.GRAY + "Buy Volume: " + (int)craft[3] + EnumChatFormatting.WHITE + " | " + EnumChatFormatting.GRAY + "Sell Volume: " + (int)craft[4];
		
		        	ChatComponentText profitMessage = new ChatComponentText("");
		    
		        	ChatComponentText craftCostText = new ChatComponentText(EnumChatFormatting.WHITE + " | " + EnumChatFormatting.GOLD + "Craft cost: " + Utils.formatNumber(craft[2]));
		        	ChatStyle craftClick = new ChatStyle();
			        craftClick.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mesky itemcraftprofit " + itemId + " " + tax + " " + buy.code + " " + sell.code));
			        craftClick.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Show process")));
			        craftCostText.setChatStyle(craftClick);
		        	
		        	
		        	ChatComponentText chatText = new ChatComponentText(message);
			        ChatStyle bazaarClick = new ChatStyle();
			        bazaarClick.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bz " + itemId));
			        bazaarClick.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("/bz " + itemId)));
			        chatText.setChatStyle(bazaarClick);
			
			        ChatComponentText id = new ChatComponentText(EnumChatFormatting.AQUA + itemId);
			        id.setChatStyle(bazaarClick);
			        
			        ChatStyle recipe = new ChatStyle();
			        recipe.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/recipe " + itemId));
			        recipe.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Show recipe")));
			        recipe.setColor(EnumChatFormatting.DARK_RED); // Set the color of the button text
			        
			        ChatComponentText clickableRecipe = new ChatComponentText(" [RECIPE]");
			        clickableRecipe.setChatStyle(recipe); 
			        
			        profitMessage.appendSibling(id);
			        profitMessage.appendSibling(craftCostText);
			        profitMessage.appendSibling(chatText);
			        profitMessage.appendSibling(clickableRecipe);
			       
			       Utils.addMinecraftMessage(profitMessage);
			       profitItemsCount++;
		        }
		    }
		    
		    Utils.addMinecraftMessageWithPrefix("Finished searching bazaar. Found " + profitItemsCount + " profit crafts");
		} catch (Exception e) {
			Utils.writeError(e);
		}
	}

	public static float[] getCraftProfit(String itemId, JsonObject bazaar, float tax, BuyType buy, SellType sell, boolean detailedInfo) {
		ItemRecipe recipe = RecipeHandler.getItemRecipe(itemId);
        if (recipe == null) return new float[] {0,0,0,0,0};
        
        alreadyTriedCraftingRecipe.clear();
        CraftResult craft = calculateCraftCost(recipe, bazaar, buy, tax, " ");
        List<String> craftProcess = craft.craftings;
        float craftCost = craft.cost;
        craftCostMap.put(itemId, craftCost);
        
        
        JsonObject bazaarItem = getBazaarItem(itemId, bazaar);
        float sellPrice = 0;
        int buyVolume = 0;
        int sellVolume = 0;
        
        if(detailedInfo && craftProcess != null) {
        	for (String string : craftProcess) {
				Utils.addMinecraftMessage(string);
			}
        }
        
        if(bazaarItem != null) {
        	Float s = getSellPrice(bazaarItem, sell, tax);
        	if(s != null) {
        		sellPrice = s;
        	}else {
        		Utils.debug(itemId + " didnt have sell orders");
        	}
        	
        	Integer bV = getBuyVolume(bazaarItem);
        	if(bV != null) buyVolume = bV;
        	
        	Integer sV = getSellVolume(bazaarItem);
        	if(sV != null) sellVolume = sV;
        }
        
        return new float[] {sellPrice - craftCost, sellPrice, craftCost, buyVolume, sellVolume};
	}
	
	private static class CraftResult {
		float cost;
		List<String> craftings; // will be used for better logging
		
		private CraftResult(float cost, List<String> craftings) {
			this.cost = cost;
			this.craftings = craftings;
		}
	}
	
	private static CraftResult calculateCraftCost(ItemRecipe recipe, JsonObject bazaar, BuyType buy, float tax, String detailedInfo) {
		float total_cost = 0;
		List<String> process = new ArrayList<>();
		
		for (int i = 1; i <= 9; i++) {
			if(Thread.interrupted()) return new CraftResult(Float.MAX_VALUE, null);
			
			String id = recipe.getItem(i);
			if(id == null || id.equals("")) continue;
			int amount = recipe.getItemAmount(i);
			
			JsonObject bazaarItem = getBazaarItem(id, bazaar);
			if(bazaarItem == null) { // item isnt on bazaar
				Item item = ItemsHandler.getItemFromId(id);
				if(item == null) { // item isnt in data for id -> name
					ItemRecipe recipe2 = RecipeHandler.getItemRecipe(id);
			        if (recipe2 == null || alreadyTriedCraftingRecipe.contains(id)) {
			        	if(alreadyTriedCraftingRecipe.contains(id) && craftCostMap.containsKey(id)){
			        		total_cost += craftCostMap.get(id) * amount;
			        		process.add(detailedInfo + EnumChatFormatting.BLUE + "Using recipe for " + EnumChatFormatting.GOLD + id + EnumChatFormatting.DARK_GREEN + "x" + amount + EnumChatFormatting.BLUE + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(craftCostMap.get(id)*amount) + EnumChatFormatting.BLUE + ")");
			        	}else {
			        		process.add(detailedInfo + EnumChatFormatting.RED + "Can't find " + EnumChatFormatting.GOLD + id + EnumChatFormatting.BLUE + " in ID -> NAME mapping, so no auctions");
			        		return new CraftResult(Float.MAX_VALUE, process);
			        	}
			        }else {
			        	alreadyTriedCraftingRecipe.add(id);
			        	CraftResult craft = calculateCraftCost(recipe2, bazaar, buy, tax, ">" + detailedInfo);
			        	
			        	float cost = craft.cost * amount;
			        	craftCostMap.put(id, cost);
			        	if (cost == Float.MAX_VALUE) {
			        		process.addAll(craft.craftings);
			        		return new CraftResult(Float.MAX_VALUE, process);
			        	}
			        	total_cost += cost;
			        	
			        	process.add(detailedInfo + EnumChatFormatting.BLUE + "Calculated craft cost of recipe for " + EnumChatFormatting.GOLD + id + EnumChatFormatting.DARK_GREEN + "x" + amount + EnumChatFormatting.BLUE + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(cost) + EnumChatFormatting.BLUE + "):");
			        	process.addAll(craft.craftings);
			        	continue;
			        }
				}
				
				Float cost = Auction.getLowestBin(item.name);
				if(cost == null) {
					ItemRecipe recipe2 = RecipeHandler.getItemRecipe(id);
			        if (recipe2 == null || alreadyTriedCraftingRecipe.contains(id)) {
			        	if(alreadyTriedCraftingRecipe.contains(id) && craftCostMap.containsKey(id)){
			        		total_cost += craftCostMap.get(id) * amount;
			        		process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.BLUE + "Using recipe for " + EnumChatFormatting.GOLD + id + EnumChatFormatting.DARK_GREEN + "x" + amount + EnumChatFormatting.BLUE + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(craftCostMap.get(id)*amount) + EnumChatFormatting.BLUE + ")");
			        	}else {
			        		process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.RED + "Can't find " + EnumChatFormatting.GOLD + id + EnumChatFormatting.BLUE + " in auction house and it doesn't have recipe");
			        		return new CraftResult(Float.MAX_VALUE, process);
			        	}
			        }else {
			        	alreadyTriedCraftingRecipe.add(id);
			        	CraftResult craft = calculateCraftCost(recipe2, bazaar, buy, tax, ">" + detailedInfo);
			        	
			        	cost = craft.cost * amount;
			        	craftCostMap.put(id, cost);
			        	if (cost == Float.MAX_VALUE) {
			        		process.addAll(craft.craftings);
			        		return new CraftResult(Float.MAX_VALUE, process);
			        	}
			        	total_cost += cost;
			        	
			        	process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.BLUE + "Calculated craft cost of recipe for " + EnumChatFormatting.GOLD + id + EnumChatFormatting.DARK_GREEN + "x" + amount + EnumChatFormatting.BLUE + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(cost) + EnumChatFormatting.BLUE + "):");
			        	process.addAll(craft.craftings);
			        }
				}else {
					total_cost += cost;
					
					process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.BLUE + "Found lowest bin for " + EnumChatFormatting.GOLD + id + EnumChatFormatting.LIGHT_PURPLE + "x" + amount + EnumChatFormatting.BLUE + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(cost) + EnumChatFormatting.BLUE + ")");
				}
			}else { // item is in bazaar
				Float bazaarCost = getBuyPrice(bazaarItem, buy, tax);
				if(bazaarCost == null) { // cant buy (no sell/buy orders
					ItemRecipe recipe2 = RecipeHandler.getItemRecipe(id);
			        if (recipe2 == null || alreadyTriedCraftingRecipe.contains(id)) {
			        	if(alreadyTriedCraftingRecipe.contains(id) && craftCostMap.containsKey(id)){
			        		total_cost += craftCostMap.get(id) * amount;
			        		process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.BLUE + "Using recipe for " + EnumChatFormatting.GOLD + id + EnumChatFormatting.DARK_GREEN + "x" + amount + EnumChatFormatting.BLUE + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(craftCostMap.get(id)*amount) + EnumChatFormatting.BLUE + ")");
			        	}else {
			        		process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.RED + "No buy/sell orders for " + EnumChatFormatting.GOLD + id + EnumChatFormatting.BLUE + " in bazaar and it doesn't have recipe");
			        		return new CraftResult(Float.MAX_VALUE, process);
			        	}
			        }else {
			        	alreadyTriedCraftingRecipe.add(id);
			        	CraftResult craft = calculateCraftCost(recipe2, bazaar, buy, tax, ">" + detailedInfo);
			        	
			        	bazaarCost = craft.cost;
			        	craftCostMap.put(id, bazaarCost);
			        	if (bazaarCost == Float.MAX_VALUE) {
			        		process.addAll(craft.craftings);
			        		return new CraftResult(Float.MAX_VALUE, process);
			        	}
			        	total_cost += bazaarCost * amount;
			        	
			        	process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.BLUE + "Calculated craft cost of recipe for " + EnumChatFormatting.GOLD + id + EnumChatFormatting.DARK_GREEN + "x" + amount + EnumChatFormatting.BLUE + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(bazaarCost*amount) + EnumChatFormatting.BLUE + "):");
			        	process.addAll(craft.craftings);
			        	continue;
			        }
				}
				
				float cost = bazaarCost.floatValue() * amount;
				
				if(craftCostMap.get(id) != null) {
					if(cost > craftCostMap.get(id) * amount) {
						cost = craftCostMap.get(id) * amount;
						process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.BLUE + "Using recipe for " + EnumChatFormatting.GOLD + id + EnumChatFormatting.DARK_AQUA + "x" + amount + EnumChatFormatting.BLUE + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(cost) + EnumChatFormatting.BLUE + ")");
					}else {
						process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.BLUE + "Found " + EnumChatFormatting.GOLD + id + EnumChatFormatting.DARK_AQUA + "x" + amount + EnumChatFormatting.BLUE + " in bazaar" + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(cost) + EnumChatFormatting.BLUE + ")");
					}
		        	
		        	total_cost += cost;
				}else {
					ItemRecipe recipe2 = RecipeHandler.getItemRecipe(id);
			        if (recipe2 == null || alreadyTriedCraftingRecipe.contains(id)) {
			        	total_cost += cost;
			        	process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.BLUE + "Found " + EnumChatFormatting.GOLD + id + EnumChatFormatting.GREEN + "x" + amount + EnumChatFormatting.BLUE + " in bazaar" + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(cost) + EnumChatFormatting.BLUE + ")");
			        }else {
			        	alreadyTriedCraftingRecipe.add(id);
			        	CraftResult craft = calculateCraftCost(recipe2, bazaar, buy, tax, ">" + detailedInfo);
			        	
			        	float craftCost = craft.cost;
			        	craftCostMap.put(id, craftCost);
			        	
			        	if(craftCost != Float.MAX_VALUE && cost > craftCost * amount) {
			        		cost = craftCost * amount;
			        		process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.BLUE + "Calculated craft cost of recipe for " + EnumChatFormatting.GOLD + id + EnumChatFormatting.AQUA + "x" + amount + EnumChatFormatting.BLUE + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(cost) + EnumChatFormatting.BLUE + "):");
			        		process.addAll(craft.craftings);
			        	}else {
			        		process.add(fixSpaceInDetailedInfo(detailedInfo) + EnumChatFormatting.BLUE + "Found " + EnumChatFormatting.GOLD + id + EnumChatFormatting.AQUA + "x" + amount + EnumChatFormatting.BLUE + " in bazaar" + " (" + EnumChatFormatting.WHITE + Utils.formatNumber(cost) + EnumChatFormatting.BLUE + ")");
						}
			        	
			        	total_cost += cost;
			        }
				}
				
			}
		}
		
		if(total_cost == 0) return new CraftResult(Float.MAX_VALUE, null);
		return new CraftResult(total_cost, process);
	}
	
	private static String fixSpaceInDetailedInfo(String di) {
		if(di.contains(">")) {
			return di;
		}else {
			return "";
		}
	}
}
