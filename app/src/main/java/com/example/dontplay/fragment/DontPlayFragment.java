package com.example.dontplay.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dontplay.R;
import com.example.dontplay.model.AppLab;
import com.example.dontplay.model.Tab;
import com.example.dontplay.model.App;
import com.example.dontplay.util.AppUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.dontplay.util.AppUtil.getAllApps;


/**
 * Created by Crazy贵子 on 2017/12/13.
 */

public class DontPlayFragment extends Fragment implements AllAppFragment.onTabooAppListener,TabooAppFragment.onCancelTabooAppListener {

    private static final String TAG = "DontPlayFragment";
    private static final int GRANTED_CONTACTS = 1; // 获取读联系人权限
    private static final int GRANTED_SMS = 2; // 获取发送短信权限

    private View mView; // 保存view
    private List<Tab> mTabs = new ArrayList<>(); // 用于存放底部导航栏的选项
    private List<App> mTaboo = new ArrayList<>(); // 缓存设为禁忌的软件集合
    private List<App> mCancel = new ArrayList<>(); // 缓存取消禁忌的软件集合
    private AppLab mAppLab;

    private LayoutInflater mInflater;
    private FragmentManager mFragmentManager;
    private FragmentTabHost mTabHost;
    private PackageManager mPackageManager;


    public static DontPlayFragment newInstance() {
        return new DontPlayFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabs.add(new Tab(R.string.nav_all_app, R.drawable.selector_all_app_image, AllAppFragment.class));
        mTabs.add(new Tab(R.string.nav_taboo_app, R.drawable.selector_taboo_app_image, TabooAppFragment.class));
        mFragmentManager = getChildFragmentManager();
        mInflater = getLayoutInflater();

        getGranted();
        mPackageManager = getActivity().getPackageManager();
        mAppLab = AppLab.get(getActivity()); // 实例化单例

        // 初始化数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppLab.initDatabase();
            }
        }).start();
    }

    // 动态获取权限
    private void getGranted() {
        String[] permission = new String[] {Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS};
        int granted = 0;
        for (String p : permission) {
            if (ContextCompat.checkSelfPermission(getActivity(), p) != mPackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {p}, ++granted);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length >0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != mPackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "无权限部分功能受限", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 缓存Fragment，避免每次切换都重新生成
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView= inflater.inflate(R.layout.fragment_dont_play, container, false);
        }
        ViewGroup parentView = (ViewGroup) mView.getParent();
        if (parentView != null) {
            parentView.removeView(mView);
        }

        initTab(mView);
        return mView;
    }

    // 初始化FragmentTabHost
    private void initTab(View v) {
        mTabHost = v.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), mFragmentManager, R.id.container);
        for (Tab tab: mTabs) {
            mTabHost.addTab(mTabHost.newTabSpec(getString(tab.getName())).setIndicator(buildIndicator(tab)), tab.getFragment(), null);
        }
        mTabHost.getTabWidget().setDividerDrawable(null); // 去掉分割线
        mTabHost.setCurrentTab(0);
    }

    // 创建Tab视图
    private View buildIndicator(Tab tab) {
        View view = mInflater.inflate(R.layout.indicator, null);
        ImageView ivIcon = view.findViewById(R.id.iv_nav_icon);
        TextView tvTitle = view.findViewById(R.id.tv_nav_title);
        ivIcon.setImageDrawable(getResources().getDrawable(tab.getNavIcon()));
        tvTitle.setText(getString(tab.getName()));
        return  view;
    }


    // 取消软件的禁忌状态
    @Override
    public void onCancelTaboo(App app) {
        mCancel.add(app); // 加入取消禁忌的软件，缓存
        app.setIsTaboo(0); // 为0则是非禁忌状态
        mAppLab.updateApp(app);
    }

    // 获取禁忌软件集合
    @Override
    public List<App> getTabooApps() {
        return mAppLab.getTabooApps();
    }

    @Override
    public List<App> getTaboo() {
        List<App> apps = mTaboo;
        mTaboo.clear(); // 只要一读取完设置禁忌状态的缓冲数据，将就缓冲列表清空
        return apps;
    }

    // 设置软件为禁忌状态
    @Override
    public void onTaboo(App app) {
        mTaboo.add(app); // 加入设置为禁忌的软件，缓存
        app.setIsTaboo(1); // 为1则是禁忌状态
        mAppLab.updateApp(app);
    }

    // 获取非禁忌软件集合
    @Override
    public List<App> getApps() {
        return mAppLab.getApps();
    }

    @Override
    public List<App> getCancel() {
        List<App> apps = mCancel;
        mCancel.clear(); // 只要一读取完取消禁忌状态的缓冲数据，将就缓冲列表清空
        return apps;
    }
}
