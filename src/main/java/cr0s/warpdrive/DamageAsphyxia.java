package cr0s.warpdrive;

import net.minecraft.util.DamageSource;

public class DamageAsphyxia extends DamageSource {

	public DamageAsphyxia() {
		super("warpdrive.asphyxia");
		setDamageBypassesArmor();
		setDamageIsAbsolute();
	}
	
}
