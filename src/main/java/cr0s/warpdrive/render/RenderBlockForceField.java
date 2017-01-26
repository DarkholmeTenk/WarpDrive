package cr0s.warpdrive.render;

public class RenderBlockForceField {
	
}
/* @TODO MC1.10
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.forcefield.TileEntityForceField;
import cr0s.warpdrive.config.Dictionary;
import net.minecraft.block.*;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

// wrapper to native classes to renderId is non-zero so we don't render faces when player camera is inside the block
public class RenderBlockForceField implements ISimpleBlockRenderingHandler {
	public static int renderId = 0;
	public static RenderBlockForceField instance = new RenderBlockForceField();
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		// this is not supposed to happen
		//noinspection ConstantConditions
		assert(false);
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityForceField)) {
			return false;
		}
		
		int renderType = -1;
		Block blockCamouflage = ((TileEntityForceField)tileEntity).cache_blockCamouflage;
		if (blockCamouflage != null && !Dictionary.BLOCKS_NOCAMOUFLAGE.contains(blockCamouflage)) {
			renderType = blockCamouflage.getRenderType();
		}
		
		if (renderType >= 0) {
			try {
				blockCamouflage.setBlockBoundsBasedOnState(renderer.blockAccess, x, y, z);
				renderer.setRenderBoundsFromBlock(blockCamouflage);
				
				switch (renderType) {
				case 0 : renderer.renderStandardBlock(blockCamouflage, x, y, z); break;
				case 1 : renderer.renderCrossedSquares(blockCamouflage, x, y, z); break;
				case 2 : renderer.renderBlockTorch(blockCamouflage, x, y, z); break;
				case 3 : renderer.renderBlockFire((BlockFire)blockCamouflage, x, y, z); break;
				// case 4 : renderer.renderBlockLiquid(blockCamouflage, x, y, z); break; // not working due to material check of neighbours during computation
				case 5 : renderer.renderBlockRedstoneWire(blockCamouflage, x, y, z); break;
				case 6 : renderer.renderBlockCrops(blockCamouflage, x, y, z); break;
				// case 7 : renderer.renderBlockDoor(blockCamouflage, x, y, z); break; // not working and doesn't make sense
				case 9 : renderer.renderBlockMinecartTrack((BlockRailBase)blockCamouflage, x, y, z); break;
				case 10 : renderer.renderBlockStairs((BlockStairs)blockCamouflage, x, y, z); break;
				case 11 : renderer.renderBlockFence((BlockFence)blockCamouflage, x, y, z); break;
				case 12 : renderer.renderBlockLever(blockCamouflage, x, y, z); break;
				case 13 : renderer.renderBlockCactus(blockCamouflage, x, y, z); break;
				case 14 : renderer.renderBlockBed(blockCamouflage, x, y, z); break;
				case 15 : renderer.renderBlockRepeater((BlockRedstoneRepeater)blockCamouflage, x, y, z); break;
				case 16 : renderer.renderPistonBase(blockCamouflage, x, y, z, false); break;
				case 17 : renderer.renderPistonExtension(blockCamouflage, x, y, z, true); break;
				case 18 : renderer.renderBlockPane((BlockPane)blockCamouflage, x, y, z); break;
				// 19 is stem
				case 20 : renderer.renderBlockVine(blockCamouflage, x, y, z); break;
				case 21 : renderer.renderBlockFenceGate((BlockFenceGate)blockCamouflage, x, y, z); break;
				// 22 is chest
				case 23 : renderer.renderBlockLilyPad(blockCamouflage, x, y, z); break;
				case 24 : renderer.renderBlockCauldron((BlockCauldron)blockCamouflage, x, y, z); break;
				case 25 : renderer.renderBlockBrewingStand((BlockBrewingStand)blockCamouflage, x, y, z); break;
				case 26 : renderer.renderBlockEndPortalFrame((BlockEndPortalFrame)blockCamouflage, x, y, z); break;
				case 27 : renderer.renderBlockDragonEgg((BlockDragonEgg)blockCamouflage, x, y, z); break;
				case 28 : renderer.renderBlockCocoa((BlockCocoa)blockCamouflage, x, y, z); break;
				case 29 : renderer.renderBlockTripWireSource(blockCamouflage, x, y, z); break;
				case 30 : renderer.renderBlockTripWire(blockCamouflage, x, y, z); break;
				case 31 : renderer.renderBlockLog(blockCamouflage, x, y, z); break;
				case 32 : renderer.renderBlockWall((BlockWall)blockCamouflage, x, y, z); break;
				case 33 : renderer.renderBlockFlowerpot((BlockFlowerPot)blockCamouflage, x, y, z); break; // won't render content due to tileEntity access
				case 34 : renderer.renderBlockBeacon((BlockBeacon)blockCamouflage, x, y, z); break;
				case 35 : renderer.renderBlockAnvil((BlockAnvil)blockCamouflage, x, y, z); break;
				case 36 : renderer.renderBlockRedstoneDiode((BlockRedstoneDiode)blockCamouflage, x, y, z); break;
				case 37 : renderer.renderBlockRedstoneComparator((BlockRedstoneComparator)blockCamouflage, x, y, z); break;
				case 38 : renderer.renderBlockHopper((BlockHopper)blockCamouflage, x, y, z); break;
				case 39 : renderer.renderBlockQuartz(blockCamouflage, x, y, z); break;
				// 40 is double plant
				case 41 : renderer.renderBlockStainedGlassPane(blockCamouflage, x, y, z); break;
				default:
					// blacklist the faulty block
					WarpDrive.logger.error("Disabling camouflage with block " + Block.blockRegistry.getNameForObject(blockCamouflage) + " due to invalid renderType " + renderType);
					Dictionary.BLOCKS_NOCAMOUFLAGE.add(blockCamouflage);
					return false;
				}
			} catch(Exception exception) {
				exception.printStackTrace();
				
				// blacklist the faulty block
				WarpDrive.logger.error("Disabling camouflage block " + Block.blockRegistry.getNameForObject(blockCamouflage) + " due to previous exception");
				Dictionary.BLOCKS_NOCAMOUFLAGE.add(blockCamouflage);
				
				// render normal force field
				renderer.renderStandardBlock(block, x, y, z);
				// renderer.renderBlockAsItem(blockCamouflage, metaCamouflage, 1);
			}
			return true;
		}
		
		return renderer.renderStandardBlock(block, x, y, z);
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}
	
	@Override
	public int getRenderId() {
		return renderId;
	}
}
/**/