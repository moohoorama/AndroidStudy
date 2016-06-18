package com.example.yanoo.glexam.game;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.example.yanoo.glexam.CubeTile;
import com.example.yanoo.glexam.MainGLSurfaceView;
import com.example.yanoo.glexam.R;
import com.example.yanoo.glexam.graphic.Pos;
import com.example.yanoo.glexam.graphic.GLRenderer;
import com.example.yanoo.glexam.graphic.TButton;
import com.example.yanoo.glexam.graphic.TUI;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.NormalTouchListener;
import com.example.yanoo.glexam.touch.TouchListener;
import com.example.yanoo.glexam.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yanoo on 2016. 6. 7..
 */
public class CubeDrawer implements GameLogic {
    private TouchListener   mTouchListener= new NormalTouchListener(0,0);
    private CubeTile        mCubeTile = new CubeTile();

    @Override
    public TouchListener getTouchListener() {
        return mTouchListener;
    }

    private Pos cursor = new Pos();

    private int     CubeMap[][][] = new int [12][12][12];
    private TButton left   = new TButton(0.0f, 0.8f, 0.2f, 0.9f, Util.getRString(R.string.Left), new TUI.TUIListener() {
        @Override
        public void press(TouchListener.TouchEvent tl) {
            cursor.x--;
        }
        public void depress(TouchListener.TouchEvent tl) {}
    });
    private TButton right  = new TButton(0.4f, 0.8f, 0.6f, 0.9f, Util.getRString(R.string.Right), new TUI.TUIListener() {
        @Override
        public void press(TouchListener.TouchEvent tl) {
                cursor.x++;
        }
        public void depress(TouchListener.TouchEvent tl) {}
    });
    private TButton top    = new TButton(0.2f, 0.7f, 0.4f, 0.8f,Util.getRString(R.string.Up), new TUI.TUIListener() {
        @Override
        public void press(TouchListener.TouchEvent tl) {
                cursor.y--;
        }
        public void depress(TouchListener.TouchEvent tl) {}
    });
    private TButton bottom = new TButton(0.2f, 0.9f, 0.4f, 1.0f,Util.getRString(R.string.Down),  new TUI.TUIListener() {
        @Override
        public void press(TouchListener.TouchEvent tl) {
                cursor.y++;
        }
        public void depress(TouchListener.TouchEvent tl) {}
    });
    private TButton center = new TButton(0.2f, 0.8f, 0.4f, 0.9f,Util.getRString(R.string.Check),  new TUI.TUIListener() {
        @Override
        public void press(TouchListener.TouchEvent tl) {
            CubeMap[(int)cursor.z][(int)cursor.y][(int)cursor.x] =
                    1 - CubeMap[(int)cursor.z][(int)cursor.y][(int)cursor.x];
        }
        public void depress(TouchListener.TouchEvent tl) {}
    });

    public void          registUI() {
        GLRenderer.singletone.registTUI(left);
        GLRenderer.singletone.registTUI(right);
        GLRenderer.singletone.registTUI(top);
        GLRenderer.singletone.registTUI(bottom);
        GLRenderer.singletone.registTUI(center);
        GLRenderer.singletone.registTUI(
                new TButton(0.6f,0.8f,0.8f,0.9f, Util.getRString(R.string.Save), new TUI.TUIListener() {
                    @Override
                    public void press(TouchListener.TouchEvent tl) {
                        JSONObject obj = new JSONObject();
                        JSONArray  arr = new JSONArray();
                        try {
                            obj.put("z", CubeMap.length);
                            obj.put("y", CubeMap[0].length);
                            obj.put("x", CubeMap[0][0].length);
                            for (int[][] z:CubeMap) {
                                for (int[] y:z) {
                                    for (int x:y) {
                                        arr.put(x);
                                    }
                                }
                            }
                            obj.put("data", arr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String path =  "/map.json";
                        Util.writeFile(path, obj.toString().getBytes());
                        MainGLSurfaceView.sSingletone.toast(path + " Save");
                        Log.i("Save", obj.toString());
                    }
                    @Override
                    public void depress(TouchListener.TouchEvent tl) {
                    }
                })
        );
        GLRenderer.singletone.registTUI(
                new TButton(0.8f,0.8f,1.0f,0.9f, Util.getRString(R.string.Load), new TUI.TUIListener() {

                    @Override
                    public void press(TouchListener.TouchEvent tl) {
                        String path = "/map.json";
                        byte contents[] = Util.readFile(path);
                        if (contents == null) {
                            return;
                        }
                        String org = new String(contents);
                        Log.i("Load", org);
                        JSONObject obj =null;
                        try {
                            obj=new JSONObject(org);

                            int x = obj.getInt("x");
                            int y = obj.getInt("y");
                            int z = obj.getInt("z");

                            CubeMap = new int [z][y][x];

                            JSONArray arr = obj.getJSONArray("data");
                            for (int i = 0; i < arr.length(); i++) {
                                CubeMap[i/x/y][i/x % y][i % x] = arr.getInt(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MainGLSurfaceView.sSingletone.toast(path + "Load");
                    }

                    @Override
                    public void depress(TouchListener.TouchEvent tl) {

                    }
                })
        );
    }

    public CubeDrawer() {
    }

    @Override
    public void draw(TextureManager tm) {
        int     i;

        cursor.x = Math.max(0, Math.min(CubeMap[0][0].length-1, cursor.x));
        cursor.y = Math.max(0, Math.min(CubeMap[0].length-1, cursor.y));
        cursor.z = Math.max(0, Math.min(CubeMap.length-1, cursor.z));

        float check = 0;
        for (int z = CubeMap.length-1; z >= 0 ; z --) {
            for (int y=0; y < CubeMap[z].length; y++) {
                for (int x = 0; x < CubeMap[z][y].length; x ++) {
                    check = 0.0f;
                    if (x == cursor.x && y == cursor.y && z == cursor.z) {
                        check += 0.1f;
                    }
                    if (CubeMap[z][y][x] > 0) {
                        check+=0.3f;
                    }
                    if (check > 0.0f) {
                        mCubeTile.drawShape(tm,mTouchListener, x, y, z, 0, check);
                        mCubeTile.drawShape(tm,mTouchListener, x, y, z, 1, check);
                        mCubeTile.drawShape(tm,mTouchListener, x, y, z, 2, check);
                    }
                }
            }
        }

        tm.addText('l',50, 50, "frame", 64, 1.0f,1.0f,1.0f,1.0f);
        tm.addText('l',50, 150, "abcd", 64, 1.0f,1.0f,1.0f,1.0f);
        tm.addText('l',50, 250, "위", 128, 1.0f,1.0f,1.0f,1.0f);
        tm.addTextureRect(0,new RectF(0,0,512,512), new RectF(0.0f,0.0f,1.0f,1.0f),1.0f,1.0f,1.0f,1.0f);
        /*
        if (press_info[4]) {
            CubeMap[(int)cursor.z][(int)cursor.y][(int)cursor.x] =
                    1 - CubeMap[(int)cursor.z][(int)cursor.y][(int)cursor.x];
        }*/

    }
}
