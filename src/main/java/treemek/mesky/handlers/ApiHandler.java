package treemek.mesky.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Loader;
import treemek.mesky.utils.Utils;

public class ApiHandler {
	public enum ApiType {
		BAZAAR("skyblock/bazaar"),
		AUCTION("skyblock/auctions"),
		BINGO("skyblock/bingo"),
		ELECTION("skyblock/election"),
		ITEMS("resources/skyblock/items"),
		NEU_LOWESTBIN("lowestbin.json.gz");
		
	    private final String code;
		ApiType(String code) {
	        this.code = code;
	    }
	}
	
	public static JsonObject fetchHypixelApi(ApiType type) {
        try {
            URL url = new URL("https://api.hypixel.net/v2/" + type.code);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                JsonParser parser = new JsonParser(); // for older Gson
                JsonElement element = parser.parse(reader);
                return element.getAsJsonObject();
            }
        } catch (Exception e) {
        	Utils.writeToConsole("Error with fetching type: " + type.toString());
            e.printStackTrace();
            return null;
        }
    }
	
	public static JsonObject fetchApi(ApiType type) {
		if(type == ApiType.NEU_LOWESTBIN) {
			return fetchNeuApi(type);
		}else {
			return fetchHypixelApi(type);
		}
	}
	
	private static JsonObject fetchNeuApi(ApiType type) {
		try {
			URL	url = new URL("https://moulberry.codes/" + type.code);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setRequestMethod("GET");
	    
		    try (GZIPInputStream gzipStream = new GZIPInputStream(conn.getInputStream());
		         InputStreamReader reader = new InputStreamReader(gzipStream);
		         BufferedReader in = new BufferedReader(reader)) {
	
		        StringBuilder response = new StringBuilder();
		        String line;
		        while ((line = in.readLine()) != null) {
		            response.append(line);
		        }
	
		        return new JsonParser().parse(response.toString()).getAsJsonObject();
		    }
	    } catch (IOException e) {
			e.printStackTrace();
		}
	    
		return null;
	}
}
