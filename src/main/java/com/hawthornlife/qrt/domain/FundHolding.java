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
	
	private Double marketValue = 0.0;
	
	private String quotationCurrencyCode;
	
	private String cic;
	
	private String securityName;
		
	private String moodyRating;
	
	private String couponType;
	
	private String couponFrequency;
	
	private Double couponRate = 0.0;
		
	private String modifiedDuration;
	
	private String yieldToMaturity;
	
	private String maturityDate;
		
	private String settlementDate;
	
	private Double nominalValue = 0.0;
		
	private String interestAccrualConvention;
	
	private String floatingRateNoteIndexBenchmark;
		
	private Double weighting = 0.0;
	
	private Double adjustedWeighting = 0.0;
	
	private String uac;

}
