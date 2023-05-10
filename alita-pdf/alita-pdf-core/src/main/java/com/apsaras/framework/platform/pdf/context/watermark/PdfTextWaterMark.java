package com.apsaras.framework.platform.pdf.context.watermark;

import com.apsaras.framework.platform.pdf.context.watermark.PdfWaterMark;

/**
 * PDF 水印
 */
public interface PdfTextWaterMark {

    /**
     * 是否显示水印.
     *
     * @return default false
     */
    boolean isWaterMark();

    /**
     * 设置是否显示水印.
     *
     * @param flag default false
     */
    void setPdfWaterMark(boolean flag);

    /**
     * pdf 水印设置.
     *
     * @return PdfWaterMark
     */
    PdfWaterMark getPdfWaterMark();

}
