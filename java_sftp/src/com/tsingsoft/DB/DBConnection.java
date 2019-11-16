/*
 * Copyright (c) 2005-2014 Beijing TsingSoft Technology Co.,Ltd.
 * All rights reserved.
 * Created on 2018-6-3
 * 
 * HNPV_EnergyCollect
 * com.tsingsoft.DB
 * DBConnection.java
 */
package com.tsingsoft.DB;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * oracle数据库连接类
 * 
 * @author
 * @since 2018-6-3 上午08:31:47
 * @version
 */
public class DBConnection {

	/**
	 * 连接数据库方法
	 * 
	 * @param url
	 *            数据库URL
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	public static Connection getConnection(String url, String userName,
	        String password) {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, userName, password);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}
