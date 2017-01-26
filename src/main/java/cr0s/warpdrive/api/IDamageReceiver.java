package cr0s.warpdrive.api;

import cr0s.warpdrive.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDamageReceiver {
	
	/**
	 * Return the block hardness to use for damage resolution.
	 * Useful for blocks that are unbreakable to normal tools like force fields.
	 */
	float getBlockHardness(IBlockState blockState, World world, final BlockPos blockPos,
	                       final DamageSource damageSource, final int damageParameter, final Vector3 damageDirection, final int damageLevel);
	
	/**
	 * Resolve damage applied to a certain level at specific coordinates.
	 * Returns the remaining damage level or 0 if it was fully absorbed.
	 */
	int applyDamage(IBlockState blockState, World world, final BlockPos blockPos,
					final DamageSource damageSource, final int damageParameter, final Vector3 damageDirection, final int damageLevel);
}
