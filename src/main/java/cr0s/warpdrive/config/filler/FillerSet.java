package cr0s.warpdrive.config.filler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.init.Blocks;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.config.InvalidXmlException;
import cr0s.warpdrive.config.RandomCollection;
import cr0s.warpdrive.config.IXmlRepresentable;

/**
 * Represents a set of fillers.
 **/
public class FillerSet implements IXmlRepresentable, Comparable {
	protected String group;
	protected String name;
	private RandomCollection<Filler> fillers;
	private ArrayList<String> importGroupNames;
	private ArrayList<String> importGroups;
	
	public String getFullName() {
		return group + ":" + name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public FillerSet(final String group, final String name) {
		this.group = group;
		this.name = name;
		fillers = new RandomCollection<>();
		importGroupNames = new ArrayList<>();
		importGroups = new ArrayList<>();
	}
	
	public boolean isEmpty() {
		return fillers.isEmpty();
	}
	
	public Filler getRandomBlock(Random random) {
		Filler filler = fillers.getRandomEntry(random);
		if (filler == null) {
			WarpDrive.logger.error("null filler encountered in FillerSet " + getFullName());
			filler = new Filler();
			filler.block = Blocks.glass;
		}
		return filler;
	}
	
	@Override
	public boolean loadFromXmlElement(Element element) throws InvalidXmlException {
		NodeList nodeListFillers = element.getElementsByTagName("filler");
		for (int i = 0; i < nodeListFillers.getLength(); i++) {
			
			Element elementFiller = (Element) nodeListFillers.item(i);
			
			Filler filler = new Filler();
			fillers.loadFromXML(filler, elementFiller);
		}
		
		NodeList nodeListImports = element.getElementsByTagName("import");
		if (nodeListImports.getLength() > 0) { 
			for (int importIndex = 0; importIndex < nodeListImports.getLength(); importIndex++) {
				Element elementImport = (Element) nodeListImports.item(importIndex);
				String importGroup = elementImport.getAttribute("group");
				String importName = elementImport.getAttribute("name");
				if (!importGroup.isEmpty()) {
					if (!importName.isEmpty()) {
						importGroupNames.add(importGroup + ":" + importName);
					} else {
						importGroups.add(importGroup);
					}
				} else {
					WarpDrive.logger.warn("Ignoring import with no group definition in import element from " + getFullName());
				}
			}
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
	
	@Override
	public int compareTo(Object object) {
		return name.compareTo(((FillerSet) object).name);
	}
	
	@Override
	public String toString() {
		return getFullName() + "(" + (fillers == null ? "-empty-" : fillers.elements().size()) + ")";
	}
	
	/**
	 * Adds the blocks from the given fillerSet into this one. Must be pre-finishConstruction()
	 *
	 * @param fillerSet
	 *            The fillerSet to add from
	 */
	public void loadFrom(FillerSet fillerSet) throws InvalidXmlException {
		fillers.loadFrom(fillerSet.fillers);
	}
	
	/**
	 * Return static import dependencies
	 * 
	 * @return null or a list of group:names to be imported
	 **/
	public Collection<String> getImportGroupNames() {
		return importGroupNames; 
	}
	
	/**
	 * Return dynamic import dependencies
	 * 
	 * @return null or a list of groups to be imported
	 **/
	public Collection<String> getImportGroups() {
		return importGroups; 
	}
}
