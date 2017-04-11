/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.codehaus.plexus.util.FileUtils;

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
	
	private final List<Map<String, List<FundHolding>>> groupedAssetClassAggregation = new ArrayList<>();
	
	
	
	public InvestmentReportServiceImpl(SortedMap<String, Fund> funds) {
		this.funds = funds;
	}
	
	
	
	@SneakyThrows
	@Override
	public void generate() {
		
		log.debug("Entering");
		
		createAumSheet();
	     
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_km");
		String text = now.format(formatter);
		
		FileUtils.forceMkdir(new File(text));
		
		File reportFile = new File(text + "/Hawthorn-Life-QRT.xlsx");
		
		FileOutputStream out = new FileOutputStream(reportFile);
		
		log.info("Creating report {}", reportFile.getAbsoluteFile());
		
		workbook.write(out);
		
		out.close();
		
	}
	
	private void createAumSheet() {
		
		log.debug("Entering");
		
		int rowIndex = 0;
		Integer combinedSheetRowIndex = 0;
		
		XSSFSheet combinedSpreadsheet = workbook.createSheet("Combined Asset Class");
		
		addAssetClassHeader(combinedSpreadsheet);
		
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
			createCombinedAssetClassSheet(combinedSpreadsheet, fund, combinedSheetRowIndex);			
		 }		
		
		workbook.setSheetOrder("Combined Asset Class", workbook.getNumberOfSheets() - 1);
	}

	private void createAssetClassSheet(final Fund fund) {
		
		log.debug("Entering with {}", fund.getLegalName());
		
		XSSFSheet spreadsheet = workbook.createSheet(fund.getIsin() + " - Asset Class");

		addAssetClassHeader(spreadsheet);
		
		addAssetClassRows(spreadsheet, fund, new Integer(0));		
	
	}
	
	private void addAssetClassHeader(XSSFSheet spreadsheet) {
		
		log.debug("Entering");
		
		XSSFRow row = spreadsheet.createRow(0);
		
		int columnIndex = 0;
		
		row.createCell(columnIndex++).setCellValue("ISIN");
		row.createCell(columnIndex++).setCellValue("Asset Class Code");
		row.createCell(columnIndex++).setCellValue("Country Code");
		row.createCell(columnIndex++).setCellValue("Currency Code");
		row.createCell(columnIndex++).setCellValue("Adjusted Weighting");
		row.createCell(columnIndex++).setCellValue("Total Value");
		
	}

	private void createCombinedAssetClassSheet(XSSFSheet combinedSpreadsheet, Fund fund, Integer rowIndex) {
		
		log.debug("Entering");	
					
		addAssetClassRows(combinedSpreadsheet, fund, rowIndex);		
		
	}
	
	private void addAssetClassRows(XSSFSheet spreadsheet, Fund fund, Integer rowIndex) {
		
		Map<String, List<FundHolding>> assetClassAggregation = groupByAssetClass(fund);		
		
		for(String key : assetClassAggregation.keySet()) {
			
			int columnIndex = 0;
			
			XSSFRow row = spreadsheet.createRow(++rowIndex);
						
			for(String keyPart: key.split(":")) {
				createPossibleCell(row, columnIndex++, keyPart);				
			}
			
			double summedAdjustedWeight = assetClassAggregation.get(key)
					.stream()
					.mapToDouble(fh -> fh.getAdjustedWeighting()).sum();
			
			row.createCell(columnIndex++).setCellValue(summedAdjustedWeight);
			row.createCell(columnIndex++).setCellValue(summedAdjustedWeight * fund.getAssetUnderManagement());
		}
		
	}
	
	private void createFundHoldingSheet(final Fund fund) {
		
		log.debug("Entering with {}", fund.getLegalName());
		
		XSSFSheet spreadsheet = workbook.createSheet(fund.getIsin());
		
		addFundHoldingHeaders(spreadsheet);
		
		int index  = 1;
				
		for(FundHolding fundHolding: fund.getFundHoldings().values()) {
			addFundHoldingRow(index++, spreadsheet, fund, fundHolding);			
		}
		
	}
	
	
	private void addFundHoldingHeaders(XSSFSheet spreadsheet) {
		
		log.debug("Entering");
		
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
		
		log.debug("Entering with row {}, Fund {}", rowIndex, fund.getLegalName());
		
		XSSFRow row = spreadsheet.createRow(rowIndex);
		 
		int columnIndex = 0;
		
		row.createCell(columnIndex++).setCellValue(fundHolding.getId());
		row.createCell(columnIndex++).setCellValue(fundHolding.getExternalId());
		row.createCell(columnIndex++).setCellValue(fundHolding.getName());
		
		createPossibleCell(row, columnIndex++, fundHolding.getCountry());
		createPossibleCell(row, columnIndex++, fundHolding.getCountryCode());
		createPossibleCell(row, columnIndex++, fundHolding.getLocalCurrencyCode());
						
		row.createCell(columnIndex++).setCellValue(fundHolding.getAssetClass());
		row.createCell(columnIndex++).setCellValue(fundHolding.getMarketValue());
		row.createCell(columnIndex++).setCellValue(fundHolding.getWeighting());
		row.createCell(columnIndex++).setCellValue(fundHolding.getAdjustedWeighting());
		
		row.createCell(columnIndex++).setCellValue(fund.getAssetUnderManagement().doubleValue() * fundHolding.getAdjustedWeighting());
	}
	
	private void createPossibleCell(final XSSFRow row, int columnIndex, String value) {
		
		if("N/A".equalsIgnoreCase(value)) {
			row.createCell(columnIndex++).setCellType(CellType.BLANK);
		} else {
			row.createCell(columnIndex++).setCellValue(value);
		}
	}
	
	private Map<String, List<FundHolding>> groupByAssetClass(Fund fund) {
		
		return fund.getFundHoldings()
				.values()
				.stream()
				.collect(Collectors.groupingBy(fh -> fund.getIsin() + ":" + fh.getAssetClass() 
								+ ":" + fh.getCountryCode() + ":" + fh.getLocalCurrencyCode()));

	}

}
