package cr0s.warpdrive.compat;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.config.WarpDriveConfig;

public class CompatIndustrialCraft2 implements IBlockTransformer {
	
	private static Class<?> classIC2tileEntity;
	
	public static void register() {
		try {
			classIC2tileEntity = Class.forName("ic2.core.block.TileEntityBlock");
			WarpDriveConfig.registerBlockTransformer("IC2", new CompatIndustrialCraft2());
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final Block block, final int metadata, final TileEntity tileEntity) {
		return classIC2tileEntity.isInstance(tileEntity);
	}
	
	@Override
	public boolean isJumpReady(final Block block, final int metadata, final TileEntity tileEntity, StringBuilder reason) {
		return true;
	}
	
	@Override
	public NBTBase saveExternals(final TileEntity tileEntity) {
		// nothing to do
		return null;
	}
	
	@Override
	public void remove(TileEntity tileEntity) {
		// nothing to do
	}
	
	@Override
	public int rotate(final Block block, final int metadata, NBTTagCompound nbtTileEntity, final ITransformation transformation) {
		byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0 || !nbtTileEntity.hasKey("facing")) {
			return metadata;
		}
		
		short facing = nbtTileEntity.getShort("facing");
		final short[] mrot = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
		switch (rotationSteps) {
		case 1:
			nbtTileEntity.setShort("facing", mrot[facing]);
			return metadata;
		case 2:
			nbtTileEntity.setShort("facing", mrot[mrot[facing]]);
			return metadata;
		case 3:
			nbtTileEntity.setShort("facing", mrot[mrot[mrot[facing]]]);
			return metadata;
		default:
			return metadata;
		}
	}
	
	@Override
	public void restoreExternals(TileEntity tileEntity, ITransformation transformation, NBTBase nbtBase) {
		// nothing to do
	}
}
