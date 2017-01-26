package cr0s.warpdrive.block.movement;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockTransporter extends BlockAbstractContainer {
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	
	public BlockTransporter(final String registryName) {
		super(registryName, Material.IRON);
		setUnlocalizedName("warpdrive.movement.Transporter");
		GameRegistry.registerTileEntity(TileEntityTransporter.class, WarpDrive.PREFIX + registryName);
		
		setDefaultState(getDefaultState().withProperty(ACTIVE, false));
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ACTIVE);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int metadata) {
		return getDefaultState()
				.withProperty(ACTIVE, metadata != 0);
	}
	
	@Override
	public int getMetaFromState(IBlockState blockState) {
		return blockState.getValue(ACTIVE) ? 1 : 0;
	}
	
	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
		return new TileEntityTransporter();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer entityPlayer, EnumHand hand, @Nullable ItemStack itemStackHeld, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		if (itemStackHeld == null) {
			TileEntity tileEntity = world.getTileEntity(blockPos);
			if (tileEntity instanceof TileEntityTransporter) {
				WarpDrive.addChatMessage(entityPlayer, ((TileEntityTransporter)tileEntity).getStatus());
				return true;
			}
		}
		
		return false;
	}
}