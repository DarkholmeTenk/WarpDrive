package cr0s.warpdrive.block.detection;

import java.util.Random;

import cr0s.warpdrive.block.BlockAbstractBase;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockWarpIsolation extends BlockAbstractBase {
	
	public BlockWarpIsolation(final String registryName) {
		super(registryName, Material.IRON);
		setHardness(3.5F);
		setUnlocalizedName("warpdrive.detection.WarpIsolation");
	}
	
	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}
}