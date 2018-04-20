package org.skynet.frame.util.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

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

	public static Date addDay(int num) {
		return DateUtils.addDays(new Date(), num);
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
