package com.example.yanoo.glexam.util;

import java.util.ArrayList;

/**
 * Created by Yanoo on 2016. 6. 4..
 */
public class Stopwatch {
    private long beginTime;
    private long prevTime;
    StringBuffer ret = new StringBuffer();


    public Stopwatch() {
        beginTime = prevTime = System.currentTimeMillis();
    }

    public void event(String msg) {
        long curTime = System.currentTimeMillis();
        ret.append(String.format("|%d|%s", curTime - prevTime, msg));
        prevTime = curTime;
    }

    public String toString() {
        long curTime = System.currentTimeMillis();
        ret.append(String.format("|%d|%s(%d)", curTime - prevTime, "done", curTime - beginTime));
        return ret.toString();
    }
}
