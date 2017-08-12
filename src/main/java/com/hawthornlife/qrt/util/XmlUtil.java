/**
 * 
 */
package com.hawthornlife.qrt.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Contains Helper method in relation to XML.
 * @author Derek Reynolds
 *
 */
public class XmlUtil {

	private static  Logger log = LoggerFactory.getLogger(XmlUtil.class);
	
	/**
	 * Gets the {@link Document} from the file.
	 * @param file - the XML file to get the {@link Document} from.
	 * @return a {@link Document}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document getDocument(final File file) throws ParserConfigurationException, SAXException, IOException {
		
		log.debug("Entering with {}", file.getAbsoluteFile());
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		return dBuilder.parse(file);

	}
	
}
