package com.hawthornlife.qrt.domain;

import lombok.Data;

/**
 * Fund holdings for each {@link Fund}
 * 
 * @author Derek Reynolds
 *
 */
public @Data class FundHolding {

	private Integer id;
	
	private String externalId;
	
	private String name;	
	
	private String countryCode;
	
	private String country;
	
	private String localCurrencyCode;
		
	private String assetClass;
	
	private String isin;
	
	private double marketValue = 0.0;
	
	private String quotationCurrencyCode;
	
	private String cic;
	
	private String securityName;
		
	private String moodyRating;
	
	private String couponType;
	
	private String couponFrequency;
	
	private double couponRate = 0.0;
		
	private String modifiedDuration;
	
	private String yieldToMaturity;
	
	private String maturityDate;
		
	private String settlementDate;
	
	private double nominalValue = 0.0;
		
	private String interestAccrualConvention;
	
	private String floatingRateNoteIndexBenchmark;
		
	private double weighting = 0.0;
	
	private double adjustedWeighting = 0.0;
	
	private String uac;

}
