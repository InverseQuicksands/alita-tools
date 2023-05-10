package com.apsaras.framework.platform.pdf.context.headerfooter;

/**
 * 页眉、页脚
 */
public interface PdfPageHeaderFooter {

    /**
     * 设置页眉
     *
     * @param headerContent 页眉内容
     */
    void setHeaderContent(String headerContent);

    /**
     * 设置页脚
     *
     * @param footerContent 页脚内容
     */
    void setFooterContent(String footerContent);

    /**
     * 设置是否展示页眉
     *
     * @param header defualt false
     */
    void setHeader(boolean header);

    /**
     * 设置是否展示页脚
     *
     * @param footer default false
     */
    void setFooter(boolean footer);

    /**
     * 设置总页数
     *
     * @param totalNum 总页数
     */
    void setTotalNum(int totalNum);

}
