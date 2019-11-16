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
 * ������
 * @author wqk
 * @since 2019��11��16�� ����12:05:31
 * @version   
 * @see 
 */
public class FtpUtils {

	public  FTPClient getFTPClient(String ftpHost,int ftpPort,String ftpUserName,String ftpPass) {
		FTPClient ftp = null;
		try {
			ftp = new FTPClient();
			//����ftp
			ftp.connect(ftpHost, ftpPort);
			//��½ftp
			ftp.login(ftpUserName, ftpPass);
			//�������ӳ�ʱʱ��
			ftp.setConnectTimeout(50000);
			//���ñ���
			ftp.setControlEncoding("utf-8");
			if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				System.out.println("δ���ӵ�ftp���û��������������");
				ftp.disconnect();
			}else {
				System.out.println("���ӳɹ�");
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("����ʧ��");
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
			System.out.println("ftp�ر�ʧ��");
		}finally {
			if(ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (Exception e2) {
					System.out.println("ftp�ر�ʧ��");
				}
			}
		}
		return false;
	}
	
	
	/**
	 * 
	 * <p>��������: </p>  
	 * @param remotePath
	 * @param fileName
	 * @param localPath
	 * @return
	 * @author: wqk   
	 * @date: 2019��11��16�� ����12:51:56
	 * @return: boolean
	 * @see
	 */
	public boolean downloadFile(String remotePath,String fileName,String localPath) {
		boolean flag = false;//Ĭ��Ϊfalse
		OutputStream out = null;
		FTPClient ftp = null;
		try {
			ftp = getFTPClient("192.168.43.8", 8888, "ftp", "111111");
			
			System.out.println("��ʼ�����ļ�");
			
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
			System.out.println("�ļ����سɹ�");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("�ļ�����ʧ��");
		}finally {
			closeFtp(ftp);
		}
		return flag;
	}
	
	
	public boolean deleteFile(String remotePath,String fileName) {
		boolean flag = false;
		FTPClient ftp = null;
		try {
			System.out.println("��ʼɾ���ļ�");
			ftp = getFTPClient("192.168.43.8", 8888, "ftp", "111111");
			ftp.changeWorkingDirectory(remotePath);
			flag = ftp.deleteFile(fileName);
			System.out.println("�ļ�ɾ���ɹ�");
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
