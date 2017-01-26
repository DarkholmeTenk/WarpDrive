package cr0s.warpdrive.block.energy;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.BlockAbstractContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

public class BlockEnanReactorCore extends BlockAbstractContainer {
	
	public BlockEnanReactorCore(final String registryName) {
		super(registryName, Material.IRON);
		setUnlocalizedName("warpdrive.energy.EnanReactorCore");
		GameRegistry.registerTileEntity(TileEntityEnanReactorCore.class, WarpDrive.PREFIX + registryName);
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
		return new TileEntityEnanReactorCore();
	}
	
	@Override
	public void breakBlock(World world, @Nonnull BlockPos blockPos, @Nonnull IBlockState blockState) {
		super.breakBlock(world, blockPos, blockState);
		
		int[] offsetsX = { -2, 2, 0, 0 };
		int[] offsetsZ = { 0, 0, -2, 2 };
		for (int i = 0; i < 4; i++) {
			TileEntity tileEntity = world.getTileEntity(blockPos.add(offsetsX[i], 0, offsetsZ[i]));
			if (tileEntity instanceof TileEntityEnanReactorLaser) {
				((TileEntityEnanReactorLaser) tileEntity).unlink();
			}
		}
	}
}