package com.alita.framework.pdf.pdf.context.headerfooter;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;

/**
 * VariableHeader
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-02-10 10:55
 */
public class VariableHeader {
    public static final String DEST = "/Users/zhang/Desktop/variable_header.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();

        new VariableHeader().manipulatePdf(DEST);
    }

    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);
        VariableHeaderEventHandler handler = new VariableHeaderEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, handler);

        for (int i = 0; i < 4; i++) {
            doc.add(new Paragraph("This is a prime number!"));
            doc.add(new Paragraph("Factor: " + i));
            handler.setHeader(String.format("THE FACTORS OF %s", i));

            if (4 != i) {
                doc.add(new AreaBreak());
            }
        }

        doc.close();
    }


    private static class VariableHeaderEventHandler implements IEventHandler {
        protected String header;

        public void setHeader(String header) {
            this.header = header;
        }

        @Override
        public void handleEvent(Event currentEvent) {
            PdfDocumentEvent documentEvent = (PdfDocumentEvent) currentEvent;
            PdfPage page = documentEvent.getPage();
            new Canvas(page, page.getPageSize())
                    .showTextAligned(header, 490, 806, TextAlignment.CENTER)
                    .close();
        }
    }
}
