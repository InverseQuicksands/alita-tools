package com.apsaras.framework.platform.pdf;

import com.apsaras.framework.platform.io.ResourceResolver;
import com.apsaras.framework.platform.io.ResourcePatternLoader;
import com.apsaras.framework.platform.pdf.context.font.PdfFontStyle;
import com.apsaras.framework.platform.pdf.context.font.PdfTextFont;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

/**
 * PDF 全局配置类
 */
@AutoConfiguration
@EnableConfigurationProperties({PdfConfigurationProperties.class})
public class PDFAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(PDFAutoConfiguration.class);

    /**
     * 扫描字体的路径
     * 如果要使用.ttc字体文件，需要在加数字1.如：{@code PdfFontFactory.createFont("/static/fonts/simsun.ttc,1")}
     */
    public static final String FONT_LOADER_PATH = "classpath*:/static/fonts/*.ttf";

    private final PdfConfigurationProperties pdfConfigurationProperties;

    public PDFAutoConfiguration(PdfConfigurationProperties pdfConfigurationProperties) {
        this.pdfConfigurationProperties = pdfConfigurationProperties;
    }

    /**
     * 配置 PDF 字体.
     *
     * @return PdfFontStyle
     * @throws IOException 异常
     */
    @Bean
    @ConditionalOnMissingBean
    public PdfFontStyle pdfFontStyle() throws IOException {
        ResourceResolver resourceResolver = new ResourcePatternLoader();
        Resource[] patternResources = resourceResolver.findPatternResources(FONT_LOADER_PATH);
        logger.debug("font resource path: {}", Arrays.toString(patternResources));
        String fontName = pdfConfigurationProperties.getFontName();
        if (StringUtils.isNotBlank(fontName)) {
            Resource pdfFontResource = Arrays.stream(patternResources)
                    .filter(resource -> resource.getFilename().equals(fontName))
                    .findFirst()
                    .get();

            byte[] bytes = pdfFontResource.getInputStream().readAllBytes();
            PdfFontStyle pdfFont = new PdfFontStyle();
            pdfFont.loadFont(bytes);
            return pdfFont;
        }

        return new PdfFontStyle();
    }



}
