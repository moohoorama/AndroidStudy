package com.example.yanoo.glexam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Yanoo on 2016. 5. 25..
 */
public class GLRenderer implements GLSurfaceView.Renderer{
    private TopTexture mTopTexture;
    private CubeTile   mCubeTile;
    private int mWidth = 1;
    private int mHeight = 1;

    public GLRenderer(Context context) {
        mTopTexture = new TopTexture();
        mCubeTile = new CubeTile();
    }

    public CubeTile getCubeTile() {
        return mCubeTile;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        if (h == 0) {
            h = 1;
        }
        mWidth = w;
        mHeight = h;
        gl.glViewport(0, 0, w, h);         // ViewPort 리셋
        gl.glMatrixMode(GL10.GL_PROJECTION);        // MatrixMode를 Project Mode로
        gl.glLoadIdentity();                        // Matrix 리셋
        gl.glOrthof(0, w, h, 0, 1, -1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);         // Matrix를 ModelView Mode로 변환
        gl.glLoadIdentity();                        // Matrix 리셋
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Stopwatch sw = new Stopwatch();

        gl.glClearColor(0, 0, 0, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();                        // Matrix 리셋

        sw.event("clear");
        mTopTexture.testAct();
        sw.event("act");

        for (int yy = 0; yy < 32; yy ++) {
            for (int xx=0; xx < 32; xx++) {
                mCubeTile.drawShape(mWidth,mHeight,xx,yy,0,0);
            }
        }
        sw.event("tile");

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_BLEND);
        gl.glFrontFace(GL10.GL_CW);     // 시계방향 그리기 설정

        mTopTexture.draw(this, gl, mWidth, mHeight);

        gl.glDisable(GL10.GL_BLEND);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        sw.event("draw");
        Log.d("draw", sw.toString());
    }

    public void setTexture(GL10 gl, int id, Bitmap bitmap) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    }
}
