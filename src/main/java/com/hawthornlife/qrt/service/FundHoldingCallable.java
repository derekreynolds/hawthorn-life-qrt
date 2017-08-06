/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.domain.FundHolding;

/**
 * Used to create a thread that will get the {@link FundHolding}s.
 * 
 * @author Derek Reynolds
 *
 */
public class FundHoldingCallable implements Callable<Boolean> {

	private static Logger log = LoggerFactory.getLogger(FundHoldingCallable.class);
	
	private final Fund fund;
	
	
	public FundHoldingCallable(final Fund fund) {
		this.fund = fund;
	}
	
	@Override
	public Boolean call() throws Exception {
		
		log.debug("Entering");
		
		log.info("Getting fund holdings for {}", fund.getLegalName());
		
		try {
		
			FundHoldingService fundHoldingService = new FundHoldingServiceImpl();
			
			StopWatch watch = StopWatch.createStarted();
			
			fund.setFundHoldings(fundHoldingService.getFundHoldings(fund));
			
			watch.stop();
			
			log.info("Time taken to get fund holdings: {} for {}", watch.getTime() , fund.getLegalName());
			
		} catch (Exception ex) {
			log.error("Error getting fund holdings", ex);
			return false;
		}
		
		
		return true;
	}
	
		
}
