package cr0s.warpdrive;

import net.minecraft.util.DamageSource;

public class DamageLaser extends DamageSource {

	public DamageLaser() {
		super("warpdrive.laser");
		setDamageBypassesArmor();
		setDamageIsAbsolute();
	}
	
}
