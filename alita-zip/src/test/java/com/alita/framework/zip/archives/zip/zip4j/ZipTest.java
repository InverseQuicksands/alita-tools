package com.alita.framework.zip.archives.zip.zip4j;

import com.alita.framework.archives.CompressFactory;
import com.alita.framework.archives.zip.ZipArchive;
import com.alita.framework.archives.zip.ZipArchiveFactory;
import com.alita.framework.archives.zip.zip4j.Zip4jArchive;
import org.junit.jupiter.api.Test;

/**
 * ZipTest
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-04-06 15:23
 */
public class ZipTest {


    @Test
    public void toZip() throws Exception {
        long start = System.currentTimeMillis();
        ZipArchive archive = new Zip4jArchive();
        archive.setPassword("123456");
        CompressFactory factory = new ZipArchiveFactory(archive);
        factory.compress("/Users/zhang/Desktop/aa", "/Users/zhang/Desktop/bb.zip");
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) / 1000 + "秒");
    }


    @Test
    public void unZip() throws Exception {
        long start = System.currentTimeMillis();
        ZipArchive archive = new Zip4jArchive();
//        archive.setPassword("123456");
        CompressFactory factory = new ZipArchiveFactory(archive);
        factory.unCompress("/Users/zhang/Desktop/aa.zip", "/Users/zhang/Desktop/bb");
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) / 1000 + "秒");
    }

}
