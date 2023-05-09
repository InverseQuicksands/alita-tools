package com.alita.framework.archives.zip;

import com.alita.framework.archives.CompressFactory;
import com.alita.framework.archives.IORuntimeException;
import com.alita.framework.archives.zip.nio.ZipArchiveTradition;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * ZipFactory
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-29 23:43
 */
public class ZipArchiveFactory implements CompressFactory {

    private ZipArchive zipArchive;

    public ZipArchiveFactory() {
        this(new ZipArchiveTradition());
    }

    public ZipArchiveFactory(ZipArchive zipArchive) {
        this.zipArchive = zipArchive;
    }

    /**
     * 返回当前工厂类
     *
     * @return CompressFactory
     */
    @Override
    public CompressFactory getFactory() {
        return this;
    }

    /**
     * 压缩文件/文件夹为zip，gzip，tar，rar，7z.
     *
     * @param sourceFile 原文件路径
     * @param targetFile 压缩后文件路径
     * @throws Exception 异常
     */
    @Override
    public void compress(String sourceFile, String targetFile) throws Exception {
        checkFileExists(sourceFile);
        createDirectory(targetFile);
        this.zipArchive.toZip(sourceFile, targetFile);
    }

    /**
     * 解压缩文件
     *
     * @param sourceFile 压缩文件路径
     * @param targetFile 解压到指定目录
     * @throws Exception 异常
     */
    @Override
    public void unCompress(String sourceFile, String targetFile) throws Exception {
        checkZipFile(sourceFile);
        checkFileExists(sourceFile);
        createDirectory(targetFile);
        this.zipArchive.unZip(sourceFile, targetFile);
    }

    /**
     * 判断文件/文件夹是否存在
     *
     * @param sourceFilePath
     * @throws FileNotFoundException
     */
    private void checkFileExists(String sourceFilePath) throws FileNotFoundException {
        File file = new File(sourceFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException(sourceFilePath + " is not found!");
        }
    }

    /**
     * 目标路径是否存在
     *
     * @param targetZipFile 路径
     */
    private void createDirectory(String targetZipFile) {
        File zipFile = new File(targetZipFile);
        File parentFolder = new File(zipFile.getParent());
        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }
    }

    /**
     * 判断文件名是否以.zip为后缀
     *
     * @param fileName 需要判断的文件名
     */
    private void checkZipFile(String fileName) throws IORuntimeException {
        if (fileName != null && !"".equals(fileName.trim())) {
            if (!fileName.endsWith(".zip")) {
                throw new IORuntimeException(fileName + " is not zip file!");
            }
        }
    }

}
