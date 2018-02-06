/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringEscapeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.domain.FundHolding;
import com.hawthornlife.qrt.util.XPathUtil;
import com.hawthornlife.qrt.util.XmlUtil;

import lombok.SneakyThrows;

/**
 * @author Derek Reynolds
 *
 */
public class FundHoldingServiceImpl implements FundHoldingService {

	private static Logger log = LoggerFactory.getLogger(FundHoldingServiceImpl.class);
	
	private XPathFactory xPathfactory = XPathFactory.newInstance();
	
	private XPath xpath;
	
	private XPathUtil xPathUtil = new XPathUtil();
	
	private Document document;
	
	private Node portfolioHoldingNode;
	
	
	public FundHoldingServiceImpl() {
		xpath = xPathfactory.newXPath();
	}
		
	
	@Override
	public SortedMap<Integer, FundHolding> getFundHoldings(final Fund fund) {
		
		log.debug("Entering with {} - {}", fund.getLegalName(), fund.getIsin());
				
		return adjust(createFundHoldings(fund));
	}


	@SneakyThrows
	private SortedMap<Integer, FundHolding> createFundHoldings(Fund fund) {
		
		log.debug("Entering");		

		document = XmlUtil.getDocument(fund.getFile());
		
		SortedMap<Integer, FundHolding> fundHoldings = new TreeMap<>();		
		
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/PortfolioList/Portfolio/AggregatedHolding/HoldingDetail");
	
		NodeList holdings = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
		
		for(int i = 0; i < holdings.getLength(); i++) {
			FundHolding fundHolding = getFundHolding(fund, holdings.item(i).cloneNode(true));			
			fundHoldings.put(fundHolding.getId(), fundHolding);
		}
				
		return fundHoldings;
	}
	
	@SneakyThrows
	private Optional<Node> getPortfolioHoldingDetailNode(Fund fund, String externalName) {
		
		log.debug("Entering with {} - {}", fund.getName(), externalName);	
		
		if(portfolioHoldingNode == null)
			portfolioHoldingNode = getPortfolioHoldingNode(fund);
		
		XPathExpression expr = xpath.compile("./HoldingDetail[@ExternalName=\""+ StringEscapeUtils.escapeXml11(externalName) + "\"]");
		
		NodeList holdings = (NodeList)expr.evaluate(portfolioHoldingNode, XPathConstants.NODESET);		
		
		return holdings.getLength() > 0 ? Optional.of(holdings.item(0).cloneNode(true)) : Optional.empty();
		
	}
	
	@SneakyThrows
	private Node getPortfolioHoldingNode(Fund fund) {
		
		log.debug("Entering with {} - {}", fund.getName());		
		
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/PortfolioList/Portfolio/Holding");
		
		return (Node) expr.evaluate(document, XPathConstants.NODE);		
				
	}
	
	
	@SneakyThrows
	private FundHolding getFundHolding(Fund fund, Node node) {
		
		log.debug("Entering");
		
		FundHolding fundHolding = new FundHolding();
			
		fundHolding.setId(Integer.valueOf(xPathUtil.getValue(node, "./@_StorageId", XPathUtil.EMPTY_STRING_DEFAULT_VALUE)));
		fundHolding.setExternalId(xPathUtil.getValue(node, "./@_ExternalId", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setName(xPathUtil.getValue(node, "./@ExternalName", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setCountry(xPathUtil.getValue(node, "./Country", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));	
		fundHolding.setCountryCode(xPathUtil.getValue(node, "./Country/@_Id", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setLocalCurrencyCode(xPathUtil.getValue(node, "./LocalCurrencyCode", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setCic(xPathUtil.getValue(node, "./CIC", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setSecurityName(xPathUtil.getValue(node, "./SecurityName", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setIsin(xPathUtil.getValue(node, "./ISIN", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setMarketValue(Double.valueOf(xPathUtil.getValue(node, "./MarketValue", XPathUtil.EMPTY_DOUBLE_DEFAULT_VALUE)));
		fundHolding.setWeighting(Double.valueOf(xPathUtil.getValue(node, "./Weighting", XPathUtil.EMPTY_DOUBLE_DEFAULT_VALUE)));
		// Adjust the weighting: negative if market value is negative
		// Also, convert to percent as we us this for calculation.
		fundHolding.setAdjustedWeighting((fundHolding.getMarketValue() > 0.0 
				? fundHolding.getWeighting() 
				: fundHolding.getWeighting() * -1) / 100);
		fundHolding.setAssetClass(xPathUtil.getValue(node, "./UAC", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setCouponType(xPathUtil.getValue(node, "./CouponType", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setCouponFrequency(xPathUtil.getValue(node, "./CouponFrequency", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setCouponRate(Double.valueOf(xPathUtil.getValue(node, "./Coupon", XPathUtil.EMPTY_DOUBLE_DEFAULT_VALUE)));
		fundHolding.setSettlementDate(xPathUtil.getValue(node, "./SettlementDate", XPathUtil.EMPTY_STRING_DEFAULT_VALUE)); 
		fundHolding.setMoodyRating(xPathUtil.getValue(node, "./MoodyRating", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setModifiedDuration(xPathUtil.getValue(node, "./ModifiedDuration", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setYieldToMaturity(xPathUtil.getValue(node, "./YieldtoMaturity", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setMaturityDate(xPathUtil.getValue(node, "./MaturityDate", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setNominalValue(Double.valueOf(xPathUtil.getValue(node, "./NominalValue", XPathUtil.EMPTY_DOUBLE_DEFAULT_VALUE)));
		fundHolding.setInterestAccrualConvention(xPathUtil.getValue(node, "./InterestAccrualConvention", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setFloatingRateNoteIndexBenchmark(xPathUtil.getValue(node, "./FrnIndexBenchmark", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		fundHolding.setUac(xPathUtil.getValue(node, "./UAC", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		
		Optional<Node> portfolioNode = getPortfolioHoldingDetailNode(fund, fundHolding.getName());
		
		if(portfolioNode.isPresent()) {
			fundHolding.setQuotationCurrencyCode(xPathUtil.getValue(portfolioNode.get(), "./LocalCurrencyCode", XPathUtil.EMPTY_STRING_DEFAULT_VALUE));
		}
		
		
		return fundHolding;
	}

	
	
	private SortedMap<Integer, FundHolding> adjust(final SortedMap<Integer, FundHolding> fundHoldings) {
		
		log.debug("Entering");
		
		if(fundHoldings.isEmpty()) {
			log.info("No fund holdings found");
			return fundHoldings;
		}
			
		
		double totalValue = fundHoldings.values()
				.stream()
				.mapToDouble(fh -> fh.getAdjustedWeighting()).sum();
		
		if(totalValue < 1.0) {
			
			double delta = 1.0 - totalValue;			

			log.debug("Delta {}", delta);			
			
			FundHolding fundHolding = Collections.max(fundHoldings.values(), 
					Comparator.comparing(fh -> fh.getAdjustedWeighting()));
			
			log.debug("Adjusting fund {} with weight {}", fundHolding.getId(), fundHolding.getAdjustedWeighting());
			
			fundHolding.setAdjustedWeighting(fundHolding.getAdjustedWeighting() + delta);
			
			log.debug("New weight {}", fundHolding.getAdjustedWeighting());
			
		}
		
		return fundHoldings;
	}
	
}
