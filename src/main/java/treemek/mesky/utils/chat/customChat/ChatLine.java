package treemek.mesky.utils.chat.customChat;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class ChatLine {
	IChatComponent message;
	long createdTime;
	
	public ChatLine() {
		message = new ChatComponentText("");
		createdTime = System.currentTimeMillis();
	}
	
	public ChatLine(IChatComponent message) {
		this.message = message;
		createdTime = System.currentTimeMillis();
	}
	
	public ChatLine(IChatComponent message, long createdTime) {
		this.message = message;
		this.createdTime = createdTime;
	}
}
