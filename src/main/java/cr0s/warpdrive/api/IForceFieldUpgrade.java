package cr0s.warpdrive.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by LemADEC on 16/05/2016.
 */
public interface IForceFieldUpgrade {
	// Unique name for this upgrade category
	String getUpgradeKey();
	
	// Bonus give to this upgrade category (can be positive or negative)
	int getUpgradeValue();
	
	// Maximum speed of this upgrade in blocks per projector update
	// 0.3 to 30 blocks per upgrade ?
	float getMaxScanSpeed(final String upgradeKey, final int upgradeCount);
	float getMaxPlaceSpeed(final String upgradeKey, final int upgradeCount);
	
	// Energy impacts of this upgrade
	float getStartupEnergyCost(final String upgradeKey, final int upgradeCount);
	float getScanEnergyCost(final String upgradeKey, final int upgradeCount);
	float getPlaceEnergyCost(final String upgradeKey, final int upgradeCount);
}
