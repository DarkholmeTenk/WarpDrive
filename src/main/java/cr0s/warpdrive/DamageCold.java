package cr0s.warpdrive;

import net.minecraft.util.DamageSource;

public class DamageCold extends DamageSource {

	public DamageCold() {
		super("warpdrive.cool");
		setDamageBypassesArmor();
		setDamageIsAbsolute();
	}
	
}
