package cr0s.warpdrive.block.passive;

import java.util.Random;

import cr0s.warpdrive.block.BlockAbstractBase;
import net.minecraft.block.material.Material;

public class BlockIridium extends BlockAbstractBase {
	public BlockIridium(final String registryName) {
		super(registryName, Material.IRON);
		setHardness(3.4F);
		setResistance(360.0F * 5 / 3);
		setUnlocalizedName("warpdrive.passive.IridiumBlock");
	}
	
	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}
}