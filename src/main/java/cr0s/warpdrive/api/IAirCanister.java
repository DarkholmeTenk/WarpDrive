package cr0s.warpdrive.api;

import net.minecraft.item.ItemStack;

@Deprecated // we should use fluid storage instead here
public interface IAirCanister {
	// Return true if that itemStack is Air compatible (i.e. may or already contains air)
	public boolean canContainAir(ItemStack itemStack);
	
	public boolean containsAir(ItemStack itemStack);
	public ItemStack emptyDrop(ItemStack itemStack);
	public ItemStack fullDrop(ItemStack itemStack);
}
