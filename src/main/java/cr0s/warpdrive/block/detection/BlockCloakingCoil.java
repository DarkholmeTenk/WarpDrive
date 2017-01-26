package cr0s.warpdrive.block.detection;

import java.util.Random;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.BlockProperties;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockCloakingCoil extends BlockAbstractBase {
	
	public static final PropertyBool OUTER = PropertyBool.create("outer");
	
	public BlockCloakingCoil(final String registryName) {
		super(registryName, Material.IRON);
		setHardness(3.5F);
		setUnlocalizedName("warpdrive.detection.CloakingCoil");
		
		setDefaultState(getDefaultState().withProperty(BlockProperties.ACTIVE, false).withProperty(OUTER, false).withProperty(BlockProperties.FACING, EnumFacing.UP));
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockProperties.ACTIVE, OUTER, BlockProperties.FACING);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int metadata) {
		boolean isActive = (metadata & 7) != 0;
		boolean isOuter = (metadata & 7) > 1;
		return getDefaultState()
				.withProperty(BlockProperties.ACTIVE, isActive)
				.withProperty(OUTER, isOuter)
				.withProperty(BlockProperties.FACING, isOuter ? EnumFacing.getFront(metadata & 7 - 1) : EnumFacing.UP);
	}
	
	@Override
	public int getMetaFromState(IBlockState blockState) {
		if (!blockState.getValue(BlockProperties.ACTIVE)) {
			return 0;
		}
		if (!blockState.getValue(OUTER)) {
			return 1;
		}
		return 2 + blockState.getValue(BlockProperties.FACING).ordinal();
	}
	
	public static void setBlockState(@Nonnull World world, @Nonnull final BlockPos blockPos, final boolean isActive, final boolean isOuter, final EnumFacing enumFacing) {
		IBlockState blockStateActual = world.getBlockState(blockPos);
		IBlockState blockStateNew = blockStateActual.withProperty(BlockProperties.ACTIVE, isActive).withProperty(OUTER, isOuter);
		if (enumFacing != null) {
			blockStateNew = blockStateNew.withProperty(BlockProperties.FACING, enumFacing);
		}
		if (blockStateActual.getBlock().getMetaFromState(blockStateActual) != blockStateActual.getBlock().getMetaFromState(blockStateNew)) {
			world.setBlockState(blockPos, blockStateNew);
		}
	}
	
	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}
}
