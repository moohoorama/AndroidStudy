package com.example.yanoo.glexam;

import android.util.Log;

/**
 * Created by Yanoo on 2016. 5. 29..
 */
public class Pos {
    public static final int Q_LEN = 20;

    public int x;
    public int y;
    public int z;

    public float vx;
    public float vy;
    public float vz;

    public Pos() {
    }
    public Pos(int x, int y, int z) {
        this.set(x,y,z);
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void trans2d() {
        vx = (x - y) * Q_LEN*4;
        vy = ((x + y)*2+z) * Q_LEN;
        vz = 0;
    }
}
