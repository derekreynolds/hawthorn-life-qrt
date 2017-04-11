/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hawthornlife.qrt.domain.Fund;

/**
 * @author Derek Reynolds
 *
 */
public class FundHoldingCallable implements Callable<Boolean> {

	private static Logger log = LoggerFactory.getLogger(FundHoldingCallable.class);
	
	private final FundHoldingService fundHoldingService;

	private final Fund fund;
	
	
	public FundHoldingCallable(final FundHoldingService fundHoldingService, final Fund fund) {
		this.fundHoldingService = fundHoldingService;
		this.fund = fund;
	}
	
	@Override
	public Boolean call() throws Exception {
		
		log.debug("Entering");
		
		log.info("Getting fund holdings for {}", fund.getLegalName());
		
		try {
			
			fund.setFundHoldings(fundHoldingService.getFundHoldings(fund));
			
		} catch (Exception ex) {
			log.error("Error getting fund holdings", ex);
			return false;
		}
		
		
		return true;
	}
	
		
}
