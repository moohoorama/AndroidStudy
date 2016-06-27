package com.example.yanoo.glexam.game;

import com.example.yanoo.glexam.CubeTile;
import com.example.yanoo.glexam.R;
import com.example.yanoo.glexam.gameobj.IsoMap;
import com.example.yanoo.glexam.graphic.GLRenderer;
import com.example.yanoo.glexam.graphic.TColor;
import com.example.yanoo.glexam.graphic.ui.TButton;
import com.example.yanoo.glexam.graphic.ui.THDragBar;
import com.example.yanoo.glexam.graphic.ui.TPanel;
import com.example.yanoo.glexam.graphic.ui.TUI;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.NormalTouchListener;
import com.example.yanoo.glexam.touch.TouchListener;
import com.example.yanoo.glexam.util.Util;

/**
 * Created by Yanoo on 2016. 6. 7..
 */
public class MapEditor implements GameLogic {
    private NormalTouchListener   mTouchListener= new NormalTouchListener(0,0);
    private CubeTile        mCubeTile = new CubeTile();

    @Override
    public TouchListener getTouchListener() {
        return mTouchListener;
    }

    private IsoMap isoMap = new IsoMap(24,24);
    private int    cursor_x = 0;
    private int    cursor_y = 0;
    private int    newMapX  = 16;
    private int    newMapY  = 16;

    public void          registUI(final GLRenderer renderer) {
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            renderer.registTUI(new TButton(0.8f, idx*0.1f, 0.9f, idx*0.1f+0.1f,
                    String.format("%s %d", Util.getRString(R.string.Height), idx), new TButton.Listener() {
                public void press(TouchListener.TouchEvent tl) {
                    isoMap.setHeight(cursor_x, cursor_y, idx);
                }

                public void depress(TouchListener.TouchEvent tl) {
                }
            }));
            renderer.registTUI(new TButton(0.9f, idx*0.1f, 1.0f, idx*0.1f+0.1f, Util.getRString(R.string.Check), new TButton.Listener() {
                public void press(TouchListener.TouchEvent tl) {
                    isoMap.setZone(cursor_x, cursor_y, idx);
                }

                public void depress(TouchListener.TouchEvent tl) {
                }
            }));
        }
        renderer.registTUI(new TButton(0.6f, 0.7f, 0.8f, 0.8f,Util.getRString(R.string.Reset),  new TButton.Listener() {
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
                        new THDragBar(0.2f,0.2f,0.5f,
                                new String[] { "12","16","20","24"}, new THDragBar.Listener() {
                            public void press(int idx, String value) {
                                newMapX = Integer.parseInt(value);
                            }
                        }),
                        new THDragBar(0.2f,0.3f,0.5f,
                                new String[] { "12","16","20","24"}, new THDragBar.Listener() {
                            public void press(int idx, String value) {
                                newMapY = Integer.parseInt(value);
                            }
                        }),
                        new TButton(0.6f, 0.7f, 0.7f, 0.8f,Util.getRString(R.string.Reset), new TButton.Listener() {
                            public void press(TouchListener.TouchEvent tl) {
                            }
                            public void depress(TouchListener.TouchEvent tl) {
                                isoMap = new IsoMap(newMapX,newMapY);
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

        renderer.registTUI(
                new TButton(0.6f,0.8f,0.8f,0.9f, Util.getRString(R.string.Save), new TButton.Listener() {
                    public void press(TouchListener.TouchEvent tl) {
                        isoMap.save("map.json");
                    }
                    public void depress(TouchListener.TouchEvent tl) {
                    }
                })
        );
        GLRenderer.singletone.registTUI(
                new TButton(0.8f,0.8f,1.0f,0.9f, Util.getRString(R.string.Load), new TButton.Listener() {
                    public void press(TouchListener.TouchEvent tl) {
                        IsoMap newIsoMap = IsoMap.load("map.json");
                        if (newIsoMap != null) {
                            isoMap = newIsoMap;
                        }
                    }
                    public void depress(TouchListener.TouchEvent tl) {
                    }
                })
        );
        GLRenderer.singletone.registTUI(
                new TButton(0.8f,0.9f,1.0f,1.0f, Util.getRString(R.string.Exit), new TButton.Listener() {
                    public void press(TouchListener.TouchEvent tl) {
                    }
                    public void depress(TouchListener.TouchEvent tl) {
                        renderer.reserveNextGameLogic(new TitleLogic());
                    }
                })
        );
    }

    public MapEditor() {
    }

    @Override
    public void draw(TextureManager tm) {
        int     i;

        if (getTouchListener().getClickX() < mTouchListener.getWidth()*0.8) {
            cursor_x=isoMap.getIsoX((int) getTouchListener().getLogX(), (int) getTouchListener().getLogY());
            cursor_y=isoMap.getIsoY((int) getTouchListener().getLogX(), (int) getTouchListener().getLogY());
            cursor_x=Math.max(0, Math.min(isoMap.getX() - 1, cursor_x));
            cursor_y=Math.max(0, Math.min(isoMap.getY() - 1, cursor_y));
        }

        isoMap.drawMap(tm, mTouchListener);

        isoMap.drawCursor(tm, getTouchListener(),
                cursor_x,
                cursor_y,
                0,
                0.3f + ((float)Math.sin(tm.getRunSequence()*Math.PI/30))*0.4f,3);
        isoMap.drawCursor(tm, getTouchListener(),
                cursor_x,
                cursor_y,
                isoMap.getHeight(cursor_x,cursor_y),
                0.3f + ((float)Math.sin(tm.getRunSequence()*Math.PI/30))*0.4f,3);

        long curTimestamp = System.currentTimeMillis();
        if (timestamp4fps + 1000 < curTimestamp) {
            prevFPS = frame;
            frame = 0;
            timestamp4fps += 1000;
            if (timestamp4fps < curTimestamp) {
                timestamp4fps = curTimestamp;
            }
        }
        tm.addText(0,0,800,100,
                String.format("%f, %f  %f,%f  %d %d fps",
                        getTouchListener().getLogX(),
                        getTouchListener().getLogY(),
                        getTouchListener().getClickX() - mTouchListener.getWidth()*0.8,
                        mTouchListener.mScroll.y,
                        mTouchListener.getTouchEvent().count,
                        prevFPS),
                TColor.WHITE);
        frame ++;
    }
    private static int  countList[] = new int[30];
    private static int  frame = 0;
    private static int  prevFPS = 0;
    private static long timestamp4fps = 0;
}
