package com.example.yanoo.glexam.graphic;

import android.content.Context;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.yanoo.glexam.util.Stopwatch;
import com.example.yanoo.glexam.game.GameLogic;
import com.example.yanoo.glexam.game.TitleLogic;
import com.example.yanoo.glexam.graphic.ui.TUI;

import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Yanoo on 2016. 5. 25..
 */
public class GLRenderer implements GLSurfaceView.Renderer{
    static public GLRenderer singletone;

    private TextureManager textureManager;
    private int width= 1;
    private int height= 1;

    private static final int GUIDE_FPS = 60;

    private GameLogic gameLogic = null;
    private GameLogic nextLogic = null;

    private Stack<TUI[]>  TUIStack = new Stack<TUI[]>();

    /* UI를 Stack에 넣고 뺄때 FadeInFadeOut을 위해 존재하는 값 */
    private long stackPushTime = 0;
    /* GameLogic이 변경될때 사용하는 값 */
    private long changeTime = 0;
    private long endTime4fps, startTime4fps;

    private static final int changeDuration = 1000; //ms

    public GLRenderer(Context context) {
        singletone= this;
        textureManager= new TextureManager(context);
        reserveNextGameLogic(new TitleLogic());
        changeTime = System.currentTimeMillis();
        changeGameLogic();
    }
    public void changeGameLogic() {
        if (nextLogic == null) {
            return;
        }
        if (System.currentTimeMillis() >= changeTime) {
            TUIStack.clear();
            if (gameLogic != null) {
                nextLogic.getTouchListener().setScreenSize(
                        gameLogic.getTouchListener().getWidth(),
                        gameLogic.getTouchListener().getHeight());
            }
            gameLogic=nextLogic;
            gameLogic.registUI(this);
            nextLogic=null;
        }
    }
    public void reserveNextGameLogic(GameLogic newLogic) {
        nextLogic = newLogic;
        changeTime = System.currentTimeMillis() + changeDuration; // 500ms
    }

    public GameLogic getGameLogic() {return gameLogic;}

    public void pushUI(TUI []uilist) {
        TUIStack.push(uilist);
        stackPushTime = System.currentTimeMillis() + changeDuration/2;
    }
    public void popUI() {
        TUIStack.pop();
        changeTime = System.currentTimeMillis() - changeDuration/2;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_BLEND);
        gl.glFrontFace(GL10.GL_CW);     // 시계방향 그리기 설정
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        if (h == 0) {
            h = 1;
        }
        width= w;
        height= h;
        gl.glViewport(0, 0, w, h);         // ViewPort 리셋
        gl.glMatrixMode(GL10.GL_PROJECTION);        // MatrixMode를 Project Mode로
        gl.glLoadIdentity();                        // Matrix 리셋
        gl.glOrthof(0, w, h, 0, 1, -1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);         // Matrix를 ModelView Mode로 변환
        gl.glLoadIdentity();                        // Matrix 리셋
        textureManager.refresh(gl);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Stopwatch sw = new Stopwatch();
        long      curTime = System.currentTimeMillis();

        endTime4fps= System.currentTimeMillis();
        long duration = endTime4fps - startTime4fps;
        if (duration < 1000/GUIDE_FPS) {
            try {
                Thread.sleep(1000/GUIDE_FPS - duration);
            }catch (InterruptedException ie){

            }
        }
        startTime4fps= curTime;

        changeGameLogic();

        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();                        // Matrix 리셋

        sw.event("clear");

        if (!textureManager.isPrepared()) {
            textureManager.init(this,gl);
        }
        gameLogic.draw(textureManager);
        gameLogic.getTouchListener().act();

        Stack<TUI[]>  stack4draw = (Stack<TUI[]>)TUIStack.clone();
        for (int i = 0; i < stack4draw.size(); i++) {
            for (TUI tui : stack4draw.get(i)) {
                tui.Draw(textureManager, gameLogic.getTouchListener(), i == stack4draw.size()-1);
            }
            if (i == stack4draw.size()-2) {
                /* active stack */
                if (stackPushTime > curTime) {
                    textureManager.addTextureRect(-1,
                            new RectF(0, 0,
                                    gameLogic.getTouchListener().getWidth(), gameLogic.getTouchListener().getHeight()),
                            null,
                            new TColor(0.0f,0.0f,0.0f,0.5f - (float)(stackPushTime-curTime)/changeDuration));
                } else {
                    textureManager.addTextureRect(-1,
                            new RectF(0, 0,
                                    gameLogic.getTouchListener().getWidth(), gameLogic.getTouchListener().getHeight()),
                            null,
                            new TColor(0.0f,0.0f,0.0f,0.5f));
                }
            }
        }
        if (changeTime > curTime) {
            textureManager.addTextureRect(-1,
                    new RectF(0, 0,
                            gameLogic.getTouchListener().getWidth(), gameLogic.getTouchListener().getHeight()),
                    null,
                    new TColor(0.0f,0.0f,0.0f,1.0f - (float)(changeTime-curTime)/changeDuration));
        } else if (changeTime + changeDuration > curTime) {
            textureManager.addTextureRect(-1,
                    new RectF(0, 0,
                            gameLogic.getTouchListener().getWidth(), gameLogic.getTouchListener().getHeight()),
                    null,
                    new TColor(0.0f,0.0f,0.0f,1.0f - (float)(curTime-changeTime)/changeDuration));
        }

        textureManager.addTextureRect(-1,
                new RectF(0, 0,
                        gameLogic.getTouchListener().getWidth(), gameLogic.getTouchListener().getHeight()/2),
                null,
                new TColor(0.0f,0.0f,0.0f,0.0f));
        textureManager.addTextureRect(0,
                new RectF(0.0f,0.0f,gameLogic.getTouchListener().getWidth(),gameLogic.getTouchListener().getHeight()),
                new RectF(0.0f,0.0f,1.0f,1.0f),
                TColor.WHITE);
        sw.event("act");
        textureManager.draw(this, gl, width, height);

        sw.event("draw");
        Log.d("draw", sw.toString());
    }
}
