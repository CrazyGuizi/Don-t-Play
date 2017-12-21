package com.example.dontplay.fragment;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dontplay.R;
import com.example.dontplay.service.MonitorService;

/**
 * Created by Crazy贵子 on 2017/12/19.
 */

public class MonitorFragment extends DialogFragment implements View.OnClickListener {
    private static final String ARG_TABOO_APP_NAME = "tabooAppName";
    private static final int REQUEST_CONTACT = 1;
    
    private String mSupervisorName; // 监督者姓名
    private String mSupervisorNumber; // 监督者电话号码

    private TextView mCancelTextView; // 停止监督
    private TextView mCommitTextView; // 确认监督
    private Button mChooseSupervisorButton; // 选择监督者

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Intent mServer; // 服务

    private AlertDialog mAlertDialog;

    public static MonitorFragment newInstance() {
        return new MonitorFragment();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mSharedPreferences = getActivity().getSharedPreferences("server", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        // 读取监督者
        mSupervisorName = mSharedPreferences.getString("supervisorName", getString(R.string.dont_play_choose_supervisor));

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_monitor, null);
        mCancelTextView = view.findViewById(R.id.tv_cancel_monitor);
        mCancelTextView.setOnClickListener(this);
        mCommitTextView = view.findViewById(R.id.tv_commit_monitor);
        mCommitTextView.setOnClickListener(this);
        mChooseSupervisorButton = view.findViewById(R.id.bt_choose_supervisor);
        mChooseSupervisorButton.setText(mSupervisorName);
        mChooseSupervisorButton.setOnClickListener(this);

        mAlertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        return mAlertDialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_monitor: // 停止后台服务
                if (!mSharedPreferences.getBoolean("isStart", false)) {
                    Toast.makeText(getActivity(), "还未启动服务", Toast.LENGTH_SHORT).show();
                } else {
                    mEditor.putBoolean("isStart", false); // 关闭开机自启动服务广播
                    mEditor.commit();
                    mServer = new Intent(getActivity(), MonitorService.class); // 获取服务
                    getActivity().stopService(mServer);// 关闭服务
                    mAlertDialog.dismiss();
                }
                break;

            case R.id.tv_commit_monitor: // 开启后台服务
                if (getString(R.string.dont_play_choose_supervisor).equals(mSupervisorName)) {
                    Toast.makeText(getActivity(), "请先选择监督者", Toast.LENGTH_SHORT).show();
                } else {
                    mEditor.putBoolean("isStart", true); // 令开机自启动服务广播起效
                    mEditor.putString("supervisorName", mSupervisorName); // 存入监督者
                    mEditor.putString("supervisorNumber", mSupervisorNumber); // 存入监督者电话
                    mEditor.commit();
                    mServer = new Intent(getActivity(), MonitorService.class); // 获取服务
                    getActivity().startService(mServer); // 开启服务
                    mAlertDialog.dismiss();
                }
                break;

            case R.id.bt_choose_supervisor:
                // 选择联系人
                Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContact, REQUEST_CONTACT);
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONTACT:
                if (data != null) {
                    Uri contactUri = data.getData();
                    readContact(contactUri);
                }
                break;
            default:
        }
    }

    // 读取联系人
    private void readContact(Uri contactUri) {
        String[] queryFields = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
        if (cursor.getCount() == 0) {
            return;
        }
        cursor.moveToFirst();
        mSupervisorName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        mSupervisorNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        mSupervisorNumber = mSupervisorNumber.replaceAll("-","");
        mChooseSupervisorButton.setText(mSupervisorName);
    }
}
