package com.hawthornlife.qrt.domain;

import lombok.Data;

public @Data class FundHolding {

	private Integer id;
	
	private String externalId;
	
	private String name;	
	
	private String countryCode;
	
	private String country;
	
	private String localCurrencyCode;
		
	private String assetClass;
	
	private Double marketValue;
	
	private Double weighting;
	
	private Double adjustedWeighting;

}
