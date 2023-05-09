package com.apsaras.framework.platform.pdf.context.signature;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.element.Image;
import com.itextpdf.signatures.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.*;
import java.security.cert.Certificate;

/**
 * PDF 电子签章.
 *
 * <p>只在最后一页签章.
 *
 * @date 2023-02-16 09:27
 */
public class OnePdfSignature extends PdfSignature{

    public static final Logger logger = LoggerFactory.getLogger(OnePdfSignature.class);

    /**
     * PDF 电子签章.
     *
     * <p>只在最后一页签章.
     *
     * @param srcPdfPath 源文件路径
     * @param targetPdfPath 盖章后PDF路径
     * @param signatureInfo 签章信息
     * @param imageData 签章图片数据
     */
    @Override
    public void pdfSign(String srcPdfPath, String targetPdfPath, SignatureInfo signatureInfo, byte[] imageData) throws Exception {
        InputStream inputStream = new FileInputStream(srcPdfPath);
        ByteArrayOutputStream tempArrayOutputStream = new ByteArrayOutputStream();
        FileOutputStream outputStream = null;
        PdfReader reader = null;

        try {
            reader = new PdfReader(inputStream);
            PdfSigner signer = new PdfSigner(reader, tempArrayOutputStream, new StampingProperties());
            signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);
            // 获取总页数
            int numberOfPages = signer.getDocument().getNumberOfPages();

            // 获取数字签章属性对象，设定数字签章的属性
            PdfSignatureAppearance appearance = signer.getSignatureAppearance();
            appearance.setReason(signatureInfo.getReason());
            appearance.setLocation(signatureInfo.getLocation());

            //加盖图章图片
            ImageData img = null;
            if (imageData != null) {
                img = ImageDataFactory.create(imageData);
            } else {
                Assert.hasText(signatureInfo.getImagePath(), "PDF signature image path must not be null!");
                img = ImageDataFactory.create(signatureInfo.getImagePath());
            }

            //读取图章图片，这个image是itext包的image
            Image image = new Image(img);
            float height = image.getImageHeight();
            float width = image.getImageWidth();
            //签名的位置，是图章相对于pdf页面的位置坐标，原点为pdf页面左下角
            //四个参数的分别是，图章左下角x，图章左下角y，图章宽度，图章高度
            appearance.setPageNumber(numberOfPages);
            appearance.setPageRect(new Rectangle(signatureInfo.getRectllx(), signatureInfo.getRectlly(), width, height));
            //插入盖章图片
            appearance.setSignatureGraphic(img);
            //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
            appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);

            // 摘要算法
            IExternalDigest digest = new BouncyCastleDigest();
            // 签名算法
            IExternalSignature signature = super.getPrivateKeySignature(null);
            // 证书链
            Certificate[] chain = super.getCertificateChain(null);

            // 调用itext签名方法完成pdf签章
            signer.signDetached(digest, signature, chain, null, null,null, 0, PdfSigner.CryptoStandard.CADES);

            outputStream = new FileOutputStream(targetPdfPath);
            outputStream.write(tempArrayOutputStream.toByteArray());
            outputStream.flush();
        } finally {
            try {
                if(null!=tempArrayOutputStream){
                    tempArrayOutputStream.close();
                }
                if(null!=outputStream){
                    outputStream.close();
                }
                if(null!=inputStream){
                    inputStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
