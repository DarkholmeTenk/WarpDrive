package cr0s.warpdrive.data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.block.detection.BlockMonitor;
import cr0s.warpdrive.compat.CompatForgeMultipart;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.config.filler.Filler;

public class JumpBlock {
	public Block block;
	public int blockMeta;
	public TileEntity blockTileEntity;
	public NBTTagCompound blockNBT;
	public int x;
	public int y;
	public int z;
	public HashMap<String, NBTBase> externals;
	
	public JumpBlock() {
	}

	public JumpBlock(Block block, int blockMeta, TileEntity tileEntity, int x, int y, int z) {
		this.block = block;
		this.blockMeta = blockMeta;
		blockTileEntity = tileEntity;
		this.x = x;
		this.y = y;
		this.z = z;
		
		// save externals
		for (Entry<String, IBlockTransformer> entryBlockTransformer : WarpDriveConfig.blockTransformers.entrySet()) {
			if (entryBlockTransformer.getValue().isApplicable(block, blockMeta, tileEntity)) {
				NBTBase nbtBase = entryBlockTransformer.getValue().saveExternals(tileEntity);
				setExternal(entryBlockTransformer.getKey(), nbtBase);
			}
		}
	}
	
	public JumpBlock(Filler filler, int x, int y, int z) {
		if (filler.block == null) {
			WarpDrive.logger.info("Forcing glass for invalid filler with null block at " + x + " " + y + " " + z);
			filler.block = Blocks.glass;
		}
		block = filler.block;
		blockMeta = filler.metadata;
		blockNBT = (filler.tag != null) ? (NBTTagCompound) filler.tag.copy() : null;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public NBTBase getExternal(final String modId) {
		if (externals == null) {
			return null;
		}
		NBTBase nbtExternal = externals.get(modId);
		if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
			WarpDrive.logger.info("Restoring " + modId + " externals at " + x + " " + y + " " + z + " " + nbtExternal);
		}
		if (nbtExternal == null) {
			return null;
		}
		return nbtExternal.copy();
	}
	
	public void setExternal(final String modId, final NBTBase nbtExternal) {
		if (externals == null) {
			externals = new HashMap<>();
		}
		externals.put(modId, nbtExternal);
		if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
			WarpDrive.logger.info("Saved " + modId + " externals at " + x + " " + y + " " + z + " " + nbtExternal);
		}
	}
	
	private static final byte[] mrotNone           = {  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[] mrotRail           = {  1,  0,  5,  4,  2,  3,  7,  8,  9,  6, 10, 11, 12, 13, 14, 15 };
	private static final byte[] mrotAnvil          = {  1,  2,  3,  0,  5,  6,  7,  4,  9, 10, 11,  8, 12, 13, 14, 15 };
	private static final byte[] mrotFenceGate      = {  1,  0,  2,  3,  5,  6,  7,  4,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[] mrotPumpkin        = {  1,  2,  3,  0,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };	// Tripwire hook, Pumpkin, Jack-o-lantern
	private static final byte[] mrotEndPortalFrame = {  1,  2,  3,  0,  5,  6,  7,  4,  8,  9, 10, 11, 12, 13, 14, 15 };	// EndPortal, doors (open/closed, base/top)
	private static final byte[] mrotCocoa          = {  1,  2,  3,  0,  5,  6,  7,  4,  9, 10, 11,  8, 12, 13, 14, 15 };
	private static final byte[] mrotRepeater       = {  1,  2,  3,  0,  5,  6,  7,  4,  9, 10, 11,  8, 13, 14, 15, 12 };	// Repeater (normal/lit), Comparator
	private static final byte[] mrotBed            = {  1,  2,  3,  0,  4,  5,  6,  7,  9, 10, 11,  8, 12, 13, 14, 15 };
	private static final byte[] mrotStair          = {  2,  3,  1,  0,  6,  7,  5,  4,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[] mrotSign           = {  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,  0,  1,  2,  3 };	// Sign, Skull
	private static final byte[] mrotTrapDoor       = {  3,  2,  0,  1,  7,  6,  4,  5, 11, 10,  8,  9, 15, 14, 12, 13 };
	private static final byte[] mrotLever          = {  7,  2,  3,  4,  1,  6,  5,  0, 15, 11, 12, 10,  9, 14, 13,  8 };
	private static final byte[] mrotNetherPortal   = {  0,  2,  1,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	private static final byte[] mrotVine           = {  0,  2,  4,  6,  8, 10, 12, 14,  1,  3,  5,  7,  9, 11, 13, 15 };
	private static final byte[] mrotButton         = {  0,  3,  4,  2,  1,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };	// Button, torch (normal, redstone lit/unlit)
	private static final byte[] mrotMushroom       = {  0,  3,  6,  9,  2,  5,  8,  1,  4,  7, 10, 11, 12, 13, 14, 15 };	// Red/brown mushroom block
	private static final byte[] mrotForgeDirection = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };	// Furnace (lit/normal), Dispenser/Dropper, Enderchest, Chest (normal/trapped), Hopper, Ladder, Wall sign
	private static final byte[] mrotPiston         = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 13, 12, 10, 11, 14, 15 };	// Pistons (sticky/normal, base/head)
	private static final byte[] mrotWoodLog        = {  0,  1,  2,  3,  8,  9, 10, 11,  4,  5,  6,  7, 12, 13, 14, 15 };
	
	// Return updated metadata from rotating a vanilla block
	private int getMetadataRotation(NBTTagCompound nbtTileEntity, final byte rotationSteps) {
		if (rotationSteps == 0) {
			return blockMeta;
		}
		
		byte[] mrot = mrotNone;
		if (block instanceof BlockRailBase) {
			mrot = mrotRail;
		} else if (block instanceof BlockAnvil) {
			mrot = mrotAnvil;
		} else if (block instanceof BlockFenceGate) {
			mrot = mrotFenceGate;
		} else if (block instanceof BlockPumpkin || block instanceof BlockTripWireHook) {
			mrot = mrotPumpkin;
		} else if (block instanceof BlockEndPortalFrame || block instanceof BlockDoor) {
			mrot = mrotEndPortalFrame;
		} else if (block instanceof BlockCocoa) {
			mrot = mrotCocoa;
		} else if (block instanceof BlockRedstoneDiode) {
			mrot = mrotRepeater;
		} else if (block instanceof BlockBed) {
			mrot = mrotBed;
		} else if (block instanceof BlockStairs) {
			mrot = mrotStair;
		} else if (block instanceof BlockSign) {
			if (block == Blocks.wall_sign) {
				mrot = mrotForgeDirection;
			} else {
				mrot = mrotSign;
			}
		} else if (block instanceof BlockTrapDoor) {
			mrot = mrotTrapDoor;
		} else if (block instanceof BlockLever) {
			mrot = mrotLever;
		} else if (block instanceof BlockPortal) {
			mrot = mrotNetherPortal;
		} else if (block instanceof BlockVine) {
			mrot = mrotVine;
		} else if (block instanceof BlockButton || block instanceof BlockTorch) {
			mrot = mrotButton;
		} else if (block instanceof BlockHugeMushroom) {
			mrot = mrotMushroom;
		} else if (block instanceof BlockFurnace || block instanceof BlockDispenser || block instanceof BlockHopper
				|| block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockLadder
				|| block instanceof BlockMonitor) {
			mrot = mrotForgeDirection;
		} else if (block instanceof BlockPistonBase || block instanceof BlockPistonExtension || block instanceof BlockPistonMoving) {
			mrot = mrotPiston;
		} else if (block instanceof BlockLog) {
			mrot = mrotWoodLog;
		} else if (block instanceof BlockSkull) {
			mrot = mrotNone;
			short facing = nbtTileEntity.getShort("Rot");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.setShort("Rot", mrotSign[facing]);
				break;
			case 2:
				nbtTileEntity.setShort("Rot", mrotSign[mrotSign[facing]]);
				break;
			case 3:
				nbtTileEntity.setShort("Rot", mrotSign[mrotSign[mrotSign[facing]]]);
				break;
			default:
				break;
			}
		}
		
		switch (rotationSteps) {
			case 1:
				return mrot[blockMeta];
			case 2:
				return mrot[mrot[blockMeta]];
			case 3:
				return mrot[mrot[mrot[blockMeta]]];
			default:
				return blockMeta;
		}
	}
	
	public void deploy(World targetWorld, ITransformation transformation) {
		try {
			NBTTagCompound nbtToDeploy = null;
			if (blockTileEntity != null) {
				nbtToDeploy = new NBTTagCompound();
				blockTileEntity.writeToNBT(nbtToDeploy);
			}
			int newBlockMeta = blockMeta;
			if (externals != null) {
				for (Entry<String, NBTBase> external : externals.entrySet()) {
					IBlockTransformer blockTransformer = WarpDriveConfig.blockTransformers.get(external.getKey());
					if (blockTransformer != null) {
						newBlockMeta = blockTransformer.rotate(block, blockMeta, nbtToDeploy, transformation);
					}
				}
			} else {
				newBlockMeta = getMetadataRotation(nbtToDeploy, transformation.getRotationSteps());
			}
			ChunkCoordinates target = transformation.apply(x, y, z);
			setBlockNoLight(targetWorld, target.posX, target.posY, target.posZ, block, newBlockMeta, 2);
			
			// Re-schedule air blocks update
			if (block == WarpDrive.blockAir) {
				targetWorld.markBlockForUpdate(target.posX, target.posY, target.posZ);
				targetWorld.scheduleBlockUpdate(target.posX, target.posY, target.posZ, block, 40 + targetWorld.rand.nextInt(20));
			}
			
			if (nbtToDeploy != null) {
				nbtToDeploy.setInteger("x", target.posX);
				nbtToDeploy.setInteger("y", target.posY);
				nbtToDeploy.setInteger("z", target.posZ);
				
				if (nbtToDeploy.hasKey("mainX") && nbtToDeploy.hasKey("mainY") && nbtToDeploy.hasKey("mainZ")) {// Mekanism 6.0.4.44
					if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
						WarpDrive.logger.info(this + " deploy: TileEntity has mainXYZ");
					}
					ChunkCoordinates mainTarget = transformation.apply(nbtToDeploy.getInteger("mainX"), nbtToDeploy.getInteger("mainY"), nbtToDeploy.getInteger("mainZ"));
					nbtToDeploy.setInteger("mainX", mainTarget.posX);
					nbtToDeploy.setInteger("mainY", mainTarget.posY);
					nbtToDeploy.setInteger("mainZ", mainTarget.posZ);
				}
				
				if (nbtToDeploy.hasKey("screenData")) {// IC2NuclearControl 2.2.5a
					NBTTagCompound nbtScreenData = nbtToDeploy.getCompoundTag("screenData");
					if ( nbtScreenData.hasKey("minX") && nbtScreenData.hasKey("minY") && nbtScreenData.hasKey("minZ")
					  && nbtScreenData.hasKey("maxX") && nbtScreenData.hasKey("maxY") && nbtScreenData.hasKey("maxZ")) {
						if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
							WarpDrive.logger.info(this + " deploy: TileEntity has screenData.min/maxXYZ");
						}
						ChunkCoordinates minTarget = transformation.apply(nbtScreenData.getInteger("minX"), nbtScreenData.getInteger("minY"), nbtScreenData.getInteger("minZ"));
						nbtScreenData.setInteger("minX", minTarget.posX);
						nbtScreenData.setInteger("minY", minTarget.posY);
						nbtScreenData.setInteger("minZ", minTarget.posZ);
						ChunkCoordinates maxTarget = transformation.apply(nbtScreenData.getInteger("maxX"), nbtScreenData.getInteger("maxY"), nbtScreenData.getInteger("maxZ"));
						nbtScreenData.setInteger("maxX", maxTarget.posX);
						nbtScreenData.setInteger("maxY", maxTarget.posY);
						nbtScreenData.setInteger("maxZ", maxTarget.posZ);
						nbtToDeploy.setTag("screenData", nbtScreenData);
					}
				}
				
				if (nbtToDeploy.hasKey("hasValidBubble")) {// Galacticraft 3.0.11.333
					nbtToDeploy.setBoolean("hasValidBubble", false);
					// old bubble will die naturally due to missing tile entity, new one will be spawned
				}
				
				TileEntity newTileEntity = null;
				boolean isForgeMultipart = false;
				if (WarpDriveConfig.isForgeMultipartLoaded && nbtToDeploy.hasKey("id") && nbtToDeploy.getString("id").equals("savedMultipart")) {
					isForgeMultipart = true;
					newTileEntity = (TileEntity) CompatForgeMultipart.methodMultipartHelper_createTileFromNBT.invoke(null, targetWorld, nbtToDeploy);
					
				} else if (block == WarpDriveConfig.CC_Computer || block == WarpDriveConfig.CC_peripheral
						|| block == WarpDriveConfig.CCT_Turtle || block == WarpDriveConfig.CCT_Expanded || block == WarpDriveConfig.CCT_Advanced) {
					newTileEntity = TileEntity.createAndLoadEntity(nbtToDeploy);
					newTileEntity.invalidate();
					
				}
				
				if (newTileEntity == null) {
					newTileEntity = TileEntity.createAndLoadEntity(nbtToDeploy);
				}
				
				if (newTileEntity != null) {
					newTileEntity.setWorldObj(targetWorld);
					newTileEntity.validate();
					
					targetWorld.setTileEntity(target.posX, target.posY, target.posZ, newTileEntity);
					if (isForgeMultipart) {
						CompatForgeMultipart.methodTileMultipart_onChunkLoad.invoke(newTileEntity);
						CompatForgeMultipart.methodMultipartHelper_sendDescPacket.invoke(null, targetWorld, newTileEntity);
					}
					return;
				} else {
					WarpDrive.logger.info(" deploy failed to create new tile entity at " + x + " " + y + " " + z + " blockId " + block + ":" + blockMeta);
					WarpDrive.logger.info("NBT data was " + nbtToDeploy);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			String coordinates;
			try {
				coordinates = " at " + x + " " + y + " " + z + " blockId " + block + ":" + blockMeta;
			} catch (Exception dropMe) {
				coordinates = " (unknown coordinates)";
			}
			WarpDrive.logger.info("moveBlockSimple exception at " + coordinates);
			return;
		}
	}
	
	public static void refreshBlockStateOnClient(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity != null) {
			Class<?> teClass = tileEntity.getClass();
			if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
				WarpDrive.logger.info("Tile at " + x + " " + y + " " + z + " is " + teClass + " derived from " + teClass.getSuperclass());
			}
			try {
				String superClassName = teClass.getSuperclass().getName();
				boolean isIC2 = superClassName.contains("ic2.core.block");
				if (isIC2 || superClassName.contains("advsolar.common.tiles")) {// IC2
					Method onUnloaded = teClass.getMethod("onUnloaded");
					Method onLoaded = teClass.getMethod("onLoaded");
					if (onUnloaded != null && onLoaded != null) {
						onUnloaded.invoke(tileEntity);
						onLoaded.invoke(tileEntity);
					} else {
						WarpDrive.logger.error("Missing IC2 (un)loaded events for TileEntity '" + teClass.getName() + "' at " + x + " " + y + " " + z + ". Please report this issue!");
					}
					
					tileEntity.updateContainingBlockInfo();
				}
				
				if (isIC2) {// IC2
					// required in SSP during same dimension jump to update client with rotation data
					if (teClass.getName().equals("ic2.core.block.wiring.TileEntityCable")) {
						NetworkHelper_updateTileEntityField(tileEntity, "color");
						NetworkHelper_updateTileEntityField(tileEntity, "foamColor");
						NetworkHelper_updateTileEntityField(tileEntity, "foamed");
					} else {
						NetworkHelper_updateTileEntityField(tileEntity, "active");
						NetworkHelper_updateTileEntityField(tileEntity, "facing");
						if (teClass.getName().equals("ic2.core.block.reactor.TileEntityNuclearReactorElectric")) {
							NetworkHelper_updateTileEntityField(tileEntity, "heat");	// not working, probably an IC2 bug here...
						}
						// not needed: if ic2.core.block.machine.tileentity.TileEntityMatter then updated "state"
					}
				} else {// IC2 extensions without network optimization (transferring all fields) 
					try {
						Method getNetworkedFields = teClass.getMethod("getNetworkedFields");
						List<String> fields = (List<String>) getNetworkedFields.invoke(tileEntity);
						if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
							WarpDrive.logger.info("Tile has " + fields.size() + " networked fields: " + fields);
						}
						for (String field : fields) {
							NetworkHelper_updateTileEntityField(tileEntity, field);
						}
					} catch (NoSuchMethodException exception) {
						// WarpDrive.logger.info("Tile has no getNetworkedFields method");
					} catch (NoClassDefFoundError exception) {
						if (WarpDriveConfig.LOGGING_JUMP) {
							WarpDrive.logger.info("TileEntity " + teClass.getName() + " at " + x + " " + y + " " + z + " is missing a class definition");
							if (WarpDriveConfig.LOGGING_JUMPBLOCKS) {
								exception.printStackTrace();
							}
						}
					}
				}
			} catch (Exception exception) {
				WarpDrive.logger.info("Exception involving TileEntity " + teClass.getName() + " at " + x + " " + y + " " + z);
				exception.printStackTrace();
			}
		}
	}
	
	// IC2 support for updating tile entity fields
	private static Object NetworkManager_instance;
	private static Method NetworkManager_updateTileEntityField;
	
	public static void NetworkHelper_init() {
		try {
			NetworkManager_updateTileEntityField = Class.forName("ic2.core.network.NetworkManager").getMethod("updateTileEntityField", new Class[] { TileEntity.class, String.class });
			
			NetworkManager_instance = Class.forName("ic2.core.IC2").getDeclaredField("network").get(null);
			// This code is an IC2 hack to fix an issue on 1.7.10 up to industrialcraft-2-2.2.763-experimental, see http://bt.industrial-craft.net/view.php?id=1704
			if (!NetworkManager_instance.getClass().getName().contains("NetworkManager")) {
				NetworkManager_instance = Class.forName("ic2.core.util.SideGateway").getMethod("get").invoke(NetworkManager_instance);
				WarpDrive.logger.error("Patched IC2 API, new instance is '" + NetworkManager_instance + "'");
			}
			// IC2 hack ends here
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	public static void NetworkHelper_updateTileEntityField(TileEntity tileEntity, String field) {
		try {
			if (NetworkManager_instance == null) {
				NetworkHelper_init();
			}
			NetworkManager_updateTileEntityField.invoke(NetworkManager_instance, tileEntity, field);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	// IC2 support ends here
	
	// This code is a straight copy from Vanilla net.minecraft.world.World.setBlock to remove lighting computations
	public static boolean setBlockNoLight(World w, int x, int y, int z, Block block, int blockMeta, int par6) {
		if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
			if (y < 0) {
				return false;
			} else if (y >= 256) {
				return false;
			} else {
				Chunk chunk = w.getChunkFromChunkCoords(x >> 4, z >> 4);
				Block block1 = null;
				// net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
				
				if ((par6 & 1) != 0) {
					block1 = chunk.getBlock(x & 15, y, z & 15);
				}
				
				// Disable rollback on item use
				// if (w.captureBlockSnapshots && !w.isRemote) {
				// 	blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(w, x, y, z, par6);
				// 	w.capturedBlockSnapshots.add(blockSnapshot);
				// }
				
				boolean flag = myChunkSBIDWMT(chunk, x & 15, y, z & 15, block, blockMeta);
				
				// Disable rollback on item use
				// if (!flag && blockSnapshot != null) {
				//	w.capturedBlockSnapshots.remove(blockSnapshot);
				//	blockSnapshot = null;
				// }
				
				// Remove light computations
				// w.theProfiler.startSection("checkLight");
				// w.func_147451_t(x, y, z);
				// w.theProfiler.endSection();
				
				// Disable rollback on item use
				// if (flag && blockSnapshot == null) {// Don't notify clients or update physics while capturing blockstates
					// Modularize client and physic updates
					w.markAndNotifyBlock(x, y, z, chunk, block1, block, par6);
				// }
					
				return flag;
			}
		} else {
			return false;
		}
	}
	
	// This code is a straight copy from Vanilla net.minecraft.world.Chunk.func_150807_a to remove lighting computations
	public static boolean myChunkSBIDWMT(Chunk c, int x, int y, int z, Block block, int blockMeta) {
		int i1 = z << 4 | x;
		
		if (y >= c.precipitationHeightMap[i1] - 1) {
			c.precipitationHeightMap[i1] = -999;
		}
		
		// Removed light recalculations
		// int j1 = c.heightMap[i1];
		Block block1 = c.getBlock(x, y, z);
		int k1 = c.getBlockMetadata(x, y, z);
		
		if (block1 == block && k1 == blockMeta) {
			return false;
		} else {
			ExtendedBlockStorage[] storageArrays = c.getBlockStorageArray();
			ExtendedBlockStorage extendedblockstorage = storageArrays[y >> 4];
			// Removed light recalculations
			// boolean flag = false;
			
			if (extendedblockstorage == null) {
				if (block == Blocks.air) {
					return false;
				}
				
				extendedblockstorage = storageArrays[y >> 4] = new ExtendedBlockStorage(y >> 4 << 4, !c.worldObj.provider.hasNoSky);
				// Removed light recalculations
				// flag = y >= j1;
			}
			
			int l1 = c.xPosition * 16 + x;
			int i2 = c.zPosition * 16 + z;
			
			// Removed light recalculations
			// int k2 = block1.getLightOpacity(c.worldObj, l1, y, i2);
			
			// Removed preDestroy event
			// if (!c.worldObj.isRemote) {
			// 	block1.onBlockPreDestroy(c.worldObj, l1, y, i2, k1);
			// }
			
			extendedblockstorage.func_150818_a(x, y & 15, z, block);
			extendedblockstorage.setExtBlockMetadata(x, y & 15, z, blockMeta); // This line duplicates the one below, so breakBlock fires with valid worldstate
			
			// Skip air at destination
			if (block1 != Blocks.air) {
				if (!c.worldObj.isRemote) {
					block1.breakBlock(c.worldObj, l1, y, i2, block1, k1);
					// After breakBlock a phantom TE might have been created with incorrect meta. This attempts to kill that phantom TE so the normal one can be created properly later
					TileEntity te = c.getTileEntityUnsafe(x & 0x0F, y, z & 0x0F);
					if (te != null && te.shouldRefresh(block1, c.getBlock(x & 0x0F, y, z & 0x0F), k1, c.getBlockMetadata(x & 0x0F, y, z & 0x0F), c.worldObj, l1, y, i2)) {
						c.removeTileEntity(x & 0x0F, y, z & 0x0F);
					}
				} else if (block1.hasTileEntity(k1)) {
					TileEntity te = c.getTileEntityUnsafe(x & 0x0F, y, z & 0x0F);
					if (te != null && te.shouldRefresh(block1, block, k1, blockMeta, c.worldObj, l1, y, i2)) {
						c.worldObj.removeTileEntity(l1, y, i2);
					}
				}
			}
			
			if (extendedblockstorage.getBlockByExtId(x, y & 15, z) != block) {
				return false;
			} else {
				extendedblockstorage.setExtBlockMetadata(x, y & 15, z, blockMeta);
				// Removed light recalculations
				/*
				if (flag) {
					c.generateSkylightMap();
				} else {
					int j2 = block.getLightOpacity(c.worldObj, l1, y, i2);
	
					if (j2 > 0) {
						if (y >= j1) {
							c.relightBlock(x, y + 1, z);
						}
					} else if (y == j1 - 1) {
						c.relightBlock(x, y, z);
					}
	
					if (j2 != k2 && (j2 < k2 || c.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) > 0 || c.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 0)) {
						c.propagateSkylightOcclusion(x, z);
					}
				}
				/**/
				
				TileEntity tileentity;
				
				// Removed onBlockAdded event
				// if (!c.worldObj.isRemote) {
				//	block.onBlockAdded(c.worldObj, l1, y, i2);
				// }
				
				// Skip air at destination
				if (block1 != Blocks.air) {
					if (block.hasTileEntity(blockMeta)) {
						tileentity = c.func_150806_e(x, y, z);
						
						if (tileentity != null) {
							tileentity.updateContainingBlockInfo();
							tileentity.blockMetadata = blockMeta;
						}
					}
				}
				
				c.isModified = true;
				return true;
			}
		}
	}
}
