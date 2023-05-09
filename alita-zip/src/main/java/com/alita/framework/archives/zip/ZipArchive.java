package com.alita.framework.archives.zip;

import java.io.File;
import java.io.IOException;

/**
 * ZipArchive
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-28 22:26
 */
public interface ZipArchive {

    /**
     * 压缩文件/文件夹
     *
     * @param sourceZipFile 原文件/文件夹路径
     * @param targetZipFile 目标文件路径
     * @throws Exception 异常
     */
    void toZip(String sourceZipFile, String targetZipFile) throws Exception;

    /**
     * 解压缩文件
     *
     * @param zipFile 压缩文件路径
     * @param targetPath 解压到指定路径
     * @throws IOException 异常
     */
    void unZip(String zipFile, String targetPath) throws IOException;

    /**
     * 设置密码
     *
     * @param password 密码
     */
    default void setPassword(String password) {

    }
}
