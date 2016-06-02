package cr0s.warpdrive.block;

import java.math.BigDecimal;
import java.util.HashMap;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.Optional;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.UpgradeType;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

@Optional.InterfaceList({
	@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHCore"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2API"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2API")
})
public abstract class TileEntityAbstractEnergy extends TileEntityAbstractInterfaced implements IEnergyHandler, IEnergySink, IEnergySource {
	protected boolean addedToEnergyNet = false;
	protected int energyStored_internal = 0;
	private static final double EU_PER_INTERNAL = 1.0D;
	private static final double RF_PER_INTERNAL = 1800.0D / 437.5D;
	protected int IC2_sinkTier = 3;
	protected int IC2_sourceTier = 3;
	
	private int scanTickCount = -1;
	
	private Object[] cofhEnergyReceivers;
	
	protected final HashMap<UpgradeType,Integer> upgrades = new HashMap<>();
	
	public TileEntityAbstractEnergy() {
		super();
		if (WarpDriveConfig.isCoFHCoreLoaded) {
			RF_initialiseAPI();
		}
		addMethods(new String[] { "energy" });
	}
	
	public Object[] getUpgrades()
	{
		Object[] retVal = new Object[UpgradeType.values().length];
		for(UpgradeType type : UpgradeType.values())
		{
			int am = 0;
			if(upgrades.containsKey(type))
				am = upgrades.get(type);
			retVal[type.ordinal()] = type + ":" + am;
		}	
		return retVal;
	}
	
	// WarpDrive methods
	protected static int convertInternalToRF(int energy) {
		return (int)Math.round(energy * RF_PER_INTERNAL);
	}
	
	protected static int convertRFtoInternal(int energy) {
		return (int)Math.round(energy / RF_PER_INTERNAL);
	}
	
	protected static double convertInternalToEU(int energy) {
		return Math.round(energy * EU_PER_INTERNAL);
	}
	
	protected static int convertEUtoInternal(double amount) {
		return (int)Math.round(amount / EU_PER_INTERNAL);
	}
	
	public int getEnergyStored() {
		return energyStored_internal;
	}
	
	// Methods to override
	/**
	 * Should return the maximum amount of energy that can be stored (measured in internal energy units).
	 */
	public int getMaxEnergyStored() {
		return 0;
	}
	
	/**
	 * Should return the maximum amount of energy that can be output (measured in internal energy units).
	 */
	public int getPotentialEnergyOutput() {
		return 0;
	}
	
	/**
	 * Remove energy from storage, called after actual output happened (measured in internal energy units).
	 * Override this to use custom storage or measure output statistics.
	 */
	protected void energyOutputDone(int energyOutput_internal) {
		consumeEnergy(energyOutput_internal, false);
	}
	
	/**
	 * Should return true if that direction can receive energy.
	 */
	public boolean canInputEnergy(ForgeDirection from) {
		return false;
	}
	
	/**
	 * Should return true if that direction can output energy.
	 */
	public boolean canOutputEnergy(ForgeDirection to) {
		return false;
	}
	
	/**
	 * Consume energy from storage for internal usage or after outputting (measured in internal energy units).
	 * Override this to use custom storage or measure energy consumption statistics (internal usage or output).
	 */
	public boolean consumeEnergy(int amount_internal, boolean simulate) {
		int amountUpgraded = amount_internal;
		if (upgrades.containsKey(UpgradeType.Power)) {
			double valueMul = Math.pow(0.8, upgrades.get(UpgradeType.Power));
			amountUpgraded = (int) Math.ceil(valueMul * amountUpgraded);
		}
		
		if (upgrades.containsKey(UpgradeType.Range)) {
			double valueMul = Math.pow(1.2, upgrades.get(UpgradeType.Range));
			amountUpgraded = (int) Math.ceil(valueMul * amountUpgraded);
		}
		
		if (upgrades.containsKey(UpgradeType.Speed)) {
			double valueMul = Math.pow(1.2, upgrades.get(UpgradeType.Speed));
			amountUpgraded = (int) Math.ceil(valueMul * amountUpgraded);
		}
		// FIXME: upgrades balancing & implementation to be done...
		
		if (getEnergyStored() >= amount_internal) {
			if (!simulate) {
				energyStored_internal -= amount_internal;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Consume all internal energy and return it's value (measured in internal energy units).
	 * Override this to use custom storage or measure energy consumption statistics of this kind.
	 */
	public int consumeAllEnergy() {
		int temp = energyStored_internal;
		energyStored_internal = 0;
		return temp;
	}
	
	public Object[] energy() {
		return new Object[] { getEnergyStored(), getMaxEnergyStored() };
	}
	
	public String getEnergyStatus() {
		if (getMaxEnergyStored() == 0) {
			return "";
		}
		return StatCollector.translateToLocalFormatted("warpdrive.energy.statusLine",
				BigDecimal.valueOf(convertInternalToEU(getEnergyStored())).toPlainString(),
				BigDecimal.valueOf(convertInternalToEU(getMaxEnergyStored())).toPlainString() );
	}
	
	public String getStatus() {
		return StatCollector.translateToLocalFormatted("warpdrive.guide.prefix",
				getBlockType().getLocalizedName())
				+ getEnergyStatus();
	}
	
	// OpenComputer callback methods
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] energy(Context context, Arguments arguments) {
		return energy();
	}
	
	// ComputerCraft methods
	@Override
	@Optional.Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
		String methodName = getMethodName(method);
		
		if (methodName.equals("energy")) {
			return energy();
		}
		
		return super.callMethod(computer, context, method, arguments);
	}
	
	// Minecraft overrides
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if (worldObj.isRemote) {
			return;
		}
		
		// IndustrialCraft2
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			IC2_addToEnergyNet();
		}
		
		// Thermal Expansion
		if (WarpDriveConfig.isCoFHCoreLoaded) {
			scanTickCount++;
			if (scanTickCount >= 20) {
				scanTickCount = 0;
				scanForEnergyHandlers();
			}
			outputEnergy();
		}
	}
	
	@Override
	public void onChunkUnload() {
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			IC2_removeFromEnergyNet();
		}
		
		super.onChunkUnload();
	}
	
	@Override
	public void invalidate() {
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			IC2_removeFromEnergyNet();
		}
		
		super.invalidate();
	}
	
	// IndustrialCraft IEnergySink interface
	@Override
	@Optional.Method(modid = "IC2")
	public double getDemandedEnergy() {
		return Math.max(0.0D, convertInternalToEU(getMaxEnergyStored() - energyStored_internal));
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public double injectEnergy(ForgeDirection from, double amount_EU, double voltage) {
		int leftover_internal = 0;
		energyStored_internal += convertEUtoInternal(amount_EU);
		
		if (energyStored_internal > getMaxEnergyStored()) {
			leftover_internal = (energyStored_internal - getMaxEnergyStored());
			energyStored_internal = getMaxEnergyStored();
		}
		
		return convertInternalToEU(leftover_internal);
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection from) {
		return canInputEnergy(from);
	}
	
	// IndustrialCraft IEnergySource interface
	@Override
	@Optional.Method(modid = "IC2")
	public double getOfferedEnergy() {
		return convertInternalToEU(getPotentialEnergyOutput());
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public void drawEnergy(double amount_EU) {
		energyOutputDone(convertEUtoInternal(amount_EU));
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection to) {
		return canOutputEnergy(to);
	}
	
	@Optional.Method(modid = "IC2")
	private void IC2_addToEnergyNet() {
		if (!addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToEnergyNet = true;
		}
	}
	
	@Optional.Method(modid = "IC2")
	private void IC2_removeFromEnergyNet() {
		if (addedToEnergyNet) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnergyNet = false;
		}
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public int getSinkTier() {
		return IC2_sinkTier;
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public int getSourceTier() {
		return IC2_sourceTier;
	}
	
	
	// ThermalExpansion IEnergyHandler interface
	@Override
	@Optional.Method(modid = "CoFHCore")	/* IEnergyReceiver */
	public int receiveEnergy(ForgeDirection from, int maxReceive_RF, boolean simulate) {
		if (!canInputEnergy(from)) {
			return 0;
		}
		
		int maxStored_RF = getMaxEnergyStored(from);
		if (maxStored_RF == 0) {
			return 0;
		}
		int energyStored_RF = getEnergyStored(from);
		
		int toAdd_RF = Math.min(maxReceive_RF, maxStored_RF - energyStored_RF);
		if (!simulate) {
			energyStored_internal = Math.min(getMaxEnergyStored(), energyStored_internal + convertRFtoInternal(toAdd_RF));
		}
		
		return toAdd_RF;
	}
	
	@Override
	@Optional.Method(modid = "CoFHCore")	/* IEnergyProvider */
	public int extractEnergy(ForgeDirection from, int maxExtract_RF, boolean simulate) {
		if (!canOutputEnergy(from)) {
			return 0;
		}
		
		int potentialEnergyOutput_internal = getPotentialEnergyOutput();
		int energyExtracted_internal = Math.min(convertRFtoInternal(maxExtract_RF), potentialEnergyOutput_internal);
		if (!simulate) {
			energyOutputDone(energyExtracted_internal);
			// WarpDrive.debugPrint("extractEnergy Potential " + potentialEnergyOutput_internal + " EU, Requested " + maxExtract_RF + " RF, energyExtracted_internal " + energyExtracted_internal + "(" + convertInternalToRF(energyExtracted_internal) + " RF)");
		}
		return convertInternalToRF(energyExtracted_internal);
	}
	
	@Override
	@Optional.Method(modid = "CoFHCore")	/* IEnergyConnection */
	public boolean canConnectEnergy(ForgeDirection from) {
		return (getMaxEnergyStored() != 0) && (canInputEnergy(from) || canOutputEnergy(from)); // FIXME deadlock risk
	}
	
	@Override
	@Optional.Method(modid = "CoFHCore")	/* IEnergyReceiver and IEnergyProvider */
	public int getEnergyStored(ForgeDirection from) {
		return canConnectEnergy(from) ? convertInternalToRF(getEnergyStored()) : 0;
	}
	
	@Override
	@Optional.Method(modid = "CoFHCore")	/* IEnergyReceiver and IEnergyProvider */
	public int getMaxEnergyStored(ForgeDirection from) {
		return canConnectEnergy(from) ? convertInternalToRF(getMaxEnergyStored()) : 0;
	}
	
	
	// WarpDrive overrides for Thermal Expansion
	@Optional.Method(modid = "CoFHCore")
	private void outputEnergy(ForgeDirection from, IEnergyReceiver energyReceiver) {
		if (energyReceiver == null || worldObj.getTileEntity(xCoord + from.offsetX, yCoord + from.offsetY, zCoord + from.offsetZ) == null) {
			return;
		}
		int potentialEnergyOutput_internal = getPotentialEnergyOutput();
		if (potentialEnergyOutput_internal > 0) {
			int energyToOutput_RF = energyReceiver.receiveEnergy(from.getOpposite(), convertInternalToRF(potentialEnergyOutput_internal), true);
			if (energyToOutput_RF > 0) {
				int energyOutputted_RF = energyReceiver.receiveEnergy(from.getOpposite(), energyToOutput_RF, false);
				energyOutputDone(convertRFtoInternal(energyOutputted_RF));
				// WarpDrive.debugPrint("ForcedOutputEnergy Potential " + potentialEnergyOutput_internal + " EU, Actual output " + energyOutputted_RF + " RF, simulated at " + energyToOutput_RF + " RF");
			}
		}
	}
	
	@Optional.Method(modid = "CoFHCore")
	private void outputEnergy() {
		for(ForgeDirection from: ForgeDirection.VALID_DIRECTIONS) {
			if (cofhEnergyReceivers[from.ordinal()] != null) {
				outputEnergy(from, (IEnergyReceiver) cofhEnergyReceivers[from.ordinal()]);
			}
		}
	}
	
	@Optional.Method(modid = "CoFHCore")
	private void RF_initialiseAPI() {
		cofhEnergyReceivers = new IEnergyReceiver[ForgeDirection.VALID_DIRECTIONS.length];
	}
	
	
	// Forge overrides
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energyStored_internal = tag.getInteger("energy");
		if (energyStored_internal > getMaxEnergyStored()) {
			energyStored_internal = getMaxEnergyStored();
		}
		if (tag.hasKey("upgrades")) {
			NBTTagCompound upgradeTag = tag.getCompoundTag("upgrades");
			for (UpgradeType type : UpgradeType.values()) {
				if (upgradeTag.hasKey(type.toString()) && upgradeTag.getInteger(type.toString()) != 0) {
					upgrades.put(type, upgradeTag.getInteger(type.toString()));
				}
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (energyStored_internal < 0) {
			energyStored_internal = 0;
		}
		tag.setInteger("energy", energyStored_internal);
		if (!upgrades.isEmpty()) {
			NBTTagCompound upgradeTag = new NBTTagCompound();
			for (UpgradeType type : UpgradeType.values()) {
				if (upgrades.containsKey(type)) {
					upgradeTag.setInteger(type.toString(), upgrades.get(type));
				}
			}
			tag.setTag("upgrades", upgradeTag);
		}
	}
	
	// WarpDrive overrides
	@Override
	public void updatedNeighbours() {
		super.updatedNeighbours();
		
		if (WarpDriveConfig.isCoFHCoreLoaded) {
			scanForEnergyHandlers();
		}
	}
	
	@Optional.Method(modid = "CoFHCore")
	private void scanForEnergyHandlers() {
		for(ForgeDirection from : ForgeDirection.VALID_DIRECTIONS) {
			boolean energyReceiverFound = false;
			if (canConnectEnergy(from)) {
				TileEntity tileEntity = worldObj.getTileEntity(xCoord + from.offsetX, yCoord + from.offsetY, zCoord + from.offsetZ);
				if (tileEntity != null && tileEntity instanceof IEnergyReceiver) {
					IEnergyReceiver energyReceiver = (IEnergyReceiver)tileEntity;
					if (energyReceiver.canConnectEnergy(from.getOpposite())) {
						energyReceiverFound = true;
						cofhEnergyReceivers[from.ordinal()] = energyReceiver;
					}
				}
			}
			if (!energyReceiverFound) {
				cofhEnergyReceivers[from.ordinal()] = null;
			}
		}
	}
}