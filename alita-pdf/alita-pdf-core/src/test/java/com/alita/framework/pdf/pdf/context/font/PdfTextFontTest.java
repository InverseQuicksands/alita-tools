package com.alita.framework.pdf.pdf.context.font;

import com.apsaras.framework.platform.pdf.context.font.PdfFontStyle;
import com.apsaras.framework.platform.pdf.context.font.PdfTextFont;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * PdfFontStyleTest
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-02-10 10:14
 */
@DisplayName("PdfTestFontTest")
public class PdfTextFontTest {


    @DisplayName("getPdfFont")
    @Test
    public void getPdfFont() throws IOException {
        PdfTextFont pdfTextFont = new PdfFontStyle();
        Assertions.assertNotNull(pdfTextFont.getPdfFont());

        PdfFont font = PdfFontFactory.createFont("/static/fonts/simsun.ttc,1");
        Assertions.assertNotNull(font.getFontProgram());
    }


}
