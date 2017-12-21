package com.example.dontplay.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dontplay.R;
import com.example.dontplay.activity.DontPlayActivity;
import com.example.dontplay.model.App;
import com.example.dontplay.model.AppLab;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Crazy贵子 on 2017/12/14.
 */

public class TabooAppFragment extends Fragment {
    private static final String TAG = "TabooAppFragment";
    private static final String DIALOG_MONITOR = "DialogMonitor";

    private onCancelTabooAppListener mCallBack;
    private List<App> mTabooApps = new ArrayList<>(); // 存放被设置被禁忌状态的软件

    private Button mStartMonitor; // 启动监督
    private RecyclerView mRecyclerView;
    private TabooAppAdapter mAdapter;

    public static TabooAppFragment newInstance() {
        return new TabooAppFragment();
    }

    public interface onCancelTabooAppListener{
        void onCancelTaboo(App app); // 发送取消了禁忌的软件
        List<App> getTabooApps(); // 获取禁忌的软件
        List<App> getTaboo(); // 获取被设置为禁忌的软件
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBack = DontPlayActivity.sDontPlayFragment;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    // 更新页面
    private void updateUI() {
        mTabooApps.clear();
        mTabooApps.addAll(mCallBack.getTabooApps());
        mAdapter.notifyDataSetChanged();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taboo_app, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new TabooAppAdapter(mTabooApps);
        mRecyclerView.setAdapter(mAdapter);

        // 启动监督的点击事件
        mStartMonitor = view.findViewById(R.id.bt_start_monitor);
        mStartMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    MonitorFragment monitorFragment = MonitorFragment.newInstance();
                    monitorFragment.show(manager, DIALOG_MONITOR);
            }
        });
        return view;
    }

    private class TabooAppHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mIcon;
        private TextView mAppName;
        private Button mCancelTaboo; // 取消禁忌状态
        private App mApp;

        public TabooAppHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.app_icon_image_view);
            mAppName = itemView.findViewById(R.id.app_name_text_view);
            mCancelTaboo = itemView.findViewById(R.id.cancel_taboo_app_button);
            itemView.setOnClickListener(this);
        }

        public void onBind(final App app) {
             mApp = app;
            mAppName.setText(app.getName());
            mIcon.setImageDrawable(app.getIcon());

            // 为引用取消禁忌状态
            mCancelTaboo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onCancelTaboo(app); // 发送取消禁忌状态的软件
                    mTabooApps.remove(app);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        // 点击卸载App
        @Override
        public void onClick(View v) {
            uninstall();
        }

        private void uninstall() {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" +mApp.getPackageName()));
            startActivity(intent);
        }
    }

    private class TabooAppAdapter extends RecyclerView.Adapter<TabooAppHolder> {
        private List<App> mApps;

        public TabooAppAdapter(List<App> apps) {
            mApps = apps;
        }

        @Override
        public TabooAppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_taboo_app_list, parent, false);
            return new TabooAppHolder(view);
        }

        @Override
        public void onBindViewHolder(TabooAppHolder holder, int position) {
            App app = mApps.get(position);
            holder.onBind(app);
        }

        @Override
        public int getItemCount() {
            return mApps.size();
        }

        public void setApps(List<App> apps) {
            mApps = apps;
        }
    }

}
