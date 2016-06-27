package com.example.yanoo.glexam.graphic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;

import java.util.HashMap;
import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Yanoo on 2016. 6. 16..
 */
public class CanvasBitmap {
    private static final int       FontSize = 48;
    private static final int       BaseSize = 64;
    private static final int       TextureSize = 1024;
    private TextureManager         tm;
    private Bitmap                 bitmap = Bitmap.createBitmap(TextureSize,TextureSize, Bitmap.Config.ARGB_4444);
    private boolean                useTexture = false;
    private boolean                dirty = false;
    private boolean                needReset = false;
    private int                    x;
    private int                    y;
    private HashMap<String, RectF> msgMap = new HashMap<String, RectF>();

    public CanvasBitmap(TextureManager tm) {
        this.tm = tm;
        needReset = true;
        useTexture = false;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void  refresh() {
        bitmap.eraseColor(Color.TRANSPARENT);
        //Canvas canvas = new Canvas( bitmap );
        //canvas.drawColor(0, Display.Mode.CLEAR);

        msgMap.clear();
        x = 0;
        y = 0;
        Log.i("CanvasBitmap", "reset!");
        needReset = false;
    }

    public void  setTexture(GL10 gl, int idx) {
        if (dirty) {
            tm.setTexture(gl,idx,bitmap);
        }
        dirty      = false;
        useTexture = false;
    }

    public  RectF getText(String msg) {
        if (!useTexture && needReset) {
            refresh();
        }
        useTexture = true;
        if (msgMap.containsKey(msg)) {
            return msgMap.get(msg);
        }
        RectF ret = drawText(msg);
        msgMap.put(msg, ret);
        return ret;
    }

    private RectF drawText(String msg) {
        Typeface typeface = tm.getTypeface();

        Canvas canvas = new Canvas( bitmap );
        Paint textPaint = new Paint();
        textPaint.setTextSize( FontSize );
        textPaint.setAntiAlias( true );
        textPaint.setARGB( 0xff, 0xff, 0xff, 0xff );
        textPaint.setTextAlign( Paint.Align.LEFT );
        textPaint.setTypeface(typeface);
        textPaint.setStrokeWidth(0);
        textPaint.setStyle(Paint.Style.FILL);

        Rect bound = new Rect();
        textPaint.getTextBounds(msg,0,msg.length(), bound);
        if (x + bound.right - bound.left >= TextureSize) { /* carriage return*/
            x = 0;
            y += BaseSize;
            if (y + BaseSize >= TextureSize/2) {
                needReset=true;
            }
            if (y +BaseSize >= TextureSize) {
                return null;
            }
        }
        /* left,top쪽 방향으로 조금 오버해서 그릴 수도 있음 */
        canvas.drawText( msg, x+(-bound.left), y+(-bound.top+bound.bottom), textPaint);
        dirty = true;

        RectF ret = new RectF(
                x*1.0f/TextureSize,
                y*1.0f/TextureSize,
                (x+bound.right - bound.left)*1.0f/TextureSize,
                (y + BaseSize)*1.0f/TextureSize);

        x += bound.width()+2;
        return ret;
    }
}
