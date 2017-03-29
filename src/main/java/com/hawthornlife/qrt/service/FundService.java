/**
 * 
 */
package com.hawthornlife.qrt.service;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import com.hawthornlife.qrt.domain.Fund;

/**
 * @author Derek Reynolds
 *
 */
public class FundService {

	public Fund build(Document document) {
		
		Fund fund = new Fund();
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		
		try {
			
			XPathExpression expr = xpath.compile("/FundShareClass/Operation/ShareClassBasics/ISIN");
			
			fund.setIsin(expr.evaluate(document));
			
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return fund;
	}
	
}
