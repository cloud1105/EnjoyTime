package com.leo.enjoytime.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * when use PhotoView with viewpager,if we zoom the pic in the PhotoView we found
 * exception: java.lang.IllegalArgumentException: pointerIndex out of range
 * this view fix it.
 * see https://github.com/chrisbanes/PhotoView/issues/31
 * Created by leo on 16/3/17.
 */
public class ViewPagerFixed extends android.support.v4.view.ViewPager {

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
