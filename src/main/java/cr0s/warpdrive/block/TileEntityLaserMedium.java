package cr0s.warpdrive.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import cr0s.warpdrive.config.WarpDriveConfig;

public class TileEntityLaserMedium extends TileEntityAbstractEnergy {
	private int ticks = 0;
	
	public TileEntityLaserMedium() {
		peripheralName = "warpdriveLaserMedium";
		OC_enable = false;
	}
	
	@Override
	public void update() {
		super.update();
		
		if (worldObj.isRemote) {
			return;
		}
		
		ticks++;
		if (ticks > 20) {
			ticks = 0;
			
			int level = Math.max(0, Math.min(7, Math.round((getEnergyStored() * 8) / getMaxEnergyStored())));
			IBlockState blockState = worldObj.getBlockState(pos);
			if (blockState.getValue(BlockLaserMedium.LEVEL) != level) {
				updateBlockState(blockState, BlockLaserMedium.LEVEL, level);
			}
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return super.writeToNBT(tag);
	}
	
	// IEnergySink methods implementation
	@Override
	public int getMaxEnergyStored() {
		return WarpDriveConfig.LASER_MEDIUM_MAX_ENERGY_STORED;
	}
	
	@Override
	public boolean canInputEnergy(EnumFacing from) {
		return true;
	}
}
