package com.example.yanoo.glexam.graphic.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.yanoo.glexam.GLActivity;
import com.example.yanoo.glexam.MainGLSurfaceView;
import com.example.yanoo.glexam.graphic.PosI;
import com.example.yanoo.glexam.graphic.TColor;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;
import com.example.yanoo.glexam.util.Sprite;
import com.example.yanoo.glexam.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

/** tiso map ui
 * Created by Yanoo on 2016. 6. 25..
 */
public class TIsoMap implements TUI {
    private static final int[][] shape_type= {
            {6,4,7,5,7,4},
            {6,2,7,3,7,2},
            {7,3,5,1,5,3},
    };
    private static final float[][] shape_color= {
            {1,1,1,1,1,1},
            {1,0,1,0,1,0},
            {1,0,1,0,1,0},
    };

    public class TileModel {
        public TColor tc;
        public Sprite texture[];
        public Sprite object[];
        public int    textureType; /* 0:random */
        public int    objectType;  /* 0:random, 1:wall */

        public String name;
        public int    height;
        public int    movement[]; /* footman, horse, babarian, pirates, machine */

        public TileModel(String name, int height, int movement[], TColor tc, int texture[], int textureType, int object[], int objectType) {
            this.name = name;
            this.height = height;
            this.movement = movement;
            this.tc = tc;
            if (texture != null) {
                this.texture = new Sprite[texture.length];
                for (int i = 0; i < texture.length; i++) {
                    this.texture[i] = Sprite.getTileTexture(texture[i]);
                }
            } else {
                this.texture = null;
            }
            this.textureType = textureType;

            if (object != null) {
                this.object = new Sprite[object.length];
                for (int i = 0; i < object.length; i++) {
                    this.object[i] = Sprite.getTileObject(object[i]);
                }
            } else {
                this.object = null;
            }
            this.objectType = objectType;
        }
    }

    public static final int GRASS  = 0;
    public static final int RIVER  = 1;
    public static final int BRIDGE = 2;
    public static final int INHOUSE= 3;
    public static final int WALL   = 4;
    public static final int HILL   = 5;
    public static final int MOUNTAIN = 6;
    public static final int DESERT  = 7;
    public static final int FORSET  = 8;
    public static final int CASTLE  = 9;
    public static final int BILLET = 10;
    public static final int OUTSIDE = 11;

    public static final int OBJECT_NORMAL = 0;
    public static final int OBJECT_WALL = 1;
    public static final int OBJECT_BRIDGE = 2;
    public static final int OBJECT_FOREST = 3;

    public TileModel tileModel[] = {
            new TileModel("초원", 0, null,TColor.GREEN, new int[]{1,2,3,4}, 0, null, 0),
            new TileModel("강",  -1, null,TColor.BLUE,  new int[]{0},       0, null, 0),
            new TileModel("다리",-1, null,TColor.BROWN, new int[]{5},       0, new int[]{12,13,14,15}, OBJECT_BRIDGE),
            new TileModel("성내", 0, null,TColor.GRAY,  new int[]{5},       0, null, 0),
            new TileModel("성곽", 1, null,TColor.GRAY,  new int[]{5},       0, new int[]{ 9,11, 8,10}, OBJECT_WALL),
            new TileModel("언덕", 0, null,TColor.GREEN, new int[]{0},       0, new int[]{ 0, 1, 2, 3}, 0),
            new TileModel("산",   0, null,TColor.GREEN, new int[]{0},       0, new int[]{16,17,18,19}, 0),
            new TileModel("사막", 0, null,TColor.AMBER, new int[]{6},       0, null, 0),
            new TileModel("숲",   0, null,TColor.GREEN, new int[]{0},       0, new int[]{20,21,22,23}, OBJECT_FOREST),
            new TileModel("성",   0, null,TColor.GRAY,  new int[]{5},       0, new int[]{4}, OBJECT_NORMAL),
            new TileModel("진지", 0, null,TColor.BROWN, new int[]{5},       0, new int[]{5}, OBJECT_NORMAL),
            new TileModel("외각",-4, null,TColor.WHITE, new int[]{0},       0, null, 0),
    };

    public String[] getTileModelNames() {
        String[] ret = new String[tileModel.length];

        for (int i = 0; i < tileModel.length; i++) {
            ret[i] = tileModel[i].name;
        }

        return ret;
    }

    private boolean enable = true;

    private int cursor_type = 0; /* 0 : single, 1 : box */

    private int x;
    private int y;

    private int zone[][];

    private int cursor_x = 0;
    private int cursor_y = 0;

    private int scroll_x = 0;
    private int scroll_y = 0;

    private int scroll_begin_x = 0;
    private int scroll_begin_y = 0;
    private long last_click_ms = 0;

    private PosI clickPos = null;

    /* 세로길이이며, 가로의 반 */
    private int BASE_LEN = 120;
    private int Q_LEN = 32;

    public TIsoMap(int x, int y, int cursor_type) {
        reset(x,y);
        this.cursor_type = cursor_type;
    }

    public void reset(int x,int y) {
        this.x = x;
        this.y = y;

        this.zone   = new int[y][x];
        for (int j = 0; j < y; j++) {
            for (int i=0; i < x; i++) {
                this.zone[j][i] = 0;
            }
        }
    }

    public void makeMinimap(GL10 gl, TextureManager tm, int idx) {
        int bitmapBuffer[] = new int[x * y];
        int bitmapSource[] = new int[x * y];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        int offset1, offset2;
        for (int i = 0; i < y; i++) {
            offset1 = i * x;
            offset2 = (y - i - 1) * x;
            for (int j = 0; j < x; j++) {
                int col = tileModel[getZone(j,i)].tc.getInt();
                bitmapSource[offset1 + j] = col;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitmapSource, x, y, Bitmap.Config.ARGB_8888);
        tm.setTexture(gl,idx,bitmap,true);
    }

    public void load(String path) {
        byte contents[] = Util.readFile(path);
        if (contents == null) {
            return;
        }
        String org = new String(contents);
        try {
            JSONObject obj = new JSONObject(new String(org));
            TIsoMap newMap= new TIsoMap(obj.getInt("x"), obj.getInt("y"), cursor_type);

            JSONArray arr = obj.getJSONArray("zone");
            if (arr.length() != newMap.x * newMap.y) {
                return;
            }
            for (int i = 0; i < arr.length(); i++) {
                newMap.zone[i/ newMap.x][i % newMap.x] = arr.getInt(i);
            }

            this.x      = newMap.x;
            this.y      = newMap.y;
            this.zone   = newMap.zone;

            MainGLSurfaceView.sSingletone.toast(path + "Load");

            return;
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }
    public void save(String path) {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        try {
            obj.put("x", x);
            obj.put("y", y);

            arr = new JSONArray();
            for (int[] y:zone) {
                for (int x:y) {
                    arr.put(x);
                }
            }
            obj.put("zone", arr);
            Util.writeFile(path, obj.toString().getBytes());
            MainGLSurfaceView.sSingletone.toast(path + " Save");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public PosI getClick() {
        PosI ret = clickPos;
        clickPos = null;
        return ret;
    }
    public int getCursor_x() {
        return cursor_x;
    }
    public int getCursor_y() {
        return cursor_y;
    }

    public int boundary_x(int _x) {
        return Math.max(0, Math.min(getX() - 1, _x));
    }
    public int boundary_y(int _y) {
        return Math.max(0, Math.min(getY() - 1, _y));
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

    public void setZone(int x, int y, int z) {
        zone[boundary_y(y)][boundary_x(x)] = z;
    }
    public int getZone(int x, int y) {
        if (x < 0 || y < 0 || x >= getX() || y >= getY()) {
            return OUTSIDE;
        }
        return zone[boundary_y(y)][boundary_x(x)];
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void setBrush(int x,int y,int brush) {
        x = boundary_x(x);
        y = boundary_y(y);
        zone[y][x] = brush;
    }
    public void drawShape(TextureManager tm, TouchListener touchListener, int x, int y, int z, int type, int zone) {
        float     tf[] = new float[6*2];
        float     vf[] = new float[6*3];
        float     cf[] = new float[6*4];
        int       xx,yy,zz;

        TileModel model = tileModel[zone];
        TColor    tc    = model.tc;
        Sprite    tile  = model.texture[0];

        if (model.textureType == 0) {
            tile = model.texture[(x+y) % model.texture.length];
        }

        for (int i = 0; i < 6; i++) {
            xx = (x+(shape_type[type][i] & 1));
            yy = (y+(shape_type[type][i] & 2)/2);
            zz = (z-tileModel[OUTSIDE].height)*((shape_type[type][i] & 4)/4);
            tf[i*2    ] = tile.getX((shape_type[0][i] & 1));
            tf[i*2 + 1] = tile.getY((shape_type[0][i] & 2)/2);

            vf[i*3    ] = (get2dx(xx,yy,zz+tileModel[OUTSIDE].height) - scroll_x);
            vf[i*3 + 1] = (get2dy(xx,yy,zz+tileModel[OUTSIDE].height) - scroll_y);
            vf[i*3 + 2] = tm.getDepth();

            cf[i*4    ] = (zone & 4)/4 * 0.3f + 0.05f*(shape_color[type][i]);
            cf[i*4 + 1] = (zone & 2)/2 * 0.3f + 0.05f*(shape_color[type][i]);
            cf[i*4 + 2] = (zone & 1)/1 * 0.3f + 0.05f*(shape_color[type][i]);
            cf[i*4 + 3] = 1.0f;

            cf[i*4    ] = tc.r - (type)*0.20f + 0.05f*(shape_color[type][i])*z;
            cf[i*4 + 1] = tc.g - (type)*0.20f + 0.05f*(shape_color[type][i])*z;
            cf[i*4 + 2] = tc.b - (type)*0.20f + 0.05f*(shape_color[type][i])*z;
            cf[i*4 + 3] = 1.0f;
        }
        tm.addTexture(tile.bitmapIdx, vf, tf, cf);
    }

    void drawObject(TextureManager tm, Sprite spr, int x,int y, int z){
        tm.addTexture(spr.bitmapIdx,
                tm.dot4To6Point(new RectF(
                        get2dx(x, y + 1, z) - scroll_x,
                        get2dy(x, y - 1, z) - scroll_y,
                        get2dx(x + 1, y, z) - scroll_x,
                        get2dy(x + 1, y + 2, z) - scroll_y
                )),
                spr.txPoints,
                tm.rgbToPoint(TColor.WHITE, 6));
    }

    public void drawTile(TextureManager tm, TouchListener touchListener, int x, int y) {
        int       zone = getZone(x,y);
        TileModel model = tileModel[zone];
        int       z     = model.height;

        Q_LEN = BASE_LEN * touchListener.getWidth() / 1280;

        if (z > tileModel[getZone(x+1,y)].height) {
            drawShape(tm, touchListener, x, y, z, 2, zone);
        }
        if (z > tileModel[getZone(x,y+1)].height) {
            drawShape(tm,touchListener,x,y,z,1,zone);
        }
        if (model.objectType == OBJECT_BRIDGE) {
            drawShape(tm, touchListener, x, y, z, 0, RIVER);
            drawShape(tm, touchListener, x, y, 0, 0, zone);
        } else {
            drawShape(tm, touchListener, x, y, z, 0, zone);
        }

        if (model.object != null) {
            if (model.objectType == OBJECT_WALL &&
                model.object.length == 4) { /* wall */
                boolean drawList[] = {
                        (getZone(x, y - 1) != zone),
                        (getZone(x - 1, y) != zone),
                        (getZone(x, y + 1) != zone),
                        (getZone(x + 1, y) != zone),
                };
                int i;
                for (i = 0; i < drawList.length; i++) {
                    if (drawList[i]) {
                        Sprite spr = model.object[i];
                        drawObject(tm, spr, x, y, z);
                    }
                }
            }
            if (model.objectType == OBJECT_BRIDGE &&
                    model.object.length == 4) { /* bridge */
                boolean drawList[] = {
                        (getZone(x, y - 1) == RIVER),
                        (getZone(x - 1, y) == RIVER),
                        (getZone(x, y + 1) == RIVER),
                        (getZone(x + 1, y) == RIVER),
                };
                int i;
                for (i = 0; i < drawList.length; i++) {
                    if (drawList[i]) {
                        Sprite spr = model.object[i];;
                        drawObject(tm, spr, x, y, z);
                    }
                }
            }
            if (model.objectType == OBJECT_NORMAL) {
                int tileIdx = (x + y) % model.object.length;
                Sprite spr = model.object[tileIdx];
                drawObject(tm, spr, x, y, z);
            }
            if (model.objectType == OBJECT_FOREST) {
                int tileIdx = (x + y) % (model.object.length/2);
                for (int i=0;i<2;i++) {
                    Sprite spr = model.object[tileIdx*2+i];
                    drawObject(tm, spr, x, y, z);
                }
            }
        }
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

            vf[ i * 3 * 3]    =(get2dx(xx, yy, zz) - scroll_x);
            vf[ i * 3 * 3 + 1]=(get2dy(xx, yy, zz) - scroll_y);
            vf[ i * 3 * 3 + 2]=tm.getDepth();
            vf[(j * 3+1) * 3]    =(get2dx(xx, yy, zz) - scroll_x);
            vf[(j * 3+1) * 3 + 1]=(get2dy(xx, yy, zz) - scroll_y);
            vf[(j * 3+1) * 3 + 2]=tm.getDepth();
            vf[(j * 3+2) * 3]    =((get2dx(x, y, zz)+get2dx(x+1, y+1, zz))/2 - scroll_x);
            vf[(j * 3+2) * 3 + 1]=((get2dy(x, y, zz)+get2dy(x+1, y+1, zz))/2 - scroll_y);
            vf[(j * 3+2) * 3 + 2]=tm.getDepth();
            cf[(i*3+2)*4    ] = (color & 4)/4 * 1.0f;
            cf[(i*3+2)*4 + 1] = (color & 2)/2 * 1.0f;
            cf[(i*3+2)*4 + 2] = (color & 1)/1 * 1.0f;
            cf[(i*3+2)*4 + 3] = depth*0.6f;
        }
        tm.addTexture(-1, vf, null, cf);
    }
    public void drawMap(TextureManager tm, TouchListener tl) {
        int  left  = getIsoX(scroll_x, scroll_y);
        int  top   = getIsoY(scroll_x, scroll_y);
        Q_LEN = BASE_LEN * tl.getWidth() / 1280;
        int x,y;
        int viewX, viewY;
        float r = 1.0f;
        for (viewY = -3; viewY < tl.getHeight()*2/(Q_LEN)+3; viewY ++) {
            x = left + viewY/2-1;
            y = top + (viewY+1)/2;
            r = 1.0f*(viewY % 1);
            for (viewX = 0; viewX < tl.getWidth()*0.8f/(Q_LEN*2)+2; viewX ++) {
                if (x >= 0 && y >= 0 &&
                    x < this.x && y < this.y) {
                    drawTile(tm, tl, x, y);
                }
                x ++;
                y --;
            }
        }
        if (cursor_type == 1) {
            drawCursor(tm, tl,
                    cursor_x,
                    cursor_y,
                    0,
                    0.3f + ((float)Math.sin(tm.getRunSequence()*Math.PI/30))*0.4f,3);
        }
    }


    @Override
    public void Draw(TextureManager tm, TouchListener tl, boolean enable) {
        TouchListener.TouchEvent te = tl.getTouchEvent();
        this.enable = enable;
        if (tl.getClickX() < tl.getWidth()*0.8 && enable) {
            if (te.count == 1) {
                if (te.phase <= 1) {
                    if (te.phase > 0) {
                        if (System.currentTimeMillis() - last_click_ms > 100) {
                            scroll_x+=scroll_begin_x - (int) te.pos.x;
                            scroll_y+=scroll_begin_y - (int) te.pos.y;
                            last_click_ms = 0;
                        }
                    } else {
                        last_click_ms = System.currentTimeMillis();
                    }
                    scroll_begin_x=(int) te.pos.x;
                    scroll_begin_y=(int) te.pos.y;
                }

                cursor_x=boundary_x(getIsoX((int)te.multi_x[0] + scroll_x, (int)te.multi_y[0] + scroll_y));
                cursor_y=boundary_y(getIsoY((int)te.multi_x[0] + scroll_x, (int)te.multi_y[0] + scroll_y));

                if (te.phase == 2 && last_click_ms != 0) {
                    clickPos = new PosI(cursor_x, cursor_y, 0);
                }
            }
        }

        drawMap(tm, tl);
        /*
        drawCursor(tm, tl,
                cursor_x,
                cursor_y,
                0,
                0.3f + ((float)Math.sin(tm.getRunSequence()*Math.PI/30))*0.4f,3);
        drawCursor(tm, tl,
                cursor_x,
                cursor_y,
                getHeight(cursor_x,cursor_y),
                0.3f + ((float)Math.sin(tm.getRunSequence()*Math.PI/30))*0.4f,3);
        */
    }

}
