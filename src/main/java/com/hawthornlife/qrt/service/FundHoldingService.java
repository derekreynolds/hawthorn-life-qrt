/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.util.SortedMap;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.domain.FundHolding;

/**
 * @author Derek Reynolds
 *
 */
public interface FundHoldingService {

	public SortedMap<Integer, FundHolding> getFundHoldings(final Fund fund);
	
}
