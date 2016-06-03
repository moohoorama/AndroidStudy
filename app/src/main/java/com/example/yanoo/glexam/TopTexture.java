package com.example.yanoo.glexam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.renderscript.Type;
import android.util.Log;

import com.example.yanoo.glexam.Util;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Yanoo on 2016. 6. 1..
 */
public class TopTexture {
    private Typeface mTypeface;
    private long     prevTime = 0;

    private int[]    glTexture = null;
    private int[]    bitmapIds = {-1, R.drawable.baby2}; /* -1은 최상단 Canvas용 Bitmap */
    private Bitmap   bitmaps[];
    private Bitmap   topBitmap = null;

    private ArrayList<Float>[] mVertexList;
    private ArrayList<Float>[] mTextureList;
    private ArrayList<Float>[] mColorList;


    public TopTexture() {
    }

    public void init(GLRenderer renderer, GL10 gl) {
        glTexture = new int[bitmapIds.length];
        gl.glGenTextures(glTexture.length, glTexture, 0);

        mTypeface = GLActivity.mSingletone.createTF();
        assert (bitmapIds[0] == -1);

        bitmaps = new Bitmap[bitmapIds.length];
        mVertexList  = new ArrayList[bitmapIds.length+1];
        mTextureList = new ArrayList[bitmapIds.length+1];
        mColorList   = new ArrayList[bitmapIds.length+1];
        for (int idx = 0; idx < bitmapIds.length; idx++) {
            if (bitmapIds[idx] == -1) {
                bitmaps[idx] = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_4444);
            } else {
                bitmaps[idx] = BitmapFactory.decodeResource(GLActivity.mSingletone.getResources(), bitmapIds[idx]);
            }
            renderer.setTexture(gl, glTexture[idx], bitmaps[idx]);
            mVertexList[idx]  = new ArrayList<Float>();
            mTextureList[idx] = new ArrayList<Float>();
            mColorList[idx]   = new ArrayList<Float>();
        }
        topBitmap = bitmaps[0];
        topBitmap.eraseColor(Color.TRANSPARENT);
    }
    public int getNullTextureID() { return bitmapIds.length; }

    public void draw(GLRenderer renderer, GL10 gl, int width, int height) {
        long prevTime = System.currentTimeMillis();
        long curTime;

        if (glTexture == null) {
            init(renderer, gl);
        }

        curTime = System.currentTimeMillis();
        Log.i("draw init", String.format("%d ms", curTime -prevTime));
        prevTime = curTime;


        for (int idx = -1; idx < bitmapIds.length; idx++) {
            FloatBuffer vb=null, tb=null, cb=null;

            if (idx == -1) {
                float[] vf = {
                        0.0f, height, 0.0f,
                        0.0f, 0.0f, 0.0f,
                        height, height, 0.0f,
                        height, height, 0.0f,
                        0.0f, 0.0f, 0.0f,
                        height, 0.0f, 0.0f,
                };
                float[] cf = {
                        1.0f,1.0f,1.0f,1.0f,
                        1.0f,1.0f,1.0f,1.0f,
                        1.0f,1.0f,1.0f,1.0f,
                        1.0f,1.0f,1.0f,1.0f,
                        1.0f,1.0f,1.0f,1.0f,
                        1.0f,1.0f,1.0f,1.0f,
                };
                float[] tf = {
                        0.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 1.0f,
                        1.0f, 1.0f,
                        0.0f, 0.0f,
                        1.0f, 0.0f,
                };
                vb = Util.setFloatBuffer(vf);
                tb = Util.setFloatBuffer(tf);
                cb = Util.setFloatBuffer(cf);

                curTime = System.currentTimeMillis();
                Log.i("draw set float buffer", String.format("%d ms", curTime -prevTime));
                prevTime = curTime;

                renderer.setTexture(gl, glTexture[0], topBitmap);

                curTime = System.currentTimeMillis();
                Log.i("draw setTexture", String.format("%d ms", curTime -prevTime));
                prevTime = curTime;
            } else {
                if (mVertexList[idx].size() <= 0) {
                    continue;
                }

                Log.i("draw", String.format("NormalTexture"));
                if (bitmapIds[idx] == -1) {
                    gl.glBindTexture(GL10.GL_TEXTURE_2D, -1);
                } else {
                    gl.glBindTexture(GL10.GL_TEXTURE_2D, glTexture[idx]);
                }
                vb = Util.setFloatBufferFromArrayList(mVertexList[idx]);
                tb = Util.setFloatBufferFromArrayList(mTextureList[idx]);
                cb = Util.setFloatBufferFromArrayList(mColorList[idx]);
                mVertexList[idx].clear();
                mTextureList[idx].clear();
                mColorList[idx].clear();
            }

            gl.glVertexPointer  (3, GL10.GL_FLOAT, 0, vb);
            gl.glColorPointer   (4, GL10.GL_FLOAT, 0, cb);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tb);
            gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

            curTime = System.currentTimeMillis();
            Log.i("draw set Pointer", String.format("%d ms", curTime -prevTime));
            prevTime = curTime;
            Log.i("draw",String.format("%d %d %d %d",vb.capacity(), vb.limit(),
                    cb.capacity(),tb.capacity()));

            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vb.capacity() / 3);

            curTime = System.currentTimeMillis();
            Log.i("draw array", String.format("%d ms", curTime -prevTime));
            prevTime = curTime;
        }
        topBitmap.eraseColor(Color.TRANSPARENT);
        curTime = System.currentTimeMillis();
        Log.i("draw eraseColor", String.format("%d ms", curTime -prevTime));
        prevTime = curTime;

    }

    float x = 50;
    float y = 50;
    public void testAct() {
        if (topBitmap == null) {
            return;
        }
        Canvas canvas = new Canvas( topBitmap );


        Paint textPaint = new Paint();
        textPaint.setTextSize( 30 );
        textPaint.setAntiAlias( true );
        textPaint.setARGB( 0xff, 0x00, 0x00, 0xff );
        textPaint.setTextAlign( Paint.Align.LEFT );
        textPaint.setTypeface(mTypeface);
//        textPaint.setTextScaleX( 0.5f );

        long curTime = System.currentTimeMillis();
        String msg = String.format("프레임 %d ms / %d fps",
                //glTexture[0],
                //Runtime.getRuntime().maxMemory()/1024/1024,
                curTime-prevTime, 1000/(curTime-prevTime));
        prevTime = curTime;
        Rect  bound = new Rect();
        textPaint.getTextBounds(msg,0,msg.length(), bound);
        textPaint.setARGB( 0xff, 0x7f, 0x7f, 0x7f );
        Log.i("Act",msg);

        textPaint.setARGB( 0xff, 0xff, 0xff, 0xff );
//        canvas.drawBitmap(bitmap_i1, null, new Rect(0,0,1024,1024), textPaint);
        canvas.drawText( msg, x+50, y+120, textPaint);

        x = (x + 1) % 50;
        y = (y + 1) % 50;
    }
}
