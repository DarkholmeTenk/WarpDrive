package cr0s.warpdrive.api;

import cr0s.warpdrive.data.Vector3;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public interface IDamageReceiver {
	
	/**
	 * Return the block hardness to use for damage resolution.
	 * Useful for blocks that are unbreakable to normal tools like forcefields.
	 */
	float getBlockHardness(World world, final int x, final int y, final int z,
	                       final DamageSource damageSource, final int damageParameter, final Vector3 damageDirection, final int damageLevel);
	
	/**
	 * Resolve damage applied to a certain level at specific coordinates.
	 * Returns the remaining damage level or 0 if it was fully absorbed.
	 */
	int applyDamage(World world, final int x, final int y, final int z,
	                final DamageSource damageSource, final int damageParameter, final Vector3 damageDirection, final int damageLevel);
}
