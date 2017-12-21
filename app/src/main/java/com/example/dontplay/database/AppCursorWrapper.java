package com.example.dontplay.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.dontplay.model.App;
import com.example.dontplay.util.AppUtil;

/**
 * Created by Crazy贵子 on 2017/12/18.
 */

public class AppCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public AppCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    // 从数据库中获取app
    public App getApp() {
        String name = getString(getColumnIndex(AppDbSchema.AppTable.Cols.NAME));
        String packageName = getString(getColumnIndex(AppDbSchema.AppTable.Cols.PACKAGE_NAME));
        Drawable icon = AppUtil.getIconFromByte(getBlob(getColumnIndex(AppDbSchema.AppTable.Cols.ICON)));
        int isTaboo = getInt(getColumnIndex(AppDbSchema.AppTable.Cols.ISTABOO));

        App app = new App(name, packageName, icon, isTaboo);
        return app;
    }


}
