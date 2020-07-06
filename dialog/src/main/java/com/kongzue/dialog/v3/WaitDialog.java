package com.kongzue.dialog.v3;

import androidx.appcompat.app.AppCompatActivity;

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
public class WaitDialog extends TipDialog {
    
    private WaitDialog() {
    }
    
    public static TipDialog show(AppCompatActivity context, String message) {
        return TipDialog.showWait(context, message);
    }
    
    public static TipDialog show(AppCompatActivity context, int messageResId) {
        return TipDialog.showWait(context, messageResId);
    }
    
    @Override
    public void show() {
        setDismissEvent();
        showDialog();
    }
    
    public WaitDialog setCustomDialogStyleId(int customDialogStyleId) {
        if (isAlreadyShown) {
            error("You must use setTheme(...) to modify the dialog theme or style only when you use the build(...) method.");
            return this;
        }
        this.customDialogStyleId = customDialogStyleId;
        return this;
    }
    
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    }
}
