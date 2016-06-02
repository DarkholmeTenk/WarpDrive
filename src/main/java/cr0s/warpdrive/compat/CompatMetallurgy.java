package cr0s.warpdrive.compat;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.config.WarpDriveConfig;

public class CompatMetallurgy implements IBlockTransformer {
	
	private static Class<?> classBlockMetallurgyMachine;
	
	public static void register() {
		try {
			classBlockMetallurgyMachine = Class.forName("com.teammetallurgy.metallurgy.machines.BlockMetallurgy");
			WarpDriveConfig.registerBlockTransformer("Metallurgy", new CompatMetallurgy());
		} catch(ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final Block block, final int metadata, final TileEntity tileEntity) {
		return classBlockMetallurgyMachine.isInstance(block);
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
	
	private static final int[] mrot = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public int rotate(final Block block, final int metadata, NBTTagCompound nbtTileEntity, final ITransformation transformation) {
		byte rotationSteps = transformation.getRotationSteps();
		if (rotationSteps == 0) {
			return metadata;
		}
		
		// metadata rotations
		if (classBlockMetallurgyMachine.isInstance(block)) {
			switch (rotationSteps) {
			case 1:
				return mrot[metadata];
			case 2:
				return mrot[mrot[metadata]];
			case 3:
				return mrot[mrot[mrot[metadata]]];
			default:
				return metadata;
			}
		}
		
		return metadata;
	}
	
	@Override
	public void restoreExternals(TileEntity tileEntity, ITransformation transformation, NBTBase nbtBase) {
		// nothing to do
	}
}
