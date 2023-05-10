package com.alita.framework.pdf.pdf.html;

import com.apsaras.framework.platform.pdf.context.PdfContext;
import com.apsaras.framework.platform.pdf.context.font.PdfFontStyle;
import com.apsaras.framework.platform.pdf.context.font.PdfTextFont;
import com.apsaras.framework.platform.pdf.context.headerfooter.BaseHeaderFooterEvent;
import com.apsaras.framework.platform.pdf.context.image.PdfPositionImageEvent;
import com.apsaras.framework.platform.pdf.html.AbstractHtmlConvertPdfContext;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CreatePdfTest
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-02-13 15:26
 */
@DisplayName("CreatePdfTest")
public class CreatePdfTest {


    @Test
    public void createPdf() throws Exception {
        PdfTextFont pdfFont = new PdfFontStyle();
        PdfFontStyle fontStyle = pdfFont.loadFont("/static/fonts/simsun.ttc,1");

        FreeMarkerConfigurationFactoryBean factoryBean = new FreeMarkerConfigurationFactoryBean();
        factoryBean.setTemplateLoaderPath("/templates/");
        factoryBean.setDefaultEncoding("UTF-8");
        Configuration beanObject = factoryBean.createConfiguration();
        Template template = beanObject.getTemplate("pdfResume.ftl");
        Map<String, Object> map = new HashMap<>(8);
        map.put("statisticalTime", "2022-04-30");

        AbstractHtmlConvertPdfContext pdfContext = new AbstractHtmlConvertPdfContext(fontStyle);
        pdfContext.setTemplate(template);
        String content = pdfContext.htmlTemplateContent(map);
        pdfContext.convertToPdf(content, new FileOutputStream("/Users/zhang/Desktop/test33.pdf"));
    }


    @Test
    public void createPdf1() throws Exception {
        long start = System.currentTimeMillis();
        PdfTextFont pdfFont = new PdfFontStyle().loadFont("/static/fonts/simsun.ttc,0");

        FreeMarkerConfigurationFactoryBean factoryBean = new FreeMarkerConfigurationFactoryBean();
        factoryBean.setTemplateLoaderPath("/templates/");
        factoryBean.setDefaultEncoding("UTF-8");
        Configuration beanObject = factoryBean.createConfiguration();
        Template template = beanObject.getTemplate("water.ftl");

        PdfContext pdfContext = new AbstractHtmlConvertPdfContext(pdfFont);
        pdfContext.setTemplate(template);

        List<Map> list = new ArrayList<>(30);
        // 模仿1000条数据
        for (int i=0; i<1000; i++) {
            // 模仿单个对象数据
            Map<String, String> hashMap = new HashMap<>(16);
            hashMap.put("data", "2022-03-14");
            hashMap.put("num", "0001");
            hashMap.put("jiesuan", "结算活期");
            hashMap.put("money", "人民币元");
            hashMap.put("chao", "钞");
            hashMap.put("jin", "36.00");
            hashMap.put("yu", "244.64");
            hashMap.put("hu", "迁九七");
            hashMap.put("zhang", "6217991410000400503");
            hashMap.put("zhai", "转账汇出");
            hashMap.put("channel", "手机银行");

            list.add(hashMap);
        }

        // 页脚事件
        BaseHeaderFooterEvent footerEvent = new BaseHeaderFooterEvent(pdfFont);
        footerEvent.setFooter(true);
        // 总页数
        footerEvent.setTotalNum(53);

        // 在指定位置插入盖章图片
        PdfPositionImageEvent imageEvent = new PdfPositionImageEvent("/static/095.png", null);
//        PdfPositionImageEvent imageEvent = new PdfPositionImageEvent("/Users/zhang/zhang/学习/学习项目/后端/idea_work/work/apsaras-pdf/apsaras-pdf-core/src/test/resources/static/095.png");

        // 生成PDF
        pdfContext.convertToPdf(list, new FileOutputStream("/Users/zhang/Desktop/test88.pdf"),
                19, footerEvent, imageEvent);
        long endTime = System.currentTimeMillis();
        long aa = endTime - start;

        // TODO 1000条数据耗时 3535ms，待优化
        System.out.println("总耗时：" + aa);

    }




}
