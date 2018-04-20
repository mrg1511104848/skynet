package org.skynet.frame.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class WordUtil {
	public static String readWord(String filePath) throws IOException{  
	    String text = "";  
	    File file = new File(filePath);  
	    //2003  
	    if(file.getName().endsWith(".doc")){  
	    	FileInputStream stream = null;
	    	WordExtractor word = null;
	        try {  
	            stream = new FileInputStream(file);  
	            word = new WordExtractor(stream);  
	            text = word.getText();  
	            //去掉word文档中的多个换行  
	            text = text.replaceAll("(\\r\\n){2,}", "\r\n");  
	            text = text.replaceAll("(\\n){2,}", "\n");  
	        }finally{
	        	try {
	        		if(word!=null)
	        			word.close();
	        		if(stream!=null)
	        			stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}  
	        }
	  
	    }else if(file.getName().endsWith(".docx")){       //2007  
	    	OPCPackage oPCPackage = null;
	    	XWPFDocument xwpf = null;
	    	POIXMLTextExtractor ex = null;
	        try {  
	            oPCPackage = POIXMLDocument.openPackage(filePath);  
	            xwpf = new XWPFDocument(oPCPackage);  
	            ex = new XWPFWordExtractor(xwpf);  
	            text = ex.getText();  
	            //去掉word文档中的多个换行  
	            text = text.replaceAll("(\\r\\n){2,}", "\r\n");  
	            text = text.replaceAll("(\\n){2,}", "\n");  
	        }finally{
	        	try {
					if(ex!=null)
						ex.close();
					if(xwpf!=null)
						xwpf.close();
					if(oPCPackage!=null)
						oPCPackage.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
	        } 
	    }  
	    return text;  
	} 
}
