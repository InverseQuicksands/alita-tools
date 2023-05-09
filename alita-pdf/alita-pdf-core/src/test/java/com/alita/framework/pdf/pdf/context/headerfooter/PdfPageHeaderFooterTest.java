package com.alita.framework.pdf.pdf.context.headerfooter;

import com.alita.framework.pdf.pdf.context.font.PdfFontStyle;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * PdfPageHeaderFooterTest
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-02-10 10:18
 */
@DisplayName("PdfPageHeaderFooterTest")
public class PdfPageHeaderFooterTest {


    @DisplayName("header")
    @Test
    public void header() throws IOException {
        String pdfPath = "/Users/zhang/Desktop/header.pdf";
        FileOutputStream outputStream = new FileOutputStream(pdfPath);

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        BaseHeaderFooterEvent headerFooter = new BaseHeaderFooterEvent(new PdfFontStyle());
        headerFooter.setHeader(true);
//        headerFooter.setHeaderContent("流沙");
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, headerFooter);
        for (int i = 0; i < 3; i++) {
            document.add(new Paragraph("Test " + (i + 1)));
            document.add(new AreaBreak());
        }

        document.close();
    }

    @DisplayName("footer")
    @Test
    public void footer() throws IOException {
        String pdfPath = "/Users/zhang/Desktop/footer.pdf";
        FileOutputStream outputStream = new FileOutputStream(pdfPath);

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        BaseHeaderFooterEvent headerFooter = new BaseHeaderFooterEvent(new PdfFontStyle());
        headerFooter.setFooter(true);
        headerFooter.setTotalNum(4);
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, headerFooter);
        for (int i = 0; i < 3; i++) {
            document.add(new Paragraph("Test " + (i + 1)));
            document.add(new AreaBreak());
        }

        document.close();
    }


}



