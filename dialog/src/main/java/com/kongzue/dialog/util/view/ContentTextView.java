package com.kongzue.dialog.util.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Build;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.Method;
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

public class ContentTextView extends androidx.appcompat.widget.AppCompatTextView {
    
    private final static String TAG = ">>>";
    private final static char SPACE = ' '; //Space;
    private List<Integer> addCharPosition = new ArrayList<Integer>();  //Add spaces
    private static List<Character> punctuation = new ArrayList<Character>(); //Punctuation
    private CharSequence oldText = ""; //Old text, text that should have been displayed
    private CharSequence newText = ""; //New text, the text actually displayed
    private boolean inProcess = false; //Whether old text has been processed as new text
    private boolean isAddPadding = false; //Whether to add margins
    
    //Punctuation marks are used to add space to the back of the punctuation marks when there is more space on the right side of the textView, so that the right end is aligned
    static {
        punctuation.clear();
        punctuation.add(',');
        punctuation.add('.');
        punctuation.add('?');
        punctuation.add('!');
        punctuation.add(';');
        punctuation.add('，');
        punctuation.add('。');
        punctuation.add('？');
        punctuation.add('！');
        punctuation.add('；');
        punctuation.add('）');
        punctuation.add('】');
        punctuation.add(')');
        punctuation.add(']');
        punctuation.add('}');
    }
    
    public ContentTextView(Context context) {
        super(context);
    }
    
    private void init(Context context, AttributeSet attrs) {
        //Determine whether to use android:text in xml
        TypedArray tsa = context.obtainStyledAttributes(attrs, new int[]{
                android.R.attr.text
        });
        String text = tsa.getString(0);
        tsa.recycle();
        if (!TextUtils.isEmpty(text)) {
            setText(text);
        }
    }
    
    public ContentTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }
    
    public ContentTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    /**
     * Monitor text copying and remove spaces from the copied text
     *
     * @param id Operation id (copy, select all, etc.)
     * @return Whether the operation was successful
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.copy) {
            
            if (isFocused()) {
                final int selStart = getSelectionStart();
                final int selEnd = getSelectionEnd();
                
                int min = Math.max(0, Math.min(selStart, selEnd));
                int max = Math.max(0, Math.max(selStart, selEnd));
                
                //Use reflection to get the selected text information and close the operation box at the same time
                try {
                    Class cls = getClass().getSuperclass();
                    Method getSelectTextMethod = cls.getDeclaredMethod("getTransformedText", new Class[]{int.class, int.class});
                    getSelectTextMethod.setAccessible(true);
                    CharSequence selectedText = (CharSequence) getSelectTextMethod.invoke(this,
                            min, max);
                    copy(selectedText.toString());
                    
                    Method closeMenuMethod;
                    if (Build.VERSION.SDK_INT < 23) {
                        closeMenuMethod = cls.getDeclaredMethod("stopSelectionActionMode");
                    } else {
                        closeMenuMethod = cls.getDeclaredMethod("stopTextActionMode");
                    }
                    closeMenuMethod.setAccessible(true);
                    closeMenuMethod.invoke(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        } else {
            return super.onTextContextMenuItem(id);
        }
    }
    
    
    /**
     * Copy text to clipboard, remove added characters
     *
     * @param text text
     */
    private void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context
                .CLIPBOARD_SERVICE);
        int start = newText.toString().indexOf(text);
        int end = start + text.length();
        StringBuilder sb = new StringBuilder(text);
        for (int i = addCharPosition.size() - 1; i >= 0; i--) {
            int position = addCharPosition.get(i);
            if (position < end && position >= start) {
                sb.deleteCharAt(position - start);
            }
        }
        try {
            android.content.ClipData clip = android.content.ClipData.newPlainText(null, sb.toString());
            clipboard.setPrimaryClip(clip);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
    
    /**
     * Reset state
     */
    public void reset(){
        inProcess = false;
        addCharPosition.clear();
        newText = "";
        newText = "";
    }
    
    /**
     * Handle multiple lines of text
     *
     * @param paint brush
     * @param text  text
     * @param width Maximum available width
     * @return Processed text
     */
    private String processText(Paint paint, String text, int width) {
        if (text == null || text.length() == 0) {
            return "";
        }
        String[] lines = text.split("\\n");
        StringBuilder newText = new StringBuilder();
        for (String line : lines) {
            newText.append('\n');
            newText.append(processLine(paint, line, width, newText.length() - 1));
        }
        if (newText.length() > 0) {
            newText.deleteCharAt(0);
        }
        return newText.toString();
    }
    
    
    /**
     * Handle single lines of text
     *
     * @param paint                     brush
     * @param text                      text
     * @param width                     Maximum available width
     * @param addCharacterStartPosition Start position for adding text
     * @return Processed text
     */
    private String processLine(Paint paint, String text, int width, int addCharacterStartPosition) {
        if (text == null || text.length() == 0) {
            return "";
        }
        
        StringBuilder old = new StringBuilder(text);
        int startPosition = 0; // 起始位置
        
        float chineseWidth = paint.measureText("中");
        float spaceWidth = paint.measureText(SPACE + "");
        
        //The maximum capacity of Chinese characters, every time from this position to advance backward calculation
        int maxChineseCount = (int) (width / chineseWidth);
        
        //Reduce the width of a Chinese character to ensure that there is a space before and after each line
        maxChineseCount--;
        
        //If it cannot accommodate Chinese characters, return the empty string directly
        if (maxChineseCount <= 0) {
            return "";
        }
        
        for (int i = maxChineseCount; i < old.length(); i++) {
            if (paint.measureText(old.substring(startPosition, i + 1)) > (width - spaceWidth)) {
                
                //Width of excess space on the right
                float gap = (width - spaceWidth - paint.measureText(old.substring(startPosition,
                        i)));
                
                List<Integer> positions = new ArrayList<Integer>();
                for (int j = startPosition; j < i; j++) {
                    char ch = old.charAt(j);
                    if (punctuation.contains(ch)) {
                        positions.add(j + 1);
                    }
                }
                
                //The gap can be replaced with up to several spaces
                int number = (int) (gap / spaceWidth);
                
                //Increase the number of spaces
                int use = 0;
                
                if (positions.size() > 0) {
                    for (int k = 0; k < positions.size() && number > 0; k++) {
                        int times = number / (positions.size() - k);
                        int position = positions.get(k / positions.size());
                        for (int m = 0; m < times; m++) {
                            old.insert(position + m, SPACE);
                            addCharPosition.add(position + m + addCharacterStartPosition);
                            use++;
                            number--;
                        }
                    }
                }
                
                //Move the pointer, add spaces to the end of the paragraph for branch processing
                i = i + use;
                old.insert(i, SPACE);
                addCharPosition.add(i + addCharacterStartPosition);
                
                startPosition = i + 1;
                i = i + maxChineseCount;
            }
        }
        
        return old.toString();
    }
    
    @Override
    public void setText(CharSequence text, BufferType type) {
        //When the parent class is initialized, the subclass is not initialized temporarily, and the override method will be executed to shield it
        if (addCharPosition == null) {
            super.setText(text, type);
            return;
        }
        if (!inProcess && (text != null && !text.equals(newText))) {
            oldText = text;
            process(false);
            super.setText(newText, type);
        } else {
            //Restore initial state
            inProcess = false;
            super.setText(text, type);
        }
    }
    
    /**
     * Get real text
     *
     * @return  text
     */
    public CharSequence getRealText() {
        return oldText;
    }
    
    /**
     * Text conversion
     *
     * @param setText Whether to set the text of the textView
     */
    private void process(boolean setText) {
        if (oldText == null) {
            oldText = "";
        }
        if (!inProcess && getVisibility() == VISIBLE) {
            addCharPosition.clear();
           
            if (getWidth() == 0) {
                //No measurement is completed, wait for the measurement after processing
                post(new Runnable() {
                    @Override
                    public void run() {
                        process(true);
                    }
                });
                return;
            }
            
            //Do not add again after adding margins
            if (!isAddPadding) {
                int spaceWidth = (int) (getPaint().measureText(SPACE + ""));
                newText = processText(getPaint(), oldText.toString(), getWidth() - getPaddingLeft
                        () -
                        getPaddingRight() - spaceWidth);
                setPadding(getPaddingLeft() + spaceWidth, getPaddingTop(), getPaddingRight(),
                        getPaddingBottom());
                isAddPadding = true;
            } else {
                newText = processText(getPaint(), oldText.toString(), getWidth() - getPaddingLeft
                        () -
                        getPaddingRight());
            }
            inProcess = true;
            if (setText) {
                setText(newText);
            }
        }
    }
}