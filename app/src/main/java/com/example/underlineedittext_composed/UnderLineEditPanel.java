package com.example.underlineedittext_composed;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by BigFaceBear on 2017.10.30
 */

public class UnderLineEditPanel extends LinearLayout {
    private EditText mEt1;
    private View mLine1;
    private EditText mEt2;
    private View mLine2;
    private EditText mEt3;
    private View mLine3;
    private EditText mEt4;
    private View mLine4;

    private int mCursorPos = 1;

    public UnderLineEditPanel(Context context) {
        this(context, null);
    }

    public UnderLineEditPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnderLineEditPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(HORIZONTAL);
        View.inflate(getContext(), R.layout.view_number_line, this);

        initView();
    }

    private void initView() {
        mEt1 = findViewById(R.id.et1);
        mLine1 = findViewById(R.id.line1);
        mEt2 = findViewById(R.id.et2);
        mLine2 = findViewById(R.id.line2);
        mEt3 = findViewById(R.id.et3);
        mLine3 = findViewById(R.id.line3);
        mEt4 = findViewById(R.id.et4);
        mLine4 = findViewById(R.id.line4);

        //初始化光标和下划线状态
        mEt1.requestFocus();
        mCursorPos = 1;
        mLine1.setEnabled(false);
        mLine2.setEnabled(false);
        mLine3.setEnabled(false);
        mLine4.setEnabled(false);

//        mEt1.setCursorVisible(false);
//        mEt2.setCursorVisible(false);
//        mEt3.setCursorVisible(false);
//        mEt4.setCursorVisible(false);

        //初始化光标、下划线等状态控制的textWatcher
        mEt1.addTextChangedListener(new NextFocusWatcher(mLine1, mEt2));
        mEt2.addTextChangedListener(new NextFocusWatcher(mLine2, mEt3));
        mEt3.addTextChangedListener(new NextFocusWatcher(mLine3, mEt4));
        mEt4.addTextChangedListener(new NextFocusWatcher(mLine4, null));

        mEt1.setOnKeyListener(new OnDeleteListener(mLine1, mEt1, null, null));
        mEt2.setOnKeyListener(new OnDeleteListener(mLine2, mEt2, mLine1, mEt1));
        mEt3.setOnKeyListener(new OnDeleteListener(mLine3, mEt3, mLine2, mEt2));
        mEt4.setOnKeyListener(new OnDeleteListener(mLine4, mEt4, mLine3, mEt3));

    }

    //不允许子View任意获取触摸事件，EditText的焦点和光标由当前父布局控制
    //光标在谁，谁处理触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = true;
        switch (mCursorPos) {
            case 1:
                result = mEt1.onTouchEvent(event);
                break;
            case 2:
                result = mEt2.onTouchEvent(event);
                break;
            case 3:
                result = mEt3.onTouchEvent(event);
                break;
            case 4:
                result = mEt4.onTouchEvent(event);
                break;
        }
        return result;
    }

    private OnInputCompleteListener mInputCompleteListener;

    public void setInputCompleteListener(OnInputCompleteListener inputCompleteListener) {
        mInputCompleteListener = inputCompleteListener;
    }

    private interface OnInputCompleteListener {
        void onInputComplete(String input);
    }


    /*
     * 只负责正向的填写流程
     */
    private class NextFocusWatcher implements TextWatcher {
        View selfLine;
        EditText next;

        int before;

        public NextFocusWatcher(View selfLine, EditText next) {
            this.selfLine = selfLine;
            this.next = next;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            before = s.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (before == 0 && s.length() == 1) {
                //当前输入框没有字符，输入一个字符后跳转到下一个EditText

                if (next != null) {
                    next.requestFocus();
                    mCursorPos++;
                } else {
                    //最后一个EditTExt输入字符后，回调

                    if (mInputCompleteListener != null) {
                        mInputCompleteListener.onInputComplete(
                                mEt1.getText().toString()
                                        + mEt2.getText().toString()
                                        + mEt3.getText().toString()
                                        + mEt4.getText().toString());
                    }
                }

                selfLine.setEnabled(true);

            }
        }
    }

    public static long sLastClickTime;
    private class OnDeleteListener implements OnKeyListener {
        View selfLine;
        EditText selfEdit;
        View prevLine;
        EditText prevEdit;

        public OnDeleteListener(View selfLine, EditText selfEdit, View prevLine, EditText prevEdit) {
            this.selfLine = selfLine;
            this.selfEdit = selfEdit;
            this.prevLine = prevLine;
            this.prevEdit = prevEdit;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (SystemClock.elapsedRealtime() - sLastClickTime < 200) {
                return true;
            }
            sLastClickTime = SystemClock.elapsedRealtime();

            if (keyCode == KeyEvent.KEYCODE_DEL) {

                if (selfEdit.getText().length() > 0) {
                    // 如果当前EditText里有1个字符，那么删除字符，光标停留在当前EditText
                    selfEdit.setText(null);
                    selfLine.setEnabled(false);

                } else {
                    // 如果当前EditText里没有字符，那么删除前一个EditText里的字符，并将光标移动至
                    // 在前一个EditText中，自身的line恢复到没有字符时的颜色。
                    if (prevEdit != null) {
                        prevEdit.requestFocus();
                        mCursorPos--;
                        prevEdit.setText(null);
                        prevLine.setEnabled(false);
                    }
                }


                return true;
            }

            return false;
        }
    }

}
