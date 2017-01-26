package cr0s.warpdrive.block.decoration;

import cr0s.warpdrive.data.BlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

public class BlockLamp_long extends BlockAbstractLamp {
	
	private static final AxisAlignedBB AABB_DOWN  = new AxisAlignedBB(0.00D, 0.80D, 0.32D, 1.00D, 1.00D, 0.68D);
	private static final AxisAlignedBB AABB_UP    = new AxisAlignedBB(0.00D, 0.00D, 0.32D, 1.00D, 0.20D, 0.68D);
	private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.00D, 0.32D, 0.80D, 1.00D, 0.68D, 1.00D);
	private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.00D, 0.32D, 0.00D, 1.00D, 0.68D, 0.20D);
	private static final AxisAlignedBB AABB_WEST  = new AxisAlignedBB(0.80D, 0.32D, 0.00D, 1.00D, 0.68D, 1.00D);
	private static final AxisAlignedBB AABB_EAST  = new AxisAlignedBB(0.00D, 0.32D, 0.00D, 0.20D, 0.68D, 1.00D);
	
	public BlockLamp_long(final String registryName) {
		super(registryName, "warpdrive.passive.long_lamp");
	}
	
	@Nonnull
	@SuppressWarnings("deprecation")
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(BlockProperties.FACING)) {
			case DOWN : return AABB_DOWN ;
			case UP   : return AABB_UP   ;
			case NORTH: return AABB_NORTH;
			case SOUTH: return AABB_SOUTH;
			case WEST : return AABB_WEST ;
			case EAST : return AABB_EAST ;
			default   : return AABB_UP;
		}
	}
}