package com.example.dontplay.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.dontplay.database.AppBaseHelper;
import com.example.dontplay.database.AppCursorWrapper;
import com.example.dontplay.database.AppDbSchema;
import com.example.dontplay.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.dontplay.util.AppUtil.getAllApps;
import static com.example.dontplay.util.AppUtil.setIconToByte;

/**
 * Created by Crazy贵子 on 2017/12/18.
 */

public class AppLab {
    private static final String TAG = "AppLab";

    private static AppLab sAppLab; // 单例


    private Context mContext;
    private SQLiteDatabase mDatabase;
    private PackageManager mPackageManager;

    public static AppLab get(Context context) {
        if (sAppLab == null) {
            sAppLab = new AppLab(context);
        }
        return sAppLab;
    }

    private AppLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new AppBaseHelper(mContext).getWritableDatabase();
        mPackageManager = context.getPackageManager();
    }

    /*
    * 初始化数据库，找出手机上所有的应用，判断这些应用有没有加入数据库，如果没有则加入，否则不加入
    * 如果之前有在数据库则进一步判断其禁忌状态是否为2,如果是则改变为1。
    */
    public void initDatabase() {
        List<App> apps = AppUtil.getAllApps(mPackageManager); // 手机上的全部应用
        for (App app : apps) {
            boolean isExits = true;
            for (App app1 : getAllApps()) {
                if (app.getPackageName().equals(app1.getPackageName())) {
                    if (app1.getIsTaboo() == 2) {
                        app1.setIsTaboo(1);
                        updateApp(app1);
                    }
                    isExits = false;
                    break;
                }
            }
            if (isExits) {
                addApp(app);
            }
        }
    }

    // 获取不是禁忌状态的软件
    public List<App> getApps() {
        List<App> apps = new ArrayList<>(); // 未被设置为禁忌的软件
        AppCursorWrapper appCursorWrapper = queryApps(AppDbSchema.AppTable.Cols.ISTABOO + "=? ",new String[] {"0"});
        try {
            appCursorWrapper.moveToFirst();
            while (!appCursorWrapper.isAfterLast()) {
                apps.add(appCursorWrapper.getApp());
                appCursorWrapper.moveToNext();
            }
        } finally {
            appCursorWrapper.close();
        }

        return apps;
    }


    // 获取状态为禁忌的软件
    public List<App> getTabooApps() {
        List<App> apps = new ArrayList<>(); // 被设置为禁忌的软件
        AppCursorWrapper appCursorWrapper = queryApps(AppDbSchema.AppTable.Cols.ISTABOO + "=?",new String[] {""+1});
        try {
            appCursorWrapper.moveToFirst();
            while (!appCursorWrapper.isAfterLast()) {
                apps.add(appCursorWrapper.getApp());
                appCursorWrapper.moveToNext();
            }
        } finally {
            appCursorWrapper.close();
        }
        return apps;
    }

    // 获取黑名单软件
    public List<App> getBlackList() {
        List<App> apps = new ArrayList<>(); // 被设置为禁忌的软件
        AppCursorWrapper appCursorWrapper = queryApps(AppDbSchema.AppTable.Cols.ISTABOO + "=?",new String[] {""+2});
        try {
            appCursorWrapper.moveToFirst();
            while (!appCursorWrapper.isAfterLast()) {
                apps.add(appCursorWrapper.getApp());
                appCursorWrapper.moveToNext();
            }
        } finally {
            appCursorWrapper.close();
        }
        return apps;
    }

    // 获取全部软件，不管是不是禁忌
    public List<App> getAllApps() {
        List<App> allApps = new ArrayList<>(); // 全部软件
        AppCursorWrapper appCursorWrapper = queryApps(null, null);
        try {
            appCursorWrapper.moveToFirst();
            while (!appCursorWrapper.isAfterLast()) {
                allApps.add(appCursorWrapper.getApp());
                appCursorWrapper.moveToNext();
            }
        } finally {
            appCursorWrapper.close();
        }
        return allApps;
    }

    // 将App先转化为values，以便其存入数据库
    private static ContentValues getContentValues(App app) {
        ContentValues values = new ContentValues();
        values.put(AppDbSchema.AppTable.Cols.NAME,app.getName());
        values.put(AppDbSchema.AppTable.Cols.PACKAGE_NAME, app.getPackageName());
        values.put(AppDbSchema.AppTable.Cols.ICON, setIconToByte(app.getIcon()));
        values.put(AppDbSchema.AppTable.Cols.ISTABOO, app.getIsTaboo());
        return values;
    }

    // 插入新的应用软件
    public void addApp(App app) {
        ContentValues values = getContentValues(app);
        mDatabase.insert(AppDbSchema.AppTable.NAME, null, values);
    }


    // 更新记录
    public void updateApp(App app) {
        String name = app.getName();
        ContentValues values = getContentValues(app);
        mDatabase.update(AppDbSchema.AppTable.NAME, values,  AppDbSchema.AppTable.Cols.NAME + " = ? ", new String[] {name});
    }

    // 删除记录
    public void deleteApp (App app) {
        String packageName = app.getPackageName();
        mDatabase.delete(AppDbSchema.AppTable.NAME, AppDbSchema.AppTable.Cols.PACKAGE_NAME + " = ? ", new String[] {packageName});
    }

    // 查询数据库中的软件
    private AppCursorWrapper queryApps(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                AppDbSchema.AppTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new AppCursorWrapper(cursor);
    }


}
