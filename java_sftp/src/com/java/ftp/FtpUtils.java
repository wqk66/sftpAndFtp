/**  
 * java_sftp
 * com.java.ftp 
 */
package com.java.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * 描述：
 * @author wqk
 * @since 2019年11月16日 上午12:05:31
 * @version   
 * @see 
 */
public class FtpUtils {

	public  FTPClient getFTPClient(String ftpHost,int ftpPort,String ftpUserName,String ftpPass) {
		FTPClient ftp = null;
		try {
			ftp = new FTPClient();
			//连接ftp
			ftp.connect(ftpHost, ftpPort);
			//登陆ftp
			ftp.login(ftpUserName, ftpPass);
			//设置连接超时时间
			ftp.setConnectTimeout(50000);
			//设置编码
			ftp.setControlEncoding("utf-8");
			if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				System.out.println("未连接到ftp，用户名或者密码错误");
				ftp.disconnect();
			}else {
				System.out.println("连接成功");
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("连接失败");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ftp;
	}
	
	public boolean closeFtp(FTPClient ftp) {
		try {
			return ftp.logout();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ftp关闭失败");
		}finally {
			if(ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (Exception e2) {
					System.out.println("ftp关闭失败");
				}
			}
		}
		return false;
	}
	
	
	/**
	 * 
	 * <p>功能描述: </p>  
	 * @param remotePath
	 * @param fileName
	 * @param localPath
	 * @return
	 * @author: wqk   
	 * @date: 2019年11月16日 上午12:51:56
	 * @return: boolean
	 * @see
	 */
	public boolean downloadFile(String remotePath,String fileName,String localPath) {
		boolean flag = false;//默认为false
		OutputStream out = null;
		FTPClient ftp = null;
		try {
			ftp = getFTPClient("192.168.43.8", 8888, "ftp", "111111");
			
			System.out.println("开始下载文件");
			
			if(ftp.changeWorkingDirectory(remotePath)) {
				
				FTPFile[] files = ftp.listFiles();
				for(FTPFile file : files) {
					if(fileName.equalsIgnoreCase(file.getName())) {
						out = new FileOutputStream(new File(localPath+File.separator+fileName));
						flag = ftp.retrieveFile(new String(fileName.getBytes("UTF-8"),"ISO-8859-1"), out);
						out.close();
					}
					
				}
			}
			System.out.println("文件下载成功");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("文件下载失败");
		}finally {
			closeFtp(ftp);
		}
		return flag;
	}
	
	
	public boolean deleteFile(String remotePath,String fileName) {
		boolean flag = false;
		FTPClient ftp = null;
		try {
			System.out.println("开始删除文件");
			ftp = getFTPClient("192.168.43.8", 8888, "ftp", "111111");
			ftp.changeWorkingDirectory(remotePath);
			flag = ftp.deleteFile(fileName);
			System.out.println("文件删除成功");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			closeFtp(ftp);
		}
		return flag;
	}
	public static void main(String[] args) {
		FtpUtils util = new FtpUtils();
//		util.downloadFile("/", "JL_20191114_154500_SCADA_YC.DT", "F://localFtp");
		util.deleteFile("/", "JL_20191112_084500_SCADA_YC.DT");
	}
}
