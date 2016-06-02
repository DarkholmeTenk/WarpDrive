package cr0s.warpdrive.block.forcefield;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IShapeProvider;
import cr0s.warpdrive.config.*;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.data.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by LemADEC on 16/05/2016.
 */
public class TileEntityProjector extends TileEntityAbstractForceField {
	private static final int PROJECTOR_MAX_ENERGY_STORED = 100000;
	private static final int PROJECTOR_COOLDOWN_TICKS = 200;
	private static final int PROJECTOR_PROJECTION_UPDATE_TICKS = 8;
	private static final int PROJECTOR_SOUND_UPDATE_TICKS = Integer.MAX_VALUE; // TODO
	private static final int PROJECTOR_SCAN_MAX_BLOCKS_PER_UPDATE = 200;
	private static final int PROJECTOR_PLACE_MAX_BLOCKS_PER_UPDATE = 100;
	
	// persistent properties
	public boolean isDoubleSided = true;
	private byte tier = -1;
	private EnumForceFieldShape shape = EnumForceFieldShape.NONE;
	
	// computed properties
	private int cooldownTicks;
	private int updateTicks;
	protected boolean isPowered = true;
	private final boolean enableSound = false;
	
	// allow only one computation at a time
	private static final AtomicBoolean isGlobalThreadRunning = new AtomicBoolean(false);
	// computation is ongoing for this specific tile
	private final AtomicBoolean isThreadRunning = new AtomicBoolean(false);
	// parameters have changed, new computation is required
	private final AtomicBoolean isDirty = new AtomicBoolean(true);
	
	private Set<VectorI> calculated_interiorField = null;
	private Set<VectorI> calculated_forceField = null;
	private Iterator<VectorI> iteratorForcefield = null;
	
	// currently placed forcefields
	private final Set<VectorI> vForceFields = new HashSet<>();
	
	public TileEntityProjector() {
		super();
		
		peripheralName = "warpdriveForceFieldProjector";
		addMethods(new String[] {
			"status"    // isConnected, isPowered, shape
		});
	}
	
	@Override
	protected void onFirstUpdateTick() {
		super.onFirstUpdateTick();
		tier = ((BlockProjector) getBlockType()).tier;
		cooldownTicks = worldObj.rand.nextInt(PROJECTOR_PROJECTION_UPDATE_TICKS);
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		// Frequency is not set
		if (!isConnected) {
			return;
		}
		
		// Powered ?
		int energyRequired;
		energyRequired = getEnergyRequired();
		isPowered = getEnergyStored() >= energyRequired;
		
		boolean isEnabledAndValid = isEnabled && isValid();
		boolean isOn = isEnabledAndValid && cooldownTicks <= 0 && isPowered;
		if (isOn) {
			consumeEnergy(energyRequired, false);
			cooldownTicks = 0;
			
			updateTicks++;
			if (!worldObj.isRemote) {
				if (updateTicks > PROJECTOR_PROJECTION_UPDATE_TICKS) {
					updateTicks = 0;
					if (!isCalculated()) {
						calculateForceField();
					} else {
						projectForceField();
					}
				}
			} else {
				// TODO add some animation
				if (updateTicks > PROJECTOR_SOUND_UPDATE_TICKS) {
					if (enableSound) {
						worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "projecting", 1.0F, 0.85F + 0.15F * worldObj.rand.nextFloat());
					}
				}
			}
			
		} else if (!worldObj.isRemote) {
			destroyForceField(false);
			if (cooldownTicks > 0) {
				cooldownTicks--;
			} else if (isEnabledAndValid) {
				cooldownTicks = PROJECTOR_COOLDOWN_TICKS;
				String msg = "We're running out of power captain, reduce our consumption or get that scottish engineer to boost our power!";
				
				AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(xCoord - 10, yCoord - 10, zCoord - 10, xCoord + 10, yCoord + 10, zCoord + 10);
				List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);
				
				System.out.println(this + " messageToPlayersNearby: " + msg);
				for (Entity entity : list) {
					if (entity == null || (!(entity instanceof EntityPlayer)) || entity instanceof FakePlayer) {
						continue;
					}
					
					WarpDrive.addChatMessage((EntityPlayer) entity, "[Projector] " + msg);
				}
			}
		}
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		destroyForceField(true);
	}
	
	private int getEnergyRequired() {
		return 6 + 2 * tier;
	}
	
	public boolean isValid() {
		return getShape() != EnumForceFieldShape.NONE;
	}
	
	public boolean isCalculated() {
		return !isDirty.get() && !isThreadRunning.get();
	}
	
	private void calculateForceField() {
		if ((!worldObj.isRemote) && isValid()) {
			if (!isGlobalThreadRunning.getAndSet(true)) {
				isThreadRunning.set(true);
				isDirty.set(false);
				iteratorForcefield = null;
				calculated_interiorField = null;
				calculated_forceField = null;
				vForceFields.clear();
				
				new ThreadCalculation(this).start();
			}
		}
	}
	
	private void calculation_done(Set<VectorI> interiorField, Set<VectorI> forceField) {
		if (interiorField == null || forceField == null) {
			calculated_interiorField = new HashSet<>(0);
			calculated_forceField = new HashSet<>(0);
		} else {
			calculated_interiorField = interiorField;
			calculated_forceField = forceField;
		}
		isThreadRunning.set(false);
		isGlobalThreadRunning.set(false);
	}
	
	public boolean isPartOfForceField(VectorI vector) {
		if (!isEnabled || !isValid()) {
			return false;
		}
		if (!isCalculated()) {
			return true;
		}
		// only consider the forcefield itself
		return calculated_forceField.contains(vector);
	}
	
	private Set<VectorI> getInteriorPoints() {
		if (!isCalculated()) {
			throw new ConcurrentModificationException("Calculation ongoing...");
		}
		return calculated_interiorField;
	}
	
	private void projectForceField() {
		if (worldObj.isRemote || (!isCalculated())) {
			return;
		}
		
		ForceFieldSetup forceFieldSetup = getForceFieldSetup();
		int scanCount = 0;
		int scanSpeed = Math.min(calculated_forceField.size(), PROJECTOR_SCAN_MAX_BLOCKS_PER_UPDATE);
		int constructionCount = 0;
		int constructionSpeed = Math.min(forceFieldSetup.getProjectionSpeed(), PROJECTOR_PLACE_MAX_BLOCKS_PER_UPDATE);
		
		boolean hasDisintegration = forceFieldSetup.disintegrationLevel > 0;
		
		Set<TileEntityProjector> projectors = new HashSet<>();
		if (forceFieldSetup.hasFusion) {
			for (TileEntity tileEntity : ForceFieldRegistry.getTileEntities(getBeamFrequency())) {
				if ( (tileEntity instanceof TileEntityProjector)
					&& (tileEntity != this)
					&& (((TileEntityProjector) tileEntity).worldObj == worldObj)
					&& (((TileEntityProjector) tileEntity).isEnabled)
					&& (((TileEntityProjector) tileEntity).isValid())
					&& (((TileEntityProjector) tileEntity).isCalculated())) {
					projectors.add((TileEntityProjector) tileEntity);
				}
			}
		}
		
		VectorI vector;
		Block block;
		boolean noFusion;
		
		while (scanCount < scanSpeed && constructionCount < constructionSpeed) {
			if (iteratorForcefield == null || !iteratorForcefield.hasNext()) {
				iteratorForcefield = calculated_forceField.iterator();
			}
			scanCount++;
			
			vector = iteratorForcefield.next();
			
			if (!worldObj.blockExists(vector.x, vector.y, vector.z) || !worldObj.getChunkFromBlockCoords(vector.x, vector.z).isChunkLoaded) {
				continue;
			}
			
			block = vector.getBlock(worldObj);
			noFusion = true;
			
			if (forceFieldSetup.hasFusion) {
				for (TileEntityProjector projector : projectors) {
					if (projector.getInteriorPoints().contains(vector)) {
						noFusion = false;
						break;
					}
				}
			}
			
			if (noFusion) {
				// TODO: check area protection
				// MFR laser is unbreakable and replaceable
				// Liquid, vine and snow are replaceable
				if ( (block == null) || (block == Blocks.tallgrass) || (block == Blocks.deadbush)
					|| Dictionary.BLOCKS_EXPANDABLE.contains(block)
					|| (hasDisintegration && (block.getBlockHardness(worldObj, vector.x, vector.y, vector.z) != -1.0F))
					|| (!hasDisintegration && block.isReplaceable(worldObj, vector.x, vector.y, vector.z))) {
					
					if ((block != WarpDrive.blockForceField) && (!vector.equals(this))) {
						float energyConsumed = 0;
						// if (forceFieldSetup.disintegrationLevel > 0) {
							// TODO break the block
							// TODO store result in chest or drop it?
						// } else if (forceFieldSetup.hasStabilize) {
							// TODO collect from chest
							// TODO place block (ItemBlock.place?)
						// } else if (forceFieldSetup.hasPump) {
							
						// } else if (forceFieldSetup.temperatureLevel != 0 && forceFieldSetup.hasWorldInteraction) {
							// TODO glass <> sandstone <> sand <> gravel <> cobblestone <> stone <> obsidian
							// TODO ice <> snow <> water <> air > fire
							// TODO obsidian < lava
						// } else {
							energyConsumed = 1; // TODO
							worldObj.setBlock(vector.x, vector.y, vector.z, WarpDrive.blockForceField, 0, 2);
							
							TileEntity tileEntity = worldObj.getTileEntity(vector.x, vector.y, vector.z);
							if (tileEntity instanceof TileEntityForceField) {
								((TileEntityForceField) tileEntity).setProjector(new VectorI(this));
							}
							
							vForceFields.add(vector);
						// }
						if (energyConsumed > 0) {
							constructionCount++;
							consumeEnergy(Math.round(energyConsumed), false);
						} else {
							consumeEnergy(Math.round(1), false); // TODO
						}
					}
				} else if (block == WarpDrive.blockForceField && !vForceFields.contains(vector)) {
					TileEntity tileEntity = worldObj.getTileEntity(vector.x, vector.y, vector.z);
					if (tileEntity instanceof TileEntityForceField && (((TileEntityForceField) tileEntity).getProjector() == this)) {
						vForceFields.add(vector);
					}
				}
			} else {
				if (block == WarpDrive.blockForceField) {
					if (((BlockForceField) block).getProjector(worldObj, vector.x, vector.y, vector.z) == this) {
						worldObj.setBlockToAir(vector.x, vector.y, vector.z);
					}
				}
			}
		}
	}
	
	private void destroyForceField(boolean isChunkLoading) {
		if ((!worldObj.isRemote) && (!vForceFields.isEmpty())) {
			for (Iterator<VectorI> iterator = vForceFields.iterator(); iterator.hasNext();) {
				VectorI vector = iterator.next();
				if (!isChunkLoading) {
					if (!(worldObj.blockExists(vector.x, vector.y, vector.z))) {// chunk is not loaded, skip it
						continue;
					}
					if (!worldObj.getChunkFromBlockCoords(vector.x, vector.z).isChunkLoaded) {// chunk is unloading, skip it
						continue;
					}
				}
				Block block = vector.getBlock(worldObj);
				
				if (block == WarpDrive.blockForceField) {
					worldObj.setBlockToAir(vector.x, vector.y, vector.z);
				}
				iterator.remove();
			}
		}
		
		if ((!worldObj.isRemote) && isCalculated() && isChunkLoading) {
			for (VectorI vector : calculated_forceField) {
				Block block = vector.getBlock(worldObj);
				
				if (block == WarpDrive.blockForceField) {
					TileEntity tileEntity = worldObj.getTileEntity(vector.x, vector.y, vector.z);
					if (tileEntity instanceof TileEntityForceField && (((TileEntityForceField) tileEntity).getProjector() == this)) {
						worldObj.setBlockToAir(vector.x, vector.y, vector.z);
					}
				}
			}
		}
	}
	
	public IShapeProvider getShapeProvider() {
		return WarpDrive.itemForceFieldShape;
	}
	
	@Override
	public int getBeamFrequency() {
		return beamFrequency;
	}
	
	@Override
	public void setBeamFrequency(final int parBeamFrequency) {
		super.setBeamFrequency(parBeamFrequency);
		isDirty.set(true);
		if (worldObj != null) {
			destroyForceField(false);
		}
	}
	
	public String getStatus() {
		return StatCollector.translateToLocalFormatted("warpdrive.guide.prefix",
			getBlockType().getLocalizedName())
			+ getBeamFrequencyStatus();
		// TODO add energy info
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		isDoubleSided = tag.getBoolean("isDoubleSided");
		tier = tag.getByte("tier");
		setShape(EnumForceFieldShape.get(tag.getByte("shape")));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("isDoubleSided", isDoubleSided);
		tag.setByte("tier", tier);
		tag.setByte("shape", (byte) getShape().ordinal());
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		tagCompound.setBoolean("isPowered", isPowered);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tagCompound);
	}
	
	@Override
	public void onDataPacket(NetworkManager networkManager, S35PacketUpdateTileEntity packet) {
		NBTTagCompound tagCompound = packet.func_148857_g();
		readFromNBT(tagCompound);
		isPowered = tagCompound.getBoolean("isPowered");
	}
	
	public ForceFieldSetup getForceFieldSetup() {
		return new ForceFieldSetup(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, tier, beamFrequency);
	}
	
	@Override
	public int getMaxEnergyStored() {
		return PROJECTOR_MAX_ENERGY_STORED;
	}
	
	@Override
	public boolean canInputEnergy(ForgeDirection from) {
		return true;
	}
	
	public EnumForceFieldShape getShape() {
		return shape;
	}
	
	public void setShape(EnumForceFieldShape shape) {
		this.shape = shape;
		isDirty.set(true);
		if (worldObj != null) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			destroyForceField(false);
		}
	}
	
	private class ThreadCalculation extends Thread {
		private final TileEntityProjector projector;
		
		public ThreadCalculation(TileEntityProjector projector) {
			this.projector = projector;
		}
		
		@Override
		public void run() {
			Set<VectorI> vPerimeterBlocks = null;
			Set<VectorI> vInteriorBlocks = null;
			
			// calculation start is done synchronously, by caller
			try {
				if (projector.isValid()) {
					ForceFieldSetup forceFieldSetup = projector.getForceFieldSetup();
					if (WarpDriveConfig.LOGGING_FORCEFIELD) {
						WarpDrive.logger.info("Calculation started for " + projector);
					}
					
					// create HashSets
					VectorI vScale = forceFieldSetup.vMax.clone().subtract(forceFieldSetup.vMin);
					vInteriorBlocks = new HashSet<>(vScale.x * vScale.y * vScale.z);
					vPerimeterBlocks = new HashSet<>(2 * vScale.x * vScale.y + 2 * vScale.x * vScale.z + 2 * vScale.y * vScale.z);
					
					// compute interior fields to remove overlapping parts
					for (Map.Entry<VectorI, Boolean> entry : forceFieldSetup.shapeProvider.getVertexes(forceFieldSetup).entrySet()) {
						VectorI vPosition = entry.getKey();
						if (forceFieldSetup.isDoubleSided || vPosition.y >= yCoord) {
							if ((forceFieldSetup.rotationYaw != 0) || (forceFieldSetup.rotationPitch != 0)) {
								vPosition.rotateByAngle(forceFieldSetup.rotationYaw, forceFieldSetup.rotationPitch);
							}
							
							vPosition.translate(forceFieldSetup.vTranslation);
							
							if (vPosition.y > 0 && vPosition.y <= projector.worldObj.getHeight()) {
								if (entry.getValue()) {
									vPerimeterBlocks.add(vPosition);
								} else {
									vInteriorBlocks.add(vPosition);
								}
							}
						}
					}
					
					// compute forcefield itself
					if (forceFieldSetup.isInverted) {
						// inverted mode => same as interior before fusion => need to be fully cloned
						vPerimeterBlocks = new HashSet<>(vInteriorBlocks);
					}
					
					if (WarpDriveConfig.LOGGING_FORCEFIELD) {
						WarpDrive.logger.info(projector + " Calculation done: "
							+ vInteriorBlocks.size() + " blocks inside, including " + vPerimeterBlocks.size() + " blocks to place");
					}
				} else {
					if (WarpDriveConfig.LOGGING_FORCEFIELD) {
						WarpDrive.logger.info(projector + " Calculation aborted");
					}
				}
			} catch (Exception exception) {
				vInteriorBlocks = null;
				vPerimeterBlocks = null;
				exception.printStackTrace();
				WarpDrive.logger.error(projector + " Calculation failed");
			}
			
			projector.calculation_done(vInteriorBlocks, vPerimeterBlocks);
		}
	}
}
