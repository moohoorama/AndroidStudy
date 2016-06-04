package com.example.yanoo.glexam;


/**
 * for draw cube
 * Created by Yanoo on 2016. 5. 29..
 */
public class CubeTile {
    public float  mScrollX = 0;
    public float  mScrollY = 0;

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


    public void drawShape(int screenWidth, int screenHeight, int x, int y, int z, int type) {
        float tf[] = new float[6*2];
        float vf[] = new float[6*3];
        float cf[] = new float[6*4];
        int   xx,yy,zz;

        for (int i = 0; i < 6; i++) {
            xx = x+(shape_type[type][i] & 1);
            yy = y+(shape_type[type][i] & 2)/2;
            zz = z+(shape_type[type][i] & 4)/4;
            tf[i*2    ] = 0;
            tf[i*2 + 1] = 0;

            vf[i*3    ] = (Util.get2dx(xx,yy,zz) - mScrollX)*MainGLSurfaceView.sSingletone.mScaleFactor + screenWidth/2;
            vf[i*3 + 1] = (Util.get2dy(xx,yy,zz) - mScrollY)*MainGLSurfaceView.sSingletone.mScaleFactor + screenHeight/2;
            vf[i*3 + 2] = TopTexture.sSingletone.getDepth();

            cf[i*4    ] = shape_color[type][i]/10;
            cf[i*4 + 1] = shape_color[type][i]/10+0.3f;
            cf[i*4 + 2] = shape_color[type][i]/10;
            cf[i*4 + 3] = 1.0f;
        }
        TopTexture.sSingletone.addTexture(0, vf,tf,cf);
    }

    public void dragXY(float x, float y) {
        mScrollX += x/MainGLSurfaceView.sSingletone.mScaleFactor;
        mScrollY += y/MainGLSurfaceView.sSingletone.mScaleFactor;
    }
}
