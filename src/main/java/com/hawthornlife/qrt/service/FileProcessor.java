/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Derek Reynolds
 *
 */
public class FileProcessor {

	private static Logger log = LoggerFactory.getLogger(FileProcessor.class);
	
	public void process(final File file) {
		
		log.debug("Processing file {}", file.getAbsoluteFile());
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		
			Document doc = dBuilder.parse(file);
			
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.error("Error processing file {}", file.getAbsoluteFile());
			e.printStackTrace();
		}
		
	}
	
}