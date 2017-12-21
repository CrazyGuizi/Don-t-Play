package com.example.dontplay.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Crazy贵子 on 2017/12/18.
 */

public class AppBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "app.db";

    public AppBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + AppDbSchema.AppTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                AppDbSchema.AppTable.Cols.NAME + "," +
                AppDbSchema.AppTable.Cols.PACKAGE_NAME + "," +
                AppDbSchema.AppTable.Cols.ICON + "," +
                AppDbSchema.AppTable.Cols.ISTABOO + " integer" + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
