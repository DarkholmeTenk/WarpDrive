package cr0s.warpdrive.api;

import java.util.Map;

import cr0s.warpdrive.data.UpgradeType;

public interface IUpgradable
{
	public boolean takeUpgrade(UpgradeType upgradeType,boolean simulate);
	public Map<UpgradeType,Integer> getInstalledUpgrades();
}
