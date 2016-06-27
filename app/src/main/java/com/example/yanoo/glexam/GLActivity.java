package com.example.yanoo.glexam;

import android.app.Activity;
import android.os.Bundle;

public class GLActivity extends Activity {
    private MainGLSurfaceView glView;
    static public  GLActivity        singletone;

    public GLActivity() {
        singletone = this;
    }

    public void onBackPressed() {
        glView.toast("back");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glView= new MainGLSurfaceView(this);
        setContentView(glView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        glView.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        glView.onResume();

    }
}
