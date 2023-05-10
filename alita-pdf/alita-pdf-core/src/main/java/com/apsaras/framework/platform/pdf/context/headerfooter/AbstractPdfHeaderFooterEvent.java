package com.apsaras.framework.platform.pdf.context.headerfooter;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PDF 页眉、页脚抽象类
 */
public abstract class AbstractPdfHeaderFooterEvent implements PdfPageHeaderFooter, IEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPdfHeaderFooterEvent.class);

    /**
     * 是否展示页眉
     */
    private boolean header;

    /**
     * 是否展示页脚
     */
    private boolean footer;

    /**
     * 页眉内容
     */
    private String headerContent;

    /**
     * 页脚内容
     */
    private String footerContent;

    /**
     * 总页数
     */
    private int totalNum;

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
        final Rectangle pageSize = page.getPageSize();

        final float pdfHeight = pageSize.getHeight();

        // 页眉
        if (this.header) {
            try{
                this.addPageHeader(doc, pdfHeight);
            }catch(Exception e){
                logger.error("【添加页眉错误】", e);
            }
        }

        if (this.footer) {
            try{
                this.addPageFooter(doc, pdfDoc, page);
            }catch(Exception e){
                logger.error("【添加页脚错误】", e);
            }
        }
    }

    /**
     * 生成页眉
     *
     * @param doc Document
     * @param pdfHeight pdf 宽度
     */
    public abstract void addPageHeader(Document doc, float pdfHeight);

    /**
     * 生成页脚
     *
     * @param doc Document
     * @param pdfDoc PdfDocument
     * @param page 页码
     */
    public abstract void addPageFooter(Document doc, PdfDocument pdfDoc, PdfPage page);


    public String getHeaderContent() {
        return headerContent;
    }

    @Override
    public void setHeaderContent(String headerContent) {
        this.headerContent = headerContent;
    }

    public String getFooterContent() {
        return footerContent;
    }

    @Override
    public void setFooterContent(String footerContent) {
        this.footerContent = footerContent;
    }

    public boolean isHeader() {
        return header;
    }

    @Override
    public void setHeader(boolean header) {
        this.header = header;
    }

    public boolean isFooter() {
        return footer;
    }

    @Override
    public void setFooter(boolean footer) {
        this.footer = footer;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }
}
