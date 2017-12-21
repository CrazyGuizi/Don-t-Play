package com.example.dontplay.broadcast;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.dontplay.model.App;
import com.example.dontplay.model.AppLab;
import com.example.dontplay.service.MonitorService;
import com.example.dontplay.util.AppUtil;

import java.util.List;

public class AppReceiver extends BroadcastReceiver {

    private static final String TAG = "AppReceiver";

    private static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    private static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    private String mPackageName;
    
    private List<App> mApps; // 全部的App
    private App mAppFromDB; // 数据库中找到的

    // 返回appReceiver的实例并且注册了广播
    public static AppReceiver newInstance() {
        return new AppReceiver();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppLab appLab = AppLab.get(context);
        mApps = appLab.getAllApps(); // 这里获取数据库中全部的应用，用于实现安装和卸载的相关逻辑实现功能

        if (intent.getAction().equals(ACTION_PACKAGE_ADDED)) { // 监听安装实现逻辑功能
            if ((mAppFromDB = getAppFromDB(intent)) != null) { // 在数据库中找得到这个软件
                if (mAppFromDB.getIsTaboo() != 0) { // 黑名单中的软件，一经安装或者更新立刻给监督人发送短信服务
                    AppUtil.sendMessage(context, AppUtil.FLAG_INSTALL, null, mAppFromDB.getName());
                    Log.d(TAG, "onReceive: 又安装了软件" + mPackageName);
                }
            }
            appLab.initDatabase(); // 刷新数据库

        } else if (intent.getAction().equals(ACTION_PACKAGE_REMOVED)) { // 监听卸载实现逻辑功能
                /*
                * 把还处于禁忌状态（即isTaboo=1）的软件卸载，卸载的时候把isTaboo设置为2，意思为虽然软件卸载了，但还是属于
                * 禁忌软件（被列入黑名单），在数据库中还保存着这一条记录，只不过禁忌列表扫描不出，这么做是为了方便在用户重
                * 新安装的时候通过扫描数据库的黑名单，如果发现在黑名单里，立刻把其列入禁忌列表中
                * */
                if ((mAppFromDB = getAppFromDB(intent)) != null) {
                    if (mAppFromDB.getIsTaboo() == 1) {
                        mAppFromDB.setIsTaboo(2);
                        appLab.updateApp(mAppFromDB);
                        AppUtil.sendMessage(context, AppUtil.FLAG_UNINSTALL, null, mAppFromDB.getName());
                        Log.d(TAG, "onReceive: 卸载了软件" + mPackageName);
                    } else if (mAppFromDB.getIsTaboo() == 0){ // 如果是非禁忌状态的软件，则从数据库中删除这一条记录
                        appLab.deleteApp(mAppFromDB);
                    }
                }
        }
    }
    
    private App getAppFromDB(Intent intent) {
        mPackageName = intent.getDataString(); // 这里注意，获取的数据为package:包名,所以我们要去掉package:
        mPackageName = mPackageName.replaceAll("package:", "");
        Log.d(TAG, "onReceive: " +mPackageName);
        for (App app : mApps) {
            if (mPackageName.equals(app.getPackageName())) { // 如果在数据库中找到了，则实例化appFromDB
                return app;
            }
        }
        return null;
    }
}
