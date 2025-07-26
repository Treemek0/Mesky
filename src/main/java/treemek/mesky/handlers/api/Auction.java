package treemek.mesky.handlers.api;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import treemek.mesky.handlers.ApiHandler;
import treemek.mesky.handlers.ApiHandler.ApiType;
import treemek.mesky.utils.Utils;

public class Auction {
	
	public static List<JsonObject> getAuctionItems(String itemName) {
	    JsonObject json = ApiHandler.fetchApi(ApiType.AUCTION);
	    if (json == null || !json.has("auctions")) return null;

	    JsonArray auctions = json.getAsJsonArray("auctions");
	    List<JsonObject> matchingItems = new ArrayList<>();

	    for (JsonElement element : auctions) {
	        JsonObject auction = element.getAsJsonObject();
	        String name = auction.get("item_name").getAsString();

	        if (name.equalsIgnoreCase(itemName)) {
	            matchingItems.add(auction);
	        }
	    }

	    return matchingItems;
	}
	
	public static Float getLowestBin(String itemName) {
	    JsonObject json = ApiHandler.fetchApi(ApiType.AUCTION);
	    if (json == null || !json.has("auctions")) return null;

	    JsonArray auctions = json.getAsJsonArray("auctions");
	    Float lowest = Float.MAX_VALUE;

	    for (JsonElement element : auctions) {
	        JsonObject auction = element.getAsJsonObject();

	        if (!auction.has("bin") || !auction.get("bin").getAsBoolean()) continue;

	        String name = auction.get("item_name").getAsString();
	        if (!name.equalsIgnoreCase(itemName)) continue;

	        float price = auction.get("starting_bid").getAsFloat();
	        if (price < lowest) {
	            lowest = price;
	        }
	    }

	    return (lowest == Float.MAX_VALUE) ? null : lowest;
	}

	public static void findProfitAuctions(float minProfit) {
	    JsonObject json = ApiHandler.fetchApi(ApiType.AUCTION);
	    if (json == null || !json.has("auctions")) return;

	    JsonArray auctions = json.getAsJsonArray("auctions");
	    Map<String, List<Float>> itemPrices = new HashMap<>();

	    for (JsonElement element : auctions) {
	        JsonObject auction = element.getAsJsonObject();
	        if (!auction.has("bin") || !auction.get("bin").getAsBoolean()) continue;

	        String itemName = auction.get("item_name").getAsString();
	        float price = auction.get("starting_bid").getAsFloat();

	        itemPrices.computeIfAbsent(itemName, k -> new ArrayList<>()).add(price);
	    }

	    for (Map.Entry<String, List<Float>> entry : itemPrices.entrySet()) {
	        String itemName = entry.getKey();
	        List<Float> prices = entry.getValue();

	        if (prices.size() < 4) continue; // skip items with not enough data

	        prices.sort(Float::compare);
	        List<Float> pricesForAverage = prices.subList(1, 4);
	        
	        float lowest = prices.get(0);
	        float average = (float) pricesForAverage.stream().skip(1).mapToDouble(p -> p).average().orElse(0);

	        float profit = average - lowest;
	        if (profit > minProfit) { // adjust threshold as needed
	        	String message = EnumChatFormatting.AQUA + itemName + EnumChatFormatting.GOLD + " | BIN: " + Utils.formatNumber(lowest)
	        			 + EnumChatFormatting.YELLOW + " | Avg: " + Utils.formatNumber(average)
	        			 + EnumChatFormatting.BLUE + " | Profit: " + Utils.formatNumber(profit);

	            ChatComponentText chatText = new ChatComponentText(message);
	            ChatStyle style = new ChatStyle();
	            style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ahs " + itemName));
	            chatText.setChatStyle(style);

	            Utils.addMinecraftMessage(chatText);
	        }
	    }
	}
}
