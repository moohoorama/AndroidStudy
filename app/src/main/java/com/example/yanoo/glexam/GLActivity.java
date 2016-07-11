package com.example.yanoo.glexam;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.HashMap;

public class GLActivity extends Activity {
    private MainGLSurfaceView glView;
    static public  GLActivity        singletone;
    static final public int FILE_SELECT_CODE = 0x12345;

    public interface IntentCallback {
        public void call(Intent intent);
    }
    private HashMap<Integer, IntentCallback> EventCallback = new HashMap<>();

    public GLActivity() {
        singletone = this;
    }

    public void onBackPressed() {
        glView.toast("back");
    }

    public  void getFileName(IntentCallback cb) {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        EventCallback.put(FILE_SELECT_CODE, cb);

        if (isKitKat) {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, FILE_SELECT_CODE);

        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent,FILE_SELECT_CODE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK &&
            EventCallback.containsKey(requestCode)) {
            IntentCallback val = EventCallback.get(requestCode);
            EventCallback.remove(requestCode);
            val.call (intent);
        }
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
