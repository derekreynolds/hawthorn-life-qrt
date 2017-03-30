/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.domain.FundHolding;

import lombok.SneakyThrows;

/**
 * @author Derek Reynolds
 *
 */
public class FundService {

	private XPathFactory xPathfactory = XPathFactory.newInstance();
	
	private XPath xpath;
	
	public FundService() {
		xpath = xPathfactory.newXPath();
	}
	
	public Fund build(Document document) {
		
		Fund fund = new Fund();
		
		fund.setName(getName(document));			
		fund.setIsin(getIsin(document));
		fund.setCountry(getDomicile(document));
		fund.setCountryCode(getDomicileCode(document));
		
		//fund.setFundHoldings(getFundHoldings(document));
		
		return fund;
	}	
	
	
	@SneakyThrows
	private String getName(Document document) {
				
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/FundBasics/Name");
		
		return expr.evaluate(document);
	}
	
	@SneakyThrows
	private String getIsin(Document document) {
				
		XPathExpression expr = xpath.compile("/FundShareClass/Operation/ShareClassBasics/ISIN");
		
		return expr.evaluate(document);
	}
	
	@SneakyThrows
	private String getDomicile(Document document) {
			
		XPathExpression expr = xpath.compile("/FundShareClass/Operation/ShareClassBasics/Domicile");
		
		return expr.evaluate(document);
	}
	
	@SneakyThrows
	private String getDomicileCode(Document document) {
			
		XPathExpression expr = xpath.compile("/FundShareClass/Operation/ShareClassBasics/Domicile/@_Id");
		
		return expr.evaluate(document);
	}
	
	@SneakyThrows
	private SortedMap<String, FundHolding> getFundHoldings(Document document) {
		
		SortedMap<String, FundHolding> fundHoldings = new TreeMap<>();		
		
		XPathExpression expr = xpath.compile("/FundShareClass/Fund/PortfolioList/Portfolio/AggregatedHolding/HoldingDetail");
	
		NodeList holdings = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
		
		for(int i = 0; i < holdings.getLength(); i++) {
			FundHolding fundHolding = getFundHolding(holdings.item(i));
			fundHoldings.put(fundHolding.getIsin(), fundHolding);
		}
				
		return fundHoldings;
	}
	
	@SneakyThrows
	private FundHolding getFundHolding(Node node) {
		
		FundHolding fundHolding = new FundHolding();
		
		XPathExpression expr =  xpath.compile("./Country");
		
		fundHolding.setName(expr.evaluate(node));
		
		expr =  xpath.compile("./Isin");
		
		fundHolding.setIsin(expr.evaluate(node));
		
		return fundHolding;
	}
	
}
