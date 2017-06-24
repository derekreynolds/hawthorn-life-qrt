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
		
		createWorksheets();
		
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
	
	private void createWorksheets() {
	
		for(Fund fund: funds.values()) {
			
			if(fund.getAssetUnderManagement() <= 0.0)
				continue;
		
			XSSFSheet spreadsheet = workbook.createSheet(fund.getIsin());
			
			addFundHoldingHeaders(spreadsheet);
			
			int index  = 1;
					
			for(FundHolding fundHolding: fund.getFundHoldings().values()) {
				addFundHoldingRow(index++, spreadsheet, fund, fundHolding);			
			}
		}
	
	}
	
	private void addFundHoldingHeaders(XSSFSheet spreadsheet) {
		
		log.debug("Entering");
		
		XSSFRow row = spreadsheet.createRow(0);
		
		int columnIndex = 0;
		
		row.createCell(columnIndex++).setCellValue("Valuation date");
		row.createCell(columnIndex++).setCellValue("Portfolio name");
		row.createCell(columnIndex++).setCellValue("Portfolio currency(B)");
		row.createCell(columnIndex++).setCellValue("Net asset valuation of the portfolio in Portfolio currency");
		row.createCell(columnIndex++).setCellValue("Duration");
		row.createCell(columnIndex++).setCellValue("Fund custodian country");
		row.createCell(columnIndex++).setCellValue("Fund issuer group name");
		row.createCell(columnIndex++).setCellValue("Fund issuer country");		
		row.createCell(columnIndex++).setCellValue("Fund CIC code");
		
		row.createCell(columnIndex++).setCellValue("Valuation weight");
		row.createCell(columnIndex++).setCellValue("Identification code of the instrument");
		row.createCell(columnIndex++).setCellValue("CIC code of the instrument");
		row.createCell(columnIndex++).setCellValue("Instrument name");
		row.createCell(columnIndex++).setCellValue("Quotation currency (A)");
		row.createCell(columnIndex++).setCellValue("Market valuation in quotation currency(A)");		
		row.createCell(columnIndex++).setCellValue("Market valuation in portfolio currency(B)");
		row.createCell(columnIndex++).setCellValue("Coupon rate");
		row.createCell(columnIndex++).setCellValue("Maturity date");
		row.createCell(columnIndex++).setCellValue("Credit rating");
		row.createCell(columnIndex++).setCellValue("Underlying asset category");
		row.createCell(columnIndex++).setCellValue("Coupon type");
		row.createCell(columnIndex++).setCellValue("Coupon frequency");
		row.createCell(columnIndex++).setCellValue("Callable");
		row.createCell(columnIndex++).setCellValue("Putable");
		row.createCell(columnIndex++).setCellValue("EDI issuer name");
		row.createCell(columnIndex++).setCellValue("EDI issuer id");
		row.createCell(columnIndex++).setCellValue("Modified duration");
		row.createCell(columnIndex++).setCellValue("Yield to maturity");
		row.createCell(columnIndex++).setCellValue("Maturity date");
		row.createCell(columnIndex++).setCellValue("Settlement date");
		row.createCell(columnIndex++).setCellValue("Primary exchange");
		row.createCell(columnIndex++).setCellValue("Accrued interest");
		row.createCell(columnIndex++).setCellValue("Yield To call");
		row.createCell(columnIndex++).setCellValue("Yield To put");
		row.createCell(columnIndex++).setCellValue("Effective duration");
		row.createCell(columnIndex++).setCellValue("Macaulay duration");
		row.createCell(columnIndex++).setCellValue("Convexity");
		row.createCell(columnIndex++).setCellValue("First coupon date");
		row.createCell(columnIndex++).setCellValue("Coupon rate");
		row.createCell(columnIndex++).setCellValue("Nominal value");
		row.createCell(columnIndex++).setCellValue("Issue date");
		row.createCell(columnIndex++).setCellValue("Outstanding amount");
		row.createCell(columnIndex++).setCellValue("Interest commencement date");
		row.createCell(columnIndex++).setCellValue("Interest accrual convention");
		row.createCell(columnIndex++).setCellValue("Floating rate note index benchmark");
		row.createCell(columnIndex++).setCellValue("Perpetual");
		row.createCell(columnIndex++).setCellValue("Maturity price as a percent");
		row.createCell(columnIndex++).setCellValue("Maturity structure");

		
	}
	
	private void addFundHoldingRow(int rowIndex, final XSSFSheet spreadsheet, final Fund fund, final FundHolding fundHolding) {
		
		log.debug("Entering with row {}, Fund {}", rowIndex, fund.getLegalName());
		
		XSSFRow row = spreadsheet.createRow(rowIndex);
		 
		int columnIndex = 0;
		
		row.createCell(columnIndex++).setCellValue(fund.getValuationDate());
		row.createCell(columnIndex++).setCellValue(fund.getPortfolioName());
		row.createCell(columnIndex++).setCellValue(fund.getPortfolioCurrency());		
		row.createCell(columnIndex++).setCellValue(fund.getLatestNetAssetValutation());		
		row.createCell(columnIndex++).setCellValue(fund.getDuration());		
		row.createCell(columnIndex++).setCellValue(fund.getFundCustodianCountry());
		row.createCell(columnIndex++).setCellValue(fund.getFundIssuerGroupName());
		row.createCell(columnIndex++).setCellValue(fund.getFundIssuerCountry());
		row.createCell(columnIndex++).setCellValue(fund.getShareClassCic());
		
		
		row.createCell(columnIndex++).setCellValue(fundHolding.getWeighting());
		row.createCell(columnIndex++).setCellValue(fundHolding.getIsin());
		row.createCell(columnIndex++).setCellValue(fundHolding.getCic());
		row.createCell(columnIndex++).setCellValue(fundHolding.getSecurityName());
		row.createCell(columnIndex++).setCellValue(fundHolding.getQuotationCurrencyCode());
		row.createCell(columnIndex++).setCellValue(fundHolding.getLocalMarketValue());
		row.createCell(columnIndex++).setCellValue(fundHolding.getMarketValue());
		row.createCell(columnIndex++).setCellValue(fundHolding.getCouponRate());
		row.createCell(columnIndex++).setCellValue(fundHolding.getMaturityDate());
		row.createCell(columnIndex++).setCellValue(fundHolding.getMoodyRating());
		row.createCell(columnIndex++).setCellValue(fundHolding.getUac());
		row.createCell(columnIndex++).setCellValue(fundHolding.getCouponType());
		row.createCell(columnIndex++).setCellValue(fundHolding.getCouponFrequency());
		row.createCell(columnIndex++).setCellValue(fundHolding.getCallable());
		row.createCell(columnIndex++).setCellValue(fundHolding.getPuttable());
		row.createCell(columnIndex++).setCellValue(fundHolding.getEdiIssuerName());
		row.createCell(columnIndex++).setCellValue(fundHolding.getEdiIssuerId());
		row.createCell(columnIndex++).setCellValue(fundHolding.getModifiedDuration());
		row.createCell(columnIndex++).setCellValue(fundHolding.getYieldToMaturity());
		row.createCell(columnIndex++).setCellValue(fundHolding.getMaturityDate());
		row.createCell(columnIndex++).setCellValue(fundHolding.getSettlementDate());
		row.createCell(columnIndex++).setCellValue(fundHolding.getPrimaryExchange());
		row.createCell(columnIndex++).setCellValue(fundHolding.getAccruedInterest());
		row.createCell(columnIndex++).setCellValue(fundHolding.getYieldToCall());
		row.createCell(columnIndex++).setCellValue(fundHolding.getYieldToPut());
		row.createCell(columnIndex++).setCellValue(fundHolding.getEffectiveDuration());
		row.createCell(columnIndex++).setCellValue(fundHolding.getMacaulayDuration());
		row.createCell(columnIndex++).setCellValue(fundHolding.getConvexity());
		row.createCell(columnIndex++).setCellValue(fundHolding.getFirstCouponDate());
		row.createCell(columnIndex++).setCellValue(fundHolding.getCouponRate());
		row.createCell(columnIndex++).setCellValue(fundHolding.getNominalValue());
		row.createCell(columnIndex++).setCellValue(fundHolding.getIssueDate());
		row.createCell(columnIndex++).setCellValue(fundHolding.getOutstandingAmount());
		row.createCell(columnIndex++).setCellValue(fundHolding.getInterestCommencementDate());
		row.createCell(columnIndex++).setCellValue(fundHolding.getInterestAccrualConvention());
		row.createCell(columnIndex++).setCellValue(fundHolding.getFloatingRateNoteIndexBenchmark());
		row.createCell(columnIndex++).setCellValue(fundHolding.getPerpetual());
		row.createCell(columnIndex++).setCellValue(fundHolding.getMaturityPriceAsAPercent());
		row.createCell(columnIndex++).setCellValue(fundHolding.getMaturityStructure());

	}
	
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
