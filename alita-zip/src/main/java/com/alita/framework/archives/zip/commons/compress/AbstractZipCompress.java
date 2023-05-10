package com.alita.framework.archives.zip.commons.compress;

import com.alita.framework.archives.zip.ZipArchive;
import com.alita.framework.archives.IORuntimeException;
import com.alita.framework.utils.StringUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.*;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

/**
 * ZipArchiveFactory
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-22 22:20
 */
public class AbstractZipCompress implements ZipArchive {

    private static final Logger logger = LoggerFactory.getLogger(AbstractZipCompress.class);

    /**
     * 限制最大使用线程
     */
    private static final int MAX_THREADS = 10;

    /**
     * 默认使用线程数百分比：50%
     */
    private static final double DEFAULT_THREADS_RATIO = 0.5;

    /**
     * 压缩文件/文件夹
     *
     * @param sourceZipFile 原文件/文件夹路径
     * @param targetZipFile 目标文件路径
     */
    @Override
    public void toZip(String sourceZipFile, String targetZipFile) throws Exception {
        this.toZip(sourceZipFile, targetZipFile, ZipMethod.DEFLATED.getCode(), getAvailableThreads(DEFAULT_THREADS_RATIO));
    }

    /**
     * 创建压缩文件
     *
     * @param sourceZipFile 需压缩文件夹/文件
     * @param targetZipFile 压缩包路径 + 文件名
     * @param nThreads      线程数
     */
    public void toZip(String sourceZipFile, String targetZipFile, int nThreads) {
        this.toZip(sourceZipFile, targetZipFile, ZipMethod.DEFLATED.getCode(), nThreads);
    }

    /**
     * 创建压缩文件
     *
     * @param sourceZipFile         需压缩文件夹/文件
     * @param targetZipFile         压缩包路径 + 文件名
     * @param availableThreadsRatio 可用线程比例
     */
    public void toZip(String sourceZipFile, String targetZipFile, double availableThreadsRatio) {
        this.toZip(sourceZipFile, targetZipFile, ZipMethod.DEFLATED.getCode(), getAvailableThreads(availableThreadsRatio));
    }

    /**
     * 创建压缩文件
     *
     * @param sourceZipFile     需压缩文件夹/文件
     * @param targetZipFile     压缩包路径 + 文件名
     * @param zipMethod         压缩方式：ZipMethod.DEFLATED: 压缩/ZipMethod.STORED:不压缩
     * @param nThreads          线程数
     */
    public void toZip(String sourceZipFile, String targetZipFile, int zipMethod, int nThreads) {
        try {
            File sourceFile = new File(sourceZipFile);
            ZipArchiveScatterOutputStream scatterOutput = new ZipArchiveScatterOutputStream(sourceFile.getAbsolutePath(), nThreads);
            compress(sourceFile, scatterOutput, sourceFile.getName(), zipMethod);

            File zipFile = new File(targetZipFile);
            ZipArchiveOutputStream archiveOutput = new ZipArchiveOutputStream(zipFile);
            archiveOutput.setEncoding("UTF-8");
            scatterOutput.writeTo(archiveOutput);
            archiveOutput.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 压缩文件
     *
     * @param sourceFile    待压缩文件
     * @param output        ZipArchive多线程输出流
     * @param zipName       压缩包名称
     * @param method        压缩方式：ZipEntry.DEFLATED: 压缩/ZipEntry.STORED:不压缩
     * @throws IOException  流异常
     */
    private void compress(File sourceFile, ZipArchiveScatterOutputStream output, String zipName, int method) throws IOException {
        if (sourceFile.isFile()) {
            addEntry(zipName, sourceFile, output, method);
            return;
        }
        if (Objects.requireNonNull(sourceFile.listFiles()).length == 0) {
            String fileName = zipName + sourceFile.getAbsolutePath().replace(output.getSourceFilePath(), "") + File.separator;
            addEntry(fileName, sourceFile, output, method);
            return;
        }
        for (File file : sourceFile.listFiles()) {
            if (file.isDirectory()) {
                compress(file, output, zipName, method);
            } else {
                String fileName = zipName + file.getParent().replace(output.getSourceFilePath(), "") + File.separator + file.getName();
                addEntry(fileName, file, output, method);
            }
        }
    }

    /**
     * 添加目录/文件
     *
     * @param filePath      压缩文件路径
     * @param file          压缩文件
     * @param output        ZipArchive多线程输出流
     * @param method        压缩方式：ZipEntry.DEFLATED: 压缩/ZipEntry.STORED:不压缩
     * @throws IOException  流异常
     */
    private void addEntry(String filePath, File file, ZipArchiveScatterOutputStream output, int method) throws IOException {
        ZipArchiveEntry archiveEntry = new ZipArchiveEntry(filePath);
        archiveEntry.setMethod(method);
        archiveEntry.setSize(file.length());
//        archiveEntry.setUnixMode(UnixStat.FILE_FLAG | 436);
        InputStreamSupplier supplier = new ZipArchiveInputStreamSupplier(file);
        output.addEntry(archiveEntry, supplier);
    }

    /**
     * 获取无后缀文件名
     *
     * @param fileName  文件名
     * @return          无后缀文件名
     */
    private String getFileName(String fileName) {
        if (fileName == null || fileName.length() <= 1 || !fileName.contains(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * 计算可用线程
     *
     * @param ratio  使用线程比率
     * @return       可用线程
     */
    private int getAvailableThreads(double ratio) {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int nThreads = (int) (availableProcessors * ratio);
        if (nThreads <= 0) {
            return  1;
        } else if (nThreads > MAX_THREADS) {
            return Math.min(MAX_THREADS, availableProcessors);
        }
        return Math.min(nThreads, availableProcessors);
    }



    /**
     * 解压缩文件
     *
     * @param zipFile    压缩文件路径
     * @param targetPath 解压到指定路径
     */
    @Override
    public void unZip(String zipFile, String targetPath) throws IOException {
        File sourceZipFile = new File(zipFile);
        if (!sourceZipFile.exists()) {
            throw new FileNotFoundException(sourceZipFile + " not found!");
        }
        // 如果 destDir 为 null, 空字符串, 或者全是空格, 则解压到压缩文件所在目录
        if(StringUtils.isBlank(targetPath)) {
            targetPath = sourceZipFile.getParent();
        }

        ArchiveEntry zipEntry = null;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceZipFile));
             ZipArchiveInputStream zipInputStream = new ZipArchiveInputStream(bis)) {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File unZipFile = new File(targetPath, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    boolean mkdirs = unZipFile.mkdirs();
                    if (!mkdirs) {
                        logger.warn("make dir fails, dir exists Chinese");
                    }
                } else {
                    try (FileOutputStream outPut = new FileOutputStream(unZipFile);
                         BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outPut)) {
                        IOUtils.copy(zipInputStream, bufferedOutputStream, 8192);
                    } catch (IOException e) {
                        logger.warn("file exists Chinese");
                    }
                }
            }
        } catch (IOException e) {
            logger.error("have an IOException", e);
            throw new IORuntimeException("解压失败", e);
        }
    }

}
