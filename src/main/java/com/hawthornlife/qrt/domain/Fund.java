/**
 * 
 */
package com.hawthornlife.qrt.domain;


import java.io.File;
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

	private File file;
	
	private String name;
	
	private String legalName;
	
	private String valuationDate;
	
	private String portfolioName;
	
	private String portfolioCurrency;
			
	private String isin;
	
	private String countryCode;
	
	private String country;
	
	private String shareClassCic;
	
	private Double assetUnderManagement = 0.0;
	
	private Double duration = 0.0;
	
	private Double latestNetAssetValutation = 0.0;

	private SortedMap<Integer, FundHolding> fundHoldings = new TreeMap<>();
	

}
