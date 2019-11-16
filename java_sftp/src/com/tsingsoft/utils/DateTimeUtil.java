/*
 * Copyright (c) 2005-2014 Beijing TsingSoft Technology Co.,Ltd.
 * All rights reserved.
 * Created on 2018-6-1
 * 
 * HNPV_EnergyCollect
 * com.tsingsoft.utils
 * DateTimeUtil.java
 */
package com.tsingsoft.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期相关的工具
 * 
 * @author wqk
 * @since 2018-6-1 下午10:00:44
 * @version
 */
public class DateTimeUtil {

	/**
	 * 获得当年
	 * 
	 * @return yyyy
	 */
	public static String getYYYY() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		Date date = new Date();
		String yyyy = sdf.format(date);
		return yyyy;
	}

	/**
	 * 获得当年月
	 * 
	 * @return yyyyMM
	 */
	public static String getYM() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Date date = new Date();
		String yyyyMM = sdf.format(date);
		return yyyyMM;
	}

	/**
	 * 获得当年-月
	 * 
	 * @return yyyy-MM
	 */
	public static String addMonths(String pattern, int interval) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = addMonths(new Date(), interval);
		String yyyyMM = sdf.format(date);
		return yyyyMM;
	}

	public static java.util.Date addMonths(java.util.Date date, int amount) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(Calendar.MONTH, amount);
		return ca.getTime();
	}
}
