package org.skynet.frame.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DateUtils;
import org.skynet.frame.util.RegexUtils;

public class DateUtil {
	/**
	 * yyyy-MM-dd HH:mm:ss:SSS
	 */
	public static final String DATE_FORMAT_01 = "yyyy-MM-dd HH:mm:ss:SSS";
	/**
	 * yyyy-MM-dd
	 */
	public static final String DATE_FORMAT_02 = "yyyy-MM-dd";

	/**
	 * yyyyMMdd
	 */
	public static final String DATE_FORMAT_03 = "yyyyMMdd";
	
	/**
	 * yyyy/MM/dd
	 */
	public static final String DATE_FORMAT_04 = "yyyy/MM/dd";
	
	/**
	 * yyyy/MM/dd HH:mm:ss
	 */
	public static final String DATE_FORMAT_05 = "yyyy/MM/dd HH:mm:ss";
	
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String DATE_FORMAT_06 = "yyyy-MM-dd HH:mm:ss";

	public static Date addDay(int num) {
		return DateUtils.addDays(new Date(), num);
	}
	/**
	 * 判断date是否在区间内，根据年份判断
	 * @return
	 */
	public static boolean dateInSection(Date bornDate,String sections){
		String[] sectionsArr = sections.split("-");
		if(sectionsArr.length!=2){
			throw new RuntimeException("Sections each length must be two!");
		}
		int[] section = new int[sectionsArr.length];
		for (int i = 0; i < sectionsArr.length; i++) {
			section[i] = Integer.parseInt(sectionsArr[i]);
		}
		Calendar mycalendar=Calendar.getInstance();
		int year = mycalendar.get(Calendar.YEAR);
		int startYear = year - section[1];
		int endYear = year - section[0];
		
		Calendar bornCal = Calendar.getInstance();
		bornCal.setTime(bornDate);
		int bornYear = bornCal.get(Calendar.YEAR);
		
		if(bornYear>=startYear && bornYear<=endYear){
			return true;
		}
		return false;
	}
	public static Date parseToDate(String strToParse) throws ParseException{
		return new DateUtils().parseDate(strToParse, DATE_FORMAT_01,DATE_FORMAT_02,DATE_FORMAT_04,DATE_FORMAT_05,DATE_FORMAT_06);
	}
	/**
	 * 
	 * @param type
	 *            1:当月第一天 2：当月最后一天 else 当天
	 * @return
	 */
	public static String getDay(int type, String pattern) {
		Calendar cale = null;
		cale = Calendar.getInstance();

		// 获取当月第一天和最后一天
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String firstday, lastday;
		// 获取前月的第一天
		cale = Calendar.getInstance();
		if (type == 1) {
			cale.add(Calendar.MONTH, 0);
			cale.set(Calendar.DAY_OF_MONTH, 1);
			firstday = format.format(cale.getTime());
			return firstday;
		} else if (type == 2) {
			// 获取前月的最后一天
			cale.add(Calendar.MONTH, 1);
			cale.set(Calendar.DAY_OF_MONTH, 0);
			lastday = format.format(cale.getTime());
			return lastday;
		}
		return format.format(new Date());
	}

	public static int getYear() {
		Calendar a = Calendar.getInstance();
		return a.get(Calendar.YEAR);
	}

	public static int getMonth() {
		Calendar a = Calendar.getInstance();
		return a.get(Calendar.MONTH) + 1;
	}
}
