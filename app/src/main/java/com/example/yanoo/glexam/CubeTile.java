package com.example.yanoo.glexam;


import android.util.Log;

import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;
import com.example.yanoo.glexam.util.Util;

/**
 * for draw cube
 * Created by Yanoo on 2016. 5. 29..
 */
public class CubeTile {
    private static final int[][] shape_type= {
            {2,0,3,1,3,0},
            {6,2,7,3,7,2},
            {7,3,5,1,5,3},
    };
    private static final float[][] shape_color= {
            {2,0,3,1,3,0},
            {6,2,7,3,7,2},
            {7,3,5,1,5,3},
    };


    static public void drawShape(TextureManager tm, TouchListener touchListener, int x, int y, int z, int type, float col) {
        float    tf[] = new float[6*2];
        float    vf[] = new float[6*3];
        float    cf[] = new float[6*4];
        int      xx,yy,zz;
        int      tx,ty;

        for (int i = 0; i < 6; i++) {
            xx = (x+(shape_type[type][i] & 1));
            yy = (y+(shape_type[type][i] & 2)/2);
            zz = z+((shape_type[type][i] & 4)/4);
            tf[i*2    ] = 0;
            tf[i*2 + 1] = 0;


            vf[i*3    ] = (Util.get2dx(xx,yy,zz) - touchListener.getDragX())* touchListener.getScaleFactor();
            vf[i*3 + 1] = (Util.get2dy(xx,yy,zz) - touchListener.getDragY())* touchListener.getScaleFactor();
            vf[i*3 + 2] = tm.getDepth();

            cf[i*4    ] = shape_color[type][i]/20 + (x / 32.0f)+  (type &1)*0.3f;
            cf[i*4 + 1] = shape_color[type][i]/20 + (y / 32.0f)+0.3f + (type & 2)*0.3f/2;
            cf[i*4 + 2] = shape_color[type][i]/20 + (z / 32.0f)+ (type & 4)*0.3f/4;
            cf[i*4 + 3] = col;
        }
        tm.addTexture(-1, vf, tf, cf);
    }
}
