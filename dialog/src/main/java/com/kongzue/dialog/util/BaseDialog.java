package com.kongzue.dialog.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;

import com.kongzue.dialog.R;
import com.kongzue.dialog.interfaces.OnBackClickListener;
import com.kongzue.dialog.interfaces.OnShowListener;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.v3.BottomMenu;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.ShareDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
public abstract class BaseDialog {
    
    protected static WeakReference<AppCompatActivity> newContext;
    
    public BaseDialog() {
        initDefaultSettings();
    }
    
    protected enum BOOLEAN {
        NULL, FALSE, TRUE
    }
    
    protected static List<BaseDialog> dialogList = new ArrayList<>();           //Dialog list
    
    public WeakReference<AppCompatActivity> context;
    public WeakReference<DialogHelper> dialog;                                                 //Body！
    
    private BaseDialog baseDialog;
    private int layoutId;
    private int styleId;
    public boolean isShow;
    protected boolean isAlreadyShown;
    protected int customDialogStyleId;                                          //Dialog's style resource file
    
    protected DialogSettings.STYLE style;
    protected DialogSettings.THEME theme;
    protected BOOLEAN cancelable;
    
    protected TextInfo titleTextInfo;
    protected TextInfo messageTextInfo;
    protected TextInfo tipTextInfo;
    protected TextInfo buttonTextInfo;
    protected TextInfo buttonPositiveTextInfo;
    protected InputInfo inputInfo;
    protected int backgroundColor = 0;
    protected View customView;
    protected int backgroundResId = -1;
    
    public enum ALIGN {
        DEFAULT,
        TOP,
        BOTTOM
    }
    
    protected ALIGN align = ALIGN.DEFAULT;
    
    protected OnDismissListener onDismissListener;
    protected OnDismissListener dismissEvent;
    protected OnShowListener onShowListener;
    protected OnBackClickListener onBackClickListener;
    
    public void log(Object o) {
        if (DialogSettings.DEBUGMODE) Log.i(">>>", o.toString());
    }
    
    public void error(Object o) {
        if (DialogSettings.DEBUGMODE) Log.e(">>>", o.toString());
    }
    
    public BaseDialog build(BaseDialog baseDialog, int layoutId) {
        this.baseDialog = baseDialog;
        this.layoutId = layoutId;
        if ((style == DialogSettings.STYLE.STYLE_MIUI && baseDialog instanceof MessageDialog) ||
                baseDialog instanceof BottomMenu ||
                baseDialog instanceof ShareDialog) {
            align = ALIGN.BOTTOM;
        } else {
            align = ALIGN.DEFAULT;
        }
        return baseDialog;
    }
    
    public BaseDialog build(BaseDialog baseDialog) {
        this.baseDialog = baseDialog;
        this.layoutId = -1;
        return baseDialog;
    }
    
    protected void showDialog() {
        log("# showDialog");
        showDialog(R.style.BaseDialog);
    }
    
    protected void showDialog(int style) {
        if (isAlreadyShown) {
            return;
        }
        isAlreadyShown = true;
        dismissedFlag = false;
        if (DialogSettings.dialogLifeCycleListener != null)
            DialogSettings.dialogLifeCycleListener.onCreate(this);
        styleId = style;
        dismissEvent = new OnDismissListener() {
            @Override
            public void onDismiss() {
                log("# dismissEvent");
                dismissEvent();
                dismissedFlag = true;
                isShow = false;
                dialogList.remove(baseDialog);
                if (!(baseDialog instanceof TipDialog)) showNext();
                if (onDismissListener != null) onDismissListener.onDismiss();
                if (DialogSettings.dialogLifeCycleListener != null)
                    DialogSettings.dialogLifeCycleListener.onDismiss(BaseDialog.this);
            }
        };
        dialogList.add(this);
        if (!DialogSettings.modalDialog) {
            showNow();
        } else {
            if (baseDialog instanceof TipDialog) {
                showNow();
            } else {
                showNext();
            }
        }
    }
    
    protected void showNext() {
        log("# showNext:" + dialogList.size());
        List<BaseDialog> cache = new ArrayList<>();
        cache.addAll(BaseDialog.dialogList);
        for (BaseDialog dialog : cache) {
            if (dialog.context.get().isDestroyed()) {
                log("# Since context has been recycled, uninstall Dialog：" + dialog);
                dialogList.remove(dialog);
            }
        }
        for (BaseDialog dialog : dialogList) {
            if (!(dialog instanceof TipDialog)) {
                if (dialog.isShow) {
                    log("# Start interruption: Dialog is being displayed:" + dialog);
                    return;
                }
            }
        }
        for (BaseDialog dialog : dialogList) {
            if (!(dialog instanceof TipDialog)) {
                dialog.showNow();
                return;
            }
        }
    }
    
    private void showNow() {
        log("# showNow: " + toString());
        isShow = true;
        if (context.get() == null || context.get().isDestroyed()) {
            if (newContext == null || newContext.get() == null) {
                error("Context incorrectly pointed to an Activity or Null that has been closed. It is possible that the Activity was restarted due to horizontal and vertical screen switching or you manually executed the unload() method. Please confirm that it can correctly point to an Activity in use.");
                return;
            }
            context = new WeakReference<>(newContext.get());
        }
        FragmentManager fragmentManager = context.get().getSupportFragmentManager();
        dialog = new WeakReference<>(new DialogHelper().setLayoutId(baseDialog, layoutId));
        if (baseDialog instanceof MessageDialog && style == DialogSettings.STYLE.STYLE_MIUI) {
            styleId = R.style.BottomDialog;
        }
        if (baseDialog instanceof BottomMenu || baseDialog instanceof ShareDialog) {
            styleId = R.style.BottomDialog;
        }
        if (DialogSettings.systemDialogStyle != 0) {
            styleId = DialogSettings.systemDialogStyle;
        }
        if (customDialogStyleId != 0) {
            styleId = customDialogStyleId;
        }
        dialog.get().setStyle(DialogFragment.STYLE_NORMAL, styleId);
        dialog.get().show(fragmentManager, "kongzueDialog");
        dialog.get().setOnShowListener(new DialogHelper.PreviewOnShowListener() {
            @Override
            public void onShow(Dialog dialog) {
                showEvent();
                if (DialogSettings.dialogLifeCycleListener != null)
                    DialogSettings.dialogLifeCycleListener.onShow(BaseDialog.this);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        boolean flag = false;
                        if (onBackClickListener != null) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                                return flag = onBackClickListener.onBackClick();
                            }
                        }
                        return flag;
                    }
                });
            }
        });
        if (DialogSettings.systemDialogStyle == 0 && style == DialogSettings.STYLE.STYLE_IOS && !(baseDialog instanceof TipDialog) && !(baseDialog instanceof BottomMenu) && !(baseDialog instanceof ShareDialog)) {
            dialog.get().setAnim(R.style.iOSDialogAnimStyle);
        }
        
        if (baseDialog instanceof TipDialog) {
            if (cancelable == null)
                cancelable = DialogSettings.cancelableTipDialog ? BOOLEAN.TRUE : BOOLEAN.FALSE;
        } else {
            if (cancelable == null)
                cancelable = DialogSettings.cancelable ? BOOLEAN.TRUE : BOOLEAN.FALSE;
        }
        dialog.get().setCancelable(cancelable == BOOLEAN.TRUE);
    }
    
    public abstract void bindView(View rootView);
    
    public abstract void refreshView();
    
    public abstract void show();
    
    protected boolean dismissedFlag = false;
    
    public void doDismiss() {
        dismissedFlag = true;
        if (dialog != null && dialog.get() != null) {
            dialog.get().dismiss();
        }
    }
    
    protected void initDefaultSettings() {
        if (theme == null) theme = DialogSettings.theme;
        if (style == null) style = DialogSettings.style;
        if (backgroundColor == 0) backgroundColor = DialogSettings.backgroundColor;
        if (titleTextInfo == null) titleTextInfo = DialogSettings.titleTextInfo;
        if (messageTextInfo == null) messageTextInfo = DialogSettings.contentTextInfo;
        if (tipTextInfo == null) tipTextInfo = DialogSettings.tipTextInfo;
        if (buttonTextInfo == null) buttonTextInfo = DialogSettings.buttonTextInfo;
        if (inputInfo == null) inputInfo = DialogSettings.inputInfo;
        if (buttonPositiveTextInfo == null) {
            if (DialogSettings.buttonPositiveTextInfo == null) {
                buttonPositiveTextInfo = buttonTextInfo;
            } else {
                buttonPositiveTextInfo = DialogSettings.buttonPositiveTextInfo;
            }
        }
    }
    
    protected void useTextInfo(TextView textView, TextInfo textInfo) {
        if (textInfo == null) return;
        if (textView == null) return;
        if (textInfo.getFontSize() > 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textInfo.getFontSize());
        }
        if (textInfo.getFontColor() != 1) {
            textView.setTextColor(textInfo.getFontColor());
        }
        if (textInfo.getGravity() != -1) {
            textView.setGravity(textInfo.getGravity());
        }
        Typeface font = Typeface.create(Typeface.SANS_SERIF, textInfo.isBold() ? Typeface.BOLD : Typeface.NORMAL);
        textView.setTypeface(font);
    }
    
    //Check null
    protected boolean isNull(String s) {
        if (s == null || s.trim().isEmpty() || s.equals("null") || s.equals("(null)")) {
            return true;
        }
        return false;
    }
    
    protected int dip2px(float dpValue) {
        final float scale = context.get().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
    //Uneasy or obsessive-compulsive disorder available uninstall method
    public static void unload() {
        reset();
    }
    
    public static void reset() {
        for (BaseDialog dialog : dialogList) {
            if (dialog.isShow) {
                dialog.doDismiss();
                if (dialog.context != null) dialog.context.clear();
                dialog.dialog = null;
            }
        }
        dialogList = new ArrayList<>();
        if (newContext != null) newContext.clear();
        WaitDialog.waitDialogTemp = null;
    }
    
    public static int getSize() {
        return dialogList.size();
    }
    
    protected int getRootHeight() {
        int displayHeight = 0;
        Display display = context.get().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(point);
            displayHeight = point.y;
        } else {
            DisplayMetrics dm = new DisplayMetrics();
            context.get().getWindowManager().getDefaultDisplay().getMetrics(dm);
            displayHeight = dm.heightPixels;
        }
        return displayHeight;
    }
    
    protected int getRootWidth() {
        int displayWidth = 0;
        Display display = context.get().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(point);
            displayWidth = point.x;
        } else {
            DisplayMetrics dm = new DisplayMetrics();
            context.get().getWindowManager().getDefaultDisplay().getMetrics(dm);
            displayWidth = dm.widthPixels;
        }
        return displayWidth;
    }
    
    protected int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WindowInsets windowInsets = null;
            windowInsets = context.get().getWindow().getDecorView().getRootView().getRootWindowInsets();
            if (windowInsets != null) {
                return windowInsets.getStableInsetBottom();
            }
        }
        return 0;
    }
    
    protected void showEvent() {
    
    }
    
    protected void dismissEvent() {
    
    }
}
