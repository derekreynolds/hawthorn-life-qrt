/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.util.XPathUtil;


/**
 * This service reads the Fund level data that is needed for the reports.
 * 
 * @author Derek Reynolds
 *
 */
public class FundServiceImpl implements FundService {

	private static Logger log = LoggerFactory.getLogger(FundServiceImpl.class);
	
	private XPathUtil xPathUtil = new XPathUtil();

	
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
		fund.setName(xPathUtil.getValue(document, "/FundShareClass/Fund/FundBasics/Name", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fund.setLegalName(xPathUtil.getValue(document, "/FundShareClass/Fund/FundBasics/LegalName", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fund.setValuationDate(xPathUtil.getValue(document, "/FundShareClass/Fund/PortfolioList/Portfolio/PortfolioSummary/Date", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fund.setPortfolioName(xPathUtil.getValue(document, "/FundShareClass/Operation/ShareClassBasics/LegalName", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fund.setPortfolioCurrency(xPathUtil.getValue(document, "/FundShareClass/Operation/ShareClassBasics/Currency", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fund.setIsin(xPathUtil.getValue(document, "/FundShareClass/Operation/ShareClassBasics/ISIN", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fund.setCountry(xPathUtil.getValue(document, "/FundShareClass/Fund/FundBasics/Domicile", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fund.setCountryCode(xPathUtil.getValue(document, "/FundShareClass/Fund/FundBasics/Domicile/@_Id", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fund.setShareClassCic(xPathUtil.getValue(document, "/FundShareClass/ProprietaryData/CIC/ShareClassCIC", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fund.setDuration(Double.valueOf(xPathUtil.getValue(document, "/FundShareClass/Fund/PortfolioList/Portfolio/PortfolioStatistics/BondStatistics/EffectiveDuration", XPathUtil.EMPTY_DOUBLE_DEFAULT_VALUE)));
		fund.setLatestNetAssetValutation(Double.valueOf(xPathUtil.getValue(document, "/FundShareClass/Fund/HistoricalOperation/LatestNetAsset/Value", XPathUtil.EMPTY_DOUBLE_DEFAULT_VALUE)));
					
		return fund;
	}	
	
	
}
