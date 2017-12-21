package com.example.dontplay.model;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

import com.example.dontplay.fragment.AllAppFragment;

/**
 * Created by Crazy贵子 on 2017/12/14.
 *  用于实现底部的导航栏的信息存放
 */

public class Tab {
    private int mId;
    private int mName;
    private int  mNavIcon;
    private Class mFragment;

    public Tab(int name, int navIcon, Class fragment) {
        mName = name;
        mNavIcon = navIcon;
        mFragment = fragment;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getName() {
        return mName;
    }

    public void setName(int name) {
        mName = name;
    }

    public int getNavIcon() {
        return mNavIcon;
    }

    public void setNavIcon(int navIcon) {
        mNavIcon = navIcon;
    }

    public Class getFragment() {
        return mFragment;
    }

    public void setFragment(Class fragment) {
        mFragment = fragment;
    }
}
