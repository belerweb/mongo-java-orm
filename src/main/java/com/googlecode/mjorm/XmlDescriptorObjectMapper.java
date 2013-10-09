package com.googlecode.mjorm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * An {@link ObjectMapper} that reads {@link ObjectDescriptor}s
 * from an XML document for mapping.
 */
public class XmlDescriptorObjectMapper
	extends DescriptorObjectMapper {

	private XmlObjectDescriptorParser xmlObjectDescriptorParser
		= new XmlObjectDescriptorParser();

	/**
	 * Adds the given document configuration.
	 * @param file the {@link File}
	 * @throws IOException on error
	 * @throws ParserConfigurationException on error
	 * @throws SAXException on error
	 * @throws XPathExpressionException on error
	 * @throws ClassNotFoundException on error
	 */
	public void addXmlObjectDescriptor(File file)
		throws XPathExpressionException,
		IOException,
		ParserConfigurationException,
		SAXException,
		ClassNotFoundException {
		assimilateObjectDescriptors(
			xmlObjectDescriptorParser.parseDocument(file));
	}

	/**
	 * Adds the given document configuration.
	 * @param inputStream the {@link InputStream}
	 * @throws IOException on error
	 * @throws ParserConfigurationException on error
	 * @throws SAXException on error
	 * @throws XPathExpressionException on error
	 * @throws ClassNotFoundException on error
	 */
	public void addXmlObjectDescriptor(InputStream inputStream)
		throws XPathExpressionException,
		IOException,
		ParserConfigurationException,
		SAXException,
		ClassNotFoundException {
		assimilateObjectDescriptors(
			xmlObjectDescriptorParser.parseDocument(inputStream));
	}

	/**
	 * Adds the given document configuration.
	 * @param doc the document
	 * @throws XPathExpressionException on error
	 * @throws ClassNotFoundException on error
	 */
	public void addXmlObjectDescriptor(Document doc)
		throws XPathExpressionException,
		ClassNotFoundException {
		assimilateObjectDescriptors(
			xmlObjectDescriptorParser.parseDocument(doc));
	}

	/**
	 * Assimilates {@link ObjectDescriptor}s.
	 * @param descriptors the {@link ObjectDescriptor}s
	 */
	private void assimilateObjectDescriptors(Collection<ObjectDescriptor> descriptors) {
		for (ObjectDescriptor desc : descriptors) {
			registerObjectDescriptor(desc);
		}
	}
}
