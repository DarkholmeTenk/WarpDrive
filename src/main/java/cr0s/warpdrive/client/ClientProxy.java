package cr0s.warpdrive.client;

import cr0s.warpdrive.CommonProxy;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.api.IItemBase;
import cr0s.warpdrive.render.MyCustomModelLoader;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void onForgePreInitialisation() {
		super.onForgePreInitialisation();
		
		ModelLoaderRegistry.registerLoader(MyCustomModelLoader.INSTANCE);
		
		// blocks
		// IModelInitialisation(WarpDrive.blockShipCore);
		// IModelInitialisation(WarpDrive.blockShipController);
		IModelInitialisation(WarpDrive.blockRadar);
		IModelInitialisation(WarpDrive.blockWarpIsolation);
		IModelInitialisation(WarpDrive.blockAirGenerator);
		IModelInitialisation(WarpDrive.blockLaser);
		IModelInitialisation(WarpDrive.blockLaserCamera);
		IModelInitialisation(WarpDrive.blockWeaponController);
		IModelInitialisation(WarpDrive.blockCamera);
		IModelInitialisation(WarpDrive.blockMonitor);
		IModelInitialisation(WarpDrive.blockLaserMedium);
		IModelInitialisation(WarpDrive.blockMiningLaser);
		IModelInitialisation(WarpDrive.blockLaserTreeFarm);
		IModelInitialisation(WarpDrive.blockLift);
		IModelInitialisation(WarpDrive.blockShipScanner);
		IModelInitialisation(WarpDrive.blockCloakingCore);
		IModelInitialisation(WarpDrive.blockCloakingCoil);
		IModelInitialisation(WarpDrive.blockTransporter);
		// if (WarpDriveConfig.isIndustrialCraft2Loaded) {
		// 	IModelInitialisation(WarpDrive.blockIC2reactorLaserMonitor);
		// }
		// IModelInitialisation(WarpDrive.blockEnanReactorCore);
		// IModelInitialisation(WarpDrive.blockEnanReactorLaser);
		// IModelInitialisation(WarpDrive.blockEnergyBank);
		IModelInitialisation(WarpDrive.blockAir);
		IModelInitialisation(WarpDrive.blockGas);
		IModelInitialisation(WarpDrive.blockIridium);
		IModelInitialisation(WarpDrive.blockLamp_bubble);
		IModelInitialisation(WarpDrive.blockLamp_flat);
		IModelInitialisation(WarpDrive.blockLamp_long);
		IModelInitialisation(WarpDrive.blockHighlyAdvancedMachine);
		IModelInitialisation(WarpDrive.blockTransportBeacon);
		IModelInitialisation(WarpDrive.blockChunkLoader);
		for (int index = 0; index < 3; index++) {
			IModelInitialisation(WarpDrive.blockForceFields[index]);
			IModelInitialisation(WarpDrive.blockForceFieldProjectors[index]);
			IModelInitialisation(WarpDrive.blockForceFieldRelays[index]);
		}
		
		IModelInitialisation(WarpDrive.blockDecorative);
		
		for (int index = 0; index < 3; index++) {
			IModelInitialisation(WarpDrive.blockHulls_plain[index]);
			IModelInitialisation(WarpDrive.blockHulls_glass[index]);
			for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
				IModelInitialisation(WarpDrive.blockHulls_stairs[index][enumDyeColor.getMetadata()]);
				// IModelInitialisation(WarpDrive.blockHulls_slab[index][enumDyeColor.getMetadata()]);
			}
		}
		
		// items
		if (WarpDriveConfig.isIndustrialCraft2Loaded) {
			IModelInitialisation(WarpDrive.itemIC2reactorLaserFocus);
		}
		IModelInitialisation(WarpDrive.itemComponent);
		IModelInitialisation(WarpDrive.itemCrystalToken);
		if (WarpDriveConfig.RECIPES_ENABLE_VANILLA) {
			IModelInitialisation(WarpDrive.itemUpgrade);
		}
		IModelInitialisation(WarpDrive.itemTuningRod);
		IModelInitialisation(WarpDrive.itemForceFieldShape);
		IModelInitialisation(WarpDrive.itemForceFieldUpgrade);
		IModelInitialisation(WarpDrive.itemHelmet);
		IModelInitialisation(WarpDrive.itemAirCanisterFull);
		
		// entities
		// RenderingRegistry.registerEntityRenderingHandler(EntityXXX.class, RenderXXX::new);
		
		// MinecraftForge.EVENT_BUS.register(new WarpDriveKeyBindings());
		// MinecraftForge.EVENT_BUS.register(new RenderGameOverlay();
	}
	
	private static void IModelInitialisation(Object object) {
		if (object instanceof IBlockBase) {
			((IBlockBase) object).modelInitialisation();
		} else if (object instanceof Block) {
			Item item = Item.getItemFromBlock((Block) object);
			modelInitialisation(item);
		} else if (object instanceof Item) {
			modelInitialisation((Item) object);
		} else if (object == null) {
			WarpDrive.logger.info("Ignoring null object ModelInitialisation...");
		} else {
			throw new RuntimeException("Invalid object " + object);
		}
	}
	
	@Nonnull
	public static ModelResourceLocation getModelResourceLocation(final ItemStack itemStack) {
		Item item = itemStack.getItem();
		ResourceLocation resourceLocation = item.getRegistryName();
		if (item.getHasSubtypes()) {
			resourceLocation = new ResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath() + "-" + itemStack.getItemDamage());
		}
		return new ModelResourceLocation(resourceLocation, "inventory");
	}
	
	public static void modelInitialisation(Item item) {
		if (item == null) {
			throw new RuntimeException("Unable to PreInitialize a null item");
		} else if (!item.getHasSubtypes()) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		} else {
			List<ItemStack> listItemStacks = new ArrayList<>(16);
			item.getSubItems(item, item.getCreativeTab(), listItemStacks);
			for (ItemStack itemStack : listItemStacks) {
				ModelResourceLocation modelResourceLocation; 
				if (item instanceof IItemBase) {
					modelResourceLocation = ((IItemBase) item).getModelResourceLocation(itemStack);
				} else {
					modelResourceLocation = getModelResourceLocation(itemStack);
				}
				ModelLoader.setCustomModelResourceLocation(item, itemStack.getMetadata(), modelResourceLocation);
			}
		}
	}
}