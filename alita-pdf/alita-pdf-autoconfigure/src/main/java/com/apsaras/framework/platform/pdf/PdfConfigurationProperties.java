package com.apsaras.framework.platform.pdf;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "itextpdf")
public class PdfConfigurationProperties {

    /**
     * 字体名称
     */
    private String fontName;


    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
