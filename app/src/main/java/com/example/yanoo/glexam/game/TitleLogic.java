package com.example.yanoo.glexam.game;

import android.graphics.RectF;

import com.example.yanoo.glexam.R;
import com.example.yanoo.glexam.graphic.GLRenderer;
import com.example.yanoo.glexam.graphic.TButton;
import com.example.yanoo.glexam.graphic.TUI;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.NormalTouchListener;
import com.example.yanoo.glexam.touch.TouchListener;
import com.example.yanoo.glexam.util.Util;

/**
 * Created by Yanoo on 2016. 6. 18..
 */
public class TitleLogic implements GameLogic {
    private TouchListener   touchListener= new NormalTouchListener(0,0);
    private GLRenderer      renderer;

    public TitleLogic(GLRenderer renderer) {
        this.renderer=renderer;
    }
    public void          registUI() {
        renderer.registTUI(new TButton(0.1f, 0.1f, 0.4f, 0.4f, Util.getRString(R.string.Check),  new TUI.TUIListener() {
            @Override
            public void press(TouchListener.TouchEvent tl) {
                renderer.changeGameLogic(new CubeDrawer());
            }
            public void depress(TouchListener.TouchEvent tl) {}
        }));
        renderer.registTUI(new TButton(0.1f, 0.6f, 0.4f, 0.9f, Util.getRString(R.string.Check),  new TUI.TUIListener() {
            @Override
            public void press(TouchListener.TouchEvent tl) {
            }
            public void depress(TouchListener.TouchEvent tl) {}
        }));
        renderer.registTUI(new TButton(0.6f, 0.1f, 0.9f, 0.4f, Util.getRString(R.string.Check),  new TUI.TUIListener() {
            @Override
            public void press(TouchListener.TouchEvent tl) {
            }
            public void depress(TouchListener.TouchEvent tl) {}
        }));
        renderer.registTUI(new TButton(0.6f, 0.6f, 0.9f, 0.9f, Util.getRString(R.string.Check),  new TUI.TUIListener() {
            @Override
            public void press(TouchListener.TouchEvent tl) {
            }
            public void depress(TouchListener.TouchEvent tl) {}
        }));
    }

    @Override
    public TouchListener getTouchListener() {
        return touchListener;
    }

    @Override
    public void draw(TextureManager tm) {
        tm.addText('l',50, 150, "abcd", 64, 1.0f,1.0f,1.0f,1.0f);
        tm.addText('l',50, 250, "위", 128, 1.0f,1.0f,1.0f,1.0f);
        tm.addTextureRect(0,new RectF(0,0,512,512), new RectF(0.0f,0.0f,1.0f,1.0f),1.0f,1.0f,1.0f,1.0f);

    }
}
