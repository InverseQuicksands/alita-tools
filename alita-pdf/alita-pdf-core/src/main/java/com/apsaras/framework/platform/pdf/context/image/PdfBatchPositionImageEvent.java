package com.apsaras.framework.platform.pdf.context.image;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 每页 PDF 添加图片
 */
public class PdfBatchPositionImageEvent implements IEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(PdfPositionImageEvent.class);

    private List<byte[]> imgFileByte;

    private ImageInfo imageInfo;

    public PdfBatchPositionImageEvent(List<byte[]> imgFileByte, ImageInfo imageInfo) {
        this.imgFileByte = imgFileByte;
        this.imageInfo = imageInfo;
    }

    /**
     * Hook for handling events. Implementations can access the PdfDocument instance
     * associated to the specified Event or, if available, the PdfPage instance.
     *
     * @param event the Event that needs to be processed
     */
    @Override
    public void handleEvent(Event event) {
        final PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        final PdfDocument pdfDoc = docEvent.getDocument();
        final Document doc = new Document(pdfDoc);
        final PdfPage page = docEvent.getPage();
        int numberOfPages = pdfDoc.getNumberOfPages();

        PdfCanvas pdfCanvas = new PdfCanvas(page);
        Image image = new Image(ImageDataFactory.create(imgFileByte.get(numberOfPages-1)));
        if (this.imageInfo.isScale()) {
            image.scaleToFit(this.imageInfo.getFitWidth(), this.imageInfo.getFitHeight());
        }
        // 设置到相同的地方
        Rectangle rectangle = new Rectangle(doc.getLeftMargin() + this.imageInfo.getX(), imageInfo.getY(), image.getImageWidth(), image.getImageHeight());
        Canvas canvas = new Canvas(pdfCanvas, rectangle);
        canvas.add(image);
        canvas.close();
    }
}
