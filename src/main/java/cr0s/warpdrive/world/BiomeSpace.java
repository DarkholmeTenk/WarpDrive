package cr0s.warpdrive.world;

import net.minecraft.world.biome.Biome;

public class BiomeSpace extends Biome
{
    public BiomeSpace(BiomeProperties biomeProperties)
    {
        super(biomeProperties);
        this.theBiomeDecorator.treesPerChunk = 0;
        //this.temperature = 1F;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 0;
        this.setRegistryName("Space");
    }

    @Override
    public float getSpawningChance()
    {
        return 0;
    }
    
    @Override
    public boolean getEnableSnow()
    {
        return false;
    }
}
