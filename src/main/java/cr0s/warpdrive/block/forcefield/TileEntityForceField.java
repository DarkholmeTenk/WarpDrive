package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.TileEntityAbstractBase;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.ForceFieldSetup;
import cr0s.warpdrive.data.VectorI;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

public class TileEntityForceField extends TileEntityAbstractBase {
	private VectorI vProjector;

	// cache parameters used for rendering, force projector check for others
	private int cache_beamFrequency;
	public IBlockState cache_blockStateCamouflage;
	protected int cache_colorMultiplierCamouflage;
	protected int cache_lightCamouflage;
	
	// number of projectors check ignored before self-destruction
	private int gracePeriod_calls = 3;
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("projector")) {
			vProjector = VectorI.readFromNBT(tag.getCompoundTag("projector"));
			cache_beamFrequency = tag.getInteger("beamFrequency");
			if (tag.hasKey("projector")) {
				try {
					cache_blockStateCamouflage = Block.getBlockFromName(tag.getString("camouflageBlock")).getStateFromMeta(tag.getByte("camouflageMeta"));
					cache_colorMultiplierCamouflage = tag.getInteger("camouflageColorMultiplier");
					cache_lightCamouflage = tag.getByte("camouflageLight");
					if (Dictionary.BLOCKS_NOCAMOUFLAGE.contains(cache_blockStateCamouflage.getBlock())) {
						cache_blockStateCamouflage = null;
						cache_colorMultiplierCamouflage = 0;
						cache_lightCamouflage = 0;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			} else {
				cache_blockStateCamouflage = null;
				cache_colorMultiplierCamouflage = 0;
				cache_lightCamouflage = 0;
			}
		} else {
			vProjector = null;
			cache_beamFrequency = -1;
			cache_blockStateCamouflage = null;
			cache_colorMultiplierCamouflage = 0;
			cache_lightCamouflage = 0;
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		tagCompound = super.writeToNBT(tagCompound);
		if (vProjector != null) {
			tagCompound.setTag("projector", vProjector.writeToNBT(new NBTTagCompound()));
			tagCompound.setInteger("beamFrequency", cache_beamFrequency);
			if (cache_blockStateCamouflage != null) {
				tagCompound.setString("camouflageBlock", cache_blockStateCamouflage.getBlock().getRegistryName().toString());
				tagCompound.setByte("camouflageMeta", (byte)cache_blockStateCamouflage.getBlock().getMetaFromState(cache_blockStateCamouflage));
				tagCompound.setInteger("camouflageColorMultiplier", cache_colorMultiplierCamouflage);
				tagCompound.setByte("camouflageLight", (byte)cache_lightCamouflage);
			}
		}
		return tagCompound;
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
	}
	
	public void setProjector(final VectorI vectorI) {
		vProjector = vectorI;
		ForceFieldSetup forceFieldSetup = getForceFieldSetup();
		if (forceFieldSetup != null) {
			cache_beamFrequency = forceFieldSetup.beamFrequency;
			if (getBlockMetadata() == forceFieldSetup.getCamouflageBlockState().getBlock().getMetaFromState(forceFieldSetup.getCamouflageBlockState())) {
				cache_blockStateCamouflage = forceFieldSetup.getCamouflageBlockState();
				cache_colorMultiplierCamouflage = forceFieldSetup.getCamouflageColorMultiplier();
				cache_lightCamouflage = forceFieldSetup.getCamouflageLight();
			}
		}
		IBlockState blockState = worldObj.getBlockState(pos);
		worldObj.notifyBlockUpdate(pos, blockState, blockState, 3);
	}
	
	public TileEntityForceFieldProjector getProjector() {
		if (vProjector != null) {
			TileEntity tileEntity = vProjector.getTileEntity(worldObj);
			if (tileEntity instanceof TileEntityForceFieldProjector) {
				TileEntityForceFieldProjector tileEntityForceFieldProjector = (TileEntityForceFieldProjector) tileEntity;
				if (worldObj.isRemote) {
					return tileEntityForceFieldProjector;
					
				} else if (tileEntityForceFieldProjector.isPartOfForceField(new VectorI(this))) {
					if (tileEntityForceFieldProjector.isOn()) {
						return tileEntityForceFieldProjector;
					} else {
						// projector is disabled or out of power
						worldObj.setBlockToAir(pos);
						if (WarpDriveConfig.LOGGING_FORCEFIELD) {
							WarpDrive.logger.info("Removed a force field from an offline projector at "
							    + (worldObj == null ? "~NULL~" : worldObj.getWorldInfo().getWorldName()) + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
						}
					}
				}
			}
		}
		
		if (!worldObj.isRemote) {
			gracePeriod_calls--;
			if (gracePeriod_calls < 0) {
				worldObj.setBlockToAir(pos);
				if (WarpDriveConfig.LOGGING_FORCEFIELD) {
					WarpDrive.logger.info("Removed a force field with no projector defined at "
						+ (worldObj == null ? "~NULL~" : worldObj.getWorldInfo().getWorldName()) + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
				}
			}
		}
		
		return null;
	}
	
	public ForceFieldSetup getForceFieldSetup() {
		TileEntityForceFieldProjector tileEntityForceFieldProjector = getProjector();
		if (tileEntityForceFieldProjector == null) {
			return null;
		}
		return tileEntityForceFieldProjector.getForceFieldSetup();
	}
}
