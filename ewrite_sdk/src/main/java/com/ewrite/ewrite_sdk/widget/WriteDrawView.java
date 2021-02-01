package com.ewrite.ewrite_sdk.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.ewrite.ewrite_sdk.R;
import com.ewrite.ewrite_sdk.bean.EwritePoint;
import com.ewrite.ewrite_sdk.dialog.EwriteEditDrawDialog;
import com.ewrite.ewrite_sdk.dialog.choosecolordialog.EwriteChooseColorDialog;
import com.ewrite.ewrite_sdk.utils.EwriteCommenUtil;
import com.ewrite.ewrite_sdk.utils.EwriteJsonUtils;
import com.ewrite.ewrite_sdk.utils.EwriteNoteBookScaleUtil;
import com.ewrite.ewrite_sdk.utils.EwritePointUtils;
import com.ewrite.ewrite_sdk.utils.EwriteSPUtils;
import com.ewrite.ewrite_sdk.utils.EwriteScreenUtil;
import com.ewrite.ewrite_sdk.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/*
 * Describtion :
 * Create by sunlp on 2019/5/5 10:53
 */
public class WriteDrawView extends View implements EwriteEditDrawDialog.EditDialogClickCallback, EwriteChooseColorDialog.ChangeColorCallBack {
    public final int IS_PEN_WRITE = 0;
    public final int IS_LOOK = 1;
    public final int IS_EDIT_NOTE = 2;
    public final String COPY_POINTS = "copy_points";
    private final String IS_PEN_WRITE_STEP = "is_pen_write_step";
    private final String IS_LOOK_STEP = "is_look_step";
    private final String MOVE_STEP = "move_step";
    private final String PASTE_STEP = "paste_step";
    private final String COLOR_STEP = "color_step";
    private final String ERASER_STEP = "eraser_step";
    private final String DELETE_STEP = "delete_step";
    private float SCALE_X;
    private float SCALE_Y;
    private ArrayList<EwritePoint> mPointList = new ArrayList<>();//全部画布点坐标
    private ArrayList<EwritePoint> mMoveRectPointList;    //保存移动选择框后点坐标
    private ArrayList<EwritePoint> oldPointList;     //上传服务器追加点
    private ArrayList<EwritePoint> delPointList;     //上传服务器删除点
    private ArrayList<List<EwritePoint>> saveEditPointList;    //保存操作点坐标（写、移动、粘贴、更改颜色）
    private ArrayList<List<EwritePoint>> saveUndoEditPointList;    //保存撤销操作点坐标（移动、粘贴、更改颜色）
    private ArrayList<String> stepTypeList;     //记录操作步骤类型
    private ArrayList<String> stepUndoTypeList;     //记录操作步骤类型
    private ArrayList<List<EwritePoint>> preMovePointList;      //保存移动以前的坐标点
    private ArrayList<List<EwritePoint>> preChangeColorPointList;      //保存改变颜色以前的坐标点
    private ArrayList<List<EwritePoint>> preEraserPointList;      //保存橡皮擦擦除以前的坐标点
    private ArrayList<List<EwritePoint>> preDeletePointList;      //保存删除以前的坐标点
    private ArrayList<List<EwritePoint>> preUndoMovePointList;      //保存移动以前的坐标点
    private ArrayList<List<EwritePoint>> preUndoChangeColorPointList;      //保存改变颜色以前的坐标点
    private ArrayList<List<EwritePoint>> preUndoEraserPointList;      //保存橡皮擦擦除以前的坐标点
    private ArrayList<List<EwritePoint>> preUndoDeletePointList;      //保存删除以前的坐标点
    private Bitmap mBitmap;//总底部图片
    private Canvas mCanvas;//总画布
    private Path mPath;//总画笔路径
    private Paint mPaint;// 真实的画笔
    private float mPaintWidth = 3.0f;    //真实画笔的宽度
    private float mX, mY;// 临时点坐标
    private final float TOUCH_TOLERANCE = 4;
    // 保存Path路径的集合,用List集合来模拟栈
    private List<EwritePoint> editPoint;      //保存绘制点
    // 记录Path路径的对象
    private int screenWidth, screenHeight;// 屏幕長寬

    //橡皮擦
    private Paint mEraserPaint;
    private int mEraserWidth = 30;  //橡皮擦宽度

    //编辑选择框参数(以下)
    private EditFinishListener editFinishListener;
    private Paint mEditRectPaint;
    private int mEditLinePaintWidth = 3;
    private String mEditLinePaintColor = "#666666";
    private Paint mEditCirclePaint;
    private int mEditCiclePaintWidth = 3;
    private String mEditCirclePaintColor = "#5B93FF";
    private Path mEditCirclePath;
    private float startEditPointX, startEditPointY;     //触摸屏幕开始位置
    private float moveEditPointX, moveEditPointY;       //触摸屏幕移动位置
    private long downTime;
    private long moveTime;
    private float drawRectLeft, drawRectTop, drawRectRight, drawRectDown;       //最终画完矩形的坐标
    private float leftP;
    private float topP;
    private float rightP;
    private float endP;    //最终画完矩形的坐标（用于保存与替换的坐标）
    private float circleRadius;     //圆半径
    private boolean canEdit = false;    //可以画矩形
    private boolean isEdit = false;     //已经画了矩形
    private boolean isRectMoved = false;    //是否可以移动
    private float moveLeft, moveTop, moveRight, moveDown;       //矩形移动后的坐标
    private EwriteEditDrawDialog createNoteEditDialog;//选择框选择后弹出dialog
    private EwriteChooseColorDialog chooseColorDialog;//颜色选择dialog
    private boolean isCircleMove = false;
    private float slideX;
    private float slideY;
    private boolean isEraser = false;
    private Matrix drawMatrix;

    private Bitmap clipBitmap;      //编辑框
    private Paint clipPaint;
    private Canvas clipCanvas;
    private float slideStartPointX, slideMovePointX;
    private float slideStartPointY, slideMovePointY;
    private Matrix moveRectMatrix;
    private String defaultColor = "#333333";
    private int editColor = Color.parseColor(defaultColor);
    private List<EwritePoint> eraserPointList;

    private Context context;
    private boolean isShowBackGroudView = false;
    private ClickDownListener clickDownListener;
    private FlashDataListener flashDataListener;

    public WriteDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        SCALE_X = EwriteNoteBookScaleUtil.getWidthScale(context);
        SCALE_Y = EwriteNoteBookScaleUtil.getHeightScale(context);
        screenWidth = EwriteScreenUtil.getScreenWidth(context);
        screenHeight = EwriteScreenUtil.getScreenHeight(context);
        initMy();
//        initCircleRect();
//        initEraser();

        createNoteEditDialog = new EwriteEditDrawDialog(this.getContext(), this);
        chooseColorDialog = new EwriteChooseColorDialog(this.getContext(), this);

        minSize = EwriteNoteBookScaleUtil.dip2px(context, 1.2f);
        SharedPreferences sharedPreferences = context.getSharedPreferences("乐写字", Context.MODE_PRIVATE);
        changeP = sharedPreferences.getInt("sp_pen_size", 0);
        EwritePointUtils.x = sharedPreferences.getInt("sp_x_offset", 145);
        EwritePointUtils.y = sharedPreferences.getInt("sp_y_offset", 85);
    }

    private Paint getNewPaint(float paintWidth, int editColor) {
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        paint.setStrokeCap(Paint.Cap.ROUND);// 形状
        paint.setStrokeWidth(paintWidth);// 画笔宽度
        paint.setColor(editColor);
        paint.setDither(true);
//        LogUtils.e("getNewPaint   :  ", paintWidth + "  ");
        return paint;
    }

    private void initMy() {
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444);
        // 保存一次一次绘制出来的图形
        mCanvas = new Canvas(mBitmap);
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        mPaint.setDither(true);
        mPaint.setStrokeWidth(mPaintWidth);// 画笔宽度
        mPaint.setColor(Color.parseColor(defaultColor));
        oldPointList = new ArrayList<>();
        delPointList = new ArrayList<>();
        saveEditPointList = new ArrayList<>();
        stepTypeList = new ArrayList<>();
        preMovePointList = new ArrayList<>();
        preChangeColorPointList = new ArrayList<>();
        preEraserPointList = new ArrayList<>();
        saveUndoEditPointList = new ArrayList<>();
        stepUndoTypeList = new ArrayList<>();
        preUndoMovePointList = new ArrayList<>();
        preUndoChangeColorPointList = new ArrayList<>();
        preUndoEraserPointList = new ArrayList<>();
        preDeletePointList = new ArrayList<>();
        preUndoDeletePointList = new ArrayList<>();
    }

    private void initClip(Bitmap bitmap) {
        clipBitmap = bitmap;
        clipCanvas = new Canvas(clipBitmap);
        clipPaint = new Paint();
        clipPaint.setAntiAlias(true);
        clipPaint.setStyle(Paint.Style.STROKE);
        clipPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        clipPaint.setStrokeCap(Paint.Cap.SQUARE);// 形状
        clipPaint.setStrokeWidth(3);// 画笔宽度
    }

    private void initCircleRect() {
        circleRadius = EwriteScreenUtil.getWidthScale(context) * 14;
        mEditRectPaint = new Paint();
        mEditRectPaint.setColor(Color.parseColor(mEditLinePaintColor));
        mEditRectPaint.setStyle(Paint.Style.STROKE);
        mEditRectPaint.setStrokeWidth(mEditLinePaintWidth);

        mEditCirclePaint = new Paint();
        mEditCirclePaint.setColor(Color.parseColor(mEditCirclePaintColor));
        mEditCirclePaint.setStyle(Paint.Style.FILL);
        mEditCirclePaint.setStrokeWidth(mEditCiclePaintWidth);

        mEditRectPaint.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));

        mEditCirclePath = new Path();
    }

    private void initEraser() {
        mEraserPaint = new Paint();
        mEraserPaint.setAlpha(0);
        //这个属性是设置paint为橡皮擦重中之重
        //下面这句代码是橡皮擦设置的重点
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setDither(true);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeWidth(mEraserWidth);
    }

    @Override
    public void onDraw(Canvas canvas) {
//        // 将前面已经画过得显示出来
//        if (clipBitmap != null) {
//            canvas.drawBitmap(clipBitmap, moveRectMatrix, clipPaint);
//        }
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        }
//        if (canEdit) {
//            //绘制选择框虚线
//            if (isRectMoved) {
//                canvas.drawRect(moveLeft, moveTop, moveRight, moveDown, mEditRectPaint);
//            } else {
//                canvas.drawRect(drawRectLeft, drawRectTop, drawRectRight, drawRectDown, mEditRectPaint);
//            }
//            //绘制选择框四个角圆形
//            canvas.drawPath(mEditCirclePath, mEditCirclePaint);
//        }
    }

    private float defaultPoint = 1.5f;

    private int changeP = 0;//设置不同笔粗细的改变值，放在压力值上，做到ios，Android 互通

    public void setChangeP(int changeP) {
        this.changeP = changeP;
    }

    /**
     * 笔落下
     *
     * @param point
     * @param msgWhat
     */
    public void pen_start(EwritePoint point, int msgWhat) {
        mPath = new Path();
        int x, y;
        if (point.isUsedHttpOffset()) {
            x = EwritePointUtils.getXLocation(point.getPointsStr(), SCALE_X, basePoint, point.getXOffset());//X坐标
            y = EwritePointUtils.getYLocation(point.getPointsStr(), SCALE_Y, basePoint, point.getYOffset());//Y坐标
        } else {
            x = EwritePointUtils.getXLocation(point.getPointsStr(), SCALE_X, basePoint);//X坐标
            y = EwritePointUtils.getYLocation(point.getPointsStr(), SCALE_Y, basePoint);//Y坐标
        }
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
//        if (msgWhat == IS_PEN_WRITE) {
//            addPointList(point);
//            //记录写时点信息
//            editPoint = new ArrayList<>();
//            editPoint.add(point);
//        } else if (msgWhat == IS_EDIT_NOTE) {
//            addPointList(point);
//            //记录写时点信息
//            editPoint.add(point);
//        } else if (msgWhat == IS_LOOK) {
//            //记录写时点信息
//            editPoint = new ArrayList<>();
//            editPoint.add(point);
//        }
//        if (!defaultColor.equals(point.getColor())) {
//            mPaint.setColor(Color.parseColor("#" + point.getColor()));
//        } else {
//        mPaint.setColor(Color.parseColor(defaultColor));
//        }
//        int P = EwritePointUtils.getPenPressure(point.getPointsStr());
//        mP = P;
//        float paintSize = 0;
//        paintSize = (P - 75) / EwriteNoteBookScaleUtil.dip2px(context, defaultPoint);
//        if (paintSize < minSize) {
//            paintSize = minSize;
//        }
//        setmPaintWidth(paintSize);
//        LogUtils.i("AABBB", "P:" + P + "    " + F);
//        try {
//            mCanvas.drawPath(mPath, mPaint);
//        } catch (Exception e) {
//
//        }

        invalidate();
    }

    float minSize = 5f;

    float mP;

    /**
     * 笔移动
     *
     * @param point
     * @param msgWhat
     */
    public void pen_move(EwritePoint point, int msgWhat) {
//        if (mPath == null) {
//            StringBuilder last = new StringBuilder(point.getPointsStr());
//            last.replace(0, 1, "A");
//            point.setPointsStr(String.valueOf(last));
//            pen_start(point, msgWhat);
//        }

        int x, y;
        if (point.isUsedHttpOffset()) {
            x = EwritePointUtils.getXLocation(point.getPointsStr(), SCALE_X, basePoint, point.getXOffset());//X坐标
            y = EwritePointUtils.getYLocation(point.getPointsStr(), SCALE_Y, basePoint, point.getYOffset());//Y坐标
        } else {
            x = EwritePointUtils.getXLocation(point.getPointsStr(), SCALE_X, basePoint);//X坐标
            y = EwritePointUtils.getYLocation(point.getPointsStr(), SCALE_Y, basePoint);//Y坐标
        }

        int P = EwritePointUtils.getPenPressure(point.getPointsStr());

//        float dx = Math.abs(x - mX);
//        float dy = Math.abs(mY - y);

        float dx = x - mX;
        float dy = y - mY;

        float sd = (float) (Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)));
//        LogUtils.e("EwriteNoteBookScaleUtil", (P - 75) / EwriteNoteBookScaleUtil.dip2px(context, defaultPoint) + "  " + P + "   " + sd + "  " + EwriteNoteBookScaleUtil.dip2px(context, defaultPoint));
//        LogUtils.e("PPPPPP :   ", EwritePointUtils.getStrokes(point.getPointsStr()) + "    " + P + "  " + sd);
        int count = (int) sd;
        if (count < 4) {
            count = 1;
        }
        for (int i = 0; i < count; i++) {
//            xList.add(dx / 10 * i + mX);
//            yList.add(dy / 10 * i + mY);

            float paintSize = 0;
//            paintSize = (P - 75 + (mP - P) / count * i) / EwriteNoteBookScaleUtil.dip2px(context, defaultPoint) / sd;
//            if (paintSize < minSize) {
//                paintSize = minSize;
//            }
//            setmPaintWidth(paintSize);
//            if (mP - P == 5) {
//                mP = P;
//            }

//            float pChange = (mP - 90 - (mP - P) / count * i) / 20;
//
//            if (pChange < 0 || pChange == 0) {
//                pChange = 0.25f;
//            }

//            float timeChange = 15 - sd;
//            if (timeChange < 0) {
//                timeChange = 1;
//            }
            float pChange = (mP - (mP - P) / count * i + changeP) / 110;

            paintSize = (pChange) * EwriteNoteBookScaleUtil.dip2px(context, defaultPoint);
//            if (paintSize < minSize) {
//                paintSize = minSize;
//            }
            setDefaultColor(point.getColor());
            mPaint.setStrokeWidth(paintSize * basePoint);
//            mPath = new Path();
//            mPath.moveTo(dx / count * i + mX, dy / count * i + mY);
//            mPath.quadTo((dy / count * i + mX + mX) / 2, (dy / count * i + mY + mY) / 2, dx / count * i + mX, dy / count * i + mY);
//            mCanvas.drawPath(mPath, getNewPaint(paintSize * basePoint, Color.parseColor(point.getColor())));
//            LogUtils.e("paintSize :   ", paintSize + "    " + pChange + "    " + P + "    " + sd);
            mCanvas.drawCircle(dx / count * i + mX, dy / count * i + mY, paintSize * basePoint, mPaint);
//            LogUtils.e("paintSize :   ", paintSize + "    " + P);
        }
        mX = x;
        mY = y;
        mP = P;
        invalidate();
    }

//    /**
//     * 笔移动
//     *
//     * @param point
//     * @param msgWhat
//     */
//    public void pen_move(EwritePoint point, int msgWhat) {
////        if (mPath == null) {
////            StringBuilder last = new StringBuilder(point.getPointsStr());
////            last.replace(0, 1, "A");
////            point.setPointsStr(String.valueOf(last));
////            pen_start(point, msgWhat);
////        }
//
//        mPath = new Path();
//        mPath.moveTo(mX, mY);
//
//        int x = EwritePointUtils.getXLocation(point.getPointsStr(), SCALE_X, basePoint);//X坐标
//        int y = EwritePointUtils.getYLocation(point.getPointsStr(), SCALE_Y, basePoint);//Y坐标
//        int P = EwritePointUtils.getPenPressure(point.getPointsStr());
//
//
//        float dx = Math.abs(x - mX);
//        float dy = Math.abs(mY - y);
//
//        float sd = (float) (Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)));
//
//
//        mPath.quadTo((x + mX) / 2, (y + mY) / 2, x, y);
//        mPaint.setColor(Color.parseColor(defaultColor));
////        LogUtils.e("pen_move:  " + point.getPointsStr(), x + "  " + y + "   " + mX + "    " + mY);
//        float paintSize = 0;
//        paintSize = (P - 75) / EwriteNoteBookScaleUtil.dip2px(context, defaultPoint);
//        if (paintSize < EwriteNoteBookScaleUtil.dip2px(context, minSize)) {
//            paintSize = EwriteNoteBookScaleUtil.dip2px(context, minSize);
//        }
//
//        LogUtils.e("sd", sd + "   " + paintSize);
//        mCanvas.drawPath(mPath, getNewPaint(paintSize * basePoint, Color.parseColor(point.getColor())));
//
//
////        LogUtils.e("moveTo", dx + "    " + dy);
//
////        float size = 5;
////        if (!(dx >= size || dy >= size) && (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)) {
////            mPath.reset();
////            mPath.moveTo(x, y);
////        }
//
//        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//            // 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用mPath.lineTo也是可以的)
//            mPath.quadTo((x + mX) / 2, (y + mY) / 2, mX, mY);
////            mPath.cubicTo();
//            mX = x;
//            mY = y;
////            if (!defaultColor.equals(point.getColor())) {
////                mPaint.setColor(Color.parseColor("#" + point.getColor()));
////            } else {
//            mPaint.setColor(Color.parseColor(defaultColor));
////            }
////            int P = EwritePointUtils.getP(point.getPointsStr());
////            int F = EwritePointUtils.getPenStatues(point.getPointsStr());
//
//            paintSize = 0;
//
//            paintSize = (P - 75) / EwriteNoteBookScaleUtil.dip2px(context, defaultPoint) / sd;
//            if (paintSize < EwriteNoteBookScaleUtil.dip2px(context, minSize)) {
//                paintSize = EwriteNoteBookScaleUtil.dip2px(context, minSize);
//            }
//            mPaint.setTextSize(paintSize * basePoint);
//            mPaint.setStrokeWidth(paintSize * basePoint);
//
////            LogUtils.i("AABBB", "P:" + P + "    " + F);
//            mCanvas.drawPath(mPath, getNewPaint(paintSize * basePoint, Color.parseColor(point.getColor())));
////            mCanvas.drawPath(mPath, mPaint);
//        }
////        if (msgWhat == IS_PEN_WRITE) {
////            addPointList(point);
////            editPoint.add(point);
////        } else if (msgWhat == IS_EDIT_NOTE) {
////            addPointList(point);
////            editPoint.add(point);
////        } else if (msgWhat == IS_LOOK) {
////            editPoint.add(point);
////        }
//        invalidate();
//    }

    /**
     * 笔抬起
     *
     * @param point
     * @param msgWhat
     */
    public void pen_up(EwritePoint point, int msgWhat) {
        if (mPath == null) {
            return;
        }
        //将一条完整的点保存下来(相当于入栈操作)
        if (editPoint != null) {
            editPoint.add(point);
        }
        mPath.lineTo(mX, mY);
//        if (!defaultColor.equals(point.getColor())) {
//            mPaint.setColor(Color.parseColor("#" + point.getColor()));
//        } else {
        mPaint.setColor(Color.parseColor(defaultColor));
//        }l

        int P = EwritePointUtils.getPenPressure(point.getPointsStr());
        float paintSize = 0;
        paintSize = (P - 75) / EwriteNoteBookScaleUtil.dip2px(context, defaultPoint);
        if (paintSize < EwriteNoteBookScaleUtil.dip2px(context, minSize)) {
            paintSize = EwriteNoteBookScaleUtil.dip2px(context, minSize);
        }
        setmPaintWidth(paintSize);
//        LogUtils.i("AABBB", "P:" + P + "    " + F);
        try {
            mCanvas.drawPath(mPath, mPaint);
        } catch (Exception e) {

        }
        mPath = null;// 重新置空
        if (msgWhat == IS_PEN_WRITE) {
            addPointList(point);
            setSaveEditPointList(editPoint);
            loopPointToOldPointList(editPoint);
            stepTypeList.add(IS_PEN_WRITE_STEP);
            editPoint = null;
        } else if (msgWhat == IS_EDIT_NOTE) {
            addPointList(point);
        } else if (msgWhat == IS_LOOK) {
            stepTypeList.add(IS_LOOK_STEP);
            setSaveEditPointList(editPoint);
            editPoint = null;
        }
        invalidate();
    }


    //存储编辑点信息（用于撤销）
    private void setSaveEditPointList(List<EwritePoint> pointList) {
        if (saveEditPointList.size() > 30) {
            saveEditPointList.remove(0);
            saveEditPointList.add(pointList);
        } else {
            saveEditPointList.add(pointList);
        }
    }

    //获取最新编辑的点信息（用于撤销）
    private List<EwritePoint> getSaveEditPointList() {
        return saveEditPointList.get(saveEditPointList.size() - 1);
    }

    private List<EwritePoint> getSaveUndoEditPointList() {
        return saveUndoEditPointList.get(saveUndoEditPointList.size() - 1);
    }

    private void addPointList(EwritePoint point) {
        if (!mPointList.contains(point)) {
            mPointList.add(new EwritePoint(point.getColor(), point.getPointsStr()));
        }
    }

    /**
     * 删除存储的点
     *
     * @param x
     * @param y
     */
    private void removeEraserPointList(int x, int y) {
        for (int i = 0; i < mPointList.size(); i++) {
            EwritePoint point = mPointList.get(i);
            int xPoint = EwritePointUtils.getXLocation(point.getPointsStr(), SCALE_X, basePoint);//X坐标
            int yPoint = EwritePointUtils.getYLocation(point.getPointsStr(), SCALE_Y, basePoint);//Y坐标
            //判断橡皮擦擦除位置是否有字
            if (xPoint < x + 30 && xPoint > x - 30 && yPoint < y + 30 && yPoint > y - 30) {
                if (!eraserPointList.contains(point)) {
                    eraserPointList.add(point);
                }
            }
        }
    }


    /**
     * 当选择框移动时移除全部坐标包含的选择框坐标
     *
     * @param left
     * @param top
     * @param right
     * @param down
     */
    private void removeMoveRectPointsList(int left, int top, int right, int down) {
        mMoveRectPointList = new ArrayList<>();
        List<EwritePoint> preMoveList = new ArrayList<>();
        for (int i = 0; i < mPointList.size(); i++) {
            EwritePoint point = mPointList.get(i);
            int xPoint = EwritePointUtils.getXLocation(point.getPointsStr(), SCALE_X, basePoint);//X坐标
            int yPoint = EwritePointUtils.getYLocation(point.getPointsStr(), SCALE_Y, basePoint);//Y坐标
            if (xPoint < right && xPoint > left && yPoint < down && yPoint > top) {
                if (!mMoveRectPointList.contains(point)) {
                    mMoveRectPointList.add(new EwritePoint(point.getColor(), point.getPointsStr()));
                }
                preMoveList.add(new EwritePoint(point.getColor(), point.getPointsStr()));
            }
        }
        mPointList.removeAll(preMoveList);
        preMovePointList.add(preMoveList);
        loopPointToDelPointList(preMoveList);
        if (mMoveRectPointList.size() > 0) {
            paintDeletePointCanvas(left, top, right, down);
        }
    }

    //将删除的点位置的画布重绘(与橡皮擦重绘相同)
    private void paintDeletePointCanvas(int left, int top, int right, int down) {
        Paint deletePaint = new Paint();
        deletePaint.setAlpha(0);
        //这个属性是设置paint为橡皮擦重中之重
        //下面这句代码是橡皮擦设置的重点
        deletePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        deletePaint.setAntiAlias(true);
        deletePaint.setDither(true);
        deletePaint.setColor(getResources().getColor(R.color.transparent));
        deletePaint.setStyle(Paint.Style.FILL);
        deletePaint.setStrokeWidth(3);
        mPath = new Path();
        mPath.addRect(left, top, right, down, Path.Direction.CCW);
        mCanvas.drawPath(mPath, deletePaint);
        invalidate();
    }

    public void dealData(String pointStr, int V, int xOffset, int yOffset) {
        EwritePoint point = new EwritePoint(defaultColor, pointStr, xOffset, yOffset);
        if (V != 5) {
            invalidatePoint(point, IS_PEN_WRITE);
        }
    }

    public void dealData(String pointStr, int V, boolean isRed) {
        if (isRed) {
            defaultColor = "#F9543E";
        } else {
            defaultColor = "#333333";
        }
        dealData(pointStr, V);
    }

    //处理接收到的笔数据
    public void dealData(String pointStr, int V) {
//        if () {
        EwritePoint point = new EwritePoint(defaultColor, pointStr);
//        EwritePoint point = new EwritePoint(EwriteCommenUtil.colorInt2Hex(editColor), pointStr);
        if (V != 5) {
            invalidatePoint(point, IS_PEN_WRITE);
        }
//        } else {
//            //代表离线数据同步完成
//            if (data.endsWith("CAEA0200003BE6")) {
//                if (flashDataListener != null) {
//                    flashDataListener.getFlashDataFinish();
//                }
//            }
//        }

//        if (data.length() == 40 && data.startsWith("CA9")) {
//            try {
//                int V = Integer.parseInt(data.substring(1, 3), 16); //错误帧标识
//                data = data.substring(3);
//                String F = data.substring(0, 1);    //落笔书写抬笔标识
//                String B = data.substring(5, 7);    //笔画号
//                String P = data.substring(7, 9);    //压力值
//                String shubenhao = data.substring(9, 21);   //书本号
//                String YEMA = data.substring(21, 25);   //页码
//                String xStr = data.substring(25, 29);  //X坐标
//                String yStr = data.substring(29, 33);  //Y坐标
//                String pointStr = F + "_" + B + "_" + xStr + "_" + yStr + "_" + P;
//                EwritePoint point = new EwritePoint(EwriteCommenUtil.colorInt2Hex(editColor), pointStr);
//
////                LogUtils.e("dealData F :   ", F);
////                LogUtils.e("dealData B :   ", B);
////                LogUtils.e("dealData P :   ", P);
////                LogUtils.e("dealData xStr :   ", xStr);
////                LogUtils.e("dealData yStr :   ", yStr);
//                LogUtils.e("dealData pointStr :   ", pointStr);
//
//                LogUtils.e("dealDatasss 书本号:   ", shubenhao);
//                LogUtils.e("dealDatasss 页码:   ", YEMA);
//
//
//                //发送点的数据，错误帧不发送
//                if (V != 5) {
//                    invalidatePoint(point, IS_PEN_WRITE);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            //代表离线数据同步完成
//            if (data.endsWith("CAEA0200003BE6")) {
//                if (flashDataListener != null) {
//                    flashDataListener.getFlashDataFinish();
//                }
//            }
//        }
    }

    public void invalidatePoint(EwritePoint point, int msgWhat) {
        MeaasgeHander messageHandler = new MeaasgeHander(Looper.myLooper());
        Message message = Message.obtain();
        message.what = msgWhat;
        message.obj = point;
        messageHandler.sendMessage(message);
    }

    //绘制编辑的点（移动、粘贴、改变颜色）
    private void invalidateEditPoint() {
        MeaasgeHander messageHandler = new MeaasgeHander(Looper.myLooper());
        for (int i = 0; i < mMoveRectPointList.size(); i++) {
            Message message = Message.obtain();
            if (i == mMoveRectPointList.size() - 1) {
                message.arg1 = -1;
            }
            //绘制编辑的点时，是增加点坐标，所以同比写操作
            message.what = IS_EDIT_NOTE;
            message.obj = mMoveRectPointList.get(i);
            messageHandler.sendMessage(message);
        }
    }

    //重新绘制全部点
    private void invalidateAllPoint() {
        mBitmap = Bitmap.createBitmap(EwriteScreenUtil.getScreenWidth(context), EwriteScreenUtil.getScreenHeight(context), Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布
        if (mPointList != null && mPointList.size() > 0) {
            MeaasgeHander messageHandler = new MeaasgeHander(Looper.myLooper());
            for (int i = 0; i < mPointList.size(); i++) {
                Message message = Message.obtain();
                if (i == mPointList.size() - 1) {
                    message.arg1 = -1;
                }
                message.what = -1;
                message.obj = mPointList.get(i);
                messageHandler.sendMessage(message);
            }
        } else {
            clearmBitmap();
        }

    }

    /**
     * 当选择框改变颜色时移除全部坐标包含的选择框坐标
     *
     * @param left
     * @param top
     * @param right
     * @param down
     */
    private void removeMoveRectPointsList(int left, int top, int right, int down, int editColor) {
        mMoveRectPointList = new ArrayList<>();
        List<EwritePoint> changeColorPointList = new ArrayList<>();
        for (int i = 0; i < mPointList.size(); i++) {
            EwritePoint point = mPointList.get(i);
            int xPoint = EwritePointUtils.getXLocation(point.getPointsStr(), SCALE_X, basePoint);//X坐标
            int yPoint = EwritePointUtils.getYLocation(point.getPointsStr(), SCALE_Y, basePoint);//Y坐标
            if (xPoint < right && xPoint > left && yPoint < down && yPoint > top) {
                if (!mMoveRectPointList.contains(point)) {
                    mMoveRectPointList.add(new EwritePoint(EwriteCommenUtil.colorInt2Hex(editColor), point.getPointsStr()));
                }
                changeColorPointList.add(new EwritePoint(point.getColor(), point.getPointsStr()));
            }
        }
        mPointList.removeAll(changeColorPointList);
        //保存颜色改变之前的点
        preChangeColorPointList.add(changeColorPointList);
        if (mMoveRectPointList.size() > 0) {
            paintDeletePointCanvas(left, top, right, down);
        }
    }

    /**
     * 清空选择框
     *
     * @param left
     * @param top
     * @param right
     * @param down
     */
    private void clearRectCanvas(int left, int top, int right, int down) {
        mCanvas.save();
        mCanvas.clipRect(left, top, right, down);
        mCanvas.drawColor(Color.WHITE);
        mCanvas.restore();
    }

    /**
     * 选择框移动时改变选择框中的点坐标
     */
    private void moveRectPointList(float xM, float yM) {
        if (mMoveRectPointList.size() > 0) {
            for (int i = 0; i < mMoveRectPointList.size(); i++) {
                EwritePoint point = mMoveRectPointList.get(i);
                int x = EwritePointUtils.getBeforeX(point.getPointsStr()) + (int) (xM * SCALE_X);//X坐标
                int y = EwritePointUtils.getBeforeY(point.getPointsStr()) + (int) (yM * SCALE_Y);//Y坐标
                String xStr = "";
                String yStr = "";
                if (x < 0) {
                    xStr = "0000";
                } else if (x < 16) {
                    //一位数
                    xStr = "000" + Integer.toHexString(x);
                } else if (x < 256) {
                    //两位数
                    xStr = "00" + Integer.toHexString(x);
                } else if (x < 4096) {
                    //三位数
                    xStr = "0" + Integer.toHexString(x);
                } else {
                    xStr = Integer.toHexString(x);
                }
                if (y < 0) {
                    yStr = "0000";
                } else if (y < 16) {
                    yStr = "000" + Integer.toHexString(y);
                } else if (y < 256) {
                    yStr = "00" + Integer.toHexString(y);
                } else if (y < 4096) {
                    yStr = "0" + Integer.toHexString(y);
                } else {
                    yStr = Integer.toHexString(y);
                }
                //更改x、y坐标点
                StringBuilder replaceX = new StringBuilder(point.getPointsStr());
                replaceX.replace(7, 9, xStr.substring(0, 2));
                replaceX.replace(5, 7, xStr.substring(2, 4));
                point.setPointsStr(String.valueOf(replaceX));
                StringBuilder replaceY = new StringBuilder(point.getPointsStr());
                replaceY.replace(12, 14, yStr.substring(0, 2));
                replaceY.replace(10, 12, yStr.substring(2, 4));
                point.setPointsStr(String.valueOf(replaceY));
            }
        }
    }

    /**
     * 粘贴时，计算点放置位置
     */
    private void moveRectPointList(float xM, float yM, float centerX, float centerY) {
        if (mMoveRectPointList.size() > 0) {
            int pasteX = (int) (xM * SCALE_X - centerX * SCALE_X);//X坐标
            int pasteY = (int) (yM * SCALE_Y - centerY * SCALE_Y);//Y坐标
            for (int i = 0; i < mMoveRectPointList.size(); i++) {
                EwritePoint point = mMoveRectPointList.get(i);
                int x = EwritePointUtils.getBeforeX(point.getPointsStr()) + pasteX;//X坐标
                int y = EwritePointUtils.getBeforeY(point.getPointsStr()) + pasteY;//Y坐标
                String xStr = "";
                String yStr = "";
                if (x < 0) {
                    xStr = "0000";
                } else if (x < 16) {
                    //一位数
                    xStr = "000" + Integer.toHexString(x);
                } else if (x < 256) {
                    //两位数
                    xStr = "00" + Integer.toHexString(x);
                } else if (x < 4096) {
                    //三位数
                    xStr = "0" + Integer.toHexString(x);
                } else {
                    xStr = Integer.toHexString(x);
                }
                if (y < 0) {
                    yStr = "0000";
                } else if (y < 16) {
                    yStr = "000" + Integer.toHexString(y);
                } else if (y < 256) {
                    yStr = "00" + Integer.toHexString(y);
                } else if (y < 4096) {
                    yStr = "0" + Integer.toHexString(y);
                } else {
                    yStr = Integer.toHexString(y);
                }
                //更改x、y坐标点
                StringBuilder replaceX = new StringBuilder(point.getPointsStr());
                replaceX.replace(7, 9, xStr.substring(0, 2));
                replaceX.replace(5, 7, xStr.substring(2, 4));
                point.setPointsStr(String.valueOf(replaceX));
                StringBuilder replaceY = new StringBuilder(point.getPointsStr());
                replaceY.replace(12, 14, yStr.substring(0, 2));
                replaceY.replace(10, 12, yStr.substring(2, 4));
                point.setPointsStr(String.valueOf(replaceY));
            }
        }
    }

    /**
     * 将改变后的点添加到总坐标中
     */
    private void addMoveRectPointsToMPoints() {
        if (mMoveRectPointList != null && mMoveRectPointList.size() > 0) {
            mPointList.addAll(mMoveRectPointList);
            mMoveRectPointList.clear();
            mMoveRectPointList = null;
        }
    }

    //将操作的点加入到追加点集合中
    private void loopPointToOldPointList(List<EwritePoint> changingPointList) {
        if (changingPointList != null && changingPointList.size() > 0) {
            for (int j = 0; j < changingPointList.size(); j++) {
                if (!oldPointList.contains(changingPointList.get(j))) {
                    oldPointList.add(changingPointList.get(j));
                }
            }
        }
    }

    private void loopRedoPointToOldPointList(List<EwritePoint> changingPointList) {
        if (changingPointList != null && changingPointList.size() > 0) {
            for (int j = 0; j < changingPointList.size(); j++) {
                if (oldPointList.contains(changingPointList.get(j))) {
                    oldPointList.remove(changingPointList.get(j));
                }
            }
        }
    }

    //将删除点加入到删除集合中
    private void loopPointToDelPointList(List<EwritePoint> changingPointList) {
        if (changingPointList != null && changingPointList.size() > 0) {
            for (int j = 0; j < changingPointList.size(); j++) {
                if (!delPointList.contains(changingPointList.get(j))) {
                    delPointList.add(changingPointList.get(j));
                }
            }
        }
    }


    //将撤回的点从删除点集合中移除
    private void loopRedoPointToDelPointList(List<EwritePoint> redoPointList) {
        if (redoPointList != null && redoPointList.size() > 0) {
            for (int i = 0; i < redoPointList.size(); i++) {
                if (delPointList.contains(redoPointList.get(i))) {
                    delPointList.remove(redoPointList.get(i));
                }
            }
        }
    }

    private String last = "-1";

    public class MeaasgeHander extends Handler {
        public MeaasgeHander(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int type = msg.what;
            EwritePoint point = (EwritePoint) msg.obj;
            String numberlast = point.getPointsStr();
            if (numberlast != null) {
                int F = EwritePointUtils.getPenStatues(numberlast);//落笔书写抬笔标识
                String B = EwritePointUtils.getStrokes(numberlast);//笔画号

//                int P = EwritePointUtils.getPenPressure(numberlast);
//                setmPaintWidth(P / 10f);
//                LogUtils.i("AAvvvv", "P:" + P + "    " + F);

                //解决笔画漂移问题，当笔画号和上
                // 一笔不一致，则认为是落笔帧
                if (!last.equals(B)) {
                    F = 10;
                }
                last = B;
                switch (F) {
                    case 10://落笔
                        //每一次记录的路径对象是不一样的
                        pen_start(point, type);
                        break;
                    case 11://绘制
                        pen_move(point, type);
                        break;
                    case 12://抬笔
                        pen_up(point, type);
                        break;
                    default:
                        mPath = null;// 重新置空
                        editColor = Color.parseColor(defaultColor);
                        break;
                }
            }
            if (msg.arg1 == -1) {
                last = "-1";
                clearClipBitmap();
//                clearmBitmap();
                //如果是移动、粘贴、改变颜色操作，添加全部点坐标进保存列表中（就不一笔一笔添加了）
                if (type == IS_EDIT_NOTE) {
                    setSaveEditPointList(editPoint);
                    editPoint = null;
                }
            }
        }
    }

    /**
     * 撤销的核心思想就是将画布清空，
     * 将保存下来的Path路径最后一个移除掉，
     * 重新将路径画在画布上面。
     */
    public void undo() {
        if (saveEditPointList != null && saveEditPointList.size() > 0) {
            //判断当前撤销的是哪个步骤
            switch (stepTypeList.get(stepTypeList.size() - 1)) {
                case IS_PEN_WRITE_STEP:
                    stepUndoTypeList.add(IS_PEN_WRITE_STEP);
                    allPointRemoveUndoPoint(getSaveEditPointList());
                    loopPointToDelPointList(getSaveEditPointList());
                    break;
                case IS_LOOK_STEP:
                    stepUndoTypeList.add(IS_LOOK_STEP);
                    allPointRemoveUndoPoint(getSaveEditPointList());
                    loopPointToDelPointList(getSaveEditPointList());
                    break;
                case MOVE_STEP:
                    //撤销移动，将移动前的坐标从删除集合中移动，将移动后的坐标从追加集合中移除（因为移动时将移动前坐标添加到了删除集合中，将移动后的坐标添加到了追加集合中）
                    stepUndoTypeList.add(MOVE_STEP);
                    //撤销移动的点，将移动之前的坐标点添加到全部点列表中
                    for (int i = 0; i < preMovePointList.get(preMovePointList.size() - 1).size(); i++) {
                        addPointList(preMovePointList.get(preMovePointList.size() - 1).get(i));
                    }
                    allPointRemoveUndoPoint(getSaveEditPointList());
                    //将移动后的坐标从追加集合中移除
                    loopRedoPointToOldPointList(getSaveEditPointList());
                    //将移动前的坐标添加
                    loopRedoPointToDelPointList(preMovePointList.get(preMovePointList.size() - 1));
                    preUndoMovePointList.add(preMovePointList.get(preMovePointList.size() - 1));
                    preMovePointList.remove(preMovePointList.size() - 1);
                    break;
                case PASTE_STEP:
                    stepUndoTypeList.add(PASTE_STEP);
                    allPointRemoveUndoPoint(getSaveEditPointList());
                    loopPointToDelPointList(getSaveEditPointList());
                    break;
                case COLOR_STEP:
                    stepUndoTypeList.add(COLOR_STEP);
                    //撤销改变颜色的点，将移动之前的坐标点添加到全部点列表中
                    for (int i = 0; i < preChangeColorPointList.get(preChangeColorPointList.size() - 1).size(); i++) {
                        addPointList(preChangeColorPointList.get(preChangeColorPointList.size() - 1).get(i));
                    }
                    allPointRemoveUndoPoint(getSaveEditPointList());
                    preUndoChangeColorPointList.add(preChangeColorPointList.get(preChangeColorPointList.size() - 1));
                    preChangeColorPointList.remove(preChangeColorPointList.size() - 1);
                    loopPointToDelPointList(getSaveEditPointList());
                    break;
                case ERASER_STEP:
                    stepUndoTypeList.add(ERASER_STEP);
                    for (int i = 0; i < preEraserPointList.get(preEraserPointList.size() - 1).size(); i++) {
                        addPointList(preEraserPointList.get(preEraserPointList.size() - 1).get(i));
                    }
                    preUndoEraserPointList.add(preEraserPointList.get(preEraserPointList.size() - 1));
                    preEraserPointList.remove(preEraserPointList.size() - 1);
                    loopRedoPointToDelPointList(getSaveEditPointList());
                    break;
                case DELETE_STEP:
                    stepUndoTypeList.add(DELETE_STEP);
                    for (int i = 0; i < preDeletePointList.get(preDeletePointList.size() - 1).size(); i++) {
                        addPointList(preDeletePointList.get(preDeletePointList.size() - 1).get(i));
                    }
                    preUndoDeletePointList.add(preDeletePointList.get(preDeletePointList.size() - 1));
                    preDeletePointList.remove(preDeletePointList.size() - 1);
                    loopRedoPointToDelPointList(getSaveEditPointList());
                    break;
                default:
                    break;
            }
            stepUndoListAdd();
            stepListRemove();
            invalidateAllPoint();
        }
    }

    //从全部坐标点中移除撤销的点
    private void allPointRemoveUndoPoint(List<EwritePoint> editPointList) {
        if (editPointList != null && editPointList.size() > 0) {
            for (int i = 0; i < editPointList.size(); i++) {
                if (mPointList.contains(editPointList.get(i))) {
                    mPointList.remove(editPointList.get(i));
                }
            }
        }
    }

    //移除操作记录
    private void stepListRemove() {
        saveEditPointList.remove(saveEditPointList.size() - 1);
        stepTypeList.remove(stepTypeList.size() - 1);
    }

    //添加撤销记录
    private void stepUndoListAdd() {
        saveUndoEditPointList.add(getSaveEditPointList());
    }

    /**
     * 重做的核心思想就是将撤销的路径保存到另外一个集合里面(栈)，
     * 然后从redo的集合里面取出最顶端对象，
     * 画在画布上面即可。
     */
    public void redo() {
        if (saveUndoEditPointList != null && saveUndoEditPointList.size() > 0) {
            switch (stepUndoTypeList.get(stepUndoTypeList.size() - 1)) {
                case IS_PEN_WRITE_STEP:
                    allPointAddUndoPoint(getSaveUndoEditPointList());
                    stepTypeList.add(IS_PEN_WRITE_STEP);
                    setSaveEditPointList(getSaveUndoEditPointList());
                    loopRedoPointToDelPointList(getSaveUndoEditPointList());
                    break;
                case IS_LOOK_STEP:
                    allPointAddUndoPoint(getSaveUndoEditPointList());
                    stepTypeList.add(IS_LOOK_STEP);
                    setSaveEditPointList(getSaveUndoEditPointList());
                    loopRedoPointToDelPointList(getSaveUndoEditPointList());
                    break;
                case MOVE_STEP:
                    allPointRemoveUndoPoint(preUndoMovePointList.get(preUndoMovePointList.size() - 1));
                    allPointAddUndoPoint(getSaveUndoEditPointList());
                    stepTypeList.add(MOVE_STEP);
                    setSaveEditPointList(getSaveUndoEditPointList());
                    loopPointToDelPointList(preUndoMovePointList.get(preUndoMovePointList.size() - 1));
                    loopPointToOldPointList(getSaveUndoEditPointList());
                    preMovePointList.add(preUndoMovePointList.get(preUndoMovePointList.size() - 1));
                    preUndoMovePointList.remove(preUndoMovePointList.size() - 1);
//                    loopRedoPointToDelPointList(getSaveUndoEditPointList());
                    break;
                case PASTE_STEP:
                    allPointAddUndoPoint(getSaveUndoEditPointList());
                    stepTypeList.add(PASTE_STEP);
                    setSaveEditPointList(getSaveUndoEditPointList());
                    loopRedoPointToDelPointList(getSaveUndoEditPointList());
                    break;
                case COLOR_STEP:
                    allPointAddUndoPoint(getSaveUndoEditPointList());
                    stepTypeList.add(COLOR_STEP);
                    setSaveEditPointList(getSaveUndoEditPointList());
                    preChangeColorPointList.add(preUndoChangeColorPointList.get(preUndoChangeColorPointList.size() - 1));
                    preUndoChangeColorPointList.remove(preUndoChangeColorPointList.size() - 1);
                    loopRedoPointToDelPointList(getSaveUndoEditPointList());
                    break;
                case ERASER_STEP:
                    stepTypeList.add(ERASER_STEP);
                    setSaveEditPointList(getSaveUndoEditPointList());
                    for (int i = 0; i < preUndoEraserPointList.get(preUndoEraserPointList.size() - 1).size(); i++) {
                        if (mPointList.contains(preUndoEraserPointList.get(preUndoEraserPointList.size() - 1).get(i))) {
                            mPointList.remove(preUndoEraserPointList.get(preUndoEraserPointList.size() - 1).get(i));
                        }
                    }
                    loopPointToDelPointList(getSaveUndoEditPointList());
                    preEraserPointList.add(preUndoEraserPointList.get(preUndoEraserPointList.size() - 1));
                    preUndoEraserPointList.remove(preUndoEraserPointList.size() - 1);
                    break;
                case DELETE_STEP:
                    stepTypeList.add(DELETE_STEP);
                    setSaveEditPointList(getSaveUndoEditPointList());
                    for (int i = 0; i < preUndoDeletePointList.get(preUndoDeletePointList.size() - 1).size(); i++) {
                        if (mPointList.contains(preUndoDeletePointList.get(preUndoDeletePointList.size() - 1).get(i))) {
                            mPointList.remove(preUndoDeletePointList.get(preUndoDeletePointList.size() - 1).get(i));
                        }
                    }
                    loopPointToDelPointList(getSaveUndoEditPointList());
                    preDeletePointList.add(preUndoDeletePointList.get(preUndoDeletePointList.size() - 1));
                    preUndoDeletePointList.remove(preUndoDeletePointList.size() - 1);
                    break;
                default:
                    break;
            }
            saveUndoEditPointList.remove(saveUndoEditPointList.size() - 1);
            stepUndoTypeList.remove(stepUndoTypeList.size() - 1);
            invalidateAllPoint();
        }
    }

    private void allPointAddUndoPoint(List<EwritePoint> editPointList) {
        if (editPointList != null && editPointList.size() > 0) {
            for (int i = 0; i < editPointList.size(); i++) {
                if (!mPointList.contains(editPointList.get(i))) {
                    mPointList.add(editPointList.get(i));
                }
            }
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case ACTION_DOWN:
//                if (clickDownListener != null) {
//                    clickDownListener.clickDown();
//                }
//
//                startEditPointX = event.getX();
//                startEditPointY = event.getY();
//                slideStartPointX = event.getX();
//                slideStartPointY = event.getY();
//                downTime = System.currentTimeMillis();
//                if (isEraser) {
//                    touch_start(startEditPointX, startEditPointY);
//                }
//                if (!canEdit) {
//                    return true;
//                }
//                if (isEdit) {
//                    if (startEditPointX > leftP + circleRadius
//                            && startEditPointX < rightP - circleRadius
//                            && startEditPointY > topP + circleRadius
//                            && startEditPointY < endP - circleRadius) {
//                        //点击位置在矩形中间1/4位置，表示可以移动
//                        isRectMoved = true;
//                        initClip(EwriteCommenUtil.getClipBitmap(mBitmap, leftP, topP, rightP, endP));
//                        moveRectMatrix = new Matrix();
//                        moveRectMatrix.postTranslate(leftP, topP);
//                        removeMoveRectPointsList((int) leftP, (int) topP, (int) rightP, (int) endP);
//                    } else {
//                        isRectMoved = false;
//                    }
//                    editCanMoveCircle(0);
//                    cancleEdit();
//                }
//                break;
//            case ACTION_MOVE:
//                moveEditPointX = event.getX();
//                moveEditPointY = event.getY();
//                moveTime = System.currentTimeMillis();
//                mEditCirclePath.reset();
//                if (!canEdit && !isEraser && EwriteCommenUtil.isLongPressed(startEditPointX, startEditPointY, moveEditPointX,
//                        moveEditPointY, downTime, moveTime, 800)) {
//                    createNoteEditDialog.showEditPopupWindow(startEditPointX, startEditPointY, true);
//                    return true;
//                }
//                if (isEraser) {
//                    touch_move(moveEditPointX, moveEditPointY);
//                }
//                if (!canEdit) {
//                    return true;
//                }
//                //编辑dialog显示时，或者长按时不能拖动
//                if (isRectMoved) {
//                    //得到手指移动距离
//                    slideX = moveEditPointX - startEditPointX;
//                    slideY = moveEditPointY - startEditPointY;
//
//                    slideMovePointX = moveEditPointX - slideStartPointX;
//                    slideMovePointY = moveEditPointY - slideStartPointY;
//                    slideStartPointX = event.getX();
//                    slideStartPointY = event.getY();
//
//                    moveLeft = drawRectLeft + slideX;
//                    moveTop = drawRectTop + slideY;
//                    moveRight = drawRectRight + slideX;
//                    moveDown = drawRectDown + slideY;
//
//                    if (moveLeft - circleRadius < 0 || moveRight + circleRadius > getMeasuredWidth()) {
////                        moveLeft = drawRectLeft - slideX;
////                        moveRight = drawRectRight - slideX;
////                        return true;
//                    }
//                    if (moveTop - circleRadius < 0 || moveDown + circleRadius > getMeasuredHeight()) {
////                        moveTop = drawRectTop - slideY;
////                        moveDown = drawRectDown - slideY;
////                        return true;
//                    }
//                    addMoveCircle();
//                    moveRectPointList(slideMovePointX, slideMovePointY);
//                    moveRectMatrix.postTranslate(slideMovePointX, slideMovePointY);
//                } else {
//                    //判断往哪个方向画的矩形(起点和移动方向对比)
//                    if (startEditPointX < moveEditPointX) {
//                        if (startEditPointY < moveEditPointY) {
//                            //5点钟方向
//                            if (isEdit) {
//                                editCanMoveCircle(1);
//                            } else {
//                                addCircle();
//                                setRectValue(startEditPointX, startEditPointY, moveEditPointX, moveEditPointY);
//                                isEdit = false;
//                                isRectMoved = false;
//                            }
//                        } else {
//                            //2点钟方向
//                            if (isEdit) {
//                                editCanMoveCircle(2);
//                            } else {
//                                addCircle();
//                                setRectValue(startEditPointX, moveEditPointY, moveEditPointX, startEditPointY);
//                                isEdit = false;
//                                isRectMoved = false;
//                            }
//                        }
//                    } else {
//                        if (startEditPointY < moveEditPointY) {
//                            //7点钟方向
//                            if (isEdit) {
//                                editCanMoveCircle(3);
//                            } else {
//                                addCircle();
//                                setRectValue(moveEditPointX, startEditPointY, startEditPointX, moveEditPointY);
//                                isEdit = false;
//                                isRectMoved = false;
//                            }
//                        } else {
//                            //10点钟方向
//                            if (isEdit) {
//                                editCanMoveCircle(4);
//                            } else {
//                                addCircle();
//                                setRectValue(moveEditPointX, moveEditPointY, startEditPointX, startEditPointY);
//                                isEdit = false;
//                                isRectMoved = false;
//                            }
//                        }
//                    }
//                }
//                invalidate();
//                break;
//            case ACTION_CANCEL:
//            case ACTION_UP:
//                if (isEraser) {
//                    touch_up();
//                }
//                if (!canEdit) {
//                    return true;
//                }
//
//                isEdit = true;
//                if (isRectMoved) {
//                    //如果编辑框移动了，四个边框的坐标为移动后的坐标
//                    leftP = moveLeft;
//                    topP = moveTop;
//                    rightP = moveRight;
//                    endP = moveDown;
//
//                    setRectValue(moveLeft, moveTop, moveRight, moveDown);
//                } else {
//                    leftP = drawRectLeft;
//                    topP = drawRectTop;
//                    rightP = drawRectRight;
//                    endP = drawRectDown;
//                }
//                //如果移动了点，重新绘制全部点，添加移动的点到全部点中
//                if (mMoveRectPointList != null && mMoveRectPointList.size() > 0) {
//                    loopPointToOldPointList(mMoveRectPointList);
//                    //移动
//                    editPoint = mMoveRectPointList;
//                    stepTypeList.add(MOVE_STEP);
//                    invalidateEditPoint();
//                    addMoveRectPointsToMPoints();
//                }
//                changeSpot();
//                int rectWidth = (int) Math.abs(rightP - leftP);     //矩形宽度
//                int rectHeight = (int) Math.abs(endP - topP);       //矩形高度
//                createNoteEditDialog.showEditPopupWindow(leftP + rectWidth / 2, topP + EwriteCommenUtil.dip2px(getContext(), 50), false);
//                invalidate();
//                break;
//        }
//        return true;
//    }

    //橡皮擦操作开始
    private void touch_start(float x, float y) {
        eraserPointList = new ArrayList<>();
        mPath = new Path();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        mCanvas.drawPath(mPath, mEraserPaint);
        //记录写时路径信息
        removeEraserPointList((int) x, (int) y);
        invalidate();
    }

    //橡皮擦操作移动
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(mY - y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
            mCanvas.drawPath(mPath, mEraserPaint);
            removeEraserPointList((int) x, (int) y);
        }
        invalidate();
    }

    //橡皮擦操作抬起
    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mEraserPaint);
        //将一条完整的路径保存下来(相当于入栈操作)
        preEraserPointList.add(eraserPointList);
        setSaveEditPointList(eraserPointList);
        loopPointToDelPointList(eraserPointList);
        mPointList.removeAll(eraserPointList);
        stepTypeList.add(ERASER_STEP);
        invalidate();
    }

    /**
     * 替换坐标，左右小的为left，上下小的为top
     */
    private void changeSpot() {
        if (leftP > rightP) {
            float change = leftP;
            leftP = rightP;
            rightP = change;
        }
        if (topP > endP) {
            float change = topP;
            topP = endP;
            endP = change;
        }
    }

    /**
     * 判断在已经画了矩形的状态下，按下是否在四个圆内
     *
     * @param type
     */
    private void editCanMoveCircle(int type) {
        if (canMoveEditCircle(leftP, topP, startEditPointX, startEditPointY)) {
            setRectValue(moveEditPointX, moveEditPointY, rightP, endP);
            mEditCirclePath.addCircle(moveEditPointX, moveEditPointY, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(moveEditPointX, endP, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(rightP, moveEditPointY, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(rightP, endP, circleRadius, Path.Direction.CCW);
        } else if (canMoveEditCircle(leftP, endP, startEditPointX, startEditPointY)) {
            setRectValue(moveEditPointX, topP, rightP, moveEditPointY);
            mEditCirclePath.addCircle(moveEditPointX, topP, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(moveEditPointX, moveEditPointY, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(rightP, topP, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(rightP, moveEditPointY, circleRadius, Path.Direction.CCW);
        } else if (canMoveEditCircle(rightP, topP, startEditPointX, startEditPointY)) {
            setRectValue(leftP, moveEditPointY, moveEditPointX, endP);
            mEditCirclePath.addCircle(leftP, moveEditPointY, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(leftP, endP, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(moveEditPointX, moveEditPointY, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(moveEditPointX, endP, circleRadius, Path.Direction.CCW);
        } else if (canMoveEditCircle(rightP, endP, startEditPointX, startEditPointY)) {
            setRectValue(leftP, topP, moveEditPointX, moveEditPointY);
            mEditCirclePath.addCircle(leftP, topP, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(leftP, moveEditPointY, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(moveEditPointX, topP, circleRadius, Path.Direction.CCW);
            mEditCirclePath.addCircle(moveEditPointX, moveEditPointY, circleRadius, Path.Direction.CCW);
        } else {
            switch (type) {
                case 1:
                    addCircle();
                    setRectValue(startEditPointX, startEditPointY, moveEditPointX, moveEditPointY);
                    break;
                case 2:
                    addCircle();
                    setRectValue(startEditPointX, moveEditPointY, moveEditPointX, startEditPointY);
                    break;
                case 3:
                    addCircle();
                    setRectValue(moveEditPointX, startEditPointY, startEditPointX, moveEditPointY);
                    break;
                case 4:
                    addCircle();
                    setRectValue(moveEditPointX, moveEditPointY, startEditPointX, startEditPointY);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 添加圆
     */
    private void addCircle() {
        mEditCirclePath.addCircle(startEditPointX, startEditPointY, circleRadius, Path.Direction.CCW);
        mEditCirclePath.addCircle(startEditPointX, moveEditPointY, circleRadius, Path.Direction.CCW);
        mEditCirclePath.addCircle(moveEditPointX, startEditPointY, circleRadius, Path.Direction.CCW);
        mEditCirclePath.addCircle(moveEditPointX, moveEditPointY, circleRadius, Path.Direction.CCW);
    }

    /**
     * 添加移动中的圆
     */
    private void addMoveCircle() {
        mEditCirclePath.addCircle(leftP + slideX, topP + slideY, circleRadius, Path.Direction.CCW);
        mEditCirclePath.addCircle(leftP + slideX, endP + slideY, circleRadius, Path.Direction.CCW);
        mEditCirclePath.addCircle(rightP + slideX, topP + slideY, circleRadius, Path.Direction.CCW);
        mEditCirclePath.addCircle(rightP + slideX, endP + slideY, circleRadius, Path.Direction.CCW);
    }

    /**
     * 设置矩形坐标
     *
     * @param left
     * @param top
     * @param right
     * @param down
     */
    private void setRectValue(float left, float top, float right, float down) {
        drawRectLeft = left;
        drawRectTop = top;
        drawRectRight = right;
        drawRectDown = down;
    }

    /**
     * 判断点击是否在圆内，圆内可以移动，否则不可以
     *
     * @param circleCenterX
     * @param circleCenterY
     * @param downX
     * @param downY
     * @return
     */
    private boolean canMoveEditCircle(float circleCenterX, float circleCenterY, float downX,
                                      float downY) {
        int distanceX = (int) Math.abs(circleCenterX - downX);
        int distanceY = (int) Math.abs(circleCenterY - downY);
        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        if (distanceZ < circleRadius) {
            isCircleMove = true;
            return true;
        } else {
            isCircleMove = false;
            return false;
        }
    }

    /**
     * 点击编辑框外面，编辑框消失
     */
    public void cancleEdit() {
        if (!isRectMoved && canEdit && !isCircleMove) {
            isEdit = false;
            isCircleMove = false;
            clearEdit();
        }
    }

    /**
     * 清除编辑框
     */
    public void clearEdit() {
        if (editFinishListener != null) {
            editFinishListener.editFinish();
        }
        if (isEraser) {
            isEdit = false;
            isCircleMove = false;
            isRectMoved = false;
        }
        canEdit = false;
        mEditCirclePath.reset();
        mEditCirclePath.addCircle(0, 0, 0, Path.Direction.CCW);
        drawRectLeft = 0;
        drawRectTop = 0;
        drawRectRight = 0;
        drawRectDown = 0;
        moveLeft = 0;
        moveTop = 0;
        moveRight = 0;
        moveDown = 0;
        leftP = 0;
        topP = 0;
        rightP = 0;
        endP = 0;
        invalidate();
    }

    public void clearmPointsList() {
        if (mPointList != null && mPointList.size() > 0) {
            mPointList.clear();
        }
    }

    public void clearmBitmap() {
        if (mBitmap != null) {
            mBitmap = null;
            ensureSignatureBitmap();
        }

        invalidate();
    }

    public void clearClipBitmap() {
        if (clipBitmap != null) {
            clipBitmap = null;
            ensureClipBitmap();
        }

        invalidate();
    }

    private void ensureSignatureBitmap() {
        if (mBitmap == null) {
            int a = getWidth();
            int b = getHeight();
            mBitmap = Bitmap.createBitmap(EwriteScreenUtil.getScreenWidth(context), EwriteScreenUtil.getScreenHeight(context), Bitmap.Config.ARGB_8888);
//            mBitmap = Bitmap.createBitmap(a, b, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
    }

    private void ensureClipBitmap() {
        if (clipBitmap == null) {
            int a = getWidth();
            int b = getHeight();
            clipBitmap = Bitmap.createBitmap(EwriteScreenUtil.getScreenWidth(context), EwriteScreenUtil.getScreenHeight(context), Bitmap.Config.ARGB_8888);
//            mBitmap = Bitmap.createBitmap(a, b, Bitmap.Config.ARGB_8888);
            clipCanvas = new Canvas(clipBitmap);
        }
    }


    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        RectF tempSrc = new RectF();
        RectF tempDst = new RectF();

        int dWidth = bitmap.getWidth();
        int dHeight = bitmap.getHeight();
        int vWidth = getWidth();
        int vHeight = getHeight();

        // Generate the required transform.
        tempSrc.set(0, 0, dWidth, dHeight);
        tempDst.set(0, 0, vWidth, vHeight);

        drawMatrix = new Matrix();
        drawMatrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER);

        invalidate();
    }

    public ArrayList<EwritePoint> getmMoveRectPointList() {
        return mMoveRectPointList;
    }

    public void setmMoveRectPointList(ArrayList<EwritePoint> mMoveRectPointList) {
        this.mMoveRectPointList = mMoveRectPointList;
    }

    public ArrayList<EwritePoint> getmPointList() {
        return mPointList;
    }

    public void setmPointList(ArrayList<EwritePoint> mPointList) {
        this.mPointList = mPointList;
    }

    public ArrayList<EwritePoint> getOldPointList() {
        return oldPointList;
    }

    public ArrayList<EwritePoint> getDelPointList() {
        return delPointList;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
        isEraser = false;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    //选择框编辑结束回调
    public interface EditFinishListener {
        void editFinish();
    }

    public void setEditFinishListener(EditFinishListener editFinishListener) {
        this.editFinishListener = editFinishListener;
    }

    //将View转换为bitmap
    public Bitmap createViewToBitmap() {
        this.buildDrawingCache();
        Bitmap bitmap = this.getDrawingCache();
        return bitmap;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setEraser(boolean isEraser) {
        this.isEraser = isEraser;
        canEdit = false;
    }

    public boolean getEraser() {
        return isEraser;
    }

    public void setShowBackGroudView(boolean showBackGroudView) {
        isShowBackGroudView = showBackGroudView;
    }

    public boolean getIsShowBackGroudView() {
        return isShowBackGroudView;
    }

    public boolean isEmpty() {
        if (mPointList == null || mPointList.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    //点击down事件
    public interface ClickDownListener {
        void clickDown();
    }

    public void setClickDownListener(ClickDownListener clickDownListener) {
        this.clickDownListener = clickDownListener;
    }

    //获取离线数据监听
    public interface FlashDataListener {
        void getFlashDataFinish();  //获取离线数据完成
    }

    public void setFlashDataListener(FlashDataListener flashDataListener) {
        this.flashDataListener = flashDataListener;
    }

    //设置默认画笔颜色
    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
        mPaint.setColor(Color.parseColor(this.defaultColor));
    }

    //设置默认画笔宽度
    private void setmPaintWidth(float mPaintWidth) {
//        if (this.mPaintWidth - mPaintWidth > 1) {
//            this.mPaintWidth -= 1;
//        } else {
        this.mPaintWidth = mPaintWidth;
//        }
//        this.mPaintWidth = mPaintWidth;
        mPaint.setStrokeWidth(this.mPaintWidth * basePoint);
    }

    //设置橡皮擦宽度
    private void setmEraserWidth(int mEraserWidth) {
        this.mEraserWidth = mEraserWidth;
        mEraserPaint.setStrokeWidth(this.mEraserWidth);
    }

    //设置编辑框线宽度
    private void setmEditLinePaintWidth(int mEditLinePaintWidth) {
        this.mEditLinePaintWidth = mEditLinePaintWidth;
        mEditRectPaint.setStrokeWidth(this.mEditLinePaintWidth);
    }

    //设置编辑框线颜色
    private void setmEditLinePaintColor(String mEditLinePaintColor) {
        this.mEditLinePaintColor = mEditLinePaintColor;
        mEditRectPaint.setColor(Color.parseColor(this.mEditCirclePaintColor));
    }

    //设置编辑框圆宽度
    private void setmEditCiclePaintWidth(int mEditCiclePaintWidth) {
        this.mEditCiclePaintWidth = mEditCiclePaintWidth;
        mEditCirclePaint.setStrokeWidth(this.mEditCiclePaintWidth);
    }

    //设置编辑框圆颜色
    private void setmEditCirclePaintColor(String mEditCirclePaintColor) {
        this.mEditCirclePaintColor = mEditCirclePaintColor;
        mEditCirclePaint.setColor(Color.parseColor(this.mEditCirclePaintColor));
    }

    /**
     * 弹出框复制操作
     */
    @Override
    public void copy() {
        mMoveRectPointList = new ArrayList<>();
        for (int i = 0; i < mPointList.size(); i++) {
            EwritePoint point = mPointList.get(i);
            int xPoint = EwritePointUtils.getXLocation(point.getPointsStr(), SCALE_X, basePoint);//X坐标
            int yPoint = EwritePointUtils.getYLocation(point.getPointsStr(), SCALE_Y, basePoint);//Y坐标
            if (xPoint < rightP && xPoint > leftP && yPoint < endP && yPoint > topP) {
                if (!mMoveRectPointList.contains(point)) {
                    mMoveRectPointList.add(new EwritePoint(point.getColor(), point.getPointsStr()));
                }
            }
        }
        if (mMoveRectPointList.size() > 0) {
            EwriteSPUtils.put(this.getContext(), COPY_POINTS, EwriteJsonUtils.ListToJson(mMoveRectPointList));
            EwriteSPUtils.put(this.getContext(), "centerX", drawRectLeft + ((drawRectRight - drawRectLeft) / 2));
            EwriteSPUtils.put(this.getContext(), "centerY", drawRectTop + ((drawRectDown - drawRectTop) / 2));
            mMoveRectPointList.clear();
            mMoveRectPointList = null;
        }
    }

    /**
     * 粘贴操作
     */
    @Override
    public void paste() {
        mMoveRectPointList = new ArrayList<>();
        if (EwriteSPUtils.get(this.getContext(), COPY_POINTS, "") != null && EwriteSPUtils.get(this.getContext(), COPY_POINTS, "").toString().length() > 0) {
            mMoveRectPointList = EwriteJsonUtils.JsonToList((String) EwriteSPUtils.get(this.getContext(), COPY_POINTS, ""));
            float centerX = (float) EwriteSPUtils.get(this.getContext(), "centerX", (float) 0.0);
            float centerY = (float) EwriteSPUtils.get(this.getContext(), "centerY", (float) 0.0);
            //粘贴
            moveRectPointList(startEditPointX, startEditPointY, centerX, centerY);
            loopPointToOldPointList(mMoveRectPointList);
            editPoint = new ArrayList<>();
            stepTypeList.add(PASTE_STEP);
            invalidateEditPoint();
            addMoveRectPointsToMPoints();
        }
    }

    /**
     * 弹出框删除操作
     */
    @Override
    public void delete() {
        List<EwritePoint> delPointList = new ArrayList<>();
        for (int i = 0; i < mPointList.size(); i++) {
            EwritePoint point = mPointList.get(i);
            int xPoint = EwritePointUtils.getXLocation(point.getPointsStr(), SCALE_X, basePoint);//X坐标
            int yPoint = EwritePointUtils.getYLocation(point.getPointsStr(), SCALE_Y, basePoint);//Y坐标
            if (xPoint < rightP && xPoint > leftP && yPoint < endP && yPoint > topP) {
                delPointList.add(point);
            }
        }
        mPointList.removeAll(delPointList);
        loopPointToDelPointList(delPointList);
        stepTypeList.add(DELETE_STEP);
        preDeletePointList.add(delPointList);
        setSaveEditPointList(delPointList);
        paintDeletePointCanvas((int) leftP, (int) topP, (int) rightP, (int) endP);
    }

    private String type;
    public static final String EDIT_WRITE_COLOR = "edit_write_color";
    public static final String EDIT_CHOOSE_COLOR = "edit_choose_color";

    /**
     * 弹出框改变颜色显示颜色选择dialog
     *
     * @param type 弹窗形式：EDIT_WRITE_COLOR：更改画笔颜色，EDIT_CHOOSE_COLOR：更改选择框选中颜色
     * @param y    弹窗 Y 轴坐标
     */
    public void showChangeColorDialog(String type, float y) {
        this.type = type;
        chooseColorDialog.showChangeColorDialog(0, y);
        chooseColorDialog.setCurColor(editColor);
    }

    /**
     * 弹出框改变颜色显示颜色选择dialog
     *
     * @param colorList 自定义颜色列表
     */
    public void showChangeColorDialog(String type, float y, List<String> colorList) {
        this.type = type;
        chooseColorDialog.showChangeColorDialog(0, y, colorList);
        chooseColorDialog.setCurColor(editColor);
    }

    /**
     * 弹出框改变颜色显示颜色选择dialog
     *
     * @param type 弹窗形式：EDIT_WRITE_COLOR：更改画笔颜色，EDIT_CHOOSE_COLOR：更改选择框选中颜色
     */
    @Override
    public void changecolor(String type) {
        this.type = type;
        chooseColorDialog.showChangeColorDialog(0, topP);
        chooseColorDialog.setCurColor(Color.parseColor(defaultColor));
    }

    /**
     * 弹出框消失
     */
    @Override
    public void editDialogDismiss() {
    }

    /**
     * 颜色选择器选择颜色
     *
     * @param chooseColor
     */
    @Override
    public void onColorChange(int chooseColor) {
        editColor = chooseColor;
        if (EDIT_WRITE_COLOR.equals(type)) {
            mPaint.setColor(editColor);
        } else if (EDIT_CHOOSE_COLOR.equals(type)) {
            if (clipBitmap == null) {
                initClip(EwriteCommenUtil.getClipBitmap(mBitmap, leftP, topP, rightP, endP));
            }
            moveRectMatrix = new Matrix();
            moveRectMatrix.postTranslate(leftP, topP);
            clipPaint.setColorFilter(new PorterDuffColorFilter(editColor, PorterDuff.Mode.SRC_IN));
            removeMoveRectPointsList((int) leftP, (int) topP, (int) rightP, (int) endP, editColor);
            loopPointToOldPointList(mMoveRectPointList);
            //改变颜色
            editPoint = new ArrayList<>();
            stepTypeList.add(COLOR_STEP);
            invalidateEditPoint();
            addMoveRectPointsToMPoints();
        }
    }

    /**
     * 颜色选择器消失
     */
    @Override
    public void changeColorDialogDismiss() {
        if (EDIT_WRITE_COLOR.equals(type)) {

        } else if (EDIT_CHOOSE_COLOR.equals(type)) {

        }
    }

    /**
     * 获取位图流文件
     *
     * @return
     */
    public File getViewBitmapByte() {
        destroyDrawingCache();
        setDrawingCacheEnabled(true);
        buildDrawingCache();//这句话可加可不加，因为getDrawingCache()执行的主体就是buildDrawingCache()
//        Bitmap bitmap = Bitmap.createBitmap(getDrawingCache(), 0, 0, getMeasuredWidth(), getMeasuredHeight() - getPaddingBottom());
        Bitmap bitmap = getDrawingCache();
//        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                + File.separator + "Android/data/lpt.smart.pen/writeViewImg" + File.separator);
        File appDir = this.context.getExternalFilesDir("writeViewImg");
//        if (!appDir.exists()) {
//            appDir.mkdirs();
//        }

        //获取文件
        File file = new File(appDir, "picture_" + System.currentTimeMillis() + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();

            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
//                if (bitmap != null) {
//                    bitmap.recycle();
//                    bitmap = null;
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setDrawingCacheEnabled(false);
        return null;
    }

    float basePoint = 1f;

    /**
     * 设置缩放系数
     *
     * @param dpValues
     */
    public void setScale(float dpValues, float basePoint) {
        SCALE_X = EwriteNoteBookScaleUtil.getWidthScale(context, dpValues);
        SCALE_Y = EwriteNoteBookScaleUtil.getHeightScale(context, dpValues);
        this.basePoint = basePoint;
    }

    /**
     * 清除页面数据
     */
    public void clearData() {
        if (mCanvas != null) {
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
    }

    public void destroyView() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        if (mCanvas != null) {
            mCanvas = null;
        }
    }
}