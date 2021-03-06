package com.kongzue.dialog.util.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;


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

public class InterceptYLinearLayout extends LinearLayout {
    
    private OnYChanged onYChangedListener;
    
    public InterceptYLinearLayout(Context context) {
        super(context);
        
        init();
    }
    
    public InterceptYLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        
        init();
    }
    
    public InterceptYLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        init();
    }
    
    private float startAnimValue = 0, endAnimValue = 0;
    
    public ViewPropertyAnimator animY(float aimValue) {
        startAnimValue = getY();
        endAnimValue = aimValue;
        Log.i(">>>", "animY: from=" + startAnimValue + "  to=" + aimValue);
        return animate().setDuration(300).translationY(aimValue);
    }
    
    private void init() {
        if (!isInEditMode()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                animate().setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float progress = (float) animation.getAnimatedValue();
                        long value = (long) (startAnimValue + (endAnimValue - startAnimValue) * progress);
                        if (onYChangedListener != null) onYChangedListener.y(value);
                    }
                });
            }
        }
    }
    
    @Override
    public void setY(float y) {
        super.setY(y);
    }
    
    public OnYChanged getOnYChanged() {
        return onYChangedListener;
    }
    
    public InterceptYLinearLayout setOnYChanged(OnYChanged onYChanged) {
        this.onYChangedListener = onYChanged;
        return this;
    }
    
    public interface OnYChanged {
        void y(float y);
    }
    
    @Override
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        if (onYChangedListener != null) onYChangedListener.y(translationY);
    }
    
    private boolean touchDown;
    private float oldY;
    private boolean isBeingDragged = false;
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        
        if (isBeingDragged && action == MotionEvent.ACTION_MOVE) {
            return true;
        }
        
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isBeingDragged = false;
                touchDown = true;
                oldY = ev.getY();
                onTouchListener.onTouch(this, ev);
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchDown) {
                    if (Math.abs(ev.getY() - oldY) > dip2px(10)) {
                        isBeingDragged = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                touchDown = false;
                break;
        }
        return isBeingDragged;
    }
    
    private OnTouchListener onTouchListener;
    
    @Override
    public void setOnTouchListener(OnTouchListener l) {
        onTouchListener = l;
        super.setOnTouchListener(l);
    }
    
    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
