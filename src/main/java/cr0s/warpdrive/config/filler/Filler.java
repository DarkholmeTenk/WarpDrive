package cr0s.warpdrive.config.filler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.InvalidXmlException;
import cr0s.warpdrive.config.IXmlRepresentable;
import cr0s.warpdrive.data.JumpBlock;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Represents a single filler block.
 **/
public class Filler implements IXmlRepresentable {
	private String name;
	public Block block;
	public int metadata;
	public NBTTagCompound tag = null; // TODO
	
	@Override
	public String getName() {
		return name;
	}
	
	public Filler() {
	}
	
	@Override
	public boolean loadFromXmlElement(Element element) throws InvalidXmlException {
		
		// Check there is a block name
		if (!element.hasAttribute("block")) {
			throw new InvalidXmlException("Filler " + element + " is missing a block tag!");
		}
		
		String blockName = element.getAttribute("block");
		block = Block.getBlockFromName(blockName);
		if (block == null) {
			WarpDrive.logger.warn("Skipping missing block " + blockName);
			return false;
		}
		
		// Get metadata attribute, defaults to 0
		metadata = 0;
		String stringMetadata = element.getAttribute("metadata");
		if (!stringMetadata.isEmpty()) {
			try {
				metadata = Integer.parseInt(stringMetadata);
			} catch (NumberFormatException exception) {
				throw new InvalidXmlException("Invalid metadata for block " + blockName);
			}
		}
		
		name = blockName + "@" + metadata + "{" + tag + "}";
		
		return true;
	}
	
	/**
	 * @deprecated Not implemented
	 **/
	@Deprecated
	@Override
	public void saveToXmlElement(Element element, Document document) throws InvalidXmlException {
		throw new InvalidXmlException("Not implemented");
	}

	public void setBlock(World world, int x, int y, int z) {
		JumpBlock.setBlockNoLight(world, x, y, z, block, metadata, 2);
		// world.setBlock(x, y, z, block, metadata, 2);
		// TODO set NBT data
	}
	
	@Override
	public boolean equals(Object object) {
		return object instanceof Filler
			&& (block == null || block.equals(((Filler)object).block))
			&& metadata == ((Filler)object).metadata
			&& (tag == null || tag.equals(((Filler)object).tag));
	}
	
	@Override
	public String toString() {
		return "Filler(" + block.getUnlocalizedName() + "@" + metadata + ")";
	}

	@Override
	public int hashCode() {
		return Block.getIdFromBlock(block) * 16 + metadata + (tag == null ? 0 : tag.hashCode() * 4096 * 16);
	}
}
