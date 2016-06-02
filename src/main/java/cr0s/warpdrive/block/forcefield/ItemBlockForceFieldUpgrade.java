package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.DecorativeType;
import cr0s.warpdrive.data.EnumForceFieldUpgrade;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemBlockForceFieldUpgrade extends ItemBlock {
	private static ItemStack[] itemStackCache;
	
	public ItemBlockForceFieldUpgrade(Block block) {
		super(block);
		setHasSubtypes(true);
		setUnlocalizedName("warpdrive.forcefield.upgrade");
		itemStackCache = new ItemStack[EnumForceFieldUpgrade.length];
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage;
	}
		
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for(EnumForceFieldUpgrade enumForceFieldUpgrade : EnumForceFieldUpgrade.values()) {
			list.add(new ItemStack(item, 1, enumForceFieldUpgrade.ordinal()));
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		if (damage >= 0 && damage < EnumForceFieldUpgrade.length) {
			return getUnlocalizedName() + "." + EnumForceFieldUpgrade.get(damage).unlocalizedName;
		}
		return getUnlocalizedName();
	}
	
	public static ItemStack getItemStack(EnumForceFieldUpgrade enumForceFieldUpgrade) {
		if (enumForceFieldUpgrade != null) {
			int damage = enumForceFieldUpgrade.ordinal();
			if (itemStackCache[damage] == null) {
				itemStackCache[damage] = new ItemStack(WarpDrive.itemComponent, 1, damage);
			}
			return itemStackCache[damage];
		}
		return null;
	}
	
	public static ItemStack getItemStackNoCache(EnumForceFieldUpgrade enumForceFieldUpgrade, int amount) {
		return new ItemStack(WarpDrive.blockForceFieldUpgrade, amount, enumForceFieldUpgrade.ordinal());
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		
		String tooltipName1 = getUnlocalizedName(itemStack) + ".tooltip";
		if (StatCollector.canTranslate(tooltipName1)) {
			WarpDrive.addTooltip(list, StatCollector.translateToLocalFormatted(tooltipName1));
		}
		
		String tooltipName2 = getUnlocalizedName() + ".tooltip";
		if ((!tooltipName1.equals(tooltipName2)) && StatCollector.canTranslate(tooltipName2)) {
			WarpDrive.addTooltip(list, StatCollector.translateToLocalFormatted(tooltipName2));
		}
	}
}
