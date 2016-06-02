package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.api.IForceFieldUpgrade;
import cr0s.warpdrive.data.EnumForceFieldUpgrade;

/**
 * Created by LemADEC on 16/05/2016.
 */
public class TileEntityForceFieldUpgrade extends TileEntityAbstractForceField implements IForceFieldUpgrade {
	public TileEntityForceFieldUpgrade() {
		super();
		
		peripheralName = "warpdriveForceFieldUpgrade";
	}
	
	@Override
	public String getUpgradeKey() {
		return isEnabled ? EnumForceFieldUpgrade.get(getBlockMetadata()).unlocalizedName : null;
	}
	
	@Override
	public int getUpgradeValue() {
		return 1000;
	}
	
	@Override
	public float getMaxScanSpeed(final String upgradeKey, final int upgradeCount) {
		return 10.0F;
	}
	
	@Override
	public float getMaxPlaceSpeed(final String upgradeKey, final int upgradeCount) {
		return 10.0F;
	}
	
	@Override
	public float getStartupEnergyCost(final String upgradeKey, final int upgradeCount) {
		return 100.0F;
	}
	
	@Override
	public float getScanEnergyCost(final String upgradeKey, final int upgradeCount) {
		return 1.0F;
	}
	
	@Override
	public float getPlaceEnergyCost(final String upgradeKey, final int upgradeCount) {
		return 10.0F;
	}
}
