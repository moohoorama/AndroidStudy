package com.example.yanoo.glexam;

import android.util.Log;

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Yanoo on 2016. 5. 29..
 */
public class CubeTile {
    public int[]  map;
    public float  mScrollX = 0;
    public float  mScrollY = 0;

    private static final int[][] shape_type= {
            {2,0,3,1},
            {6,2,7,3},
            {7,3,5,1},
    };
    private static final float[][] shape_color= {
            {0.2f, 0.3f,0.7f},
            {0.15f,0.2f,0.4f},
            {0.1f, 0.1f,0.2f},
    };

    public void draw(GL10 gl) {
        int x;
        int y;
        for (x = 0; x < 16; x++) {
            for (y = 0; y < 16; y++) {
                drawShape(gl, x,y,0,0);
            }
        }
        drawShape(gl, 3,0,-1,0);
        drawShape(gl, 3,0,-1,1);
        drawShape(gl, 3,0,-1,2);
    }

    public void drawShape(GL10 gl, int x, int y, int z, int type) {
        Pos[] locs = new Pos[4];

        for (int i = 0; i < locs.length; i++) {
            locs[i] = new Pos(
                    x + (shape_type[type][i] & 1),
                    y + (shape_type[type][i] & 2)/2,
                    z + (shape_type[type][i] & 4)/4);
            locs[i].trans2d();
            locs[i].vx -= mScrollX;
            locs[i].vy -= mScrollY;
        }
        drawRectangle(gl, locs,
                shape_color[type][0],shape_color[type][1],shape_color[type][2]);
    }

    public static void drawRectangle(GL10 gl, Pos[] loc, float r, float g, float b) {
        FloatBuffer cb= null, vb = null;
        float[] vf = {
                /* ld, lt, rd rt */
                loc[0].vx, loc[0].vy, loc[0].vz,
                loc[1].vx, loc[1].vy, loc[1].vz,
                loc[2].vx, loc[2].vy, loc[2].vz,
                loc[3].vx, loc[3].vy, loc[3].vz,
        };
        vb = Util.setFloatBuffer(vf);
        float[] cf = {
                /* ld, lt, rd rt */
                r*0.9f, g*0.9f, b*0.9f, 1.0f,
                r*1.0f, g*1.0f, b*1.0f, 1.0f,
                r*0.7f, g*0.7f, b*0.7f, 1.0f,
                r*0.8f, g*0.8f, b*0.8f, 1.0f,
        };
        cb = Util.setFloatBuffer(cf);

        gl.glColorPointer  (4, GL10.GL_FLOAT, 0, cb);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vb);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vf.length / 3);
    }

    public void dragXY(float x, float y) {
        mScrollX += x;
        mScrollY += y;
    }
    public void clickXY(float x, float y) {
        mScrollX = x;
        mScrollY = y;
    }
}
