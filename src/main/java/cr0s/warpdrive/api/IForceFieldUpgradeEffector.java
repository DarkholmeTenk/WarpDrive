package cr0s.warpdrive.api;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IForceFieldUpgradeEffector {
	// Apply scaling to the sum of all upgrades for this upgrade category
	// Use this to cap the upgrade or apply non-linear scaling.
	float getScaledValue(final float ratio, final float upgradeValue);
	
	// Speed factor of this upgrade
	// Values above 1.0F will increase the speed
	// Values below 1.0F will decrease the speed
	// Return 0 or below if the upgrade has no effect
	float getScanSpeedFactor(final float scaledValue);
	float getPlaceSpeedFactor(final float scaledValue);
	
	// Energy impacts of this upgrade
	float getStartupEnergyCost(final float scaledValue);
	float getScanEnergyCost(final float scaledValue);
	float getPlaceEnergyCost(final float scaledValue);
	float getEntityEffectEnergyCost(final float scaledValue);
	
	// Entity impact of this upgrade
	// Return 
	int onEntityEffect(final float scaledValue, World world, final int projectorX, final int projectorY, final int projectorZ,
					   final BlockPos blockPos, Entity entity);
}
