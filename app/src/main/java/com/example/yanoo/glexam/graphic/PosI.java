package com.example.yanoo.glexam.graphic;

/**
 * Created by Yanoo on 2016. 5. 29..
 */
public class PosI {
    public int x = 0;
    public int y = 0;
    public int z = 0;

    public PosI(int x,int y,int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void    setZero() {
        x = 0;
        y = 0;
        z = 0;
    }
    public boolean isZero(){ return x == 0 && y ==0 && z ==0;}
    public void     copyFrom(PosI pos) {
        x = pos.x;
        y = pos.y;
        z = pos.z;
    }
}
