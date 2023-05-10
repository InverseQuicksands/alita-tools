package com.apsaras.framework.platform.pdf.context.signature;

/**
 * MutipleSignatureWithOnePdf
 *
 * @date 2023-02-16 10:45
 */
public class MutipleSignatureWithOnePdf extends PdfSignature {



    /**
     * PDF 电子签章.
     *
     * <p>此处空实现，因签章的方式各不相同，如：只在页尾盖章；每一页都需要盖章；盖骑缝章等。
     * 具体电子签章的过程需自己实现。
     *
     * @param srcPdfPath
     * @param targetPdfPath
     * @param signatureInfo 签章信息
     * @param imageData
     */
    @Override
    public void pdfSign(String srcPdfPath, String targetPdfPath, SignatureInfo signatureInfo, byte[] imageData) throws Exception {

    }
}
