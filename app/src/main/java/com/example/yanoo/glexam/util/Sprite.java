package com.example.yanoo.glexam.util;

import android.graphics.RectF;

import com.example.yanoo.glexam.graphic.TextureManager;

/**
 * Created by kyw on 2016-07-04.
 */
public class Sprite {
    public int   bitmapIdx;
    public float left,top,right,bottom;
    public RectF rectF;
    public float txPoints[];

    public Sprite(int idx, float left, float top, float right,float bottom) {
        reset(idx,left,top,right,bottom);
    }

    public Sprite(int idx, int left, int top, int right,int bottom) {
        reset(idx,
            (left + 0.5f) / 1024.0f,
            (top + 0.5f) / 1024.0f,
            (right - 0.5f) / 1024.0f,
            (bottom - 0.5f) / 1024.0f);
    }

    public void reset(int idx, float left, float top, float right,float bottom) {
        this.bitmapIdx = idx;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.rectF = new RectF(left,top,right,bottom);
        this.txPoints = TextureManager.dot4To4Point(this.rectF);
    }

    public RectF getRectF() {
        return rectF;
    }

    public float getX(int i) {
        return (i == 0) ? left : right;
    }
    public float getY(int i) {
        return (i == 0) ? top : bottom;
    }

    public static Sprite getTileTexture(int idx) {
        int    offset = idx*64;
        return new Sprite(1,
                offset % 1024, (offset/1024)*64,
                (offset % 1024)+64, (offset/1024)*64+64);
    }
    public static Sprite getTileObject(int idx) {
        int    offset = idx*128 + 1024*4;
        return new Sprite(1,
                offset % 1024, (offset/1024)*128,
                (offset % 1024)+128, (offset/1024)*128+128);
    }

}
