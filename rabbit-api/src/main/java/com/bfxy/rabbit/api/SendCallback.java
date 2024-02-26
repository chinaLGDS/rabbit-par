package com.bfxy.rabbit.api;

/**
 * $SendCallback 回调函数处理
 * @author nly
 */
public interface SendCallback {

    void onSuccess();

    void onFailure();

}
