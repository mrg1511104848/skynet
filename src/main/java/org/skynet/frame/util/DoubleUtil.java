package org.skynet.frame.util;

import java.text.DecimalFormat;

public class DoubleUtil {
	public static double parseStr2Double(String str,int precision){
		if(precision<=0) throw new RuntimeException(" 精确度[precision] 必须大于0");
		Double toBeParse = Double.parseDouble(str);//6.2041    这个是转为double类型  
		StringBuffer sb = new StringBuffer("0.");
		for (int i = 0; i < precision; i++) {
			sb.append("0");
		}
		DecimalFormat df = new DecimalFormat(sb.toString());   
		String toBeParseStr = df.format(toBeParse); //6.20   这个是字符串，但已经是我要的两位小数了  
		return Double.parseDouble(toBeParseStr); //6.20  
	}
}
