package cr0s.warpdrive;

import net.minecraft.util.DamageSource;

public class DamageShock extends DamageSource {

	public DamageShock() {
		super("warpdrive.shock");
		setDamageBypassesArmor();
		setDamageIsAbsolute();
	}
	
}
