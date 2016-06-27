package com.example.yanoo.glexam.game;

import com.example.yanoo.glexam.graphic.GLRenderer;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;

/**
 * Created by Yanoo on 2016. 6. 5..
 */
public interface GameLogic {
    public TouchListener getTouchListener();
    public void          registUI(final GLRenderer renderer);
    public void          draw(TextureManager textureManager);
}
