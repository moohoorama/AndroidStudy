package com.example.yanoo.glexam.graphic.ui;

import com.example.yanoo.glexam.graphic.TColor;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;

/** drawing button to texture
 * Created by Yanoo on 2016. 6. 13..
 */
public class THDragBar implements TUI{
    public interface Listener {
        public void press(int idx, String value);
    }
    private float    left;
    private float    right;
    private float    top;
    private float    bottom;

    private int      floating;

    private TColor   tc;
    private TColor   fontTc;

    private Listener tuil;
    private int      choice;
    private String   []option;

    public THDragBar(float left, float top, float size,
                     String []option, Listener tuil) {
        if (size < 0.1) {
            size = 0.1f;
        }
        this.left   = left;
        this.top    = top - 0.015f;
        this.right  = left + size;
        this.bottom = top + 0.015f;
        this.option = option;
        this.choice = 0;
        this.tuil   = tuil;

        this.tc     = TColor.LIGHTBLUE;
        this.fontTc = TColor.WHITE;

        floating = 4;
    }

    public void Draw(TextureManager tm, TouchListener tl, boolean enable) {
        TouchListener.TouchEvent te = tl.getTouchEvent();
        int  _left   = (int)(tl.getWidth()*left);
        int  _right  = (int)(tl.getWidth()*right);
        int  _top    = (int)(tl.getHeight()*top);
        int  _bottom = (int)(tl.getHeight()*bottom);
        int  base_height = (_bottom - _top);
        float curPress = 0;

        if (enable) {
            if (_left <= te.pos.x && te.pos.x < _right &&
                _top <= te.pos.y && te.pos.y < _bottom &&
                te.count == 1) {
                curPress = (te.pos.x - _left)/(_right - _left);
                choice =
                    (int)(curPress * (option.length-1) + 0.5f);
                tuil.press(choice, option[choice]);
            }
            tm.addRoundedRectangle(
                    _left, _top,
                    _right, _bottom,
                    0, base_height / 2,
                    -4, null,
                    tc.Grayscale());
            if (choice != 0) {
                tm.addRoundedRectangle(
                        _left, _top,
                        _left + choice * (_right - _left) / (option.length - 1), _bottom,
                        0, base_height / 2,
                        -4, null,
                        tc);
            }
            tm.addRoundedRectangle(
                    _left + choice * (_right - _left) / (option.length - 1) - base_height/2,
                    _top - base_height/2,
                    _left + choice * (_right - _left) / (option.length - 1) + base_height/2,
                    _bottom + base_height/2,
                    0, base_height / 2,
                    -4, null,
                    tc);
            for (int i = 0; i < option.length; i++) {
                int baseX = _left + i * (_right-_left)/(option.length-1);
                int height = (int)(tl.getWidth()*0.02);
                tm.addText(
                        baseX - height/2,
                        _bottom,
                        baseX + height/2,
                        _bottom+(height),option[i],
                        fontTc);
            }
        } else {
            tm.addRoundedRectangle(
                    _left, _top,
                    _right, _bottom,
                    0, base_height / 2,
                    -4, null,
                    tc.Grayscale().MultiplyRGB(0.5f));
            if (choice != 0) {
                tm.addRoundedRectangle(
                        _left, _top,
                        _left + choice * (_right - _left) / (option.length - 1), _bottom,
                        0, base_height / 2,
                        -4, null,
                        fontTc.MultiplyRGB(0.5f));
            }
            for (int i = 0; i < option.length; i++) {
                int baseX = _left + i * (_right-_left)/(option.length-1);
                int height = (int)(tl.getWidth()*0.02);
                tm.addText(
                        baseX - height/2,
                        _bottom,
                        baseX + height/2,
                        _bottom+height,option[i],
                        fontTc.MultiplyRGB(0.5f));
            }
        }
    }
}