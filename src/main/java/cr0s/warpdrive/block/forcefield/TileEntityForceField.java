package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.block.TileEntityAbstractBase;
import cr0s.warpdrive.data.ForceFieldSetup;
import cr0s.warpdrive.data.VectorI;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by LemADEC on 16/05/2016.
 */
public class TileEntityForceField extends TileEntityAbstractBase {
	private VectorI vProjector;
	// cache parameters used for rendering, force projector check for others
	private int cache_beamFrequency;
	private ItemStack cache_itemStackCamouflage;
	private int gracePeriod_calls = 1;
	
	@Override
	public boolean canUpdate() {
		return false;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("projector")) {
			vProjector = VectorI.readFromNBT(tag.getCompoundTag("projector"));
			cache_beamFrequency = tag.getInteger("beamFrequency");
			if (tag.hasKey("projector")) {
				try {
					cache_itemStackCamouflage = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("camouflage"));
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			} else {
				cache_itemStackCamouflage = null;
			}
		} else {
			vProjector = null;
			cache_beamFrequency = -1;
			cache_itemStackCamouflage = null;
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		if (vProjector != null) {
			tagCompound.setTag("projector", vProjector.writeToNBT(new NBTTagCompound()));
			tagCompound.setInteger("beamFrequency", cache_beamFrequency);
			if (cache_itemStackCamouflage != null) {
				tagCompound.setTag("camouflage", cache_itemStackCamouflage.writeToNBT(new NBTTagCompound()));
			}
		}
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -1, tagCompound);
	}
	
	@Override
	public void onDataPacket(NetworkManager networkManager, S35PacketUpdateTileEntity packet) {
		NBTTagCompound tagCompound = packet.func_148857_g();
		readFromNBT(tagCompound);
		// worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); // TODO is it needed?
	}
	
	public void setProjector(final VectorI vectorI) {
		vProjector = vectorI;
		ForceFieldSetup forceFieldSetup = getForceFieldSetup();
		if (forceFieldSetup != null) {
			cache_beamFrequency = forceFieldSetup.beamFrequency;
			cache_itemStackCamouflage = forceFieldSetup.itemStackCamouflage;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public TileEntityProjector getProjector() {
		if (vProjector != null) {
			TileEntity tileEntity = vProjector.getTileEntity(worldObj);
			if (tileEntity instanceof TileEntityProjector) {
				TileEntityProjector tileEntityProjector = (TileEntityProjector) tileEntity;
				if (worldObj.isRemote || tileEntityProjector.isPartOfForceField(new VectorI(this))) {
					return tileEntityProjector;
				}
			}
		}
		
		if (!worldObj.isRemote) {
			gracePeriod_calls--;
			if (gracePeriod_calls < 0) {
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			}
		}
		
		return null;
	}
	
	public ForceFieldSetup getForceFieldSetup() {
		TileEntityProjector tileEntityProjector = getProjector();
		if (tileEntityProjector == null) {
			return null;
		}
		return tileEntityProjector.getForceFieldSetup();
	}
}
