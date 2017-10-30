package com.example.underlineedittext_composed;

import android.os.SystemClock;

/**
 * Created by bear 2017.8.12
 */

public class ClickUtil {
    public static long sLastClickTime;
    private static final int CLICK_SPAN = 300; //点击事件时间间隔（毫秒）

    public static boolean isRealClick() {
        if (SystemClock.elapsedRealtime() - sLastClickTime < CLICK_SPAN) {
            return false;
        }
        sLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }
}
