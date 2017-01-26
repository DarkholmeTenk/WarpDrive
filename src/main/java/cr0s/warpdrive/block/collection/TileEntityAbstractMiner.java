package cr0s.warpdrive.block.collection;

import java.util.ArrayList;
import java.util.List;

import cr0s.warpdrive.data.SoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.TileEntityAbstractLaser;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.data.VectorI;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public abstract class TileEntityAbstractMiner extends TileEntityAbstractLaser {
	// machine type
	protected EnumFacing	laserOutputSide = EnumFacing.NORTH;
	
	// machine state
	protected boolean			enableSilktouch = false;
	
	// pre-computation
	protected Vector3				laserOutput = null;
	
	public TileEntityAbstractMiner() {
		super();
	}
	
	@Override
	public void validate() {
		super.validate();
		laserOutput = new Vector3(this).translate(0.5D).translate(laserOutputSide, 0.5D);
	}
	
	protected void stop() {
		if (WarpDriveConfig.LOGGING_COLLECTION) {
			WarpDrive.logger.info(this + " Stop requested");
		}
	}
	
	protected void harvestBlock(final BlockPos valuable) {
		IBlockState blockState = worldObj.getBlockState(valuable);
		if (blockState.getBlock() == Blocks.AIR) {
			return;
		}
		if (blockState.getBlock() instanceof BlockLiquid) {
			// Evaporate fluid
			worldObj.playSound(null, valuable, net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
					2.6F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.8F);
		} else {
			List<ItemStack> itemStacks = getItemStackFromBlock(valuable, blockState);
			if (addToConnectedInventories(itemStacks)) {
				stop();
			}
			// standard harvest block effect
			worldObj.playEvent(2001, valuable, Block.getStateId(blockState));
		}
		worldObj.setBlockToAir(valuable);
	}
	
	private List<ItemStack> getItemStackFromBlock(BlockPos blockPos, IBlockState blockState) {
		if (blockState == null) {
			WarpDrive.logger.error(this + " Invalid block at " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ());
			return null;
		}
		if (enableSilktouch) {
			boolean isSilkHarvestable = false;
			try {
				isSilkHarvestable = blockState.getBlock().canSilkHarvest(worldObj, blockPos, blockState, null);
			} catch (Exception exception) {// protect in case the mined block is corrupted
				exception.printStackTrace();
			}
			if (isSilkHarvestable) {
				ArrayList<ItemStack> isBlock = new ArrayList<>();
				// TODO 1.10
				// isBlock.add(blockState.getBlock().createStackedBlock(blockState));
				return isBlock;
			}
		}
		
		try {
			return blockState.getBlock().getDrops(worldObj, blockPos, blockState, 0);
		} catch (Exception exception) {// protect in case the mined block is corrupted
			exception.printStackTrace();
			return null;
		}
	}
	
	// NBT DATA
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		enableSilktouch = tag.getBoolean("enableSilktouch");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setBoolean("enableSilktouch", enableSilktouch);
		return tag;
	}
}
