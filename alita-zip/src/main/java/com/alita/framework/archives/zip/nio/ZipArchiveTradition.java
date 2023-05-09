package com.alita.framework.archives.zip.nio;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩文件/文件夹
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-28 23:23
 */
public class ZipArchiveTradition extends AbstractZipArchive {

    /**
     * 压缩文件/文件夹
     *
     * @param out           zip输出流
     * @param sourceFile    目标文件
     * @param targetZipName 压缩文件名
     */
    @Override
    protected void compress(ZipOutputStream out, File sourceFile, String targetZipName) throws Exception {
        BufferedOutputStream bos = new BufferedOutputStream(out);
        compress(out, bos, sourceFile, targetZipName);
    }


    /**
     * 压缩文件/文件夹
     *
     * @param out           zip输出流
     * @param bos           缓冲输出流
     * @param sourceFile    目标文件
     * @param targetZipName 压缩文件名
     * @throws IOException 异常
     */
    protected void compress(ZipOutputStream out, BufferedOutputStream bos, File sourceFile, String targetZipName) throws IOException {
        if (targetZipName == null) {
            targetZipName = sourceFile.getName();
        }

        //如果路径为目录（文件夹）
        if (sourceFile.isDirectory()) {
            // 取出文件夹中的文件（或子文件夹）
            File[] listFiles = sourceFile.listFiles();
            // 如果文件夹为空，则只需在目的地zip文件中写入一个目录进入
            if (listFiles.length == 0) {
                out.putNextEntry(new ZipEntry(targetZipName + "/"));
            } else {
                // 如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
                for (int i = 0; i < listFiles.length; i++) {
                    compress(out, bos, listFiles[i], targetZipName + "/" + listFiles[i].getName());
                }
            }
        } else {
            //如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
            out.putNextEntry(new ZipEntry(targetZipName));
            FileInputStream fos = new FileInputStream(sourceFile);
            BufferedInputStream bis = new BufferedInputStream(fos);
            int len = -1;
            //将源文件写入到zip文件中
            byte[] buf = new byte[1024];
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bis.close();
            fos.close();
        }
    }
}
