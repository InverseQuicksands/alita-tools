package com.apsaras.framework.platform.pdf.context.font;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.IOException;

/**
 * PDF 字体设置，默认字体为 STSongStd-Light，对中文显示友好.
 */
public class PdfFontStyle implements PdfTextFont {

    /**
     * 字体
     */
    private PdfFont pdfFont;

    // 设置默认中文字体
    // 如果pdf较大,或者频繁创建.每页频繁创建字体会导致内存溢出.

    /**
     * 设置默认中文字体.
     *
     * <p>如果pdf较大,或者频繁创建.每页频繁创建字体会导致内存溢出.
     *
     * <p>但是该字段进行html模板转PDF时会因为不支持中文而报错，所以要自行加载字体.
     *
     * @throws IOException
     */
    public PdfFontStyle() throws IOException {
        this.pdfFont = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H",
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED, false);
    }

    @Override
    public PdfFont getPdfFont() {
        return this.pdfFont;
    }

    /**
     * 系统会扫描 {@code classpath:/static/fonts/*.ttf} 目录下的所有字体，在配置文件中配置
     * {@code itextpdf.fontName} 属性，即可从类路径下查找对应的字体.
     *
     * <p>使用iText自带的中文jar包，输出结果会有乱码，还有一个致命的问题是如果输出中文的省略号或者中文汉字“凉”，会直接报空指针.
     *
     * @param fontPath 字体路径
     * @return PdfFontStyle
     * @throws IOException 异常
     * @see "https://www.cnblogs.com/whalesea/p/11714681.html"
     * @see "https://blog.csdn.net/Lewishhhh/article/details/122812272"
     */
    @Override
    public final PdfFontStyle loadFont(String fontPath) throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(fontPath, false);
        this.pdfFont = PdfFontFactory.createFont(fontProgram);

        return this;
    }

    public PdfFontStyle loadFont(byte[] fontPrograms) throws IOException {
        FontProgram fontProgram = FontProgramFactory.createFont(fontPrograms, false);
        this.pdfFont = PdfFontFactory.createFont(fontProgram);

        return this;
    }
}
