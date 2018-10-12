package com.eningqu.emeeting.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.aispeech.AIError;
import com.aispeech.AIResult;
import com.aispeech.DUILiteSDK;
import com.aispeech.common.AIConstant;
import com.aispeech.common.JSONResultParser;
import com.aispeech.export.engines.AICloudASREngine;
import com.aispeech.export.listeners.AIASRListener;
import com.eningqu.emeeting.R;
import com.eningqu.emeeting.adapter.MyAdapter;
import com.eningqu.emeeting.bean.TranslateInfo;
import com.eningqu.emeeting.net.TransApi;
import com.eningqu.emeeting.tools.IflyLocalASR;
import com.eningqu.emeeting.tools.LangUtils;
import com.eningqu.emeeting.tools.OfflineVoiceUtils;
import com.eningqu.emeeting.tools.OrderForTTS;
import com.eningqu.emeeting.tools.TTSUtils;
import com.eningqu.emeeting.utils.JsonParser;
import com.eningqu.emeeting.utils.NetWorkUtils;
import com.eningqu.emeeting.utils.TranslateDao;
import com.eningqu.emeeting.utils.TranslateData;
import com.iflytek.cloud.SpeechError;
import com.pgyersdk.update.DownloadFileListener;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.pgyersdk.update.javabean.AppBean;
import com.youdao.localtransengine.LanguageConvert;
import com.youdao.sdk.app.Language;
import com.youdao.sdk.app.LanguageUtils;
import com.youdao.sdk.ydtranslate.EnLineNMTTranslator;
import com.youdao.sdk.ydtranslate.EnWordTranslator;
import com.youdao.sdk.ydtranslate.Translate;
import com.youdao.sdk.ydtranslate.TranslateErrorCode;
import com.youdao.sdk.ydtranslate.TranslateListener;
import com.youdao.sdk.ydtranslate.TranslateParameters;
import com.youdao.sdk.ydtranslate.TranslatorOffline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener , com.iflytek.cloud.RecognizerListener{
    private final String Tag = this.getClass().getName();
    private ListView mMainMeetingLy = null;
    private PopupWindow window = null;
    private ProgressDialog dialog = null;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private LinearLayout mSpeechRecognizingContent = null;
    private ImageView mMainSpeak = null;

    private static final String APP_ID = "20170713000064403";
    private static final String SECURITY_KEY = "Byg3e6QIsqS3E4ypHfXR";
    private TransApi mtransApi;
    private Toast mToast;
    private AICloudASREngine mEngine;//语音识别引擎
    private IflyLocalASR mLocalAsr;//讯飞离线语音识别引擎
    private TranslatorOffline translatorOffline;//离线翻译引擎


    private String mOriginalString = null;

    private volatile int mAIConstantStatus = 0;

    private TranslateInfo mTranslateInfo=null;
    private List<TranslateInfo> mTranslateInfoList = null;
    private MyAdapter myAdapter = null;
    private TranslateDao mTranslateDao = null;
    private boolean SAVEDATE = true;

    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermmssion();

        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);


        setContentView(R.layout.activity_home);
        mSharedPreferences = getSharedPreferences("com.eningqu.setting", MODE_PRIVATE);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        mMainMeetingLy = (ListView)findViewById(R.id.main_listview);
        findViewById(R.id.main_settings).setOnClickListener(this);
        mSpeechRecognizingContent = (LinearLayout)findViewById(R.id.speech_recognizing_content);
        mSpeechRecognizingContent.setVisibility(View.GONE);
        mMainSpeak = (ImageView)findViewById(R.id.main_speaking);
        mMainSpeak.setOnClickListener(this);
        if (NetWorkUtils.isNetworkConnected(getApplicationContext())){
            mMainSpeak.setVisibility(View.GONE);
        }else {
            mMainSpeak.setVisibility(View.VISIBLE);
        }
        auth();

        mTranslateDao = new TranslateDao(this);

        /*初始化百度翻译*/
        mtransApi = new TransApi(APP_ID, SECURITY_KEY);
        translatorOffline = new TranslatorOffline();

        //初始化词库方式一，词库在assets/dict目录下,不支持直接放在assets目录下，会遍历该目录下所有词库进行初始化
        translatorOffline.initWordDict("dict", true, new EnWordTranslator.EnWordInitListener() {
            @Override
            public void success() {

            }
            public void fail(TranslateErrorCode errorCode){
                Log.d("translatorOffline","init false");
            }
        });

        //比较耗时，建议线程调用
        new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化离线句子库
                translatorOffline.initLineDict( Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/ce/");
                translatorOffline.initLineLanguage(LanguageConvert.CH2EN, new EnLineNMTTranslator.EnLineInitListener() {
                    @Override
                    public void success() {
                        Log.d("initLineLanguage", "init success");
                    }

                    @Override
                    public void fail(TranslateErrorCode errorCode) {
                        Log.d("initLineLanguage", "init failue");
                    }
                });
            }
        }).start();


        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);

        mLocalAsr = new IflyLocalASR(this);

        mTranslateInfoList = new ArrayList<TranslateInfo>();

        if(SAVEDATE){
            mTranslateInfoList.addAll(mTranslateDao.alterAllDate());
        }else{
            mTranslateDao.deleteAllDate();
        }
        myAdapter = new MyAdapter(this,mTranslateInfoList);
        mMainMeetingLy.setAdapter(myAdapter);
        mMainMeetingLy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TranslateInfo mTranslateInfo = mTranslateInfoList.get(i);
                mTranslateInfo.getTranslate_other();
               // Log.d(Tag,"mTts.isSpeaking():+++++++++++++++++++++++++++"+mTts.isSpeaking());

                if (NetWorkUtils.isNetworkConnected(getApplicationContext())){

                    TTSUtils.getInstance().speak(mTranslateInfo.getTranslate_other(), getApplicationContext());

                }else {
                    OfflineVoiceUtils.getInstance(getApplicationContext()).speak(mTranslateInfo.getTranslate_other(), getApplicationContext());
                }
            }
        });
    }
    private void auth() {
//        DUILiteSDK.openLog();//须在init之前调用
        /**
         * 初始化授权信息
         * @param context 上下文
         * @param apikey 从DUI平台产品里获取的ApiKey
         * @param productId 产品ID
         * @param listener 授权回调
         */

        DUILiteSDK.init(this, "718e43382d1583e51d727ade5b8d30e0", "278574096", new DUILiteSDK.InitListener() {
            @Override
            public void success() {
                initEngine();
                Log.d(Tag, "授权成功! ");
                //showTip("授权成功!");

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putBoolean("authStatus",true);
                msg.setData(data);
                msg.what = 10;
                handler.sendMessage(msg);

            }

            @Override
            public void error(String errorCode, String errorInfo) {

                showTip("授权失败!");
                Log.d(Tag, "授权失败, errorcode: " + errorCode + ",errorInfo:" + errorInfo);
            }
        });

    }

    private void initEngine() {
        if (!AICloudASREngine.checkLibValid()) {
            showTip("so加载失败");
        } else {
            mEngine = AICloudASREngine.createInstance();
            mEngine.setVadResource("vad.bin");//配置assets目录下的本地vad资源
            //mEngine.setVadResBinPath("/sdcard/aispeech/vad1.bin");//自定义vad资源加载目录。默认在assets目录下，无需配置
            mEngine.setLocalVadEnable(true);//设置是否启用本地vad,默认开启为true
//            mEngine.setPauseTime(300);//设置本地VAD右边界,默认为300ms
            mEngine.setPauseTime(420);


            mEngine.setServer("ws://asr.dui.ai/runtime/v2/recognize");//设置服务器地址，默认不用设置
            mEngine.setEnablePunctuation(true);//设置是否启用标点符号识别,默认为false关闭
            mEngine.setResourceType("comm");//设置识别引擎的资源类型,默认为comm
            mEngine.init(new AICloudASRListenerImpl());
            //mEngine.setRealback(true);
            mEngine.setNoSpeechTimeOut(0);
            mEngine.setMaxSpeechTimeS(600);//音频最大录音时长
            mEngine.setCloudVadEnable(true);//设置是否开启服务端的vad功能,默认开启为true
            mEngine.setSaveAudioPath("/sdcard/aispeech");//保存的音频路径,格式为.ogg
        }
    }

    /**
     *  讯飞离线语音识别接口监听
     *
     *
     * */
    @Override
    public void onVolumeChanged(int i, byte[] bytes) {

       // showTip("当前正在说话，音量大小：" + i);
        Log.d(Tag, String.valueOf(bytes.length));
    }

    @Override
    public void onBeginOfSpeech() {
        Log.d(Tag,"开始说话");
    }

    @Override
    public void onEndOfSpeech() {

        Log.d(Tag,"结束说话");
        mMainSpeak.setVisibility(View.VISIBLE);
        mSpeechRecognizingContent.setVisibility(View.GONE);
       // mLocalAsr.stopRecognize();
    }

    @Override
    public void onResult(com.iflytek.cloud.RecognizerResult recognizerResult, boolean b) {

        if (null != recognizerResult && !TextUtils.isEmpty(recognizerResult.getResultString())) {
            Log.d(Tag, "recognizer result：" + recognizerResult.getResultString());
            String text = "";

            text = JsonParser.parseLocalGrammarResult(recognizerResult.getResultString());
            text = text.replace("【结果】","").replace("\n","");
            int index = text.lastIndexOf("【置信度】");
            String newText = text.substring(0, index);

            mOriginalString = newText;
            if (mOriginalString == null || mOriginalString.length() <= 0){

                return;
            }
            mTranslateInfo= new TranslateInfo();

            if(!LangUtils.isChinese(newText)){

                mTranslateInfo.setType(TranslateInfo.TYPE_RECEIVE);
                String oldText = newText;
                mTranslateInfo.setTranslate_old(oldText.substring(0,1).toUpperCase()+oldText.toLowerCase(Locale.ENGLISH).substring(1,oldText.length()));

            }else{
                mTranslateInfo.setType(TranslateInfo.TYPE_SEND);
                mTranslateInfo.setTranslate_old(newText);
            }

            mOriginalString = newText;

            mTranslateInfo.setUrl(null);
            mTranslateInfo.setTranslate_other("翻译中...");
            mTranslateInfoList.add(mTranslateInfo);


            handler.sendEmptyMessage(9);

            new Thread(runnable).start();   //启动子线

            // 显示
            Log.d(Tag, "recognizer result : " + newText);

        } else {
            Log.d(Tag, "recognizer result : null");
        }
    }

    @Override
    public void onError(SpeechError speechError) {

        showTip(speechError.getErrorDescription());
        mLocalAsr.stopRecognize();
        mSpeechRecognizingContent.setVisibility(View.GONE);
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }


    /**
     *  思必驰在线语音识别接口
     *
     *
     * */
    private class AICloudASRListenerImpl implements AIASRListener {

        @Override
        public void onReadyForSpeech() {
//            showTip("请说话...");
        }

        @Override
        public void onBeginningOfSpeech() {
//            showTip("检测到说话");
        }

        @Override
        public void onEndOfSpeech() {
//            showTip("检测到语音停止，开始识别...");
            mMainSpeak.setVisibility(View.VISIBLE);
            mSpeechRecognizingContent.setVisibility(View.GONE);
        }

        @Override
        public void onRmsChanged(float rmsdB) {
//            showTip("RmsDB = " + rmsdB);
        }

        @Override
        public void onError(AIError error) {
            Log.e(Tag, "error:" + error.toString());
            showTip(error.getError());
            mSpeechRecognizingContent.setVisibility(View.GONE);
        }

        @Override
        public void onResults(AIResult results) {
            if (results.isLast()) {
                if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
                    Log.i(Tag, "result JSON = " + results.getResultObject().toString());
                    // 可以使用JSONResultParser来解析识别结果
                    // 结果按概率由大到小排序
                    JSONResultParser parser = new JSONResultParser(results.getResultObject().toString());
                    mOriginalString = parser.getText().toUpperCase();
                    if (mOriginalString == null || mOriginalString.length() <= 0){
//                        showTip("识别结果为空！");
                        mOriginalString = null;
                        return;
                    }

                    mTranslateInfo= new TranslateInfo();
                    String pingyin = null;
//                    String oldText = null;
                    try {
                        JSONObject  obj  = new JSONObject(results.getResultObject().toString()).getJSONObject("result");
                        if (obj.has("pinyin")) {
                            pingyin = obj.optString("pinyin").trim().replace(" ", "");
                        }

                        if(pingyin==null||"".equalsIgnoreCase(pingyin)){
                            mTranslateInfo.setType(TranslateInfo.TYPE_RECEIVE);
                            if (obj.has("rec")) {
//                                oldText = obj.optString("rec");
                                String oldText = obj.optString("rec");
                                mTranslateInfo.setTranslate_old(oldText.substring(0,1).toUpperCase()+oldText.toLowerCase(Locale.ENGLISH).substring(1,oldText.length()));
                            }
                        }else{
                            mTranslateInfo.setType(TranslateInfo.TYPE_SEND);
                            mTranslateInfo.setTranslate_old(mOriginalString);
//                            mTranslateInfo.setTranslate_old(parser.getText());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(Tag, "result pingyin = " + pingyin);

                    mTranslateInfo.setUrl(null);
                    mTranslateInfo.setTranslate_other("翻译中...");
                    mTranslateInfoList.add(mTranslateInfo);

                    handler.sendEmptyMessage(9);

                    Log.i(Tag, "result mOriginalString   = " + mOriginalString);
                    new Thread(runnable).start();   //启动子线
                }
            }
        }

        @Override
        public void onInit(int status) {
            Log.i(Tag, "Init result " + status);
            mAIConstantStatus = status;
            if (status == AIConstant.OPT_SUCCESS) {
                Log.e(Tag, "初始化成功!");
                mMainSpeak.setVisibility(View.VISIBLE);
            } else {
                Log.e(Tag, "初始化失败!code:" + status);
            }
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
//            Log.d(Tag, "录音机音频数据");
        }

    }

    private boolean ttsOrVoice;
    //handler 处理返回的请求结果
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 9:
                    myAdapter.notifyDataSetChanged();
                    break;
                case 8:

                    break;

                case 10:

                    if ((mEngine != null)&&(mAIConstantStatus == AIConstant.OPT_FAILED)){
                        if (mLocalAsr != null) {

                            mLocalAsr.release();
                        }
                         mEngine.start();
                    }

                    break;

                case 1:
                    Bundle data = msg.getData();

                    String funCtionString = null;

                    funCtionString = OrderForTTS.getInstance(getApplicationContext()).ttsForString(mTranslateInfo.getTranslate_old());

                    if(mTranslateInfo!=null){
                        mTranslateInfo.setTranslate_other(data.getString("resultString"));
                        myAdapter.notifyDataSetChanged();
                    }
                    if(SAVEDATE){
                        mTranslateDao.addDate(mTranslateInfo.getTranslate_old(),mTranslateInfo.getTranslate_other(),mTranslateInfo.getDate(),mTranslateInfo.getType());
                    }

                    String oldText = funCtionString;
                    if (ttsOrVoice == true) return;

                    if (oldText != null){
                        TranslateInfo tepTranslateInfo = new TranslateInfo();
                        tepTranslateInfo.setType(TranslateInfo.TYPE_RECEIVE);
                        tepTranslateInfo.setUrl(null);
                        tepTranslateInfo.setTranslate_other("翻译中...");
                        mTranslateInfoList.add(tepTranslateInfo);

                        mTranslateInfo = tepTranslateInfo;

                        tepTranslateInfo.setTranslate_old(oldText);
                        myAdapter.notifyDataSetChanged();
//                        if(SAVEDATE){
//
//                            mTranslateDao.addDate(tepTranslateInfo.getTranslate_old(),tepTranslateInfo.getTranslate_other(),tepTranslateInfo.getDate(),tepTranslateInfo.getType());
//                        }
                        ttsOrVoice = true;

                        mOriginalString = funCtionString;
                        new Thread(runnable).start();   //启动子线
                        funCtionString = null;
                    }



//                    mOriginalString = null;
//                    mToast = null;
//                    mTranslateInfo = null;
                    break;
            }

        }
    };


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String jsonString = null;
            String dst = null;
            if (NetWorkUtils.isNetworkConnected(getApplicationContext()) == true) {

                try {
                    jsonString = mtransApi.getTransResult(mOriginalString, "auto", "auto");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.e(Tag, "jsonString++++++++++++++++++++++++++++++++++!" + jsonString);
                if (jsonString == null || "".equalsIgnoreCase(jsonString))
                    return;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                    JSONArray message = jsonObject.getJSONArray("trans_result");
                    JSONObject info = message.getJSONObject(0);  //解析数组数据
                    dst = info.getString("dst");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{

                String from = "英文";
                String to = "中文";
                Language langFrom = LanguageUtils.getLangByName(from);
                Language langTo = LanguageUtils.getLangByName(to);
                final TranslateParameters parameters = new  TranslateParameters.Builder().
                        from(langFrom).to(langTo).useAutoConvertWord(false).useAutoConvertLine(false).build();

                mOriginalString = "hello";
                //dst = "离线翻译中...";
                dst = "";
                //translatorOffline.lookupNative(mOriginalString, parameters, "requestId", localTranslatorlistener);

            }

            mOriginalString = null;

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("resultString", dst);
            msg.setData(data);
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        TTSUtils.getInstance().release();
        OfflineVoiceUtils.getInstance(this).release();
        mLocalAsr.release();
    }

    private  void showTip(final String str) {
        if(true){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mToast == null){
                        mToast = Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT);  //正常执行
                    }else {
                        mToast.setText(str);  //用于覆盖前面未消失的提示信息
                    }
                    mToast.show();
                }
            });
        }
    }


    /**
    *离线翻译结果
    *
    * */
    TranslateListener localTranslatorlistener = new TranslateListener() {
        @Override
        public void onResult(final Translate result, String input, String requestId) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TranslateData td = new TranslateData(
                            System.currentTimeMillis(), result);

                }
            });
        }

        @Override
        public void onError(final TranslateErrorCode error, String requestId) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showTip("查询错误:" + error.name());
                }
            });
        }

        @Override
        public void onResult(final List<Translate> results, List<String> inputs, final List<TranslateErrorCode> errors, String requestId) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    StringBuilder sb = new StringBuilder("错误如下：");
                    boolean error = false;
                    for (int i = 0; i < results.size(); i++) {
                        Translate result = results.get(i);
                        if (result == null) {
                            sb.append(i + " " + errors.get(i).getCode() + "  ");
                            error = true;
                            continue;
                        }
                        TranslateData td = new TranslateData(
                                System.currentTimeMillis(), result);

                    }
                    if (error) {
                        showTip(sb.toString());
                    }
                }
            });
        }
    };


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void showPopupWindow() {
        //添加视图
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.settings, null);

        view.findViewById(R.id.set_wifi).setOnClickListener(this);
        view.findViewById(R.id.set_update).setOnClickListener(this);
        view.findViewById(R.id.set_about).setOnClickListener(this);
      //  ((TextView)view.findViewById(R.id.set_imei)).setText(EWeChatApp.getIMEI());

        //设定弹出窗口容器的属性
        window = new PopupWindow(view, screenWidth/5,WindowManager.LayoutParams.MATCH_PARENT);

        //设置popwindow弹出窗体可点击
        window.setFocusable(true);

        //实例化一个ColorDrawable颜色为半透明
        //ColorDrawable drawable = new ColorDrawable(getResources().getColor(R.color.transparent));
        ColorDrawable dw = new ColorDrawable(0x55000000);//
        window.setBackgroundDrawable(dw);

        //设置popwindow的显示和消失动画
//        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        window.showAsDropDown(mMainMeetingLy,0, -(window.getContentView().getMeasuredHeight()+mMainMeetingLy.getHeight()), Gravity.RIGHT);

        //检验popWindow里的button是否可以点击
        view.findViewById(R.id.set_wifi).setOnClickListener(this);
        view.findViewById(R.id.set_update).setOnClickListener(this);
        view.findViewById(R.id.set_about).setOnClickListener(this);

        //popwindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_wifi:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            case R.id.set_update:
                update();
                break;
            case R.id.set_about:
                about();
                break;
            case R.id.main_settings:
                showPopupWindow();
                break;
            case R.id.main_speaking:
                if (mAIConstantStatus == AIConstant.OPT_SUCCESS) {
                    if(mSpeechRecognizingContent.getVisibility()==View.VISIBLE){
                        mSpeechRecognizingContent.setVisibility(View.GONE);

                    }else{
                        mSpeechRecognizingContent.setVisibility(View.VISIBLE);
                        mMainSpeak.setVisibility(View.GONE);
                        ttsOrVoice = false;
                        if (NetWorkUtils.isNetworkConnected(this) == true){

                            if (mLocalAsr != null) {

                                mLocalAsr.release();
                            }
                            if (mEngine == null){
                                auth();
                                return;
                            }
                            if (mEngine != null){

                                mEngine.start();
                            }

                        }else {

                            if (mLocalAsr == null) return;
                            if (mEngine != null){

                                mEngine.destroy();
                                mEngine = null;
                            }
                              mLocalAsr.startRecognize(MainActivity.this);
                        }

                    }
                }
                break;
        }
    }

    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View  viewAbout = getLayoutInflater().inflate(R.layout.pager_about,null);
        builder.setView(viewAbout);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }

        });

        builder.show();
    }

    private void update() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View  viewAbout = getLayoutInflater().inflate(R.layout.pager_update,null);
        builder.setView(viewAbout);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }

        });

        builder.show();
    }

    private void updateVersion() {
        new PgyUpdateManager.Builder()
                .setForced(false)                //设置是否强制更新,非自定义回调更新接口此方法有用
                .setUserCanRetry(false)         //失败后是否提示重新下载，非自定义下载 apk 回调此方法有用
                .setDeleteHistroyApk(true)     // 检查更新前是否删除本地历史 Apk
                .setUpdateManagerListener(new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                        //没有更新是回调此方法
                        Log.d("pgyer", "there is no new version");
                        showNoNewUpdate("已是最新版本",0,null);
                    }

                    @Override
                    public void onUpdateAvailable(AppBean appBean) {
                        //没有更新是回调此方法
                        Log.d("pgyer", "there is new version can update"
                                + "new versionCode is " + appBean.getVersionCode());
                        StringBuffer sb = new StringBuffer("版本号:");
                        sb.append(appBean.getVersionCode());
                        sb.append("\n版本名称:");
                        sb.append(appBean.getVersionName());
                        if(appBean.getReleaseNote()!=null&&!"".equalsIgnoreCase(appBean.getReleaseNote())){
                            sb.append("\n更新描述:");
                            sb.append(appBean.getReleaseNote());
                        }

                        showNoNewUpdate(sb.toString(),1,appBean.getDownloadURL());

                    }

                    @Override
                    public void checkUpdateFailed(Exception e) {
                        //更新检测失败回调
                        Log.e("pgyer", "check update failed ", e);

                    }
                })
                //注意 ：下载方法调用 PgyUpdateManager.downLoadApk(appBean.getDownloadURL()); 此回调才有效
                .setDownloadFileListener(new DownloadFileListener() {   // 使用蒲公英提供的下载方法，这个接口才有效。
                    @Override
                    public void downloadFailed() {
                        //下载失败
                        Log.e("pgyer", "download apk failed");
                    }

                    @Override
                    public void downloadSuccessful(Uri uri) {
                        Log.e("pgyer", "download apk successful");
                        if(dialog!=null&&dialog.isShowing()){
                            dialog.dismiss();
                        }
                        PgyUpdateManager.installApk(uri);  // 使用蒲公英提供的安装方法提示用户 安装apk
                    }

                    @Override
                    public void onProgressUpdate(Integer... integers) {
                        Log.e("pgyer", "update download apk progress" + integers);
                    }
                })
                .register();
    }

    private void showDialog(){
        dialog.setIcon(R.drawable.ic_launcher);
        dialog.setMessage("软件更新中...");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true); // 填false表示是明确显示进度的 填true表示不是明确显示进度的

        dialog.show();
    }

    private void showNoNewUpdate(String msg, final int type, final String downlaodURL){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView tvUpdateMsg = new TextView(this);
        if(type==1){
            tvUpdateMsg.setHeight(150);
        }else{
            tvUpdateMsg.setHeight(50);
        }

        tvUpdateMsg.setPadding(10,5,0,0);
        tvUpdateMsg.setText(msg);
        builder.setView(tvUpdateMsg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(type==1){
                    showDialog();
                    PgyUpdateManager.downLoadApk(downlaodURL);
                }
                dialog.cancel();
            }

        });

        if(type==1){
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                }
            });
        }

        builder.show();
    }

     private void getPermmssion(){

         ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                 Manifest.permission.READ_PHONE_STATE,
                 Manifest.permission.WRITE_EXTERNAL_STORAGE,
                 Manifest.permission.READ_EXTERNAL_STORAGE,
                 Manifest.permission.RECORD_AUDIO,
         }, 1);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                Log.d("permission", "permission denied to READ_EXTERNAL_STORAGE - requesting it");
//                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//
//                requestPermissions(permissions, 1);
//            }
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                Log.d("permission", "permission denied to READ_EXTERNAL_STORAGE - requesting it");
//                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//
//                requestPermissions(permissions, 1);
//            }
//a
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
//                    == PackageManager.PERMISSION_DENIED) {
//
//                Log.d("permission", "permission denied to READ_PHONE_STATE - requesting it");
//                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
//
//                requestPermissions(permissions, 1);
//
//            }
//            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
//
//                Log.d("permission", "permission denied to RECORD_AUDIO - requesting it");
//                String[] permissions = {Manifest.permission.RECORD_AUDIO};
//
//                requestPermissions(permissions, 1);
//
//            }




        //}
    }
}
