package com.example.yanoo.glexam.touch;

import com.example.yanoo.glexam.graphic.Pos;

/**
 * Created by Yanoo on 2016. 6. 5..
 */
public class DragTouchListener extends TouchListener {

    private float mScaleFactor=1.0f;

    private Pos   mScroll      = new Pos();
    private Pos   mClick       = new Pos();
    private Pos   mLogical     = new Pos();

    private Pos   mScrollBegin = new Pos();
    private Pos   mPressBegin  = new Pos();
    private float mBeginDistance = 0;

    public DragTouchListener(int width, int height) {
        super(width, height, null);
    }

    public float  getScaleFactor() { return mScaleFactor;}
    public float  getDragX() { return mScroll.x;}
    public float  getDragY() { return mScroll.y;}

    public float  getLogX() { return mScroll.x + (mWidth/2)/mScaleFactor;}
    public float  getLogY() { return mScroll.y + (mHeight/2)/mScaleFactor;}

    public float  getClickX() { return mClick.x;}
    public float  getClickY() { return mClick.y;}

    public void press(TouchEvent te) {
        if (te.count == 1) {
            switch (te.phase) {
                case 0:
                    mScrollBegin.copyFrom(mScroll);
                    mPressBegin.copyFrom(te.pos);
                    break;
                case 1:
                    mScroll.x = mScrollBegin.x + ((mPressBegin.x - te.pos.x) / mScaleFactor);
                    mScroll.y = mScrollBegin.y + ((mPressBegin.y - te.pos.y) / mScaleFactor);
                    break;
                case 2:
                    mLogical.x=(int) (mScroll.x + (mClick.x) / mScaleFactor);
                    mLogical.y=(int) (mScroll.y + (mClick.y) / mScaleFactor);
                    break;
            }
        } else {
            switch (te.phase) {
                case 0:
                    break;
                case 1:
                    mScroll.x += te.pos.x/mScaleFactor;
                    mScroll.y += te.pos.y/mScaleFactor;
                    mScaleFactor += (te.distance - mBeginDistance) / 200.0f;
                    mScaleFactor=Math.max(1.0f, Math.min(5.0f, mScaleFactor));
                    mScroll.x -= te.pos.x/mScaleFactor;
                    mScroll.y -= te.pos.y/mScaleFactor;
                    break;
                case 2:
                    break;
            }
            mBeginDistance=te.distance;
        }
    }
}
