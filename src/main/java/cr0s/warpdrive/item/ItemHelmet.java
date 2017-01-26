package cr0s.warpdrive.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IAirCanister;
import cr0s.warpdrive.api.IBreathingHelmet;
import cr0s.warpdrive.config.WarpDriveConfig;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

public class ItemHelmet extends ItemArmor implements IBreathingHelmet {
	
	public ItemHelmet(final String registryName, ArmorMaterial mat, EntityEquipmentSlot entityEquipmentSlot) {
		super(mat, 0, entityEquipmentSlot);
		setUnlocalizedName("warpdrive.armor.Helmet");
		setRegistryName(registryName);
		setCreativeTab(WarpDrive.creativeTabWarpDrive);
		GameRegistry.register(this);
	}
	
	@Nonnull
	@Override
	public String getArmorTexture(ItemStack itemStack, Entity entity, EntityEquipmentSlot slot, String type) {
		return "warpdrive:textures/armor/warpArmor_1.png";
	}
	
	@Override
	public boolean canBreath(Entity player) {
		return true;
	}
	
	@Override
	public boolean removeAir(Entity entity) {
		if (WarpDriveConfig.LOGGING_BREATHING) {
			WarpDrive.logger.info("Checking breathing!");
		}
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP entityPlayer = (EntityPlayerMP) entity;
			ItemStack[] playerInventory = entityPlayer.inventory.mainInventory;
			int slotAirCanisterFound = -1;
			float fillingRatioAirCanisterFound = 0.0F;
			
			// find most consumed air canister with smallest stack
			for (int slotIndex = 0; slotIndex < playerInventory.length; slotIndex++) {
				ItemStack itemStack = playerInventory[slotIndex];
				if (itemStack != null && itemStack.getItem() instanceof IAirCanister) {
					IAirCanister airCanister = (IAirCanister) itemStack.getItem();
					if (airCanister.containsAir(itemStack)) {
						float fillingRatio = 1.0F - itemStack.getItemDamage() / (float)itemStack.getMaxDamage();
						fillingRatio -= itemStack.stackSize / 1000;
						if (fillingRatioAirCanisterFound <= 0.0F || fillingRatio < fillingRatioAirCanisterFound) {
							slotAirCanisterFound = slotIndex;
							fillingRatioAirCanisterFound = fillingRatio;
						}
					}
				}
			}
			// consume air on the selected Air canister
			if (slotAirCanisterFound >= 0) {
				ItemStack itemStack = playerInventory[slotAirCanisterFound];
				if (itemStack != null && itemStack.getItem() instanceof IAirCanister) {
					IAirCanister airCanister = (IAirCanister) itemStack.getItem();
					if (airCanister.containsAir(itemStack)) {
						if (itemStack.stackSize > 1) {// unstack
							itemStack.stackSize--;
							ItemStack toAdd = itemStack.copy();
							toAdd.stackSize = 1;
							toAdd.setItemDamage(itemStack.getItemDamage() + 1); // bypass unbreaking enchantment
							if (itemStack.getItemDamage() >= itemStack.getMaxDamage()) {
								toAdd = airCanister.emptyDrop(itemStack);
							}
							if (!entityPlayer.inventory.addItemStackToInventory(toAdd)) {
								EntityItem entityItem = new EntityItem(entityPlayer.worldObj, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, toAdd);
								entityPlayer.worldObj.spawnEntityInWorld(entityItem);
							}
							entityPlayer.sendContainerToPlayer(entityPlayer.inventoryContainer);
						} else {
							itemStack.setItemDamage(itemStack.getItemDamage() + 1); // bypass unbreaking enchantment
							if (itemStack.getItemDamage() >= itemStack.getMaxDamage()) {
								playerInventory[slotAirCanisterFound] = airCanister.emptyDrop(itemStack);
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public int ticksPerCanDamage() {
		return 300;
	}
}
