package com.example.yanoo.glexam;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

public class MainGLSurfaceView extends GLSurfaceView {
    GLRenderer mRenderer;
    public MainGLSurfaceView(Context context) {
        super(context);

        Log.i("MainGLSurfaceView", "Create");
        mRenderer = new GLRenderer(context);
        Log.i("MainGLSurfaceView", "set");
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    public boolean onTouchEvent(final MotionEvent event) {
        queueEvent(new Runnable(){
            public void run() {

                mRenderer.setColor(event.getX() / getWidth(),
                        event.getY() / getHeight(),
                        1.0f);


                requestRender();

            }});
        return true;
    }
}

