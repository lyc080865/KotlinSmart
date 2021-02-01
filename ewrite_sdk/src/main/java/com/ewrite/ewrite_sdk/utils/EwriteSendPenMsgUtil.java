package com.ewrite.ewrite_sdk.utils;

import com.ble.api.DataUtil;
import com.ewrite.ewrite_sdk.EwriteLeProxy;


/**
 * Created by JiaJiefei on 2017/2/20.
 */

public class EwriteSendPenMsgUtil {


    //查询序列号
    public static void getSerialNum(final String bluetoothAddress) {
        EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC000000"));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CCA10045B1"));
            }
        }.start();
    }

    /*
     * describtion:查询离线数据状态
     * Create by zhonghuibin on 2019/4/10 16:58
     */
    public synchronized static void getFlashStation(final String bluetoothAddress) {
        EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC000000"));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CCE10089BC"));
            }
        }.start();
    }

    /*
     * describtion:获取离线数据
     * Create by zhonghuibin on 2019/4/9 14:45
     */
    public static void getFlashData(final String bluetoothAddress, final String order) {
        EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC000000"));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray(EwriteCrcUtil.getFlashOrder(order)));
            }
        }.start();
    }

    /*
     * describtion:验证用户输入密码是否正确
     * Create by zhonghuibin on 2019/4/9 14:45
     */
    public static void checkPassWord(final String bluetoothAddress, final String order) {
        EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC000000"));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray(EwriteCrcUtil.getValiDateOrder(order)));
            }
        }.start();
    }

    /*
     * describtion:验证用户是否已经设置了密码
     * Create by zhonghuibin on 2019/4/9 14:45
     */
    public static void checkHasPassWord(final String bluetoothAddress) {
        EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC000000"));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray(EwriteCrcUtil.getYanzhenZOrder("12345678")));
            }
        }.start();
    }

    /*
     * describtion:查询电量
     * Create by zhonghuibin on 2019/4/9 11:35
     */
    public static void selectPower(final String bluetoothAddress) {
        if (!EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC000000"))) {//发送失败，app断开蓝牙，需要重新连接
//            EwriteLeProxy.getInstance().disconnect(EwritePenConstant.PEN_ADDRESS);
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC8100A3B7"));
            }
        }.start();
    }

    /*
     * describtion:清除密码
     * Create by zhonghuibin on 2019/4/3 10:05
     */
    public static void clearPassWord(final String bluetoothAddress) {
        EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC000000"));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CCFC00A6C9"));
            }
        }.start();
    }

    /*
     * describtion:清除缓存数据
     * Create by zhonghuibin on 2019/4/3 10:05
     */
    public static void clearFlashData(final String bluetoothAddress) {
        EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC000000"));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CCFD0097FA"));
            }
        }.start();
    }

    /*
     * describtion:请求更新MTU
     * Create by zhonghuibin on 2019/4/3 10:05
     */
    public static void setMTU(final String bluetoothAddress, final int order) {
        EwriteLeProxy.getInstance().requestMtu(bluetoothAddress, order);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().requestMtu(bluetoothAddress, order);
            }
        }.start();
    }

    /*
     * describtion:设置密码
     * Create by zhonghuibin on 2019/4/3 10:05
     */
    public static void setPassWord(final String bluetoothAddress, final String order) {
        EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC000000"));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray(EwriteCrcUtil.getSetPassWordOrder(order)));
            }
        }.start();
    }

    /*
     * describtion:设置笔名字
     * Create by zhonghuibin on 2019/4/3 10:05
     */
    public static void reName(final String bluetoothAddress, final String order) {
        EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray("CC000000"));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                EwriteLeProxy.getInstance().send(bluetoothAddress, DataUtil.hexToByteArray(EwriteCrcUtil.penRename(order)));
            }
        }.start();
    }


    //String-->UniCode
    public static String stringToUnicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            //"\\u只是代号，请根据具体所需添加相应的符号"
//            unicode.append("\\u" + Integer.toHexString(c));
            unicode.append(Integer.toHexString(c));
        }
        return unicode.toString();
    }

    //UniCode-->String
    public static String unicodeToString(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            // 转换
            int data = Integer.parseInt(hex[i], 16);
            // 拼接成string
            string.append((char) data);
        }

        return string.toString();
    }
}
