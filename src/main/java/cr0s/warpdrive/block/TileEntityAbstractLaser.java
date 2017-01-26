package cr0s.warpdrive.block;

import java.util.LinkedList;
import java.util.List;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraftforge.fml.common.Optional;
import cr0s.warpdrive.config.WarpDriveConfig;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;


// Abstract class to manage laser mediums
public abstract class TileEntityAbstractLaser extends TileEntityAbstractInterfaced {
	// direction of the laser medium stack
	protected EnumFacing facingLaserMedium = null;
	protected EnumFacing[] directionsValidLaserMedium = EnumFacing.values();
	protected int laserMediumMaxCount = 0;
	protected int laserMediumCount = 0;
	
	private final int updateInterval_ticks = 20 * WarpDriveConfig.SHIP_CONTROLLER_UPDATE_INTERVAL_SECONDS;
	private int updateTicks = updateInterval_ticks;
	private int bootTicks = 20;
	
	public TileEntityAbstractLaser() {
		super();
		
		addMethods(new String[] {
				"energy",
				"laserMediumDirection",
				"laserMediumCount"
		});
	}
	
	@Override
	public void update() {
		super.update();
		
		if (worldObj.isRemote) {
			return;
		}
		
		// accelerate update ticks during boot
		if (bootTicks > 0) {
			bootTicks--;
			if (facingLaserMedium == null) {
				updateTicks = 1;
			}
		}
		updateTicks--;
		if (updateTicks <= 0) {
			updateTicks = updateInterval_ticks;
			
			updateLaserMediumStatus();
		}
	}
	
	private void updateLaserMediumStatus() {
		for(EnumFacing facing : directionsValidLaserMedium) {
			TileEntity tileEntity = worldObj.getTileEntity(pos.offset(facing));
			if (tileEntity != null && tileEntity instanceof TileEntityLaserMedium) {
				facingLaserMedium = facing;
				laserMediumCount = 0;
				while(tileEntity != null && (tileEntity instanceof TileEntityLaserMedium) && laserMediumCount < laserMediumMaxCount) {
					laserMediumCount++;
					tileEntity = worldObj.getTileEntity(pos.offset(facing, laserMediumCount + 1));
				}
				return;
			}
		}
		facingLaserMedium = null;
	}
	
	protected int getEnergyStored() {
		return consumeCappedEnergyFromLaserMediums(Integer.MAX_VALUE, true);
	}
	
	protected boolean consumeEnergyFromLaserMediums(final int amount, final boolean simulate) {
		if (simulate) {
			return amount <= consumeCappedEnergyFromLaserMediums(amount, true);
		} else {
			if (amount > consumeCappedEnergyFromLaserMediums(amount, true)) {
				return false;
			} else {
				return amount <= consumeCappedEnergyFromLaserMediums(amount, false);
			}
		}
	}
	
	protected int consumeCappedEnergyFromLaserMediums(final int amount, final boolean simulate) {
		if (facingLaserMedium == null) {
			return 0;
		}
		
		// Primary scan of all laser mediums
		int totalEnergy = 0;
		int count = 1;
		List<TileEntityLaserMedium> laserMediums = new LinkedList<>();
		for (; count <= laserMediumMaxCount; count++) {
			TileEntity tileEntity = worldObj.getTileEntity(pos.offset(facingLaserMedium, count));
			if (!(tileEntity instanceof TileEntityLaserMedium)) {
				break;
			}
			laserMediums.add((TileEntityLaserMedium) tileEntity);
			totalEnergy += ((TileEntityLaserMedium) tileEntity).getEnergyStored();
		}
		count--;
		if (count == 0) {
			return 0;
		}
		if (simulate) {
			return totalEnergy;
		}
		
		// Compute average energy to get per laser medium, capped at its capacity
		int energyAverage = amount / count;
		int energyLeftOver = amount - energyAverage * count;
		if (energyAverage >= WarpDriveConfig.LASER_MEDIUM_MAX_ENERGY_STORED) {
			energyAverage = WarpDriveConfig.LASER_MEDIUM_MAX_ENERGY_STORED;
			energyLeftOver = 0;
		}
		
		// Secondary scan for laser medium below the required average
		for (TileEntityLaserMedium laserMedium : laserMediums) {
			int energyStored = laserMedium.getEnergyStored();
			if (energyStored < energyAverage) {
				energyLeftOver += energyAverage - energyStored;
			}
		}
		
		// Third and final pass for energy consumption
		int energyTotalConsumed = 0;
		for (TileEntityLaserMedium laserMedium : laserMediums) {
			int energyStored = laserMedium.getEnergyStored();
			int energyToConsume = Math.min(energyStored, energyAverage + energyLeftOver);
			energyLeftOver -= Math.max(0, energyToConsume - energyAverage);
			laserMedium.consumeEnergy(energyToConsume, false); // simulate is always false here
			energyTotalConsumed += energyToConsume;
		}
		return energyTotalConsumed;
	}
	
	protected Object[] energy() {
		if (facingLaserMedium == null) {
			return new Object[] { 0, 0 };
		} else {
			int energyStored = 0;
			int energyStoredMax = 0;
			int count = 1;
			// List<TileEntityLaserMedium> laserMediums = new LinkedList();
			for (; count <= laserMediumMaxCount; count++) {
				TileEntity tileEntity = worldObj.getTileEntity(pos.offset(facingLaserMedium, count));
				if (!(tileEntity instanceof TileEntityLaserMedium)) {
					break;
				}
				// laserMediums.add((TileEntityLaserMedium) tileEntity);
				energyStored += ((TileEntityLaserMedium) tileEntity).getEnergyStored();
				energyStoredMax += ((TileEntityLaserMedium) tileEntity).getMaxEnergyStored();
			}
			return new Object[] { energyStored, energyStoredMax };
		}
	}
	
	protected Object[] laserMediumDirection() {
		return new Object[] { facingLaserMedium.name(), facingLaserMedium.getFrontOffsetX(), facingLaserMedium.getFrontOffsetY(), facingLaserMedium.getFrontOffsetZ() };
	}
	
	protected Object[] laserMediumCount() {
		return new Object[] { laserMediumCount };
	}
	// OpenComputers callback methods
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] energy(Context context, Arguments arguments) {
		return energy();
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] laserMediumDirection(Context context, Arguments arguments) {
		return laserMediumDirection();
	}
	
	// ComputerCraft methods
	@Override
	@Optional.Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
		String methodName = getMethodName(method);
		
		switch (methodName) {
			case "energy":
				return energy();
			case "laserMediumDirection":
				return laserMediumDirection();
			case "laserMediumCount":
				return laserMediumCount();
		}
		
		return super.callMethod(computer, context, method, arguments);
	}
}
