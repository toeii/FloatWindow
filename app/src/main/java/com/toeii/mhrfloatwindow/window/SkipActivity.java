package com.toeii.mhrfloatwindow.window;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.toeii.mhrfloatwindow.R;

public class SkipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_layout);
        relativeLayout.setBackgroundColor(Color.BLUE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatWindowManager.getInstance(this).showFloatWindow(this);
    }
}
