/**
 *
 */
package cr0s.warpdrive.config.structures;

import java.util.HashMap;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cr0s.warpdrive.config.InvalidXmlException;
import cr0s.warpdrive.config.IXmlRepresentable;
import net.minecraft.world.gen.feature.WorldGenerator;

/**
 * @author Francesco, LemADEC
 *
 */
public abstract class AbstractStructure extends WorldGenerator implements IXmlRepresentable {
	protected String group;
	protected String name;
	protected HashMap<String,String> variables = new HashMap<>();
	
	public AbstractStructure(final String group, final String name) {
		this.group = group;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return group + ":" + name;
	}
	
	
	abstract public AbstractInstance instantiate(Random random);
	
	@Override
	public boolean loadFromXmlElement(Element element) throws InvalidXmlException {
		
		NodeList nodeListVariables = element.getElementsByTagName("variable");
		for (int variableIndex = 0; variableIndex < nodeListVariables.getLength(); variableIndex++) {
			Element elementVariable = (Element) nodeListVariables.item(variableIndex);
			String variableName = elementVariable.getAttribute("name");
			String variableExpression = elementVariable.getTextContent();
			variables.put(variableName, variableExpression);
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
