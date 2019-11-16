/*
 * Copyright (c) 2005-2014 Beijing TsingSoft Technology Co.,Ltd.
 * All rights reserved.
 * Created on 2018-4-28
 * 
 * SFTPProcessor
 * com.tsingsoft.download
 * SFTPProcessor.java
 */
package com.tsingsoft.sftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.tsingsoft.utils.PropertyUtil;

/**
 * ������sftp�ϴ��ļ�
 * 
 * @author
 * @since 2018-06-03
 * @version
 */
public class SFTPProcessor {

    private static Session     session = null;

    private static ChannelSftp channel = null;

    private static Logger      logger  = Logger.getLogger(SFTPProcessor.class);

    // ����ģʽ����ֹ�����������
    private static class LazySFTP {

        private static final SFTPProcessor INSTANCE = new SFTPProcessor();
    }

    private SFTPProcessor() {
    }

    public static final SFTPProcessor getInstance() {
        return LazySFTP.INSTANCE;
    }

    /**
     * 
     * ���ܣ�sftp����
     * ���ߣ�wqk
     * ���ڣ�2019-4-25 ����01:17:58
     * 
     * @param proper
     * @param isDownLoad
     *            �Ƿ������أ�trueΪ���أ�false Ϊ�ϴ�
     * @return
     */
    public ChannelSftp getConnect(PropertyUtil proper, boolean isDownLoad) {
        String host = proper.getValue("up.sftpHost");
        int port = Integer.valueOf(proper.getValue("up.sftpPort"));
        String userName = proper.getValue("up.sftpUserName");
        String password = proper.getValue("up.sftpPassword");
        if (isDownLoad) {
            host = proper.getValue("dw.sftpHost");
            port = Integer.valueOf(proper.getValue("dw.sftpPort"));
            userName = proper.getValue("dw.sftpUserName");
            password = proper.getValue("dw.sftpPassword");
        }
        JSch jsch = new JSch(); // ����JSch����
        // �����û���,����ip,�˿ڻ�ȡһ��Session����
        try {
            logger.info("sftp [ ftpHost = " + host + "  ftpPort = " + port + "  ftpUserName = "
                        + userName + "  ftpPassword = " + password + " ]");
            session = jsch.getSession(userName, host, port);
            session.setPassword(password); // ��������
            String ftpTO = "";
            if (!(ftpTO == null || "".equals(ftpTO))) {
                int ftpTimeout = Integer.parseInt(ftpTO);
                session.setTimeout(ftpTimeout); // ����timeoutʱ��
            }
            // ����һ����������ܳ׷����˱仯���;ܾ����ӡ�
            session.setConfig("StrictHostKeyChecking", "no");
            // Ĭ��ֵ�� ��yes�� �˴�����������SFTP��������DNS���������⣬���UseDNS����Ϊ��no��
            session.setConfig("UseDNS", "no");
            session.connect(); // ���ɹ���Session��������
            logger.debug("Session connected.");
            logger.debug("Opening SFTP Channel.");
            channel = (ChannelSftp) session.openChannel("sftp"); // ��SFTPͨ��
            channel.connect(); // ����SFTPͨ��������
            logger.debug("Connected successfully to ftpHost = " + host + ",as ftpUserName = "
                         + userName + ", returning: " + channel);
        } catch (JSchException e) {
            logger.error("sftp getConnect error : " + e.getMessage());
        }
        return channel;
    }

    /**
     * �ļ��ϴ�
     * 
     * @param uploadDir
     *            Զ��Ŀ¼
     * @param uploadFileName
     *            Զ���ļ�����
     * @param in
     *            ������
     */
    public boolean uploadFile(String remotePath, String fileName, InputStream in) {

        boolean isSuccess = false;
        // String uploadDirs[] = uploadDir.split("/");
        // for(String folder : uploadDirs ){
        // //linux�е�·����ͷΪ���滻��/
        // if(folder.length() == 0){
        // folder = "/";
        // }
        // try {
        // channel.cd(folder);
        // }
        // catch (SftpException e) {
        // try {
        // channel.mkdir(folder);
        // channel.cd(folder);
        // }
        // catch (SftpException e1) {
        // e1.printStackTrace();
        // }
        // }
        // }
        //		
        // try {
        // channel.put(in, uploadFileName);
        // }
        // catch (SftpException e) {
        // e.printStackTrace();
        // }
        try {
            channel.cd(remotePath);// �л�Զ��Ŀ¼
            channel.put(in, fileName);// �ϴ��ļ�
            isSuccess = true;
        } catch (SftpException e) {
            logger.error("sftp �ϴ��ļ�ʧ�ܣ�" + e.getMessage());
        }
        return isSuccess;
    }

    /**
     * �ļ�����
     */
    public void downLoadFile(String remotePath, String fileName, String localPath) {
        try {
            channel.cd(remotePath);
            File file = new File(localPath + File.separator + fileName);
            channel.get(fileName, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * �ر�server
     */
    public void disconnect() {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }
}
