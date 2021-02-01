package com.ewrite.ewrite_sdk.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;


/*
 * Describtion :
 * Create by sunlp on 2019/7/12 13:20
 */
public class EwriteNoteBookScaleUtil {
    /**
     * 参考笔记本的宽度
     */
    public static float BASE_NOTEBOOK_WIDTH = 373;
    /**
     * 参考笔记本的高度
     */
    public static float BASE_NOTEBOOK_HEIGHT = 373;

    public static void setBaseNotebookWidth(float baseNotebookWidth) {
        BASE_NOTEBOOK_WIDTH = baseNotebookWidth;
    }

    public static void setBaseNotebookHeight(float baseNotebookHeight) {
        BASE_NOTEBOOK_HEIGHT = baseNotebookHeight;
    }

    /**
     * 得到宽度的缩放比例
     *
     * @return
     */
    public static float getWidthScale(Context context, float dpValue) {
        return BASE_NOTEBOOK_WIDTH / dip2px(context, dpValue);
//        return BASE_NOTEBOOK_WIDTH / EwriteScreenUtil.getScreenWidth(context);
    }

    /**
     * 得到高度的缩放比例
     *
     * @return
     */
    public static float getHeightScale(Context context, float dpValue) {
        return BASE_NOTEBOOK_HEIGHT / dip2px(context, dpValue);
//        return BASE_NOTEBOOK_HEIGHT / EwriteScreenUtil.getScreenHeight(context);
    }

    /**
     * 得到宽度的缩放比例
     *
     * @return
     */
    public static float getWidthScale(Context context) {
        return getWidthScale(context, 100);
    }

    /**
     * 得到高度的缩放比例
     *
     * @return
     */
    public static float getHeightScale(Context context) {
        return getHeightScale(context, 100);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将dp值转换为px值，保证尺寸大小不变
     *
     * @return
     */
    public static int dpAdapt(float dp, float widthDpBase, Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        int heightPixels = metric.heightPixels;//高的像素
        int widthPixels = metric.widthPixels;//宽的像素
//        int densityDpi = dm.densityDpi;//dpi
//        float xdpi = dm.xdpi;//xdpi
//        float ydpi = dm.ydpi;//ydpi
        float density = metric.density;//density=dpi/160,密度比
//        float scaledDensity = dm.scaledDensity;//scaledDensity=dpi/160 字体缩放密度比
        float heightDP = heightPixels / density;//高度的dp
        float widthDP = widthPixels / density;//宽度的dp
        float w = widthDP > heightDP ? heightDP : widthDP;
        if (widthDpBase == 0) {
            widthDpBase = widthDP;
        }
        return (int) (dp * w / widthDpBase * density + 0.5f);
    }


}
