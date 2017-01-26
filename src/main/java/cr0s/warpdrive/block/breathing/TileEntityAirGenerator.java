package cr0s.warpdrive.block.breathing;

import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.data.BlockProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import net.minecraft.util.math.BlockPos;

public class TileEntityAirGenerator extends TileEntityAbstractEnergy {
	private int cooldownTicks = 0;
	private static final int START_CONCENTRATION_VALUE = 15;
	
	public TileEntityAirGenerator() {
		super();
		
		peripheralName = "warpdriveAirGenerator";
	}
	
	@Override
	public void update() {
		super.update();
		
		if (worldObj.isRemote) {
			return;
		}
		
		if (isInvalid()) {
			return;
		}
		
		// Air generator works only in space & hyperspace
		if (worldObj.provider.getDimension() != WarpDriveConfig.G_SPACE_DIMENSION_ID && worldObj.provider.getDimension() != WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID) {
			IBlockState blockState = worldObj.getBlockState(pos);
			if (blockState.getValue(BlockProperties.ACTIVE)) {
				worldObj.setBlockState(pos, blockState.withProperty(BlockProperties.ACTIVE, false)); // set disabled texture
			}
			return;
		}
		
		cooldownTicks++;
		if (cooldownTicks > WarpDriveConfig.AIRGEN_AIR_GENERATION_TICKS) {
			IBlockState blockState = worldObj.getBlockState(pos);
			if (consumeEnergy(WarpDriveConfig.AIRGEN_ENERGY_PER_NEWAIRBLOCK, true)) {
				if (!blockState.getValue(BlockProperties.ACTIVE)) {
					worldObj.setBlockState(pos, blockState.withProperty(BlockProperties.ACTIVE, true)); // set enabled texture
				}
			} else {
				if (blockState.getValue(BlockProperties.ACTIVE)) {
					worldObj.setBlockState(pos, blockState.withProperty(BlockProperties.ACTIVE, false)); // set disabled texture
				}
			}
			releaseAir(pos.add( 1,  0,  0));
			releaseAir(pos.add(-1,  0,  0));
			releaseAir(pos.add( 0,  1,  0));
			releaseAir(pos.add( 0, -1,  0));
			releaseAir(pos.add( 0,  0,  1));
			releaseAir(pos.add( 0,  0, -1));
			
			cooldownTicks = 0;
		}
	}
	
	private void releaseAir(BlockPos blockPos) {
		IBlockState blockState = worldObj.getBlockState(blockPos);
		if (blockState.getBlock().isAir(blockState, worldObj, blockPos)) {// can be air
			int energy_cost = (!blockState.getBlock().isAssociatedBlock(WarpDrive.blockAir)) ? WarpDriveConfig.AIRGEN_ENERGY_PER_NEWAIRBLOCK : WarpDriveConfig.AIRGEN_ENERGY_PER_EXISTINGAIRBLOCK;
			if (consumeEnergy(energy_cost, true)) {// enough energy
				if (worldObj.setBlockState(blockPos, WarpDrive.blockAir.getStateFromMeta(START_CONCENTRATION_VALUE), 2)) {
					// (needs to renew air or was not maxed out)
					consumeEnergy(WarpDriveConfig.AIRGEN_ENERGY_PER_NEWAIRBLOCK, false);
				} else {
					consumeEnergy(WarpDriveConfig.AIRGEN_ENERGY_PER_EXISTINGAIRBLOCK, false);
				}
			} else {// low energy => remove air block
				if (blockState.getBlock().isAssociatedBlock(WarpDrive.blockAir)) {
					int metadata = blockState.getBlock().getMetaFromState(blockState);
					if (metadata > 4) {
						worldObj.setBlockState(blockPos, WarpDrive.blockAir.getStateFromMeta(metadata - 4), 2);
					} else if (metadata > 1) {
						worldObj.setBlockState(blockPos, WarpDrive.blockAir.getStateFromMeta(1), 2);
					} else {
						// worldObj.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
					}
				}
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
	
	@Override
	public int getMaxEnergyStored() {
		return WarpDriveConfig.AIRGEN_MAX_ENERGY_STORED;
	}
	
	@Override
	public boolean canInputEnergy(EnumFacing from) {
		return true;
	}
}
