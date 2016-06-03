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
    private int mWidth = 1;
    private int mHeight = 1;

    public GLRenderer(Context context) {
        mTopTexture = new TopTexture();
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
        gl.glClearColor(0, 0, 0, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();                        // Matrix 리셋

        mTopTexture.testAct();

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
    }

    public void setTexture(GL10 gl, int id, Bitmap bitmap) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    }

    public void drawRect(GL10 gl, int id, float x, float y, float w, float h, float r, float g, float b) {
        FloatBuffer vb=null, cb=null, tb=null;
        float[] vf = {
                x, y + h, 0.0f,
                x, y, 0.0f,
                x + w, y + h, 0.0f,
                x + w, y, 0.0f,
        };
        float[] cf = {
                /* ld, lt, rd rt */
                r*0.9f, g*0.9f, b*0.9f, 1.0f,
                r*1.0f, g*1.0f, b*1.0f, 1.0f,
                r*0.7f, g*0.7f, b*0.7f, 1.0f,
                r*0.8f, g*0.8f, b*0.8f, 1.0f,
        };
        float[] tf = {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
        };
        vb = Util.setFloatBuffer(vf);
        tb = Util.setFloatBuffer(tf);
        cb = Util.setFloatBuffer(cf);


        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vb);
        gl.glColorPointer  (4, GL10.GL_FLOAT, 0, cb);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tb);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id); /* background */

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vf.length / 3);
    }
}
