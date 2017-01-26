package cr0s.warpdrive.block.passive;

import java.util.Random;

import cr0s.warpdrive.block.BlockAbstractBase;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockHighlyAdvancedMachine extends BlockAbstractBase {
	public BlockHighlyAdvancedMachine(final String registryName) {
		super(registryName, Material.IRON);
		setHardness(5.0F);
		setUnlocalizedName("warpdrive.passive.HighlyAdvancedMachine");
	}
	
	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}
}