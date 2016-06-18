package com.example.yanoo.glexam.graphic;

import android.graphics.RectF;
import android.util.Log;

import com.example.yanoo.glexam.touch.TouchListener;

/** drawing button to texture
 * Created by Yanoo on 2016. 6. 13..
 */
public class TButton implements TUI{
    private float  left;
    private float  right;
    private float  top;
    private float  bottom;

    private int    floating;
    private float  r;
    private float  g;
    private float  b;
    private float  a;
    private TUIListener tuil;
    private int    prevPress = 0;
    private String msg;

    public TButton(float left, float top, float right, float bottom, String msg, TUIListener tuil) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
        this.msg    = msg;
        this.tuil   = tuil;
        r = 0.4f;
        g = 0.5f;
        b = 0.7f;
        a = 1.0f;
        floating = 4;
    }
    public TUIListener getTUIL() {
        return tuil;
    }

    public void Draw(TextureManager tm, TouchListener tl) {
        TouchListener.TouchEvent te = tl.getTouchEvent();
        int _left   = (int)(tl.getWidth()*left);
        int _right  = (int)(tl.getWidth()*right);
        int _top    = (int)(tl.getHeight()*top);
        int _bottom = (int)(tl.getHeight()*bottom);
        int curPress = 0;
        int pressSize = (int)(Math.min((_right-_left),(_bottom-_top)))/8;

        if (_left <= te.pos.x && te.pos.x < _right &&
            _top <= te.pos.y  && te.pos.y < _bottom &&
            te.count == 1) {
            curPress = 1;
        }
        if (curPress > 0) {
            if (tuil != null && floating > 0) {
                tuil.press(te);
            }
            floating = 0;
        } else {
            if (tuil != null && floating == 0) {
                tuil.depress(te);
            }
//            floating = Math.min(pressSize, floating+1);
            floating = (pressSize+floating)/2;
        }

        tm.addRoundedRectangle(
                _left+floating, _top+floating,
                _right-floating,_bottom-floating,
                -1, null,
                //0, rect,
                r,g,b,a);
        /*
        RectF rect = tm.getText(msg);
        tm.addRoundedRectangle(
                _left+floating, _top+floating,
                _right-floating,_bottom-floating,
                //-1, null,
                0, rect,
                r,g,b,a);
                */
        if (msg != null) {
            tm.addText('c', (int) (_left + _right) / 2, (int) (_top + _bottom) / 2, msg,
                    (Math.min(_right - _left, _bottom - _top)) / 2 - floating, r, g, b, a);
        }
        prevPress = curPress;
    }
}