/**
 * 
 */
package com.hawthornlife.qrt.domain;

import java.util.SortedMap;
import java.util.TreeMap;

import lombok.Data;


/**
 * This class contains the breakdown of all the detail pertaining to a fund.
 * 
 * @author Derek Reynolds
 *
 */

public @Data class Fund {

	private String name;
	
	private String isin;
	
	private String countryCode;
	
	private String country;
	
	private Double assetUnderManagement;

	private SortedMap<String, FundHolding> fundHoldings = new TreeMap<>();
	
	
	public void add(String isin, FundHolding fundHolding) {
		fundHoldings.put(isin, fundHolding);
	}
}
