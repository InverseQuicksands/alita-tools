package com.alita.framework.archives.gzip;

import com.alita.framework.archives.CompressFactory;

/**
 * GzipArchiveFactory
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-29 23:48
 */
public class GzipArchiveFactory implements CompressFactory {

    private GzipArchive gzipArchive;

    public GzipArchiveFactory() {
    }

    public GzipArchiveFactory(GzipArchive gzipArchive) {
        this.gzipArchive = gzipArchive;
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
        this.gzipArchive.toGzip(sourceFile, targetFile);
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
        this.gzipArchive.unGzip(sourceFile, targetFile);
    }
}
