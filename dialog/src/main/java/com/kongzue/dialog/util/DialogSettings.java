package com.kongzue.dialog.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

import android.util.Log;

import com.kongzue.dialog.interfaces.DialogLifeCycleListener;

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
public class DialogSettings {
    
    public enum STYLE {
        STYLE_MATERIAL, STYLE_KONGZUE, STYLE_IOS, STYLE_MIUI
    }
    
    public enum THEME {
        LIGHT, DARK
    }
    
    //Whether to enable the blur effect
    public static boolean isUseBlur = false;
    
    //Open the modal queue start method
    public static boolean modalDialog = false;
    
    //Global theme style
    public static STYLE style = STYLE.STYLE_MATERIAL;
    
    //Global dialog light and dark style
    public static THEME theme = THEME.LIGHT;
    
    //Global prompt box light and dark style
    public static THEME tipTheme = THEME.DARK;
    
    //Global title text style
    public static TextInfo titleTextInfo;
    
    //Global body text style
    public static TextInfo contentTextInfo;
    
    //Global prompt text style
    public static TextInfo tipTextInfo;
    
    //Global default button text style
    public static TextInfo buttonTextInfo;
    
    //Global focus button text style
    public static TextInfo buttonPositiveTextInfo;
    
    //Global input box text style
    public static InputInfo inputInfo;
    
    //Global menu title style
    public static TextInfo menuTitleInfo;
    
    //Global menu text style
    public static TextInfo menuTextInfo;
    
    //Global dialog background color, not effective at value 0
    public static int backgroundColor = 0;
    
    //Whether the global dialog box can be closed by clicking the peripheral mask area or the back button by default, this switch does not affect the prompt box (TipDialog) and the wait box (TipDialog)
    public static boolean cancelable = true;
    
    //Whether the global prompt box and wait box (WaitDialog, TipDialog) can be closed by default
    public static boolean cancelableTipDialog = false;
    
    //Whether to allow log display
    public static boolean DEBUGMODE = false;
    
    //Blur transparency (0~255)
    public static int blurAlpha = 210;
    
    //Allow to customize the system dialog style. Note that setting this function will cause the original dialog style and animation to be invalid
    public static int systemDialogStyle;
    
    //The default cancel button text and text affect BottomDialog, ShareDialog
    public static String defaultCancelButtonText;
    
    //Global Dialog lifecycle listener
    public static DialogLifeCycleListener dialogLifeCycleListener;
    
    //Global prompt box background resource, does not take effect when value is 0
    public static int tipBackgroundResId = 0;
    
    //Dialog, global button resources
    public static Drawable okButtonDrawable;
    public static Drawable cancelButtonDrawable;
    public static Drawable otherButtonDrawable;
    
    //Input dialog, whether to automatically pop up the input keyboard
    public static boolean autoShowInputKeyboard = false;
    
    //Check Renderscript support
    public static boolean checkRenderscriptSupport(Context context) {
        boolean isSupport = true;
        try {
            DialogSettings.class.getClassLoader().loadClass("android.graphics.drawable.RippleDrawable");
            DialogSettings.class.getClassLoader().loadClass("androidx.renderscript.RenderScript");
        } catch (ClassNotFoundException e) {
            isSupport = false;
            if (DEBUGMODE) {
                Log.e(">>>", "\nerror！\nThe RenderScript support library is not enabled. To enable the blur effect, add the following statement in the Gradle configuration file of your app:" +
                        "\nandroid { \n...\n  defaultConfig { \n    ...\n    renderscriptTargetApi 17 \n    renderscriptSupportModeEnabled true \n  }\n}");
            }
        }
        
        RenderScript renderScript = null;
        ScriptIntrinsicBlur blurScript = null;
        try {
            renderScript = RenderScript.create(context);
            blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        } catch (Exception e) {
            isSupport = false;
        } finally {
            if (renderScript != null) {
                renderScript.destroy();
                renderScript = null;
            }
            if (blurScript != null) {
                blurScript.destroy();
                blurScript = null;
            }
        }
        isUseBlur = isSupport;
        
        if (DEBUGMODE) {
            Log.i(">>>", "Check Renderscript support: " + isSupport);
        }
        return isSupport;
    }
    
    public static void init() {
        BaseDialog.reset();
    }
}
