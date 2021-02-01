package com.ewrite.ewrite_sdk.utils;

import java.util.Arrays;

/*
 * Describtion :
 * Create by zhonghuibin on 2019/2/20 11:45
 */
public class EwriteCrcUtil {
    /**
     * 为Byte数组最后添加两位CRC校验
     *
     * @param buf（验证的byte数组）
     * @return
     */
    public static byte[] setParamCRC(byte[] buf) {
        int checkCode = 0;
        checkCode = crc_16_CCITT_False(buf, buf.length);
        byte[] crcByte = new byte[2];
        crcByte[0] = (byte) ((checkCode >> 8) & 0xff);
        crcByte[1] = (byte) (checkCode & 0xff);
        // 将新生成的byte数组添加到原数据结尾并返回
        return concatAll(buf, crcByte);
    }

    /**
     * CRC-16/CCITT-FALSE x16+x12+x5+1 算法
     * <p>
     * info
     * Name:CRC-16/CCITT-FAI
     * Width:16
     * Poly:0x1021
     * Init:0xFFFF
     * RefIn:False
     * RefOut:False
     * XorOut:0x0000
     *
     * @param bytes
     * @param length
     * @return
     */

    public static int crc_16_CCITT_False(byte[] bytes, int length) {
        int crc = 0xffff; // initial value
        int polynomial = 0x1021; // poly value
        for (int index = 0; index < bytes.length; index++) {
            byte b = bytes[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }
        crc &= 0xffff;
        //输出String字样的16进制
        String strCrc = Integer.toHexString(crc).toUpperCase();
        System.out.println(strCrc);
        return crc;
    }

    public static String crc_16_CCITT_False_String(byte[] bytes, int length) {
        int crc = 0xffff; // initial value
        int polynomial = 0x1021; // poly value
        for (int index = 0; index < bytes.length; index++) {
            byte b = bytes[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }
        crc &= 0xffff;
        //输出String字样的16进制
        String strCrc = Integer.toHexString(crc).toUpperCase();
        System.out.println(strCrc);
        return strCrc;
    }

    /***
     * CRC校验是否通过
     * @param srcByte
     * @param length(验证码字节长度)
     * @return
     */
    public static boolean isPassCRC(byte[] srcByte, int length) {

        // 取出除crc校验位的其他数组，进行计算，得到CRC校验结果
        int calcCRC = calcCRC(srcByte, 0, srcByte.length - length);
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((calcCRC >> 8) & 0xff);
        bytes[1] = (byte) (calcCRC & 0xff);

        // 取出CRC校验位，进行计算
        int i = srcByte.length;
        byte[] b = {srcByte[i - 2], srcByte[i - 1]};

        // 比较
        return bytes[0] == b[0] && bytes[1] == b[1];
    }

    /**
     * 对buf中offset以前crcLen长度的字节作crc校验，返回校验结果
     *
     * @param buf
     * @param crcLen
     */
    private static int calcCRC(byte[] buf, int offset, int crcLen) {
        int start = offset;
        int end = offset + crcLen;
        int crc = 0xffff; // initial value
        int polynomial = 0x1021;
        for (int index = start; index < end; index++) {
            byte b = buf[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }
        crc &= 0xffff;
        return crc;
    }

    /**
     * 多个数组合并
     *
     * @param first
     * @param rest
     * @return
     */
    public static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static class Test {
        //测试数据
        static byte[] data = {(byte) 0xAA, 0x0C, 0x01, 0x00, 0x01, 0x00, 0x00, 0x04, 0x05, 0x17, 0x05, 0x01, (byte) 0xA0, (byte) 0x86, 0x01, 0x00};

        //AA 0C 01 00 01 00 00 04 05 17 05 01 A0 86 01 00
        //结果为：F2E3
        public static void main(String[] s) {
            byte[] crcData = EwriteCrcUtil.setParamCRC(data);
            if (EwriteCrcUtil.isPassCRC(crcData, 2)) {
                System.out.println("验证通过");
            } else {
                System.out.println("验证失败");
            }
        }
    }

    /**
     * 把16进制字符串转换成字节数组
     *
     * @param
     * @return byte[]
     */
    public static String hexStringToByte(String hex, int leng) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return crc_16_CCITT_False_String(result, 2);
//        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /*
     * describtion:清除密码指令生成
     * Create by zhonghuibin on 2019/4/2 14:57
     */
    public static String getSetPassWordOrder(String s) {
        String password = "";
        password = EwriteSendPenMsgUtil.stringToUnicode(s);
        String passwordleth = "";
        if (s.length() >= 10) {
            passwordleth = s.length() + "";
        } else {
            passwordleth = "0" + s.length();
        }
        String crc = EwriteCrcUtil.hexStringToByte("CCC1" + passwordleth + password, 2);
        if (crc.length() == 3) {
            crc = "0" + crc;
        }
        String lastestString = "CCC1" + passwordleth + password + crc.substring(2, 4) + crc.substring(0, 2);
        return lastestString;
    }

    /*
     * describtion:获取离线数据指令生成
     * Create by zhonghuibin on 2019/4/2 14:57
     */
    public static String getFlashOrder(String s) {
        String password = "";
        for (int i = 0; i < s.length(); i++) {
            password = password + "3" + s.substring(i, i + 1);
        }
        String passwordleth = "";
        if (s.length() >= 10) {
            passwordleth = s.length() + "";
        } else {
            passwordleth = "0" + s.length();
        }
        String crc = EwriteCrcUtil.hexStringToByte("CCD1" + passwordleth + password, 2);
        if (crc.length() == 3) {
            crc = "0" + crc;
        }
        String lastestString = "CCD1" + passwordleth + password + crc.substring(2, 4) + crc.substring(0, 2);
        return lastestString;
    }

    /*
     * describtion:获取验证密码指令生成
     * Create by zhonghuibin on 2019/4/2 14:57
     */
    public static String getYanzhenZOrder(String s) {
        String password = "";
        for (int i = 0; i < s.length(); i++) {
            password = password + "3" + s.substring(i, i + 1);
        }
        String passwordleth = "";
        if (s.length() >= 10) {
            passwordleth = s.length() + "";
        } else {
            passwordleth = "0" + s.length();
        }
        String crc = EwriteCrcUtil.hexStringToByte("CCC3" + passwordleth + password, 2);
        if (crc.length() == 3) {
            crc = "0" + crc;
        }
        String lastestString = "CCC3" + passwordleth + password + crc.substring(2, 4) + crc.substring(0, 2);
        return lastestString;
    }

    /*
     * describtion:智能笔重命名
     * Create by zhonghuibin on 2019/4/2 15:50
     */
    public static String penRename(String s) {
        String password = "";
        password = EwriteSendPenMsgUtil.stringToUnicode(s);
        String passwordleth = "";

        if (s.length() >= 10 && s.length() < 16) {
            passwordleth = Integer.toHexString(s.length()).toUpperCase();
            if (passwordleth.length() == 1) {
                passwordleth = "0" + passwordleth;
            }
        } else if (s.length() == 16) {
            passwordleth = "0G";
        } else if (s.length() == 17) {
            passwordleth = "0H";
        } else if (s.length() == 18) {
            passwordleth = "0I";
        } else if (s.length() == 19) {
            passwordleth = "0J";
        } else if (s.length() == 20) {
            passwordleth = "0K";
        } else {
            passwordleth = "0" + s.length();
        }
        if (s.length() >= 10) {
            passwordleth = Integer.toHexString(s.length()).toUpperCase();
            if (passwordleth.length() == 1) {
                passwordleth = "0" + passwordleth;
            }
        } else {
            passwordleth = "0" + s.length();
        }
        String crc = EwriteCrcUtil.hexStringToByte("CCA7" + passwordleth + password, 2);
        if (crc.length() == 3) {
            crc = "0" + crc;
        }
        String lastestString = "CCA7" + passwordleth + password + crc.substring(2, 4) + crc.substring(0, 2);
        return lastestString;
    }

    /*
     * describtion:获取离线数据指令生成
     * Create by zhonghuibin on 2019/4/2 14:57
     */
    public static String getValiDateOrder(String s) {
        String password = "";
        for (int i = 0; i < s.length(); i++) {
            password = password + "3" + s.substring(i, i + 1);
        }
        String passwordleth = "";
        if (s.length() >= 10) {
            passwordleth = s.length() + "";
        } else {
            passwordleth = "0" + s.length();
        }
        String crc = EwriteCrcUtil.hexStringToByte("CCC3" + passwordleth + password, 2);
        if (crc.length() == 3) {
            crc = "0" + crc;
        }
        String lastestString = "CCC3" + passwordleth + password + crc.substring(2, 4) + crc.substring(0, 2);
        return lastestString;
    }

    public static String getSerialNum(String serialStr) {
        //将获取到的序列号转换为unicode，在转换为string(其实它已经是unicode，只是没有\\u00标识，所以无法直接转换)
        StringBuilder unicodeAppend = new StringBuilder();
        String unicodeStr = serialStr.substring(6, serialStr.length() - 4);
        for (int i = 0; i < unicodeStr.length(); i += 2) {
            unicodeAppend.append("\\u00" + unicodeStr.substring(i, i + 2));
        }
        return EwriteSendPenMsgUtil.unicodeToString(String.valueOf(unicodeAppend));
    }

    public static String getPenPower(String str) {
        return str.substring(9, 10);
    }
}
