package com.example.dontplay.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.dontplay.R;
import com.example.dontplay.broadcast.AppReceiver;
import com.example.dontplay.model.App;
import com.example.dontplay.model.AppLab;
import com.example.dontplay.util.AppUtil;

import java.util.List;

import static com.example.dontplay.util.AppUtil.FLAG_INSTALL;
import static com.example.dontplay.util.AppUtil.FLAG_START;
import static com.example.dontplay.util.AppUtil.FLAG_UNINSTALL;
import static com.example.dontplay.util.AppUtil.sendMessage;

public class MonitorService extends Service {
    private static final String TAG = "monitorService";

    private static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    private static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    private static final String SCHEME_PACKAGE = "package";

    private SharedPreferences mSharedPreferences; // 记录是否开启监督和监督者
    private Context mContext; // 上下文
    private AppLab mAppLab;
    private List<App> mApps; // 获取禁忌软件
    private AppReceiver mAppReceiver; // 接收安装和卸载软件的广播
    private MonitorBinder mBinder = new MonitorBinder();

    // 暂时用不到
    public class MonitorBinder extends Binder {

        // 重新安装禁忌软件的时候发短信
        public void monitorWhenInstall(String packageName) { // 通过包名匹配应用名
            String name = getAppName(packageName, mApps);
            if (!TextUtils.isEmpty(name)) {
                try {
                    sendMessage(mContext, FLAG_INSTALL,  name);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "无发送短信权限", Toast.LENGTH_SHORT).show();
                }
            }
            Log.d(TAG, "monitorWhenInstall: 启动了");
        }

        // 卸载禁忌软件的时候发送信息
        public void monitorWhenUninstall(String packageName) {
            String name = getAppName(packageName, mApps);
            if (!TextUtils.isEmpty(name)) {
                try {
                    sendMessage(mContext, FLAG_UNINSTALL,  name);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "无发送短信权限", Toast.LENGTH_SHORT).show();
                }
            }
            Log.d(TAG, "monitorWhenUninstall: 启动了");
        }

    }

    // 通过包名找应用名
    public static String getAppName(String packageName, List<App> apps) {
        String name = null;
        if (apps != null) {
            for (App app : apps) {
                if (packageName.equals(app.getPackageName())) {
                    name = app.getName();
                    break;
                }
            }
        }
        return name;
    }


    public MonitorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getApplicationContext().getSharedPreferences("server", MODE_PRIVATE);
        mContext = getApplicationContext();
        mAppLab = AppLab.get(mContext);

        IntentFilter intentFilter = new IntentFilter(); // 过滤器
        intentFilter.addAction(ACTION_PACKAGE_ADDED); // 监听应用安装
        intentFilter.addAction(ACTION_PACKAGE_REMOVED); // 监听应用卸载
        intentFilter.addDataScheme(SCHEME_PACKAGE);
        mAppReceiver = AppReceiver.newInstance(); // 注册监听安装和卸载应用广播
        registerReceiver(mAppReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initData();
        startMonitor();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMonitor();
    }

    private void initData() {
        mApps = mAppLab.getTabooApps();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // 开始启动监督，扫描禁忌软件列表，如果有数据则发送信息给监督者
    private void startMonitor() {
        String name = ""; // 禁忌软件的名字
        if (mApps.size() != 0) {
            for (App app : mApps) {
                name += app.getName() + "，";
            }
            name = name.substring(0,name.length()-1);

                sendMessage(mContext,FLAG_START, name);

        }
        Log.d(TAG, "startMonitor: 启动了");
    }

    // 停止监督
    private void stopMonitor() {
        if (mAppReceiver != null) {
            unregisterReceiver(mAppReceiver); // 关闭监听安装和卸载应用广播
            Log.d(TAG, "stopMonitor: 启动了");
            mAppReceiver = null;
        }
    }
}
