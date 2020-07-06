package com.kongzue.dialog.util;


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
public class TextInfo {
    
    private int fontSize = -1;              //Font size, the default style is used when the value is -1, unit: dp
    private int gravity = -1;               //Alignment, the default style is used when the value is -1, the value can be used Gravity.CENTER and other alignment
    private int fontColor = 1;              //Text color, the default style is used when the value is 1, the value can be obtained by Color.rgb(r,g,b), etc.
    private boolean bold = false;           //Bold
    
    public int getFontSize() {
        return fontSize;
    }
    
    public TextInfo setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }
    
    public int getGravity() {
        return gravity;
    }
    
    public TextInfo setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }
    
    public int getFontColor() {
        return fontColor;
    }
    
    public TextInfo setFontColor(int fontColor) {
        this.fontColor = fontColor;
        return this;
    }
    
    public boolean isBold() {
        return bold;
    }
    
    public TextInfo setBold(boolean bold) {
        this.bold = bold;
        return this;
    }
    
    @Override
    public String toString() {
        return "TextInfo{" +
                "fontSize=" + fontSize +
                ", gravity=" + gravity +
                ", fontColor=" + fontColor +
                ", bold=" + bold +
                '}';
    }
}
