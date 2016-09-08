package com.toeii.mhrfloatwindow.window.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.toeii.mhrfloatwindow.R;
import com.toeii.mhrfloatwindow.ScreenUtils;
import com.toeii.mhrfloatwindow.window.Config;
import com.toeii.mhrfloatwindow.window.FloatWindowManager;
import com.toeii.mhrfloatwindow.window.SuspendMenuEntity;

import java.util.ArrayList;

/**
 * 主悬浮窗
 * 
 * @version 1.0.0.0
 * @author toeii
 * @date 2016/9/6
 * @path https://github.com/toeii/FloatWindow
 *
 */
public class FloatWindowMainView extends LinearLayout implements SensorEventListener {

	private int TIME = 5000;// 5秒后开始贴边
	private int LONG_TIME = 10000;// 10秒后开始贴边
	private final int MOVE_DISTANCE = 20;// 移动距离

	// 子窗口显示位置
	private int position = 2;
	private final int LEFT = 1;
	private final int RIGHT = 2;

	private Context mContext;

	// 翻转角度
	private final int FLIP_ANGLE_DOWN = -9;
	private final int FLIP_ANGLE_UP = 9;
	// 感应器管理器
	private SensorManager mSensorManager = null;
	private Sensor mAccelerometer = null;
	// 不再提醒
	private SharedPreferences mPreferences;

	private FloatWindowManager mFloatWindowManager;
	private WindowManager mWindowManager;
	private FloatWindowHideView mFloatWindowHideView;// 隐藏窗
	private FloatWindowMenuView mFloatWindowMenuView;// 菜单窗
	private WindowManager.LayoutParams mWindowParams;
	private WindowManager.LayoutParams mMenuViewWindowParams;

	private ImageView mSuspendIv;
	private static Handler handler = new Handler();// 透明化定时
//	private TranslateAnimation mClingAnimation;// 悬浮窗贴边动画

	// 屏幕宽高
	private int mScreenWidth;
	private int mScreenHeigh;

	// 窗体宽高
	public int mWindowWidth;
	public int mWindowHeight;

	// 当前移动X轴,Y轴
	private float mNowX;
	private float mNowY;

	// 之前位置X轴,Y轴
	private float mTouchStartX;
	private float mTouchStartY;

	// 隐藏窗X轴,Y轴
	private int mViewHideX;
	private int mViewHideY;

	private boolean isMove = false;//是否正在移动
	private boolean isCling = false;// 是否贴边
	private boolean isMoveComeHide = false;// 是否移动在隐藏窗口上
	private boolean isTurnOver = false;// 手机翻转是否停止
	private boolean isAddedMenu = false;// 是否有显示菜单栏窗口

	public void setWindowParams(WindowManager.LayoutParams mWindowParams) {
		this.mWindowParams = mWindowParams;
	}

	public FloatWindowMainView(Context context) {
		super(context);
		this.mContext = context;
		mSensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
		mPreferences = context.getSharedPreferences(Config.SuspendKey.FLOAT_WINDOW, Context.MODE_PRIVATE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mFloatWindowManager = FloatWindowManager.getInstance(mContext);
		mWindowManager = mFloatWindowManager.getWindowManager();
		mFloatWindowHideView = mFloatWindowManager.getFloatWindowHideView();
		mFloatWindowMenuView = mFloatWindowManager.getFloatWindowMenuView();
		mMenuViewWindowParams = mFloatWindowManager.getLayoutParams(mFloatWindowManager.VIEW_MENU);
		LayoutInflater.from(mContext).inflate(R.layout.view_game_suspend_menu, this);
		View view = findViewById(R.id.ll_suspend);
		mWindowWidth = view.getLayoutParams().width;
		mWindowHeight = view.getLayoutParams().height;
		mSuspendIv = (ImageView) findViewById(R.id.iv_suspend);
		mSuspendIv.setVisibility(View.VISIBLE);

		setAlpha();

		mScreenWidth = ScreenUtils.getScreenWidth(context);
		mScreenHeigh = ScreenUtils.getScreenHeight(context);

		mViewHideX = 0;
		mViewHideY = mScreenHeigh - ScreenUtils.getStatusHeight(context) - mFloatWindowHideView.getLayoutParams().height;

		handler.postDelayed(clingBoundaryRunnable, LONG_TIME);

		mFloatWindowMenuView.post(new Runnable() {
			@Override
			public void run() {
				initMenuData();
			}
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mNowX = event.getRawX();
		mNowY = event.getRawY() - ScreenUtils.getStatusHeight(mContext);
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				handler.removeCallbacks(clingBoundaryRunnable);
				handler.removeCallbacks(transparentRunnable);
				isMove = false;
				mTouchStartX = event.getX();
				mTouchStartY = event.getY();
				setAlpha();
				break;
			case MotionEvent.ACTION_MOVE:
				if (mTouchStartY - event.getY() > MOVE_DISTANCE
						|| mTouchStartY - event.getY() < -MOVE_DISTANCE
						|| mTouchStartX - event.getX() > MOVE_DISTANCE
						|| mTouchStartX - event.getX() < -MOVE_DISTANCE) {
					isMove = true;
					if (isAddedMenu) {
						isAddedMenu = false;
						// 隐藏菜单
						mFloatWindowMenuView.setVisibility(View.GONE);
					}
					updateViewPosition();

					if(mFloatWindowManager.getFloatWindowHideView().getVisibility() == View.GONE){
						mFloatWindowManager.getFloatWindowHideView().setVisibility(View.VISIBLE);
					}

					// 检测是否移动在隐藏窗口上
					if (isCollsionWithRect(mViewHideX, mViewHideY, ScreenUtils.getScreenWidth(mContext), mFloatWindowHideView.getHeight(), mWindowParams.x, mWindowParams.y, mWindowParams.width,
							mWindowParams.height)) {
						isMoveComeHide = true;
						mFloatWindowManager.getFloatWindowHideView().updateHideView(true);
					} else {
						isMoveComeHide = false;
						mFloatWindowManager.getFloatWindowHideView().updateHideView(false);
					}

				}

				break;
			case MotionEvent.ACTION_UP:
				mFloatWindowHideView.setVisibility(View.GONE);
				if(isMoveComeHide){
					mFloatWindowManager.removeMainWindow();
					if (!mPreferences.getBoolean(Config.SuspendKey.SP_CHOOSE, false)) {
						mFloatWindowManager.createRemindWindow(mContext);
					}
				}else{
					judgeScreenEdge();
					if (mTouchStartY - event.getY() > MOVE_DISTANCE
							|| mTouchStartY - event.getY() < -MOVE_DISTANCE
							|| mTouchStartX - event.getX() > MOVE_DISTANCE
							|| mTouchStartX - event.getX() < -MOVE_DISTANCE) {
						mTouchStartX = mTouchStartY = 0;
					}else{
						if (isAddedMenu) {
							isAddedMenu = false;
							mFloatWindowMenuView.setVisibility(View.GONE);
						} else if(!isMove  && !isCling){
							mMenuViewWindowParams.y = mWindowParams.y;
							if (position == LEFT) {
								mMenuViewWindowParams.x = mWindowParams.x + mSuspendIv.getWidth();
								mFloatWindowMenuView.setBackgroundResource(R.mipmap.icon_suspend_menu_right);
							} else {
								mMenuViewWindowParams.x = mWindowParams.x - mFloatWindowMenuView.getWidth() - ScreenUtils.px2dip(mContext, 12);
								mFloatWindowMenuView.setBackgroundResource(R.mipmap.icon_suspend_menu_left);
							}
							mFloatWindowMenuView.setVisibility(View.VISIBLE);
							updateViewLayout(mFloatWindowMenuView,mMenuViewWindowParams);
							isAddedMenu = true;
						}
					}
					suspendMoveEnd();
				}
				isCling = false;
				break;
		}
		return true;
	}

	// 更新浮动窗口位置参数
	private void updateViewPosition() {
		mWindowParams.x = (int) (mNowX - mTouchStartX);
		mWindowParams.y = (int) (mNowY - mTouchStartY);
		// 限制悬浮滑动范围
		viewMoveRestrict();
		updateViewLayout(mFloatWindowManager.getFloatWindowMainView(),mWindowParams);
	}

	private void updateViewLayout(View view,WindowManager.LayoutParams mWindowParams) {
		if(null == view || null == mWindowParams){return;}
		mFloatWindowManager.setWindowParams(mWindowParams);
		try {
			mWindowManager.updateViewLayout(view, mWindowParams);
		}catch (Exception e){
			mWindowManager.addView(view, mWindowParams);
			mWindowManager.updateViewLayout(view, mWindowParams);
		}
	}

	private void viewMoveRestrict() {
		if (mWindowParams.x > mScreenWidth - ScreenUtils.dip2px(mContext, 48)) {
			mWindowParams.x = mScreenWidth - ScreenUtils.dip2px(mContext, 48);
		} else if (mWindowParams.x <= 0) {
			mWindowParams.x = 0;
		}
		if (mWindowParams.y > mScreenHeigh - ScreenUtils.dip2px(mContext, 48)) {
			mWindowParams.y = mScreenHeigh - ScreenUtils.dip2px(mContext, 48);
		} else if (mWindowParams.y <= 0) {
			mWindowParams.y = 0;
		}
	}

	// 移动结束后的定位
	public void suspendMoveEnd() {
		if(null == mFloatWindowManager.getFloatWindowMainView()){return;}
//		if (AppUtil.isScreenChange(mContext)) {
//			if (mScreenHeigh / 2 > mWindowParams.x) {
//				mWindowParams.x = 0;
//			} else {
//				mWindowParams.x = mScreenHeigh - mSuspendIv.getWidth();
//			}
//		} else {
			if (mScreenWidth / 2 > mWindowParams.x) {
				mWindowParams.x = 0;
			} else {
				mWindowParams.x = mScreenWidth - ScreenUtils.dip2px(mContext,48);
			}
//		}

		updateViewLayout(mFloatWindowManager.getFloatWindowMainView(),mWindowParams);
		timerAgain();
	}

	// 透明化定时
	private void timerAgain() {
		if(null == mFloatWindowManager.getFloatWindowMainView()){return;}
		if (null != handler) {
			handler.removeCallbacks(clingBoundaryRunnable);
			if (isAddedMenu) {
				handler.postDelayed(clingBoundaryRunnable, LONG_TIME);
			} else {
				handler.postDelayed(clingBoundaryRunnable, TIME);
			}
		}
	}

	/**
	 * 矩形碰撞的函数
	 *
	 * @param x1
	 *            第一个矩形的X坐标
	 * @param y1
	 *            第一个矩形的Y坐标
	 * @param w1
	 *            第一个矩形的宽
	 * @param h1
	 *            第一个矩形的高
	 * @param x2
	 *            第二个矩形的X坐标
	 * @param y2
	 *            第二个矩形的Y坐标
	 * @param w2
	 *            第二个矩形的宽
	 * @param h2
	 *            第二个矩形的高
	 */
	public boolean isCollsionWithRect(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		// 当矩形1位于矩形2的左侧
		if (x1 >= x2 && x1 >= x2 + w2) {
			return false;
			// 当矩形1位于矩形2的右侧
		} else if (x1 <= x2 && x1 + w1 <= x2) {
			return false;
			// 当矩形1位于矩形2的上方
		} else if (y1 >= y2 && y1 >= y2 + h2) {
			return false;
		} else if (y1 <= y2 && y1 + h1 <= y2) {
			return false;
		}
		// 所有不会发生碰撞都不满足时，肯定就是碰撞了
		return true;
	}

	// 还原透明度
	private void setAlpha() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mSuspendIv.setImageAlpha(204);
		} else {
			mSuspendIv.setAlpha(204);
		}
	}

	// 悬浮窗贴边线程
	protected Runnable clingBoundaryRunnable = new Runnable() {
		@Override
		public void run() {
			suspendClingBoundary();

			// 两秒后透明
			handler.postDelayed(transparentRunnable, 2000);

			// 菜单栏
			if (isAddedMenu) {
				isAddedMenu = false;
				mFloatWindowMenuView.setVisibility(View.GONE);
			}
		}
	};

	// 贴边后的半透明处理
	protected Runnable transparentRunnable = new Runnable() {
		@Override
		public void run() {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				mSuspendIv.setImageAlpha(128);
			} else {
				mSuspendIv.setAlpha(128);
			}
		}
	};

	// 判断屏幕两边
	private void judgeScreenEdge () {
		if (mScreenWidth / 2 > mWindowParams.x) {
			position = LEFT;
		} else {
			position = RIGHT;
		}
	}

	// 贴边
	private void suspendClingBoundary() {
		if (mWindowParams.x == 0) {
			mWindowParams.x = ScreenUtils.dip2px(mContext, -24);
		} else {
//			if (AppUtil.isScreenChange(mContext)) {
//				mWindowParams.x = mScreenHeigh - AppUtil.dip2px(mContext, 24);
//			} else {
				mWindowParams.x = mScreenWidth - ScreenUtils.dip2px(mContext, 24);
//			}
		}
		updateViewLayout(mFloatWindowManager.getFloatWindowMainView(),mWindowParams);
		isCling = true;
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		float value = event.values[2];
		// -10 ~ 10 水平翻转值
		if (value < FLIP_ANGLE_DOWN) {
			isTurnOver = true;
		}

		if (isTurnOver && value > FLIP_ANGLE_UP) {
			if (!mFloatWindowManager.isWindowShowing()) {
				mFloatWindowManager.createMainWindow(mContext);
				// 恢复默认坐标
//				updateViewLayout(mFloatWindowManager.getFloatWindowMainView(),mWindowParams);
				isTurnOver = false;
				suspendMoveEnd();
			} else {
				mFloatWindowManager.removeMainWindow();
				isTurnOver = false;
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void registerSensorEventListener() {
		if (null != mSensorManager) {
			mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
		}
	}

	public void unregisterSensorEventListener() {
		if (null != mSensorManager) {
			mSensorManager.unregisterListener(this);
		}
	}

	private void initMenuData() {
		if(mFloatWindowMenuView.getSuspendMenuLayout().getChildCount() <= 1){
			initMenuDefaultData();
			mFloatWindowMenuView.invalidate();
		}
		updateViewLayout(mFloatWindowMenuView,mMenuViewWindowParams);
	}

	private void initMenuDefaultData() {
		ArrayList<SuspendMenuEntity> suspendMenuBeans = new ArrayList<>();
		SuspendMenuEntity suspendMenuEntity1 = new SuspendMenuEntity();
		suspendMenuEntity1.setMenuItemName("第一");

		SuspendMenuEntity suspendMenuEntity2 = new SuspendMenuEntity();
		suspendMenuEntity2.setMenuItemName("第二");

		SuspendMenuEntity suspendMenuEntity3 = new SuspendMenuEntity();
		suspendMenuEntity3.setMenuItemName("第三");

		SuspendMenuEntity suspendMenuEntity4 = new SuspendMenuEntity();
		suspendMenuEntity4.setMenuItemName("第四");

		suspendMenuBeans.add(suspendMenuEntity1);
		suspendMenuBeans.add(suspendMenuEntity2);
		suspendMenuBeans.add(suspendMenuEntity3);
		suspendMenuBeans.add(suspendMenuEntity4);
		mFloatWindowMenuView.addMenuChild(suspendMenuBeans);
	}

}
