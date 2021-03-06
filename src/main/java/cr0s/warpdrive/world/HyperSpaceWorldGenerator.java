package cr0s.warpdrive.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import cr0s.warpdrive.config.WarpDriveConfig;

/**
 * @author Cr0s
 */
public class HyperSpaceWorldGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId != WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID) {
            // ...
        }
    }
}
