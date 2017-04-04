/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.domain.FundHolding;

import lombok.SneakyThrows;

/**
 * @author Derek Reynolds
 *
 */
public class InvestmentReportServiceImpl implements InvestmentReportService {

	private static Logger log = LoggerFactory.getLogger(InvestmentReportServiceImpl.class);
	
	private final XSSFWorkbook workbook = new XSSFWorkbook();
	
	private final SortedMap<String, Fund> funds;
	
	public InvestmentReportServiceImpl(SortedMap<String, Fund> funds) {
		this.funds = funds;
	}
	
	@SneakyThrows
	@Override
	public void generate() {
		
		log.info("Generating Investment Report");
		
		createAumSheet();
	     
		FileOutputStream out = new FileOutputStream(new File("Hawthorn-Life-QRT.xlsx"));
		workbook.write(out);
		out.close();
	}
	
	private void createAumSheet() {
		
		int rowIndex = 0;
		
		XSSFSheet spreadsheet = workbook.createSheet("AUM Summary");
		 
		XSSFRow row = spreadsheet.createRow(rowIndex);
		 
		row.createCell(0).setCellValue("ISIN");		
		row.createCell(1).setCellValue("Fund Name");		 
		row.createCell(2).setCellValue("Amount");		 

		 
		for(Fund fund: funds.values()) {
			
			log.debug("AUM {} {}", fund.getLegalName(), fund.getAssetUnderManagement());
			
			if(fund.getAssetUnderManagement() <= 0.0)
				continue;
			
			XSSFRow r = spreadsheet.createRow(++rowIndex);
			 
			r.createCell(0).setCellValue(fund.getIsin());
			r.createCell(1).setCellValue(fund.getLegalName());
			r.createCell(2).setCellValue(Double.valueOf(fund.getAssetUnderManagement()));
			
			createFundHoldingSheet(fund);	
			createAssetClassSheet(fund);
						
		 }
		
	}

	private void createAssetClassSheet(final Fund fund) {
		
		XSSFSheet spreadsheet = workbook.createSheet(fund.getIsin() + " - Asset Class");

		Map<String, List<FundHolding>> assetClassAggregation = fund.getFundHoldings()
				.values()
				.stream()
				.collect(Collectors.groupingBy(fh -> fh.getAssetClass() 
								+ ":" + fh.getCountryCode() + ":" + fh.getLocalCurrencyCode()));

		addAssetClassHeader(spreadsheet);
		
		int rowIndex = 0;
		
		for(String key : assetClassAggregation.keySet()) {
			
			int columnIndex = 0;
			
			XSSFRow row = spreadsheet.createRow(++rowIndex);
			
			for(String k: key.split(":")) {
				row.createCell(columnIndex++).setCellValue(k);				
			}
			
			double summedAdjustedWeight = assetClassAggregation.get(key)
					.stream()
					.mapToDouble(fh -> fh.getAdjustedWeighting()).sum();
			
			row.createCell(columnIndex++).setCellValue(summedAdjustedWeight);
			row.createCell(columnIndex++).setCellValue(summedAdjustedWeight * fund.getAssetUnderManagement());
		}
	
	}
	
	private void addAssetClassHeader(XSSFSheet spreadsheet) {
		
		XSSFRow row = spreadsheet.createRow(0);
		
		int columnIndex = 0;
		
		row.createCell(columnIndex++).setCellValue("Asset Class Code");
		row.createCell(columnIndex++).setCellValue("Country Code");
		row.createCell(columnIndex++).setCellValue("Currency Code");
		row.createCell(columnIndex++).setCellValue("Adjusted Weighting");
		row.createCell(columnIndex++).setCellValue("Total Value");
		
	}

	private void createFundHoldingSheet(final Fund fund) {
		
		XSSFSheet spreadsheet = workbook.createSheet(fund.getIsin());
		
		addFundHoldingHeaders(spreadsheet);
		
		int index  = 1;
				
		for(FundHolding fundHolding: fund.getFundHoldings().values()) {
			addFundHoldingRow(index++, spreadsheet, fund, fundHolding);			
		}
		
	}
	
	
	private void addFundHoldingHeaders(XSSFSheet spreadsheet) {
		
		XSSFRow row = spreadsheet.createRow(0);
		
		int columnIndex = 0;
		
		row.createCell(columnIndex++).setCellValue("Id");
		row.createCell(columnIndex++).setCellValue("External Id");
		row.createCell(columnIndex++).setCellValue("Name");
		row.createCell(columnIndex++).setCellValue("Country");
		row.createCell(columnIndex++).setCellValue("Country Code");
		row.createCell(columnIndex++).setCellValue("Local Currency Code");
		row.createCell(columnIndex++).setCellValue("Asset Class");
		row.createCell(columnIndex++).setCellValue("Market Value");
		row.createCell(columnIndex++).setCellValue("Weighting");
		row.createCell(columnIndex++).setCellValue("Adjusted Weighting");		
		row.createCell(columnIndex++).setCellValue("Total Value");
	}
	
	private void addFundHoldingRow(int rowIndex, final XSSFSheet spreadsheet, final Fund fund, final FundHolding fundHolding) {
		
		XSSFRow row = spreadsheet.createRow(rowIndex);
		 
		int columnIndex = 0;
		
		row.createCell(columnIndex++).setCellValue(fundHolding.getId());
		row.createCell(columnIndex++).setCellValue(fundHolding.getExternalId());
		row.createCell(columnIndex++).setCellValue(fundHolding.getName());
		row.createCell(columnIndex++).setCellValue(fundHolding.getCountry());
		row.createCell(columnIndex++).setCellValue(fundHolding.getCountryCode());
		row.createCell(columnIndex++).setCellValue(fundHolding.getLocalCurrencyCode());
		row.createCell(columnIndex++).setCellValue(fundHolding.getAssetClass());
		row.createCell(columnIndex++).setCellValue(fundHolding.getMarketValue());
		row.createCell(columnIndex++).setCellValue(fundHolding.getWeighting());
		row.createCell(columnIndex++).setCellValue(fundHolding.getAdjustedWeighting());
		
		row.createCell(columnIndex++).setCellValue(fund.getAssetUnderManagement().doubleValue() * fundHolding.getAdjustedWeighting());
	}
		
}
