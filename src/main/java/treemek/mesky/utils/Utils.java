package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Utils {

	
	// Taken from SkyblockAddons
		public static List<String> getItemLore(ItemStack itemStack) {
			final int NBT_INTEGER = 3;
			final int NBT_STRING = 8;
			final int NBT_LIST = 9;
			final int NBT_COMPOUND = 10;

			if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("display", NBT_COMPOUND)) {
				NBTTagCompound display = itemStack.getTagCompound().getCompoundTag("display");

				if (display.hasKey("Lore", NBT_LIST)) {
					NBTTagList lore = display.getTagList("Lore", NBT_STRING);

					List<String> loreAsList = new ArrayList<>();
					for (int lineNumber = 0; lineNumber < lore.tagCount(); lineNumber++) {
						loreAsList.add(lore.getStringTagAt(lineNumber));
					}

					return Collections.unmodifiableList(loreAsList);
				}
			}

			return Collections.emptyList();
		}
}
