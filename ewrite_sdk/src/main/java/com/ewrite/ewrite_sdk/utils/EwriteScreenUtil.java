package com.ewrite.ewrite_sdk.utils;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/***
 * 对布局进行屏幕适配操作
 */
public class EwriteScreenUtil {

    /**
     * 参考屏幕的宽度
     */
    private final static float BASE_SCREEN_WIDTH = 2332;
    /**
     * 参考屏幕的高度
     */
    private final static float BASE_SCREEN_HEIGHT = 1038;

    /**
     * 得到宽度的缩放比例
     *
     * @return
     */
    public static float getWidthScale(Context context) {
        return getScreenWidth(context) / BASE_SCREEN_WIDTH;
    }

    /**
     * 得到高度的缩放比例
     *
     * @return
     */
    public static float getHeightScale(Context context) {
        return getScreenHeight(context) / BASE_SCREEN_HEIGHT;
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        return width;
    }


    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;       // 屏幕高度（像素）
        return height;

    }

}
