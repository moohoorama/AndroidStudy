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
    private static final int FontSize = 28;
    private static final int BaseSize = 32;
    private static final int TextureSize = 512;
    private TextureManager tm;
    private Bitmap bitmap = Bitmap.createBitmap(TextureSize, TextureSize, Bitmap.Config.ARGB_4444);
    private boolean useTexture = false;
    private boolean dirty = false;
    private boolean needReset = false;
    private int x;
    private int y;
    private int textureIdx;
    private HashMap<String, RectF> msgMap = new HashMap<String, RectF>();
    private float[]                white  = TextureManager.dot4To4Point(
            new RectF(0.0f,0.0f,BaseSize*1.0f/TextureSize,BaseSize*1.0f/TextureSize));

    public CanvasBitmap(TextureManager tm) {
        this.tm = tm;
        needReset = true;
        useTexture = false;
    }

    public Bitmap  getBitmap() {
        return bitmap;
    }
    public float[] getWhite()  { return white; }

    public void refresh() {
//        bitmap.eraseColor(Color.TRANSPARENT);
        bitmap.eraseColor(Color.argb(0, 255, 255, 255));

        Canvas canvas = new Canvas( bitmap );
        Paint  paint = new Paint();
        msgMap.clear();
        x = 0;
        y = 0;

        /* 이거 설정 안해주면 getText하면서 recursive 빠짐 */
        useTexture = true;
        paint.setARGB(0xff, 0xff, 0xff, 0xff);
        canvas.drawRect(0,0,BaseSize,BaseSize, paint);
        x += BaseSize;

        for (int i = 0; i <= 9; i++) {
            getText(String.format("%d",i));
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            getText(String.format("%c",i));
        }

        //canvas.drawColor(0, Display.Mode.CLEAR);


        Log.w("CanvasBitmap", "reset!");
        needReset = false;
    }

    public void setTextureIdx(int idx) {
        this.textureIdx = idx;
    }

    public void  setTexture(GL10 gl) {
        if (dirty) {
            tm.setTexture(gl, textureIdx, bitmap, true/*neat*/);
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
        int curFontSize = FontSize;

        int    yy =0;
        Rect   bound = new Rect(0,0,0,0);
        Canvas canvas = new Canvas( bitmap );
        Paint  textPaint = new Paint();

        while (curFontSize > 0) {
            textPaint.setTextSize(curFontSize);
            textPaint.setAntiAlias(true);
            textPaint.setARGB(0xff, 0xff, 0xff, 0xff);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setTypeface(typeface);
            textPaint.setStrokeWidth(0);
            textPaint.setStyle(Paint.Style.FILL);

            bound = new Rect();
            textPaint.getTextBounds(msg, 0, msg.length(), bound);
            if (x + bound.right - bound.left >= TextureSize) { /* carriage return*/
                x = 0;
                y += BaseSize;
                if (y + BaseSize >= TextureSize / 2) {
                    needReset = true;
                }
                if (y + BaseSize >= TextureSize) {
                    return null;
                }
            }
            yy = (BaseSize - curFontSize) / 2;
            if (yy+bound.top < 0) {
                yy = -bound.top;
            }
            if (yy + bound.bottom < BaseSize) {
                break;
            }
            curFontSize -= 4;
        }
        if (curFontSize <= 0) {
            return null;
        }
        /* left,top쪽 방향으로 조금 오버해서 그릴 수도 있음 */
        canvas.drawText( msg, x+(-bound.left), y+yy, textPaint);
        dirty = true;
        Log.w(msg, String.format("%d,%d %d %d", x,y,yy,BaseSize));

        RectF ret = new RectF(
                x*1.0f/TextureSize,
                y*1.0f/TextureSize,
                (x+bound.right - bound.left)*1.0f/TextureSize,
                (y + BaseSize)*1.0f/TextureSize);

        x += bound.width()+2;
        return ret;
    }
}
