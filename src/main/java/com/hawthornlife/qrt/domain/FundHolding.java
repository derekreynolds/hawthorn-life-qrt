package com.hawthornlife.qrt.domain;

import lombok.Data;

public @Data class FundHolding {

	private String name;
	
	private String isin;
	
	private String countryCode;
	
	private String country;
	
	private String localCountry;
	
	private String assetClass;
	
	private Double weighting;
	
	private Double marketValue;
}
