package com.rbp.filexplorer.modelo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SwipeListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public SwipeListener(Context context) {
        this.gestureDetector = new GestureDetector(context, new GesticureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GesticureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffX = e1.getX() - e2.getX();
                float diffY = e1.getY() - e2.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0)
                            swipeLeft();
                        else
                            swipeRight();
                        result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0)
                        swipeUp();
                    else
                        swipeDown();
                    result = true;
                }
            } catch (Exception e) {
                Log.w("SWIPE LISTENER", e.toString());
            }
            return result;
        }
    }

    public void swipeUp() {
        Log.d("SWIPE", "UP");
    }

    public void swipeDown() {
        Log.d("SWIPE", "DOWN");
    }

    public void swipeLeft() {
        Log.d("SWIPE", "LEFT");
    }

    public void swipeRight() {
        Log.d("SWIPE", "RIGHT");
    }
}
