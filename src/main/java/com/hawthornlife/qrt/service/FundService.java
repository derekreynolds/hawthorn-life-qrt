package com.hawthornlife.qrt.service;

import java.io.File;

import com.hawthornlife.qrt.domain.Fund;

public interface FundService {

	Fund getFundSummary(final File file);

}