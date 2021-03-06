package com.example.yanoo.glexam.game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.example.yanoo.glexam.GLActivity;
import com.example.yanoo.glexam.R;
import com.example.yanoo.glexam.graphic.PosI;
import com.example.yanoo.glexam.graphic.ui.TIsoMap;
import com.example.yanoo.glexam.graphic.GLRenderer;
import com.example.yanoo.glexam.graphic.TColor;
import com.example.yanoo.glexam.graphic.ui.TButton;
import com.example.yanoo.glexam.graphic.ui.THDragBar;
import com.example.yanoo.glexam.graphic.ui.TPanel;
import com.example.yanoo.glexam.graphic.ui.TSelectBox;
import com.example.yanoo.glexam.graphic.ui.TUI;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.NormalTouchListener;
import com.example.yanoo.glexam.touch.TouchListener;
import com.example.yanoo.glexam.util.Util;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

/** map editor
 * Created by Yanoo on 2016. 6. 7..
 */
public class MapEditor implements GameLogic {
    private NormalTouchListener   mTouchListener= new NormalTouchListener(0,0);

    @Override
    public TouchListener getTouchListener() {
        return mTouchListener;
    }

    private int     newMapX  = 24;
    private int     newMapY  = 24;
    private TIsoMap tMap= new TIsoMap(24,24,1);
    private int     brush = 0;
    private String  map_name = "0";
    private Boolean getTexture = false;

    public void          registUI(final GLRenderer renderer) {
        LinkedList<TUI> list = new LinkedList<TUI>();
        list.add(tMap);
        list.add(new TPanel(0.8f, 0.0f, 1.0f, 1.0f, ""));
        list.add(new TSelectBox(0.8f, 0.0f, 1.0f, 0.6f, tMap.getTileModelNames(), new TSelectBox.Listener() {
            @Override
            public void press(int idx, String value) {
                brush = idx;
            }
        }));
        list.add(new TButton(0.8f, 0.6f, 1.0f, 0.7f,Util.getRString(R.string.Reset),  new TButton.Listener() {
            @Override
            public void press(TouchListener.TouchEvent tl) {
            }
            public void depress(TouchListener.TouchEvent tl) {
                newMapX = 12;
                newMapY = 12;
                renderer.pushUI(new TUI[] {
                        new TPanel(0.1f, 0.1f, 0.8f, 0.8f, ""),
                        TPanel.makeTransparentPanel(0.12f, 0.22f, 0.18f, 0.28f, "X 크기"),
                        TPanel.makeTransparentPanel(0.12f, 0.32f, 0.18f, 0.38f, "Y 크기"),
                        new THDragBar(0.2f,0.25f,0.5f,
                                new String[] { "12","16","20","24"}, new THDragBar.Listener() {
                            public void press(int idx, String value) {
                                newMapX = Integer.parseInt(value);
                            }
                        }),
                        new THDragBar(0.2f,0.35f,0.5f,
                                new String[] { "12","16","20","24"}, new THDragBar.Listener() {
                            public void press(int idx, String value) {
                                newMapY = Integer.parseInt(value);
                            }
                        }),
                        new TButton(0.6f, 0.7f, 0.7f, 0.8f,Util.getRString(R.string.Reset), new TButton.Listener() {
                            public void press(TouchListener.TouchEvent tl) {
                            }
                            public void depress(TouchListener.TouchEvent tl) {
                                tMap.reset(newMapX,newMapY);
                                renderer.popUI();
                            }}),
                        new TButton(0.7f, 0.7f, 0.8f, 0.8f,Util.getRString(R.string.Exit), new TButton.Listener() {
                            public void press(TouchListener.TouchEvent tl) {
                            }
                            public void depress(TouchListener.TouchEvent tl) {
                                renderer.popUI();
                            }})
                });
            }
        }));

        list.add(
                new TButton(0.8f,0.7f,1.0f,0.8f, Util.getRString(R.string.Load), new TButton.Listener() {
                    public void press(TouchListener.TouchEvent tl) {
                    }
                    public void depress(TouchListener.TouchEvent tl) {
                        map_name = "0.map";
                        renderer.pushUI(new TUI[] {
                                new TPanel(0.1f, 0.1f, 0.8f, 0.8f, ""),
                                new TSelectBox(0.2f,0.2f,0.5f,0.7f,Util.StringNumberArray(30),new TSelectBox.Listener() {

                                    @Override
                                    public void press(int idx, String value) {
                                        map_name = value + ".map";
                                        tMap.load(map_name);
                                        getTexture = false;
                                    }
                                }),
                                new TButton(0.7f, 0.6f, 0.8f, 0.7f,Util.getRString(R.string.Load), new TButton.Listener() {
                                    public void press(TouchListener.TouchEvent tl) {
                                    }
                                    public void depress(TouchListener.TouchEvent tl) {
                                        tMap.load(map_name);
                                        getTexture = false;
                                        renderer.popUI();
                                    }}),
                                new TButton(0.7f, 0.7f, 0.8f, 0.8f,Util.getRString(R.string.Exit), new TButton.Listener() {
                                    public void press(TouchListener.TouchEvent tl) {
                                    }
                                    public void depress(TouchListener.TouchEvent tl) {
                                        renderer.popUI();
                                    }})
                        });
                    }
                })
        );
        list.add(
                new TButton(0.8f,0.8f,1.0f,0.9f, Util.getRString(R.string.Save), new TButton.Listener() {
                    public void press(TouchListener.TouchEvent tl) {
                    }
                    public void depress(TouchListener.TouchEvent tl) {
                        map_name = "0";
                        renderer.pushUI(new TUI[] {
                                new TPanel(0.1f, 0.1f, 0.8f, 0.8f, ""),
                                new TSelectBox(0.2f,0.2f,0.7f,0.6f,Util.StringNumberArray(30),new TSelectBox.Listener() {

                                    @Override
                                    public void press(int idx, String value) {
                                        map_name = value;
                                    }
                                }),
                                new TButton(0.6f, 0.7f, 0.7f, 0.8f,Util.getRString(R.string.Save), new TButton.Listener() {
                                    public void press(TouchListener.TouchEvent tl) {
                                    }
                                    public void depress(TouchListener.TouchEvent tl) {
                                        tMap.save(map_name+".map");
                                        renderer.popUI();
                                    }}),
                                new TButton(0.7f, 0.7f, 0.8f, 0.8f,Util.getRString(R.string.Exit), new TButton.Listener() {
                                    public void press(TouchListener.TouchEvent tl) {
                                    }
                                    public void depress(TouchListener.TouchEvent tl) {
                                        renderer.popUI();
                                    }})
                        });
                    }
                })
        );
        list.add(
                new TButton(0.8f,0.9f,1.0f,1.0f, Util.getRString(R.string.Exit), new TButton.Listener() {
                    public void press(TouchListener.TouchEvent tl) {
                    }
                    public void depress(TouchListener.TouchEvent tl) {
                        renderer.reserveNextGameLogic(new TitleLogic());
                    }
                })
        );
        renderer.pushUI((TUI[])list.toArray(new TUI[list.size()]));
    }

    public MapEditor() {
    }

    public void act(TextureManager tm) {
    }
    public void draw(GL10 gl, TextureManager tm) {
        long curTimestamp = System.currentTimeMillis();
        if (timestamp4fps + 1000 < curTimestamp) {
            prevFPS = frame;
            frame = 0;
            timestamp4fps += 1000;
            if (timestamp4fps < curTimestamp) {
                timestamp4fps = curTimestamp;
            }
        }
        PosI click = tMap.getClick();
        if (click != null) {
            tMap.setBrush(click.x,click.y,brush);
        }


        if (!getTexture) {
            tMap.makeMinimap(gl, tm, tm.MINIMAP_IDX);
            getTexture = true;
        }

        tm.addTextureRect(tm.MINIMAP_IDX,
                new RectF(0.0f,0.0f,256,256),
                new RectF(0.0f,0.0f,1.0f,1.0f),
                TColor.WHITE);

        tm.addText(0,0,100,100,
                String.format("%d fps",
                        prevFPS),
                TColor.WHITE);
        frame ++;
    }
    private static int  frame = 0;
    private static int  prevFPS = 0;
    private static long timestamp4fps = 0;
}
