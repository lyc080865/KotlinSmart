package com.ewrite.ewrite_sdk.bean;

/*
 * Describtion :
 * Create by sunlp on 2019/5/5 10:54
 */public class EwritePoint {
    private String color;
    private String pointsStr;
    private int xOffset, yOffset;//需要指定偏移量
    private boolean isUsedHttpOffset=false;//是否是使用上传时保存的偏移量

    public EwritePoint(String color, String pointsStr) {
        this.color = color;
        this.pointsStr = pointsStr;
    }

    public EwritePoint(String color, String pointsStr, int xOffset, int yOffset) {
        this.color = color;
        this.pointsStr = pointsStr;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        isUsedHttpOffset=true;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPointsStr() {
        return pointsStr;
    }

    public void setPointsStr(String pointsStr) {
        this.pointsStr = pointsStr;
    }

    public int getXOffset() {
        return xOffset;
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public boolean isUsedHttpOffset() {
        return isUsedHttpOffset;
    }

    @Override
    public boolean equals(Object obj) {
        EwritePoint point = (EwritePoint) obj;
        if (point.getColor().equals(getColor()) && point.getPointsStr().equals(getPointsStr())) {
            return true;
        } else {
            return false;
        }
    }
}
