/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.domain.FundHolding;

import lombok.SneakyThrows;

/**
 * @author Derek Reynolds
 *
 */
public class FundHoldingServiceImpl implements FundHoldingService {

	private static Logger log = LoggerFactory.getLogger(FundHoldingServiceImpl.class);
	
	private XPathFactory xPathfactory = XPathFactory.newInstance();
	
	private XPath xpath;
	
	
	public FundHoldingServiceImpl() {
		xpath = xPathfactory.newXPath();
	}
		
	
	@Override
	public SortedMap<Integer, FundHolding> getFundHoldings(final Fund fund) {
		
		log.debug("Entering with {} - {}", fund.getLegalName(), fund.getIsin());
		
		return createFundHoldings(fund);
	}


	@SneakyThrows
	private SortedMap<Integer, FundHolding> createFundHoldings(Fund fund) {
		
		log.debug("Entering");
		
		SortedMap<Integer, FundHolding> fundHoldings = new TreeMap<>();		
		
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/PortfolioList/Portfolio/AggregatedHolding/HoldingDetail");
	
		NodeList holdings = (NodeList)expr.evaluate(fund.getDocument(), XPathConstants.NODESET);
		
		for(int i = 0; i < holdings.getLength(); i++) {
			FundHolding fundHolding = getFundHolding(holdings.item(i));			
			fundHoldings.put(fundHolding.getId(), fundHolding);
		}
				
		return fundHoldings;
	}
	
	@SneakyThrows
	private FundHolding getFundHolding(Node node) {
		
		log.debug("Entering");
		
		FundHolding fundHolding = new FundHolding();
			
		fundHolding.setId(Integer.valueOf(getId(node)));
		fundHolding.setExternalId(getExternalId(node));
		fundHolding.setName(getName(node));
		fundHolding.setCountry(getCountry(node));	
		fundHolding.setCountryCode(getCountryCode(node));
		fundHolding.setMarketValue(Double.valueOf(getMarketValue(node)));
		fundHolding.setWeighting(Double.valueOf(getWeighting(node)));
		// Adjust the weighting: negative if market value is negative
		// Also, convert to percent as we us this for calculation.
		fundHolding.setAdjustedWeighting((fundHolding.getMarketValue() > 0.0 
				? fundHolding.getWeighting() 
				: fundHolding.getWeighting() * -1) / 100);
		fundHolding.setAssetClass(getAssetClass(node));
		fundHolding.setLocalCurrencyCode(getLocalCurrencyCode(node));
		
		return fundHolding;
	}
	
	@SneakyThrows
	private String getName(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./@ExternalName");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getCountry(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./Country");
		
		String country = expr.evaluate(node);
		
		return StringUtils.isBlank(country) ? "N/A" : country;
	}
	
	@SneakyThrows
	private String getCountryCode(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./Country/@_Id");
		
		String countryCode = expr.evaluate(node);
		
		return StringUtils.isBlank(countryCode) ? "N/A" : countryCode;
	}
		
	@SneakyThrows
	private String getId(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./@_StorageId");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getExternalId(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./@_ExternalId");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getWeighting(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./Weighting");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getMarketValue(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./MarketValue");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getAssetClass(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./UAC");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getLocalCurrencyCode(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./LocalCurrencyCode");
		
		String localCurrencyCode = expr.evaluate(node);
		
		return StringUtils.isBlank(localCurrencyCode) ? "N/A" : localCurrencyCode;
	}

	
}
