package cr0s.warpdrive.block;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockUpdateDetector;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.VectorI;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public abstract class TileEntityAbstractBase extends TileEntity implements IBlockUpdateDetector {
	private boolean isFirstTick = true;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (isFirstTick) {
			isFirstTick = false;
			onFirstUpdateTick();
		}
	}
	
	protected void onFirstUpdateTick() {
		// No operation
	}
	
	@Override
	public void updatedNeighbours() {
	}
	
	protected boolean isOnPlanet() {
		return worldObj.provider.dimensionId == 0;
	}
	
	protected void updateMetadata(int metadata) {
		if (getBlockMetadata() != metadata) {
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, metadata, 2);
		}
	}
	
	
	// Inventory management methods
	
	public static ItemStack copyWithSize(ItemStack itemStack, int newSize) {
		ItemStack ret = itemStack.copy();
		ret.stackSize = newSize;
		return ret;
	}
	
	protected IInventory getFirstConnectedInventory() {
		TileEntity result;
		
		for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
			result = worldObj.getTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ);
			if (result != null && (result instanceof IInventory)) {
				return (IInventory) result;
			}
		}
		return null;
	}
	
	protected boolean addToConnectedInventory(ItemStack itemStack) {
		List<ItemStack> stacks = new ArrayList<>(1);
		stacks.add(itemStack);
		return addToConnectedInventory(stacks);
	}
	
	protected boolean addToConnectedInventory(List<ItemStack> stacks) {
		boolean overflow = false;
		if (stacks != null) {
			int qtyLeft;
			for (ItemStack stack : stacks) {
				qtyLeft = addToInventory(getFirstConnectedInventory(), stack);
				if (qtyLeft > 0) {
					if (WarpDriveConfig.LOGGING_COLLECTION) {
						WarpDrive.logger.info(this + " Overflow detected");
					}
					overflow = true;
					int transfer;
					while (qtyLeft > 0) {
						transfer = Math.min(qtyLeft, stack.getMaxStackSize());
						ItemStack itemStackDrop = copyWithSize(stack, transfer);
						EntityItem entityItem = new EntityItem(worldObj, xCoord + 0.5D, yCoord + 1.0D, zCoord + 0.5D, itemStackDrop);
						worldObj.spawnEntityInWorld(entityItem);
						qtyLeft -= transfer;
					}
				}
			}
		}
		return overflow;
	}
	
	private int addToInventory(IInventory inventory, ItemStack itemStackSource) {
		if (itemStackSource == null) {
			return 0;
		}
		
		int qtyLeft = itemStackSource.stackSize;
		int transfer;
		
		if (inventory != null) {
			// fill existing stacks first
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (!inventory.isItemValidForSlot(i, itemStackSource)) {
					continue;
				}
				
				ItemStack itemStack = inventory.getStackInSlot(i);
				if (itemStack == null || !itemStack.isItemEqual(itemStackSource)) {
					continue;
				}
				
				transfer = Math.min(qtyLeft, itemStack.getMaxStackSize() - itemStack.stackSize);
				itemStack.stackSize += transfer;
				qtyLeft -= transfer;
				if (qtyLeft <= 0) {
					return 0;
				}
			}
			
			// put remaining in empty slot
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (!inventory.isItemValidForSlot(i, itemStackSource)) {
					continue;
				}
				
				ItemStack itemStack = inventory.getStackInSlot(i);
				if (itemStack != null) {
					continue;
				}
				
				transfer = Math.min(qtyLeft, itemStackSource.getMaxStackSize());
				ItemStack dest = copyWithSize(itemStackSource, transfer);
				inventory.setInventorySlotContents(i, dest);
				qtyLeft -= transfer;
				
				if (qtyLeft <= 0) {
					return 0;
				}
			}
		}
		
		return qtyLeft;
	}
	
	
	// searching methods
	
	public static final ForgeDirection[] UP_DIRECTIONS = { ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST };
	public static Set<VectorI> getConnectedBlocks(World world, VectorI start, ForgeDirection[] directions, HashSet<Block> whitelist, int maxRange, VectorI... ignore) {
		return getConnectedBlocks(world, Arrays.asList(start), directions, whitelist, maxRange, ignore);
	}
	public static Set<VectorI> getConnectedBlocks(World world, Collection<VectorI> start, ForgeDirection[] directions, HashSet<Block> whitelist, int maxRange, VectorI... ignore) {
		Set<VectorI> toIgnore = new HashSet<>();
		if (ignore != null) {
			toIgnore.addAll(Arrays.asList(ignore));
		}
		
		Set<VectorI> toIterate = new HashSet<>();
		toIterate.addAll(start);
		
		Set<VectorI> toIterateNext;
		
		Set<VectorI> iterated = new HashSet<>();
		
		int range = 0;
		while(!toIterate.isEmpty() && range < maxRange) {
			toIterateNext = new HashSet<>();
			for (VectorI current : toIterate) {
				if (whitelist.contains(current.getBlock_noChunkLoading(world))) {
					iterated.add(current);
				}
				
				for(ForgeDirection direction : directions) {
					VectorI next = current.clone(direction);
					if (!iterated.contains(next) && !toIgnore.contains(next) && !toIterate.contains(next) && !toIterateNext.contains(next)) {
						if (whitelist.contains(next.getBlock_noChunkLoading(world))) {
							toIterateNext.add(next);
						}
					}
				}
			}
			toIterate = toIterateNext;
			range++;
		}
		
		return iterated;
	}
	
	
	// data manipulation methods
	
	protected static int toInt(double d) {
		return (int) Math.round(d);
	}
	
	protected static int toInt(Object object) {
		return toInt(toDouble(object));
	}
	
	protected static double toDouble(Object object) {
		return Double.parseDouble(object.toString());
	}
	
	protected static float toFloat(Object object) {
		return Float.parseFloat(object.toString());
	}
	
	protected static boolean toBool(Object object) {
		if (object == null) {
			 return false;
		}
		if (object instanceof Boolean) {
			 return ((Boolean) object);
		}
		String string = object.toString();
		return string.equals("true") || string.equals("1.0") || string.equals("1") || string.equals("y") || string.equals("yes");
	}
	
	protected static int clamp(final int min, final int max, final int value) {
		return Math.min(max, Math.max(value, min));
	}
	
	protected static double clamp(final double min, final double max, final double value) {
		return Math.min(max, Math.max(value, min));
	}
	
	protected boolean isBlockBreakCanceled(EntityPlayer entityPlayer, World world, int eventX, int eventY, int eventZ) {
		return WarpDrive.proxy.isBlockBreakCanceled(entityPlayer, xCoord, yCoord, zCoord, world, eventX, eventY, eventZ);
	}
	
	protected boolean isBlockPlaceCanceled(EntityPlayer entityPlayer, World world, int eventX, int eventY, int eventZ, Block block, int metadata) {
		return WarpDrive.proxy.isBlockPlaceCanceled(entityPlayer, xCoord, yCoord, zCoord, world, eventX, eventY, eventZ, block, metadata);
	}
}
