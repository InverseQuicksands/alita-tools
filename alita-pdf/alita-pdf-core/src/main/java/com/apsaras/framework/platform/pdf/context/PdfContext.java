package com.apsaras.framework.platform.pdf.context;


import com.itextpdf.kernel.events.IEventHandler;
import freemarker.template.Template;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 生成 PDF 上下文.
 */
public interface PdfContext {

    /**
     * 生成 pdf.
     *
     * @param htmlContext html 模板内容
     * @param outputStream pdf 文件
     * @param eventHandlers 事件
     * @throws IOException 异常
     */
    void convertToPdf(String htmlContext, OutputStream outputStream, IEventHandler... eventHandlers) throws IOException;

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
    <T> void convertToPdf(List<T> dataList, OutputStream outputStream, int rowNum, IEventHandler... eventHandlers);

    void setTemplate(Template template);
}
