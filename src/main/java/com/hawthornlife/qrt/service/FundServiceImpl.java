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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.hawthornlife.qrt.domain.Fund;


/**
 * This service reads the Fund level data that is needed for the reports.
 * 
 * @author Derek Reynolds
 *
 */
public class FundServiceImpl implements FundService {

	private static Logger log = LoggerFactory.getLogger(FundServiceImpl.class);
	
	private XPathFactory xPathfactory = XPathFactory.newInstance();
	
	private XPath xpath;
	
	
	public FundServiceImpl() {
		xpath = xPathfactory.newXPath();
	}
	
	@Override
	public Fund getFundSummary(final File file) {
		
		log.debug("Entering with {}", file.getAbsoluteFile());
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		
		try {
			
			dBuilder = dbFactory.newDocumentBuilder();
		
			Document document = dBuilder.parse(file);
			
			return createFund(document);
			
		} catch (ParserConfigurationException | SAXException | XPathExpressionException | IOException e) {
			log.error("Error reading file {}", file.getAbsoluteFile());
			log.error("Exception", e);
			throw new XmlFileException("Error reading file: " +  file.getAbsoluteFile());
		}
		
	}

	/* (non-Javadoc)
	 * @see com.hawthornlife.qrt.service.FileService#createFund(org.w3c.dom.Document)
	 */
	private Fund createFund(Document document) throws XPathExpressionException {
		
		Fund fund = new Fund();
		
		fund.setDocument(document);
		fund.setName(getName(document));
		fund.setLegalName(getLegalName(document));
		fund.setIsin(getIsin(document));
		fund.setCountry(getDomicile(document));
		fund.setCountryCode(getDomicileCode(document));
				
		return fund;
	}	
	
	
	private String getName(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
				
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/FundBasics/Name");
		
		return expr.evaluate(document);
	}
	
	private String getLegalName(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
				
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/FundBasics/LegalName");
		
		return expr.evaluate(document);
	}
	
	private String getIsin(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
				
		XPathExpression expr = xpath.compile("/FundShareClass/Operation/ShareClassBasics/ISIN");
		
		return expr.evaluate(document);
	}
	
	private String getDomicile(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
			
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/FundBasics/Domicile");
		
		String country = expr.evaluate(document);
		
		return StringUtils.isBlank(country) ? "N/A" : country;		
		
	}
	
	private String getDomicileCode(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
			
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/FundBasics/Domicile/@_Id");
		
		String countryCode = expr.evaluate(document);
		
		return StringUtils.isBlank(countryCode) ? "N/A" : countryCode;
	}	
	
	
}
