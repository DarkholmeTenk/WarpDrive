package cr0s.warpdrive.block;

import java.math.BigDecimal;
import java.util.HashMap;

import cr0s.warpdrive.WarpDrive;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.UpgradeType;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;

@Optional.InterfaceList({
	@Optional.Interface(iface = "cofh.api.energy.IEnergyProvider", modid = "CoFHCore"),
	@Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = "CoFHCore"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2")
})
public abstract class TileEntityAbstractEnergy extends TileEntityAbstractInterfaced implements IEnergyProvider, IEnergyReceiver, IEnergySink, IEnergySource {
	private boolean addedToEnergyNet = false;
	private int energyStored_internal = 0;
	private static final double EU_PER_INTERNAL = 1.0D;
	private static final double RF_PER_INTERNAL = 1800.0D / 437.5D;
	protected int IC2_sinkTier = 3;
	protected int IC2_sourceTier = 3;
	
	private int scanTickCount = -1;
	
	private Object[] cofhEnergyReceivers;
	
	@Deprecated
	protected final HashMap<UpgradeType, Integer> deprecated_upgrades = new HashMap<>();
	
	public TileEntityAbstractEnergy() {
		super();
		if (WarpDriveConfig.isCoFHCoreLoaded) {
			RF_initialiseAPI();
		}
		addMethods(new String[] { "energy" });
	}
	
	@Deprecated
	public Object[] getUpgrades_deprecated() {
		Object[] retVal = new Object[UpgradeType.values().length];
		for (UpgradeType type : UpgradeType.values()) {
			int am = 0;
			if (deprecated_upgrades.containsKey(type))
				am = deprecated_upgrades.get(type);
			retVal[type.ordinal()] = type + ":" + am;
		}
		return retVal;
	}
	
	// WarpDrive methods
	protected static int convertInternalToRF(int energy) {
		return (int) Math.round(energy * RF_PER_INTERNAL);
	}
	
	protected static int convertRFtoInternal(int energy) {
		return (int) Math.round(energy / RF_PER_INTERNAL);
	}
	
	protected static double convertInternalToEU_ceil(int energy) {
		return Math.ceil(energy * EU_PER_INTERNAL);
	}
	
	protected static double convertInternalToEU_floor(int energy) {
		return Math.floor(energy * EU_PER_INTERNAL);
	}
	
	protected static int convertEUtoInternal_ceil(double amount) {
		return (int) Math.ceil(amount / EU_PER_INTERNAL);
	}
	
	protected static int convertEUtoInternal_floor(double amount) {
		return (int) Math.floor(amount / EU_PER_INTERNAL);
	}
	
	public int getEnergyStored() {
		return clamp(0, getMaxEnergyStored(), energyStored_internal);
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
	@SuppressWarnings("UnusedParameters")
	public boolean canInputEnergy(EnumFacing from) {
		return false;
	}
	
	/**
	 * Should return true if that direction can output energy.
	 */
	@SuppressWarnings("UnusedParameters")
	public boolean canOutputEnergy(EnumFacing to) {
		return false;
	}
	
	/**
	 * Consume energy from storage for internal usage or after outputting (measured in internal energy units).
	 * Override this to use custom storage or measure energy consumption statistics (internal usage or output).
	 */
	public boolean consumeEnergy(int amount_internal, boolean simulate) {
		int amountUpgraded = amount_internal;
		if (deprecated_upgrades.containsKey(UpgradeType.Power)) {
			double valueMul = Math.pow(0.8, deprecated_upgrades.get(UpgradeType.Power));
			amountUpgraded = (int) Math.ceil(valueMul * amountUpgraded);
		}
		
		if (deprecated_upgrades.containsKey(UpgradeType.Range)) {
			double valueMul = Math.pow(1.2, deprecated_upgrades.get(UpgradeType.Range));
			amountUpgraded = (int) Math.ceil(valueMul * amountUpgraded);
		}
		
		if (deprecated_upgrades.containsKey(UpgradeType.Speed)) {
			double valueMul = Math.pow(1.2, deprecated_upgrades.get(UpgradeType.Speed));
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
		int temp = getEnergyStored();
		energyStored_internal = 0;
		return temp;
	}
	
	public Object[] energy() {
		return new Object[] { getEnergyStored(), getMaxEnergyStored() };
	}
	
	public ITextComponent getEnergyStatus() {
		if (getMaxEnergyStored() == 0) {
			return new TextComponentString("");
		}
		return new TextComponentTranslation("warpdrive.energy.statusLine",
			BigDecimal.valueOf(convertInternalToEU_floor(getEnergyStored())).toPlainString(),
			BigDecimal.valueOf(convertInternalToEU_floor(getMaxEnergyStored())).toPlainString());
	}
	
	@Override
	public ITextComponent getStatus() {
		ITextComponent textEnergyStatus = getEnergyStatus();
		if (textEnergyStatus.getFormattedText().isEmpty()) {
			return super.getStatus();
		} else {
			return super.getStatus()
				.appendSibling(new TextComponentString("\n")).appendSibling(textEnergyStatus);
		}
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
	public void update() {
		super.update();
		
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
		return Math.max(0.0D, convertInternalToEU_floor(getMaxEnergyStored() - getEnergyStored()));
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public double injectEnergy(EnumFacing from, double amount_EU, double voltage) {
		if (WarpDriveConfig.LOGGING_ENERGY) {
			WarpDrive.logger.info(this + " [IC2]injectEnergy from " + from + " amount_EU " + amount_EU + " " + voltage);
		}
		if (canInputEnergy(from)) {
			int leftover_internal = 0;
			energyStored_internal += convertEUtoInternal_floor(amount_EU);
			
			if (energyStored_internal > getMaxEnergyStored()) {
				leftover_internal = (energyStored_internal - getMaxEnergyStored());
				energyStored_internal = getMaxEnergyStored();
			}
			
			return convertInternalToEU_ceil(leftover_internal);
		} else {
			return amount_EU;
		}
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing from) {
		return canInputEnergy(from);
	}
	
	// IndustrialCraft IEnergySource interface
	@Override
	@Optional.Method(modid = "IC2")
	public double getOfferedEnergy() {
		return convertInternalToEU_floor(getPotentialEnergyOutput());
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public void drawEnergy(double amount_EU) {
		if (WarpDriveConfig.LOGGING_ENERGY) {
			WarpDrive.logger.info(this + " [IC2]drawEnergy amount_EU " + amount_EU);
		}
		energyOutputDone(convertEUtoInternal_ceil(amount_EU));
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing to) {
		if (WarpDriveConfig.LOGGING_ENERGY) {
			WarpDrive.logger.info(this + " [IC2]emitsEnergyTo receiver " + receiver + " to " + to);
		}
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

	// IndustrialCraft IEnergySink interface
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
	public int receiveEnergy(EnumFacing from, int maxReceive_RF, boolean simulate) {
		if (WarpDriveConfig.LOGGING_ENERGY) {
			WarpDrive.logger.info(this + " [CoFH]receiveEnergy from " + from + " maxReceive_RF " + maxReceive_RF + " simulate " + simulate);
		}
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
			energyStored_internal = Math.min(getMaxEnergyStored(), getEnergyStored() + convertRFtoInternal(toAdd_RF));
		}
		
		return toAdd_RF;
	}
	
	@Override
	@Optional.Method(modid = "CoFHCore")	/* IEnergyProvider */
	public int extractEnergy(EnumFacing from, int maxExtract_RF, boolean simulate) {
		if (WarpDriveConfig.LOGGING_ENERGY) {
			WarpDrive.logger.info(this + " [CoFH]extractEnergy from " + from + " maxExtract_RF " + maxExtract_RF + " simulate " + simulate);
		}
		if (!canOutputEnergy(from)) {
			return 0;
		}
		
		int potentialEnergyOutput_internal = getPotentialEnergyOutput();
		int energyExtracted_internal = Math.min(convertRFtoInternal(maxExtract_RF), potentialEnergyOutput_internal);
		if (!simulate) {
			energyOutputDone(energyExtracted_internal);
		}
		return convertInternalToRF(energyExtracted_internal);
	}
	
	@Override
	@Optional.Method(modid = "CoFHCore")	/* IEnergyConnection */
	public boolean canConnectEnergy(EnumFacing from) {
		return (getMaxEnergyStored() != 0) && (canInputEnergy(from) || canOutputEnergy(from)); // Warning: deadlock risk depending on child implementation
	}
	
	@Override
	@Optional.Method(modid = "CoFHCore")	/* IEnergyReceiver and IEnergyProvider */
	public int getEnergyStored(EnumFacing from) {
		return canConnectEnergy(from) ? convertInternalToRF(getEnergyStored()) : 0;
	}
	
	@Override
	@Optional.Method(modid = "CoFHCore")	/* IEnergyReceiver and IEnergyProvider */
	public int getMaxEnergyStored(EnumFacing from) {
		return canConnectEnergy(from) ? convertInternalToRF(getMaxEnergyStored()) : 0;
	}
	
	
	// WarpDrive overrides for Thermal Expansion
	@Optional.Method(modid = "CoFHCore")
	private void outputEnergy(EnumFacing from, IEnergyReceiver energyReceiver) {
		if (energyReceiver == null || worldObj.getTileEntity(pos.add(from.getFrontOffsetX(), from.getFrontOffsetY(), from.getFrontOffsetZ())) == null) {
			return;
		}
		if (!canOutputEnergy(from)) {
			return;
		}
		int potentialEnergyOutput_internal = getPotentialEnergyOutput();
		if (potentialEnergyOutput_internal > 0) {
			int energyToOutput_RF = energyReceiver.receiveEnergy(from.getOpposite(), convertInternalToRF(potentialEnergyOutput_internal), true);
			if (energyToOutput_RF > 0) {
				int energyOutputted_RF = energyReceiver.receiveEnergy(from.getOpposite(), energyToOutput_RF, false);
				energyOutputDone(convertRFtoInternal(energyOutputted_RF));
			}
		}
	}
	
	@Optional.Method(modid = "CoFHCore")
	private void outputEnergy() {
		for (EnumFacing from : EnumFacing.VALUES) {
			if (cofhEnergyReceivers[from.ordinal()] != null) {
				outputEnergy(from, (IEnergyReceiver) cofhEnergyReceivers[from.ordinal()]);
			}
		}
	}
	
	@Optional.Method(modid = "CoFHCore")
	private void RF_initialiseAPI() {
		cofhEnergyReceivers = new IEnergyReceiver[EnumFacing.VALUES.length];
	}
	
	
	// Forge overrides
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		energyStored_internal = tag.getInteger("energy");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setInteger("energy", getEnergyStored());
		return tag;
	}
	
	@Override
	public NBTTagCompound writeItemDropNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound = super.writeItemDropNBT(nbtTagCompound);
		nbtTagCompound.removeTag("energy");
		return nbtTagCompound;
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
		if (WarpDriveConfig.LOGGING_ENERGY) {
			WarpDrive.logger.info(this + " [CoFH]scanForEnergyHandlers");
		}
		for (EnumFacing from : EnumFacing.VALUES) {
			boolean energyReceiverFound = false;
			if (canConnectEnergy(from)) {
				TileEntity tileEntity = worldObj.getTileEntity(pos.add(from.getFrontOffsetX(), from.getFrontOffsetY(), from.getFrontOffsetZ()));
				if (tileEntity != null && tileEntity instanceof IEnergyReceiver) {
					IEnergyReceiver energyReceiver = (IEnergyReceiver) tileEntity;
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