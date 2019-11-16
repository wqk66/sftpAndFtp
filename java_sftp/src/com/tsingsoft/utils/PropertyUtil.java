package com.tsingsoft.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

/**
 * 属性文件读取工具类
 */
public class PropertyUtil {

	private Properties proper = null;

	public InputStream is = null;

	public PropertyUtil(String propertiesName) {
		String fileName = propertiesName + ".properties";

		proper = new Properties();
		ClassLoader classLoader = Thread.currentThread()
		        .getContextClassLoader();

		try {
			proper.load(classLoader.getResourceAsStream(fileName));
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 淮南电量采集，读取properties文件
	 * add by wqk
	 * 
	 * @param filePath
	 * @param propertiesName
	 */
	public PropertyUtil(String filePath, String propertiesName) {
		String fileName = filePath + propertiesName + ".properties";

		try {
			if (fileName != null) {
				File file = new File(fileName);
				if (file.exists()) {
					is = new FileInputStream(file);
				}
			}

			is = is == null ? PropertyUtil.class.getClassLoader()
			        .getResourceAsStream(propertiesName + ".properties") : is;

			proper = new Properties();

			proper.load(new InputStreamReader(is, "UTF-8"));

			// props.load(is);
		}
		catch (Exception e) {
			// 此处不会发出异常，不做处理
		}
		finally {
			close();
		}
	}

	/**
	 * 得到存放在plan.properties中的系统定义的字符串
	 * 
	 * @param key
	 *            资源标识
	 * @return 资源值
	 */
	public String getValue(String key) {
		try {
			return proper.getProperty(key);
		}
		catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	/**
	 * 获得值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getValue(String key, String defaultValue) {

		String val = getValue(key);
		return (val == null) ? defaultValue : val;

	}

	/**
	 * 写入值
	 * 
	 * @param key
	 * @param value
	 */
	public void setValue(String key, String value) {
		proper.setProperty(key, value);
	}

	public void store(OutputStream out) {
		try {
			proper.store(out, "");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭流文件
	 */
	private void close() {
		try {
			if (is != null) {
				is.close();
			}
		}
		catch (IOException e) {
			// 此处不会发出异常，不做处理
		}
	}
}
