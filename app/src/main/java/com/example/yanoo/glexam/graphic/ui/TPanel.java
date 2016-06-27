package com.example.yanoo.glexam.graphic.ui;

import com.example.yanoo.glexam.graphic.TColor;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;

/** drawing button to texture
 * Created by Yanoo on 2016. 6. 13..
 */
public class TPanel implements TUI{
    private float  left;
    private float  right;
    private float  top;
    private float  bottom;

    private TColor tc;
    private TColor fontTc;

    private String msg;
    private boolean enable = true;

    public static TPanel makeTransparentPanel(
            float left, float top, float right, float bottom, String msg) {
        TPanel tpanel = new TPanel(left,top,right,bottom,msg);
        tpanel.left   = left;
        tpanel.top    = top;
        tpanel.right  = right;
        tpanel.bottom = bottom;
        tpanel.msg    = msg;
        tpanel.tc     = TColor.TRANSPARENT;
        tpanel.fontTc = TColor.WHITE;

        return tpanel;
    }

    public TPanel(float left, float top, float right, float bottom, String msg) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
        this.msg    = msg;

        this.tc     = TColor.GRAY;
        this.fontTc = TColor.WHITE;
    }
    public void        setEnable(boolean enable) {
        this.enable = enable;
    }

    public void Draw(TextureManager tm, TouchListener tl) {
        TouchListener.TouchEvent te = tl.getTouchEvent();
        int _left   = (int)(tl.getWidth()*left);
        int _right  = (int)(tl.getWidth()*right);
        int _top    = (int)(tl.getHeight()*top);
        int _bottom = (int)(tl.getHeight()*bottom);

        if (enable) {
            tm.addRoundedRectangle(
                    _left, _top,
                    _right, _bottom,
                    8,(_right - _left)/20,
                    -3, null,
                    tc);
            if (msg != null) {
                tm.addText(
                        _left, _top,
                        _right, _bottom,
                        msg,
                        fontTc);
            }
        } else {
            tm.addRoundedRectangle(
                    _left, _top,
                    _right, _bottom,
                    8,(_top - _left)/20,
                    -3, null,
                    tc.MultiplyRGB(0.5f));
            if (msg != null) {
                tm.addText(
                        _left, _top,
                        _right, _bottom,
                        msg,
                        fontTc.MultiplyRGB(0.5f));
            }
        }
    }
}