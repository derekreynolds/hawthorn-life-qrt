/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.util.SortedMap;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.domain.FundHolding;

/**
 * Service to get the {@link FundHolding} for a fund.
 * 
 * @author Derek Reynolds
 *
 */
public interface FundHoldingService {

	/**
	 * Retrieves the {@link FundHolding}s for the fund.
	 * @param fund - the fund that we are getting the {@link FundHolding}s for.
	 * @return a map of {@link FundHolding}s for the {@link Fund}
	 */
	public SortedMap<Integer, FundHolding> getFundHoldings(final Fund fund);
	
}
