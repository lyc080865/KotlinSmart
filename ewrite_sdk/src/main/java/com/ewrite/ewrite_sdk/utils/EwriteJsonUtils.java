package com.ewrite.ewrite_sdk.utils;

import android.text.TextUtils;


import com.ewrite.ewrite_sdk.bean.EwritePoint;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Describtion :
 * Create by zhonghuibin on 2019/5/5 16:26
 */public class EwriteJsonUtils {

    /**
     * @param result
     * @return 服务器返回是否成功
     */
    public static boolean isSuccess(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.optInt("code") == 1) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * @param result
     * @return 服务器返回是否成功
     */
    public static int getCode(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject.optInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Integer.MAX_VALUE;
    }


    /**
     * 将实体类转换成json字符串对象 注意此方法需要第三方gson
     * jar包
     *
     * @param obj 对象
     * @return map
     */
    public static String toJson(Object obj, int method) {
        if (method == 1) {

            //字段是首字母小写，其余单词首字母大写
            Gson gson = new Gson();
            String obj2 = gson.toJson(obj);
            return obj2;
        } else if (method == 2) {

            // FieldNamingPolicy.LOWER_CASE_WITH_DASHES    全部转换为小写，并用空格或者下划线分隔
            //FieldNamingPolicy.UPPER_CAMEL_CASE    所以单词首字母大写
            Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String obj2 = gson2.toJson(obj);
            return obj2;
        }
        return "";
    }

    /**
     * 把一个map变成json字符串
     *
     * @param map
     * @return
     */
    public static String parseMapToJson(Map<?, ?> map) {
        try {
            Gson gson = new Gson();
            return gson.toJson(map);
        } catch (Exception e) {
        }
        return null;
    }

    public static String ListToJson(ArrayList<EwritePoint> pointList) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<EwritePoint>>() {
        }.getType();
        return gson.toJson(pointList, type);
    }

    public static ArrayList<EwritePoint> JsonToList(String json) {
        Gson gson = new Gson();
        ArrayList<EwritePoint> p2 = new ArrayList<EwritePoint>();
        Type type1 = new TypeToken<List<EwritePoint>>() {
        }.getType();
        p2 = gson.fromJson(json, type1);
        return p2;
    }

    /**
     * 把json字符串变成集合 params: new TypeToken<List<yourbean>>(){}.getType(),
     *
     * @param json
     * @param type new TypeToken<List<yourbean>>(){}.getType()
     * @return
     */
    public static List<?> parseJsonToList(String json, Type type) {
        Gson gson = new Gson();
        List<?> list = gson.fromJson(json, type);
        return list;
    }

    /**
     * 获取json串中某个字段的值，注意，只能获取同一层级的value
     *
     * @param json
     * @param key
     * @return
     */
    public static String getFieldValue(String json, String key) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        if (!json.contains(key)) {
            return "";
        }
        JSONObject jsonObject = null;
        String value = null;
        try {
            jsonObject = new JSONObject(json);
            value = jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }
}
