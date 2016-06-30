package com.example.yanoo.glexam.touch;

import android.graphics.Rect;
import android.util.Log;

import com.example.yanoo.glexam.graphic.Pos;

/**
 * Created by Yanoo on 2016. 6. 5..
 */
public class NormalTouchListener extends TouchListener {
    public NormalTouchListener(int width, int height) {
        super(width, height, null);
    }
    public NormalTouchListener(int width, int height, Rect rect) {
        super(width, height, rect);
    }

    public float  getScaleFactor() { return mScaleFactor;}
    public float  getDragX() { return mScroll.x;}
    public float  getDragY() { return mScroll.y;}

    public float  getLogX() { return mLogical.x;}
    public float  getLogY() { return mLogical.y;}

    public float  getClickX() { return mClick.x;}
    public float  getClickY() { return mClick.y;}

    private float mScaleFactor=1.0f;

    public  Pos   mScroll      = new Pos();
    private Pos   mClick       = new Pos();
    private Pos   mLogical     = new Pos();
    public Pos    mPressBegin  = new Pos();
    private float mBeginDistance = 0;

    public void press(TouchEvent te) {
        if (te.count == 1) {
            switch (te.phase) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
            }
            mLogical.x = (int)(mScroll.x + (te.pos.x)/mScaleFactor);
            mLogical.y = (int)(mScroll.y + (te.pos.y)/mScaleFactor);
            mClick.x   = te.pos.x;
            mClick.y   = te.pos.y;
        } else {
            if (te.count == 3) {

            } else {
                switch (te.phase) {
                    case 0:
                        mPressBegin.copyFrom(te.pos);
                        break;
                    case 1:
                        mScroll.x+=(mPressBegin.x - te.pos.x) / mScaleFactor;
                        mScroll.y+=(mPressBegin.y - te.pos.y) / mScaleFactor;
                        mPressBegin.copyFrom(te.pos);
                        break;
                    case 2:
                        break;
                }
                mBeginDistance=te.distance;
            }
        }
    }

}
