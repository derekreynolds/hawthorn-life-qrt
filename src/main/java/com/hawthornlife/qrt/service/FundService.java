package com.hawthornlife.qrt.service;

import java.io.File;

import com.hawthornlife.qrt.domain.Fund;

/**
 * Service to retrieve the fund summary for a fund in the XML file.
 * 
 * @author Derek Reynolds
 *
 */
public interface FundService {

	/**
	 * Gets a {@link Fund}. It contains high-level summary information based on
	 * the XML files passed in.
	 * 
	 * @param file - the XML file
	 * @return {@link Fund}
	 */
	Fund getFundSummary(final File file);

}