package com.example.yanoo.glexam.graphic;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.yanoo.glexam.Stopwatch;
import com.example.yanoo.glexam.game.CubeDrawer;
import com.example.yanoo.glexam.game.GameLogic;
import com.example.yanoo.glexam.game.TitleLogic;

import java.util.LinkedList;

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
    private GameLogic gameLogic = null;
    private GameLogic nextLogic = null;

    private LinkedList<TUI> TUIList= new LinkedList<TUI>();

    public GLRenderer(Context context) {
        singletone= this;
        textureManager= new TextureManager(context);
        gameLogic = new TitleLogic(this);
        gameLogic.registUI();
    }
    public void      changeGameLogic(GameLogic newLogic) {
        nextLogic = newLogic;
    }

    public GameLogic getGameLogic() {return gameLogic;}

    public void registTUI(TUI tui) {
        TUIList.add(tui);
    }
    public void deregistTUI(TUI tui) {
        TUIList.remove(tui);
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

        if (nextLogic != null) {
            TUIList.clear();
            nextLogic.getTouchListener().setScreenSize(
                    gameLogic.getTouchListener().getWidth(),
                    gameLogic.getTouchListener().getHeight());
            gameLogic = nextLogic;
            gameLogic.registUI();
            nextLogic = null;
        }

        gl.glClearColor(0, 0, 0, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();                        // Matrix 리셋

        sw.event("clear");

        if (textureManager.isPrepared()) {
            gameLogic.draw(textureManager);
            gameLogic.getTouchListener().act();

            for (TUI tui:TUIList) {
                tui.Draw(textureManager, gameLogic.getTouchListener());
            }
            sw.event("act");
        }
        textureManager.draw(this, gl, width, height);

        sw.event("draw");
        Log.d("draw", sw.toString());
    }
}
