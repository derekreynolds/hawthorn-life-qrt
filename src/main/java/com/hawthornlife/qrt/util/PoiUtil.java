package com.hawthornlife.qrt.util;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

public class PoiUtil {

	
	static public XSSFCell createPossibleCell(final XSSFRow row, Integer columnIndex, String value) {
		
		XSSFCell cell = row.createCell(columnIndex++);
		
		if("N/A".equalsIgnoreCase(value)) {
			cell.setCellType(CellType.BLANK);
		} else {
			cell.setCellValue(value);
		}
		
		return cell;
	}
	
	static public XSSFCell createNumberCell(final XSSFRow row, Integer columnIndex, CellStyle numberStyle, Double value) {		
		
		XSSFCell cell = row.createCell(columnIndex, CellType.NUMERIC);
		
		cell.setCellStyle(numberStyle);
		cell.setCellValue(value);		
		
		return cell;
	}
	
	static public XSSFCell createStringCell(final XSSFRow row, Integer columnIndex, String value) {		
		
		XSSFCell cell = row.createCell(columnIndex, CellType.STRING);
		
		cell.setCellValue(value);
		
		return cell;
	}
	
	static public XSSFCell createFormualCell(final XSSFRow row, Integer columnIndex, String formula) {		
		
		XSSFCell cell = row.createCell(columnIndex, CellType.FORMULA);
		
		cell.setCellFormula(formula);	
		
		return cell;
	}
	
	
}
