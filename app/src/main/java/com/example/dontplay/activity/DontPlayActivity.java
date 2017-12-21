package com.example.dontplay.activity;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.example.dontplay.R;
import com.example.dontplay.fragment.DontPlayFragment;

public class DontPlayActivity extends SingleFragmentActivity {

    public static DontPlayFragment sDontPlayFragment; // 保存DontPlayFragment实例

    @Override
    public Fragment createFragment() {
        return sDontPlayFragment = DontPlayFragment.newInstance();
    }
}
