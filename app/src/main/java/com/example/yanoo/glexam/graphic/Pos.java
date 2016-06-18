package com.example.yanoo.glexam.graphic;

/**
 * Created by Yanoo on 2016. 5. 29..
 */
public class Pos {
    public float x = 0;
    public float y = 0;
    public float z = 0;

    public void    setZero() {
        x = 0;
        y = 0;
        z = 0;
    }
    public boolean isZero(){ return x == 0 && y ==0 && z ==0;}
    public void     copyFrom(Pos pos) {
        x = pos.x;
        y = pos.y;
        z = pos.z;
    }
}
