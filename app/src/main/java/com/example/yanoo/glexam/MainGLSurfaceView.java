package com.example.yanoo.glexam;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MainGLSurfaceView extends GLSurfaceView implements ScaleGestureDetector.OnScaleGestureListener {
    GLRenderer           mRenderer;
    ScaleGestureDetector mDetector;

    float                mClickX, mClickY;

    public MainGLSurfaceView(Context context) {
        super(context);

        mRenderer = new GLRenderer(context);
        setRenderer(mRenderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mDetector = new ScaleGestureDetector(context, this);
    }

    public boolean onTouchEvent(final MotionEvent event) {
        queueEvent(new Runnable(){
            public void run() {
                // 0 2 7
                if (event.getAction() == event.ACTION_DOWN) {
                    mClickX = event.getX();
                    mClickY = event.getY();
                }
                //Log.i("Touch", String.format("%d %d %d %d", event.getAction(), event.ACTION_UP, event.ACTION_DOWN, event.ACTION_HOVER_MOVE));
//                mRenderer.getCubeTile().dragXY(mClickX - event.getX(), mClickY - event.getY());
                mClickX = event.getX();
                mClickY = event.getY();
                requestRender();

            }});
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }
}

