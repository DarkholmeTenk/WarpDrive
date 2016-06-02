package cr0s.warpdrive.data;

import cr0s.warpdrive.api.IEffector;
import cr0s.warpdrive.api.IForceFieldUpgrade;
import cr0s.warpdrive.api.IShapeProvider;
import cr0s.warpdrive.block.forcefield.TileEntityProjector;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.*;

public class ForceFieldSetup extends GlobalPosition {
	public int beamFrequency;
	public byte tier;
	public ItemStack itemStackCamouflage;
	private Block blockCamouflage_cache;
	private static final List<Integer> ALLOWED_RENDER_TYPES = Arrays.asList(0, 1, 4, 5, 6, 8, 7, 12, 13, 14, 16, 17, 20, 23, 29, 30, 31, 39);
	private final HashMap<String, Integer> mapUpgradeValues = new HashMap<>(EnumForceFieldUpgrade.length);
	private final HashMap<String, IForceFieldUpgrade> mapUpgradeObjects = new HashMap<>(EnumForceFieldUpgrade.length);
	public boolean hasFusion;
	public boolean isInverted;
	public float startupEnergyCost;
	public float scanEnergyCost;
	public float placeEnergyCost;
	public float scanSpeed = 100000;
	public float placeSpeed = 100000;
	
	// TODO
	public Set<TileEntityProjector> projectors = new HashSet<>();
	public Set<IEffector> effectors = new HashSet<>();
	public int disintegrationLevel;
	public int temperatureLevel;
	public boolean hasStabilize;
	
	// Projector provided properties
	public float rotationYaw;
	public float rotationPitch;
	public VectorI vTranslation = new VectorI(0, 0, 0);
	public VectorI vMin = new VectorI(-8, -8, -8);
	public VectorI vMax = new VectorI(8, 8, 8);
	public IShapeProvider shapeProvider;
	public EnumForceFieldShape enumForceFieldShape;
	public boolean isDoubleSided = true;
	public float thickness = 1.0F;
	
	public ForceFieldSetup(final int dimensionId, final int x, final int y, final int z, final byte tier, final int beamFrequency) {
		super(dimensionId, x, y, z);
		this.tier = tier;
		this.beamFrequency = beamFrequency;
		refresh();
	}
	
	public Collection<IEffector> getEffectors() {
		return effectors;
	}
	
	public boolean isAccessGranted(EntityPlayer entityPlayer, PermissionNode permissionNode) {
		return false; // TODO
	}
	
	public static boolean isValidCamouflage(ItemStack itemStack) {
		if (itemStack != null && itemStack.stackSize > 0 && itemStack.getItem() instanceof ItemBlock) {
			Block block = Block.getBlockFromItem(itemStack.getItem());
			return ALLOWED_RENDER_TYPES.contains(block.getRenderType());
		}
		return false;
	}
	
	public Block getCamouflageBlock() {
		if (blockCamouflage_cache != null) {
			return blockCamouflage_cache;
		}
		if (isValidCamouflage(itemStackCamouflage)) {
			blockCamouflage_cache = Block.getBlockFromItem(itemStackCamouflage.getItem());
			return blockCamouflage_cache;
		}
		return null;
	}
	
	public void refresh() {
		Set<TileEntity> tileEntities = ForceFieldRegistry.getTileEntities(beamFrequency);
		
		for (TileEntity tileEntity : tileEntities) {
			// only consider same dimension
			if (tileEntity.getWorldObj().provider.dimensionId != dimensionId) {
				continue;
			}
			// projectors
			if (tileEntity instanceof TileEntityProjector) {
				TileEntityProjector projector = (TileEntityProjector) tileEntity;
				if (tileEntity.xCoord == x && tileEntity.yCoord == y && tileEntity.zCoord == z) {
					shapeProvider = projector.getShapeProvider();
					enumForceFieldShape = projector.getShape();
					isDoubleSided = projector.isDoubleSided;
					vTranslation = new VectorI(projector);
					// TODO vMin = projector.vMin;
					// TODO vMax = projector.vMax;
					// TODO item upgrades
				} else {
					if ((((TileEntityProjector) tileEntity).isEnabled)
						&& (((TileEntityProjector) tileEntity).isCalculated())
						&& (((TileEntityProjector) tileEntity).isValid())) {
						projectors.add((TileEntityProjector) tileEntity);
					}
				}
			}
			// block upgrades
			if (tileEntity instanceof IForceFieldUpgrade) {
				String upgradeKey = ((IForceFieldUpgrade)tileEntity).getUpgradeKey();
				if (!upgradeKey.isEmpty()) {
					Integer value = mapUpgradeValues.get(upgradeKey);
					if (value == null) {
						value = 0;
						mapUpgradeObjects.put(upgradeKey, (IForceFieldUpgrade)tileEntity);
					}
					mapUpgradeValues.put(upgradeKey, value + ((IForceFieldUpgrade)tileEntity).getUpgradeValue());
				}
			}
		}
		
		// compute results
		hasFusion = mapUpgradeObjects.containsKey(EnumForceFieldUpgrade.FUSION.unlocalizedName);
		isInverted = mapUpgradeObjects.containsKey(EnumForceFieldUpgrade.INVERT.unlocalizedName);
		for (Map.Entry<String, IForceFieldUpgrade> entry : mapUpgradeObjects.entrySet()) {
			startupEnergyCost += entry.getValue().getStartupEnergyCost(entry.getKey(), mapUpgradeValues.get(entry.getKey()));
			scanEnergyCost += entry.getValue().getScanEnergyCost(entry.getKey(), mapUpgradeValues.get(entry.getKey()));
			placeEnergyCost += entry.getValue().getPlaceEnergyCost(entry.getKey(), mapUpgradeValues.get(entry.getKey()));
			scanSpeed = Math.min(scanSpeed, entry.getValue().getMaxScanSpeed(entry.getKey(), mapUpgradeValues.get(entry.getKey())));
			placeSpeed = Math.min(placeSpeed, entry.getValue().getMaxPlaceSpeed(entry.getKey(), mapUpgradeValues.get(entry.getKey())));
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s @ DIM%d (%d %d %d) (%d %d %d) -> (%d %d %d)",
			getClass().getSimpleName(), dimensionId,
			x, y, z,
			vMin.x, vMin.y, vMin.z,
			vMax.x, vMax.y, vMax.z);
	}
	
	public int applyDamages(World world, int x, int y, int z, DamageSource damageSource, int damageParameter, Vector3 damageDirection, int damageLevel) {
		// TODO
		return 0;
	}
	
	public int getProjectionSpeed() {
		return 100; // TODO
	}
}
