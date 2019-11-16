/*
 * Copyright (c) 2005-2014 Beijing TsingSoft Technology Co.,Ltd.
 * All rights reserved.
 * Created on 2018-6-1
 * 
 * HNPV_EnergyCollect
 * com.tsingsoft.run
 * EnergyCollect.java
 */
package com.tsingsoft.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tsingsoft.DB.DBConnection;
import com.tsingsoft.DB.QueryDB;
import com.tsingsoft.sftp.SFTPProcessor;
import com.tsingsoft.utils.DateTimeUtil;
import com.tsingsoft.utils.FileUtil;
import com.tsingsoft.utils.PropertyUtil;
import com.tsingsoft.utils.SysConstant;

/**
 * 淮南电量采集入口，从电量采集系统的数据库中采集数据，并将数据写入properties文件
 * 
 * @author
 * @since 2018-6-1 下午09:56:46
 * @version
 */
public class EnergyCollect {

	/**
	 * 程序入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		PropertyUtil proper = new PropertyUtil("config");
		String url = proper.getValue("URL",
		        "jdbc:oracle:thin:@192.168.1.21:1521:tmsdb");
		String userName = proper.getValue("userName");
		String password = proper.getValue("password");
		String sql = proper.getValue("SQL").replace("AAAA",
		        DateTimeUtil.getYYYY());
		String strValue = proper.getValue("ids");
		List<String> ids = strFormatList(strValue, ";");
		Connection conn = DBConnection.getConnection(url, userName, password);
		QueryDB queryDB = new QueryDB();
		String dySQL = queryDB.createdSQL(sql, ids, DateTimeUtil.addMonths(
		        "yyyy-MM", -1), "LIKE");
		Map<String, String> datas = new HashMap<String, String>();
		Map<String, String> DYDatas = null;// 当月采集电量
		Map<String, String> NLJDatas = null;// 年累计采集电量
		DYDatas = queryDB.getDYEnergy(conn, dySQL);
		String nljSQL = queryDB.createdSQL(sql, ids, DateTimeUtil.addMonths(
		        "yyyy-MM", -1)
		        + "-31", "<=");
		NLJDatas = queryDB.getDYEnergy(conn, nljSQL);
		generateMap(datas, DYDatas, "0");
		generateMap(datas, NLJDatas, "1");

		String localDirPath = proper.getValue("localDirPath",
		        "D:\\software\\hn_collect_energy\\")
		        + DateTimeUtil.getYYYY() + "\\";// 本地文件路径

		String fileName = SysConstant.FILENAME
		        + DateTimeUtil.addMonths("yyyyMM", -1) + SysConstant.FILESUFFIX;// 文件名称
		FileUtil fileUtil = new FileUtil();
		boolean operFlag = fileUtil
		        .createPropertiesFile(localDirPath, fileName);
		if (operFlag) {
			writeDataToFile(datas, localDirPath, fileName);
		}

		SFTPProcessor sftpProcessor = SFTPProcessor.getInstance();
		sftpProcessor.getConnect(proper,false);
		String uploadDir = proper.getValue("uploadDir");// 本地上传远程服务器文件路径
		InputStream in = null;
		try {
			in = new FileInputStream(new File(localDirPath + fileName));
			sftpProcessor.uploadFile(uploadDir, fileName, in);// 上传文件
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (in != null) {
					in.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			sftpProcessor.disconnect();
		}
	}

	/**
	 * 将map格式的数据写入文件
	 * 
	 * @param datas
	 * @param filePath
	 * @param fileName
	 */
	public static void writeDataToFile(Map<String, String> datas,
	        String filePath, String fileName) {
		PropertyUtil proper = new PropertyUtil(filePath, fileName);
		OutputStream out = null;
		try {
			out = new FileOutputStream(filePath + fileName);
			for (Map.Entry<String, String> entry : datas.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				proper.setValue(key, value);
			}
			proper.store(out);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			try {
				out.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将字符串转换成集合
	 * 
	 * @param strValue
	 * @param seperator
	 *            分隔符
	 * @return list
	 */
	public static List<String> strFormatList(String strValue, String seperator) {
		List<String> list = null;
		if (strValue != null) {
			String[] strArr = strValue.split(seperator);
			list = Arrays.asList(strArr);
		}
		return list;
	}

	/**
	 * 将采集的月电量和年累计电量合并到新的map中
	 * 
	 * @param newMap
	 * @param oldMap
	 * @param ctrlFlag
	 */
	public static void generateMap(Map<String, String> newMap,
	        Map<String, String> oldMap, String ctrlFlag) {
		BigDecimal bigDecimal = null;
		for (Map.Entry<String, String> entry : oldMap.entrySet()) {
			Integer key = Integer.valueOf(entry.getKey());
			bigDecimal = new BigDecimal(Double.valueOf(entry.getValue()));
			double value = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)
			        .doubleValue();
			if (ctrlFlag.equals("0")) {// 月采集电量总量
				switch (key) {
					case 4075:
						newMap.put("dlcj_" + SysConstant.HN_FTDDL_REPORTID,
						        String.valueOf(value));
						newMap.put("dlcj_" + SysConstant.HN_FTDGFD_REPORTID,
						        String.valueOf(value));
						break;
					case 4076:
						newMap.put("dlcj_" + SysConstant.HN_FTDHD_REPORTID,
						        String.valueOf(value));
						break;
					case 4081:
						newMap.put("dlcj_" + SysConstant.HN_FBZDL_REPORTID,
						        String.valueOf(value));// 当月分布式光伏总发电量
						break;
					case 980:
						newMap.put("dlcj_" + SysConstant.HN_DYZDL_REPORTID,
						        String.valueOf(value));// 当月地区总用电量
						break;
					default:
						break;
				}
			}
			else {// 年累计采集电量总量
				switch (key) {
					case 4075:
						newMap.put("dlcj_" + SysConstant.HN_LJGFDL_REPORTID,
						        String.valueOf(value));
						break;
					case 4081:
						newMap.put("dlcj_" + SysConstant.HN_NLJDL_REPORTID,
						        String.valueOf(value));
						break;
					default:
						break;
				}
			}
		}
	}
}
