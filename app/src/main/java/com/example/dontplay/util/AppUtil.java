package com.example.dontplay.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.example.dontplay.R;
import com.example.dontplay.model.App;
import com.example.dontplay.model.AppLab;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Crazy贵子 on 2017/12/19.
 */

public class AppUtil {

    public static final int FLAG_START = 1; // 开始启动服务标志时发送对应信息的标志
    public static final int FLAG_INSTALL = 2; // 安装应用时发送对应消息的标志
    public static final int FLAG_UNINSTALL = 3; // 卸载应用时发送对应消息的标志

    private static AppLab mAppLab;

    // 将ResolveInfo类型转化为App类型数据
    public static App getAppFromResolveInfo(ResolveInfo resolveInfo, PackageManager packageManager) {
        String name = resolveInfo.loadLabel(packageManager).toString();
        String packageName = resolveInfo.activityInfo.packageName;
        Drawable drawable = resolveInfo.loadIcon(packageManager);
        int isTaboo = 0;
        return new App(name, packageName, drawable, isTaboo);
    }

    // 将drawable类型图片转化为byte[]类型
    public static byte[] setIconToByte(Drawable icon) {
        if (icon == null) {
            return null;
        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    // 从数据库中读取Icon的byte[]类型数据，转化为Drawable类型
    public static Drawable getIconFromByte(byte[] blob) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length, null);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        Drawable drawable = bitmapDrawable;
        return drawable;
    }

    // 初始化数据，找出手机上的全部应用
    public static List<App> getAllApps(final PackageManager packageManager) {
        List<App> apps = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfos, new Comparator<ResolveInfo>() { // 排序
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.loadLabel(packageManager).toString(),o2.loadLabel(packageManager).toString());
            }
        });
        for (ResolveInfo info: resolveInfos) { // 添加将应用添加到App列表
            apps.add(AppUtil.getAppFromResolveInfo(info,packageManager));
        }
        return apps;
    }


    // 不同标志发送不同的信息，比如有重新安装了禁忌软件的时候说不同于卸载软件时候的话
    public static void sendMessage (Context context, int flag, String message)  {
        String TAG = "sendMessage";
        String number = context.getSharedPreferences("server", Context.MODE_PRIVATE).getString("supervisorNumber", null);
        String supervisor = context.getSharedPreferences("server", Context.MODE_PRIVATE).getString("supervisorName", null);
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        StringBuffer text = new StringBuffer();
        if (!TextUtils.isEmpty(message)) {
            switch (flag) {
                case FLAG_START:
                    text.append(context.getString(R.string.dont_play_send_message_on_start, supervisor,message));
                    break;
                case FLAG_INSTALL:
                    text.append(context.getString(R.string.dont_play_send_message_on_install,message));
                    break;
                case FLAG_UNINSTALL:
                    text.append(context.getString(R.string.dont_paly_send_message_on_uninstall, message));
                    break;
                default:
            }
        }
        List<String> divideContents = smsManager.divideMessage(text.toString());
        Log.d(TAG, "sendMessage:电话" +number + "\n内容:\n" +divideContents.toString());
        for (String s : divideContents) {
            smsManager.sendTextMessage(number, null, s, null, null);
        }
    }

}
