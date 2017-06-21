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
		fund.setValuationDate(getValuationDate(document));
		fund.setPortfolioName(getPortfolioName(document));
		fund.setPortfolioCurrency(getPortfolioCurrency(document));
		fund.setIsin(getIsin(document));
		fund.setCountry(getDomicile(document));
		fund.setCountryCode(getDomicileCode(document));
		fund.setFundCustodianCountry(getFundCustodianCountry(document));
		fund.setFundIssuerGroupName(getFundIssuerGroupName(document));
		fund.setFundIssuerCountry(getFundIssuerCountry(document));
		fund.setShareClassCic(getShareClassCic(document));
		fund.setDuration(Double.valueOf(getDuration(document)));
		fund.setLatestNetAssetValutation(Double.valueOf(getLatestNetAssetValuation(document)));
					
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
	
	private String getValuationDate(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
		
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/PortfolioList/Portfolio/PortfolioSummary/Date");
		
		return expr.evaluate(document);
	}
	
	private String getPortfolioName(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
		
		XPathExpression expr = xpath.compile("/FundShareClass/Operation/ShareClassBasics/LegalName");
		
		return expr.evaluate(document);
	}
	
	private String getPortfolioCurrency(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
		
		XPathExpression expr = xpath.compile("/FundShareClass/Operation/ShareClassBasics/Currency");
		
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
	
	private String getFundCustodianCountry(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
			
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/FundManagement/CustodianList/CustodianCompany/Company/Headquarter/CountryHeadquarter[@PrimaryHeadquarter=\"true\"]/Country[@_Id]");
		
		String countryCode = expr.evaluate(document);
		
		return StringUtils.isBlank(countryCode) ? "N/A" : countryCode;
	}	
	
	private String getFundIssuerGroupName(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
			
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/FundManagement/UmbrellaCompany/Company/CompanyOperation/CompanyBasics/Name");
		
		String fundIssuerGroupName = expr.evaluate(document);
		
		return StringUtils.isBlank(fundIssuerGroupName) ? "N/A" : fundIssuerGroupName;
	}	
	
	private String getFundIssuerCountry(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
			
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/FundManagement/Registration/CountryOfRegistration/Company/Headquarter/CountryHeadquarter[@PrimaryHeadquarter=\"true\"]/Country[@_id]");
		
		String countryCode = expr.evaluate(document);
		
		return StringUtils.isBlank(countryCode) ? "N/A" : countryCode;
	}
	
	private String getShareClassCic(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
			
		XPathExpression expr = xpath.compile("/FundShareClass/ProprietaryData/CIC/ShareClassCIC");
		
		String shareClassCic = expr.evaluate(document);
		
		return StringUtils.isBlank(shareClassCic) ? "N/A" : shareClassCic;
	}
	
	private String getDuration(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
			
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/PortfolioList/Portfolio/PortfolioStatistics/BondStatistics/EffectiveDuration");
		
		String duration = expr.evaluate(document);
		
		return StringUtils.isBlank(duration) ? "0.0" : duration;
	}
	
	private String getLatestNetAssetValuation(Document document) throws XPathExpressionException {
		
		log.debug("Entering");
			
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/HistoricalOperation/LatestNetAsset/Value");
		
		String duration = expr.evaluate(document);
		
		return StringUtils.isBlank(duration) ? "0.0" : duration;
	}
	
	
}
