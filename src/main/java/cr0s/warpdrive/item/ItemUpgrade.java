package cr0s.warpdrive.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.UpgradeType;

public class ItemUpgrade extends Item {
	private static ItemStack[] isCache = new ItemStack[UpgradeType.values().length];
	private static IIcon[] iconBuffer = new IIcon[UpgradeType.values().length];
	
	public ItemUpgrade() {
		setHasSubtypes(true);
		setUnlocalizedName("warpdrive.upgrade.Malformed");
		setCreativeTab(WarpDrive.creativeTabWarpDrive);
	}
	
	private static boolean isValidDamage(int damage) {
		return damage >= 0 && damage < UpgradeType.values().length;
	}
	
	public static ItemStack getItemStack(UpgradeType energy) {
		if (!isValidDamage(energy.ordinal())) {
			return null;
		}
		
		if (isCache[energy.ordinal()] == null) {
			isCache[energy.ordinal()] = getItemStackNoCache(energy);
		}
		return isCache[energy.ordinal()];
	}
	
	public static ItemStack getItemStackNoCache(UpgradeType energy) {
		if (!isValidDamage(energy.ordinal())) {
			return null;
		}
		
		return new ItemStack(WarpDrive.itemUpgrade, 1, energy.ordinal());
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		
		int damage = itemStack.getItemDamage();
		if (isValidDamage(damage)) {
			return "item.warpdrive.upgrade." + UpgradeType.values()[damage];
		}
		
		return null;
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for (UpgradeType upgradeType : UpgradeType.values()) {
			list.add(getItemStack(upgradeType));
		}
	}
	
	@Override
	public void addInformation(ItemStack is, EntityPlayer pl, List list, boolean par4) {
		if (is == null) {
			return;
		}
		
		int damage = is.getItemDamage();
		if (damage == UpgradeType.Energy.ordinal()) {
			list.add("Increases the max energy of the machine");
		} else if (damage == UpgradeType.Power.ordinal()) {
			list.add("Decreases the power usage of the machine");
		} else if (damage == UpgradeType.Speed.ordinal()) {
			list.add("Increases the speed of the machine");
		} else if (damage == UpgradeType.Range.ordinal()) {
			list.add("Increases the range of the machine");
		}
	}
	
	@Override
	public void registerIcons(IIconRegister ir) {
		for (UpgradeType val : UpgradeType.values()) {
			iconBuffer[val.ordinal()] = ir.registerIcon("warpdrive:upgrade" + val);
		}
	}
	
	@Override
	public IIcon getIconFromDamage(int damage) {
		if (damage >= 0 && damage < UpgradeType.values().length) {
			return iconBuffer[damage];
		}
		return iconBuffer[0];
	}
}
