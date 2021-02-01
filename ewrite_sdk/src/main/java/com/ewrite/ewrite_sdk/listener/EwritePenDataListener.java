package com.ewrite.ewrite_sdk.listener;

/*
 * Describtion :
 * Create by sunlp on 2019/7/16 16:32
 */
public interface EwritePenDataListener {
    void connect();     //连接智能笔

    void disConnect();    //断开智能笔

//    void connectError();    //智能笔连接错误
//
//    void connectTimeout();  //智能笔连接超时

    void receiveData(String data);  //智能笔笔画数据
}
