package com.alita.framework.archives.zip.nio;

import com.alita.framework.archives.zip.ZipArchive;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 压缩文件/文件夹.
 *
 * <p> 支持保留原来的文件目录结构:
 * <ol>
 * <li>碰到空文件夹时，如果需要保留目录结构，则直接添加个ZipEntry就可以了，不过就是这个entry的名字后面需要带上一斜杠（/）表示这个是目录</li>
 * <li>递归时，不需要把zip输出流关闭，zip输出流的关闭应该是在调用完递归方法后面关闭</li>
 * <li>递归时，如果是个文件夹且需要保留目录结构，那么在调用方法压缩他的子文件时，需要把文件夹的名字加一斜杠给添加到子文件名字前面，这样压缩后才有多级目录</li>
 * </ol>
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-28 22:31
 */
public abstract class AbstractZipArchive implements ZipArchive {

    /**
     * 压缩文件/文件夹
     *
     * @param sourceZipFile 原文件/文件夹路径
     * @param targetZipFile 目标文件路径
     */
    @Override
    public void toZip(String sourceZipFile, String targetZipFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetZipFile));
        File file = new File(sourceZipFile);
        compress(out, file, null);
        out.close();
    }

    /**
     * 压缩文件/文件夹
     *
     * @param out zip输出流
     * @param sourceFile 目标文件
     * @param targetZipName 压缩文件名
     * @throws Exception 异常
     */
    protected abstract void compress(ZipOutputStream out, File sourceFile, String targetZipName) throws Exception;

    /**
     * 解压缩文件
     *
     * @param zipFile    压缩文件路径
     * @param targetPath 解压到指定路径
     */
    @Override
    public void unZip(String zipFile, String targetPath) throws IOException {
        //解决zip文件中有中文目录或者中文文件
        ZipFile zip = new ZipFile(zipFile, Charset.forName("GB18030"));
        Enumeration entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (targetPath + zipEntryName).replaceAll("\\*", "/");
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (new File(outPath).isDirectory()) {
                continue;
            }

            OutputStream out = new FileOutputStream(outPath);
            byte[] bytes = new byte[8196];
            int len;
            while ((len = in.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }
            in.close();
            out.close();
        }
    }
}
