package cr0s.warpdrive.config.structures;

import java.util.Random;

import net.minecraft.world.World;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.InvalidXmlException;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.config.filler.FillerManager;
import cr0s.warpdrive.config.filler.FillerSet;

public class Orb extends AbstractStructure {
	
	protected OrbShell[] orbShells;
	protected boolean hasStarCore = false;
	protected String schematicName;
	
	public Orb(final String group, final String name) {
		super(group, name);
	}
	
	@Override
	public boolean loadFromXmlElement(Element element) throws InvalidXmlException {
		super.loadFromXmlElement(element);
		
		NodeList nodeListShells = element.getElementsByTagName("shell");
		orbShells = new OrbShell[nodeListShells.getLength()];
		int shellIndexOut = 0;
		for (int shellIndexIn = 0; shellIndexIn < nodeListShells.getLength(); shellIndexIn++) {
			Element elementShell = (Element) nodeListShells.item(shellIndexIn);
			String orbShellName = elementShell.getAttribute("name");
			
			orbShells[shellIndexOut] = new OrbShell(getFullName(), orbShellName);
			try {
				orbShells[shellIndexOut].loadFromXmlElement(elementShell);
				shellIndexOut++;
			} catch (InvalidXmlException exception) {
				exception.printStackTrace();
				WarpDrive.logger.error("Skipping invalid shell " + orbShellName);
			}
		}
		
		NodeList nodeListSchematic = element.getElementsByTagName("schematic");
		if (nodeListSchematic.getLength() > 1) {
			WarpDrive.logger.error("Too many schematic defined, only first one will be used in structure " + getFullName());
		}
		if (nodeListSchematic.getLength() > 0) {
			schematicName = ((Element)nodeListSchematic.item(0)).getAttribute("group");
		}
		
		return true;
	}
	
	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		return instantiate(random).generate(world, random, x, y, z);
	}
	
	@Override
	public AbstractInstance instantiate(Random random) {
		return new OrbInstance(this, random);
	}
	
	public class OrbShell extends FillerSet {
		private final String parentFullName;
		protected int minThickness;
		protected int maxThickness;
		
		public OrbShell(final String parentFullName, final String name) {
			super(null, name);
			this.parentFullName = parentFullName;
		}
		
		@Override
		public boolean loadFromXmlElement(Element element) throws InvalidXmlException {
			if (WarpDriveConfig.LOGGING_WORLDGEN) {
				WarpDrive.logger.info("  + found shell " + element.getAttribute("name"));
			}
			
			super.loadFromXmlElement(element);
			
			// resolve static imports
			for (String importGroupName : getImportGroupNames()) {
				FillerSet fillerSet = FillerManager.getFillerSet(importGroupName);
				if (fillerSet == null) {
					WarpDrive.logger.warn("Skipping missing FillerSet " + importGroupName + " in shell " + parentFullName + ":" + name);
				} else {
					loadFrom(fillerSet);
				}
			}
			
			// validate dynamic imports
			for (String importGroup : getImportGroups()) {
				if (!FillerManager.doesGroupExist(importGroup)) {
					WarpDrive.logger.warn("An invalid FillerSet group " + importGroup + " is referenced in shell " + parentFullName + ":" + name);
				}
			}
			
			// shell thickness
			try {
				minThickness = Integer.parseInt(element.getAttribute("minThickness"));
			} catch (NumberFormatException exception) {
				throw new InvalidXmlException("Invalid minThickness in shell " + name + " of structure " + parentFullName);
			}
			
			try {
				maxThickness = Integer.parseInt(element.getAttribute("maxThickness"));
			} catch (NumberFormatException exception) {
				throw new InvalidXmlException("Invalid maxThickness in shell " + name + " of structure " + parentFullName);
			}
			
			if (maxThickness < minThickness) {
				throw new InvalidXmlException("Invalid maxThickness " + maxThickness + " lower than minThickness " + minThickness + " in shell " + name + " of orb " + parentFullName);
			}
			
			return true;
		}
		
		public OrbShell instantiate(Random random) {
			OrbShell orbShell = new OrbShell(parentFullName, name);
			orbShell.minThickness = minThickness;
			orbShell.maxThickness = maxThickness;
			try {
				orbShell.loadFrom(this);
				for (String importGroup : getImportGroups()) {
					FillerSet fillerSet = FillerManager.getRandomFillerSetFromGroup(random, importGroup);
					if (fillerSet == null) {
						WarpDrive.logger.info("Ignoring invalid group " + importGroup + " in shell " + name + " of structure " + parentFullName);
						continue;
					}
					if (WarpDriveConfig.LOGGING_WORLDGEN) {
						WarpDrive.logger.info("Filling " + parentFullName + ":" + name + " with " + importGroup + ":" + fillerSet.getName());
					}
					orbShell.loadFrom(fillerSet);
				}
			} catch (InvalidXmlException exception) {
				exception.printStackTrace();
				WarpDrive.logger.error("Failed to instantiate shell " + name + " from structure " + parentFullName);
			}
			if (orbShell.isEmpty()) {
				if (WarpDriveConfig.LOGGING_WORLDGEN) {
					WarpDrive.logger.info("Ignoring empty shell " + name + " in structure " + parentFullName + "");
				}
				return null;
			}
			return orbShell;
		}
	}
}
