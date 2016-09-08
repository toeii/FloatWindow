package com.toeii.mhrfloatwindow.window.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toeii.mhrfloatwindow.R;
import com.toeii.mhrfloatwindow.ScreenUtils;
import com.toeii.mhrfloatwindow.window.FloatWindowManager;
import com.toeii.mhrfloatwindow.window.SkipActivity;
import com.toeii.mhrfloatwindow.window.SuspendMenuEntity;

import java.util.ArrayList;

/**
 * 菜单窗
 *
 * @author toeii
 * @version 1.0.0.0
 * @date 2016/9/6
 * @path https://github.com/toeii/FloatWindow
 *
 */
public class FloatWindowMenuView extends LinearLayout {

    private Context mContext;

    private LinearLayout mSuspendMenuLayout;

    private ArrayList<SuspendMenuEntity> mSuspendMenuBeans = new ArrayList<>();

    public LinearLayout getSuspendMenuLayout() {
        if (null == mSuspendMenuLayout) {
            mSuspendMenuLayout = (LinearLayout) findViewById(R.id.ll_suspend);
        }
        return mSuspendMenuLayout;
    }

    public FloatWindowMenuView(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_game_suspend_menu, this);
        mSuspendMenuLayout = (LinearLayout) findViewById(R.id.ll_suspend);
    }

    public void addMenuChild(ArrayList<SuspendMenuEntity> suspendMenuBeans) {
        int width = ScreenUtils.getScreenWidth(mContext);
        int height = ScreenUtils.getScreenHeight(mContext);
        int menuWidth = width>height?height:width - (FloatWindowManager.getInstance(mContext).getFloatWindowMainView().getWidth()*2);
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.width = menuWidth;
        lp.gravity = Gravity.CENTER;
        getSuspendMenuLayout().setLayoutParams(lp);

        mSuspendMenuBeans.clear();
        mSuspendMenuBeans.addAll(suspendMenuBeans);
        if(null != suspendMenuBeans && suspendMenuBeans.size()>0){
            getSuspendMenuLayout().removeAllViews();
            for (int i = 0; i < suspendMenuBeans.size(); i++) {
                SuspendMenuEntity bean = suspendMenuBeans.get(i);
                if (null != bean) {
                    LinearLayout menuLayout = new LinearLayout(mContext);
                    initMenuView(menuLayout, i, bean);
                    getSuspendMenuLayout().addView(menuLayout);
                }
            }
        }
    }

    private void initMenuView(LinearLayout menuLayout, final int index, SuspendMenuEntity bean) {
        if (null != bean) {
            menuLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams menuLayoutLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            menuLayoutLp.gravity = Gravity.TOP | Gravity.LEFT;
            if (index == 0) {
                menuLayoutLp.setMargins(ScreenUtils.px2dip(mContext, 16), ScreenUtils.px2dip(mContext, 4), 0, 0);
            } else {
                menuLayoutLp.setMargins(0, ScreenUtils.px2dip(mContext, 4), 0, 0);
            }
            menuLayout.setLayoutParams(menuLayoutLp);

            ImageView menuIv = new ImageView(mContext);
            LinearLayout.LayoutParams menuIvLp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(mContext, 30), ScreenUtils.dip2px(mContext, 30));
            menuIvLp.gravity = Gravity.CENTER;
            menuIv.setLayoutParams(menuIvLp);

            TextView menuTv = new TextView(mContext);
            menuTv.setTextSize(10);
            menuTv.setTextColor(Color.WHITE);
            menuTv.setSingleLine(true);
            menuTv.setGravity(Gravity.CENTER);

            if (null == bean.getMenuItemIcon() || bean.getMenuItemIcon().equals("")) {
//                menuIv.setBackgroundDrawable(bean.getMenuItemLocalIcon());
                menuIv.setImageResource(R.mipmap.ic_launcher);
            } else {
//                Picasso.with(mContext).load(Uri.parse(bean.getMenuItemIcon())).into(menuIv);
            }
            menuTv.setText(bean.getMenuItemName());

            menuLayout.addView(menuIv);
            menuLayout.addView(menuTv);
            menuLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"skip app"+index,Toast.LENGTH_SHORT).show();
                    FloatWindowManager.getInstance(mContext).destroyFloatWindow();
                    mContext.startActivity(new Intent(mContext, SkipActivity.class));
                }
            });

        }
    }


}
