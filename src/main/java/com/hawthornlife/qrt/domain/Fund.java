/**
 * 
 */
package com.hawthornlife.qrt.domain;


import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Document;

import lombok.Data;


/**
 * This class contains the breakdown of all the detail pertaining to a fund.
 * 
 * @author Derek Reynolds
 *
 */

public @Data class Fund {

	private Document document;
	
	private String name;
	
	private String legalName;
	
	private String isin;
	
	private String countryCode;
	
	private String country;
	
	private Double assetUnderManagement = 0.0;

	private SortedMap<Integer, FundHolding> fundHoldings = new TreeMap<>();
	

}
