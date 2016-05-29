package com.example.yanoo.glexam;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

public class GLActivity extends Activity {
    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("activityOnCreate", "Create");

        mGLView = new MainGLSurfaceView(this);
        setContentView(mGLView);
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
