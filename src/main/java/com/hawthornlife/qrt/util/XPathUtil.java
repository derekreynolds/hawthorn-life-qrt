/**
 * 
 */
package com.hawthornlife.qrt.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Derek Reynolds
 *
 */
public class XPathUtil {

	private Logger log = LoggerFactory.getLogger(XPathUtil.class);
	
	public static String EMPTY_STRING_DEFAULT_VALUE = "N/A";
	
	public static String EMPTY_DOUBLE_DEFAULT_VALUE = "0.0";
	
	private XPathFactory xPathfactory = XPathFactory.newInstance();
	
	private XPath xpath;
	

	public XPathUtil() {
		this.xpath = xPathfactory.newXPath();
	}
	
	public String getValue(final Node node, final String xPath, final String defaultValue) throws XPathExpressionException {
		
		log.debug("Entering with path {}, default {}", xPath, defaultValue);
				
		XPathExpression expr = xpath.compile(xPath);
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? defaultValue : value;
	}
	
	
	
}
