package cr0s.warpdrive.block.detection;

import java.util.Random;

import cr0s.warpdrive.data.EnumRadarMode;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockRadar extends BlockAbstractContainer {
	public static final PropertyEnum<EnumRadarMode> MODE = PropertyEnum.create("mode", EnumRadarMode.class);
	
	public BlockRadar(final String registryName) {
		super(registryName, Material.IRON);
		setUnlocalizedName("warpdrive.detection.Radar");
		GameRegistry.registerTileEntity(TileEntityRadar.class, WarpDrive.PREFIX + registryName);
		
		setDefaultState(getDefaultState().withProperty(MODE, EnumRadarMode.INACTIVE));
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, MODE);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int metadata) {
		return getDefaultState()
				.withProperty(MODE, EnumRadarMode.get(metadata));
	}
	
	@Override
	public int getMetaFromState(IBlockState blockState) {
		return blockState.getValue(MODE).ordinal();
	}
	
	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
		return new TileEntityRadar();
	}
	
	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer entityPlayer, EnumHand hand, @Nullable ItemStack itemStackHeld, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		if (itemStackHeld == null) {
			TileEntity tileEntity = world.getTileEntity(blockPos);
			if (tileEntity instanceof TileEntityRadar) {
				WarpDrive.addChatMessage(entityPlayer, ((TileEntityRadar)tileEntity).getStatus());
				return true;
			}
		}
		
		return false;
	}
}
