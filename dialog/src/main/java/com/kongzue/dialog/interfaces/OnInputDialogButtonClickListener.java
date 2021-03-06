package com.kongzue.dialog.interfaces;

import android.view.View;

import com.kongzue.dialog.util.BaseDialog;


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
public interface OnInputDialogButtonClickListener {
    
    boolean onClick(BaseDialog baseDialog, View v, String inputStr);
}
