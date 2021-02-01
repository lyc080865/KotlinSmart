package com.ewrite.ewrite_sdk.utils;

import android.util.Log;

/*
 * Describtion :
 * Create by sunlp on 2019/5/7 16:48
 */
public class EwritePointUtils {

    public static int x, y;


    /**
     * 落笔书写抬笔标识
     */
    public static int getPenStatues(String numberlast) {
        return Integer.parseInt(numberlast.substring(0, 1), 16);
    }

    /**
     * 笔画号
     */
    public static String getStrokes(String numberlast) {
        return numberlast.substring(2, 4);//笔画号
    }

    //转换后的X坐标
    public static int getXLocation(String numberlast, float SCALE_X, float basePoint) {
        return getXLocation(numberlast, SCALE_X, basePoint, x);//X坐标
    }

    //转换后的Y坐标
    public static int getYLocation(String numberlast, float SCALE_Y, float basePoint) {
        return getYLocation(numberlast, SCALE_Y, basePoint, y);//Y坐标
    }

    //转换后的X坐标(上传过的,用上传时的偏移量绘制)
    public static int getXLocation(String numberlast, float SCALE_X, float basePoint, int xOffset) {
        return (int) (((Integer.parseInt(numberlast.substring(7, 9) + numberlast.substring(5, 7), 16) - xOffset) / SCALE_X));//X坐标
    }

    //转换后的Y坐标(上传过的,用上传时的偏移量绘制)
    public static int getYLocation(String numberlast, float SCALE_Y, float basePoint, int yOffset) {
        return (int) (((Integer.parseInt(numberlast.substring(12, 14) + numberlast.substring(10, 12), 16) - yOffset) / SCALE_Y));//Y坐标
    }

//    //转换后的X坐标
//    public static int getXLocations(String numberlast, float SCALE_X, float basePoint) {
////        Log.e("getLocationX", numberlast + "  " + SCALE_X + "  " + basePoint + " " + (int) ((Integer.parseInt(numberlast.substring(7, 9) + numberlast.substring(5, 7), 16) / SCALE_X) - 90 * basePoint));
//        return (int) (((Integer.parseInt(numberlast.substring(7, 9) + numberlast.substring(5, 7), 16) - 90) / SCALE_X));//X坐标
//    }
//
//    //转换后的Y坐标
//    public static int getYLocations(String numberlast, float SCALE_Y, float basePoint) {
////        Log.e("getLocationY", numberlast + "  " + SCALE_Y + "  " + basePoint + " " + (int) ((Integer.parseInt(numberlast.substring(12, 14) + numberlast.substring(10, 12), 16) / SCALE_Y) - 60 * basePoint));
//        return (int) (((Integer.parseInt(numberlast.substring(12, 14) + numberlast.substring(10, 12), 16) - 80) / SCALE_Y));//Y坐标
//    }

    public static int getBeforeX(String numberlast) {
        return (Integer.parseInt(numberlast.substring(7, 9) + numberlast.substring(5, 7), 16));//X坐标
    }

    public static int getBeforeY(String numberlast) {
        return (Integer.parseInt(numberlast.substring(12, 14) + numberlast.substring(10, 12), 16));//Y坐标
    }

    public static String getLastPage(String numberlast) {
        return numberlast.substring(9, 25);
    }

    public static Integer getPenPower(String str) {
        return Integer.parseInt(str.substring(9, 10));
    }

    public static int getPenPressure(String numberlast) {
        return Integer.parseInt(numberlast.substring(15, 17), 16);
    }
}
