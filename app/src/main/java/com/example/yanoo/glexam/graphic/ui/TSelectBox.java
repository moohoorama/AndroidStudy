package com.example.yanoo.glexam.graphic.ui;

import com.example.yanoo.glexam.graphic.TColor;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;

/** drawing button to texture
 * Created by Yanoo on 2016. 6. 13..
 */
public class TSelectBox implements TUI{
    public interface Listener {
        public void press(int idx, String value);
    }
    private float    left;
    private float    right;
    private float    top;
    private float    bottom;

    private TColor   tc;
    private TColor   fontTc;

    private Listener tuil;
    private int      choice;
    private int      pressY;
    private int      scrollY;
    private String   []option;

    public TSelectBox(float left, float top, float right, float bottom,
                      String []option, Listener tuil) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
        this.option = option;
        this.choice = 0;
        this.tuil   = tuil;

        this.scrollY = 0;

        this.tc     = TColor.INDIGO;
        this.fontTc = TColor.WHITE;
    }

    public void Draw(TextureManager tm, TouchListener tl, boolean enable) {
        TouchListener.TouchEvent te = tl.getTouchEvent();
        int  _left   = (int)(tl.getWidth()*left);
        int  _right  = (int)(tl.getWidth()*right);
        int  _top    = (int)(tl.getHeight()*top);
        int  _bottom = (int)(tl.getHeight()*bottom);
        int  height = (_bottom - _top);
        int  radius = (_bottom - _top)/20;
        int  fontHeight = tl.getHeight()*6/100;
        int  pad    = height/20;
        int centerY = (_top+_bottom)/2;


        if (enable) {
            if (_left <= te.pos.x && te.pos.x < _right &&
                    _top <= te.pos.y && te.pos.y < _bottom &&
                    te.count == 1) {
                if (te.phase > 0) {
                    scrollY += pressY - (int)te.pos.y;
                }
                pressY = (int)te.pos.y;
                scrollY = Math.max(0, Math.min((option.length-1)*fontHeight,scrollY));
                choice = (scrollY+fontHeight/2) / fontHeight;
                if (te.phase == 2) {
                    tuil.press(choice, option[choice]);
                }
            } else {
                scrollY = (choice*fontHeight + scrollY*7)/8;
            }
        }
        tm.addRoundedRectangle(
                _left, _top,
                _right, _bottom,
                0, radius,
                -4, null,
                tc.MultiplyRGB(0.8f));
        tm.addRoundedRectangle(
                _left +pad,
                centerY,
                _right-pad,
                centerY + fontHeight,
                0, radius,
                -4, null,
                new TColor(1.0f,1.0f,1.0f,0.4f+((float)Math.sin(tm.getRunSequence()*Math.PI/15))*0.2f));

        for (int i = 0; i < option.length; i++) {
            int baseY = centerY - scrollY + i * fontHeight;
            float bright = 1-Math.abs(scrollY - i * fontHeight)*2.0f/(_bottom-_top-pad-pad);
            if (bright < 0) {
                continue;
            }
            tm.addText(
                    _left+pad,
                    baseY,
                    _right-pad,
                    baseY + fontHeight,
                    option[i],
                    fontTc.MultiplyA(bright));
        }
    }
}