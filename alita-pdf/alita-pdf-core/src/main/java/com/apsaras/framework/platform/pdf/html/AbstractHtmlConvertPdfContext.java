package com.apsaras.framework.platform.pdf.html;

import com.apsaras.framework.platform.pdf.context.PdfContext;
import com.apsaras.framework.platform.pdf.context.font.PdfTextFont;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.font.FontProvider;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * html 模板转 PDF.
 */
public class AbstractHtmlConvertPdfContext implements PdfContext {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHtmlConvertPdfContext.class);

    private PdfTextFont pdfTextFont;

    /**
     * 缓存字体配置，避免重复创建
     */
    private ConverterProperties converterProperties;

    private Template template;

    public AbstractHtmlConvertPdfContext(PdfTextFont pdfTextFont) {
        this.pdfTextFont = pdfTextFont;

        //添加中文字体支持
        PdfFont pdfFont = this.pdfTextFont.getPdfFont();
        FontProvider fontProvider = new FontProvider();
        fontProvider.addFont(pdfFont.getFontProgram());

        this.converterProperties = new ConverterProperties();
        this.converterProperties.setFontProvider(fontProvider);
        this.converterProperties.setCharset("utf-8");
    }

    /**
     * 生成 pdf.
     *
     * @param htmlContext html 模板内容
     * @param outputStream pdf 文件
     * @param eventHandlers 事件
     * @throws IOException 异常
     */
    @Override
    public void convertToPdf(String htmlContext, OutputStream outputStream, IEventHandler... eventHandlers) throws IOException {

        PdfWriter pdfWriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        // 设置页边距
        pdfDocument.getDefaultPageSize()
                .applyMargins(20, 20, 20, 20, true);

        // 生成PDF
        HtmlConverter.convertToPdf(htmlContext, pdfDocument, this.converterProperties);
        // 添加事件
        if (eventHandlers != null && eventHandlers.length > 0) {
            for (int i = 0; i < eventHandlers.length; i++) {
                pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandlers[i]);
            }
        }

        pdfWriter.close();
        pdfDocument.close();
    }


    /**
     * 生成PDF.
     *
     * <p>每页PDF将添加固定内容
     *
     * @param dataList 数据集合
     * @param outputStream 生成PDF路径
     * @param rowNum 每页的行数
     * @param eventHandlers 各种事件：页眉/页脚，水印等
     */
    public <T> void convertToPdf(List<T> dataList, OutputStream outputStream, int rowNum, IEventHandler... eventHandlers) {
        PdfWriter pdfWriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
        Document doc = new Document(pdfDocument);

        if (eventHandlers != null && eventHandlers.length > 0) {
            for (int i = 0; i < eventHandlers.length; i++) {
                pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandlers[i]);
            }
        }

        List<T> list = new ArrayList<>(rowNum);
        int maxSize = dataList.size()-1;
        for (int i=0; i<dataList.size(); i++) {
            list.add(dataList.get(i));
            if (list.size() == rowNum) {
                add(list, doc);
                doc.add(new AreaBreak());
                list.clear();
                continue;
            }
            if (i == maxSize) {
                add(list, doc);
            }
        }
        doc.close();
    }


    private <T> void add(List<T> list, Document doc) {
        Map<String, Object> map = new HashMap<>(1);
        // 此处的 "list" 名字必须与模板中 "<#list list as stu>" 的 list 名字相同
        map.put("list", list);
        String htmlContext = htmlTemplateContent(map);
        List<IElement> iElements = HtmlConverter.convertToElements(htmlContext, this.converterProperties);
        for (IElement ie : iElements) {
            doc.add((IBlockElement) ie);
        }
    }

    /**
     * 将数据添加到 freemarker 模板中，并将模板转换成 html 字符串.
     *
     * @param map 数据
     * @return html 字符串
     */
    public String htmlTemplateContent(Map<String, Object> map) {
        StringWriter out = new StringWriter();
        BufferedWriter writer = new BufferedWriter(out);
        Assert.notNull(this.template, "The template must not be null!");
        try {
            this.template.process(map, writer);
            writer.flush();
        } catch (IOException | TemplateException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return out.toString();
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
}
