package com.rbp.filexplorer.modelo;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

public abstract class SwipeListener implements View.OnTouchListener {

    private static final int SWIPE_THRESHOLD = 100;

    private static final long DEFAULT_QUALIFICATION_SPAN = 200;

    private float startY;
    private float startX;

    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;


    private boolean hasSwiped;

    public SwipeListener() {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                float currentY = event.getY();

                float diffX = startX - currentX;
                float diffY = startY - currentY;

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    hasSwiped = true;
                    if (Math.abs(diffX) > SWIPE_THRESHOLD)
                        if (diffX > 0)
                            swipeLeft(startX, currentX, startY, currentY);
                        else
                            swipeRight(startX, currentX, startY, currentY);
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD) {
                    hasSwiped = true;
                    if (diffY > 0)
                        swipeUp(startY, currentY, startX, currentX);
                    else
                        swipeDown(startY, currentY, startX, currentX);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!hasSwiped) {
                    if ((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
                        onDoubleClick();
                    } else {
                        onClick();
                    }
                    timestampLastClick = SystemClock.elapsedRealtime();
                } else
                    onActionUp();

                hasSwiped = false;

                break;
        }
        return true;
    }


    /**
     * Método que se ejecuta cuando el usuario levanta el dedo de la pantalla;
     */
    public abstract void onActionUp();

    /**
     * Método que se ejecuta al hacer doble click
     */
    public abstract void onDoubleClick();

    /**
     * Método que se ejecuta al hacer click
     */
    public abstract void onClick();

    /**
     * Método que se ejecuta al deslizar el dedo hacia arriba
     *
     * @param startY coordenada Y inicial
     * @param y      coordenada Y actual
     * @param startX coordenada X inicial
     * @param x      coordenada X actual
     */
    public abstract void swipeUp(float startY, float y, float startX, float x);

    /**
     * Método que se ejecuta al deslizar el dedo hacia abajo
     *
     * @param startY coordenada Y inicial
     * @param y      coordenada Y actual
     * @param startX coordenada X inicial
     * @param x      coordenada X actual
     */
    public abstract void swipeDown(float startY, float y, float startX, float x);

    /**
     * Método que se ejecuta cuando se desliza el dedo hacia la izquierda
     *
     * @param startX coordenada X inicial
     * @param x      coordenada X actual
     * @param startY coordenada Y inicial
     * @param y      coordenada Y actual
     */
    public abstract void swipeLeft(float startX, float x, float startY, float y);

    /**
     * Método que se ejecuta cuando se desliza el dedo hacia la derecha
     *
     * @param startX coordenada X inicial
     * @param x      coordenada X actual
     * @param startY coordenada Y inicial
     * @param y      coordenada Y actual
     */
    public abstract void swipeRight(float startX, float x, float startY, float y);
}
