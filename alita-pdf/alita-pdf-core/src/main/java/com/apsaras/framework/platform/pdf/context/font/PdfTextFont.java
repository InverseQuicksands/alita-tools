package com.apsaras.framework.platform.pdf.context.font;

import com.itextpdf.kernel.font.PdfFont;

import java.io.IOException;

/**
 * PDF 字体设置
 */
public interface PdfTextFont {

    /**
     * 获取当前的字体
     *
     * @return PdfFont
     */
    PdfFont getPdfFont();

    /**
     * 使用自定义字体.
     *
     * @param fontPath 字体路径
     * @return PdfFontStyle
     * @throws IOException 异常
     */
    PdfFontStyle loadFont(String fontPath) throws IOException;

    /**
     * 使用自定义字体.
     *
     * @param fontPrograms 字体byte数组
     * @return PdfFontStyle
     * @throws IOException 异常
     */
    PdfFontStyle loadFont(byte[] fontPrograms) throws IOException;

}
