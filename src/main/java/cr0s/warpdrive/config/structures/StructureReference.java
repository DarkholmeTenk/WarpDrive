package cr0s.warpdrive.config.structures;

import java.util.Random;

import net.minecraft.world.World;

import org.w3c.dom.Element;

import cr0s.warpdrive.config.InvalidXmlException;

public class StructureReference extends AbstractStructure {
	
	public StructureReference(final String group, final String name) {
		super(group, name);
	}
	
	@Override
	public boolean loadFromXmlElement(Element element) throws InvalidXmlException {
		super.loadFromXmlElement(element);
		
		return true;
	}
	
	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		return instantiate(random).generate(world, random, x, y, z);
	}
	
	@Override
	public AbstractInstance instantiate(Random random) {
		return StructureManager.getStructure(random, group, name).instantiate(random);
	}
}
