package com.example.yanoo.glexam.util;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yanoo on 2016. 6. 10..
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "MandamiTestDB";
    private static final String DATABASE_TABLE = "T1";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE="CREATE TABLE " +DATABASE_TABLE+ "" +
            " (    ID    INTEGER PRIMARY    KEY AUTOINCREMENT, AGE INTEGER,   PHONE TEXT,    ADDR TEXT,    NAME TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
