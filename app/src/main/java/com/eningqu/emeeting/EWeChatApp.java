package com.eningqu.emeeting;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import com.eningqu.emeeting.utils.SystemUtil;
import com.iflytek.cloud.SpeechUtility;
import com.youdao.sdk.app.YouDaoApplication;

public class EWeChatApp extends Application{
	public final static String TAG = "SDKDemoApp";

	private static EWeChatApp instance;
	protected static Handler uiHandler = new Handler(Looper.getMainLooper());

	public static EWeChatApp getApp() {
		return instance;
	}

	public static void runOnUiGround(Runnable r, long delay) {
		if (delay > 0) {
			uiHandler.postDelayed(r, delay);
		} else {
			uiHandler.post(r);
		}
	}

	public static String getIMEI(){
		return SystemUtil.getIMEI(getApp());
	}

	public static void removeUiGroundCallback(Runnable r) {
		uiHandler.removeCallbacks(r);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;



		SpeechUtility.createUtility(EWeChatApp.this, "appid=" + getString(R.string.app_id));
		/*有道离线翻译SDK初始化*/
		YouDaoApplication.init(this,"67aecc38701a9927");
	}







}
