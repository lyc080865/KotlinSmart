package com.ewrite.ewrite_sdk.dialog.choosecolordialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.ewrite.ewrite_sdk.R;
import com.ewrite.ewrite_sdk.utils.EwriteCommenUtil;
import com.ewrite.ewrite_sdk.utils.EwriteScreenUtil;

import java.util.List;


/**
 * Created by felix on 2017/12/1 上午11:21.
 */

public class EwriteChooseColorDialog implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private Context context;
    private Dialog dialog;
    private ChangeColorCallBack changeColorCallBack;

    private EwriteColorGroup mColorGroup;

    public EwriteChooseColorDialog(Context context, ChangeColorCallBack mCallback) {
        this.context = context;
        this.changeColorCallBack = mCallback;
    }

    public void showChangeColorDialog(float x, float y) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.ewrite_change_color_layout, null);
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        }
        mColorGroup = contentView.findViewById(R.id.cg_colors);
        mColorGroup.setOnCheckedChangeListener(this);

        dialog.setContentView(contentView);
        FrameLayout.LayoutParams contentParam = new FrameLayout.LayoutParams(EwriteScreenUtil.getScreenWidth(context), ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(contentParam);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (changeColorCallBack != null) {
                    changeColorCallBack.changeColorDialogDismiss();
                }
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = (int) x;
        lp.y = (int) y;
        lp.dimAmount = 0f;
        window.setGravity(Gravity.TOP | Gravity.LEFT);
        window.setAttributes(lp);
        dialog.setCancelable(true);
        dialog.show();
    }

    public void showChangeColorDialog(float x, float y, final List<String> colorList) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.ewrite_change_self_color_layout, null);
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        }
        mColorGroup = contentView.findViewById(R.id.cg_colors);

        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        layoutParams.setMargins(EwriteCommenUtil.dip2px(context, 6),
                EwriteCommenUtil.dip2px(context, 6),
                EwriteCommenUtil.dip2px(context, 6),
                EwriteCommenUtil.dip2px(context, 6));
        for (int i = 0; i < colorList.size(); i++) {
            EwriteColorRadio colorRadio = new EwriteColorRadio(context, Color.parseColor(colorList.get(i)));
            colorRadio.setLayoutParams(layoutParams);
            colorRadio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            colorRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mColorGroup.getCheckColor();
                    if (changeColorCallBack != null) {
                        changeColorCallBack.onColorChange(mColorGroup.getCheckColor());
                    }
                }
            });
            mColorGroup.addView(colorRadio);
        }
        mColorGroup.setOnCheckedChangeListener(this);

        dialog.setContentView(contentView);
        FrameLayout.LayoutParams contentParam = new FrameLayout.LayoutParams(EwriteScreenUtil.getScreenWidth(context), ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(contentParam);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (changeColorCallBack != null) {
                    changeColorCallBack.changeColorDialogDismiss();
                }
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = (int) x;
        lp.y = (int) y;
        lp.dimAmount = 0f;
        window.setGravity(Gravity.TOP | Gravity.LEFT);
        window.setAttributes(lp);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mColorGroup.getCheckColor();
        if (changeColorCallBack != null) {
            changeColorCallBack.onColorChange(mColorGroup.getCheckColor());
        }
    }

    public void setCurColor(int color) {
        mColorGroup.setCheckColor(color);
    }


    public interface ChangeColorCallBack {
        void onColorChange(int chooseColor);

        void changeColorDialogDismiss();
    }

}
