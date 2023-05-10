package com.alita.framework.archives;

/**
 * CompressFactory
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-29 23:41
 */
public interface CompressFactory {

    /**
     * 返回当前工厂类
     *
     * @return CompressFactory
     */
    CompressFactory getFactory();

    /**
     * 压缩文件/文件夹为zip，gzip，tar，rar，7z.
     *
     * @param sourceFile 原文件路径
     * @param targetFile 压缩后文件路径
     * @throws Exception 异常
     */
    void compress(String sourceFile, String targetFile) throws Exception ;

    /**
     * 解压缩文件
     *
     * @param sourceFile 压缩文件路径
     * @param targetFile 解压到指定目录
     * @throws Exception 异常
     */
    void unCompress(String sourceFile, String targetFile) throws Exception ;
}
