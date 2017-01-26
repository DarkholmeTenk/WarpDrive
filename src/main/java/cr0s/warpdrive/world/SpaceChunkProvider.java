package cr0s.warpdrive.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import cr0s.warpdrive.WarpDrive;
import net.minecraft.world.gen.ChunkProviderOverworld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpaceChunkProvider extends ChunkProviderOverworld {
	private World world;
	private Random rand;
	
	public SpaceChunkProvider(World world, long seed) {
		super(world, seed, false, "");
		rand = new Random(seed);
		this.world = world;
	}
	
	@Override
	public @Nonnull Chunk provideChunk(int x, int z) {
		rand.setSeed(x * 341873128712L + z * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		setBlocksInChunk(x, z, chunkprimer);
		
		Chunk chunk = new Chunk(world, chunkprimer, x, z);
		byte[] byteBiomes = chunk.getBiomeArray();
		for (int i = 0; i < byteBiomes.length; ++i) {
			byteBiomes[i] = (byte)Biome.getIdForBiome(WarpDrive.spaceBiome);
		}
		
		chunk.generateSkylightMap();
		return chunk;
	}
	
	@Override
	public void populate(int x, int z) {
		// super.populate(x, z);
	}

	@Override
	public boolean generateStructures(@Nonnull Chunk chunkIn, int x, int z) {
		return false;
	}

	@Override
	public @Nonnull List<Biome.SpawnListEntry> getPossibleCreatures(@Nonnull EnumCreatureType creatureType, @Nonnull BlockPos pos) {
		return new ArrayList<>();
	}

	@Nullable
	public BlockPos getStrongholdGen(@Nonnull World worldIn, String structureName, @Nonnull BlockPos position) {
		return null;
	}
	
	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
		// no structure generation
	}
}