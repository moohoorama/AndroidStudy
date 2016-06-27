package com.example.yanoo.glexam.gameobj;

import android.util.Log;

import com.example.yanoo.glexam.MainGLSurfaceView;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;
import com.example.yanoo.glexam.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yanoo on 2016. 6. 25..
 */
public class IsoMap {
    private static final int[][] shape_type= {
            {6,4,7,5,7,4},
            {6,2,7,3,7,2},
            {7,3,5,1,5,3},
    };
    private static final float[][] shape_color= {
            {2,0,3,1,3,0},
            {6,2,7,3,7,2},
            {7,3,5,1,5,3},
    };

    private int x;
    private int y;

    private int height[][];
    private int zone[][];

    /* 세로길이이며, 가로의 반 */
    private int BASE_LEN = 80;
    private int Q_LEN = 32;

    public IsoMap(int x, int y) {
        this.x = x;
        this.y = y;

        this.height = new int[y][x];
        this.zone   = new int[y][x];
        for (int j = 0; j < y; j++) {
            for (int i=0; i < x; i++) {
                this.zone[j][i] = 2;
            }
        }
    }

    public static IsoMap load(String path) {
        byte contents[] = Util.readFile(path);
        if (contents == null) {
            return null;
        }
        String org = new String(contents);
        try {
            JSONObject obj = new JSONObject(new String(org));
            IsoMap     isoMap= new IsoMap(obj.getInt("x"), obj.getInt("y"));

            JSONArray arr = obj.getJSONArray("height");
            Log.i("arr", String.format("%d,%d %d",
                    obj.getInt("x"),
                    obj.getInt("y"),
                    arr.length()));
            if (arr.length() != isoMap.x * isoMap.y) {
                return null;
            }
            for (int i = 0; i < arr.length(); i++) {
                isoMap.height[i/isoMap.x][i % isoMap.x] = arr.getInt(i);
            }

            arr = obj.getJSONArray("zone");
            if (arr.length() != isoMap.x * isoMap.y) {
                return null;
            }
            for (int i = 0; i < arr.length(); i++) {
                isoMap.zone[i/isoMap.x][i % isoMap.x] = arr.getInt(i);
            }

            MainGLSurfaceView.sSingletone.toast(path + "Load");

            return isoMap;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void save(String path) {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            obj.put("x", x);
            obj.put("y", y);
            for (int[] y:height) {
                for (int x:y) {
                    arr.put(x);
                }
            }
            obj.put("height", arr);

            arr = new JSONArray();
            for (int[] y:zone) {
                for (int x:y) {
                    arr.put(x);
                }
            }
            obj.put("zone", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Util.writeFile(path, obj.toString().getBytes());
        MainGLSurfaceView.sSingletone.toast(path + " Save");
    }


    public int getIsoX(int x, int y) {
        return (x/2 + y) / (Q_LEN);
    }
    public int getIsoY(int x, int y) {
        return (-x/2 + y) / (Q_LEN);
    }
    public int get2dx(int x, int y,int z) {
        return (x - y) * Q_LEN;
    }
    public int get2dy(int x, int y,int z) {
        return (x + y)*Q_LEN/2-z*Q_LEN/4;
    }

    public void setHeight(int x, int y, int z) {
        height[y][x] = z;
    }
    public void setZone(int x, int y, int z) {
        zone[y][x] = z;
    }
    public int getHeight(int x, int y) {
        return height[y][x];
    }
    public int getZone(int x, int y) {
        return zone[y][x];
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void drawShape(TextureManager tm, TouchListener touchListener, int x, int y, int z, int type, int color) {
        float    tf[] = new float[6*2];
        float    vf[] = new float[6*3];
        float    cf[] = new float[6*4];
        int      xx,yy,zz;
        Q_LEN = BASE_LEN * touchListener.getWidth() / 1280;

        for (int i = 0; i < 6; i++) {
            xx = (x+(shape_type[type][i] & 1));
            yy = (y+(shape_type[type][i] & 2)/2);
            zz = z*((shape_type[type][i] & 4)/4);
            tf[i*2    ] = 0;
            tf[i*2 + 1] = 0;


            vf[i*3    ] = (get2dx(xx,yy,zz) - touchListener.getDragX())* touchListener.getScaleFactor();
            vf[i*3 + 1] = (get2dy(xx,yy,zz) - touchListener.getDragY())* touchListener.getScaleFactor();
            vf[i*3 + 2] = tm.getDepth();

            cf[i*4    ] = (color & 4)/4 * 0.3f + 0.05f*(shape_color[type][i]);
            cf[i*4 + 1] = (color & 2)/2 * 0.3f + 0.05f*(shape_color[type][i]);
            cf[i*4 + 2] = (color & 1)/1 * 0.3f + 0.05f*(shape_color[type][i]);
            cf[i*4 + 3] = 1.0f;
        }
        tm.addTexture(-1, vf, tf, cf);
    }
    public void drawCursor(TextureManager tm, TouchListener touchListener, int x, int y, int z, float depth, int color) {
        float    vf[] = new float[4*3*3];
        float    cf[] = new float[4*3*4];
        int      xx,yy,zz;
        int      angle[]={0,1,3,2};
        Q_LEN = BASE_LEN * touchListener.getWidth() / 1280;

        x = Math.max(0, Math.min(this.x-1, x));
        y = Math.max(0, Math.min(this.y-1, y));

        for (int i = 0; i < 12; i++) {
            cf[i*4    ] = (color & 4)/4 * 1.0f;
            cf[i*4 + 1] = (color & 2)/2 * 1.0f;
            cf[i*4 + 2] = (color & 1)/1 * 1.0f;
            cf[i*4 + 3] = 0.6f;
        }
        for (int i = 0; i < 4; i++) {
            int j=(i + 1) % 4;

            xx=x + (angle[i] & 1);
            yy=y + (angle[i] & 2) / 2;
            zz=z;

            vf[ i * 3 * 3]    =(get2dx(xx, yy, zz) - touchListener.getDragX()) * touchListener.getScaleFactor();
            vf[ i * 3 * 3 + 1]=(get2dy(xx, yy, zz) - touchListener.getDragY()) * touchListener.getScaleFactor();
            vf[ i * 3 * 3 + 2]=tm.getDepth();
            vf[(j * 3+1) * 3]    =(get2dx(xx, yy, zz) - touchListener.getDragX()) * touchListener.getScaleFactor();
            vf[(j * 3+1) * 3 + 1]=(get2dy(xx, yy, zz) - touchListener.getDragY()) * touchListener.getScaleFactor();
            vf[(j * 3+1) * 3 + 2]=tm.getDepth();
            vf[(j * 3+2) * 3]    =((get2dx(x, y, zz)+get2dx(x+1, y+1, zz))/2 - touchListener.getDragX()) * touchListener.getScaleFactor();
            vf[(j * 3+2) * 3 + 1]=((get2dy(x, y, zz)+get2dy(x+1, y+1, zz))/2 - touchListener.getDragY()) * touchListener.getScaleFactor();
            vf[(j * 3+2) * 3 + 2]=tm.getDepth();
            cf[(i*3+2)*4    ] = (color & 4)/4 * 1.0f;
            cf[(i*3+2)*4 + 1] = (color & 2)/2 * 1.0f;
            cf[(i*3+2)*4 + 2] = (color & 1)/1 * 1.0f;
            cf[(i*3+2)*4 + 3] = depth*0.6f;
        }
        tm.addTexture(-1, vf, null, cf);
    }
    public void drawMap(TextureManager tm, TouchListener tl) {
        int  left  = getIsoX((int)tl.getDragX(), (int)tl.getDragY());
        int  top   = getIsoY((int)tl.getDragX(), (int)tl.getDragY());
        Q_LEN = BASE_LEN * tl.getWidth() / 1280;
        int x,y;
        int viewX, viewY;
        float r = 1.0f;
        for (viewY = -3; viewY < tl.getHeight()*2/(Q_LEN)+3; viewY ++) {
            x = left + viewY/2-1;
            y = top + (viewY+1)/2;
            r = 1.0f*(viewY % 1);
            for (viewX = 0; viewX < tl.getWidth()/(Q_LEN*2)+2; viewX ++) {
                if (x >= 0 && y >= 0 &&
                    x < this.x && y < this.y) {
                    drawShape(tm, tl, x, y, height[y][x], 0, zone[y][x]);
                    if (height[y][x] > 0) {
                        drawShape(tm, tl, x, y, height[y][x], 1, zone[y][x]);
                        drawShape(tm, tl, x, y, height[y][x], 2, zone[y][x]);
                    }
                }
                x ++;
                y --;
            }
        }
    }

}
