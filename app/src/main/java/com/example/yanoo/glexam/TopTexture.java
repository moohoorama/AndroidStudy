package com.example.yanoo.glexam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Yanoo on 2016. 6. 1..
 */
public class TopTexture {
    static public TopTexture sSingletone;
    private Typeface mTypeface;
    private long     prevTime = 0;

    private int[]    glTexture = null;
    private int[]    bitmapIds = {-1, R.drawable.baby3}; /* -1은 최상단 Canvas용 Bitmap */
    private Bitmap   bitmaps[];
    private Bitmap   topBitmap = null;

    private float    depth = 0.0f;

    private ArrayList<float[]>[] mVertexList;
    private ArrayList<float[]>[] mTextureList;
    private ArrayList<float[]>[] mColorList;

    public TopTexture() {
        sSingletone = this;
    }

    public void init(GLRenderer renderer, GL10 gl) {
        glTexture = new int[bitmapIds.length];
        gl.glGenTextures(glTexture.length, glTexture, 0);

        mTypeface = GLActivity.mSingletone.createTF();
        assert (bitmapIds[0] == -1);

        bitmaps = new Bitmap[bitmapIds.length];
        mVertexList  = new ArrayList[bitmapIds.length];
        mTextureList = new ArrayList[bitmapIds.length];
        mColorList   = new ArrayList[bitmapIds.length];
        for (int idx = 0; idx < bitmapIds.length; idx++) {
            if (bitmapIds[idx] == -1) {
                bitmaps[idx] = Bitmap.createBitmap(256,256, Bitmap.Config.ARGB_4444);
                topBitmap = bitmaps[idx];
            } else {
                bitmaps[idx] = BitmapFactory.decodeResource(GLActivity.mSingletone.getResources(), bitmapIds[idx]);
            }
            renderer.setTexture(gl, glTexture[idx], bitmaps[idx]);
            mVertexList[idx]  = new ArrayList<float[]>();
            mTextureList[idx] = new ArrayList<float[]>();
            mColorList[idx]   = new ArrayList<float[]>();
        }
        topBitmap.eraseColor(Color.TRANSPARENT);
        depth = -0.001f;
    }

    public float getDepth(){return depth;}

    public boolean addTextureRect(int idx, RectF vertex, RectF texture, float r, float g, float b, float a) {
        float[] vf = {
                vertex.left,  vertex.bottom, depth,
                vertex.left,  vertex.top,    depth,
                vertex.right, vertex.bottom, depth,
                vertex.right, vertex.bottom, depth,
                vertex.left,  vertex.top,    depth,
                vertex.right, vertex.top,    depth,
        };
        float[] cf = {
                r,g,b,a,
                r,g,b,a,
                r,g,b,a,
                r,g,b,a,
                r,g,b,a,
                r,g,b,a,
        };
        float[] tf = {
                texture.left,  texture.bottom,
                texture.left,  texture.top,
                texture.right, texture.bottom,
                texture.right, texture.bottom,
                texture.left,  texture.top,
                texture.right, texture.top,
        };

        return addTexture(idx, vf, tf, cf);
    }

    public boolean addTexture(int idx, float[] vertex, float[] texture, float[] color) {
        if (idx < 0 || idx >= bitmapIds.length || topBitmap == null) {
            return false;
        }

        mVertexList[idx].add(vertex);
        mTextureList[idx].add(texture);
        mColorList[idx].add(color);

        depth -= 0.0001;
        return true;
    }

    public void draw(GLRenderer renderer, GL10 gl, int width, int height) {
        int  texture_idx = -1;
        Stopwatch stopwatch = new Stopwatch();

        if (glTexture == null) {
            init(renderer, gl);
        }
        stopwatch.event("init");

        for (int idx = 0; idx <= bitmapIds.length; idx++) {
            FloatBuffer vb=null, tb=null, cb=null;

            if (idx == bitmapIds.length) {
                float[] vf = {
                        0.0f,   height, depth,
                        0.0f,   0.0f,   depth,
                        height, height, depth,
                        height, height, depth,
                        0.0f,   0.0f,   depth,
                        height, 0.0f,   depth,
                };
                float[] cf = {
                        1.0f,1.0f,1.0f,1.00f,
                        1.0f,1.0f,1.0f,1.00f,
                        1.0f,1.0f,1.0f,1.00f,
                        1.0f,1.0f,1.0f,1.00f,
                        1.0f,1.0f,1.0f,1.00f,
                        1.0f,1.0f,1.0f,1.00f,
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

                renderer.setTexture(gl, glTexture[0], topBitmap);
                texture_idx = glTexture[0];
            } else {
                if (mVertexList[idx].size() <= 0) {
                    continue;
                }

                if (bitmapIds[idx] == -1) {
                    texture_idx = -1;
                } else {
                    texture_idx = glTexture[idx];
                }
                vb = Util.setFloatBufferFromList(mVertexList[idx]);
                tb = Util.setFloatBufferFromList(mTextureList[idx]);
                cb = Util.setFloatBufferFromList(mColorList[idx]);
                mVertexList[idx].clear();
                mTextureList[idx].clear();
                mColorList[idx].clear();
            }
            stopwatch.event(String.format("Prepare %d", idx));

            gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_idx);
            if (texture_idx == -1) {
                gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                gl.glDisable(GL10.GL_TEXTURE_2D);
            } else {
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tb);
            }
            gl.glVertexPointer  (3, GL10.GL_FLOAT, 0, vb);
            gl.glColorPointer   (4, GL10.GL_FLOAT, 0, cb);

            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vb.capacity() / 3);

            if (texture_idx == -1) {
                gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                gl.glEnable(GL10.GL_TEXTURE_2D);
            }
            stopwatch.event(String.format("draw %d", idx));
        }
        topBitmap.eraseColor(Color.TRANSPARENT);
        depth = -0.0001f;
        Log.d("stopwatch", stopwatch.toString());
    }

    float x = 50;
    float y = 50;
    public void testAct() {
        if (topBitmap == null) {
            return;
        }
        Canvas canvas = new Canvas( topBitmap );


        Paint textPaint = new Paint();
        textPaint.setTextSize( 10 );
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
        canvas.drawRect(200,100,700,600,textPaint);

        textPaint.setARGB( 0xff, 0xff, 0xff, 0xff );
//        canvas.drawBitmap(bitmap_i1, null, new Rect(0,0,1024,1024), textPaint);
        canvas.drawText( msg, x+50, y+120, textPaint);

        addTextureRect(1, new RectF(0.0f,0.0f,400.0f,400.0f), new RectF(0.0f,0.0f,1.0f,1.0f), 255,255,255,255);
        addTextureRect(1, new RectF(100.0f,100.0f,500.0f,500.0f), new RectF(0.0f,0.0f,1.0f,1.0f), 255,255,255,255);
        addTextureRect(0, new RectF(200.0f,200.0f,600.0f,600.0f), new RectF(0.0f,0.0f,1.0f,1.0f), 255,255,255,255);
        addTextureRect(1, new RectF(300.0f,300.0f,700.0f,700.0f), new RectF(0.0f,0.0f,1.0f,1.0f), 255,255,255,255);
        x = (x + 1) % 50;
        y = (y + 1) % 50;
    }
}
