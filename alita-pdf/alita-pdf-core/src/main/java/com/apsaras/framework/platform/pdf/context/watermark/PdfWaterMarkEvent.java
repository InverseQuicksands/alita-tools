package com.apsaras.framework.platform.pdf.context.watermark;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

/**
 * PDF 水印
 */
public class PdfWaterMarkEvent implements PdfTextWaterMark, IEventHandler {

    private PdfWaterMark pdfWaterMark;

    private PdfFont pdfFont;

    private boolean flag;

    public PdfWaterMarkEvent(PdfWaterMark pdfWaterMark, PdfFont pdfFont) {
        this.pdfWaterMark = pdfWaterMark;
        this.pdfFont = pdfFont;
    }

    /**
     * Hook for handling events. Implementations can access the PdfDocument instance
     * associated to the specified Event or, if available, the PdfPage instance.
     *
     * @param event the Event that needs to be processed
     */
    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent documentEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = documentEvent.getDocument();
        PdfPage page = documentEvent.getPage();
        Rectangle pageSize = page.getPageSize();

        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);
        Paragraph waterMark = new Paragraph(pdfWaterMark.getWaterMarkContent())
                .setOpacity(pdfWaterMark.getFillOpacity());

        Canvas canvas = new Canvas(pdfCanvas, pageSize)
                .setFontColor(ColorConstants.GRAY)
                .setFont(this.pdfFont)
                .setFontSize(pdfWaterMark.getFontSize());

        for (int i = 0; i < pdfWaterMark.getWaterMarkX(); i++) {
            for (int j = 0; j < pdfWaterMark.getWaterMarkY(); j++) {
                canvas.showTextAligned(waterMark, (250 + i * 300), (260 + j * 150),
                        pdfDoc.getNumberOfPages(), TextAlignment.CENTER, VerticalAlignment.BOTTOM, 170);
            }
        }
        canvas.close();
    }


    /**
     * 是否显示水印.
     *
     * @return default false
     */
    @Override
    public boolean isWaterMark() {
        return this.flag;
    }

    /**
     * 设置是否显示水印
     *
     * @param flag default false
     */
    @Override
    public void setPdfWaterMark(boolean flag) {
        this.flag = flag;
    }

    /**
     * pdf 水印设置.
     *
     * @return PdfWaterMark
     */
    @Override
    public PdfWaterMark getPdfWaterMark() {
        return this.pdfWaterMark;
    }
}
