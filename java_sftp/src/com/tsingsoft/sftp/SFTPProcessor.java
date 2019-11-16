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
 * 描述：sftp上传文件
 * 
 * @author
 * @since 2018-06-03
 * @version
 */
public class SFTPProcessor {

    private static Session     session = null;

    private static ChannelSftp channel = null;

    private static Logger      logger  = Logger.getLogger(SFTPProcessor.class);

    // 单利模式，防止创建多个对象
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
     * 功能：sftp对象
     * 作者：wqk
     * 日期：2019-4-25 下午01:17:58
     * 
     * @param proper
     * @param isDownLoad
     *            是否是下载，true为下载，false 为上传
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
        JSch jsch = new JSch(); // 创建JSch对象
        // 按照用户名,主机ip,端口获取一个Session对象
        try {
            logger.info("sftp [ ftpHost = " + host + "  ftpPort = " + port + "  ftpUserName = "
                        + userName + "  ftpPassword = " + password + " ]");
            session = jsch.getSession(userName, host, port);
            session.setPassword(password); // 设置密码
            String ftpTO = "";
            if (!(ftpTO == null || "".equals(ftpTO))) {
                int ftpTimeout = Integer.parseInt(ftpTO);
                session.setTimeout(ftpTimeout); // 设置timeout时候
            }
            // 并且一旦计算机的密匙发生了变化，就拒绝连接。
            session.setConfig("StrictHostKeyChecking", "no");
            // 默认值是 “yes” 此处是由于我们SFTP服务器的DNS解析有问题，则把UseDNS设置为“no”
            session.setConfig("UseDNS", "no");
            session.connect(); // 经由过程Session建树链接
            logger.debug("Session connected.");
            logger.debug("Opening SFTP Channel.");
            channel = (ChannelSftp) session.openChannel("sftp"); // 打开SFTP通道
            channel.connect(); // 建树SFTP通道的连接
            logger.debug("Connected successfully to ftpHost = " + host + ",as ftpUserName = "
                         + userName + ", returning: " + channel);
        } catch (JSchException e) {
            logger.error("sftp getConnect error : " + e.getMessage());
        }
        return channel;
    }

    /**
     * 文件上传
     * 
     * @param uploadDir
     *            远程目录
     * @param uploadFileName
     *            远程文件名称
     * @param in
     *            输入流
     */
    public boolean uploadFile(String remotePath, String fileName, InputStream in) {

        boolean isSuccess = false;
        // String uploadDirs[] = uploadDir.split("/");
        // for(String folder : uploadDirs ){
        // //linux中的路径开头为空替换成/
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
            channel.cd(remotePath);// 切换远程目录
            channel.put(in, fileName);// 上传文件
            isSuccess = true;
        } catch (SftpException e) {
            logger.error("sftp 上传文件失败：" + e.getMessage());
        }
        return isSuccess;
    }

    /**
     * 文件下载
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
     * 关闭server
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
