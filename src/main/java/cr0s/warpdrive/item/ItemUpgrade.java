package cr0s.warpdrive.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.UpgradeType;

import javax.annotation.Nonnull;

public class ItemUpgrade extends ItemAbstractBase {
	private static ItemStack[] isCache = new ItemStack[UpgradeType.values().length];
	
	public ItemUpgrade(final String registryName) {
		super(registryName);
		setHasSubtypes(true);
		setUnlocalizedName("warpdrive.upgrade.Malformed");
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

	@Nonnull
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		if (itemStack == null) {
			return "invalidItemStack";
		}
		
		int damage = itemStack.getItemDamage();
		if (isValidDamage(damage)) {
			return "item.warpdrive.upgrade." + UpgradeType.values()[damage];
		}
		
		return "invalidUpgradeItemDamage";
	}
	
	@Override
	public void getSubItems(@Nonnull Item item, @Nonnull CreativeTabs creativeTabs, @Nonnull List<ItemStack> subItems) {
		for (UpgradeType upgradeType : UpgradeType.values()) {
			subItems.add(getItemStack(upgradeType));
		}
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean par4) {
		if (itemStack == null) {
			return;
		}
		
		int damage = itemStack.getItemDamage();
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
}
