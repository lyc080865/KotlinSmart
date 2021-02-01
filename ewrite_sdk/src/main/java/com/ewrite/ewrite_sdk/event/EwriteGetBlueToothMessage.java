package com.ewrite.ewrite_sdk.event;

import android.content.Intent;
import android.widget.Toast;

import com.ble.api.DataUtil;
import com.ewrite.ewrite_sdk.EwriteLeProxy;
import com.ewrite.ewrite_sdk.utils.EwriteCrcUtil;

/*
 * Describtion :
 * Create by zhonghuibin on 2019/3/25 11:10
 */
public class EwriteGetBlueToothMessage {
    public static final int EWRITE_HAS_FLASH = 1;  //有离线数据
    public static final int EWRITE_SERIAL_NUM = 2; //序列号
    public static final int EWRITE_POWER_STATION = 3;  //电池状态
    public static final int EWRITE_CHECK_PWD_RIGHT = 4;    //验证密码正确
    public static final int EWRITE_SUCCESS = 5;    //相应操作成功
    public static final int EWRITE_ERROR = 6;  //相应操作错误
    public static final int EWRITE_NO_FLASH = 7;   //没有离线数据
    public static final int EWRITE_DEAL_FLASH = 8; //处理离线数据
    private int current_type;
    private boolean hasFlashData = false;

    private Intent intent;

    public Intent getIntent() {
        return intent;
    }

    public int getCurrent_type() {
        return current_type;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
        String a = getDealData();
        if (a.length() == 14 && a.startsWith("CAEA02") && !a.substring(6, 10).endsWith("0000") && !"CAEA0201000AD5".equals(a)) {//离线数据状态(有离线数据)
            current_type = EWRITE_HAS_FLASH;
        } else if (a.startsWith("CAA20F")) {    //序列号
            current_type = EWRITE_SERIAL_NUM;
        } else if (a.startsWith("CA8202")) {//电池状态
            current_type = EWRITE_POWER_STATION;
        } else if ("CAAC00B975CAEA0200003BE6".equals(a)) {//验证密码正确
            current_type = EWRITE_CHECK_PWD_RIGHT;
        } else if ("CAAC00B975".equals(a)) {//相应操作成功回调
            current_type = EWRITE_SUCCESS;
        } else if ("CAEB00E2E1".equals(a)) {//密码错误
            current_type = EWRITE_ERROR;
        } else if ("CAEA0201000AD5".equals(a)) {//离线数据状态(没有离线数据)
            current_type = EWRITE_NO_FLASH;
        } else {
            //有离线数据,处理离线数据
            current_type = EWRITE_DEAL_FLASH;
        }
    }

    public String getDealData() {
        return DataUtil.byteArrayToHex(intent.getByteArrayExtra(EwriteLeProxy.EXTRA_DATA)).replace(" ", "");
    }
}
