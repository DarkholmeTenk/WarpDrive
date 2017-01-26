package cr0s.warpdrive.block.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.config.WarpDriveConfig;

import javax.annotation.Nonnull;

public class TileEntityEnergyBank extends TileEntityAbstractEnergy {
	
	static final byte MODE_DISABLED = 0;
	static final byte MODE_INPUT = 1;
	static final byte MODE_OUTPUT = 2;
	private static final byte[] MODE_DEFAULT_SIDES = { MODE_INPUT, MODE_INPUT, MODE_OUTPUT, MODE_OUTPUT, MODE_OUTPUT, MODE_OUTPUT };
	private byte[] modeSide = MODE_DEFAULT_SIDES.clone();
	
	public TileEntityEnergyBank() {
		super();
		IC2_sinkTier = 0;
		IC2_sourceTier = 0;
		peripheralName = "warpdriveEnergyBank";
	}
	
	@Override
	public int getPotentialEnergyOutput() {
		return getEnergyStored();
	}
	
	@Override
	protected void energyOutputDone(int energyOutput) {
		consumeEnergy(energyOutput, false);
	}
	
	@Override
	public int getMaxEnergyStored() {
		return WarpDriveConfig.ENERGY_BANK_MAX_ENERGY_STORED;
	}
	
	@Override
	public boolean canInputEnergy(EnumFacing from) {
		return modeSide[from.ordinal()] == MODE_INPUT;
	}
	
	@Override
	public boolean canOutputEnergy(EnumFacing to) {
		return modeSide[to.ordinal()] == MODE_OUTPUT;
	}
	
	byte getMode(final EnumFacing facing) {
		return modeSide[facing.ordinal()];
	}
	
	void setMode(final EnumFacing facing, final byte mode) {
		modeSide[facing.ordinal()] = (byte)(mode % 3);
		markDirty();
	}
	
	// Forge overrides
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setByteArray("modeSide", modeSide);
		return nbtTagCompound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		modeSide = nbt.getByteArray("modeSide");
		if (modeSide.length != 6) {
			modeSide = MODE_DEFAULT_SIDES.clone();
		}
	}
	
	@Override
	public NBTTagCompound writeItemDropNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound = super.writeItemDropNBT(nbtTagCompound);
		return nbtTagCompound;
	}

	@Nonnull
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		return tagCompound;
	}

	@Override
	public void onDataPacket(NetworkManager networkManager, SPacketUpdateTileEntity packet) {
		NBTTagCompound tagCompound = packet.getNbtCompound();
		readFromNBT(tagCompound);
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
	}
}