package com.example.yanoo.glexam.game;

import com.example.yanoo.glexam.graphic.GLRenderer;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Yanoo on 2016. 6. 5..
 */
public interface GameLogic {
    public TouchListener getTouchListener();
    public void          registUI(final GLRenderer renderer);
    public void          act(TextureManager textureManager);
    public void          draw(GL10 gl, TextureManager textureManager);
}
