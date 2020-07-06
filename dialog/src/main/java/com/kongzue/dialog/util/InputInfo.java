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
public class InputInfo {
    
    private int MAX_LENGTH = -1;    //Maximum length, -1 does not take effect
    private int inputType;          //For details, see android.text.InputType
    private TextInfo textInfo;      //Default font style
    private boolean multipleLines;  //Support multiple lines
    private boolean selectAllText;  //All text is selected by default (easy to modify)
    
    public int getMAX_LENGTH() {
        return MAX_LENGTH;
    }
    
    public InputInfo setMAX_LENGTH(int MAX_LENGTH) {
        this.MAX_LENGTH = MAX_LENGTH;
        return this;
    }
    
    public int getInputType() {
        return inputType;
    }
    
    public InputInfo setInputType(int inputType) {
        this.inputType = inputType;
        return this;
    }
    
    public TextInfo getTextInfo() {
        return textInfo;
    }
    
    public InputInfo setTextInfo(TextInfo textInfo) {
        this.textInfo = textInfo;
        return this;
    }
    
    public boolean isMultipleLines() {
        return multipleLines;
    }
    
    public InputInfo setMultipleLines(boolean multipleLines) {
        this.multipleLines = multipleLines;
        return this;
    }
    
    public boolean isSelectAllText() {
        return selectAllText;
    }
    
    public InputInfo setSelectAllText(boolean selectAllText) {
        this.selectAllText = selectAllText;
        return this;
    }
}
