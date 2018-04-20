package org.skynet.frame.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.skynet.frame.util.excel.ExcelBean;

public class ExcelUtil {
	public static void export(String savePath,List<String> cells,List<List<String>> values){
		export(savePath, "sheet", cells, values);
	}
	public static void export(String savePath,List<ExcelBean> excelBeans){
		for (ExcelBean excelBean : excelBeans) {
			export(savePath+"/"+excelBean.getName(), "sheet", excelBean.getCells(), excelBean.getValues());
		}
	}
	@SuppressWarnings("deprecation")
	public static void export(String savePath,String sheetName,List<String> cells,List<List<String>> values){
		HSSFWorkbook wb = null;
		try  
        {  
			// 第一步，创建一个webbook，对应一个Excel文件
			wb = new HSSFWorkbook();
			// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
			HSSFSheet sheet = wb.createSheet(sheetName);
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
			HSSFRow row = sheet.createRow((int) 0);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式

			for (int i = 0; i < cells.size(); i++){
	        	HSSFCell cell = row.createCell((short) i);  
	            cell.setCellValue(cells.get(i));  
	            cell.setCellStyle(style);  
			}
			int index = 0;//记录额外创建的sheet数量
	        for (int i = 0; i < values.size(); i++)  
	        {  
	        	if ((i + 1) % 65535 == 0) {
	        		sheet = wb.createSheet(sheetName+"_"+i);
					row = sheet.createRow(0);
					for (int k = 0; k < cells.size(); k++){
			        	HSSFCell cell = row.createCell((short) k);  
			            cell.setCellValue(cells.get(k));  
			            cell.setCellStyle(style);  
					}
					index++;
	        	}
	        	row = sheet.createRow((i + 1) - (index * 65535));
//	        	// 第四步，创建单元格，并设置值  
//	            row = sheet.createRow(i + 1);  
	            List<String> properties = values.get(i);
	            for(int j = 0 ; j < properties.size() ; j++){
	            	String pro = properties.get(j);
	            		try {
	            			row.createCell((short) j).setCellValue(pro);
	            		} catch (Exception e) {
	            			System.err.println(pro);
	            			e.printStackTrace();
	            		}  
	            }
	        }  
        // 第五步，将文件存到指定位置  
        
        	String p = StringUtils.substringBeforeLast(savePath, "/");
        	File fielder = new File(p);
        	if(!fielder.exists()){
        		fielder.mkdir();
        	}
            FileOutputStream fout = new FileOutputStream(savePath);  
            wb.write(fout);  
            fout.close();  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        } 
		finally{
			try {
				if(wb!=null)
					wb.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static void main(String[] args) {
		List<String> cells = new ArrayList<String>();
		cells.add("实例名");
		cells.add("实例属性");
		cells.add("标准问题");
		cells.add("标准答案");
		
		List<List<String>> values =new ArrayList<List<String>>();
		
		List<String> propertyList =new ArrayList<String>();
		propertyList.add("");
		propertyList.add("");
		propertyList.add("");
		propertyList.add("");
		values.add(propertyList);
		
		List<List<String>> lt50Question = new ArrayList<List<String>>();
		for (List<String> v : values) {
			String name = v.get(2);
			if(name.length()<50){
				lt50Question.add(v);
			}
		}
		ExcelUtil.export("D://eeexxx.csv", "小冰闲聊", cells, lt50Question);
	}
}
