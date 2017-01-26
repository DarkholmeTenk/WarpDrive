package cr0s.warpdrive.block.forcefield;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Optional;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBeamFrequency;
import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.ForceFieldRegistry;
import cr0s.warpdrive.data.Vector3;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

import javax.annotation.Nonnull;

public class TileEntityAbstractForceField extends TileEntityAbstractEnergy implements IBeamFrequency {
	// persistent properties
	protected byte tier = -1;
	protected int beamFrequency = -1;
	public boolean isEnabled = true;
	
	// computed properties
	protected Vector3 vRGB;
	protected boolean isConnected = true;
	
	public TileEntityAbstractForceField() {
		super();

		addMethods(new String[]{
			"enable",
			"beamFrequency"
		});
	}
	
	@Override
	protected void onFirstUpdateTick() {
		Block block = getBlockType();
		if (block instanceof BlockAbstractForceField) {
			tier = ((BlockAbstractForceField) block).tier;
		} else {
			WarpDrive.logger.error("Missing block for " + this + " at " + worldObj + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
		}
		if (beamFrequency >= 0 && beamFrequency <= IBeamFrequency.BEAM_FREQUENCY_MAX) {
			ForceFieldRegistry.updateInRegistry(this);
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		if (worldObj.isRemote) {
			return;
		}
		
		// Frequency is not set
		boolean legacy_isConnected = isConnected; 
		isConnected = beamFrequency > 0 && beamFrequency <= IBeamFrequency.BEAM_FREQUENCY_MAX;
		if (legacy_isConnected != isConnected) {
			markDirty();
		}
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		ForceFieldRegistry.removeFromRegistry(this);
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		// reload chunks as needed
		// ForceFieldRegistry.removeFromRegistry(this);
	}
	
	@Override
	public int getBeamFrequency() {
		return beamFrequency;
	}
	
	@Override
	public void setBeamFrequency(final int parBeamFrequency) {
		if (beamFrequency != parBeamFrequency && (parBeamFrequency <= BEAM_FREQUENCY_MAX) && (parBeamFrequency > 0)) {
			if (WarpDriveConfig.LOGGING_VIDEO_CHANNEL) {
				WarpDrive.logger.info(this + " Beam frequency set from " + beamFrequency + " to " + parBeamFrequency);
			}
			if (worldObj != null) {
				ForceFieldRegistry.removeFromRegistry(this);
			}
			beamFrequency = parBeamFrequency;
			vRGB = IBeamFrequency.getBeamColor(beamFrequency);
		}
		markDirty();
		if (worldObj != null) {
			ForceFieldRegistry.updateInRegistry(this);
		}
	}

	private ITextComponent getBeamFrequencyStatus() {
		if (beamFrequency == -1) {
			return new TextComponentTranslation("warpdrive.beamFrequency.statusLine.undefined");
		} else if (beamFrequency < 0) {
			return new TextComponentTranslation("warpdrive.beamFrequency.statusLine.invalid", beamFrequency);
		} else {
			return new TextComponentTranslation("warpdrive.beamFrequency.statusLine.valid", beamFrequency);
		}
	}
	
	@Override
	public ITextComponent getStatus() {
		ITextComponent energyStatus = getEnergyStatus();
		return super.getStatus()
	        .appendSibling(energyStatus.toString().isEmpty() ? new TextComponentString("") : new TextComponentString("\n").appendSibling(energyStatus))
			.appendSibling(new TextComponentString("\n")).appendSibling(getBeamFrequencyStatus());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tier = tag.getByte("tier");
		setBeamFrequency(tag.getInteger("beamFrequency"));
		isEnabled = !tag.hasKey("isEnabled") || tag.getBoolean("isEnabled"); 
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setByte("tier", tier);
		tag.setInteger("beamFrequency", beamFrequency);
		tag.setBoolean("isEnabled", isEnabled);
		return tag;
	}
	
	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		tagCompound.setBoolean("isConnected", isConnected);
		return tagCompound;
	}
	
	@Override
	public void onDataPacket(NetworkManager networkManager, SPacketUpdateTileEntity packet) {
		NBTTagCompound tagCompound = packet.getNbtCompound();
		readFromNBT(tagCompound);
		isConnected = tagCompound.getBoolean("isConnected");
	}
	
	// OpenComputer callback methods
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] enable(Context context, Arguments arguments) throws Exception {
		return enable(argumentsOCtoCC(arguments));
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] beamFrequency(Context context, Arguments arguments) {
		if (arguments.count() == 1) {
			setBeamFrequency(arguments.checkInteger(0));
		}
		return new Integer[] { beamFrequency };
	}
	
	public Object[] enable(Object[] arguments) throws Exception {
		if (arguments.length == 1) {
			boolean enable;
			try {
				enable = toBool(arguments[0]);
			} catch (Exception exception) {
				throw new Exception("Function expects a boolean value");
			}
			isEnabled = enable;
		}
		return new Object[] { isEnabled };
	}
	
	// ComputerCraft IPeripheral methods implementation
	@Override
	@Optional.Method(modid = "ComputerCraft")
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) {
		String methodName = getMethodName(method);
		
		try {
			switch (methodName) {
				case "enable":
					return enable(arguments);
				
				case "beamFrequency":
					if (arguments.length == 1) {
						setBeamFrequency(toInt(arguments[0]));
					}
					return new Integer[]{ beamFrequency };
			}
		} catch (Exception exception) {
			return new String[] { exception.getMessage() };
		}
		
		return super.callMethod(computer, context, method, arguments);
	}
	
	@Override
	public String toString() {
		return String.format("%s Beam \'%d\' @ \'%s\' (%d %d %d)", getClass().getSimpleName(),
			beamFrequency, worldObj == null ? "~NULL~" : worldObj.getWorldInfo().getWorldName(), pos.getX(), pos.getY(), pos.getZ());
	}
}
