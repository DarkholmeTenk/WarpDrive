package cr0s.warpdrive.item;

import cr0s.warpdrive.WarpDrive;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemCrystalToken extends ItemAbstractBase {	
	private static ItemStack[] itemStackCache;
	private static final int COUNT = 6; 
	
	public ItemCrystalToken(final String registryName) {
		super(registryName);
		setHasSubtypes(true);
		setUnlocalizedName("warpdrive.tool.crystalToken");
		
		itemStackCache = new ItemStack[COUNT];
	}
	
	public static ItemStack getItemStack(final int damage) {
		if (damage < COUNT) {
			if (itemStackCache[damage] == null) {
				itemStackCache[damage] = new ItemStack(WarpDrive.itemCrystalToken, 1, damage);
			}
			return itemStackCache[damage];
		}
		return null;
	}
	
	public static ItemStack getItemStackNoCache(final int damage, final int amount) {
		if (damage < COUNT) {
			return new ItemStack(WarpDrive.itemCrystalToken, amount, damage);
		}
		return new ItemStack(WarpDrive.itemCrystalToken, amount, 0);
	}
	
	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		if (damage >= 0 && damage < COUNT) {
			return "item.warpdrive.tool.crystalToken" + damage;
		}
		return getUnlocalizedName();
	}
	
	@Override
	public void getSubItems(@Nonnull Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		for(int damage = 0; damage < COUNT; damage++) {
			list.add(new ItemStack(item, 1, damage));
		}
	}
	
	public static String getSchematicName(ItemStack itemStack) {
		String schematicName = "" + itemStack.getItemDamage();
		NBTTagCompound tagCompound = itemStack.getTagCompound();
		if (tagCompound != null && tagCompound.hasKey("shipName")) {
			schematicName = tagCompound.getString("shipName");
		}
		return schematicName;
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		
		String tooltipName1 = getUnlocalizedName(itemStack) + ".tooltip";
		if (I18n.hasKey(tooltipName1)) {
			WarpDrive.addTooltip(list, new TextComponentTranslation(tooltipName1, getSchematicName(itemStack)).getFormattedText());
		}
		
		String tooltipName2 = getUnlocalizedName() + ".tooltip";
		if ((!tooltipName1.equals(tooltipName2)) && I18n.hasKey(tooltipName2)) {
			WarpDrive.addTooltip(list, new TextComponentTranslation(tooltipName2, getSchematicName(itemStack)).getFormattedText());
		}
	}
}