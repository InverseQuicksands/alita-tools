package com.alita.framework.pdf.pdf.context.signature;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("pdf 单/多页签章")
public class PdfDocumentSign {

    @DisplayName("pdf 多页签章")
    @Test
    public void multiplePdfSignature() throws Exception {
        //需要进行签章的pdf
        String path = "/Users/zhang/Desktop/test.pdf";
        // 封装签章信息
        SignatureInfo signInfo = new SignatureInfo();
        signInfo.setReason("理由");
        signInfo.setLocation("位置");
        signInfo.setFieldName("demo");
        // 签章图片
        signInfo.setImagePath("");
        signInfo.setRectllx(200);  // 值越大，代表向x轴坐标平移 缩小 （反之，值越小，印章会放大）
        signInfo.setRectlly(30);  // 值越大，代表向y轴坐标向上平移（大小不变）
        signInfo.setPageCount(4);
        //签章后的pdf路径
        MultiplePdfSignature multiplePdfSignature = new MultiplePdfSignature();
        multiplePdfSignature.setKeyStoreType("PKCS12");
        multiplePdfSignature.setPkPath("");
        multiplePdfSignature.pdfSign(path, "/Users/zhang/Desktop/test22.pdf", signInfo, null);
    }


    @DisplayName("pdf 单页签章")
    @Test
    public void onePdfSignature() throws Exception {
        //需要进行签章的pdf
        String path = "/Users/zhang/Desktop/test44.pdf";
        // 封装签章信息
        SignatureInfo signInfo = new SignatureInfo();
        signInfo.setReason("理由");
        signInfo.setLocation("位置");
        signInfo.setFieldName("demo");
        // 签章图片
        signInfo.setImagePath("/Users/zhang/zhang/学习/学习项目/后端/idea_work/work/apsaras-work/apsaras-aegis-core/src/test/resources/static/095.png");
        signInfo.setRectllx(200);  // 值越大，代表向x轴坐标平移 缩小 （反之，值越小，印章会放大）
        signInfo.setRectlly(30);  // 值越大，代表向y轴坐标向上平移（大小不变）
        //签章后的pdf路径
        OnePdfSignature onePdfSignature = new OnePdfSignature();
        onePdfSignature.setKeyStoreType("PKCS12");
        onePdfSignature.setPkPath("");
        onePdfSignature.pdfSign(path, "/Users/zhang/Desktop/test88.pdf", signInfo, null);
    }
}
