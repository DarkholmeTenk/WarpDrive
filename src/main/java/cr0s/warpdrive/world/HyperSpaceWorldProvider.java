package cr0s.warpdrive.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.render.RenderBlank;

public class HyperSpaceWorldProvider extends WorldProvider {
	
	public HyperSpaceWorldProvider() {
		biomeProvider  = new BiomeProviderSingle(WarpDrive.spaceBiome);
		hasNoSky = true;
	}
	
	@Override
	public DimensionType getDimensionType() {
		return WarpDrive.dimensionTypeHyperSpace;
	}
	
	@Override
	public boolean canRespawnHere() {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public float getStarBrightness(float par1) {
		return 0F;
	}
	
	@Override
	public boolean isSurfaceWorld() {
		return true;
	}
	
	@Override
	public int getAverageGroundLevel() {
		return 1;
	}

	@Override
	public double getHorizon() {
		return 1;
	}
	
	@Override
	public void updateWeather() {
		super.resetRainAndThunder();
	}
	
	@Override
	public Biome getBiomeForCoords(BlockPos blockPos) {
		return WarpDrive.spaceBiome;
	}
	
	@Override
	public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) {
		super.setAllowedSpawnTypes(true, true);
	}
	
	@Override
	public float calculateCelestialAngle(long par1, float par3) {
		return 0.5F;
	}
	
	@Override
	protected void generateLightBrightnessTable() {
		float f = 0.0F;
		
		for (int i = 0; i <= 15; ++i) {
			float f1 = 1.0F - i / 15.0F;
			lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public String getSaveFolder() {
		return (getDimensionType().getId() == 0 ? null : "WarpDriveHyperSpace" + getDimensionType().getId());
	}
	
	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		BlockPos blockPos = worldObj.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
		return blockPos.getY() != 0;
	}
	
	@Override
	public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
		setCloudRenderer(new RenderBlank());
		setSkyRenderer(new RenderBlank());
		return new Vec3d(1.0D, 0.0D, 0.0D);
	}
	
	@Override
	public Vec3d getFogColor(float par1, float par2) {
		return new Vec3d(0.1D, 0.0D, 0.0D);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean isSkyColored() {
		return true;
	}
		
	@Override
	public int getRespawnDimension(EntityPlayerMP player) {
		return WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID;
	}
	
	@Override
	public IChunkGenerator createChunkGenerator() {
		return new HyperSpaceChunkProvider(worldObj, 46);
	}
	
	@Override
	public boolean canBlockFreeze(BlockPos blockPos, boolean byWater) {
		return false;
	}
	
	@Override
	public BlockPos getRandomizedSpawnPoint() {
		BlockPos blockPos = new BlockPos(worldObj.getSpawnPoint());
		// boolean isAdventure = worldObj.getWorldInfo().getGameType() == EnumGameType.ADVENTURE;
		int spawnFuzz = 100;
		int spawnFuzzHalf = spawnFuzz / 2;
		{
			blockPos = new BlockPos(
				blockPos.getX() + worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf,
				200,
				blockPos.getZ() + worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf);
		}
		
		if (worldObj.isAirBlock(blockPos)) {
			worldObj.setBlockState(blockPos, Blocks.STONE.getDefaultState(), 2);
			worldObj.setBlockState(blockPos.add( 1, 1,  0), Blocks.GLASS.getDefaultState(), 2);
			worldObj.setBlockState(blockPos.add( 1, 2,  0), Blocks.GLASS.getDefaultState(), 2);
			worldObj.setBlockState(blockPos.add(-1, 1,  0), Blocks.GLASS.getDefaultState(), 2);
			worldObj.setBlockState(blockPos.add(-1, 2,  0), Blocks.GLASS.getDefaultState(), 2);
			worldObj.setBlockState(blockPos.add( 0, 1,  1), Blocks.GLASS.getDefaultState(), 2);
			worldObj.setBlockState(blockPos.add( 0, 2,  1), Blocks.GLASS.getDefaultState(), 2);
			worldObj.setBlockState(blockPos.add( 0, 1, -1), Blocks.GLASS.getDefaultState(), 2);
			worldObj.setBlockState(blockPos.add( 0, 2, -1), Blocks.GLASS.getDefaultState(), 2);
			worldObj.setBlockState(blockPos.add( 0, 3,  0), Blocks.GLASS.getDefaultState(), 2);
			worldObj.setBlockState(blockPos.add( 0, 0,  0), WarpDrive.blockAir.getStateFromMeta(15), 2);
			worldObj.setBlockState(blockPos.add( 0, 1,  0), WarpDrive.blockAir.getStateFromMeta(15), 2);
		}
		
		return blockPos;
	}
	
	@Override
	public boolean isDaytime() {
		return false;
	}
	
	@Override
	public boolean canDoLightning(Chunk chunk) {
		return false;
	}
	
	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return false;
	}
}