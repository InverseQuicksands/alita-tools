package com.apsaras.framework.platform.pdf.context.signature;

import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PrivateKeySignature;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Enumeration;

/**
 * PDF 电子签章抽象类.
 *
 * keytool 生成 .keystore 文件和 .cer 文件命令：
 *
 * 生成私钥和证书
 * keytool -genkeypair -alias serverkey \
 * -keyalg RSA -keysize 2048 -validity 3650 \
 * -keypass 99700040001 -storepass 99700040001 \
 * -keystore ./cert.keystore \
 * -dname "c=CN st=北京市 l=北京市 o=中国邮政储蓄银行 ou=99700040001 cn=中国邮政储蓄银行" \
 *
 * 查看keystore详情
 * keytool -v -list -keystore cert.keystore
 *
 * 证书导入导出
 * keytool -exportcert -keystore cert.keystore -file cert.cer -alias serverkey
 *
 * 将.cer格式的证书转换为p12证书：
 * keytool -importkeystore \
 * -srckeystore ./cert.keystore -destkeystore ./cert.p12 \
 * -srcstorepass 99700040001 -deststorepass 99700040001 -noprompt \
 * -srcalias serverkey -destalias serverkey \
 * -srcstoretype jks -deststoretype pkcs12
 *
 * @date 2023-02-11 17:26
 */
public abstract class PdfSignature {

    private static final Logger logger = LoggerFactory.getLogger(PdfSignature.class);

    private String pkPath;

    private String keyStoreType = "PKCS12";

    private char[] defaultPassword = "99700040001".toCharArray();


    /**
     * 获取 KeyStore.
     *
     * @param password 证书密码
     * @return KeyStore
     * @throws Exception
     */
    private KeyStore getKeyStore(char[] password) throws Exception {
        KeyStore p12 = KeyStore.getInstance(this.keyStoreType);
        InputStream inputStream = null;
        if (StringUtils.isBlank(this.pkPath)) {
            inputStream = this.getClass().getResourceAsStream("/sign/cert.p12");
        } else {
            inputStream = new FileInputStream(this.pkPath);
        }
        p12.load(inputStream, password);

        return p12;
    }


    /**
     * 获取 PrivateKey.
     *
     * @param password 证书密码
     * @return IExternalSignature
     * @throws Exception
     */
    public IExternalSignature getPrivateKeySignature(char[] password) throws Exception {
        PrivateKey pk = null;
        if (password == null) {
            logger.warn("password is null, will use default password!");
            password = defaultPassword;
        }

        KeyStore p12 = this.getKeyStore(password);
        Enumeration<String> aliases = p12.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (p12.isKeyEntry(alias)) {
                pk = (PrivateKey) p12.getKey(alias, password);
                break;
            }
        }
        //No such provider: BC : 问题解决，加BC库支持
        Security.addProvider(new BouncyCastleProvider());

        return new PrivateKeySignature(pk, DigestAlgorithms.SHA512, BouncyCastleProvider.PROVIDER_NAME);
    }

    /**
     * 得到证书链.
     *
     * @param password 证书密码
     * @return Certificate
     * @throws Exception
     */
    public Certificate[] getCertificateChain(char[] password) throws Exception {
        Certificate[] certChain = null;
        if (password == null) {
            logger.warn("password is null, will use default password!");
            password = defaultPassword;
        }

        KeyStore p12 = this.getKeyStore(password);
        Enumeration<String> aliases = p12.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (p12.isKeyEntry(alias)) {
                certChain = p12.getCertificateChain(alias);
                break;
            }
        }
        return certChain;
    }


    /**
     * PDF 电子签章.
     *
     * <p>此处空实现，因签章的方式各不相同，如：只在页尾盖章；每一页都需要盖章；盖骑缝章等。
     * 具体电子签章的过程需自己实现。
     *
     * @param signatureInfo 签章信息
     */
    public abstract void pdfSign(String srcPdfPath, String targetPdfPath, SignatureInfo signatureInfo, byte[] imageData) throws Exception;


    public String getPkPath() {
        return pkPath;
    }

    public void setPkPath(String pkPath) {
        this.pkPath = pkPath;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public char[] getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(char[] defaultPassword) {
        this.defaultPassword = defaultPassword;
    }
}
