package com.example.yanoo.glexam.graphic.ui;

import com.example.yanoo.glexam.graphic.TColor;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.graphic.ui.TUI;
import com.example.yanoo.glexam.touch.TouchListener;

/** drawing button to texture
 * Created by Yanoo on 2016. 6. 13..
 */
public class TButton implements TUI {
    public interface Listener {
        public void press(TouchListener.TouchEvent tl);

        public void depress(TouchListener.TouchEvent tl);
    }
    private float  left;
    private float  right;
    private float  top;
    private float  bottom;

    private int    floating;

    private TColor tc;
    private TColor fontTc;

    private Listener tuil;
    private int    prevPress = 0;
    private String msg;

    static final private int ANI_FRAME = 16;

    public TButton(float left, float top, float right, float bottom, String msg, Listener tuil) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
        this.msg    = msg;
        this.tuil   = tuil;

        this.tc     = TColor.LIGHTBLUE;
        this.fontTc = TColor.WHITE;

        floating = ANI_FRAME;
    }

    public void Draw(TextureManager tm, TouchListener tl, boolean enable) {
        TouchListener.TouchEvent te = tl.getTouchEvent();
        int _left   = (int)(tl.getWidth()*left);
        int _right  = (int)(tl.getWidth()*right);
        int _top    = (int)(tl.getHeight()*top);
        int _bottom = (int)(tl.getHeight()*bottom);
        int curPress = 0;
        int pressSize = (int)(Math.min((_right-_left),(_bottom-_top)))/8;
        int actSize = 0;

        if (enable) {
            if (_left <= te.pos.x && te.pos.x < _right &&
                _top <= te.pos.y  && te.pos.y < _bottom &&
                te.count == 1) {
                curPress = 1;
            }
            if (curPress > 0) {
                if (tuil != null) {
                    if (floating > 0) {
                        tuil.press(te);
                    } else {
                        if (te.phase == 2) {
                            tuil.depress(te);
                        }
                    }
                }
                floating = 0;
            } else {
                floating = (ANI_FRAME+floating)/2;
            }
            actSize = floating*pressSize/ANI_FRAME;
            tm.addRoundedRectangle(
                    _left + actSize, _top + actSize,
                    _right - actSize, _bottom - actSize,
                    0,0,
                    -1, null,
                    tc);
            if (msg != null) {
                tm.addText(
                        _left + actSize + pressSize, _top + actSize + pressSize,
                        _right - actSize - pressSize, _bottom - actSize - pressSize,
                        msg,
                        fontTc);
            }
        } else {
            tm.addRoundedRectangle(
                    _left  + pressSize, _top + pressSize,
                    _right - pressSize, _bottom - pressSize,
                    0,0,
                    -1, null,
                    tc.MultiplyRGB(0.5f));
            if (msg != null) {
                tm.addText(
                        _left + pressSize*2, _top + pressSize*2,
                        _right- pressSize*2, _bottom - pressSize*2,
                        msg,
                        fontTc.MultiplyRGB(0.5f));
            }
        }
        prevPress = curPress;
    }
}