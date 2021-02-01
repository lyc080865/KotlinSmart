package com.ewrite.ewrite_sdk.dialog.choosecolordialog;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * Created by felix on 2017/12/1 下午3:07.
 */

public class EwriteColorGroup extends RadioGroup {

    public EwriteColorGroup(Context context) {
        super(context);
    }

    public EwriteColorGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getCheckColor() {
        int checkedId = getCheckedRadioButtonId();
        EwriteColorRadio radio = findViewById(checkedId);
        if (radio != null) {
            return radio.getColor();
        }
        return Color.parseColor("#333333");
    }

    public void setCheckColor(int color) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            EwriteColorRadio radio = (EwriteColorRadio) getChildAt(i);
            if (radio.getColor() == color) {
                radio.setChecked(true);
                break;
            }
        }
    }
}
