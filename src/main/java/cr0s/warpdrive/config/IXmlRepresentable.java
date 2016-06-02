package cr0s.warpdrive.config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface IXmlRepresentable {
	public String getName();
	
	// Load the XML element, return true if successful
	public boolean loadFromXmlElement(Element element) throws InvalidXmlException;
	
	public void saveToXmlElement(Element element, Document document) throws InvalidXmlException;
}
