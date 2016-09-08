package com.toeii.mhrfloatwindow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.toeii.mhrfloatwindow.window.FloatWindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatWindowManager.getInstance(this).showFloatWindow(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        FloatWindowManager.getInstance(this).removeMainWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatWindowManager.getInstance(this).destroyFloatWindow();
    }
}
