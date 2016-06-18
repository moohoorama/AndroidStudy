package com.example.yanoo.glexam.touch;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.example.yanoo.glexam.graphic.Pos;

/**
 * Created by Yanoo on 2016. 6. 5..
 */
public abstract class TouchListener {
    public class TouchEvent {
        public Pos   pos = new Pos();
        public float distance;
        public int   count;
        public int   action;
        public int   phase;
        public void copyFrom(TouchEvent te) {
            pos.copyFrom(te.pos);
            distance = te.distance;
            count    = te.count;
            action   = te.action;
            phase    = te.phase;
        }
    };

    private TouchEvent cur  = new TouchEvent();

    public TouchEvent getTouchEvent() {
        return prev;
    }

    private TouchEvent prev = new TouchEvent();

    protected int  mWidth  = 0;
    protected int  mHeight = 0;
    protected Rect mRect   = null;

    abstract public float  getScaleFactor();
    abstract public float  getDragX();
    abstract public float  getDragY();

    abstract public float  getLogX();
    abstract public float  getLogY();

    abstract public void press(TouchEvent te);

    public int    getWidth() {
        return mWidth;
    }
    public int    getHeight() {
        return mHeight;
    }

    public TouchListener(int width, int height, Rect rect) {
        mWidth  = width;
        mHeight = height;
        mRect   = rect;
    }

    public void   setScreenSize(int width, int height) {
        mWidth  = width;
        mHeight = height;
    }

    protected void setZone(Rect rect) {
        mRect = rect;
    }

    protected float   getPointerDistance(Pos midPos, final MotionEvent event) {
        int   i, posCount = 0;
        float distance = 0.0f;
        Pos pos = new Pos();

        for (i = 0; i < event.getPointerCount(); i++) {
            if (mRect == null || mRect.contains((int)event.getX(i),(int)event.getY(i))) {
                distance += (float)Math.hypot(event.getX(i) - midPos.x,event.getY(i) - midPos.y);
                posCount ++;
            }
        }
        if (posCount > 0) {
            distance /= posCount;
        }

        return distance;
    }

    protected Pos     getMidPosition(final MotionEvent event) {
        int i, posCount = 0;
        Pos pos = new Pos();

        for (i = 0; i < event.getPointerCount(); i++) {
            if (mRect == null || mRect.contains((int)event.getX(i),(int)event.getY(i))) {
                pos.x+=event.getX(i);
                pos.y+=event.getY(i);
                posCount ++;
            }
        }
        if (posCount > 0) {
            pos.x/= posCount;
            pos.y/= posCount;
        }
        return pos;
    }

    protected int getPointerCount(final MotionEvent event) {
        int i, posCount=0;

        for (i=0; i < event.getPointerCount(); i++) {
            if (mRect == null || mRect.contains((int)event.getX(i),(int)event.getY(i))) {
                posCount++;
            }
        }
        return posCount;
    }

    public void touch(MotionEvent event) {
        cur.pos     = getMidPosition(event);
        cur.distance= getPointerDistance(cur.pos, event);
        cur.count   = getPointerCount(event);
        cur.action  = event.getActionMasked();
    }

    public void act() {
        cur.phase = -1;

        if (cur.count > 0) {
            if (cur.action == MotionEvent.ACTION_DOWN ||
                    (cur.action & MotionEvent.ACTION_POINTER_DOWN) == MotionEvent.ACTION_POINTER_DOWN) {
                cur.phase=0;
            } else if (cur.action == MotionEvent.ACTION_MOVE) {
                cur.phase=1;
            } else {
                cur.phase=2;
            }
        }
        press(cur);

        prev.copyFrom(cur);
        if (cur.phase == 2) {
            cur.count=0;
        }
    }
}
