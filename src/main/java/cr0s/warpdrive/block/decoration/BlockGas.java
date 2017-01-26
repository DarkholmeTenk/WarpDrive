package cr0s.warpdrive.block.decoration;

import java.util.List;
import java.util.Random;

import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.data.EnumGasColor;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cr0s.warpdrive.config.WarpDriveConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockGas extends BlockAbstractBase {
	public static final PropertyEnum<EnumGasColor> COLOR = PropertyEnum.create("color", EnumGasColor.class);
	
	public BlockGas(final String registryName) {
		super(registryName, Material.FIRE);
		setHardness(0.0F);
		setUnlocalizedName("warpdrive.passive.Gas");
		
		setDefaultState(getDefaultState().withProperty(COLOR, EnumGasColor.RED));
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, COLOR);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int metadata) {
		return getDefaultState()
				.withProperty(COLOR, EnumGasColor.get(metadata));
	}
	
	@Override
	public int getMetaFromState(IBlockState blockState) {
		return blockState.getValue(COLOR).ordinal();
	}
	
	@Nullable
	@Override
	public ItemBlock createItemBlock() {
		return new ItemBlockGas(this);
	}
	
	@Override
	public void getSubBlocks(@Nonnull Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
		for (EnumGasColor enumGasColor : EnumGasColor.values()) {
			list.add(new ItemStack(item, 1, enumGasColor.ordinal()));
		}
	}
	
	@Override
	public int damageDropped(IBlockState blockState) {
		return blockState.getBlock().getMetaFromState(blockState);
	}
	
	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull World world, @Nonnull BlockPos blockPos) {
		return null;
	}
	
	@Override
	public boolean isReplaceable(IBlockAccess worldIn, @Nonnull BlockPos blockPos) {
		return true;
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos blockPos) {
		return true;
	}
	
	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		return EnumPushReaction.DESTROY;
	}

	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}
	
	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos blockPos, EnumFacing facing) {
		BlockPos blockPosSide = blockPos.offset(facing);
		IBlockState blockStateSide = blockAccess.getBlockState(blockPosSide);
		if (blockStateSide.getBlock().isAssociatedBlock(this)) {
			return false;
		}
		return blockAccess.isAirBlock(blockPosSide);
	}
	
	@Override
	public boolean isCollidable() {
		return false;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos blockPos, IBlockState blockState) {
		// Gas blocks allow only in space
		if (world.provider.getDimension() != WarpDriveConfig.G_SPACE_DIMENSION_ID) {
			world.setBlockToAir(blockPos);
		}
	}
}