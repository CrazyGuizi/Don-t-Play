package com.example.dontplay.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dontplay.R;
import com.example.dontplay.activity.DontPlayActivity;
import com.example.dontplay.model.App;
import com.example.dontplay.util.AppUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Crazy贵子 on 2017/12/14.
 *  查看手机上已经安装并且可以启动的全部应用，在这个页面可以将应用设置为禁忌应用
 */

public class AllAppFragment extends Fragment {
    private static final String TAG = "AllAppFragment";

    private onTabooAppListener mCallBack; // 用于实现和TabooAppFragment间的通信
    private List<App> mApps = new ArrayList<>(); // 手机上的可启动的全部应用
    private RecyclerView mRecyclerView;
    private AppAdapter mAdapter;

    public static AllAppFragment newInstance() {
        return new AllAppFragment();
    }

    public interface onTabooAppListener{
        void onTaboo(App app); // 发送被设置为禁忌的软件
        List<App> getApps(); // 获取非禁忌的软件
        List<App> getCancel(); // 获取被取消禁忌状态的软件
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            // 因为实现onTabooAppListener接口的是DontPlayFragment，所以实例化接口需要用到DontPlayFragment的实例，这个实例在DontPlayActivity中保存
            mCallBack = DontPlayActivity.sDontPlayFragment;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // 用于更新界面
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    // 从DontPlayFragment中获取数据库的不是禁忌状态的软件
    private void updateUI() {
        mApps.clear();
        mApps.addAll(mCallBack.getApps());
        mAdapter.notifyDataSetChanged();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_app, container, false);
        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new AppAdapter(mApps);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }


    private class AppHolder extends RecyclerView.ViewHolder {
        private ImageView mIcon;
        private TextView mAppName;
        private Button mTaboo; // 设置这个应用为禁忌应用

        public AppHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.app_icon_image_view);
            mAppName = itemView.findViewById(R.id.app_name_text_view);
            mTaboo = itemView.findViewById(R.id.taboo_app_button);

        }

        public void onBind(final App app){
            mAppName.setText(app.getName());
            mIcon.setImageDrawable(app.getIcon());

            // 设置应用为禁忌软件
            mTaboo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onTaboo(app); // 添加设置为禁忌的软件
                    mApps.remove(app);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    private class AppAdapter extends RecyclerView.Adapter<AppHolder> {
        private List<App> mApps;

        public AppAdapter(List<App> apps) {
            mApps = apps;
        }

        @Override
        public AppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_app_list, parent, false);
            return new AppHolder(view);
        }

        @Override
        public void onBindViewHolder(AppHolder holder, int position) {
            App app = mApps.get(position);
            holder.onBind(app);
        }

        @Override
        public int getItemCount() {
            return mApps.size();
        }
    }
}
