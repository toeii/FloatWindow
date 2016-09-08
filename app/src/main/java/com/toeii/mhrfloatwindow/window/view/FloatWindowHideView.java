package com.toeii.mhrfloatwindow.window.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toeii.mhrfloatwindow.R;
import com.toeii.mhrfloatwindow.ScreenUtils;

/**
 * 隐藏窗
 * @version 1.0.0.0
 * @author toeii
 * @date 2016/9/6
 * @path https://github.com/toeii/FloatWindow
 *
 */
public class FloatWindowHideView extends RelativeLayout {
	
	private Context mContext;

	private TextView mHideSuspendTv;
	private RelativeLayout mHideSuspendRl;

	public FloatWindowHideView(Context context) {
		super(context);
		this.mContext = context;
		LayoutInflater.from(context).inflate(R.layout.view_game_suspend_hide, this);
		mHideSuspendRl = (RelativeLayout) findViewById(R.id.rl_suspend_hide);
		mHideSuspendTv = (TextView) findViewById(R.id.tv_suspend_hide);

		updateHideView(false);
	}

	public void updateHideView(boolean isCollsionWithRect){
		Drawable drawable = null;
		if(isCollsionWithRect){
			drawable = getResources().getDrawable(R.mipmap.icon_suspend_hide_on);
			mHideSuspendTv.setTextColor(Color.parseColor("#FFD800"));
		}else{
			drawable = getResources().getDrawable(R.mipmap.icon_suspend_hide_off);
			mHideSuspendTv.setTextColor(Color.WHITE);
		}
		if(null != drawable){
			drawable.setBounds(0, 0, ScreenUtils.dip2px(mContext, 30), ScreenUtils.dip2px(mContext, 30));
		}
		mHideSuspendTv.setCompoundDrawables(null, drawable, null, null);
		mHideSuspendTv.setCompoundDrawablePadding(ScreenUtils.dip2px(mContext, 8));
		mHideSuspendTv.setGravity(Gravity.CENTER);
	}

}
