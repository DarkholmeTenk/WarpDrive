package cr0s.warpdrive.config.structures;

import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.InvalidXmlException;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.config.IXmlRepresentable;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class MetaOrb extends Orb {
	protected MetaShell metaShell;
	
	public MetaOrb(final String group, final String name) {
		super(group, name);
	}
	
	@Override
	public boolean loadFromXmlElement(Element element) throws InvalidXmlException {
		super.loadFromXmlElement(element);
		
		NodeList nodeListMetaShells = element.getElementsByTagName("metaShell");
		if (nodeListMetaShells.getLength() > 1) {
			throw new InvalidXmlException("Too many metaShell defined in structure " + getFullName() + ". Maximum is 1.");
		}
		if (nodeListMetaShells.getLength() == 1) {
			metaShell = new MetaShell(getFullName());
			metaShell.loadFromXmlElement((Element) nodeListMetaShells.item(0));
		}
		
		return true;
	}
	
	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		return instantiate(random).generate(world, random, x, y, z);
	}
	
	@Override
	public AbstractInstance instantiate(Random random) {
		return new MetaOrbInstance(this, random);
	}
	
	public class MetaShell implements IXmlRepresentable {
		private final String parentFullName;
		protected Block block;
		protected int metadata;
		protected int minCount;
		protected int maxCount;
		protected double minRadius;
		protected double relativeRadius;
		
		public MetaShell(final String parentFullName) {
			this.parentFullName = parentFullName;
		}
		
		@Override
		public String getName() {
			return "metashell";
		}
		
		@Override
		public boolean loadFromXmlElement(Element element) throws InvalidXmlException {
			if (WarpDriveConfig.LOGGING_WORLDGEN) {
				WarpDrive.logger.info("  + found metashell");
			}
			
			String stringValue;
			
			// block & metadata
			stringValue = element.getAttribute("block");
			if (!stringValue.isEmpty()) {
				
				block = Block.getBlockFromName(stringValue);
				if (block == null) {
					WarpDrive.logger.warn("Skipping missing metashell core block " + stringValue + " in " + parentFullName);
				} else {
					// metadata
					stringValue = element.getAttribute("metadata");
					if (stringValue.isEmpty()) {
						metadata = 0;
					} else {
						try {
							metadata = Integer.parseInt(stringValue);
						} catch (NumberFormatException exception) {
							throw new InvalidXmlException("Structure " + parentFullName + " has an invalid metadata " + stringValue + ", expecting an integer");
						}
					}
					
					if (metadata < 0 || metadata > 15) {
						throw new InvalidXmlException("Structure " + parentFullName + " has an invalid metadata " + metadata + ", expecting a value between 0 and 15 included");
					}
				}
			}
			
			// count
			try {
				minCount = Integer.parseInt(element.getAttribute("minCount"));
			} catch (NumberFormatException exception) {
				throw new InvalidXmlException("Structure " + parentFullName + " has an invalid minCount " + element.getAttribute("minCount") + ", expecting an integer");
			}
			
			if (minCount < 1) {
				throw new InvalidXmlException("Structure " + parentFullName + " has an invalid minCount " + minCount + ", expecting greater then 0");
			}
			
			try {
				maxCount = Integer.parseInt(element.getAttribute("maxCount"));
			} catch (NumberFormatException exception) {
				throw new InvalidXmlException("Structure " + parentFullName + " has an invalid maxCount " + element.getAttribute("maxCount") + ", expecting an integer");
			}
			
			if (maxCount < minCount) {
				throw new InvalidXmlException("Structure " + parentFullName + " has an invalid maxCount " + maxCount + ", expecting greater than or equal to minCount " + minCount);
			}
			
			// radius
			try {
				stringValue = element.getAttribute("minRadius");
				if (stringValue.isEmpty()) {
					minRadius = 2;
				} else {
					minRadius = Double.parseDouble(element.getAttribute("minRadius"));
				}
			} catch (NumberFormatException exception) {
				throw new InvalidXmlException("Structure " + parentFullName + " has an invalid minRadius " + element.getAttribute("minRadius") + ", expecting a double");
			}
			
			if (minRadius < 0.0D || minRadius > 20.0D) {
				throw new InvalidXmlException("Structure " + parentFullName + " has an invalid minRadius " + minRadius + ", expecting a value between 0.0 and 20.0 included");
			}
			
			try {
				stringValue = element.getAttribute("relativeRadius");
				if (stringValue.isEmpty()) {
					relativeRadius = 0.5;
				} else {
					relativeRadius = Double.parseDouble(element.getAttribute("relativeRadius"));
				}
			} catch (NumberFormatException exception) {
				throw new InvalidXmlException("Structure " + parentFullName + " has an invalid relativeRadius " + element.getAttribute("relativeRadius") + ", expecting a double");
			}
			
			if (relativeRadius < 0.0D || relativeRadius > 2.0D) {
				throw new InvalidXmlException("Structure " + parentFullName + " has an invalid relativeRadius " + relativeRadius + ", expecting a value between 0.0 and 2.0 included");
			}
			
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
	}
}
