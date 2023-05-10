package com.alita.framework.sftp;

import com.jcraft.jsch.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 文件工具类
 *
 * <p>通过 ssh 协议连接服务器，创建 {@code ChannelSftp},{@code ChannelExec},{@code ChannelShell} 通道
 * <ul>
 * <li>所有的文件路径必须以'/'开头和结尾，否则路径最后一部分会被当做是文件名</li>
 * <li>方法出现异常的时候，会关闭sftp连接（但是不会关闭session和channel），异常会抛出</li>
 * </ul>
 *
 * @author Zhang Liang
 * @since 1.0
 *
 * @see com.jcraft.jsch.JSch
 */
@SuppressWarnings({"unchecked"})
public class Jsch implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(Jsch.class);

    private String host;
    private int port;
    private String userName;
    private String password;
    private String sftpMainDir;

    /** 默认端口号 {@value} */
    private static final int defaultPort = 22;

    /** 超时时间 {@value} */
    private static int timeout = 60000;

    private ChannelSftp channelSftp;
    private ChannelExec channelExec;
    private ChannelShell channelShell;
    private Session session;


    /**
     * 构造方法
     */
    public Jsch() {
    }

    /**
     * 构造方法
     *
     * @param host 主机名
     * @param port 端口
     * @param userName 用户名
     * @param password 密码
     * @param sftpMainDir 用户主目录，如 {@code /app/data}
     */
    public Jsch(String host, int port, String userName, String password, String sftpMainDir) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.sftpMainDir = sftpMainDir;
    }


    /**
     * 获取 {@code ChannelSftp} 通道
     *
     * @return {@code ChannelSftp}
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     */
    public ChannelSftp getChannelSftpConnect() throws JSchException {
        if (port == 0) {
            this.port = defaultPort;
        }
        JSch jsch = new JSch();
        session = jsch.getSession(this.userName, this.host, this.port);
        session.setPassword(this.password);
        Properties properties = new Properties();
        properties.put("StrictHostKeyChecking", "no");
        session.setConfig(properties);
        session.setTimeout(timeout);
        session.connect();
        channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect(timeout);

        return channelSftp;
    }


    /**
     * 获取 {@code ChannelExec} 通道
     *
     * @return {@code ChannelExec}
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     */
    public ChannelExec getChannelExecConnect() throws JSchException {
        if (port == 0) {
            this.port = defaultPort;
        }
        JSch jsch = new JSch();
        session = jsch.getSession(this.userName, this.host, this.port);
        session.setPassword(this.password);
        Properties properties = new Properties();
        properties.put("StrictHostKeyChecking", "no");
        session.setConfig(properties);
        session.setTimeout(timeout);
        session.connect();
        channelExec = (ChannelExec) session.openChannel("exec");

        return channelExec;
    }


    /**
     * 获取 {@code ChannelExec} 通道
     *
     * @return {@code ChannelExec}
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     */
    public ChannelShell getChannelShellConnect() throws JSchException {
        if (port == 0) {
            this.port = defaultPort;
        }
        JSch jsch = new JSch();
        session = jsch.getSession(this.userName, this.host, this.port);
        session.setPassword(this.password);
        Properties properties = new Properties();
        properties.put("StrictHostKeyChecking", "no");
        session.setConfig(properties);
        session.setTimeout(timeout);
        session.connect();
        channelShell = (ChannelShell) session.openChannel("shell");
        channelShell.connect(timeout);

        return channelShell;
    }


    /**
     * 从远程服务器上下载文件
     * <p> 通过 {@link #getChannelSftpConnect()} {@code Jsch} 连接远程服务器，从
     * {@link #getIntoSftpMainDir()} 文件主目录和文件相对目录 {@code remotePath} 中
     * 获取文件 {@code fileName}, 下载到本地服务器 {@code localPath}
     *
     * @param remotePath 远程服务器相对于文件主目录的相对目录
     * @param localPath 本地服务器目录
     * @param fileName  文件名
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者找不到指定的文件 {@code fileName}
     */
    public void download(String remotePath, String localPath, String fileName) throws JSchException, SftpException {
        if (StringUtils.isNotBlank(remotePath) && !remotePath.endsWith("/")) {
            remotePath = remotePath + "/";
        }
        if (!localPath.endsWith("/")) {
            localPath = localPath + "/";
        }

        try {
            getChannelSftpConnect();
            getIntoSftpMainDir();
            if (StringUtils.isNotBlank(remotePath)) {
                channelSftp.cd(remotePath);
            }
            File file = new File(localPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            channelSftp.get(fileName, localPath);
            logger.debug("#########【文件下载完成，本地目录: {} 】#########", localPath + fileName);
        } finally {
            close();
        }
    }


    /**
     * 从远程服务器上下载文件
     * <p> 通过 {@link #getChannelSftpConnect()} {@code Jsch} 连接远程服务器，从
     * {@link #getIntoSftpMainDir()} 文件主目录和文件相对目录 {@code remotePath} 中,
     * 下载到本地服务器 {@code localPath}, 并重命名为 {@code newFileName}
     *
     * @param remotePath 远程服务器相对于文件主目录的相对目录
     * @param fileName  远程服务器文件名
     * @param localPath 本地服务器目录
     * @param newFileName 新文件名
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者找不到指定的文件 {@code fileName}
     */
    public void download(String remotePath, String fileName, String localPath, String newFileName) throws JSchException, SftpException {
        if (StringUtils.isNotBlank(remotePath) && !remotePath.endsWith("/")) {
            remotePath = remotePath + "/";
        }
        if (!localPath.endsWith("/")) {
            localPath = localPath + "/";
        }
        try {
            getChannelSftpConnect();
            getIntoSftpMainDir();
            if (StringUtils.isNotBlank(remotePath)) {
                channelSftp.cd(remotePath);
            }
            File file = new File(localPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            this.channelSftp.get(fileName, localPath + newFileName);
            logger.debug("#########【文件下载完成，本地目录: {} 】#########", localPath + newFileName);
        } finally {
            close();
        }
    }


    /**
     * 从远程服务器上下载文件
     * <p> 通过 {@link #getChannelSftpConnect()} {@code Jsch} 连接远程服务器，从
     * {@link #getIntoSftpMainDir()} 文件主目录和文件相对目录 {@code remotePath} 中
     * 获取文件 {@code fileName}, 下载到本地服务器 {@code localPath}
     *
     * <p>具有监控功能的文件下载，通过 {@code FileProgressMonitor} 类来实时感知文件传输进度
     * 并通过 {@link ChannelSftp#OVERWRITE} 覆盖模式，使传输的文件完全覆盖目标文件。不支持断点续传。
     *
     * @param remotePath 远程服务器相对于文件主目录的相对目录
     * @param localPath 本地服务器目录
     * @param fileName 远程服务器文件名
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者找不到指定的文件 {@code fileName}
     */
    public void downloadMonitor(String remotePath, String localPath, String fileName) throws JSchException, SftpException {
        if (StringUtils.isNotBlank(remotePath) && !remotePath.endsWith("/")) {
            remotePath = remotePath + "/";
        }
        if (!localPath.endsWith("/")) {
            localPath = localPath + "/";
        }

        try {
            getChannelSftpConnect();
            getIntoSftpMainDir();
            if (StringUtils.isNotBlank(remotePath)) {
                channelSftp.cd(remotePath);
            }
            File file = new File(localPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            this.channelSftp.get(fileName, localPath, new FileProgressMonitor(), ChannelSftp.OVERWRITE);
            logger.debug("#########【文件下载完成，本地目录: {} 】#########", localPath + fileName);
        } finally {
            close();
        }
    }


    /**
     * 从远程服务器上下载文件，并返回 {@code byte[]}
     * <p> 通过 {@link #getChannelSftpConnect()} {@code Jsch} 连接远程服务器，从
     * {@link #getIntoSftpMainDir()} 文件主目录和文件相对目录 {@code remotePath} 中
     * 获取文件 {@code fileName}, 下载到本地服务器 {@code localPath}, 返回 {@code byte[]} 输出流
     * {@code ByteArrayOutputStream}
     *
     * @param remotePath 远程服务器相对于文件主目录的相对目录
     * @param fileName 文件名
     * @return {@code byte[]}
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者找不到指定的文件 {@code fileName}
     * @throws IOException {@link InputStream#read(byte[])} 解析文件异常
     */
    public byte[] download(String remotePath, String fileName) throws JSchException, SftpException, IOException {
        if (StringUtils.isNotBlank(remotePath) && !remotePath.endsWith("/")) {
            remotePath = remotePath + "/";
        }

        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            getChannelSftpConnect();
            getIntoSftpMainDir();
            if (StringUtils.isNotBlank(remotePath)) {
                channelSftp.cd(remotePath);
            }
            inputStream = this.channelSftp.get(fileName);
            if (Objects.isNull(inputStream)) {
                return null;
            }
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int size;
            while ((size = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, size);
            }
        } catch (IOException ex) {
            logger.error("#########【 文件解析异常 】#########:", ex);
        } finally {
            inputStream.close();
            outputStream.close();
            close();
        }
        logger.debug("#########【文件下载完成】#########");
        byte[] byteArray = outputStream.toByteArray();

        return byteArray;
    }


    /**
     * 批量下载文件
     * <p>下载服务器指定目录下所有文件
     *
     * @param remotePath 远程服务器目录
     * @param localPath 本地服务器目录
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录
     */
    public void batchDownload(String remotePath, String localPath) throws JSchException, SftpException {
        try {
            if (StringUtils.isNotBlank(remotePath) && !remotePath.endsWith("/")) {
                remotePath = remotePath + "/";
            }
            if (!localPath.endsWith("/")) {
                localPath = localPath + "/";
            }

            getChannelSftpConnect();
            getIntoSftpMainDir();
            if (StringUtils.isNotBlank(remotePath)) {
                channelSftp.cd(remotePath);
            }
            String path = this.channelSftp.pwd();
            logger.debug("#########【当前目录：{}】#########", path);
            File file = new File(localPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            Vector<ChannelSftp.LsEntry> vectors = this.channelSftp.ls(path);
            if (Objects.isNull(vectors)) {
                logger.debug("#########【指定目录：{}, 文件个数为0】#########", remotePath);
                return;
            }
            for (ChannelSftp.LsEntry vector: vectors) {
                String filename = vector.getFilename();
                SftpATTRS sftpATTRS = vector.getAttrs();
                if (!sftpATTRS.isDir() && !".".equals(filename) && !"..".equals(filename)) {
                    this.channelSftp.get(filename, localPath);
                }
            }
            logger.debug("#########【当前下载目录为: {}, 下载文件数量为: {}】#########", path, vectors.size()-2);
        } finally {
            close();
        }
    }


    /**
     * 文件上传
     * <p> 通过 {@link #getChannelSftpConnect()} {@code Jsch} 连接远程服务器，
     * {@link #recursionMkdirs(String)} 判断服务器目录是否存在，若不存在则创建，
     * 查询本地文件 {@code fileName} 是否存在，若不存在抛出异常 {@code FileNotFoundException}
     * 若存在，则进行上传
     *
     * @param localPath 本地服务器路径
     * @param remotePath 远程服务器路径
     * @param fileName 远程服务器文件名
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者上传文件失败
     * @throws FileNotFoundException 本地文件未找到
     */
    public void upload(String localPath, String remotePath, String fileName) throws JSchException, SftpException, FileNotFoundException {
        try {
            getChannelSftpConnect();
            recursionMkdirs(remotePath);
            File file = new File(localPath);
            if (!file.exists()) {
                throw new FileNotFoundException("#########【本地文件: " + localPath + "不存在！】#########");
            }
            String path = this.channelSftp.pwd();
            this.channelSftp.put(localPath, fileName);
            logger.debug("#########【文件 {} 上传至 {} 完成！】#########", fileName, path);
        } finally {
            close();
        }
    }


    /**
     * 文件上传
     * <p> 通过 {@link #getChannelSftpConnect()} {@code Jsch} 连接远程服务器，
     * {@link #recursionMkdirs(String)} 判断服务器目录是否存在，若不存在则创建，
     * 查询本地文件 {@code fileName} 是否存在，若不存在抛出异常 {@code FileNotFoundException},
     * 若存在，则进行上传.
     * <p>使用 {@link ChannelSftp#put(String src, String dst)} 这个方法时，dst可以是目录，
     * 当dst是目录时，上传后的目标文件名将与src文件名相同
     *
     * @param localPath 本地服务器路径
     * @param remotePath 远程服务器路径
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者上传文件失败
     * @throws FileNotFoundException 本地文件未找到
     */
    public void upload(String localPath, String remotePath) throws JSchException, SftpException, FileNotFoundException {
        try {
            getChannelSftpConnect();
            recursionMkdirs(remotePath);
            File file = new File(localPath);
            if (!file.exists()) {
                throw new FileNotFoundException("#########【本地文件: " + localPath + "不存在！】#########");
            }
            String path = this.channelSftp.pwd();
            this.channelSftp.put(localPath, file.getName());
            logger.debug("#########【文件 {} 上传至 {} 完成！】#########", localPath, path);
        } finally {
            close();
        }
    }


    /**
     * 通过文件流进行文件上传
     * <p>通过 {@link #getChannelSftpConnect()} {@code Jsch} 连接远程服务器，
     * {@link #recursionMkdirs(String)} 判断服务器目录是否存在，若不存在则创建，
     * 判断 {@code InputStream} 是否为空，若不存在抛出异常 {@code IllegalArgumentException}
     * 若存在，则通过 {@code InputStream} 进行上传
     *
     * @param inputStream 本地文件输入流
     * @param remotePath 远程服务器路径
     * @param fileName 远程服务器文件名
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者上传文件失败
     */
    public void upload(InputStream inputStream, String remotePath, String fileName) throws JSchException, SftpException {
        if (Objects.isNull(inputStream)) {
            throw new IllegalArgumentException("#########【文件流必须不能为空！】#########");
        }
        try {
            getChannelSftpConnect();
            recursionMkdirs(remotePath);
            this.channelSftp.put(inputStream, fileName);
            logger.debug("#########【文件 {} 上传完成！】#########", fileName);
        } finally {
            close();
        }
    }


    /**
     * 文件上传
     * <p>具有监控功能的文件上传，通过 {@code FileProgressMonitor} 类来实时感知文件传输进度
     * 并通过 {@link ChannelSftp#OVERWRITE} 覆盖模式，使传输的文件完全覆盖目标文件。不支持断点续传。
     * 若想使用断点续传，请将
     * <pre>
     * {@code this.channelSftp.put(localPath, fileName, new FileProgressMonitor(), ChannelSftp.OVERWRITE);}
     * </pre>
     * 修改为
     * <pre>
     * {@code this.channelSftp.put(localPath, fileName, new FileProgressMonitor(), ChannelSftp.RESUME);}
     * </pre>
     *
     * @param localPath 本地服务器路径
     * @param remotePath 远程服务器路径
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者上传文件失败
     * @throws FileNotFoundException 本地文件未找到
     */
    public void uploadMonitor(String localPath, String remotePath) throws JSchException, SftpException, FileNotFoundException {
        try {
            getChannelSftpConnect();
            recursionMkdirs(remotePath);
            File file = new File(localPath);
            if (!file.exists()) {
                throw new FileNotFoundException("#########【本地文件: " + localPath + "不存在！】#########");
            }
            this.channelSftp.put(localPath, file.getName(), new FileProgressMonitor(), ChannelSftp.OVERWRITE);
        } finally {
            close();
        }
    }


    /**
     * 文件节流上传
     * <p>通过 {@link #getChannelSftpConnect()} {@code Jsch} 连接远程服务器，
     * {@link #recursionMkdirs(String)} 判断服务器目录是否存在，若不存在则创建，
     * 判断 {@code InputStream} 是否为空，若不存在抛出异常 {@code IllegalArgumentException}
     * 若存在，则进行上传，使传输的文件完全覆盖目标文件。不支持断点续传。
     * 若想使用断点续传，请将
     * <pre>
     * {@code this.channelSftp.put(localPath, fileName, new FileProgressMonitor(), ChannelSftp.OVERWRITE);}
     * </pre>
     * 修改为
     * <pre>
     * {@code this.channelSftp.put(localPath, fileName, new FileProgressMonitor(), ChannelSftp.RESUME);}
     * </pre>
     * <p>通过设定缓冲区 {@code byte[]}，每次上传特定大小的数据，来减少 {@code JVM Memory} 的消耗
     *
     * @param localPath 本地服务器路径
     * @param remotePath 远程服务器路径
     * @param fileName 远程服务器文件名
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者上传文件失败
     * @throws IOException 本地文件未找到
     */
    public void throttleUpload(String localPath, String remotePath, String fileName) throws JSchException, SftpException, IOException {
        try {
            getChannelSftpConnect();
            recursionMkdirs(remotePath);
            File file = new File(localPath);
            if (!file.exists()) {
                throw new FileNotFoundException("#########【本地文件: " + localPath + "不存在！】#########");
            }
            OutputStream outputStream = this.channelSftp.put(fileName);
            if (Objects.nonNull(outputStream)) {
                byte[] buffer = new byte[1024 * 256];
                int size;
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(localPath);
                    while ((size = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, size);
                        outputStream.flush();
                    }
                } finally {
                    inputStream.close();
                    outputStream.close();
                }
            }
        } finally {
            close();
        }
    }


    /**
     * 批量上传文件
     * <p>批量上传本地服务器指定目录下所有文件到远程服务器
     *
     * @param localPath 本地服务器目录
     * @param remotePath 远程服务器目录
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者上传文件失败
     * @throws FileNotFoundException 本地文件未找到
     */
    public void batchUpload(String localPath, String remotePath) throws JSchException, SftpException, FileNotFoundException {
        if (!localPath.endsWith("/")) {
            localPath = localPath + "/";
        }

        try {
            getChannelSftpConnect();
            recursionMkdirs(remotePath);
            File file = new File(localPath);
            if (!file.exists() || !file.isDirectory()) {
                throw new FileNotFoundException("#########【本地目录: " + localPath + " 不存在！】#########");
            }
            String[] files = file.list();
            if (ArrayUtils.isEmpty(files)) {
                return;
            }
            for (String fileName: files) {
                this.channelSftp.put(localPath + fileName, fileName);
            }
            logger.debug("#########【本次上传目录：{}, 上传文件个数：{}】#########", remotePath, files.length);
        } finally {
            close();
        }
    }


    /**
     * 删除文件
     * <p>删除服务器指定目录 {@code remotePath}，指定 {@code fileName} 的文件
     *
     * @param remotePath 远程服务器文件路径
     * @param fileName 远程服务器文件名
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者删除文件失败
     */
    public void rmFile(final String remotePath, final String fileName) throws JSchException, SftpException {
        try {
            getChannelSftpConnect();
            getIntoSftpMainDir();
            if (StringUtils.isNotBlank(remotePath)) {
                channelSftp.cd(remotePath);
            }
            this.channelSftp.rm(fileName);
        } finally {
            close();
        }
    }


    /**
     * 删除文件
     * <p>删除服务器上指定目录的文件 {@code remotePathFile}
     * 若 {@code remotePathFile} 为目录，则执行 {@link ChannelSftp#rm(String)} 时将抛出异常.
     *
     * @param remotePathFile 远程服务器文件路径(含文件名)
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录，或者删除文件失败
     */
    public void rmFile(final String remotePathFile) throws JSchException, SftpException {
        String retemoPath = "";
        if (remotePathFile.startsWith("/")) {
            retemoPath = StringUtils.substringAfter(remotePathFile, "/");
        }

        if (remotePathFile.endsWith("/")) {
            retemoPath = StringUtils.substringBeforeLast(retemoPath, "/");
        }
        String[] paths = retemoPath.split("/");
        StringBuilder temp = new StringBuilder();
        for (int i=0; i<paths.length-1; i++) {
            temp.append("/" + paths[i]);
        }

        try {
            getChannelSftpConnect();
            getIntoSftpMainDir();
            this.channelSftp.cd(temp.toString());
            this.channelSftp.rm(paths[paths.length-1]);
        } finally {
            close();
        }
    }


    /**
     * 判断该目录是否存在
     *
     * @param remotePath 远程服务器文件路径
     * @return {@code true} or {@code false}
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     */
    public boolean isDirectoryExist(final String remotePath) throws JSchException {
        try {
            getChannelSftpConnect();
            getIntoSftpMainDir();
            SftpATTRS sftpATTRS = this.channelSftp.lstat(remotePath);
            return sftpATTRS.isDir();
        } catch (SftpException ex) {
            return false;
        } finally {
            close();
        }
    }


    /**
     * 返回指定目录下所有文件
     *
     * @param remotePath 远程服务器文件路径
     * @return {@code List<String>}
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws SftpException 输入参数不正确，导致无法进入指定的 {@code remotePath} 目录
     */
    public List<String> listFiles(final String remotePath) throws JSchException, SftpException {
        List<String> list = new ArrayList<>();
        try {
            getChannelSftpConnect();
            getIntoSftpMainDir();
            Vector<ChannelSftp.LsEntry> vectors = this.channelSftp.ls(remotePath);
            for (ChannelSftp.LsEntry entry: vectors) {
                if (!".".equals(entry.getFilename()) && !"..".equals(entry.getFilename())) {
                    list.add(entry.getFilename());
                }
            }
        } finally {
            close();
        }
        return list;
    }


    /**
     * 循环创建目录
     *
     * @param remotePath 服务器相对目录
     * @throws SftpException 创建目录异常
     */
    public void recursionMkdirs(String remotePath) throws SftpException {
        if (!this.sftpMainDir.endsWith("/")) {
            this.sftpMainDir = this.sftpMainDir + "/";
        }
        String baseDir = this.sftpMainDir + remotePath;
        if (baseDir.startsWith("/")) {
            baseDir = StringUtils.substringAfter(baseDir, "/");
        }
        if (baseDir.endsWith("/")) {
            baseDir = StringUtils.substringBeforeLast(baseDir, "/");
        }
        String[] paths = baseDir.split("/");

        StringBuilder temp = new StringBuilder();
        for (int i=0; i<paths.length; i++) {
            temp.append("/" + paths[i]);
            try {
                this.channelSftp.cd(temp.toString());
            } catch (SftpException ex) {
                this.channelSftp.mkdir(temp.toString());
                this.channelSftp.cd(temp.toString());
                logger.debug("#########【目录新建完成: {} 】#########", temp);
            }
        }
    }


    /**
     * 进入文件主目录 {@code this.sftpMainDir}
     *
     * @throws SftpException 在远程服务器上创建目录异常
     */
    private void getIntoSftpMainDir() throws SftpException {
        if (StringUtils.isBlank(this.sftpMainDir)) {
            throw new IllegalArgumentException("#########【文件主目录：sftpMainDir 必须不能为空】#########");
        }
        try {
            channelSftp.cd(this.sftpMainDir);
        } catch (SftpException ex) {
            logger.error("#########【主目录:{} 没有创建】#########", this.sftpMainDir);
            recursionMkdirs("");
        }
    }


    /**
     * 执行单条的 Linux 命令，并输出响应的内容
     *
     * @param command 命令
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws IOException 输入流解析异常
     */
    public void execCommand(final String command) throws JSchException, IOException {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            getChannelExecConnect();
            this.channelExec.setCommand(command);
            this.channelExec.connect(timeout);
            inputStream = this.channelExec.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logger.debug("#########【line 的值】#########:" + line);
            }
        } finally {
            inputStream.close();
            bufferedReader.close();
            close();
        }
    }


    /**
     * 批量执行 shell 命令，并输出响应的内容,禁止使用诸如: {@code tail} 等阻塞命令
     *
     * @param args shell 命令集合
     * @throws JSchException 服务器信息错误，导致 {@code Jsch} 建立连接异常
     * @throws IOException 输入流解析异常
     */
    public void batchExecCommands(String ...args) throws JSchException, IOException {
        PrintWriter printWriter = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            getChannelShellConnect();
            this.channelShell.connect();
            outputStream = this.channelShell.getOutputStream();
            printWriter = new PrintWriter(outputStream);
            for (String command: args) {
                printWriter.println(command);
                printWriter.flush();
            }
            printWriter.println("exit");
            printWriter.flush();
            inputStream = this.channelShell.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logger.debug("#########【line 的值】#########:" + line);
            }
        } finally {
            printWriter.close();
            outputStream.close();
            inputStream.close();
            bufferedReader.close();
            close();
        }
    }


    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     */
    @Override
    public void close() {
        if (Objects.nonNull(channelSftp)) {
            this.channelSftp.disconnect();
        }
        if (Objects.nonNull(channelExec)) {
            this.channelExec.disconnect();
        }
        if (Objects.nonNull(channelShell)) {
            this.channelShell.disconnect();
        }
        if (Objects.nonNull(this.session)) {
            this.session.disconnect();
        }
    }

    public String getHost() {
        return host;
    }

    public Jsch setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Jsch setPort(int port) {
        this.port = port;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public Jsch setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Jsch setPassword(String password) {
        this.password = password;
        return this;
    }

    public static int getTimeout() {
        return timeout;
    }

    public static void setTimeout(int timeout) {
        Jsch.timeout = timeout;
    }

    public String getSftpMainDir() {
        return sftpMainDir;
    }

    public Jsch setSftpMainDir(String sftpMainDir) {
        this.sftpMainDir = sftpMainDir;
        return this;
    }


    /**
     * 进度监控器
     * <p>进度监控器 {@code JSch} 每次传输一个数据块，就会调用 {@link #count(long)} 方法,
     * 通过 {@link Runnable#run()} 实现文件传输进度通知.
     * <p>
     *  使用 {@link StopWatch#getTime(TimeUnit)} 方法获取本次文件传输耗时
     * </p>
     */
    private static class FileProgressMonitor implements SftpProgressMonitor, Runnable {

        private static final Logger logger = LoggerFactory.getLogger(FileProgressMonitor.class);

        /** 当前接收的总字节数 */
        private long currentCount = 0;
        /** 最终文件大小 */
        private long maxCount = 0;
        /** {@code ScheduledExecutorService} 线程执行间隔时间 */
        private final int delayTime = 1;

        private static final StopWatch stopWatch = new StopWatch();
        private volatile boolean isScheduled = false;
        ScheduledExecutorService executorService;

        /**
         * <p>初始化加载
         *
         * @param op 文件操作类型
         * @param src 起始目录
         * @param dest 目标目录
         * @param max 上传最大区块
         */
        @Override
        public void init(int op, String src, String dest, long max) {
            if (op == SftpProgressMonitor.PUT) {
                logger.debug("#########【 文件开始上传! 】#########");
            }else {
                logger.debug("#########【 文件开始下载! 】#########");
            }

            this.maxCount = max;
            this.currentCount = 0;
            stopWatch.start();
        }

        /**
         * <p>当每次传输了一个数据块后，调用count方法，count方法的参数为这一次传输的数据块大小
         *
         * @param count 数据块大小
         * @return {@code boolean}
         */
        @Override
        public boolean count(long count) {
            if (!isScheduled) {
                createTread();
            }
            this.currentCount += count;

            return count > 0;
        }


        /**
         * <p>创建一个线程每隔一定时间，输出一下上传进度
         */
        public synchronized void createTread() {
            isScheduled = true;
            executorService = Executors.newSingleThreadScheduledExecutor();
            //1秒钟后开始执行，每2杪钟执行一次
            executorService.scheduleWithFixedDelay(this, 1, delayTime, TimeUnit.SECONDS);
        }


        /**
         * <p>当传输结束时，调用end方法
         */
        @Override
        public void end() {
            stopWatch.stop();
            stop();
            logger.debug("#########【 文件传输完成！用时: {} ms  】#########", stopWatch.getTime(TimeUnit.MILLISECONDS));
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            double value = this.currentCount * 100 / (double) this.maxCount;
            NumberFormat format = NumberFormat.getNumberInstance();
            format.setMaximumFractionDigits(2);
            format.setMinimumFractionDigits(2);
            String percent = format.format(value);
            logger.debug("#########【 已传输：{} KB, 传输进度：{}% 】#########", currentCount / 1024, percent);
            if (currentCount == maxCount) {
                stop();
            }
        }

        /**
         * <p>关闭任务线程池
         */
        public void stop() {
            boolean isShutdown = executorService.isShutdown();
            if (!isShutdown) {
                executorService.shutdown();
            }
        }
    }

}
