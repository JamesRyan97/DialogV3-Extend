package com.kongzue.dialog.util;

import android.os.Handler;
import android.os.Message;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2018/12/14 14:25
 *
 * Editor: James Ryan
 * Edit date: 2020/07/06
 * Github: https://github.com/JamesRyan97
 */
public class SafelyHandlerWrapper extends Handler {
    private Handler impl;
    
    public SafelyHandlerWrapper(Handler impl) {
        this.impl = impl;
    }
    
    @Override
    public void dispatchMessage(Message msg) {
        try {
            impl.dispatchMessage(msg);
        } catch (Exception e) {
        }
    }
    
    @Override
    public void handleMessage(Message msg) {
        impl.handleMessage(msg);
    }
}