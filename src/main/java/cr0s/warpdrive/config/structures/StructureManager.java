package cr0s.warpdrive.config.structures;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.InvalidXmlException;
import cr0s.warpdrive.config.RandomCollection;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.config.XmlPreprocessor;


public class StructureManager {
	public static final String GROUP_STARS = "star";
	public static final String GROUP_MOONS = "moon";
	public static final String GROUP_GASCLOUDS = "gascloud";
	public static final String GROUP_ASTEROIDS = "asteroid";
	public static final String GROUP_ASTFIELDS_BIG = "astfield_big";
	public static final String GROUP_ASTFIELDS_SMALL = "astfield_small";
	
	private static HashMap<String, RandomCollection<AbstractStructure>> structuresByGroup;
	
	private static final String[] REQUIRED_GROUPS = { GROUP_STARS, GROUP_MOONS, GROUP_GASCLOUDS, GROUP_ASTEROIDS, GROUP_ASTFIELDS_BIG, GROUP_ASTFIELDS_SMALL };
	
	public static void load(File dir) {
		
		dir.mkdir();
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("File path " + dir.getPath() + " must be a directory!");
		}
		
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file_notUsed, String name) {
				return name.startsWith("structure") && name.endsWith(".xml");
			}
		});
		
		structuresByGroup = new HashMap<>();
		for (File file : files) {
			try {
				loadXmlStructureFile(file);
			} catch (Exception exception) {
				WarpDrive.logger.error("Error loading file " + file.getName() + ": " + exception.getMessage());
				exception.printStackTrace();
			}
		}
		
		for (String group : REQUIRED_GROUPS) {
			if (!structuresByGroup.containsKey(group)) {
				WarpDrive.logger.error("Error: no structure defined for mandatory group " + group);
			}
		}
		
		WarpDrive.logger.info("Loading structure data files done");
	}
	
	private static void loadXmlStructureFile(File file) throws InvalidXmlException, SAXException, IOException  {
		WarpDrive.logger.info("Loading structure data file " + file.getName());
		Document document = WarpDriveConfig.getXmlDocumentBuilder().parse(file);
		
		// pre-process the file
		String result = XmlPreprocessor.checkModRequirements(document.getDocumentElement());
		if (!result.isEmpty()) {
			WarpDrive.logger.info("Skipping structure data file " + file.getName() + " due to " + result);
			return;
		}
		
		XmlPreprocessor.doModReqSanitation(document);
		XmlPreprocessor.doLogicPreprocessing(document);
		
		// only add FillerSets
		NodeList nodeListStructures = document.getElementsByTagName("structure");
		for (int structureIndex = 0; structureIndex < nodeListStructures.getLength(); structureIndex++) {
			
			Element elementStructure = (Element) nodeListStructures.item(structureIndex);
			
			String group = elementStructure.getAttribute("group");
			if (group.isEmpty()) {
				throw new InvalidXmlException("Structure " + (structureIndex + 1) + "/" + nodeListStructures.getLength() + " is missing a group attribute!");
			}
			
			String name = elementStructure.getAttribute("name");
			if (name.isEmpty()) {
				throw new InvalidXmlException("Structure " + (structureIndex + 1) + "/" + nodeListStructures.getLength() + " is missing a name attribute!");
			}
			
			WarpDrive.logger.info("- found Structure " + group + ":" + name);
			
			RandomCollection<AbstractStructure> randomCollection = structuresByGroup.get(group);
			if (randomCollection == null) {
				randomCollection = new RandomCollection<>();
				structuresByGroup.put(group, randomCollection);
			}
			
			AbstractStructure abstractStructure = randomCollection.getNamedEntry(name);
			if (abstractStructure == null) {
				switch (group) {
					case GROUP_STARS:
						abstractStructure = new Star(group, name);
						break;
					case GROUP_MOONS:
						abstractStructure = new Orb(group, name);
						break;
					default:
						abstractStructure = new MetaOrb(group, name);
						break;
				}
			}
			randomCollection.loadFromXML(abstractStructure, elementStructure);
		}
	}
	
	public static AbstractStructure getStructure(Random random, final String group, final String name) {
		if (group == null || group.isEmpty()) {
			return null;
		}
		
		RandomCollection<AbstractStructure> randomCollection = structuresByGroup.get(group);
		if (randomCollection == null) {
			return null;
		}
		
		if (name == null || name.isEmpty()) {
			return randomCollection.getRandomEntry(random);
		} else {
			return randomCollection.getNamedEntry(name);
		}
	}
	
	public static String getStructureNames(final String group) {
		if (group == null || group.isEmpty()) {
			// no operation
		} else {
			RandomCollection<AbstractStructure> randomCollection = structuresByGroup.get(group);
			if (randomCollection != null) {
				return randomCollection.getNames();
			}
		}
		return "Error: group '" + group + "' isn't defined. Try one of: " + StringUtils.join(structuresByGroup.keySet(), ", ");
	}
}
