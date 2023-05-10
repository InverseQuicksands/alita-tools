package com.apsaras.framework.platform.pdf.context.image;

public class ImageInfo {

    /**
     * 图片在 PDF doc的 x 轴坐标
     */
    private float x;

    /**
     * 图片在 PDF doc的 y 轴坐标
     */
    private float y;

    /**
     * 是否缩放
     */
    private boolean scale;

    /**
     * 缩放后最大宽度
     */
    private float fitWidth = 105;

    /**
     * 缩放后最大高度
     */
    private float fitHeight = 80;


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isScale() {
        return scale;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }

    public float getFitWidth() {
        return fitWidth;
    }

    public void setFitWidth(float fitWidth) {
        this.fitWidth = fitWidth;
    }

    public float getFitHeight() {
        return fitHeight;
    }

    public void setFitHeight(float fitHeight) {
        this.fitHeight = fitHeight;
    }
}
