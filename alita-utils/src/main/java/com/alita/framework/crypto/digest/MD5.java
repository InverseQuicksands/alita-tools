package com.alita.framework.crypto.digest;

import com.alita.framework.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 工具类，提供生成 MD5 加密值及校验文件的 MD5 值.
 *
 * <p>摘要算法，也是加密算法的一种，还有另外一种叫法：指纹。<br>
 * 摘要算法就是对指定的数据进行一系列的计算，然后得出一个串内容，该内容就是该数据的摘要。
 * 不同的数据产生的摘要是不同的，所以，可以用它来进行一些数据加密的工作：通过对比两个数据加密后的摘要是否相同，来判断这两个数据是否相同。
 * 还可以用来保证数据的完整性，常见的软件在发布之后，会同时发布软件的md5和sha值，这个md5和sha值就是软件的摘要。
 * 当用户将软件下载之后，然后去计算软件的摘要，如果计算所得的摘要和软件发布方提供的摘要相同，则证明下载的软件和发布的软件一模一样，
 * 否则，就是下载过程中数据（软件）被篡改了。
 *
 * <p>常见的摘要算法包括：md、sha这两类。md包括md2、md4、md5；
 * sha包括sha1、sha224、sha256、sha384、sha512。
 *
 * <p>常见算法:
 * 对称加密算法：DES算法，3DES算法，TDEA算法，Blowfish算法，RC5算法，IDEA算法，AES算法。
 * 非对称加密算法：RSA、Elgamal、背包算法、Rabin、D-H、ECC。
 *
 * <p>MD5，全称为“Message Digest Algorithm 5”，中文名“消息摘要算法第五版”，它是计算机安全领域广泛使用的一种散列函数，
 * 用以提供消息的完整性保护。严格来说，它是一种摘要算法，是确保信息完整性的。不过，在某种意义上来说，也可以算作一种加密算法。
 * <p>MD5 算法具有很多特点：
 * <ul>
 * <li>压缩性：任意长度的数据，算出的MD5值长度都是固定的。</li>
 * <li>容易计算：从原数据计算出MD5值很容易。</li>
 * <li>抗修改性：对原数据进行任何改动，哪怕只修改1个字节，所得到的MD5值都有很大区别。</li>
 * <li>弱抗碰撞：已知原数据和其MD5值，想找到一个具有相同MD5值的数据（即伪造数据）是非常困难的。</li>
 * <li>强抗碰撞：想找到两个不同的数据，使它们具有相同的MD5值，是非常困难的。</li>
 * </ul>
 * <p>MD5 的作用是让大容量信息在用数字签名软件签署私人密钥前被"压缩"成一种保密的格式（就是把一个任意长度的字节串变换成一定长的十六进制数字串）.
 * 但实际上MD5不是一种加密算法，只是一种算法，因为它是一个不可逆的过程，即 MD5 生成消息摘要后是不能通过结果解析得到原来的内容.
 *
 * @see MessageDigest
 * @see org.bouncycastle.crypto.digests.MD5Digest
 * @see org.apache.commons.codec.digest.DigestUtils
 * @see org.springframework.util.DigestUtils
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @since 1.0.0
 * @date: 2022-11-13 13:47
 */

@SuppressWarnings({"unchecked","unused"})
public class MD5 {

    private static final Logger logger = LoggerFactory.getLogger(MD5.class);

    private static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7','8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static final String slat = "&%5123***&&%%$$#@";


    /**
     * <p>使用 jdk 原生{@link MessageDigest}类进行 MD5 加密
     * 该加密算法只是进行普通的MD5加密，没有进行“加盐”处理.
     *
     * @param text 待加密的文本
     * @param charsetName 指定字符集
     * @return MD5 加密后的字符串32位(小写字母+数字)
     */
    public static String encryption(String text, String charsetName) {
        String result = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            // 通过使用 update 方法处理数据,使指定的 byte数组更新摘要
            messageDigest.update(text.getBytes(Charset.forName(charsetName)));
            byte[] digest = messageDigest.digest();
            // 把密文转换成十六进制的字符串形式
            int len = digest.length;
            char chars[] = new char[len * 2];
            int k = 0;
            for (int i = 0; i < len; i++) {
                byte bit = digest[i];
                // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
                chars[k++] = hexDigits[bit >>> 4 & 0xf];
                // 取字节中低 4 位的数字转换
                chars[k++] = hexDigits[bit & 0xf];
            }
            result = new String(chars);
        } catch (Exception ex) {
            logger.error("##########【MD5加密异常】##########", ex);
        }
        return result;
    }


    /**
     * <p>使用 jdk 原生{@link MessageDigest}类进行 MD5 加密
     * 该加密算法通过特殊“加盐”处理，使生成的结果更安全.
     *
     * @param text 待加密的文本
     * @param saltValue 盐值
     * @param charsetName 指定字符集
     * @return MD5 加密后的字符串32位(小写字母+数字)
     */
    public static String saltEncryption(String text, String saltValue, String charsetName) {
        String result = "";
        try {
            if (StringUtils.isBlank(saltValue)) {
                saltValue = slat;
            }
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(text.getBytes(Charset.forName(charsetName)));
            messageDigest.update(saltValue.getBytes(Charset.forName(charsetName)));

            // 获得密文
            byte[] digest = messageDigest.digest();
            // 把密文转换成十六进制的字符串形式
            int len = digest.length;
            char chars[] = new char[len * 2];
            int k = 0;
            for (int i = 0; i < len; i++) {
                byte bit = digest[i];
                chars[k++] = hexDigits[bit >>> 4 & 0xf];
                chars[k++] = hexDigits[bit & 0xf];
            }
            result = new String(chars);
            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值。1 固定值
            // return new BigInteger(1,  messageDigest.digest()).toString(16);
        } catch (Exception ex) {
            logger.error("##########【MD5加密异常】##########", ex);
        }
        return result;
    }


    /**
     * <p>使用 {@code bcprov-ext-jdk15on} 包中的 {@link org.bouncycastle.crypto.digests.MD5Digest} 类进行 MD5 加密
     * 该加密算法只是进行普通的MD5加密，没有进行“加盐”处理.
     *
     * @param text 待加密的文本
     * @param charsetName 指定字符集
     * @return MD5 加密后的字符串32位(小写字母+数字)
     */
    public static String bcEncryption(String text, String charsetName) {
        MD5Digest md5Digest = new MD5Digest();
        md5Digest.reset();
        md5Digest.update(text.getBytes(Charset.forName(charsetName)), 0, text.getBytes().length);
        // 获取加密之后，字节数组的长度
        byte[] cipherBytes = new byte[md5Digest.getDigestSize()];
        md5Digest.doFinal(cipherBytes, 0);
        // 将字节数组转换为十六进制
        String cipherText = Hex.toHexString(cipherBytes);
        return cipherText;
    }


    /**
     * <p>使用 {@code bcprov-ext-jdk15on} 包中的 {@link org.bouncycastle.crypto.digests.MD5Digest} 类进行 MD5 加密
     * 该加密算法通过特殊“加盐”处理，使生成的结果更安全.
     *
     * @param text 待加密的文本
     * @param saltValue 盐值
     * @param charsetName 指定字符集
     * @return MD5 加密后的字符串32位(小写字母+数字)
     */
    public static String bcSaltEncryption(String text, String saltValue, String charsetName) {
        if (StringUtils.isBlank(saltValue)) {
            saltValue = slat;
        }
        MD5Digest md5Digest = new MD5Digest();
        md5Digest.reset();
        StringBuffer buffer = new StringBuffer();
        buffer.append(text).append(saltValue);
        String bufferStr = buffer.toString();
        md5Digest.update(bufferStr.getBytes(Charset.forName(charsetName)), 0, bufferStr.getBytes().length);
        // 获取加密之后，字节数组的长度
        byte[] cipherBytes = new byte[md5Digest.getDigestSize()];
        md5Digest.doFinal(cipherBytes, 0);
        // 将字节数组转换为十六进制
        String cipherText = Hex.toHexString(cipherBytes);
        return cipherText;
    }


    /**
     * <p>使用 {@code apache-commons-codec} 包中的 {@link org.apache.commons.codec.digest.DigestUtils} 类进行 MD5 加密
     * 该加密算法只是进行普通的MD5加密，没有进行“加盐”处理.
     * 对于md5加密来说，DigestUtils提供了6个静态方法:
     * <pre>
     *   byte[] DigestUtils.md5(byte[] data);
     *   byte[] DigestUtils.md5(InputStream is);
     *   byte[] DigestUtils.md5(String data);
     *   String DigestUtils.md5Hex(byte[] data);
     *   String DigestUtils.md5Hex(InputStream is);
     *   String DigestUtils.md5Hex(String data);
     * </pre>
     *
     * @param text 待加密的文本
     * @param charsetName 指定字符集
     * @return MD5 加密后的字符串32位(小写字母+数字)
     */
    public static String ccEncryption(String text, String charsetName) {
        String encodeText = new String(text.getBytes(), Charset.forName(charsetName));
        String cipherText = DigestUtils.md5Hex(encodeText);
        return cipherText;
    }


    /**
     * <p>使用 {@code apache-commons-codec} 包中的 {@link org.apache.commons.codec.digest.DigestUtils} 类进行 MD5 加密
     * 该加密算法通过特殊“加盐”处理，使生成的结果更安全.
     * 对于md5加密来说，DigestUtils提供了6个静态方法:
     * <pre>
     *   byte[] DigestUtils.md5(byte[] data);
     *   byte[] DigestUtils.md5(InputStream is);
     *   byte[] DigestUtils.md5(String data);
     *   String DigestUtils.md5Hex(byte[] data);
     *   String DigestUtils.md5Hex(InputStream is);
     *   String DigestUtils.md5Hex(String data);
     * </pre>
     *
     * @param text 待加密的文本
     * @param saltValue 盐值
     * @param charsetName 指定字符集
     * @return MD5 加密后的字符串32位(小写字母+数字)
     */
    public static String ccSaltEncryption(String text, String saltValue, String charsetName) {
        if (StringUtils.isBlank(saltValue)) {
            saltValue = slat;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(text).append(saltValue);
        String bufferStr = buffer.toString();
        String encodeText = new String(bufferStr.getBytes(), Charset.forName(charsetName));
        String cipherText = DigestUtils.md5Hex(encodeText);
        return cipherText;
    }


    /**
     * <p>使用 {@code spring-code} 包中的 {@link org.springframework.util.DigestUtils} 类进行 MD5 加密
     * 该加密算法只是进行普通的MD5加密，没有进行“加盐”处理.
     *
     * @param text 待加密的文本
     * @param charsetName 指定字符集
     * @return MD5 加密后的字符串32位(小写字母+数字)
     */
    public static String springEncryption(String text, String charsetName) {
        String md5 = com.alita.framework.crypto.digest.DigestUtils.md5DigestAsHex(
                text.getBytes(Charset.forName(charsetName)));

        return md5;
    }


    /**
     * <p>使用 {@code spring-code} 包中的 {@link org.springframework.util.DigestUtils} 类进行 MD5 加密
     * 该加密算法通过特殊“加盐”处理，使生成的结果更安全.
     *
     * @param text 待加密的文本
     * @param saltValue 盐值
     * @param charsetName 指定字符集
     * @return MD5 加密后的字符串32位(小写字母+数字)
     */
    public static String springSlatEncryption(String text, String saltValue, String charsetName) {
        if (StringUtils.isBlank(saltValue)) {
            saltValue = slat;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(text).append(saltValue);
        String bufferStr = buffer.toString();
        String encodeText = new String(bufferStr.getBytes(), Charset.forName(charsetName));
        String md5 = com.alita.framework.crypto.digest.DigestUtils.md5DigestAsHex(
                encodeText.getBytes(Charset.forName(charsetName)));

        return md5;
    }


    /**
     * 生成文件的md5校验值
     *
     * @param file 待校验的文件
     * @return MD5 加密后的字符串32位(小写字母+数字)
     */
    public static String getFileMD5(File file) {
        String result = "";

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            InputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int numRead = 0;
            while ((numRead = inputStream.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, numRead);
            }
            inputStream.close();
            byte[] digest = messageDigest.digest();
            int len = digest.length;
            char chars[] = new char[len * 2];
            int k = 0;
            for (int i = 0; i < len; i++) {
                byte bit = digest[i];
                chars[k++] = hexDigits[bit >>> 4 & 0xf];
                chars[k++] = hexDigits[bit & 0xf];
            }
            result = new String(chars);

        } catch (NoSuchAlgorithmException ex) {
            logger.error("##########【MD5加密异常】##########", ex);
        } catch (IOException ex) {
            logger.error("##########【读取文件异常】##########", ex);
        }

        return result;
    }

}
