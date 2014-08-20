package cr0s.WarpDrive.api;

import java.util.Map;

import cr0s.WarpDrive.EnumUpgradeTypes;

public interface IUpgradable
{
	public boolean takeUpgrade(EnumUpgradeTypes upgradeType,boolean simulate);
	public Map<EnumUpgradeTypes,Integer> getInstalledUpgrades();
}
