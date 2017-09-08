/**
 * 
 */
package com.hawthornlife.qrt.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.SortedMap;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hawthornlife.qrt.domain.Fund;
import com.hawthornlife.qrt.domain.FundHolding;
import com.hawthornlife.qrt.util.PoiUtil;

import lombok.SneakyThrows;

/**
 * This service produces the Actuarial Report.
 * 
 * @author Derek Reynolds
 *
 */
public class ActuarialReportServiceImpl implements ReportService {

	private static Logger log = LoggerFactory.getLogger(ActuarialReportServiceImpl.class);
	
	private final XSSFWorkbook workbook;
	
	private final SortedMap<String, Fund> funds;
	
	private Double totalPortfolioValue = 0.0;
		
	private XSSFCellStyle decimalStyle;
	
	private XSSFCellStyle dateStyle;
	
	private XSSFCellStyle nonNegativeStyle;
	
	@SneakyThrows
	public ActuarialReportServiceImpl(SortedMap<String, Fund> funds) {
		
		this.funds = funds;	
		
		InputStream file = getClass().getClassLoader().getResourceAsStream("template/AssetCalculationsActuarialSCRV1.0.xlsm");

		workbook = new XSSFWorkbook(file);
		
		decimalStyle = workbook.createCellStyle();
		decimalStyle.setDataFormat(workbook.createDataFormat().getFormat("#0.000000"));
		
		dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(workbook.createDataFormat().getFormat("YYYY-MM-DD"));
		
		nonNegativeStyle = workbook.createCellStyle();
		nonNegativeStyle.setDataFormat(workbook.createDataFormat().getFormat("_-* #,##0_-;-* #,##0_-;_-* \"-\"??_-;_-@_-"));
	
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
		
		File reportFile = new File(text + "/Hawthorn-Life-Actuarial-Report.xlsm");
		
		FileOutputStream out = new FileOutputStream(reportFile);
		
		log.info("Creating report {}", reportFile.getAbsoluteFile());
		
		workbook.write(out);
		
		out.close();

	}
	
	/*
	 * This method controls the creation of the worksheets.
	 */
	private void createWorksheets() {
	
		int fundCount = 0;
		
		for(Fund fund: funds.values()) {
			
			if(fund.getAssetUnderManagement() <= 0.0)
				continue;
			
			totalPortfolioValue = fund.getFundHoldings()
					.values()
					.stream()
					.mapToDouble((FundHolding fh) -> fh.getMarketValue())
					.sum();			
					
		
			XSSFSheet spreadsheet = workbook.createSheet(fund.getIsin());
			
			addFundHoldingHeaders(spreadsheet);
			
			int index  = 1;
					
			for(FundHolding fundHolding: fund.getFundHoldings().values()) {
				addFundHoldingRow(index++, spreadsheet, fund, fundHolding);			
			}
			
			addFundToSummary(fund, fundCount++);
			
			fund.getFundHoldings().clear();
		}
		
		addTotalToSummary(funds.size());
	
	}
	
	/**
	 * Adds the headers to the spreadsheet.
	 * @param spreadsheet
	 */
	private void addFundHoldingHeaders(XSSFSheet spreadsheet) {
		
		log.debug("Entering");
		
		XSSFRow row = spreadsheet.createRow(0);
		
		int columnIndex = 0;
		
		row.createCell(columnIndex++).setCellValue("Valuation date");
		row.createCell(columnIndex++).setCellValue("Portfolio name");
		row.createCell(columnIndex++).setCellValue("Portfolio currency(B)");
		row.createCell(columnIndex++).setCellValue("Net asset valuation of the portfolio in Portfolio currency");
		row.createCell(columnIndex++).setCellValue("Duration");		
		row.createCell(columnIndex++).setCellValue("Fund CIC code");
		row.createCell(columnIndex++).setCellValue("Identification code of the instrument");
		row.createCell(columnIndex++).setCellValue("CIC code of the instrument");
		row.createCell(columnIndex++).setCellValue("Instrument name");
		row.createCell(columnIndex++).setCellValue("Quotation currency (A)");		
		row.createCell(columnIndex++).setCellValue("Market valuation in portfolio currency(B)");
		row.createCell(columnIndex++).setCellValue("Credit rating");
		row.createCell(columnIndex++).setCellValue("Coupon type");
		row.createCell(columnIndex++).setCellValue("Coupon frequency");
		row.createCell(columnIndex++).setCellValue("Modified duration");
		row.createCell(columnIndex++).setCellValue("Yield to maturity");
		row.createCell(columnIndex++).setCellValue("Maturity date");
		row.createCell(columnIndex++).setCellValue("Settlement date");
		row.createCell(columnIndex++).setCellValue("Coupon rate");
		row.createCell(columnIndex++).setCellValue("Nominal value");
		row.createCell(columnIndex++).setCellValue("Interest accrual convention");
		row.createCell(columnIndex++).setCellValue("Floating rate note index benchmark");
		
		row.createCell(columnIndex++).setCellValue("Reassign codes");
		row.createCell(columnIndex++).setCellValue("Country in OECD/EEA");
		row.createCell(columnIndex++).setCellValue("Equity");
		row.createCell(columnIndex++).setCellValue("Property");
		row.createCell(columnIndex++).setCellValue("Cash");
		row.createCell(columnIndex++).setCellValue("Interest");
		row.createCell(columnIndex++).setCellValue("Spread");
		row.createCell(columnIndex++).setCellValue("Base asset value");
		row.createCell(columnIndex++).setCellValue("Equity stress factor");
		row.createCell(columnIndex++).setCellValue("Asset value after equity stress");
		row.createCell(columnIndex++).setCellValue("Asset value after property stress");
		row.createCell(columnIndex++).setCellValue("Currency");
		row.createCell(columnIndex++).setCellValue("Maturity date");
		row.createCell(columnIndex++).setCellValue("Nominal");
		row.createCell(columnIndex++).setCellValue("Floating rate");	
		row.createCell(columnIndex++).setCellValue("Base bond rate");
		row.createCell(columnIndex++).setCellValue("IR up bond rate");
		row.createCell(columnIndex++).setCellValue("IR down bond rate");
		row.createCell(columnIndex++).setCellValue("IR up stress");
		row.createCell(columnIndex++).setCellValue("IR down stress");
		row.createCell(columnIndex++).setCellValue("Asset value after IR up stress");
		row.createCell(columnIndex++).setCellValue("Asset value after IR down stress");
		row.createCell(columnIndex++).setCellValue("Yield");
		row.createCell(columnIndex++).setCellValue("Modified duration");
		row.createCell(columnIndex++).setCellValue("Maturity cohort");
		row.createCell(columnIndex++).setCellValue("Credit quality step");
		row.createCell(columnIndex++).setCellValue("Parameter 1");
		row.createCell(columnIndex++).setCellValue("Parameter 2");
		row.createCell(columnIndex++).setCellValue("Parameter 3");
		row.createCell(columnIndex++).setCellValue("Spread risk stress");
		row.createCell(columnIndex++).setCellValue("Asset value after spread stress");
		row.createCell(columnIndex++).setCellValue("Asset value after FX up stress");
		row.createCell(columnIndex++).setCellValue("Asset value after FX down stress");
		
		for(int i = 0; i < columnIndex; i++)
			spreadsheet.autoSizeColumn(i);
	}
	
	/**
	 * Adds each fund holding to the spreadsheet.
	 * @param rowIndex 
	 * @param spreadsheet
	 * @param fund
	 * @param fundHolding
	 */
	private void addFundHoldingRow(int rowIndex, final XSSFSheet spreadsheet, final Fund fund, final FundHolding fundHolding) {
		
		log.debug("Entering with row {}, Fund {}", rowIndex, fund.getLegalName());
		
		XSSFRow row = spreadsheet.createRow(rowIndex);
		 
		int columnIndex = 0;
		
		PoiUtil.createPossibleCell(row, columnIndex++, fund.getValuationDate());
		PoiUtil.createPossibleCell(row, columnIndex++, fund.getPortfolioName());
		PoiUtil.createPossibleCell(row, columnIndex++, fund.getPortfolioCurrency());		
		PoiUtil.createNumberCell(row, columnIndex++, decimalStyle, fund.getLatestNetAssetValutation());		
		PoiUtil.createNumberCell(row, columnIndex++, decimalStyle, fund.getDuration());		
		PoiUtil.createPossibleCell(row, columnIndex++, fund.getShareClassCic());		
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getIsin());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getCic());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getSecurityName());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getQuotationCurrencyCode());
		PoiUtil.createNumberCell(row, columnIndex++, decimalStyle, fundHolding.getMarketValue());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getMoodyRating());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getCouponType());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getCouponFrequency());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getModifiedDuration());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getYieldToMaturity());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getMaturityDate());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getSettlementDate());
		PoiUtil.createNumberCell(row, columnIndex++, decimalStyle, fundHolding.getCouponRate());
		PoiUtil.createNumberCell(row, columnIndex++, decimalStyle, fundHolding.getNominalValue());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getInterestAccrualConvention());
		PoiUtil.createPossibleCell(row, columnIndex++, fundHolding.getFloatingRateNoteIndexBenchmark());
		
		String rowReferenceIndex = Long.toString(rowIndex + 1);

		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF(LEN($H${0})<4,\"ZZ\"&$H${0},$H${0})", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("VLOOKUP(LEFT($W{0},2),Parameters!$C$1:$F$1048576,4,0)", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($H${0}=\"\",\"Equity\", IF(VLOOKUP(RIGHT($H${0},2),'CIC_Codes'!$D$3:$M$113,6,0)=1,\"Equity\",\"No Equity\"))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($H${0}=\"\",\"No Property\",IF(VLOOKUP(RIGHT($H${0},2),'CIC_Codes'!$D$3:$M$113,7,0)=1,\"Property\",\"No Property\"))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($H${0}=\"\",\"No Cash\",IF(VLOOKUP(RIGHT($H${0},2),'CIC_Codes'!$D$3:$M$113,4,0)=\"Cash\",\"Cash\",\"No Cash\"))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($H${0}=\"\",\"No Interest\",IF(VLOOKUP(RIGHT($H${0},2),'CIC_Codes'!$D$3:$M$113,5,0)=1,\"Interest\",\"No Interest\"))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($H${0}=\"\",\"No Spread\",IF(VLOOKUP(RIGHT($H${0},2),'CIC_Codes'!$D$3:$M$113,8,0)=1,\"Spread\",\"No Spread\"))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("$K${0} * {1, number, ##########.############}", rowReferenceIndex, fund.getAssetUnderManagement() / totalPortfolioValue));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($Y${0}=\"Equity\",IF($X${0}=\"Yes\",OthEqu_T1_Stress-Sym_Adj,OthEqu_T2_Stress-Sym_Adj),0)", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("$AD${0}*(1+$AE${0})", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($Z${0}=\"Property\",$AD${0}*(1+Property_Stress),$AD${0})", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF(ISNA(HLOOKUP($J${0},'Final_Spot_Rates'!$B$2:$AI$2,1,0)),\"GBP\",HLOOKUP($J${0},'Final_Spot_Rates'!$B$2:$AI$2,1,0))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("MAX(IF($Q${0}=\"\",IF($AA${0}=\"Cash\",Parameters!$C$2+1,Parameters!$C$3),VALUE($Q${0})),Parameters!$C$2+1)", rowReferenceIndex)).setCellStyle(dateStyle);
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($T${0}=0,100,$T${0})", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF(ISNA(VLOOKUP($V${0},Parameters!$H$38:$I$60,2,0)),0,VLOOKUP($V${0},Parameters!$H$38:$I$60,2,0))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF(OR($AB${0}=\"Interest\",$AC${0}=\"Spread\"),CALCBONDVALUE($AJ${0},$S${0}/100,$AK${0},$AI${0},IF($R${0}=\"\",Parameters!$C$2,$R${0}),OFFSET(Final_Spot_Rates!$A$3:$A$73,0,MATCH($AH${0},Final_Spot_Rates!$B$2:$AI$2,0))),0)", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF(OR($AB${0}=\"Interest\",$AC${0}=\"Spread\"),CALCBONDVALUE($AJ${0},$S${0}/100,$AK${0},$AI${0},IF($R${0}=\"\",Parameters!$C$2,$R${0}),OFFSET(Final_Spot_Rates!$AJ$3:$AJ$73,0,MATCH($AH${0},Final_Spot_Rates!$AK$2:$BR$2,0))),0)", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF(OR($AB${0}=\"Interest\",$AC${0}=\"Spread\"),CALCBONDVALUE($AJ${0},$S${0}/100,$AK${0},$AI${0},IF($R${0}=\"\",Parameters!$C$2,$R${0}),OFFSET(Final_Spot_Rates!$BS$3:$BS$73,0,MATCH($AH${0},Final_Spot_Rates!$BT$2:$DA$2,0))),0)", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($AL${0}=0,0,$AM${0}/$AL${0}-1)", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($AL${0}=0,0,$AN${0}/$AL${0}-1)", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("$AD${0}*(1+$AO{0})", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("$AD${0}*(1+$AP{0})", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($AC${0}=\"Spread\",YIELD(IF($R${0}=\"\",Parameters!$C$2,$R${0}),$AI${0},$S${0}/100,$AL${0}*100/$AJ${0},100,IF($N${0}=\"Semi-Annual\",2,IF($N${0}=\"Quarterly\",4,1)),1),\"\")", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($AC${0}=\"Spread\",IF($O${0}=\"\",MDURATION(IF($R${0}=\"\",Parameters!$C$2,AD2),$AI${0},$S${0}/100,MAX($AS${0},0),IF($N${0}=\"Semi-Annual\",2,1),1)/(1+$AS${0}),VALUE($O${0})),\"\")", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($AT${0}<5,5,IF($AT${0}>=20,999,IF(AND($AT${0}<10,$AT${0}>=5),10,IF(AND($AT${0}<15,$AT${0}>=10),15,IF(AND($AT${0}<20,$AT${0}>=15),20)))))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($AC${0}=\"Spread\",IF(ISERROR(VLOOKUP($L${0},Parameters!$I$6:$J$28,2,0)),\"unrated\",VLOOKUP($L${0},Parameters!$I$6:$J$28,2,0)),\"\")", rowReferenceIndex));		
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF(ISERROR(INDEX(Parameters!$L$5:$T$11,MATCH($AU${0},Parameters!$L$7:$L$11,0)+2,MATCH($AV${0},Parameters!$M$5:$T$5,0)+1)),\"\",INDEX(Parameters!$L$5:$T$11,MATCH($AU${0},Parameters!$L$7:$L$11,0)+2,MATCH($AV${0},Parameters!$M$5:$T$5,0)+1))", rowReferenceIndex));																				
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF(ISERROR(INDEX(Parameters!$L$12:$T$18,MATCH($AU${0},Parameters!$L$14:$L$18,0)+2,MATCH($AV${0},Parameters!$M$12:$T$12,0)+1)),\"\",INDEX(Parameters!$L$12:$T$18,MATCH($AU${0},Parameters!$L$14:$L$18,0)+2,MATCH($AV${0},Parameters!$M$12:$T$12,0)+1))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF(ISERROR(INDEX(Parameters!$L$19:$T$25,MATCH($AU${0},Parameters!$L$21:$L$25,0)+2,MATCH($AV${0},Parameters!$M$19:$T$19,0)+1)),\"\",INDEX(Parameters!$L$19:$T$25,MATCH($AU${0},Parameters!$L$21:$L$25,0)+2,MATCH($AV${0},Parameters!$M$19:$T$19,0)+1))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($AW${0}=\"\",0,-MIN(1, ($AW${0}+$AX${0}*($AU${0}-$AY${0}))))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("$AD${0}*(1+$AZ${0})", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($J${0}=\"GBP\",$AD${0},$AD${0}*(1+FX_Up))", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($J${0}=\"GBP\",$AD${0},$AD${0}*(1+FX_Down))", rowReferenceIndex));
		
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("RIGHT($H${0},2)", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("$K${0}/$D${0}", rowReferenceIndex));
		PoiUtil.createFormualCell(row, columnIndex++, MessageFormat.format("IF($Q${0}=\"\",\"\",VALUE($Q${0}))", rowReferenceIndex)).setCellStyle(dateStyle);
				
	}
	
	private void addFundToSummary(final Fund fund, int fundIndex) {
		
		int columnIndex = fundIndex + 1;
		int rowIndex = 1;
		
		XSSFSheet summary = this.workbook.getSheet("Summary");
		
		XSSFRow row = summary.getRow(rowIndex++);
		
		row.createCell(columnIndex).setCellValue(fund.getIsin());
		
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM({0}!$AD$1:$AD$1048576)", fund.getIsin()));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM({0}!$AF$1:$AF$1048576)", fund.getIsin()));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM({0}!$AG$1:$AG$1048576)", fund.getIsin()));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM({0}!$AQ$1:$AQ$1048576)", fund.getIsin()));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM({0}!$AR$1:$AR$1048576)", fund.getIsin()));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM({0}!$BA$1:$BA$1048576)", fund.getIsin()));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM({0}!$BB$1:$BB$1048576)", fund.getIsin()));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM({0}!$BC$1:$BC$1048576)", fund.getIsin()));
		
		rowIndex = rowIndex + 2;
		row = summary.getRow(rowIndex++);
		
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$4-${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$5-${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$6-${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$7-${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$8-${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$9-${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$10-${0}$3", CellReference.convertNumToColString(columnIndex)));
		
		rowIndex = rowIndex + 2;
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$13/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$14/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$15/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$16/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$17/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$18/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$19/${0}$3", CellReference.convertNumToColString(columnIndex)));
		
		rowIndex = rowIndex + 2;
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUMIFS({0}!$AD$1:$AD$1048576,{0}!$AB$1:$AB$1048576,\"Interest\",{0}!$BF$1:$BF$1048576,\"\")", fund.getIsin()));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUMIFS({0}!$BF$1:$BF$1048576,{0}!$AB$1:$AB$1048576,\"Interest\",{0}!$BF$1:$BF$1048576,\">0\")/COUNTIFS({0}!$AB$1:$AB$1048576,\"Interest\",{0}!$BF$1:$BF$1048576,\">0\")", fund.getIsin())).setCellStyle(dateStyle);
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$31/${0}$3", CellReference.convertNumToColString(columnIndex)));
	
		rowIndex = rowIndex + 2;
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUMIFS({0}!$AD$1:$AD$1048576,{0}!$AC$1:$AC$1048576,\"Spread\",{0}!$O$1:$O$1048576,\"\")-SUMIF({0}!$AA$1:$AA$1048576,\"Cash\",{0}!$AD$1:$AD$1048576)", fund.getIsin()));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$36/${0}$3", CellReference.convertNumToColString(columnIndex)));
	}
	

	private void addTotalToSummary(int numberOfFunds) {
		
		XSSFSheet summary = this.workbook.getSheet("Summary");

		int rowIndex = 1;
		int columnIndex = numberOfFunds + 1;
		
		XSSFRow row = summary.getRow(rowIndex++);
		
		row.createCell(columnIndex).setCellValue("Total");
		
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$3:${0}$3)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$4:${0}$4)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$5:${0}$5)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$6:${0}$6)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$7:${0}$7)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$8:${0}$8)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$9:${0}$9)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$10:${0}$10)", CellReference.convertNumToColString(numberOfFunds)));
		
		rowIndex = rowIndex + 2;
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$13:${0}$13)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$14:${0}$14)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$15:${0}$15)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$16:${0}$16)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$17:${0}$17)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$18:${0}$18)", CellReference.convertNumToColString(numberOfFunds)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("SUM($B$19:${0}$19)", CellReference.convertNumToColString(numberOfFunds)));

		rowIndex = rowIndex + 2;
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$13/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$14/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$15/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$16/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$17/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$18/${0}$3", CellReference.convertNumToColString(columnIndex)));
		row = summary.getRow(rowIndex++);
		PoiUtil.createFormualCell(row, columnIndex, MessageFormat.format("${0}$19/${0}$3", CellReference.convertNumToColString(columnIndex)));
	}
	
}
