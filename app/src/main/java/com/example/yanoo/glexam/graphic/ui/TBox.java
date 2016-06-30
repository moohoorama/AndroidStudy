package com.example.yanoo.glexam.graphic.ui;

import android.graphics.RectF;

import com.example.yanoo.glexam.graphic.TColor;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;

/** drawing button to texture
 * Created by Yanoo on 2016. 6. 13..
 */
public class TBox implements TUI{
    private float  left;
    private float  right;
    private float  top;
    private float  bottom;
    private TColor tc;

    public TBox(float left, float top, float right, float bottom, String msg) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;

        this.tc     = new TColor(0.0f,0.0f,0.0f,0.0f);
    }

    public void Draw(TextureManager tm, TouchListener tl, boolean enable) {
        int _left   = (int)(tl.getWidth()*left);
        int _right  = (int)(tl.getWidth()*right);
        int _top    = (int)(tl.getHeight()*top);
        int _bottom = (int)(tl.getHeight()*bottom);

        tm.addTextureRect(-1,
                new RectF(_left, _top,
                        _right, _bottom),
                null,
                tc);
    }
}