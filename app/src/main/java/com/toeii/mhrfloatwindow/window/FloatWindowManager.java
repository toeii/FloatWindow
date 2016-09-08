package com.toeii.mhrfloatwindow.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.toeii.mhrfloatwindow.ScreenUtils;
import com.toeii.mhrfloatwindow.window.view.FloatWindowHideView;
import com.toeii.mhrfloatwindow.window.view.FloatWindowMainView;
import com.toeii.mhrfloatwindow.window.view.FloatWindowMenuView;
import com.toeii.mhrfloatwindow.window.view.FloatWindowRemindView;

/**
 * 悬浮窗管理类
 * @version 1.0.0.0
 * @author toeii
 * @date 2016/9/6
 * @path https://github.com/toeii/FloatWindow
 *
 */
public class FloatWindowManager {

    public final int VIEW_MAIN = 0;
    public final int VIEW_HIDE = 1;
    public final int VIEW_REMIND = 2;
    public final int VIEW_MENU = 3;

    private static FloatWindowManager floatWindowManager;
    private Context context;
    private WindowManager windowManager;

    private FloatWindowMainView floatWindowMainView;
    private FloatWindowMenuView floatWindowMenuView;
    private FloatWindowHideView floatWindowHideView;
    private FloatWindowRemindView floatWindowRemindView;

    private static WindowManager.LayoutParams mWindowParams;

    private boolean isFullHide = false;//是否完全隐藏

    private FloatWindowManager(Context context) {
        this.context = context;
    }

    public void setWindowParams(WindowManager.LayoutParams mWindowParams) {
        FloatWindowManager.mWindowParams = mWindowParams;
    }

    public static FloatWindowManager getInstance(Context context) {
        if(null == floatWindowManager){
            floatWindowManager = new FloatWindowManager(context);
        }
        return floatWindowManager;
    }

    //初始化窗口管理器
    public WindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return windowManager;
    }

    //初始化悬浮窗参数
    public WindowManager.LayoutParams getLayoutParams(int type) {
        WindowManager.LayoutParams wmLayoutParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            wmLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            wmLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        wmLayoutParams.format = PixelFormat.RGBA_8888;
        wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        wmLayoutParams.flags = wmLayoutParams.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        wmLayoutParams.alpha = 1.0f;

        if (type == VIEW_MAIN || type == VIEW_MENU) {
            wmLayoutParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
            wmLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmLayoutParams.height = ScreenUtils.dip2px(context, 48);
        } else if (type == VIEW_HIDE) {
            wmLayoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM; // 调整悬浮窗口至下方
            wmLayoutParams.height = ScreenUtils.dip2px(context, 100);
        } else if (type == VIEW_REMIND) {
            wmLayoutParams.gravity = Gravity.CENTER; // 调整悬浮窗口至中间
            wmLayoutParams.width = ScreenUtils.dip2px(context, 300);
            wmLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        // 以屏幕左上角为原点，设置x、y初始值
        wmLayoutParams.x = 0;
        wmLayoutParams.y = 0;
        return wmLayoutParams;
    }

    public void setIsFullHide(boolean isFullHide) {
        this.isFullHide = isFullHide;
    }

    //判断悬浮窗是否存在
    public boolean isWindowShowing() {
        return floatWindowMainView != null || floatWindowMenuView != null || floatWindowHideView != null || floatWindowRemindView != null;
    }

    //显示悬浮窗
    public void showFloatWindow(Context context) {
       setIsFullHide(false);
       createMainWindow(context.getApplicationContext());
       registerSensorEventListener();
    }

    //销毁悬浮窗
    public void destroyFloatWindow() {
        setIsFullHide(true);
        removeMainWindow();
        unregisterSensorEventListener();
    }

    //创建主窗
    public void createMainWindow(Context context) {
        if(isFullHide){
            return;
        }
        WindowManager windowManager = getWindowManager();
        floatWindowMainView = new FloatWindowMainView(context.getApplicationContext());
        if(null == mWindowParams){
            mWindowParams = getLayoutParams(VIEW_MAIN);
        }
        floatWindowMainView.setWindowParams(mWindowParams);
        try {
            windowManager.addView(floatWindowMainView, mWindowParams);
        } catch (Exception e) { }

        //提前创建好隐藏窗
        if(null == floatWindowHideView){
            createHideWindow(context.getApplicationContext());
        }
    }

    //创建菜单窗
    public void createMenuWindow(Context context) {
        WindowManager windowManager = getWindowManager();
        if(null == floatWindowMenuView){
            floatWindowMenuView = new FloatWindowMenuView(context);
        }
        floatWindowMenuView.setVisibility(View.GONE);
        try {
            windowManager.addView(floatWindowMenuView,getLayoutParams(VIEW_MENU));
        } catch (Exception e) { }

    }

    //创建隐藏窗
    public void createHideWindow(Context context) {
        WindowManager windowManager = getWindowManager();
        if(null == floatWindowHideView){
            floatWindowHideView = new FloatWindowHideView(context);
        }
        floatWindowHideView.setVisibility(View.GONE);
        try {
            windowManager.addView(floatWindowHideView, getLayoutParams(VIEW_HIDE));
        } catch (Exception e) { }

    }

    //创建提醒窗
    public void createRemindWindow(Context context) {
        WindowManager windowManager = getWindowManager();
        if(null == floatWindowRemindView){
            floatWindowRemindView = new FloatWindowRemindView(context);
        }
        try {
            windowManager.addView(floatWindowRemindView,getLayoutParams(VIEW_REMIND));
        } catch (Exception e) { }

    }

    //移除主窗
    public void removeMainWindow() {
        if(null == floatWindowMainView){return;}
        WindowManager windowManager = getWindowManager();
        try {
            windowManager.removeView(floatWindowMainView);
            floatWindowMainView = null;
            //将其他悬浮窗也一并移除
            removeRemindWindow();
            removeMenuWindow();
            removeHideWindow();
        } catch (Exception e) {
        }
    }

    //移除菜单窗
    public void removeMenuWindow() {
        if(null == floatWindowMenuView){return;}
        WindowManager windowManager = getWindowManager();
        try {
            windowManager.removeView(floatWindowMenuView);
            floatWindowMenuView = null;
        } catch (Exception e) {
        }
    }

    //移除隐藏窗
    public void removeHideWindow() {
        if(null == floatWindowHideView){return;}
        WindowManager windowManager = getWindowManager();
        try {
            windowManager.removeView(floatWindowHideView);
            floatWindowHideView = null;
        } catch (Exception e) {
        }
    }

    //移除提醒窗
    public void removeRemindWindow() {
        if(null == floatWindowRemindView){return;}
        WindowManager windowManager = getWindowManager();
        try {
            windowManager.removeView(floatWindowRemindView);
            floatWindowRemindView = null;
        } catch (Exception e) {
        }
    }

    public FloatWindowMainView getFloatWindowMainView() {
        return floatWindowMainView;
    }

    public FloatWindowMenuView getFloatWindowMenuView() {
        if(null == floatWindowMenuView){
            createMenuWindow(context);
        }
        return floatWindowMenuView;
    }

    public FloatWindowHideView getFloatWindowHideView() {
        if (null == floatWindowHideView) {
            createHideWindow(context);
        }
        return floatWindowHideView;
    }

    public void registerSensorEventListener() {
        if (!isWindowShowing()) {return;}
        floatWindowMainView.registerSensorEventListener();
    }

    public void unregisterSensorEventListener() {
        if (!isWindowShowing()) {return;}
        floatWindowMainView.unregisterSensorEventListener();
    }
}
