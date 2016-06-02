package cr0s.warpdrive;

import net.minecraft.util.DamageSource;

public class DamageTeleportation extends DamageSource {
	public DamageTeleportation() {
		super("warpdrive.teleportation");
		setDamageBypassesArmor();
	}
}