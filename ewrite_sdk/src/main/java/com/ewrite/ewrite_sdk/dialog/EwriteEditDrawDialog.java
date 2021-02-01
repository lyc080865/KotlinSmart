package com.ewrite.ewrite_sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewrite.ewrite_sdk.R;
import com.ewrite.ewrite_sdk.utils.EwriteScreenUtil;

import static com.ewrite.ewrite_sdk.widget.WriteDrawView.EDIT_CHOOSE_COLOR;


public class EwriteEditDrawDialog implements View.OnClickListener {
    private Context context;
    private Dialog dialog;
    private EditDialogClickCallback editDialogClick;
    private boolean isPaste = false;

    public EwriteEditDrawDialog(Context context, EditDialogClickCallback editPopupClick) {
        this.context = context;
        this.editDialogClick = editPopupClick;
    }

    public void showEditPopupWindow(float x, float y, boolean isPaste) {
        this.isPaste = isPaste;
        View contentView = LayoutInflater.from(context).inflate(R.layout.editdraw_dialog_layout, null);
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        }
        LinearLayout layout = contentView.findViewById(R.id.popup_all_layout);
        TextView copyTv = contentView.findViewById(R.id.popup_copy_tv);
        TextView deleteTv = contentView.findViewById(R.id.popup_delete_tv);
        TextView changeColor = contentView.findViewById(R.id.popup_changecolor_tv);
        copyTv.setOnClickListener(this);
        deleteTv.setOnClickListener(this);
        changeColor.setOnClickListener(this);
        if (isPaste) {
            layout.setBackgroundResource(R.color.transparent);
            copyTv.setText("粘贴");
            copyTv.setBackgroundResource(R.drawable.ewrite_shape_edit_popup_bg);
            deleteTv.setVisibility(View.GONE);
            changeColor.setVisibility(View.GONE);
        } else {
            layout.setBackgroundResource(R.drawable.ewrite_shape_edit_popup_bg);
            copyTv.setText("复制");
            copyTv.setBackgroundResource(R.color.transparent);
            deleteTv.setVisibility(View.VISIBLE);
            changeColor.setVisibility(View.VISIBLE);
        }

        dialog.setContentView(contentView);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (editDialogClick != null) {
                    editDialogClick.editDialogDismiss();
                }
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (isPaste) {
            int xLP = (x - EwriteScreenUtil.getWidthScale(context) * 54) > 0 ? (int) (x - EwriteScreenUtil.getWidthScale(context) * 54) : 0;
            lp.x = xLP;
        } else {
            int xLP = (x - EwriteScreenUtil.getWidthScale(context) * 185) > 0 ? (int) (x - EwriteScreenUtil.getWidthScale(context) * 185) : 0;
            lp.x = xLP;
        }
        lp.y = (int) y;
        lp.dimAmount = 0f;
        window.setGravity(Gravity.START | Gravity.LEFT | Gravity.TOP);
        window.setAttributes(lp);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.popup_copy_tv) {
            if (isPaste) {
                if (editDialogClick != null) {
                    editDialogClick.paste();
                }
            } else {
                if (editDialogClick != null) {
                    editDialogClick.copy();
                }
            }
            dialog.dismiss();
        } else if (v.getId() == R.id.popup_delete_tv) {
            if (editDialogClick != null) {
                editDialogClick.delete();
            }
            dialog.dismiss();
        } else if (v.getId() == R.id.popup_changecolor_tv) {
            if (editDialogClick != null) {
                editDialogClick.changecolor(EDIT_CHOOSE_COLOR);
            }
        }
    }

    public interface EditDialogClickCallback {
        void copy();

        void paste();

        void delete();

        void changecolor(String type);

        void editDialogDismiss();

    }
}
