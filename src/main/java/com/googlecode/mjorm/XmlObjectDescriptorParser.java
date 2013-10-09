
package com.googlecode.mjorm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.bson.types.ObjectId;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.googlecode.mjorm.convert.JavaType;

/**
 * Parses XML Object descriptor files and returns
 * {@link ObjectDescriptor}s contained within the file.
 */
public class XmlObjectDescriptorParser {

	private XPath xpath;
	private DocumentBuilder builder;

	/**
	 * Adds the given document configuration.
	 * @param file the {@link File}
	 * @throws IOException on error
	 * @throws ParserConfigurationException on error
	 * @throws SAXException on error
	 * @throws XPathExpressionException on error
	 * @return a {@link List} of {@link ObjectDescriptor}s
	 * @throws ClassNotFoundException on error
	 */
	public List<ObjectDescriptor> parseDocument(File file)
		throws IOException,
		ParserConfigurationException,
		SAXException,
		XPathExpressionException,
		ClassNotFoundException {
		return parseDocument(new FileInputStream(file));
	}

	/**
	 * Adds the given document configuration.
	 * @param inputStream the {@link InputStream}
	 * @throws IOException on error
	 * @throws ParserConfigurationException on error
	 * @throws SAXException on error
	 * @throws XPathExpressionException on error
	 * @return a {@link List} of {@link ObjectDescriptor}s
	 * @throws ClassNotFoundException on error
	 */
	public List<ObjectDescriptor> parseDocument(InputStream inputStream)
		throws IOException,
		ParserConfigurationException,
		SAXException,
		XPathExpressionException,
		ClassNotFoundException {
		if (builder==null) {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		return parseDocument(builder.parse(inputStream));
	}

	/**
	 * Adds the given document configuration.
	 * @param doc the document
	 * @throws XPathExpressionException on error
	 * @throws ClassNotFoundException on error
	 * @return a {@link List} of {@link ObjectDescriptor}s
	 */
	public List<ObjectDescriptor> parseDocument(Document doc)
		throws XPathExpressionException,
		ClassNotFoundException {
		if (xpath==null) {
			xpath = XPathFactory.newInstance().newXPath();
		}

		// create return list
		List<ObjectDescriptor> ret = new ArrayList<ObjectDescriptor>();

		// get descriptor elements
		NodeList descriptorEls = (NodeList)xpath.evaluate(
			"/descriptors/object", doc, XPathConstants.NODESET);

		// loop through each element
		for (int i=0; i<descriptorEls.getLength(); i++) {

			// parse the descriptor
			ObjectDescriptor descriptor = parseDescriptor(
				Element.class.cast(descriptorEls.item(i)), ret);
			ret.add(descriptor);
		}

		// return the list
		return ret;
	}

	/**
	 * Parses an object descriptor element.
	 * @param descriptorEl
	 * @param descriptors
	 * @return
	 * @throws XPathExpressionException
	 * @throws ClassNotFoundException
	 */
	private ObjectDescriptor parseDescriptor(Element descriptorEl, List<ObjectDescriptor> descriptors)
		throws XPathExpressionException,
		ClassNotFoundException {

		// create a descriptor
		ObjectDescriptor descriptor = new ObjectDescriptor();

		// get descriptor properties
		Class<?> objClass = Class.forName(descriptorEl.getAttribute("class"));
		String discriminatorName = descriptorEl.hasAttribute("discriminator-name")
				? descriptorEl.getAttribute("discriminator-name") : null;
		String discriminatorType = descriptorEl.hasAttribute("discriminator-type")
				? descriptorEl.getAttribute("discriminator-type") : null;

		// populate the descriptor
		descriptor.setType(objClass);
		descriptor.setDiscriminatorName(discriminatorName);
		descriptor.setDiscriminatorType(discriminatorType);

		// get properties
		NodeList propertyEls = (NodeList)xpath.evaluate(
			"./property", descriptorEl, XPathConstants.NODESET);
		boolean foundIdentifier = false;
		for (int i=0; i<propertyEls.getLength(); i++) {

			// get element
			Element propertyEl = (Element)propertyEls.item(i);

			// get property name and type
			String propName = propertyEl.getAttribute("name");
			Type propType = propertyEl.hasAttribute("class")
				? Class.forName(propertyEl.getAttribute("class")) : null;
			Type storageType = propertyEl.hasAttribute("storageClass")
				? Class.forName(propertyEl.getAttribute("storageClass")) : null;
			boolean propIsIdentifier = propertyEl.hasAttribute("id")
				&& Boolean.parseBoolean(propertyEl.getAttribute("id"));

			// value generator
			boolean propIsAutoGen = false;
			ValueGenerator<?> valueGenerator = null;
			if (propertyEl.hasAttribute("auto")) {
				propIsAutoGen = true;
				String value = propertyEl.getAttribute("auto");
				if (value.toLowerCase().trim().equals("true")) {
					valueGenerator = ObjectIdValueGenerator.INSTANCE;
					storageType =  ObjectId.class;
				} else {
					try {
						valueGenerator = ValueGenerator.class.cast(
							Class.forName(value).newInstance());
					} catch(Exception e) {
						throw new IllegalArgumentException(
							"Unable to create ValueGenerator for "+value, e);
					}
				}
			}

			// get property field
			String propField = propertyEl.hasAttribute("field")
				? propertyEl.getAttribute("field") : null;
			if (propField==null && propertyEl.hasAttribute("column")) { // backwards compatibility
				propField = propertyEl.getAttribute("column");
			}
			if (propField==null) {
				propField = propIsIdentifier ? "_id" : propName;
			}

			// find the getter and setter.
			Method propSetter = ReflectionUtil.findSetter(objClass, propName);
			Method propGetter = ReflectionUtil.findGetter(objClass, propName);
			if (propGetter==null || propSetter==null) {
				throw new IllegalArgumentException(
					"Unable to find getter or setter named "+propName+" for: "+objClass);
			}

			// make sure we have the type and get
			// the generic type if there is one
			if (propIsIdentifier && !foundIdentifier) {
				foundIdentifier = true;
			} else if (propIsIdentifier && foundIdentifier) {
				throw new IllegalArgumentException(
					"Two identifiers found for: "+objClass);
			}

			// get parameter types
			NodeList parameterTypeEls = (NodeList)xpath.evaluate(
				"./type-param", propertyEl, XPathConstants.NODESET);
			Class<?>[] genericParameterTypes = new Class<?>[parameterTypeEls.getLength()];
			for (int k=0; k<parameterTypeEls.getLength(); k++) {
				Element parameterTypeEl = (Element)parameterTypeEls.item(k);
				genericParameterTypes[k] = Class.forName(parameterTypeEl.getAttribute("class"));
			}

			// get conversion hints
			NodeList conversionHints = (NodeList)xpath.evaluate(
				"./conversion-hints/hint", propertyEl, XPathConstants.NODESET);
			if (conversionHints.getLength()==0) {
				conversionHints = (NodeList)xpath.evaluate(
					"./translation-hints/hint", propertyEl, XPathConstants.NODESET);
			}
			Map<String, Object> hints = new HashMap<String, Object>();
			for (int k=0; k<conversionHints.getLength(); k++) {
				Element hintEl = (Element)conversionHints.item(k);
				hints.put(hintEl.getAttribute("name"), hintEl.getTextContent());
			}

			// makes sure we have a type
			if (propType==null) {
				propType = propGetter.getGenericReturnType();
			}

			// create the PropertyDescriptor
			PropertyDescriptor prop = new PropertyDescriptor();
			prop.setName(propName);
			prop.setFieldName(propField);
			prop.setGetter(propGetter);
			prop.setSetter(propSetter);
			prop.setIdentifier(propIsIdentifier);
			prop.setType(JavaType.fromType(propType));
			prop.setAutoGenerated(propIsAutoGen);
			prop.setValueGenerator(valueGenerator);
			prop.setConversionHints(hints);
			prop.setGenericParameterTypes(genericParameterTypes);

			// set the storage type
			if (storageType!=null) {
				prop.setStorageType(JavaType.fromType(storageType));
			}

			// add to the object descriptor
			descriptor.addPropertyDescriptor(prop);
		}

		// parse subclasses
		NodeList subClassEls = (NodeList)xpath.evaluate(
			"./subclass", descriptorEl, XPathConstants.NODESET);
		for (int i=0; i<subClassEls.getLength(); i++) {

			// get element
			Element subClassEl = (Element)subClassEls.item(i);

			// get discriminator value
			Object discriminatorValue = MappingUtil.parseDiscriminator(
				subClassEl.getAttribute("discriminator-value"), discriminatorType);

			// parse sub class
			ObjectDescriptor subClass = parseDescriptor(subClassEl, descriptors);

			// add subclass
			descriptor.addSubClassObjectDescriptor(discriminatorValue, subClass);
			descriptors.add(subClass);
		}

		// return it
		return descriptor;
	}

}
