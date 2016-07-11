package com.example.yanoo.glexam.util;

import android.database.Cursor;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.yanoo.glexam.GLActivity;
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
    static public String[] StringNumberArray(int no) {
        String[] ret = new String[no];
        for(int i = 0; i < no; i++) {
            ret[i] = String.format("%d",i);
        }
        return ret;
    }
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

    static public String getPathFromURI(Uri uri) {
        String uriString = uri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
        String displayName = null;

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = GLActivity.singletone.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
        }
        return displayName;
    }

}
