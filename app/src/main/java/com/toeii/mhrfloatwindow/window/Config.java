package com.toeii.mhrfloatwindow.window;

/**
 *
 * @version 1.0.0.0
 * @author toeii
 * @date 2016/9/6
 * @path https://github.com/toeii/FloatWindow
 *
 */
public class Config {

    //悬浮窗Key
    public static abstract class SuspendKey{
        public final static String WEB_PATH = "web_path";
        public final static String KEY_USER_ID= "key_user_id";
        public final static String KEY_SERVER_STATE="key_server_state";
        public final static String SP_CHOOSE = "choose";
        public final static String FLOAT_WINDOW = "float_window";
    }

    //悬浮窗服务状态
    public static abstract class SuspendServerState{
        public final static int DEFAULT = 0;
        public final static int SHOW = 1;
        public final static int HIDE = 2;
        public final static int DESTROY = 3;
    }

    //悬浮窗菜单点击隐藏状态
    public static abstract class SuspendMenuOnClikHideState{
        public final static int DEFAULT = 0;
        public final static int SHOW = 1;
        public final static int HIDE = 2;
        public final static int DESTROY = 3;
    }

    public static abstract class SuspendSkipType{
        public final static int ALL_SKIP = -1;
        public final static int DEFAULT_SKIP = 0;
        public final static int APP_SKIP = 2;
        public final static int WEB_SKIP = 1;
        public final static int ACCOUNT_SKIP = 3;
        public final static int GIFT_SKIP = 4;
        public final static int CIRCLE_SKIP = 5;
        public final static int MHR_CHAT_SKIP = 6;
    }

}
