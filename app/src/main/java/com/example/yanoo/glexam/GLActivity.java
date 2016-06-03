package com.example.yanoo.glexam;

import android.app.Activity;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

public class GLActivity extends Activity {
    private GLSurfaceView mGLView;
    static public GLActivity   mSingletone;
    static private Typeface      mTF;

    public GLActivity() {
        mSingletone = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new MainGLSurfaceView(this);
        setContentView(mGLView);
    }

    public Typeface createTF() {
        return Typeface.createFromAsset(getAssets(), "fonts/NanumBarunGothic.ttf");
    }

    @Override
    protected void onPause() {
        super.onPause();

        mGLView.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mGLView.onResume();

    }
}
