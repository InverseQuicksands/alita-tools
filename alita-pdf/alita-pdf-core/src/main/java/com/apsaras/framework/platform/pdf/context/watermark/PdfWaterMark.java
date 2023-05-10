package com.apsaras.framework.platform.pdf.context.watermark;

public class PdfWaterMark {

    /**
     * 水印内容
     */
    private String waterMarkContent;

    /**
     * 一页中有几列水印(x坐标)
     */
    private int waterMarkX;

    /**
     * 一页中每列有多少水印(y坐标)
     */
    private int waterMarkY;

    /**
     * 设置水印透明度
     */
    private float fillOpacity = 0.3f;

    /**
     * 字体大小
     */
    private int fontSize;


    public String getWaterMarkContent() {
        return waterMarkContent;
    }

    public void setWaterMarkContent(String waterMarkContent) {
        this.waterMarkContent = waterMarkContent;
    }

    public int getWaterMarkX() {
        return waterMarkX;
    }

    public void setWaterMarkX(int waterMarkX) {
        this.waterMarkX = waterMarkX;
    }

    public int getWaterMarkY() {
        return waterMarkY;
    }

    public void setWaterMarkY(int waterMarkY) {
        this.waterMarkY = waterMarkY;
    }

    public float getFillOpacity() {
        return fillOpacity;
    }

    public void setFillOpacity(float fillOpacity) {
        this.fillOpacity = fillOpacity;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public String toString() {
        return "PdfWaterMark{" +
                ", waterMarkContent='" + waterMarkContent + '\'' +
                ", waterMarkX=" + waterMarkX +
                ", waterMarkY=" + waterMarkY +
                ", fillOpacity=" + fillOpacity +
                ", fontSize=" + fontSize +
                '}';
    }
}
