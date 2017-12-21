package com.example.dontplay.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Crazy贵子 on 2017/12/13.
 *  用于记录手机上设置为禁忌的应用
 */

public class App {
    private String name; // 应用名
    private String packageName; // 应用包名
    private Drawable icon; // 应用图标
    private int isTaboo; // 应用的状态，为0代表非禁忌，为1代表禁忌，为2代表应用已经从手机上删除，但还是处于禁忌状态

    public App() {
    }

    public App(String name, String packageName, Drawable icon, int isTaboo) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.isTaboo = isTaboo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getIsTaboo() {
        return isTaboo;
    }

    public void setIsTaboo(int isTaboo) {
        this.isTaboo = isTaboo;
    }
}
