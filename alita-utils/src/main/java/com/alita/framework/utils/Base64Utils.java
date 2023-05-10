package com.alita.framework.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64Utils
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-01 16:32
 */
public class Base64Utils {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();




    /**
     * base64编码
     *
     * @param source 被编码的base64字符串
     * @return 被加密后的字符串
     */
    public static String encode(CharSequence source) {
        return encode(source, DEFAULT_CHARSET);
    }

    /**
     * base64编码
     *
     * @param source  被编码的base64字符串
     * @param charset 字符集
     * @return 被编码后的字符串
     */
    public static String encode(CharSequence source, Charset charset) {
        byte[] bytes = null;
        if (source == null) {
            return null;
        }

        if (charset == null) {
            bytes = source.toString().getBytes();
        } else {
            bytes = source.toString().getBytes(charset);
        }
        return encode(bytes);
    }


    /**
     * base64编码
     *
     * @param source 被编码的base64字符串
     * @return 被编码后的字符串
     */
    public static String encode(byte[] source) {
        return encoder.encodeToString(source);
    }

    /**
     * base64 解码
     *
     * @param source 被编码的base64字符串
     * @return 解码后的byte数组
     */
    public static byte[] decode(CharSequence source) {
        return decode(source.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * base64 解码
     *
     * @param source 被编码的base64字符串
     * @param charset 字符集
     * @return 解码后的byte数组
     */
    public static byte[] decode(CharSequence source, Charset charset) {
        byte[] bytes = null;
        if (source == null) {
            return null;
        }

        if (charset == null) {
            bytes = source.toString().getBytes();
        } else {
            bytes = source.toString().getBytes(charset);
        }
        return decode(bytes);
    }

    /**
     * base64 解码
     *
     * @param source 被编码的base64 byte数组
     * @return 解码后的byte数组
     */
    public static byte[] decode(byte[] source) {
        return decoder.decode(source);
    }


}
