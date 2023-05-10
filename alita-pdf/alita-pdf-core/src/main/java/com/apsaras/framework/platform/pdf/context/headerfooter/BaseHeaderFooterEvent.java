package com.apsaras.framework.platform.pdf.context.headerfooter;

import com.apsaras.framework.platform.pdf.context.font.PdfTextFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.util.Assert;

/**
 * 页眉、页脚基础类
 *
 * @date 2023-02-10 15:39
 */
public class BaseHeaderFooterEvent extends AbstractPdfHeaderFooterEvent{

    /**
     * pdf 字体设置
     */
    private final PdfTextFont pdfFont;

    public BaseHeaderFooterEvent(PdfTextFont pdfFont) {
        this.pdfFont = pdfFont;
    }

    /**
     * 生成页眉
     *
     * @param doc       Document
     * @param pdfHeight pdf 宽度
     */
    @Override
    public void addPageHeader(Document doc, float pdfHeight) {
        float width = PageSize.A4.getWidth()-60;
        Assert.hasText(super.getHeaderContent(), "HeaderContent must not be null!");

        //表格 一行一列
        Table headerTable = new Table(1);
        headerTable.setFixedLayout();
        headerTable.setWidth(width);
        headerTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

        //名称
        Paragraph paragraph = new Paragraph(super.getHeaderContent());
        paragraph.setFont(pdfFont.getPdfFont());
        paragraph.setFontSize(8f);

        Cell rightCell = new Cell();
        rightCell.add(paragraph);
        rightCell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        rightCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        rightCell.setTextAlignment(TextAlignment.RIGHT);
        rightCell.setBorder(Border.NO_BORDER);

        headerTable.addCell(rightCell);

        //设置表格的位置 页眉处
        headerTable.setFixedPosition(doc.getLeftMargin()-10, pdfHeight-doc.getTopMargin(), headerTable.getWidth());
        doc.add(headerTable);
    }

    /**
     * 生成页脚
     *
     * @param doc    Document
     * @param pdfDoc PdfDocument
     * @param page   页码
     */
    @Override
    public void addPageFooter(Document doc, PdfDocument pdfDoc, PdfPage page) {
        //页码 居右
        Rectangle pageSize = page.getPageSize();
        // 当前页
        int pageNum = pdfDoc.getPageNumber(page);
        // 总页数
        int numberOfPages = pdfDoc.getNumberOfPages();

//        Paragraph paragraph = new Paragraph("第" + pageNum + "页/共" + numberOfPages + "页");
        Paragraph paragraph = new Paragraph("第 " + pageNum + " 页");
        paragraph.add(" / 共 " + super.getTotalNum() + " 页");
        paragraph.setFont(pdfFont.getPdfFont());
        paragraph.setFontSize(8f);

        Canvas canvas = new Canvas(page, pageSize);
        float coordX = pageSize.getRight() - doc.getRightMargin() - 20;
        float footerY = pageSize.getBottom() + 15;
        canvas.showTextAligned(paragraph, coordX, footerY, TextAlignment.CENTER);
        canvas.close();
    }
}
