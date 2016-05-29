package com.example.yanoo.glexam;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Yanoo on 2016. 5. 29..
 */
public class Util {
    static public FloatBuffer setFloatBuffer(float[] src) {
        FloatBuffer dst;

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(src.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        dst = byteBuf.asFloatBuffer();
        dst.put(src);
        dst.position(0);

        return dst;
    }

    static public float get2dx(int x, int y,int z) {
        return (x - y) * Pos.Q_LEN * 4;
    }
    static public float get2dy(int x, int y,int z) {
        return ((x + y)*2+z) * Pos.Q_LEN;
    }
}
