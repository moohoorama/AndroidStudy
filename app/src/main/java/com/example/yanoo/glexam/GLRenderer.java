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
    private static final int      TEX_NUM = 4;

    private              int[]    texture = new int[TEX_NUM];
    private              Context  mContext;
    private              CubeTile mCubeTile = new CubeTile();

    public GLRenderer(Context context) {
        super();
        mContext = context;
    }

    public CubeTile getCubeTile() {
        return mCubeTile;
    }

    public void init(GL10 gl, Context context) {
        gl.glGenTextures(TEX_NUM, texture, 0);

        int[] bitmaps = {
                R.drawable.baby,
                R.drawable.baby2,
                R.drawable.baby3,
                R.drawable.baby4,
        };
        int i;

        Log.i("init", "load bitmap");

        for (i = 0 ; i < bitmaps.length; i++) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[i]);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), bitmaps[i]);
//                    BitmapFactory.decodeStream(context.getResources().openRawResource(bitmaps[i]));
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
            Log.i("load bitmap", String.format("%d" , i));
        }
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i("onSurfaceCreate", "init");

        init(gl, mContext);
        // Do nothing special.
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        if(h == 0) {
            h = 1;
        }
        gl.glViewport(0, 0, w, h);         // ViewPort 리셋
        gl.glMatrixMode(GL10.GL_PROJECTION);        // MatrixMode를 Project Mode로
        gl.glLoadIdentity();                        // Matrix 리셋
        gl.glOrthof(0, w, h, 0, 1, -1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);         // Matrix를 ModelView Mode로 변환
        gl.glLoadIdentity();                        // Matrix 리셋
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(mRed, mGreen, mBlue, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();                        // Matrix 리셋

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glFrontFace(GL10.GL_CW);     // 시계방향 그리기 설정

        mCubeTile.draw(gl);
//        setVerticesAndDraw(gl);
//        Log.i("!!", "draw");
    }


    public void setVerticesAndDraw( GL10 gl) {
        FloatBuffer m_vertexBuffer = null;

        float vertices[] = { 0.0f, 0.0f, 0.0f,
                240.0f, 800.0f, 0.0f,
                480.0f, 0.0f, 0.0f, };
        m_vertexBuffer= Util.setFloatBuffer(vertices);

        /*
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_vertexBuffer);
        gl.glColor4f(mGreen,mRed,mBlue, 0.9f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        */
        FloatBuffer tb= null, vb = null;
        float[] vf = {
              0.0f, 100.0f, 0.0f,
              0.0f,   0.0f, 0.0f,
            100.0f, 100.0f, 0.0f,
            100.0f,   0.0f, 0.0f,
        };
        float[] tf = {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
        };
        tb = Util.setFloatBuffer(tf);
        vb = Util.setFloatBuffer(vf);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glFrontFace(GL10.GL_CW);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]); /* background */
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tb);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vb);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vf.length/3);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }


    public void setColor(float r, float g, float b) {
        mRed = r;
        mGreen = g;
        mBlue = b;
    }

    private float mRed;
    private float mGreen;
    private float mBlue;
}
