package cr0s.warpdrive.block;

import cr0s.warpdrive.WarpDrive;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

public class BlockChunkLoader extends BlockAbstractContainer {
	
	public BlockChunkLoader(final String registryName) {
		super(registryName, Material.IRON);
		setUnlocalizedName("warpdrive.machines.ChunkLoader");
		GameRegistry.registerTileEntity(TileEntityChunkLoader.class, WarpDrive.PREFIX + registryName);
	}
	
	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
		return new TileEntityChunkLoader();
	}
}
