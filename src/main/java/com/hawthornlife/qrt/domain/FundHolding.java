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
	
	private Double localMarketValue = 0.0;
	
	private Double couponRate = 0.0;
	
	private String cic;
	
	private String securityName;
	
	private String maturityDate;
	
	private String moodyRating;
	
	private String couponType;
	
	private String couponFrequency;
	
	private String callable;
	
	private String puttable;
	
	private String ediIssuerName;
	
	private String ediIssuerId;
	
	private String modifiedDuration;
	
	private String yieldToMaturity;
		
	private String settlementDate;
	
	private String primaryExchange;
	
	private Double accruedInterest = 0.0;
	
	private Double yieldToCall = 0.0;
	
	private Double yieldToPut = 0.0;
	
	private Double effectiveDuration = 0.0;
	
	private Double macaulayDuration = 0.0;
	
	private Double convexity = 0.0;
	
	private String firstCouponDate;
	
	private Double nominalValue = 0.0;
	
	private String issueDate;
	
	private Double outstandingAmount = 0.0;
	
	private String interestCommencementDate;
	
	private String interestAccrualConvention;
	
	private String floatingRateNoteIndexBenchmark;
	
	private String perpetual;
	
	private String maturityPriceAsAPercent;
	
	private String maturityStructure;
	
	private Double weighting = 0.0;
	
	private Double adjustedWeighting = 0.0;
	
	private String uac;

}
