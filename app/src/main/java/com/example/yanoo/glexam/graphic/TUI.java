package com.example.yanoo.glexam.graphic;


import com.example.yanoo.glexam.touch.TouchListener;

/**
 * Created by Yanoo on 2016. 6. 13..
 */
public interface TUI {
    public interface TUIListener {
        public void press(TouchListener.TouchEvent tl);
        public void depress(TouchListener.TouchEvent tl);
    }
    public TUIListener getTUIL();
    public void        Draw(TextureManager tm, TouchListener tl);
}
