package cr0s.warpdrive.item;

import net.minecraft.item.ItemStack;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IAirCanister;
import cr0s.warpdrive.data.EnumComponentType;

public class ItemAirCanisterFull extends ItemAbstractBase implements IAirCanister {
	
	public ItemAirCanisterFull(final String registryName) {
		super(registryName);
		setMaxDamage(20);
		setMaxStackSize(1);
		setUnlocalizedName("warpdrive.armor.AirCanisterFull");
	}
	
	@Override
	public boolean canContainAir(ItemStack itemStack) {
		if (itemStack != null && itemStack.getItem() instanceof ItemAirCanisterFull) {
			return itemStack.getItemDamage() > 0;
		}
		return false;
	}
	
	@Override
	public boolean containsAir(ItemStack itemStack) {
		return true;
	}
	
	@Override
	public ItemStack emptyDrop(ItemStack itemStack) {
		return ItemComponent.getItemStackNoCache(EnumComponentType.AIR_CANISTER, 1);
	}
	
	@Override
	public ItemStack fullDrop(ItemStack itemStack) {
		return new ItemStack(WarpDrive.itemAirCanisterFull, 1);
	}
}
