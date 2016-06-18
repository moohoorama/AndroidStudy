package com.example.yanoo.glexam;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.yanoo.glexam.graphic.GLRenderer;

public class MainGLSurfaceView extends GLSurfaceView {
    public static MainGLSurfaceView sSingletone;

    Activity             activity;
    GLRenderer           renderer;
    MediaPlayer          mediaPlayer;
    SoundPool            mSoundPool;
    int[]                mSoundIdx;
    DisplayMetrics       mMetrics = new DisplayMetrics();
    Handler              handler = new Handler(Looper.getMainLooper());

    public void toast(final String msg) {
        handler.post(new Runnable(){
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
/*
                Toast toast=Toast.makeText(activity, msg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*/
            }});
    }

    public String getBasePath() {
        return activity.getFilesDir().getPath().toString();
    }

    public MainGLSurfaceView(Activity activity) {
        super(activity);
        activity.getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        this.activity = activity;
        sSingletone   = this;

        if (GLRenderer.singletone == null) {
            renderer=new GLRenderer(activity);
        } else {
            renderer=GLRenderer.singletone;
        }
        setRenderer(renderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

//        mMediaPlayer = MediaPlayer.create(context, R.raw.kalimba);
//        mMediaPlayer.start();
        mSoundPool =  new  SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        mSoundIdx = new int[1];
//        mSoundIdx[0] = mSoundPool.load(context, R.raw.sword, 0);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        renderer.getGameLogic().getTouchListener().setScreenSize(w,h);
    }

    public boolean onTouchEvent(final MotionEvent event) {
        queueEvent(new Runnable(){
            public void run() {
                renderer.getGameLogic().getTouchListener().touch(event);
            }});
        return true;
    }
}

