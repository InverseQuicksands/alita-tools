package com.apsaras.framework.platform.pdf.html;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class PdfHtmlConverter {

    private final Configuration freemarkerConfiguration;

    public PdfHtmlConverter(Configuration freemarkerConfiguration) {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    /**
     * 根据传入的模板路径，找到对应的模板，将 freemarker 模板内的变量替换成 dataMap 里的值，
     * 最后输出模板内容.
     *
     * @param templateName 模板文件相对路径 eg: "pdf_demo.ftl"
     * @param dataMap 传入 ftl 模板的 Map 数据
     * @return html 内容
     * @throws Exception 异常
     */
    public String getFtlContent(String templateName, Map<String, Object> dataMap) throws Exception {
        StringWriter out = new StringWriter();
        BufferedWriter writer = new BufferedWriter(out);

        try {
            Template template = this.freemarkerConfiguration.getTemplate(templateName);
            template.process(dataMap, writer);
            writer.flush();
        } catch (IOException e) {
            throw new FreemarkerException("The [" + templateName + "] template not found in classpath.");
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }

        return out.toString();
    }

}
