package cr0s.warpdrive.config.filler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.InvalidXmlException;
import cr0s.warpdrive.config.RandomCollection;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.config.XmlPreprocessor;

public class FillerManager {
	// all fillerSets
	private static HashMap<String, RandomCollection<FillerSet>> fillerSetsByGroup;
	
	@SuppressWarnings("MismatchedReadAndWriteOfArray") // we've no required filler groups
	private static final String[] REQUIRED_GROUPS = { };
	
	public static void load(File dir) {
		// (directory is created by caller, so it can copy default files if any)
		
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("File path " + dir.getName() + " must be a directory!");
		}
		
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file_notUsed, String name) {
				return name.startsWith("filler") && name.endsWith(".xml");
			}
		});
		
		fillerSetsByGroup = new HashMap<>();
		for(File file : files) {
			try {
				loadXmlFillerFile(file);
			} catch (Exception exception) {
				WarpDrive.logger.error("Error loading filler data file " + file.getName() + ": " + exception.getMessage());
				exception.printStackTrace();
			}
		}
		
		for (String group : REQUIRED_GROUPS) {
			if (!fillerSetsByGroup.containsKey(group)) {
				WarpDrive.logger.error("Error: no fillerSet defined for mandatory group " + group);
			}
		}
		
		propagateFillerSets();
		
		WarpDrive.logger.info("Loading filler data files done");
	}
	
	@SuppressWarnings("Convert2Diamond")
	private static void loadXmlFillerFile(File file) throws InvalidXmlException, SAXException, IOException {
		WarpDrive.logger.info("Loading filler data file " + file.getName());
		Document document = WarpDriveConfig.getXmlDocumentBuilder().parse(file);
		
		// pre-process the file
		String result = XmlPreprocessor.checkModRequirements(document.getDocumentElement());
		if (!result.isEmpty()) {
			WarpDrive.logger.info("Skipping filler data file " + file.getName() + " due to " + result);
			return;
		}
		
		XmlPreprocessor.doModReqSanitation(document);
		XmlPreprocessor.doLogicPreprocessing(document);
		
		// only add FillerSets
		NodeList nodeListFillerSet = document.getElementsByTagName("fillerSet");
		for (int fillerSetIndex = 0; fillerSetIndex < nodeListFillerSet.getLength(); fillerSetIndex++) {
			
			Element elementFillerSet = (Element) nodeListFillerSet.item(fillerSetIndex);
			
			String group = elementFillerSet.getAttribute("group");
			if (group.isEmpty()) {
				throw new InvalidXmlException("FillerSet " + (fillerSetIndex + 1) + "/" + nodeListFillerSet.getLength() + " is missing a group attribute!");
			}
			
			String name = elementFillerSet.getAttribute("name");
			if (name.isEmpty()) {
				throw new InvalidXmlException("FillerSet " + (fillerSetIndex + 1) + "/" + nodeListFillerSet.getLength() + " is missing a name attribute!");
			}
			
			if (WarpDriveConfig.LOGGING_WORLDGEN) {
				WarpDrive.logger.info("- found FillerSet " + group + ":" + name);
			}
			
			RandomCollection<FillerSet> randomCollection = fillerSetsByGroup.get(group);
			if (randomCollection == null) {
				randomCollection = new RandomCollection<>();
				fillerSetsByGroup.put(group, randomCollection);
			}
			
			FillerSet fillerSet = randomCollection.getNamedEntry(name);
			if (fillerSet == null) {
				fillerSet = new FillerSet(group, name);
			}
			randomCollection.loadFromXML(fillerSet, elementFillerSet);
		}
	}
	
	@SuppressWarnings("Convert2Diamond")
	private static void propagateFillerSets() {
		HashMap<FillerSet, ArrayList<String>> fillerSetsDependencies = new HashMap<>();
		
		// scan for static import dependencies
		for (RandomCollection<FillerSet> fillerSets : fillerSetsByGroup.values()) {
			for (FillerSet fillerSet : fillerSets.elements()) {
				ArrayList<String> dependencies = fillerSetsDependencies.get(fillerSet);
				if (dependencies == null) {
					dependencies = new ArrayList<>();
					fillerSetsDependencies.put(fillerSet, dependencies);
				}
				dependencies.addAll(fillerSet.getImportGroupNames());
			}
		}
		
		// resolve
		int iterationCount = 0;
		while (!fillerSetsDependencies.isEmpty() && iterationCount++ < 10) {
			HashMap<FillerSet, ArrayList<String>> fillerSetsLeftToImport = new HashMap<>();
			
			for (Entry<FillerSet, ArrayList<String>> entry : fillerSetsDependencies.entrySet()) {
				ArrayList<String> newDependencies = new ArrayList<>();
				for (String dependency : entry.getValue()) {
					FillerSet fillerSet = getFillerSet(dependency);
					if (fillerSet == null) {
						WarpDrive.logger.error("Ignoring missing FillerSet " + dependency + " dependency in FillerSet " + entry.getKey());
						
					} else if (fillerSetsDependencies.containsKey(fillerSet)) {
						// skip until it is loaded
						newDependencies.add(dependency);
						
					} else {
						try {
							if (WarpDriveConfig.LOGGING_WORLDGEN) {
								WarpDrive.logger.info("Importing FillerSet " + fillerSet.getFullName() + " in " + entry.getKey().getFullName());
							}
							entry.getKey().loadFrom(fillerSet);
						} catch (InvalidXmlException exception) {
							exception.printStackTrace();
							WarpDrive.logger.error("While importing " + dependency + " into FillerSet " + entry.getKey().getFullName());
						}
					}
				}
				if (!newDependencies.isEmpty()) {
					fillerSetsLeftToImport.put(entry.getKey(), newDependencies);
				}
			}
			
			fillerSetsDependencies = fillerSetsLeftToImport;
		}
		
		// recursion has reach the limit?
		if (!fillerSetsDependencies.isEmpty()) {
			WarpDrive.logger.error("Too many import recursions, ignoring the remaining ones:");
			for (Entry<FillerSet, ArrayList<String>> entry : fillerSetsDependencies.entrySet()) {
				WarpDrive.logger.warn("- FillerSet " + entry.getKey() + " is pending:");
				for (String dependency : entry.getValue()) {
					WarpDrive.logger.warn(" + " + dependency);
				}
			}
		}
	}
	
	public static boolean doesGroupExist(final String groupName) {
		return fillerSetsByGroup.get(groupName) != null;
	}
	
	public static FillerSet getRandomFillerSetFromGroup(Random random, final String groupName) {
		RandomCollection<FillerSet> group = fillerSetsByGroup.get(groupName);
		if (group == null) {
			return null;
		}
		return group.getRandomEntry(random);
	}
	
	public static FillerSet getFillerSet(final String groupAndName) {
		String[] parts = groupAndName.split(":");
		if (parts.length != 2) {
			WarpDrive.logger.error("Invalid FillerSet '" + groupAndName + "'. Expecting '{group}:{name}'");
			return null;
		}
		RandomCollection<FillerSet> group = fillerSetsByGroup.get(parts[0]);
		if (group == null) {
			return null;
		}
		return group.getNamedEntry(parts[1]);
	}
}
