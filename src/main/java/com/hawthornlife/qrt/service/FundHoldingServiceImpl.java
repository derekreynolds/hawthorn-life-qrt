/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.util.Collections;
import java.util.Comparator;
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
		
		return adjust(createFundHoldings(fund));
	}


	@SneakyThrows
	private SortedMap<Integer, FundHolding> createFundHoldings(Fund fund) {
		
		log.debug("Entering");
		
		SortedMap<Integer, FundHolding> fundHoldings = new TreeMap<>();		
		
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/PortfolioList/Portfolio/AggregatedHolding/HoldingDetail");
	
		NodeList holdings = (NodeList)expr.evaluate(fund.getDocument(), XPathConstants.NODESET);
		
		for(int i = 0; i < holdings.getLength(); i++) {
			FundHolding fundHolding = getFundHolding(holdings.item(i).cloneNode(true));			
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
		fundHolding.setCic(getCic(node));
		fundHolding.setSecurityName(getSecurityName(node));
		fundHolding.setIsin(getIsin(node));
		fundHolding.setMarketValue(Double.valueOf(getMarketValue(node)));
		fundHolding.setWeighting(Double.valueOf(getWeighting(node)));
		// Adjust the weighting: negative if market value is negative
		// Also, convert to percent as we us this for calculation.
		fundHolding.setAdjustedWeighting((fundHolding.getMarketValue() > 0.0 
				? fundHolding.getWeighting() 
				: fundHolding.getWeighting() * -1) / 100);
		fundHolding.setAssetClass(getAssetClass(node));
		fundHolding.setLocalCurrencyCode(getLocalCurrencyCode(node));
		fundHolding.setLocalMarketValue(Double.valueOf(getLocalMarketValue(node)));
		fundHolding.setCouponRate(Double.valueOf(getCouponRate(node)));
		fundHolding.setCouponType(getCouponType(node));
		fundHolding.setCouponFrequency(getCouponFrequency(node));
		fundHolding.setFirstCouponDate(getFirstCouponDate(node));
		fundHolding.setMaturityDate(getMaturityDate(node));
		fundHolding.setSettlementDate(getSettlementDate(node));
		fundHolding.setMoodyRating(getMoodyRating(node));
		fundHolding.setCallable(getCallable(node));
		fundHolding.setPuttable(getPuttable(node));
		fundHolding.setEdiIssuerName(getEdiIssuerName(node));
		fundHolding.setEdiIssuerId(getEdiIssuerId(node));
		fundHolding.setModifiedDuration(getModifiedDuration(node));
		fundHolding.setYieldToMaturity(getYieldToMaturity(node));
		fundHolding.setPrimaryExchange(getPrimaryExchange(node));
		fundHolding.setAccruedInterest(Double.valueOf(getAccruedInterest(node)));
		fundHolding.setYieldToCall(Double.valueOf(getYieldToCall(node)));
		fundHolding.setYieldToPut(Double.valueOf(getYieldToPut(node)));
		fundHolding.setEffectiveDuration(Double.valueOf(getEffectiveDuration(node)));
		fundHolding.setMacaulayDuration(Double.valueOf(getMacaulayDuration(node)));
		fundHolding.setConvexity(Double.valueOf(getConvexity(node)));
		fundHolding.setNominalValue(Double.valueOf(getNominalValue(node)));
		fundHolding.setIssueDate(getIssueDate(node));
		fundHolding.setOutstandingAmount(Double.valueOf(getOutstandingAmount(node)));
		fundHolding.setInterestAccrualConvention(getInterestAccrualConvention(node));
		fundHolding.setInterestCommencementDate(getInterestCommencementDate(node));
		fundHolding.setFloatingRateNoteIndexBenchmark(getFloatingRateNoteIndexBenchmark(node));
		fundHolding.setPerpetual(getPerpetual(node));
		fundHolding.setMaturityPriceAsAPercent(getMaturityPriceAsAPercent(node));
		fundHolding.setMaturityStructure(getMaturityStructure(node));
		fundHolding.setUac(getUac(node));
		
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
	private String getCic(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./CIC");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getSecurityName(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./SecurityName");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getIsin(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./ISIN");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getLocalCurrencyCode(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./LocalCurrencyCode");
		
		String localCurrencyCode = expr.evaluate(node);
		
		return StringUtils.isBlank(localCurrencyCode) ? "N/A" : localCurrencyCode;
	}
	
	@SneakyThrows
	private String getLocalMarketValue(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr = xpath.compile("./LocalMarketValue");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
	}
	
	@SneakyThrows
	private String getCouponRate(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./Coupon");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
	}
	
	@SneakyThrows
	private String getCouponType(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./CouponType");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getCouponFrequency(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./CouponFrequency");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getFirstCouponDate(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./FirstCouponDate");
		
		return expr.evaluate(node);
	}
	
	
	@SneakyThrows
	private String getMaturityDate(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./MaturityDate");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getMoodyRating(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./MoodyRating");
		
		return expr.evaluate(node);
	}
	
	@SneakyThrows
	private String getCallable(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./Callable");
		
		String callable = expr.evaluate(node);
		
		return StringUtils.isBlank(callable) ? "N/A" : callable;
	}
	
	@SneakyThrows
	private String getPuttable(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./Puttable");
		
		String puttable = expr.evaluate(node);
		
		return StringUtils.isBlank(puttable) ? "N/A" : puttable;
	}
	
	
	
	@SneakyThrows
	private String getEdiIssuerName(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./EDIIssuerName");
		
		return expr.evaluate(node);

	}
	
	@SneakyThrows
	private String getEdiIssuerId(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./EDIIssuerName/@_Id");
		
		return expr.evaluate(node);

	}
	
	@SneakyThrows
	private String getModifiedDuration(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./ModifiedDuration");
		
		return expr.evaluate(node);
		
	}
	
	@SneakyThrows
	private String getYieldToMaturity(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./YieldtoMaturity");
		
		return expr.evaluate(node);
		
	}
	
	@SneakyThrows
	private String getSettlementDate(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./SettlementDate");
		
		return expr.evaluate(node);
		
	}
	
	@SneakyThrows
	private String getPrimaryExchange(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./PrimaryExchange");
		
		return expr.evaluate(node);
		
	}
	
	@SneakyThrows
	private String getAccruedInterest(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./AccruedInterest");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}
	
	@SneakyThrows
	private String getYieldToCall(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./YieldtoCall");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}

	@SneakyThrows
	private String getYieldToPut(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./YieldtoPut");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}
	
	@SneakyThrows
	private String getEffectiveDuration(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./EffectiveDuration");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}
	
	@SneakyThrows
	private String getMacaulayDuration(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./MacaulayDuration");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}
	
	@SneakyThrows
	private String getConvexity(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./Convexity");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}
	
	@SneakyThrows
	private String getNominalValue(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./NominalValue");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}
	
	@SneakyThrows
	private String getIssueDate(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./IssueDate");
		
		return expr.evaluate(node);
		
	}
	
	@SneakyThrows
	private String getInterestAccrualConvention(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./InterestAccrualConvention");
		
		return expr.evaluate(node);
		
	}
	
	@SneakyThrows
	private String getOutstandingAmount(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./OutstandingAmount");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}
	
	@SneakyThrows
	private String getInterestCommencementDate(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./IntCommencementDate");
		
		return expr.evaluate(node);
		
	}
	
	@SneakyThrows
	private String getFloatingRateNoteIndexBenchmark(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr =  xpath.compile("./FrnIndexBenchmark");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}
	
	@SneakyThrows
	private String getPerpetual(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr = xpath.compile("./Perpetual");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}
	
	@SneakyThrows
	private String getMaturityPriceAsAPercent(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr = xpath.compile("./MatPriceAsPercent");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "0.0" : value;
		
	}
	
	@SneakyThrows
	private String getMaturityStructure(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr = xpath.compile("./MaturityStructure");
		
		return expr.evaluate(node);
		
	}
	
	@SneakyThrows
	private String getUac(Node node) {
		
		log.debug("Entering");
		
		XPathExpression expr = xpath.compile("./UAC");
		
		String value = expr.evaluate(node);
		
		return StringUtils.isBlank(value) ? "N/A" : value;
		
	}
	
	private SortedMap<Integer, FundHolding> adjust(final SortedMap<Integer, FundHolding> fundHoldings) {
		
		log.debug("Entering");
		
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
