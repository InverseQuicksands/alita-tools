package com.alita.framework.zip.archives.zip.common.compress;

import com.alita.framework.archives.CompressFactory;
import com.alita.framework.archives.zip.ZipArchive;
import com.alita.framework.archives.zip.ZipArchiveFactory;
import com.alita.framework.archives.zip.commons.compress.AbstractZipCompress;
import org.junit.jupiter.api.Test;

/**
 * ZipTest
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-30 11:02
 */
public class ZipTest {


    @Test
    public void toZip() throws Exception {
        long start = System.currentTimeMillis();
        ZipArchive zipArchive = new AbstractZipCompress();
        CompressFactory factory = new ZipArchiveFactory(zipArchive);
        factory.compress("/Users/zhang/Desktop/aa", "/Users/zhang/Desktop/aa.zip");
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) / 1000 + "秒");
    }


    @Test
    public void unZip() throws Exception {
        long start = System.currentTimeMillis();
        ZipArchive zipArchive = new AbstractZipCompress();
        CompressFactory factory = new ZipArchiveFactory(zipArchive);
        factory.unCompress("/Users/zhang/Desktop/aa.zip", "/Users/zhang/Desktop/");
        long end = System.currentTimeMillis();
        System.out.println("总耗时：" + (end - start) / 1000 + "秒");
    }


}
