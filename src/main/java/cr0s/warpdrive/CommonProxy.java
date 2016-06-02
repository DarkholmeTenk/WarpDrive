package cr0s.warpdrive;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.registry.EntityRegistry;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.render.EntityCamera;
import cr0s.warpdrive.world.EntitySphereGen;
import cr0s.warpdrive.world.EntityStarCore;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class CommonProxy {
	private static WeakHashMap<GameProfile, WeakReference<EntityPlayer>> fakePlayers = new WeakHashMap<>(100);
	
	void registerEntities() {
		EntityRegistry.registerModEntity(EntitySphereGen.class, "EntitySphereGenerator", WarpDriveConfig.G_ENTITY_SPHERE_GENERATOR_ID, WarpDrive.instance, 200, 1, false);
		EntityRegistry.registerModEntity(EntityStarCore.class , "EntityStarCore"       , WarpDriveConfig.G_ENTITY_STAR_CORE_ID       , WarpDrive.instance, 300, 1, false);
		EntityRegistry.registerModEntity(EntityCamera.class   , "EntityCamera"         , WarpDriveConfig.G_ENTITY_CAMERA_ID          , WarpDrive.instance, 300, 1, false);
	}
	
	private WeakReference<EntityPlayer> getFakePlayer(EntityPlayer entityPlayer, WorldServer world, int x, int y, int z) {
		GameProfile gameProfile = entityPlayer == null ? WarpDrive.gameProfile : entityPlayer.getGameProfile();
		WeakReference<EntityPlayer> weakFakePlayer = fakePlayers.get(gameProfile);
		EntityPlayer entityFakePlayer = (weakFakePlayer == null) ? null : weakFakePlayer.get();
		if (entityFakePlayer == null) {
			entityFakePlayer = FakePlayerFactory.get(world, gameProfile);
			((EntityPlayerMP)entityFakePlayer).theItemInWorldManager.setGameType(WorldSettings.GameType.SURVIVAL);
			weakFakePlayer = new WeakReference<>(entityFakePlayer);
			fakePlayers.put(gameProfile, weakFakePlayer);
		} else {
			entityFakePlayer.worldObj = world;
		}
		entityFakePlayer.posX = x;
		entityFakePlayer.posY = y;
		entityFakePlayer.posZ = z;

		return weakFakePlayer;
	}
	
	public boolean isBlockBreakCanceled(EntityPlayer entityPlayer, int sourceX, int sourceY, int sourceZ,
	                                       World world, int eventX, int eventY, int eventZ) {
		if (world.isRemote) {
			return false;
		}
		if (WarpDriveConfig.LOGGING_BREAK_PLACE) {
			WarpDrive.logger.info("isBlockBreakCanceled by " + entityPlayer + " at " + sourceX + " " + sourceY + " " + sourceZ
				+ " to " + world + " " + eventX + " " + eventY + " " + eventZ);
		}
		
		Block block = world.getBlock(eventX, eventY, eventZ);
		if (!block.isAir(world, eventX, eventY, eventZ)) {
			BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(eventX, eventY, eventZ, world,
				world.getBlock(eventX, eventY, eventZ), world.getBlockMetadata(eventX, eventY, eventZ),
				WarpDrive.proxy.getFakePlayer(entityPlayer, (WorldServer) world, sourceX, sourceY, sourceZ).get());
			MinecraftForge.EVENT_BUS.post(breakEvent);
			if (WarpDriveConfig.LOGGING_BREAK_PLACE) {
				WarpDrive.logger.info("isBlockBreakCanceled player " + breakEvent.getPlayer()
					+ " isCanceled " + breakEvent.isCanceled());
			}
			return breakEvent.isCanceled();
		}
		return false;
	}
	
	public boolean isBlockPlaceCanceled(EntityPlayer entityPlayer, int sourceX, int sourceY, int sourceZ,
	                                       World world, int eventX, int eventY, int eventZ, Block block, int metadata) {
		if (world.isRemote) {
			return false;
		}
		if (WarpDriveConfig.LOGGING_BREAK_PLACE) {
			WarpDrive.logger.info("isBlockPlaceCanceled by " + entityPlayer + " at " + sourceX + " " + sourceY + " " + sourceZ
				+ " to " + world + " " + eventX + " " + eventY + " " + eventZ + " of " + block + ":" + metadata);
		}
		BlockEvent.PlaceEvent placeEvent = new BlockEvent.PlaceEvent(
			new BlockSnapshot(world, eventX, eventY, eventZ, block, metadata), Blocks.air,
			WarpDrive.proxy.getFakePlayer(entityPlayer, (WorldServer) world, sourceX, sourceY, sourceZ).get() );
		
		MinecraftForge.EVENT_BUS.post(placeEvent);
		if (WarpDriveConfig.LOGGING_BREAK_PLACE) {
			WarpDrive.logger.info("isBlockPlaceCanceled player " + placeEvent.player + " isCanceled " + placeEvent.isCanceled());
		}
		return placeEvent.isCanceled();
	}
}