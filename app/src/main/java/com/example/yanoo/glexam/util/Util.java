package com.example.yanoo.glexam.util;

import android.graphics.RectF;

import com.example.yanoo.glexam.MainGLSurfaceView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yanoo on 2016. 5. 29..
 */
public class Util {
    static final public int Q_LEN = 15;

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

    static public String getRString(int id) {
        return MainGLSurfaceView.sSingletone.getResources().getString(id);
    }

    static public float getRoundX(int edge, int idx, int delication) {
        float baseArrow[]={
                -1, -1,
                1, -1,
                1, 1,
                -1, 1};
        return -baseArrow[edge * 2] - (float) Math.cos(edge * Math.PI / 2.0f + (idx * Math.PI / (2.0f * (delication - 1))));
    }
    static public float getRoundY(int edge, int idx, int delication) {
        float baseArrow[]={
                -1, -1,
                1, -1,
                1, 1,
                -1, 1};
        return -baseArrow[edge * 2+1] - (float) Math.sin(edge * Math.PI / 2.0f + (idx * Math.PI / (2.0f * (delication - 1))));
    }

    static public boolean writeFile(String path, byte[] contents) {
        File file = null;
        boolean isSuccess = true;

        file = new File(MainGLSurfaceView.sSingletone.getBasePath() +path);
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            try {
                isSuccess = file.createNewFile();
            } catch(IOException e) {
                e.printStackTrace();
                isSuccess = false;
            }
            if (!isSuccess) {
                return false;
            }
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(contents);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            isSuccess = false;
        }

        return isSuccess;
    }

    static public byte[] readFile(String path) {
        File file = null;

        file = new File(MainGLSurfaceView.sSingletone.getBasePath() +path);
        if (file == null || !file.exists()) {
            return null;
        }

        FileInputStream  fis;
        try {
            fis = new FileInputStream(file);
            int readSize = (int)file.length();
            byte[] buffer = new byte[readSize];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    (x - y)  = xx / (Q_LEN * 4);
    (x + y)  = yy / (Q_LEN * 2);
    x = xx / (Q_LEN * 2) + yy/(Q_LEN)
    y = yy / (Q_LEN) - xx / (Q_LEN*2)
    */
    static public int getIsoX(int x, int y) {
        return (x/2 + y) / (Q_LEN*4);
    }
    static public int getIsoY(int x, int y) {
        return (-x/2 + y) / (Q_LEN*4);
    }

    static public float get2dx(int x, int y,int z) {
        return (x - y) * Q_LEN * 4;
    }
    static public float get2dy(int x, int y,int z) {
        return ((x + y)*2+z*4) * Q_LEN;
    }
}
