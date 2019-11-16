/*
 * Copyright (c) 2005-2014 Beijing TsingSoft Technology Co.,Ltd.
 * All rights reserved.
 * Created on 2018-6-1
 * 
 * HNPV_EnergyCollect
 * com.tsingsoft.utils
 * FileUtil.java
 */
package com.tsingsoft.utils;

import java.io.File;
import java.io.IOException;

/**
 * 文件和数据流工具集
 * 
 * @author
 * @since 2018-6-1 下午10:03:50
 * @version
 */
public class FileUtil {

	/**
	 * 生成properties格式的文件
	 * 
	 * @param file
	 *            file格式文件
	 * @return
	 */
	public boolean createPropertiesFile(String dirPath, String fileName) {
		boolean ctrlFlag = false;
		try {
			if (FileUtil.isCreatedDir(dirPath)) {
				File file = new File(dirPath + fileName);
				if (file != null) {
					if (!file.exists()) {
						ctrlFlag = file.createNewFile();
					}
					else {
						ctrlFlag = true;
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return ctrlFlag;
	}

	/**
	 * 判断文件夹是否存在，如果不存在，则新建
	 */
	public static boolean isCreatedDir(String dirPath) {
		boolean isCreated = false;
		File file = new File(dirPath);
		if (file.exists() && file.isDirectory()) {
			isCreated = true;
		}
		else {
			isCreated = file.mkdirs();
		}
		return isCreated;
	}
}
