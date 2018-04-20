package org.skynet.frame.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * excel 读写
 * 
 * @author wangbd
 *
 */
public class ExcelUtils4Test {
	
	public static List<List<String>> read(String xlsFile, InputStream is, int rowBegin, int colBegin, int colEnd) throws FileNotFoundException, InvalidFormatException {
		return readFromInputStream(xlsFile, is, rowBegin, colBegin, colEnd);
	}
	
	@SuppressWarnings("unused")
	public static List<List<String>> readFromInputStream(String xlsFile, InputStream is, int rowBegin, int colBegin, int colEnd) throws InvalidFormatException {
		List<List<String>> records = new ArrayList<List<String>>();
		
		try {
			
			//InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(xlsFile);
			Workbook wb = null;
			if (xlsFile.toLowerCase().endsWith("xls")) 
			{
				wb = new HSSFWorkbook(is);
			} 
			else 
			{
				wb = WorkbookFactory.create(is);
			}
			
			Sheet sheet = wb.getSheetAt(0);
			
			int i = rowBegin;
			Row row = null;
			
			while(i <= sheet.getLastRowNum() && i <= 65536) {
				row = sheet.getRow(i);
				i++;
				List<String> record = new ArrayList<String>();
				if(row != null) {
					for(int j = colBegin; j <= colEnd; j++) {
						Cell cell = row.getCell(j);
						int idx = j - colBegin;
						String v = null;
						if(cell == null) {
							v = null;
						}
						else if(cell.getCellType() == 0) {
//							v = "" + cell.getNumericCellValue();
							
							if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {     
			                    Date theDate = cell.getDateCellValue();  
			                    SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			                    v = dff.format(theDate);  
			                }else{  
			                    DecimalFormat df = new DecimalFormat("0");    
			                    v = df.format(cell.getNumericCellValue());  
			                }  
						}
						else {
							v = cell.getStringCellValue().trim();
						}
						record.add(v);
					}
				}
				records.add(record);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("excel 读取行数" + records.size());
		
		return records;
	}
	
	public static void writeResult(List<String[]> result, String[] head, String xlsFile) {
		Workbook wb = null;
		OutputStream os = null;
		try {
			if (xlsFile.toLowerCase().endsWith("xls")) 
			{
				wb = new HSSFWorkbook();
			} 
			else 
			{
				wb = new XSSFWorkbook();
			}
			
			Sheet sheet = wb.createSheet();
			Row row = null;
			int rowNum = 0;
			
			if(head != null) {
				row = sheet.createRow(rowNum++);
				CellStyle stype = wb.createCellStyle();
				Font headerFont = wb.createFont(); 
				headerFont.setBold(true);;
				stype.setFont(headerFont);
				row.setRowStyle(stype);
				for(int i = 0; i < head.length; i++) {
					Cell cell = row.createCell(i);
					cell.setCellValue(head[i]);
				}
			}
			
			for(int j = 0; j < result.size(); j++) {
				String[] record = result.get(j);
				row = sheet.createRow(rowNum++);
				for(int i = 0; i < record.length; i++) {
					Cell cell = row.createCell(i);
					cell.setCellValue(record[i]);
				}
			}
			
			String f = Thread.currentThread().getContextClassLoader().getResource(xlsFile).getFile();
			//f = f.replaceAll("/[^/]*\\.xls", "/result.xls");
			os = new FileOutputStream(f);
			wb.write(os);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(os!=null) os.close();
				if(wb!=null) wb.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static void main(String[] args) {
		
		try {
			List<String> cells = new ArrayList<String>();
			cells.add("xxx");
			cells.add("xxx");
			cells.add("xxx");
			cells.add("xxx");
			cells.add("xxx");
			cells.add("xxx");
			cells.add("xxx");
			cells.add("xxx");
			cells.add("xxx");
			cells.add("xxx");
			List<List<String>> values = new ArrayList<List<String>>();
			List<List<String>> list = ExcelUtils4Test.read("20170920广发证券.xls", new FileInputStream(new File("C://20170920广发证券.xls")), 0, 0, 9);
			for (List<String> l : list) {
				String old_ = l.get(2);
				String new_ = l.get(3);
				if(StringUtils.isBlank(old_)){
					continue;
				}
				String[] olds = old_.split(" ");
				for (int i = 4; i < l.size(); i++) {
					if(olds.length==1){
						l.set(i,l.get(i).replace(old_, new_));
					}else{
						for (String old : olds) {
							l.set(i,l.get(i).replace(old, "*{"+old+"}*"));
						}
					}
				}
				values.add(l);
				ExcelUtil.export("C://20170920广发证券_处理后.xls", " ddd ", cells, values);
			}
		} catch (FileNotFoundException | InvalidFormatException e) {
			e.printStackTrace();
		}
	}
}
