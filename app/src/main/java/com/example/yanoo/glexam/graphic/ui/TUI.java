package com.example.yanoo.glexam.graphic.ui;


import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;

/**
 * Created by Yanoo on 2016. 6. 13..
 */
public interface TUI {
    public void        setEnable(boolean enable);
    public void        Draw(TextureManager tm, TouchListener tl);
}
