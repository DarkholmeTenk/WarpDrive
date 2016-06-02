package cr0s.warpdrive;

import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class GravityManager {
	private static final double OVERWORLD_ENTITY_GRAVITY = 0.080000000000000002D;	// Default value from Vanilla
	private static final double OVERWORLD_ITEM_GRAVITY = 0.039999999105930328D;	// Default value from Vanilla
	private static final double OVERWORLD_ITEM_GRAVITY2 = 0.9800000190734863D;	// Default value from Vanilla
	private static final double HYPERSPACE_FIELD_ENTITY_GRAVITY = 0.035D;
	private static final double HYPERSPACE_VOID_ENTITY_JITTER = 0.005D;
	private static final double SPACE_FIELD_ENTITY_GRAVITY = 0.025D;
	private static final double SPACE_FIELD_ITEM_GRAVITY = 0.02D;
	private static final double SPACE_FIELD_ITEM_GRAVITY2 = 0.60D;
	private static final double SPACE_VOID_GRAVITY = 0.001D;
	private static final double SPACE_VOID_GRAVITY_JETPACK_SNEAK = 0.02D;
	private static final double SPACE_VOID_GRAVITY_RAW_SNEAK = 0.005D; // 0.001 = no mvt
	
	public static double getGravityForEntity(Entity entity) {
		// Is entity in space or hyper-space?
		boolean inSpace = entity.worldObj.provider.dimensionId == WarpDriveConfig.G_SPACE_DIMENSION_ID;
		boolean inHyperspace = entity.worldObj.provider.dimensionId == WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID;
		
		if (inSpace || inHyperspace) {
			boolean insideGravField = isEntityInGraviField(entity);
			
			if (insideGravField) {
				if (inSpace) {
					return SPACE_FIELD_ENTITY_GRAVITY;
				} else {
					return HYPERSPACE_FIELD_ENTITY_GRAVITY;
				}
			} else {
				double jitter = (entity.worldObj.rand.nextDouble() - 0.5D) * 2.0D * HYPERSPACE_VOID_ENTITY_JITTER;
				if (inSpace)
					jitter = 0.0D;
				if (entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					
					if (player.isSneaking()) {
						for (int i = 0; i < 4; i++) {
							ItemStack armor = player.getCurrentArmor(i);
							if (armor != null) {
								if (Dictionary.ITEMS_FLYINSPACE.contains(armor.getItem())) {
									return SPACE_VOID_GRAVITY_JETPACK_SNEAK;
								}
							}
						}
						return SPACE_VOID_GRAVITY_RAW_SNEAK;
					} else {
						// FIXME: compensate jetpack
					}
				}
				
				return SPACE_VOID_GRAVITY + jitter;
			}
		}
		
		return OVERWORLD_ENTITY_GRAVITY;
	}
	
	public static double getItemGravity(EntityItem entity) {
		if ( entity.worldObj.provider.dimensionId == WarpDriveConfig.G_SPACE_DIMENSION_ID
		  || entity.worldObj.provider.dimensionId == WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID) {
			if (isEntityInGraviField(entity)) {
				return SPACE_FIELD_ITEM_GRAVITY;
			} else {
				return SPACE_VOID_GRAVITY;
			}
		} else {
			return OVERWORLD_ITEM_GRAVITY; // On Earth
		}
	}
	
	public static double getItemGravity2(EntityItem entity) {
		if ( entity.worldObj.provider.dimensionId == WarpDriveConfig.G_SPACE_DIMENSION_ID
		  || entity.worldObj.provider.dimensionId == WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID) {
			if (isEntityInGraviField(entity)) {
				return SPACE_FIELD_ITEM_GRAVITY2;
			} else {
				return SPACE_VOID_GRAVITY;
			}
		} else {
			return OVERWORLD_ITEM_GRAVITY2;
		}
	}
	
	public static boolean isEntityInGraviField(Entity entity) {
		int y = MathHelper.floor_double(entity.posY);
		int x = MathHelper.floor_double(entity.posX);
		int z = MathHelper.floor_double(entity.posZ);
		final int CHECK_DISTANCE = 20;
		
		// Search non-air blocks under player
		for (int ny = y; ny > (y - CHECK_DISTANCE); ny--) {
			if (!entity.worldObj.isAirBlock(x, ny, z)) {
				return true;
			}
		}
		
		return false;
	}
}
