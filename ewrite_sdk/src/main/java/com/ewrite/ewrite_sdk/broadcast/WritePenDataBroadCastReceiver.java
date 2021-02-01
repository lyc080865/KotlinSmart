package com.ewrite.ewrite_sdk.broadcast;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ble.api.DataUtil;
import com.ewrite.ewrite_sdk.EwriteLeProxy;
import com.ewrite.ewrite_sdk.listener.EwritePenDataListener;
import com.ewrite.ewrite_sdk.utils.EwritePenConstant;

/*
 * Describtion :
 * Create by sunlp on 2019/7/16 14:13
 */public class WritePenDataBroadCastReceiver extends BroadcastReceiver {
    private EwritePenDataListener penDataListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        switch (intent.getAction()) {
            case EwriteLeProxy.ACTION_GATT_CONNECTED://连接上笔
                String address = intent.getStringExtra(EwriteLeProxy.EXTRA_ADDRESS);
                EwritePenConstant.PEN_ADDRESS = address;
                penDataListener.connect();
                break;
            case EwriteLeProxy.ACTION_GATT_DISCONNECTED://断开连接
            case BluetoothDevice.ACTION_ACL_DISCONNECTED://断开连接
                penDataListener.disConnect();
                break;
            case EwriteLeProxy.ACTION_CONNECT_ERROR://连接错误
//                penDataListener.connectError();
//                penDataListener.disConnect();
                break;
            case EwriteLeProxy.ACTION_CONNECT_TIMEOUT://连接超时
//                penDataListener.connectTimeout();
                penDataListener.disConnect();
                break;
            case EwriteLeProxy.ACTION_DATA_AVAILABLE:// 接收到从机数据
                byte[] data = intent.getByteArrayExtra(EwriteLeProxy.EXTRA_DATA);
                String dataStr = DataUtil.byteArrayToHex(data).replace(" ", "");
                penDataListener.receiveData(dataStr);
                break;
//            case EwriteLeProxy.ACTION_DATA_AVAILABLES:// 接收到从机数据
//                String writeData =intent.getStringExtra(EwriteLeProxy.EXTRA_DATA);
//                penDataListener.receiveData(writeData);
//                break;
            default:
                break;
        }
    }

    public void setPenDataListener(EwritePenDataListener penDataListener) {
        this.penDataListener = penDataListener;
    }
}
