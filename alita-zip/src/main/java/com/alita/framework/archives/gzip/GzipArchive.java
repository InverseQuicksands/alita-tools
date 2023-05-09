package com.alita.framework.archives.gzip;

import java.io.IOException;

/**
 * GzipArchive
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-04-06 15:53
 */
public interface GzipArchive {

    /**
     * 压缩文件/文件夹
     *
     * @param sourceZipFile 原文件/文件夹路径
     * @param targetZipFile 目标文件路径
     */
    void toGzip(String sourceZipFile, String targetZipFile) throws Exception;

    /**
     * 解压缩文件
     *
     * @param zipFile 压缩文件路径
     * @param targetPath 解压到指定路径
     */
    void unGzip(String zipFile, String targetPath) throws IOException;

}
