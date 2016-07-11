package com.example.yanoo.glexam.game;

import android.content.Intent;

import com.example.yanoo.glexam.GLActivity;
import com.example.yanoo.glexam.MainGLSurfaceView;
import com.example.yanoo.glexam.R;
import com.example.yanoo.glexam.graphic.GLRenderer;
import com.example.yanoo.glexam.graphic.ui.TButton;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.graphic.ui.TUI;
import com.example.yanoo.glexam.touch.NormalTouchListener;
import com.example.yanoo.glexam.touch.TouchListener;
import com.example.yanoo.glexam.util.Util;

import java.io.StringWriter;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Yanoo on 2016. 6. 18..
 */
public class TitleLogic implements GameLogic {
    private TouchListener   touchListener= new NormalTouchListener(0,0);

    public TitleLogic() {
    }
    public void          registUI(final GLRenderer renderer) {
        renderer.pushUI(new TUI[] {
            new TButton(0.1f, 0.1f, 0.9f, 0.2f, Util.getRString(R.string.Start),  new TButton.Listener() {
                @Override
                public void press(TouchListener.TouchEvent tl) {
                    //renderer.reserveNextGameLogic(new CubeDrawer());
                }
                public void depress(TouchListener.TouchEvent tl) {}
            }),
                    new TButton(0.1f, 0.2f, 0.9f, 0.3f, Util.getRString(R.string.Load),  new TButton.Listener() {
                @Override
                public void press(TouchListener.TouchEvent tl) {
                    Intent it = null;
                    // sudo error
                    it.setType("text/plain");
                }
                public void depress(TouchListener.TouchEvent tl) {}
            }),
                    new TButton(0.1f, 0.3f, 0.9f, 0.4f, Util.getRString(R.string.Setting),  new TButton.Listener() {
                @Override
                public void press(TouchListener.TouchEvent tl) {
                    MainGLSurfaceView.sSingletone.toast("승후 놀랐다");
                    String email[] = {"moohoorama@gmail.com"};
                    StringWriter sw = new StringWriter();
                    Intent it = new Intent(Intent.ACTION_SEND);
                    it.putExtra(Intent.EXTRA_EMAIL, email);
                    it.putExtra(Intent.EXTRA_TEXT,  sw.toString());
                    it.putExtra(Intent.EXTRA_SUBJECT, "[어플이름] 오류보고서");
                    it.setType("text/plain");
                    GLActivity.singletone.startActivity(Intent.createChooser(it, "Choose Email Client"));
                }
                public void depress(TouchListener.TouchEvent tl) {}
            }),
                    new TButton(0.1f, 0.4f, 0.9f, 0.5f, Util.getRString(R.string.MapEditor),  new TButton.Listener() {
                        @Override
                        public void press(TouchListener.TouchEvent tl) {
                            renderer.reserveNextGameLogic(new MapEditor());
                        }
                        public void depress(TouchListener.TouchEvent tl) {}
                    }),
                new TButton(0.1f, 0.5f, 0.9f, 0.6f, Util.getRString(R.string.TileEditor),  new TButton.Listener() {
                    @Override
                    public void press(TouchListener.TouchEvent tl) {
                        renderer.reserveNextGameLogic(new TileEditor());
                    }
                    public void depress(TouchListener.TouchEvent tl) {}
                }),
                    new TButton(0.1f, 0.8f, 0.9f, 0.9f, Util.getRString(R.string.Exit),  new TButton.Listener() {
                @Override
                public void press(TouchListener.TouchEvent tl) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                public void depress(TouchListener.TouchEvent tl) {}
            })
        });
    }

    @Override
    public TouchListener getTouchListener() {
        return touchListener;
    }

    public void act(TextureManager tm) {
    }

    @Override
    public void draw(GL10 gl, TextureManager tm) {
    }
}
