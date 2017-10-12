package com.hisign;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	public static Map getMap(String filePath) {
		String str = "";
		Map map = new HashMap<>();
		boolean isE2007 = false; // 判断是否是excel2007格式
		if (filePath.endsWith("xlsx")) {
			isE2007 = true;
		}

		try {
			InputStream input = new FileInputStream(filePath); // 建立输入流
			Workbook wb = null;
			int sheets = 0;
			// 根据文件格式(2003或者2007)来初始化
			if (isE2007) {
				wb = new XSSFWorkbook(input);
			} else {
				wb = new HSSFWorkbook(input);
			}
			Sheet sheet = wb.getSheetAt(0);
			int firstRow = sheet.getFirstRowNum();
			int endRow = sheet.getLastRowNum();
			for (int aa = firstRow; aa < endRow; aa++) {
				Row row = sheet.getRow(aa);
				if (row != null) {

					int endCell = row.getLastCellNum();
					// 文件名
					Cell cell = row.getCell(0);
					String cellValue = setCellStyle(cell);

					Cell cell1 = row.getCell(1);
					String cellValue1 = setCellStyle(cell1);
					cellValue1 = "Z" + cellValue1.substring(1, cellValue1.length());

					// md5
					Cell cell_ = row.getCell(2);
					String cellValue_ = setCellStyle(cell_);
					// System.out.println(cellValue + "-->" + cellValue_);
					map.put(cellValue + cellValue_, "cellValue_");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 将单元格格式转换
	 * 
	 * @param cell
	 * @return
	 */
	public static String setCellStyle(Cell cell) {
		String cellValue = "";
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			DecimalFormat df = new DecimalFormat("#");
			cellValue = df.format(cell.getNumericCellValue());
			cellValue = cellValue.replace(" ", "|").replace("\n", "|").replace("\r", "|").replace("\t", "|");
		} else {
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellValue = cell.toString().replace(" ", "|").replace("\n", "|").replace("\r", "|").replace("\t", "|");
		}
		return cellValue;
	}
}
