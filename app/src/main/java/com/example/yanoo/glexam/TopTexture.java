package com.example.yanoo.glexam;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Yanoo on 2016. 5. 30..
 */
public class TopTexture {
    private Bitmap bitmap = Bitmap.createBitmap( 512, 512, Bitmap.Config.ARGB_4444);

    public void reset() {
        bitmap.eraseColor(Color.TRANSPARENT );
    }

    public void drawText(String msg, float x, float y, float size) {
        Canvas canvas = new Canvas( bitmap );

        Paint textPaint = new Paint();
        textPaint.setTextSize( size );
        textPaint.setAntiAlias( true );
        textPaint.setARGB( 0xff, 0xff, 0xff, 0xff );
        textPaint.setTextAlign( Paint.Align.LEFT );
        textPaint.setTypeface(GLActivity.getTF());
//        textPaint.setTextScaleX( 0.5f );

        Rect bound = new Rect();
        textPaint.getTextBounds(msg,0,msg.length(), bound);
        textPaint.setARGB( 0xff, 0x7f, 0x7f, 0x7f );
        float width = textPaint.measureText(msg);
        canvas.drawRect(x+bound.left,y+bound.top,x+bound.right,y+bound.bottom,textPaint);

        textPaint.setARGB( 0xff, 0xff, 0xff, 0xff );
        canvas.drawText( msg, x, y, textPaint);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
