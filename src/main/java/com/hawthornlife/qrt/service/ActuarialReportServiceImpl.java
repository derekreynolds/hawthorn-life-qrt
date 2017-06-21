/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.domain.FundHolding;

import lombok.SneakyThrows;

/**
 * @author Derek Reynolds
 *
 */
public class ActuarialReportServiceImpl implements ReportService {

	private static Logger log = LoggerFactory.getLogger(ActuarialReportServiceImpl.class);
	
	private final XSSFWorkbook workbook = new XSSFWorkbook();
	
	private final SortedMap<String, Fund> funds;
		
	private XSSFCellStyle decimalStyle;
	
	
	public ActuarialReportServiceImpl(SortedMap<String, Fund> funds) {
		
		this.funds = funds;
		
		decimalStyle=(XSSFCellStyle) workbook.createCellStyle();
		decimalStyle.setDataFormat(workbook.createDataFormat().getFormat("#0.000000"));
	
	}
	
	/* (non-Javadoc)
	 * @see com.hawthornlife.qrt.service.ReportService#generate()
	 */
	@SneakyThrows
	@Override
	public void generate() {
		
		log.debug("Entering");
		
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm");
		String text = now.format(formatter);
		
		FileUtils.forceMkdir(new File(text));
		
		File reportFile = new File(text + "/Hawthorn-Life-Actuarial-Report.xlsx");
		
		FileOutputStream out = new FileOutputStream(reportFile);
		
		log.info("Creating report {}", reportFile.getAbsoluteFile());
		
		workbook.write(out);
		
		out.close();

	}
	
	private void createAssetClassSheet(final Fund fund) {
		
		log.debug("Entering with {}", fund.getLegalName());
		
		XSSFSheet spreadsheet = workbook.createSheet(fund.getIsin() + " - Asset Class");

		addAssetClassHeader(spreadsheet);
		
		//addAssetClassRows(spreadsheet, fund, new Integer(0));		
	
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
	
	/*private Integer addAssetClassRows(XSSFSheet spreadsheet, Fund fund, Integer rowIndex) {
		
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
			
			createNumberCell(row, columnIndex++, summedAdjustedWeight);
			createNumberCell(row, columnIndex++, summedAdjustedWeight * fund.getAssetUnderManagement());
		}
		
		return assetClassAggregation.keySet().size();
		
	}*/
	
	private void createPossibleCell(final XSSFRow row, Integer columnIndex, String value) {
		
		if("N/A".equalsIgnoreCase(value)) {
			row.createCell(columnIndex++).setCellType(CellType.BLANK);
		} else {
			row.createCell(columnIndex++).setCellValue(value);
		}
	}
	
	private void createNumberCell(final XSSFRow row, Integer columnIndex, Double value) {		
		
		XSSFCell cell = row.createCell(columnIndex, CellType.NUMERIC);
		cell.setCellStyle(decimalStyle);
		cell.setCellValue(value);		
		
	}


}
