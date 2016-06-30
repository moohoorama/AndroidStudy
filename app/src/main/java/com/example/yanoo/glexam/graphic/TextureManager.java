package com.example.yanoo.glexam.graphic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.yanoo.glexam.R;
import com.example.yanoo.glexam.util.Stopwatch;
import com.example.yanoo.glexam.util.Util;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * All Texture Management
 * Created by Yanoo on 2016. 6. 1..
 */
public class TextureManager {
    private Context      context;
    private Typeface     typeface;
    private CanvasBitmap canvasBitmap;

    private int[]    bitmapIds = {-1, R.drawable.baby2, R.drawable.baby3, R.drawable.baby4}; /* -1은 최상단 CanvasBitmap */
    private int[]    glTexture = new int[bitmapIds.length];

    private int      runSequence = 0;
    private float    depth = 0.0f;

    private ArrayList<float[]>[] mVertexList;
    private ArrayList<float[]>[] mTextureList;
    private ArrayList<float[]>[] mColorList;

    public float    getDepth(){return depth;}
    public Typeface getTypeface(){return typeface;}
    public int getRunSequence() {return runSequence;}


    public TextureManager(Context _context) {
        context = _context;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/NanumBarunGothic.ttf");
        mVertexList=new ArrayList[bitmapIds.length+1];
        mTextureList=new ArrayList[bitmapIds.length+1];
        mColorList=new ArrayList[bitmapIds.length+1];
        canvasBitmap = new CanvasBitmap(this);
    }

    public boolean isPrepared() {
        return depth < 0.0f;
    }

    public void init(GLRenderer renderer, GL10 gl) {
        Bitmap   bitmaps[] = new Bitmap[bitmapIds.length];

        gl.glGenTextures(glTexture.length, glTexture, 0);

        for (int idx = 0; idx < bitmapIds.length; idx++) {
            if (bitmapIds[idx] == -1) {
                bitmaps[idx] =
                    canvasBitmap.getBitmap();
            } else {
                bitmaps[idx] = BitmapFactory.decodeResource(context.getResources(), bitmapIds[idx]);
                setTexture(gl, glTexture[idx], bitmaps[idx]);
                bitmaps[idx].recycle();
            }
        }
        for (int idx = 0; idx <= bitmapIds.length; idx++) {
            mVertexList[idx]  =new ArrayList<float[]>();
            mTextureList[idx] =new ArrayList<float[]>();
            mColorList[idx]   =new ArrayList<float[]>();
        }
        depth = -0.001f;
    }

    public void refresh(GL10 gl) {
        Bitmap   bitmaps[] = new Bitmap[bitmapIds.length];

        for (int idx = 0; idx < bitmapIds.length; idx++) {
            if (bitmapIds[idx] == -1) {
                canvasBitmap.refresh();
            } else {
                bitmaps[idx] = BitmapFactory.decodeResource(context.getResources(), bitmapIds[idx]);
                setTexture(gl, glTexture[idx], bitmaps[idx]);
                bitmaps[idx].recycle();
            }
        }
    }

    public RectF getText(String msg) {
        return canvasBitmap.getText(msg);
    }

    public boolean addText( int left, int top, int right, int bottom, String msg, TColor tc) {
        RectF texture = getText(msg);
        if (texture == null) {
            return false;
        }
        if (right <= left || bottom <= top || texture.width() == 0 || texture.height() == 0) {
            return false;
        }
        int   size = ((bottom - top));
        int   estimatedWidth = (int)(size*texture.width()/texture.height());
        if (estimatedWidth > right-left) {
            float ratio =
                    (float)((right - left)*texture.height()) / (float)(texture.width()*(bottom-top));
            if (ratio > 1.0f) {
            /* 가로 줄이기 */
                float pad=((right - left) - (bottom - top) * texture.width() / texture.height()) / 2;
                left+=pad;
                right-=pad;
            } else {
                float pad=((bottom - top) - (right - left) * texture.height() / texture.width()) / 2;
                top+=pad;
                bottom-=pad;
            }
            addTextureRect(0, new RectF(left, top, right, bottom), texture, tc);
        } else {
            left = (int)((left+right)/2 - estimatedWidth/2);
            top  = (int)((top+bottom)/2 - size/2);

            addTextureRect(0, new RectF(left, top, left+estimatedWidth, top+size), texture, tc);
        }
        return true;
    }
    public boolean addTextureLine(int idx, RectF vertex, RectF texture, float thickness, TColor tc) {
        float[] tf = dot4To6Point(2, texture);
        float[] cf = rgbToPoint(tc,6);
        float   x = 1;
        float   y = 1;
        if (vertex.top == vertex.bottom) {
            /* horizonal line */
            x = 0;
            y = thickness;
        } else {
            float gradient=(vertex.right - vertex.left) / (vertex.bottom - vertex.top);
            float hypotenuse=(float) Math.hypot(1.0, (double)gradient);

            x = (1)*thickness/hypotenuse;
            y = gradient*thickness/hypotenuse;
        }
        float[] vf = {
                vertex.left -x, vertex.top  + y, depth,
                vertex.left +x, vertex.top  - y, depth,
                vertex.right-x, vertex.bottom+y, depth,
                vertex.right-x, vertex.bottom+y, depth,
                vertex.left +x, vertex.top  - y, depth,
                vertex.right+x, vertex.bottom-y, depth,
        };
        return addTexture(idx, vf, tf, cf);
    }
    public boolean addTextureRect(int idx, RectF vertex, RectF texture, TColor tc) {
        float[] vf = dot4To6Point(3, vertex);
        float[] tf = dot4To6Point(2, texture);
        float[] cf = rgbToPoint(tc,6);
        return addTexture(idx, vf, tf, cf);
    }
    public void addRoundedRectangle(int left, int top, int right, int bottom,
                                    int delication,
                                    int radius,
                                    int textureIdx, RectF textureRect,
                                    TColor tc) {
        if (delication <= 0) {
            delication = 4;
        }
        int     pointCount = 4*(delication);
        float   vertex[]   = new float[pointCount*9];
        float   texture[]  = new float[pointCount*6];
        float   colors[]   = rgbToPoint(tc,pointCount*3);
        float   locs[]     = new float[(pointCount+1)*2];
        float   baseLoc[]  = {
                left, top,
                right,top,
                right,bottom,
                left, bottom,
                (left + right)/2.0f,      (top + bottom)/2.0f};
        float   size = (bottom + right - (top + left));
        int     i;
        int     j;
        if (radius <= 0) {
            radius = Math.min(right-left, bottom-top)/5;
        }

        for (j = 0; j < 4; j++) {
            int   base = j*2*delication;
            for (i = 0; i < delication; i++) {
                locs[base+i*2  ] = baseLoc[j*2] + Util.getRoundX(j, i, delication) * radius;
                locs[base+i*2+1] = baseLoc[j*2+1] + Util.getRoundY(j, i, delication) * radius;
            }
        }

        for (i=0; i < pointCount; i++) {
            j = (i + 1) % pointCount;

            vertex[i*9    ] = locs[i*2];
            vertex[i*9 + 1] = locs[i*2+1];
            vertex[i*9 + 2] = depth;

            vertex[i*9 + 3] = locs[j*2];
            vertex[i*9 + 4] = locs[j*2+1];
            vertex[i*9 + 5] = depth;

            vertex[i*9 + 6] = baseLoc[8];;
            vertex[i*9 + 7] = baseLoc[9];
            vertex[i*9 + 8] = depth;

        }
        if (textureIdx != -1 && textureRect != null) {
            for (i = 0; i < pointCount*3; i++) {
                texture[i*2 + 0] = textureRect.left + (vertex[i*3 + 0] - left) * textureRect.width()  / (right - left);
                texture[i*2 + 1] = textureRect.top  + (vertex[i*3 + 1] - top)  * textureRect.height() / (bottom - top);
            }
        } else {
            switch (textureIdx) {
            case -2:
                for (i=0; i < pointCount; i++) {
                    for (j = 0; j < 2; j++) {
                        colors[i*12+j*4+0] = tc.r*1.2f;
                        colors[i*12+j*4+1] = tc.g*1.2f;
                        colors[i*12+j*4+2] = tc.b*1.2f;
//                        colors[i*12+j*4+3] = tc.a *  1.0f;
                    }
//                    colors[i*12+ 9] = tc.r *  0.8f;
//                    colors[i*12+10] = tc.g *  0.8f;
//                    colors[i*12+11] = tc.b *  0.8f;
                }
                break;
            case -3:
                for (i=0; i < pointCount*3; i++) {
                    colors[i*4+0] = tc.r *  (1.2f - 0.2f*(vertex[i*3]-left)/ (right-left));
                    colors[i*4+1] = tc.g *  (1.2f - 0.2f*(vertex[i*3]-left)/ (right-left));
                    colors[i*4+2] = tc.b *  (1.2f - 0.2f*(vertex[i*3]-left)/ (right-left));
                }
                break;
            case -4:
                for (i=0; i < pointCount*3; i++) {
                    colors[i*4+0] = tc.r *  (1.2f - 0.2f*(vertex[i*3+1]-top)/ (bottom-top));
                    colors[i*4+1] = tc.g *  (1.2f - 0.2f*(vertex[i*3+1]-top)/ (bottom-top));
                    colors[i*4+2] = tc.b *  (1.2f - 0.2f*(vertex[i*3+1]-top)/ (bottom-top));
                }
                break;
            }
            textureIdx = -1;
        }
        this.addTexture(textureIdx, vertex, texture, colors);
    }

    public boolean addTexture(int idx, float[] vertex, float[] texture, float[] color) {
        if (idx == -1) {
            idx = bitmapIds.length;
        }
        if (idx < 0 || idx > bitmapIds.length || !isPrepared()) {
            return false;
        }

        mVertexList[idx].add(vertex);
        if (texture != null) {
            mTextureList[idx].add(texture);
        }
        mColorList[idx].add(color);

        depth -= 0.0001;
        return true;
    }

    public void setTexture(GL10 gl, int id, Bitmap bitmap) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    }
    public float[] rgbToPoint(TColor tc, int count) {
        return rgbToPoint(tc.r,tc.g,tc.b,tc.a,count);
    }
    public float[] rgbToPoint(float r, float g, float b, float a, int count) {
        float[] cf = new float[count*4];
        for (int i = 0; i < count; i++) {
            cf[i*4  ] = r;
            cf[i*4+1] = g;
            cf[i*4+2] = b;
            cf[i*4+3] = a;
        };
        return cf;
    }
    public float[] dot4To6Point(int p_level, RectF rect) {
        if (rect == null) {
            return null;
        }
        if (p_level == 2) {
            float []ret = {
                    rect.left,  rect.bottom,
                    rect.left,  rect.top,
                    rect.right, rect.bottom,
                    rect.right, rect.bottom,
                    rect.left,  rect.top,
                    rect.right, rect.top,
            };
            return ret;
        }
        float []ret = {
                rect.left,  rect.bottom, depth,
                rect.left,  rect.top,    depth,
                rect.right, rect.bottom, depth,
                rect.right, rect.bottom, depth,
                rect.left,  rect.top,    depth,
                rect.right, rect.top,    depth,
        };

        return ret;
    }

    public void draw(GLRenderer renderer, GL10 gl, int width, int height) {
        int  texture_idx = -1;
        Stopwatch stopwatch = new Stopwatch();

        if (!isPrepared()) {
            init(renderer, gl);
        }
        stopwatch.event("init");

        for (int idx = bitmapIds.length; idx >= 0; idx--) {
            FloatBuffer vb, tb, cb;

            if (mVertexList[idx].size() <= 0) {
                continue;
            }

            if (idx == bitmapIds.length) { /* null texture */
//                vb = Util.setFloatBuffer(dot4To6Point(3, new RectF(0.0f,0.0f,height,height)));
//                tb = Util.setFloatBuffer(dot4To6Point(2, new RectF(0.0f,0.0f,1.0f,1.0f)));
//                cb = Util.setFloatBuffer(rgbToPoint(1.0f,1.0f,1.0f,1.0f, 6));
                texture_idx = -1;
            } else {
                if (bitmapIds[idx] == -1) {
                    canvasBitmap.setTexture(gl, glTexture[idx]);
                }
                texture_idx=glTexture[idx];
            }
            vb = Util.setFloatBufferFromList(mVertexList[idx]);
            tb = Util.setFloatBufferFromList(mTextureList[idx]);
            cb = Util.setFloatBufferFromList(mColorList[idx]);
            mVertexList[idx].clear();
            mTextureList[idx].clear();
            mColorList[idx].clear();
            stopwatch.event(String.format("Prepare %d", idx));

            if (texture_idx == -1) {
                gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                gl.glDisable(GL10.GL_TEXTURE_2D);
            } else {
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tb);
            }
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture_idx);
            gl.glVertexPointer  (3, GL10.GL_FLOAT, 0, vb);
            gl.glColorPointer   (4, GL10.GL_FLOAT, 0, cb);

            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vb.capacity() / 3);

            if (texture_idx == -1) {
                gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                gl.glEnable(GL10.GL_TEXTURE_2D);
            }
            stopwatch.event(String.format("draw %d", idx));
        }
        depth = -0.0001f;
        Log.d("stopwatch", stopwatch.toString());
        runSequence++;
    }
}
