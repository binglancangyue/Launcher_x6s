package com.cywl.launcher.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TestRecycleView extends RecyclerView {
    private int mLastMotionX, mLastMotionY;
    // 是否移动了
    private boolean isMoved;
    // 是否释放了
    private boolean isReleased;
    // 计数器，防止多次点击导致最后一次形成longpress的时间变短
    private int mCounter;
    // 长按的runnable
    private Runnable mLongPressRunnable;
    // 移动的阈值
    private static final int TOUCH_SLOP = 20;
    private boolean isLongPress = false;
    private MotionEvent motionEvent = null;
    private boolean isDown = false;

    public TestRecycleView(Context context) {
        super(context);

    }


    public TestRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mLongPressRunnable = new Runnable() {
            @Override
            public void run() {
                isDown = false;
                isLongPress = true;
                performLongClick();

            }
        };
    }

    public TestRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    //    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        int x = (int) ev.getX();
//        int y = (int) ev.getY();
//        motionEvent = ev;
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                Log.d("ACTION_aa", "dispatchTouchEvent:ACTION_DOWN ");
//                mLastMotionX = x;
//                mLastMotionY = y;
//                mCounter++;
//                isLongPress = false;
//                isReleased = false;
//                isMoved = false;
//                isDown = true;
//                postDelayed(mLongPressRunnable, 3000);// 按下 3秒后调用线程
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (isMoved)
//                    break;
//                if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
//                        || Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
//                    // 移动超过阈值，则表示移动了
//                    isMoved = true;
//                    isDown = false;
//                    Log.d("ACTION_aa", "dispatchTouchEvent:ACTION_MOVE ");
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                isDown = false;
//                isLongPress = false;
//                Log.d("ACTION_aa", "dispatchTouchEvent:ACTION_UP ");
//                // 释放了
//                isReleased = true;
//                break;
//        }
//        Log.d("isLongPress", "dispatchTouchEvent: " + isLongPress);
//        if (isLongPress) {
//            if (isMoved) {
//                return super.dispatchTouchEvent(ev);
//            }
//            return true;
//        } else {
////            if (isDown) {
////                return true;
////            }
////            return true;
//            return super.dispatchTouchEvent(ev);
//        }
//    }


}
