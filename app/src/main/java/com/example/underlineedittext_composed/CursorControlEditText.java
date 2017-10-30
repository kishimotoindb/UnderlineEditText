package com.example.underlineedittext_composed;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;

/**
 * Created by BigFaceBear on 2017.10.16
 */

/*
 * 效果说明：
 * 1.背景色透明，隐藏底部的实线
 * 2.不能长按
 * 3.不能选择编辑框中的文本
 * 4.不能设置任意的光标位置。触摸该EditText时，如果已输入字符，光标位于最后一个字符后面；如果
 * 没有输入字符，光标位于起始位置。
 * 5.长按不应该调起"复制 全选 粘贴"的对话框
 */

public class CursorControlEditText extends AppCompatEditText {
    public CursorControlEditText(Context context) {
        this(context, null);
    }

    public CursorControlEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CursorControlEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        //隐藏底部的下划线，背景色设置为透明色
        setBackgroundColor(0x00000000);

        //不能长按
        setLongClickable(false);

        //不能长按，API23才可以使用这两个方法
        //setOnContextClickListener(null);
        //setContextClickable(false);

        //不能选择文本
        setTextIsSelectable(false);


        //横屏不应用额外的布局
        setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        setSelectAllOnFocus(false);

        setFocusableInTouchMode(true);

        setSingleLine();
    }

    private long lastClick;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (SystemClock.elapsedRealtime() - lastClick < 400) {
                return false;
            }

            lastClick = SystemClock.elapsedRealtime();
        }

        boolean superResult = super.onTouchEvent(event);

        setSelection(getText().length());

        return superResult;

    }
}
