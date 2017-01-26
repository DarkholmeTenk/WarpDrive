package cr0s.warpdrive.block.energy;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Optional;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.item.ItemIC2reactorLaserFocus;
import cr0s.warpdrive.network.PacketHandler;

import javax.annotation.Nonnull;

public class TileEntityIC2reactorLaserMonitor extends TileEntityAbstractEnergy {
	private int ticks = WarpDriveConfig.IC2_REACTOR_COOLING_INTERVAL_TICKS;
	private byte activeSides = 0;
	private boolean updateFlag = false;
	private boolean isFirstTick = true;
	
	public TileEntityIC2reactorLaserMonitor() {
		super();
		IC2_sinkTier = 2;
		IC2_sourceTier = 2;
	}
	
	private static final int[] deltaX = {-2, 2, 0, 0, 0, 0};
	private static final int[] deltaY = { 0, 0,-2, 2, 0, 0};
	private static final int[] deltaZ = { 0, 0, 0, 0,-2, 2};
	private static final byte[] deltaSides = { 1, 2, 4, 8, 16, 32 };
	
	protected boolean isSideActive(int side) {
		switch (side) {
		case 4: return (deltaSides[0] & activeSides) != 0;
		case 5: return (deltaSides[1] & activeSides) != 0;
		case 0: return (deltaSides[2] & activeSides) != 0;
		case 1: return (deltaSides[3] & activeSides) != 0;
		case 2: return (deltaSides[4] & activeSides) != 0;
		case 3: return (deltaSides[5] & activeSides) != 0;
		default: return false;
		}
	}
	
	// returns IReactor tile entities
	@Optional.Method(modid = "IC2")
	private Set<IReactor> findReactors() {
		Set<IReactor> output = new HashSet<>();
		
		byte newActiveSides = 0;
		for(int i = 0; i < deltaX.length; i++) {
			TileEntity tileEntity = worldObj.getTileEntity(pos.add(deltaX[i], deltaY[i], deltaZ[i]));
			if (tileEntity == null) {
				continue;
			}
			
			if (tileEntity instanceof IReactor) {
				newActiveSides |= deltaSides[i];
				output.add((IReactor)tileEntity);
				
			} else if (tileEntity instanceof IReactorChamber) {
				IReactor reactor = ((IReactorChamber)tileEntity).getReactorInstance();
				if (reactor == null) {
					continue;
				}
				
				// ignore if we're right next to the reactor
				BlockPos blockPos = reactor.getReactorPos();
				if ( Math.abs(blockPos.getX() - pos.getX()) == 1
				  || Math.abs(blockPos.getY() - pos.getY()) == 1
				  || Math.abs(blockPos.getZ() - pos.getZ()) == 1) {
					continue;
				}
				
				newActiveSides |= deltaSides[i];
				output.add(reactor);
			}
		}
		if (activeSides != newActiveSides) {
			updateFlag = !updateFlag;
			activeSides = newActiveSides;
		}
		return output;
	}
	
	@Optional.Method(modid = "IC2")
	private boolean coolReactor(IReactor reactor) {
		boolean didCoolReactor = false;
		for(int x = 0; x < 9 && !didCoolReactor; x++) {
			for(int y = 0; y < 6 && !didCoolReactor; y++) {
				ItemStack item = reactor.getItemAt(x, y);
				if (item != null) {
					if (item.getItem() instanceof ItemIC2reactorLaserFocus) {
						int heatInLaserFocus = item.getItemDamage();
						int heatRemovable = (int) Math.floor(Math.min(getEnergyStored() / WarpDriveConfig.IC2_REACTOR_ENERGY_PER_HEAT, heatInLaserFocus));
						if (heatRemovable > 0) {
							didCoolReactor = true;
							if (consumeEnergy((int) Math.ceil(heatRemovable * WarpDriveConfig.IC2_REACTOR_ENERGY_PER_HEAT), false)) {
								item.setItemDamage(heatInLaserFocus - heatRemovable);
							}
						}
					}
				}
			}
		}
		return didCoolReactor;
	}
	
	@Override
	@Optional.Method(modid = "IC2")
	public void update() {
		super.update();
		
		if (worldObj.isRemote) {
			return;
		}
		
		if (isFirstTick) {
			isFirstTick = false;
			updateFlag = (getBlockMetadata() & 1) == 0;
			WarpDrive.logger.info("" + this + " isFirstTick " + activeSides + " " + updateFlag);
		}
		
		ticks--;
		if (ticks <= 0)  {
			ticks = WarpDriveConfig.IC2_REACTOR_COOLING_INTERVAL_TICKS;
			Vector3 myPos = new Vector3(this).translate(0.5);
			Set<IReactor> reactors = findReactors();
			setMetadata();
			if (reactors.isEmpty()) {
				return;
			}
			
			for(IReactor reactor : reactors) {
				if (coolReactor(reactor)) {
					PacketHandler.sendBeamPacket(worldObj, myPos, new Vector3(reactor.getReactorPos()).translate(0.5D), 0.0f, 0.8f, 1.0f, 20, 0, 20);
				}
			}
		}
	}
	
	private void setMetadata() {
		int metadata = (updateFlag ? 0 : 1) | (activeSides != 0 ? 2 : 0);
		if (getEnergyStored() >= WarpDriveConfig.IC2_REACTOR_ENERGY_PER_HEAT) {
			metadata |= 8;
		}
		if (getBlockMetadata() != metadata) {
			updateMetadata(metadata);
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setByte("activeSides", activeSides);
		return tag;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		activeSides = tag.getByte("activeSides");
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
	
	@Override
	@Optional.Method(modid = "IC2")
	public ITextComponent getStatus() {
		if (worldObj == null) {
			return super.getStatus();
		}
		
		Set<IReactor> reactors = findReactors();
		if (reactors != null && !reactors.isEmpty()) {
			return super.getStatus()
					.appendSibling(new TextComponentTranslation("warpdrive.IC2reactorLaserMonitor.multipleReactors",
			        reactors.size()));
		} else {
			return super.getStatus()
					.appendSibling(new TextComponentTranslation("warpdrive.IC2reactorLaserMonitor.noReactor"));
		}
	}
	
	@Override
	public int getMaxEnergyStored() {
		return WarpDriveConfig.IC2_REACTOR_MAX_ENERGY_STORED;
	}
	
	@Override
	public boolean canInputEnergy(EnumFacing from) {
		return true;
	}
}
