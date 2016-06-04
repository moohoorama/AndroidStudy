package com.example.yanoo.glexam;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yanoo on 2016. 5. 29..
 */
public class Util {
    static final public int Q_LEN = 20;

    static public FloatBuffer setFloatBuffer(float[] src) {
        FloatBuffer dst;
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(src.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        dst = byteBuf.asFloatBuffer();
        dst.put(src);
        dst.position(0);

        return dst;
    }

    static public FloatBuffer setFloatBufferFromList(List<float[]> src) {
        int   size = 0;
        for (float[] val: src) {
            size += val.length;
        }
        float []med = new float[size];
        int   idx = 0;
        for (float[] arr: src) {
            System.arraycopy(arr,0,med,idx,arr.length);
            idx += arr.length;
        }

        FloatBuffer dst;
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(med.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        dst = byteBuf.asFloatBuffer();
        dst.put(med);
        dst.position(0);

        return dst;
    }


    static public float get2dx(int x, int y,int z) {
        return (x - y) * Q_LEN * 4;
    }
    static public float get2dy(int x, int y,int z) {
        return ((x + y)*2+z) * Q_LEN;
    }
}
