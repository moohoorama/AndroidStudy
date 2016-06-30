package com.example.yanoo.glexam.graphic;

/**
 * Created by Yanoo on 2016. 6. 27..
 */
public class TColor {
    public float r;
    public float g;
    public float b;
    public float a;

    public TColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1.0f;
    }
    public TColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public TColor Grayscale() {
        float avg = (this.r + this.g + this.b)/3;
        return new TColor(avg,avg,avg,this.a);
    }

    public TColor MultiplyRGB(float v) {
        return new TColor(this.r*v,this.g*v,this.b*v,this.a);
    }

    public static final TColor WHITE  = new TColor(1.0f,1.0f,1.0f);
    public static final TColor BLACK  = new TColor(0,0,0);
    public static final TColor TRANSPARENT  = new TColor(0,0,0,0);

    public static final TColor RED    = new TColor(244/256.0f,67/256.0f,54/256.0f,1.0f);
    public static final TColor PINK   = new TColor(233/256.0f,30/256.0f,99/256.0f,1.0f);
    public static final TColor PURPLE = new TColor(156/256.0f,39/256.0f,176/256.0f,1.0f);
    public static final TColor INDIGO = new TColor(63/256.0f,81/256.0f,181/256.0f,1.0f);
    public static final TColor BLUE   = new TColor(33/256.0f,150/256.0f,243/256.0f,1.0f);
    public static final TColor LIGHTBLUE = new TColor(3/256.0f,169/256.0f,244/256.0f,1.0f);
    public static final TColor CYAN = new TColor(0/256.0f,188/256.0f,212/256.0f,1.0f);
    public static final TColor TEAL   = new TColor(0/256.0f,150/256.0f,136/256.0f,1.0f);
    public static final TColor GREEN  = new TColor(76/256.0f,175/256.0f,80/256.0f,1.0f);
    public static final TColor LIGHTGREEN = new TColor(139/256.0f,195/256.0f,74/256.0f,1.0f);
    public static final TColor LIME   = new TColor(205/256.0f,220/256.0f,57/256.0f,1.0f);
    public static final TColor YELLOW = new TColor(255/256.0f,235/256.0f,59/256.0f,1.0f);
    public static final TColor AMBER  = new TColor(255/256.0f,193/256.0f,7/256.0f,1.0f);
    public static final TColor ORANGE = new TColor(255/256.0f,152/256.0f,0/256.0f,1.0f);
    public static final TColor DEEPORANGE = new TColor(255/256.0f,87/256.0f,34/256.0f,1.0f);
    public static final TColor BROWN  = new TColor(121/256.0f,85/256.0f,72/256.0f,1.0f);
    public static final TColor GRAY   = new TColor(158/256.0f,158/256.0f,158/256.0f,1.0f);

    public static final TColor []IDX = {BLACK,BLUE,GREEN,CYAN,RED,PURPLE,YELLOW,GRAY,GRAY,LIGHTBLUE,LIGHTGREEN,TEAL,PINK,AMBER,LIME,WHITE};

}
