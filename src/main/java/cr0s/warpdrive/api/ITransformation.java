package cr0s.warpdrive.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ITransformation {
	
	World getTargetWorld();
	
	byte getRotationSteps();
	
	float getRotationYaw();
	
	Vec3d apply(final double sourceX, final double sourceY, final double sourceZ);
	
	BlockPos apply(final int sourceX, final int sourceY, final int sourceZ);
	
	BlockPos apply(final TileEntity tileEntity);
	
	BlockPos apply(final BlockPos blockPos);
}