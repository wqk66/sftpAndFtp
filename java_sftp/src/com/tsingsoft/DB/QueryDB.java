/*
 * Copyright (c) 2005-2014 Beijing TsingSoft Technology Co.,Ltd.
 * All rights reserved.
 * Created on 2018-6-3
 * 
 * HNPV_EnergyCollect
 * com.tsingsoft.DB
 * QueryDB.java
 */
package com.tsingsoft.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 数据库查询对象
 * 
 * @author
 * @since 2018-6-3 上午10:23:24
 * @version
 */
public class QueryDB {

	/**
	 * 采集当月总电量的方法
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param querySQL
	 *            查询sql
	 * @param columnList
	 *            查询的列
	 * @return map
	 */
	public Map<String, String> getDYEnergy(Connection conn, String querySQL) {
		TreeMap<String, String> result = new TreeMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(querySQL);
			while (rs.next()) {
				result.put(rs.getString("id"), rs.getString("total_value"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				rs.close();
				stmt.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 采集年累计总电量的方法
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param querySQL
	 *            查询sql
	 * @param columnList
	 *            查询的列
	 * @return map
	 */
	public Map<String, String> getNLJEnergy(Connection conn, String querySQL) {
		TreeMap<String, String> result = new TreeMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(querySQL);
			while (rs.next()) {
				result.put(rs.getString("id"), rs.getString("total_value"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				rs.close();
				stmt.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 生成查询sql
	 * 
	 * @param querySQL
	 * @param ids
	 * @param queryDateTime
	 * @param querySeperator
	 * @return
	 */
	public String createdSQL(String querySQL, List<String> ids,
	        String queryDateTime, String querySeperator) {
		String subSQL = null;
		if (ids != null && ids.size() > 0) {
			StringBuilder subSB = new StringBuilder(querySQL);
			subSB.append(" AND b.id IN (");
			for (String id : ids) {
				subSB.append(id + ",");
			}

			subSQL = subSB.toString().substring(0,
			        (subSB.toString().length() - 1));
			subSQL = subSQL + ")";
		}
		StringBuilder dateSB = new StringBuilder(
		        " AND to_char(datetime/(60 * 60 * 24)+ to_date('1970-01-01 08:00:00','YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS')");
		if (querySeperator.equalsIgnoreCase("LIKE")) {
			dateSB.append(" LIKE  '" + queryDateTime + "%' group by b.id");
		}
		else {
			dateSB.append(" <=  '" + queryDateTime
			        + " 00:00:00'  group by b.id");
		}
		String returnSQL = subSQL + dateSB.toString();
		return returnSQL;
	}
}
