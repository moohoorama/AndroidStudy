package com.example.yanoo.glexam.graphic.ui;

import android.graphics.RectF;

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

        this.tc     = new TColor(129/256.0f,212/256.0f,241/256.0f, 128/256.0f);
        this.fontTc = TColor.WHITE;
    }

    public void Draw(TextureManager tm, TouchListener tl, boolean enable) {
        int _left   = (int)(tl.getWidth()*left);
        int _right  = (int)(tl.getWidth()*right);
        int _top    = (int)(tl.getHeight()*top);
        int _bottom = (int)(tl.getHeight()*bottom);
        if (enable) {
            tm.addTextureRect(-1,
                    new RectF(_left, _top,
                            _right, _bottom),
                    null,
                    tc);
            tm.addTextureRect(-1,
                    new RectF(_left + 5, _top + 5,
                            _right - 5, _bottom - 5),
                    null,
                    tc.MultiplyRGB(1.8f));
            tm.addTextureRect(-1,
                    new RectF(_left + 15, _top + 15,
                            _right - 15, _bottom - 15),
                    null,
                    tc);
            if (msg != null) {
                tm.addText(
                        _left, _top,
                        _right, _bottom,
                        msg,
                        fontTc);
            }
        } else {
            tm.addTextureRect(-1,
                    new RectF(_left, _top,
                            _right, _bottom),
                    null,
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