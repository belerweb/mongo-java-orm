package com.googlecode.mjorm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Configuration for a MongoDB MapReduce job.
 */
public class MapReduceConfiguration {

	private String mapFunction;
	private String reduceFunction;
	private String finalizeFunction;

	/**
	 * Creates a {@link MapReduceConfiguration} from the given
	 * xml {@link Document}.
	 * @param doc the document
	 * @throws XPathExpressionException on error
	 */
	public MapReduceConfiguration(Document doc)
		throws XPathExpressionException {
		XPath xpath			= XPathFactory.newInstance().newXPath();
		mapFunction			= getValue(xpath, "/mapreduce/map/text()", doc);
		reduceFunction		= getValue(xpath, "/mapreduce/reduce/text()", doc);
		finalizeFunction	= getValue(xpath, "/mapreduce/finalize/text()", doc);
	}

	/**
	 * Creates a {@link MapReduce} from the config.
	 * @return the {@link MapReduce}
	 */
	public MapReduce createMapReduce() {
		return new MapReduce(
			this.mapFunction,
			this.reduceFunction,
			this.finalizeFunction);
	}

	/**
	 * Returns the value of the given xpath expression.
	 * @param xpath the {@link XPath} object.
	 * @param expression the expression
	 * @param doc the xml {@link Document}
	 * @return the value
	 * @throws XPathExpressionException on error
	 */
	private String getValue(XPath xpath, String expression, Document doc)
		throws XPathExpressionException {
		String val = xpath.evaluate(expression, doc);
		if (val!=null) {
			val = val.replace("^[\\s\\r\\n]", "");
			val = val.replace("[\\s\\r\\n]$", "");
		}
		return (val!=null && val.trim().length()>0) ? val : null;
	}

	/**
	 * Creates a {@link MapReduceConfiguration} from the xml
	 * coming from the given {@link InputStream}.
	 * @param ips the {@link InputStream}
	 * @return the {@link MapReduceConfiguration}
	 * @throws ParserConfigurationException on error
	 * @throws SAXException on error
	 * @throws IOException on error
	 * @throws XPathExpressionException on error
	 */
	public static MapReduceConfiguration create(InputStream ips)
		throws ParserConfigurationException,
		SAXException,
		IOException,
		XPathExpressionException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return new MapReduceConfiguration(builder.parse(ips));
	}

	/**
	 * Creates a {@link MapReduceConfiguration} from the xml
	 * coming from the given {@link File}.
	 * @param file the {@link File}
	 * @return the {@link MapReduceConfiguration}
	 * @throws ParserConfigurationException on error
	 * @throws SAXException on error
	 * @throws IOException on error
	 * @throws XPathExpressionException on error
	 */
	public static MapReduceConfiguration create(File file)
		throws ParserConfigurationException,
		SAXException,
		IOException,
		XPathExpressionException {
		return create(new FileInputStream(file));
	}

	/**
	 * @return the mapFunction
	 */
	public String getMapFunction() {
		return mapFunction;
	}

	/**
	 * @return the reduceFunction
	 */
	public String getReduceFunction() {
		return reduceFunction;
	}

	/**
	 * @return the finalizeFunction
	 */
	public String getFinalizeFunction() {
		return finalizeFunction;
	}

}
