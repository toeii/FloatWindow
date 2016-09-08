package com.toeii.mhrfloatwindow.window.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.toeii.mhrfloatwindow.R;
import com.toeii.mhrfloatwindow.window.Config;
import com.toeii.mhrfloatwindow.window.FloatWindowManager;

/**
 * 翻转提醒窗
 * @version 1.0.0.0
 * @author toeii
 * @date 2016/9/6
 * @path https://github.com/toeii/FloatWindow
 *
 */
public class FloatWindowRemindView extends LinearLayout {

	private SharedPreferences preferences;

	private FloatWindowManager floatWindowManager;

	public FloatWindowRemindView(final Context context) {
		super(context);
		preferences = context.getSharedPreferences(Config.SuspendKey.FLOAT_WINDOW, Context.MODE_PRIVATE);
		LayoutInflater.from(context).inflate(R.layout.view_game_suspend_remind, this);
		floatWindowManager = FloatWindowManager.getInstance(context);
		final CheckBox suspendRemindCb = (CheckBox) findViewById(R.id.cb_suspend_remindwindow);
		Button suspendRemindCancel = (Button) findViewById(R.id.btn_suspend_remind_cancel);
		Button suspendRemindHide = (Button) findViewById(R.id.btn_suspend_remind_hide);

		suspendRemindCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				floatWindowManager.removeRemindWindow();
				floatWindowManager.createMainWindow(context);
				if(null != floatWindowManager.getFloatWindowMainView()){
					floatWindowManager.getFloatWindowMainView().suspendMoveEnd();
				}
			}
		});

		suspendRemindHide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = preferences.edit();
				if (suspendRemindCb.isChecked()) {
					editor.putBoolean(Config.SuspendKey.SP_CHOOSE, true);
				} else {
					editor.putBoolean(Config.SuspendKey.SP_CHOOSE, false);
				}
				editor.apply();
				floatWindowManager.removeRemindWindow();
			}
		});
	}

}
