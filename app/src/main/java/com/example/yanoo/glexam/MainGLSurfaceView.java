package com.example.yanoo.glexam;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MainGLSurfaceView extends GLSurfaceView {
    public static MainGLSurfaceView sSingletone;
    GLRenderer           mRenderer;
    MediaPlayer          mMediaPlayer;
    SoundPool            mSoundPool;
    int[]               mSoundIdx;

    public MainGLSurfaceView(Context context) {
        super(context);

        sSingletone = this;

        mRenderer = new GLRenderer(context);
        setRenderer(mRenderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mMediaPlayer = MediaPlayer.create(context, R.raw.kalimba);
//        mMediaPlayer.start();
        mSoundPool =  new  SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        mSoundIdx = new int[1];
        mSoundIdx[0] = mSoundPool.load(context, R.raw.sword, 0);
    }

    float                mClickX, mClickY;
    float                mBeginDistance = 0;

    private float   getPointerDistance(final MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return 0.0f;
        }
        return (float)Math.hypot(event.getX(1) - event.getX(0),event.getY(1) - event.getY(0));
    }

    private int mouseMode = 0;

    public boolean onTouchEvent(final MotionEvent event) {
        queueEvent(new Runnable(){
            public void run() {
                switch(event.getPointerCount()) {
                    case 1:
                        if (mouseMode <= 1) {
                            if (event.getActionMasked() == event.ACTION_DOWN) {
                                mClickX = event.getX();
                                mClickY = event.getY();
                                mSoundPool.play(mSoundIdx[0], 1.0f, 1.0f, 0, 0, 1.0f);
                            } else {
                                if (event.getActionMasked() == event.ACTION_MOVE) {
                                    mRenderer.getCubeTile().dragXY(mClickX - event.getX(), mClickY - event.getY());
                                    mClickX = event.getX();
                                    mClickY = event.getY();
                                }
                            }
                        }
                        break;
                    case 2:
                        mouseMode = 2;
                        if ((event.getActionMasked() & event.ACTION_POINTER_DOWN) != event.ACTION_POINTER_DOWN) {
                            mScaleFactor += (getPointerDistance(event) - mBeginDistance) / 200.0f;
                        }
                        mScaleFactor = Math.max(1.0f, Math.min(5.0f, mScaleFactor));
                        mBeginDistance = getPointerDistance(event);
                        break;
                }
                if (event.getActionMasked() == event.ACTION_UP) {
                    mouseMode = 0;
                }
                Log.i("mouse", String.format("%d %d %d", event.getActionMasked(), mouseMode, event.getPointerCount()));

                // requestRender();
            }});
        return true;
    }
    public float mScaleFactor = 1.0f;
}

