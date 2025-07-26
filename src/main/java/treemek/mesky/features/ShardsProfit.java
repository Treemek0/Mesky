package treemek.mesky.features;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import treemek.mesky.Reference;
import treemek.mesky.features.ShardsProfit.Shard;
import treemek.mesky.features.ShardsProfit.ShardsWrapper;
import treemek.mesky.handlers.ApiHandler;
import treemek.mesky.handlers.ApiHandler.ApiType;
import treemek.mesky.handlers.api.Bazaar;
import treemek.mesky.utils.Utils;

public class ShardsProfit {
	// TODO
	
	public class ShardsWrapper {
	    public Map<String, Shard> shards;
	}

	public class Shard {
	    public String productID;
	    public String id;
	    public String name;
	    public List<String> family;
	    public String type;
	    public String rarity;
	    public List<List<FusionRequirement>> fusion;
	    public Float buyPrice = 0f;
	    public Float sellPrice = 0f;
	}

	public class FusionRequirement {
	    public String type = "";
	    public String rarity = "";
	    public String family = "";
	    public List<String> shards = new ArrayList<>();
	}
	
	
	public static void getShardsProfit() {
		try {
			ResourceLocation rl = new ResourceLocation(Reference.MODID, "shards.json");
			InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
			InputStreamReader reader = new InputStreamReader(stream);

			Gson gson = new Gson();
			ShardsWrapper wrapper = gson.fromJson(reader, ShardsWrapper.class);
			Map<String, Shard> allShards = wrapper.shards;
			
			JsonObject products = ApiHandler.fetchApi(ApiType.BAZAAR).getAsJsonObject("products");
			
			for (Entry<String, Shard> entry : allShards.entrySet()) {
				String id = entry.getKey();
				Shard shard = entry.getValue();
				
				JsonObject product = products.getAsJsonObject(shard.productID);
			    if (product != null && product.has("sell_summary")) {

			        for (JsonElement e : product.getAsJsonArray("sell_summary")) {
				        if (e.isJsonObject()) {
				        	shard.sellPrice = e.getAsJsonObject().get("pricePerUnit").getAsFloat();
				        }
				    }
			    }
				
				List<List<Shard>> shardsFromReq = new ArrayList<>();
				
				if(shard.fusion.isEmpty()) continue;
				
				List<FusionRequirement> fusions = shard.fusion.get(0);
				for (FusionRequirement fusion : fusions) {
					List<Shard> matchingShards = new ArrayList<>();
					
					for (Entry<String, Shard> shardForFusion : allShards.entrySet()) {
						Shard fusionShard = shardForFusion.getValue();
						
						if(fusion.type == null || fusion.type == fusionShard.type) {
							if(fusion.family == null || fusionShard.family.contains(fusion.family)) {
								if(isRarityCorrect(fusionShard, fusion.rarity)) {
									if(fusion.shards == null || fusion.shards.contains(fusionShard.name)) {
										matchingShards.add(fusionShard);
									}
								}
							}
						}
					}
					
					shardsFromReq.add(matchingShards);
				}
				
				Map<List<Shard>, Float> profitShards = new HashMap<>();
				for (Shard shardFirst : shardsFromReq.get(0)) {
					JsonObject product1 = products.getAsJsonObject(shardFirst.productID);
				    if (product1 != null && product1.has("buy_summary")) {
				    	 for (JsonElement e : product1.getAsJsonArray("buy_summary")) {
				 	        if (e.isJsonObject()) {
				 	        	shardFirst.buyPrice = e.getAsJsonObject().get("pricePerUnit").getAsFloat();
				 	        }
				 	    }
				    }
					
					for (Shard shardSecond : shardsFromReq.get(1)) {
						JsonObject product2 = products.getAsJsonObject(shardSecond.productID);
					    if (product2 != null && product2.has("buy_summary")) {
					    	for (JsonElement e : product2.getAsJsonArray("buy_summary")) {
					 	        if (e.isJsonObject()) {
					 	        	shardSecond.buyPrice = e.getAsJsonObject().get("pricePerUnit").getAsFloat();
					 	        }
					 	    }
					    }
						
						if(shardFirst.buyPrice*5 + shardSecond.buyPrice*5 < shard.sellPrice) {
							
							List<Shard> profitFusionShards =  Arrays.asList(shard, shardFirst, shardSecond);
							float profit = shard.sellPrice - shardFirst.buyPrice*5 + shardSecond.buyPrice*5;
							profitShards.put(profitFusionShards, profit);
							Utils.debug("Profit shard " + shard.name + ": " + shardFirst.name + "x5 + " + shardSecond.name + "x5 = " + formatNumber(profit));
						}
					}
				}
			}
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
		    e.printStackTrace();
		}
	}
	
	public static String formatNumber(float number) {
	    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
	    symbols.setGroupingSeparator('\''); // Use apostrophe as separator

	    DecimalFormat formatter = new DecimalFormat("#,###", symbols);
	    return formatter.format(number);
	}
	
	public static boolean isRarityCorrect(Shard shard, String rarityRequirement) {
		if(rarityRequirement == null) {
			return true;
		}
		
	    List<String> rarities = Arrays.asList("Common", "Uncommon", "Rare", "Epic", "Legendary", "Mythic");
	    
	    String shardRarity = shard.rarity;
	    
	    if (!rarities.contains(shardRarity)) return false;
	    
	    String baseRarity = rarityRequirement.replace("+", "");
	    if (!rarities.contains(baseRarity)) return false;

	    int shardIndex = rarities.indexOf(shardRarity);
	    int requiredIndex = rarities.indexOf(baseRarity);

	    if (rarityRequirement.endsWith("+")) {
	        return shardIndex >= requiredIndex;
	    } else {
	        return shardIndex == requiredIndex;
	    }
	}
}
