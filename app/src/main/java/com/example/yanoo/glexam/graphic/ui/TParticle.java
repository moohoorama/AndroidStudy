package com.example.yanoo.glexam.graphic.ui;

import android.graphics.RectF;
import android.util.Log;

import com.example.yanoo.glexam.graphic.TColor;
import com.example.yanoo.glexam.graphic.TextureManager;
import com.example.yanoo.glexam.touch.TouchListener;
import com.example.yanoo.glexam.util.Sprite;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by kyw on 2016-06-30.
 */
public class TParticle implements TUI {
    static private  final float GRAVITY  = 1.0f/2000.0f;
    static private  final float MAX_LIFE = 20.0f;

    private LinkedList<TUI> particles = new LinkedList<TUI>();

    public void remove(TUI tui) {
        particles.remove(tui);
    }

    public void createPaperDust(float x, float y, float scale) {
        int i;
        float gravity = 1.0f/2000.0f;
        for (i = 0; i < 32; i++) {
            double angle = Math.random() * Math.PI * 2;
            double sub_scale = Math.random();
            float xx = (float)(Math.cos(angle) * scale *(0.5+sub_scale))/ 2000.0f;
            float yy = (float)(Math.sin(angle) * scale *(0.5+sub_scale))/ 2000.0f - gravity*MAX_LIFE*0.4f;
            particles.add(new TPaperDust(this, x, y, xx, yy, gravity, 20, 0, 1, TColor.RED));
        }
    }

    @Override
    public void Draw(GL10 gl, TextureManager tm, TouchListener tl, boolean enable) {
        LinkedList<TUI> clone = (LinkedList<TUI>)particles.clone();
        for (TUI one : clone) {
            one.Draw(gl, tm, tl, enable);
        }
    }

    public class TPaperDust implements TUI {
        TParticle parents;
        float     x;
        float     y;
        float     xx;
        float     yy;
        float     gravity;
        Sprite    sprite;
        TColor    tc;
        int       spin;
        int       life;
        int       max_life;

        public TPaperDust(TParticle parents, float x, float y, float xx, float yy, float gravity, int life, int sprite_idx, int spin, TColor tc) {
            this.parents = parents;
            this.x  = x;
            this.y  = y;
            this.xx = xx;
            this.yy = yy;
            this.gravity = gravity;
            this.life = life;
            this.max_life = life;
            this.sprite = Sprite.getTexture(
                    TextureManager.PARTICLE_IDX,sprite_idx,
                    16,16,1024,1024);
            this.spin = spin;
            this.tc = tc;
        }
        public void Draw(GL10 gl, TextureManager tm, TouchListener tl, boolean enable) {
            int l = (int)((x-0.01f)*tl.getWidth());
            int t = (int)((y-0.01f)*tl.getHeight());
            int r = (int)((x+0.01f)*tl.getWidth());
            int b = (int)((y+0.01f)*tl.getHeight());
            float ani = life*1.0f / max_life;
            int xi = (int)(x * tl.getWidth());
            int yi = (int)(y * tl.getHeight());

            float scale   = 0.01f * tl.getWidth();
            float x_arrow = (float)Math.cos(Math.PI*ani*1);
            float y_arrow = (float)Math.sin(Math.PI*ani*1);
            float z_arrow = (float)Math.cos(Math.PI*ani*8);

            /*
            tm.addTextureRect(-1,
                    new RectF(l,t,r,b),
                    null,
                    tc);
                    */
//            tm.addSprite(l,t,r,b, sprite,tc.MultiplyRGB(ani));
            float depth = tm.getDepth();
            tm.addTexture(
                    sprite.bitmapIdx,
                    tm.dotTo6Point(
                            xi - x_arrow*scale, yi - y_arrow*scale,
                            xi + x_arrow*scale, yi - y_arrow*scale,
                            xi + x_arrow*scale, yi + y_arrow*scale,
                            xi - x_arrow*scale, yi + y_arrow*scale
                    ),
                    sprite.txPoints,
                    tc.getColorPoints());

            x += xx;
            y += yy;
            yy += gravity;

            life --;
            if (life <= 0) {
                 parents.remove(this);
            }
        }
    }
}
