package com.alita.framework.zip.archives.zip.nio;

import com.alita.framework.archives.CompressFactory;
import com.alita.framework.archives.zip.ZipArchive;
import com.alita.framework.archives.zip.ZipArchiveFactory;
import com.alita.framework.archives.zip.nio.ZipArchiveMappedByteBuffer;
import org.junit.jupiter.api.Test;

/**
 * ZipTest
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-21 00:03
 */
public class ZipTest {

    @Test
    public void toZip() throws Exception {
        long start = System.currentTimeMillis();
        CompressFactory factory = new ZipArchiveFactory();
        factory.compress("/Users/zhang/Desktop/CentOS-7-aarch64-Minimal-2009.iso", "/Users/zhang/Desktop/aa.zip");
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) / 1000 + "秒");
    }

    @Test
    public void toZipMappedByteBuffer() throws Exception {
        long start = System.currentTimeMillis();
        ZipArchive zipArchive = new ZipArchiveMappedByteBuffer();
        CompressFactory factory = new ZipArchiveFactory(zipArchive);
        factory.compress("/Users/zhang/Desktop/CentOS-7-aarch64-Minimal-2009.iso", "/Users/zhang/Desktop/bb.zip");
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) / 1000 + "秒");
    }

    @Test
    public void unZip() throws Exception {
        long start = System.currentTimeMillis();
        CompressFactory factory = new ZipArchiveFactory();
        factory.unCompress("/Users/zhang/Desktop/aa.zip", "/Users/zhang/Desktop/");
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) / 1000 + "秒");
    }
}
