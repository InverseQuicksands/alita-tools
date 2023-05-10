package com.alita.framework.pdf.pdf.context.image;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.font.FontProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * IText7添加图片覆盖文字
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-02-13 23:14
 * @see <p> https://blog.csdn.net/super_this_part/article/details/127880695
 */
public class PictureTest {


    public static void htmlToPdf2(String html, OutputStream os, String imgFilePath)throws Exception {

        PdfWriter writer = null;
        PdfDocument pdf = null;
        Document document = null;
        try {
            writer = new PdfWriter(os);
            pdf = new PdfDocument(writer);
            ConverterProperties properties = new ConverterProperties();
            FontProvider fontProvider = new FontProvider();
            // 微软雅黑
            fontProvider.addFont(FontProgramFactory.createFont(""));
            // 微软雅黑粗体
            fontProvider.addFont(FontProgramFactory.createFont(""));
            properties.setFontProvider(fontProvider);
            pdf.setDefaultPageSize(PageSize.A4);
//            pdf.getDocumentInfo().setAuthor("小杨先森");
//            pdf.getDocumentInfo().setTitle("小杨博客");
//            pdf.getDocumentInfo().setSubject("Nice");
//            pdf.getDocumentInfo().setMoreInfo("one", "yes");
//            pdf.getDocumentInfo().setKeywords("xiaoyang");
            document = HtmlConverter.convertToDocument(html, writer, properties);
            PdfDocument pdfDocument = document.getPdfDocument();
            PdfPage page = pdfDocument.addNewPage();
            PdfCanvas pdfCanvas = new PdfCanvas(page);
            Image image = new Image(ImageDataFactory.create(imgFilePath));
            Rectangle rectangle = new Rectangle(document.getRightMargin() + 20, PageSize.A4.getWidth(), image.getImageWidth(), image.getImageHeight());
            Canvas canvas = new Canvas(pdfCanvas, rectangle);
            canvas.add(image);
//            Image image = new Image(ImageDataFactory.create(imgFilePath));
//            image.setFixedPosition(document.getRightMargin() + 20, PageSize.A4.getWidth());
//            document.add(image);
            document.close();
            System.out.println("成功!");
        } catch (FileNotFoundException exception){
            System.err.println("文件不存在！");
            exception.printStackTrace();
        } catch (IOException exception) {
            System.err.println("文件IO错误！");
            exception.printStackTrace();
        }
    }



}
