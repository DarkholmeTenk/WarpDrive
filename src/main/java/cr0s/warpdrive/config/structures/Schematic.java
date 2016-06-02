package cr0s.warpdrive.config.structures;

import java.util.Random;

import net.minecraft.world.World;

public class Schematic extends AbstractStructure {
	
	public Schematic(final String group, final String name) {
		super(group, name);
	}
	
	@Override
	public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AbstractInstance instantiate(Random random) {
		// TODO Auto-generated method stub
		return null;
	}
}
