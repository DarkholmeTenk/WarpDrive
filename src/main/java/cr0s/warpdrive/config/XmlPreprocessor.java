package cr0s.warpdrive.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cpw.mods.fml.common.Loader;
import cr0s.warpdrive.WarpDrive;

import javax.xml.transform.dom.DOMSource;


public class XmlPreprocessor {
	static final boolean enableOutput = false;
	static int outputCount = 1;
	
	/**
	 * Check the given element for a mod attribute and return a string of all the ones that are not loaded, separated by commas
	 *
	 * @param element
	 *            Element to check
	 * @return A string, which is empty if all the mods are loaded.
	 */
	public static String checkModRequirements(Element element) {
		
		ModCheckResults modCheckResults = new ModCheckResults();
		
		for (String mod : element.getAttribute("mods").split(",")) {
			
			//TODO: add version check
			
			if (mod.isEmpty()) {
				continue;
			}
			
			if (mod.startsWith("!")) {
				if (Loader.isModLoaded(mod.substring(1))) {
					modCheckResults.addMod(mod, "loaded");
				}
				
			} else if (!Loader.isModLoaded(mod)) {
				modCheckResults.addMod(mod, "not loaded");
			}
		}
		
		return modCheckResults.toString();
	}
	
	/**
	 * Goes through every child node of the given node, and remove elements failing to checkModRequirements()
	 */
	public static void doModReqSanitation(Node base) {
		
		NodeList children = base.getChildNodes();
		
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			
			if (child instanceof Element) {
				Element elementChild = (Element) child;
				String result = checkModRequirements(elementChild);
				if (!result.isEmpty()) {
					WarpDrive.logger.info("Skipping " + base.getNodeName() + "/" + elementChild.getNodeName()
							+ " " + elementChild.getAttribute("group") + elementChild.getAttribute("name") + elementChild.getAttribute("block")
							+ " due to " + result);
					base.removeChild(child);
				} else {
					doModReqSanitation(child);
				}
			}
		}
	}
	
	/**
	 * Develop 'for' elements
	 */
	public static void doLogicPreprocessing(Node root) throws InvalidXmlException {
		// process child first
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			doLogicPreprocessing(children.item(i));
		}
		
		// only process 'for' elements
		if (root.getNodeType() != Node.ELEMENT_NODE || !((Element) root).getTagName().equalsIgnoreCase("for")) {
			return;
		}
		Element elementFor = (Element) root;
		
		// get variable name
		String variableName = elementFor.getAttribute("variable");
		if(variableName.isEmpty()) {
			throw new InvalidXmlException("A for tag must include a variable attribute!");
		}
		
		// 'in' takes precedence over 'from' attribute
		if (elementFor.hasAttribute("in")) {
			String[] inOptions = elementFor.getAttribute("in").split(",");
			
			// copy children with replaced variable
			for(String variableValue : inOptions) {
				if (WarpDriveConfig.LOGGING_WORLDGEN) {
					WarpDrive.logger.info("Resolving for-loop with variable " + variableName + " = " + variableValue);
				}
				NodeList allChildren = root.getChildNodes();
				for(int childIndex = 0; childIndex < allChildren.getLength(); childIndex ++) {
					Node copy = copyNodeAndReplaceVariable(allChildren.item(childIndex), variableName, variableValue);
					root.getParentNode().appendChild(copy);
				}
			}
			
		} else {
			String stringFrom = elementFor.getAttribute("from");
			String stringTo = elementFor.getAttribute("to");
			
			if (stringTo.isEmpty() || stringFrom.isEmpty()) {
				throw new InvalidXmlException("For element with no 'in' attribute requires both 'from' and 'to' attributes! " + variableName);
			}
			
			int intFrom;
			int intTo;
			try {
				intFrom = Integer.parseInt(stringFrom);
				intTo = Integer.parseInt(stringTo);
			} catch (NumberFormatException exception) {
				throw new InvalidXmlException(exception);
			}
			
			
			// copy children with replaced variable
			for (int variableValue = intFrom; variableValue <= intTo; variableValue++) {
				if (WarpDriveConfig.LOGGING_WORLDGEN) {
					WarpDrive.logger.info("Resolving for-loop with variable " + variableName + " = " + variableValue);
				}
				NodeList allChildren = root.getChildNodes();
				for (int childIndex = 0; childIndex < allChildren.getLength(); childIndex++) {
					Node copy = copyNodeAndReplaceVariable(allChildren.item(childIndex), variableName, "" + variableValue);
					root.getParentNode().appendChild(copy);
				}
			}
		}
		
		//Remove the old node
		root.getParentNode().removeChild(root);
		
		if (enableOutput) {
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				Result output = new StreamResult(new File("output" + outputCount + ".xml"));
				Source input = new DOMSource(root.getOwnerDocument());
				
				transformer.transform(input, output);
				outputCount++;
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
	
	private static Node copyNodeAndReplaceVariable(Node nodeOriginal, String variableName, String variableValue) {
		Node nodeCopy = nodeOriginal.cloneNode(true);
		replaceVariable(nodeCopy, "%" + variableName + "%", variableValue);
		
		return nodeCopy;
	}
	
	private static void replaceVariable(Node node, String keyword, String value) {
		ArrayList<String> nameToRemove = new ArrayList<>();
		ArrayList<Attr> attrToAdd = new ArrayList<>();
		
		// process element's attributes first
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			
			// compute the changes
			NamedNodeMap attributes = node.getAttributes();
			for (int indexAttr = 0; indexAttr < attributes.getLength(); indexAttr++) {
				Attr oldAttr = (Attr) attributes.item(indexAttr);
				String oldName = oldAttr.getName();
				String newName = oldName.replace(keyword, value);
				
				if (oldName.equals(newName)) {// same name, just adjust the value
					oldAttr.setValue(oldAttr.getValue().replace(keyword, value));
					
				} else {// different name, needs to defer the add/remove
					nameToRemove.add(oldName);
					
					Attr newAttr = oldAttr.getOwnerDocument().createAttribute(newName);
					newAttr.setValue(oldAttr.getValue().replace(keyword, value));
					attrToAdd.add(newAttr);
				}
			}
			
			// then apply them
			for (String attr : nameToRemove) {
				attributes.removeNamedItem(attr);
			}
			
			for (Attr attr : attrToAdd) {
				attributes.setNamedItem(attr);
			}
		}
		
		// attributes are done, moving through child elements now
		NodeList children = node.getChildNodes();
		for (int childIndex = 0; childIndex < children.getLength(); childIndex++) {
			Node nodeChild = children.item(childIndex);
			
			switch (nodeChild.getNodeType()) {
			case Node.ELEMENT_NODE: // recurse through elements
				replaceVariable(nodeChild, keyword, value);
				break;
			case Node.TEXT_NODE: // replace text in place
				nodeChild.setTextContent(nodeChild.getTextContent().replace(keyword, value));
				break;
			default: // ignore others
				// no operation
				break;
			}
		}
	}
	
	public static class ModCheckResults {
		
		private final TreeMap<String, String> modResults;
		
		public ModCheckResults() {
			modResults = new TreeMap<>();
		}
		
		public void addMod(String name, String error) {
			modResults.put(name, error);
		}
		
		public boolean isEmpty() {
			return modResults.isEmpty();
		}
		
		@Override
		public String toString() {
			String string = (modResults.size() > 1 ? "{" : "");
			boolean isFirst = true;
			for (Entry<String, String> entry : modResults.entrySet()) {
				if (isFirst) {
					isFirst = false;
				} else {
					string += ", ";
				}
				string += entry.getKey() + ": " + entry.getValue();
			}
			
			return string + (modResults.size() > 1 ? "}" : "");
		}
	}
}
