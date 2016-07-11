package com.example.yanoo.glexam.graphic.ui;

import android.util.Log;

import com.example.yanoo.glexam.MainGLSurfaceView;
import com.example.yanoo.glexam.graphic.PosI;
import com.example.yanoo.glexam.graphic.TColor;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;
import com.example.yanoo.glexam.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** tiso map ui
 * Created by Yanoo on 2016. 6. 25..
 */
public class TIsoTile implements TUI {
    private boolean enable = true;

    private int x;
    private int y;

    private int height[][];

    private int cursor_x = 0;
    private int cursor_y = 0;

    private int scroll_x = 0;
    private int scroll_y = 0;

    private int scroll_begin_x = 0;
    private int scroll_begin_y = 0;
    private long last_click_ms = 0;

    private PosI clickPos = null;

    /* 세로길이이며, 가로의 반 */
    private int BASE_LEN = 40;
    private int Q_LEN = 16;

    public TIsoTile(int x, int y) {
        reset(x,y);
    }

    public void reset(int x,int y) {
        this.x = x;
        this.y = y;

        this.height = new int[y][x];

        int brush_size = 2;
        for(int i = 0; i < 2048; i++) {
            int xx,yy;
            xx = (int) (Math.random() * (x - brush_size * 2));
            yy = (int) (Math.random() * (y - brush_size * 2));

            for (int m = 0; m < brush_size; m++) {
                for (int j = -m; j <= m; j++) {
                    for (int k = -m; k <= m; k++) {
                        setBrush(xx + j+brush_size, yy + k+brush_size, 0);
                    }
                }
            }
            if (getHeight(xx+brush_size, yy+brush_size) == 8) {
                break;
            }
        }
    }

    public void load(String path) {
        byte contents[] = Util.readFile(path);
        if (contents == null) {
            return;
        }
        String org = new String(contents);
        try {
            JSONObject obj = new JSONObject(new String(org));
            TIsoTile newMap= new TIsoTile(obj.getInt("x"), obj.getInt("y"));

            JSONArray arr = obj.getJSONArray("height");
            Log.i("arr", String.format("%d,%d %d",
                    obj.getInt("x"),
                    obj.getInt("y"),
                    arr.length()));
            if (arr.length() != newMap.x * newMap.y) {
                return;
            }
            for (int i = 0; i < arr.length(); i++) {
                newMap.height[i/ newMap.x][i % newMap.x] = arr.getInt(i);
            }

            this.x      = newMap.x;
            this.y      = newMap.y;
            this.height = newMap.height;

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
            for (int[] y:height) {
                for (int x:y) {
                    arr.put(x);
                }
            }
            obj.put("height", arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Util.writeFile(path, obj.toString().getBytes());
        MainGLSurfaceView.sSingletone.toast(path + " Save");
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

    public int getHeight(int x, int y) {
        return height[boundary_y(y)][boundary_x(x)];
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
        return (x + y)*Q_LEN/2-z*Q_LEN/2;
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
        if (brush == 0) {
            height[y][x]++;
        } else {
            height[y][x]--;
        }
        height[y][x] = Math.max(0, Math.min(height[y][x], 8));
    }

    private static final int[] shape_type= {
            6,4,7,5,7,4
    };

    public void drawShape(TextureManager tm, TouchListener touchListener, int x, int y) {
        float    vf[] = new float[6*3];
        float    cf[] = new float[6*4];
        int      xx,yy,zz;
        TColor   tc = TColor.GREEN.MultiplyA(1.0f);

        Q_LEN = BASE_LEN * touchListener.getWidth() / 1280;

        for (int i = 0; i < 6; i++) {
            xx = (x+(shape_type[i] & 1));
            yy = (y+(shape_type[i] & 2)/2);
            zz = getHeight(xx,yy)*((shape_type[i] & 4)/4)*2;


            vf[i*3    ] = (get2dx(xx,yy,zz) - scroll_x);
            vf[i*3 + 1] = (get2dy(xx,yy,zz) - scroll_y);
            vf[i*3 + 2] = tm.getDepth();

            cf[i*4    ] = tc.r + zz/32.0f;
            cf[i*4 + 1] = tc.g + zz/32.0f;
            cf[i*4 + 2] = tc.b + zz/32.0f;
            cf[i*4 + 3] = 1.0f;
        }
        tm.addTexture(-1, vf, null, cf);
    }
    public void drawCursor(TextureManager tm, TouchListener touchListener, int x, int y, float depth, int color) {
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
            zz=getHeight(xx,yy)*2;

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
                    x < this.x-1 && y < this.y-1) {
                    drawShape(tm, tl, x, y);
                }
                x ++;
                y --;
            }
        }
        drawCursor(tm, tl,
                cursor_x,
                cursor_y,
                0.3f + ((float)Math.sin(tm.getRunSequence()*Math.PI/30))*0.4f,3);
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
