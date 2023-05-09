package com.alita.framework.archives.zip.zip4j;

import com.alita.framework.archives.zip.ZipArchive;
import com.alita.framework.utils.StringUtils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.IOException;

/**
 * Zip4jCompress
 *
 * <p>zip4j：
 * 针对ZIP压缩文件创建、添加、分卷、更新和移除文件.
 * <ol>
 *     <li>读写有密码保护的Zip文件</li>
 *     <li>支持AES 128/256算法加密</li>
 *     <li>支持标准Zip算法加密</li>
 *     <li>支持zip64格式</li>
 *     <li>支持Store(仅打包，默认不压缩，不过可以手动设置大小)和Deflate压缩方法</li>
 *     <li>针对分块zip文件创建和抽出文件</li>
 *     <li>支持编码</li>
 *     <li>进度监控</li>
 * </ol>
 *
 * <p>
 * {@link net.lingala.zip4j.model.enums.CompressionMethod}压缩方式(3种):
 * <pre>
 * static final int STORE = 0;（仅打包，不压缩） （对应好压的存储）
 * static final int DEFLATE = 8;（默认）       （对应好压的标准）
 * static final int AES_INTERNAL_ONLY = 99;
 * </pre>

 * {@link net.lingala.zip4j.model.enums.CompressionLevel}压缩级别有5种：（默认0不压缩）级别跟好压软件是对应的；
 * <pre>
 * static final int NO_COMPRESSION = 0;
 * static final int FASTEST = 1;
 * static final int FASTER = 2;
 * static final int FAST = 2;
 * static final int MEDIUM_FAST = 4;
 * static final int NORMAL = 5;
 * static final int HIGHER = 6;
 * static final int MAXIMUM = 7;
 * static final int PRE_ULTRA = 8;
 * static final int ULTRA = 9;
 * </pre>
 *
 * {@link net.lingala.zip4j.model.enums.EncryptionMethod}加密方式：
 * <pre>
 * NONE、ZIP_STANDARD、ZIP_STANDARD_VARIANT_STRONG、AES
 * </pre>
 *
 * AES Key Strength：
 * <pre>
 * KEY_STRENGTH_128、KEY_STRENGTH_192、KEY_STRENGTH_256
 * </pre>
 *
 * <p>在采取默认压缩时：<br>
 * 1.如果此压缩文件已经存在，那么压缩后，相同的文件会替换（有密码，密码被替换），原来不同的文件会继续保留，而且文件的时间还是第一次压缩的时间；如果想完全覆盖，那么需要判断此压缩文件存不存在，存在就删除.<br>
 * 2.假如a文件加密生成了a.zip,此时如果再把其他的文件b也加密，然后生成同样的a.zip,那么a.zip里面的文件a，b将会有各自的密码。需要分别输入对应密码解压，无形实现了对单个文件的单个加密，但是这样解压可能会损坏文件（个人不建议这样做）.<br>
 * 3.如果不设置压缩级别，默认级别为0（不压缩），这样生成的zip包跟原来文件的大小差不多，另外如果压缩方式设置成了Zip4jConstants.COMP_STORE（0）那么也是仅仅打个包.<br>
 * 4.如果设置了中文密码，用好压解压会提示密码错误（无法解压），用ZIP4j解压的话，正常；也就说，对于中文密码，解压软件与zip4j是不能相互解压的.<br>
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-04-03 15:36
 */
public class Zip4jArchive implements ZipArchive {

    /**
     * 密码
     */
    private String password;


    /**
     * 压缩文件/文件夹
     *
     * @param sourceZipFile 原文件/文件夹路径
     * @param targetZipFile 目标文件路径
     * @throws Exception 异常
     */
    @Override
    public void toZip(String sourceZipFile, String targetZipFile) throws Exception {
        ZipParameters parameters = new ZipParameters();
        ZipFile zipFile = null;

        if (StringUtils.isNotBlank(this.password)) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(EncryptionMethod.AES);
            // Below line is optional. AES 256 is used by default. You can override it to use AES 128. AES 192 is supported only for extracting.
            parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            zipFile = new ZipFile(targetZipFile, this.password.toCharArray());
        } else {
            zipFile = new ZipFile(targetZipFile);
        }

        File sourceFile = new File(sourceZipFile);
        if (sourceFile.isDirectory()) {
            zipFile.addFolder(sourceFile, parameters);
        } else {
            zipFile.addFile(sourceFile, parameters);
        }
    }


    /**
     * 解压缩文件
     *
     * @param zipFile    压缩文件路径
     * @param targetPath 解压到指定路径
     * @throws IOException 异常
     */
    @Override
    public void unZip(String zipFile, String targetPath) throws IOException {
        ZipFile targetZipFile = new ZipFile(zipFile);
        if (!targetZipFile.isValidZipFile()) {
            throw new ZipException("压缩文件不合法,可能被损坏.");
        }
        if (targetZipFile.isEncrypted()) {
            if (StringUtils.isBlank(this.password)) {
                throw new ZipException("压缩文件已加密，请传入密码");
            }
            targetZipFile.setPassword(this.password.toCharArray());
        }
        targetZipFile.extractAll(targetPath);
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
