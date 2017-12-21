package com.example.dontplay.broadcast;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import com.example.dontplay.model.App;
import com.example.dontplay.service.MonitorService;
import com.example.dontplay.util.AppUtil;

public class BootReceiver extends BroadcastReceiver {

    private static final String ACTION_BOOT_COMPLETED = "action.intent.action.BOOT_COMPLETED"; // 开机自启动

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("server", Context.MODE_PRIVATE); // 服务是否开启了
        boolean isStart = sharedPreferences.getBoolean("isStart", false);
        if (isStart) {
            Intent server = new Intent(context, MonitorService.class);
            context.startService(server);
        }
    }
}
