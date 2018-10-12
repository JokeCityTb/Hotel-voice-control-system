package com.eningqu.emeeting.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.eningqu.emeeting.net.TransApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import javax.security.auth.callback.Callback;


@SuppressLint("ValidFragment")
public class LanguageFragment extends Fragment {

    private String mTitle;

    final String Tag = this.getClass().getName();
    String mOriginalString = null;

    private static final String APP_ID = "20170713000064403";
    private static final String SECURITY_KEY = "Byg3e6QIsqS3E4ypHfXR";

    TransApi mtransApi;
    Toast mToast;
    AICloudASREngine mEngine;//语音识别引擎

    TextView resultText;
    Button btnStart;
    Button btnStop;

    public static LanguageFragment getInstance(String title) {
        LanguageFragment sf = new LanguageFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*初始化百度翻译*/
        mtransApi = new TransApi(APP_ID, SECURITY_KEY);

        auth();
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.asr, null);

        resultText = (TextView) v.findViewById(R.id.text_result);
        resultText.setText("正在加载资源...");
        btnStart = (Button) v.findViewById(R.id.btn_start);
        btnStop = (Button) v.findViewById(R.id.btn_end);

        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mEngine == null) return;
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                mEngine.start();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                resultText.setText("已取消");
                mEngine.cancel();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }


    private void auth() {

        DUILiteSDK.openLog();//须在init之前调用
        /**
         * 初始化授权信息
         * @param context 上下文
         * @param apikey 从DUI平台产品里获取的ApiKey
         * @param productId 产品ID
         * @param listener 授权回调
         */
        DUILiteSDK.init(getActivity(), "b1ae5c155452b1ae5c1554525b5c3a8c", "278574096", new DUILiteSDK.InitListener() {
            @Override
            public void success() {
                Log.d(Tag, "授权成功! ");
                initEngine();
            }

            @Override
            public void error(String errorCode, String errorInfo) {
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
            mEngine.setPauseTime(500);//设置本地VAD右边界,默认为300ms
            mEngine.setServer("ws://asr.dui.ai/runtime/v2/recognize");//设置服务器地址，默认不用设置
            mEngine.setEnablePunctuation(false);//设置是否启用标点符号识别,默认为false关闭
            mEngine.setResourceType("comm");//设置识别引擎的资源类型,默认为comm
            mEngine.init(new AICloudASRListenerImpl());
           // mEngine.setNoSpeechTimeOut(0);
            //mEngine.setMaxSpeechTimeS(0);//音频最大录音时长
            mEngine.setCloudVadEnable(true);//设置是否开启服务端的vad功能,默认开启为true
            mEngine.setSaveAudioPath("/sdcard/aispeech");//保存的音频路径,格式为.ogg

        }

    }


    //handler 处理返回的请求结果
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("resultString");
            //
            //  TODO 更新界面
            //
            //Log.i(Tag, "翻译结果-->" + val);

            showTip("翻译结果-->" + val);

            resultText.append("=========================="  + "\n");
            resultText.append("翻译结果为 :  " + val);


            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            mOriginalString = null;
            mToast = null;
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            //
            // TODO: http request.
            //
            String jsonString = null;
            String dst = null;
            try {
                jsonString = mtransApi.getTransResult(mOriginalString, "auto", "auto");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonString);
                JSONArray message = jsonObject.getJSONArray("trans_result");
                JSONObject info = message.getJSONObject(0);  //解析数组数据
                dst =info.getString("dst");

            } catch (JSONException e) {
                e.printStackTrace();

            }

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("resultString", dst);
            msg.setData(data);
            handler.sendMessage(msg);

        }
    };




    private class AICloudASRListenerImpl implements AIASRListener {

        @Override
        public void onReadyForSpeech() {

            showTip("请说话...");
            resultText.setText("请说话...");
        }

        @Override
        public void onBeginningOfSpeech() {

            showTip("检测到说话");
            resultText.setText("检测到说话");
        }

        @Override
        public void onEndOfSpeech() {

            showTip("检测到语音停止，开始识别...");
            resultText.append("检测到语音停止，开始识别...\n");
        }

        @Override
        public void onRmsChanged(float rmsdB) {

            showTip("RmsDB = " + rmsdB);
        }

        @Override
        public void onError(AIError error) {
            Log.e(Tag, "error:" + error.toString());

            showTip(error.toString());
            resultText.setText(error.toString());
        }


        @Override
        public void onResults(AIResult results) {
            if (results.isLast()) {
                if (results.getResultType() == AIConstant.AIENGINE_MESSAGE_TYPE_JSON) {
                    Log.i(Tag, "result JSON = " + results.getResultObject().toString());
                    // 可以使用JSONResultParser来解析识别结果
                    // 结果按概率由大到小排序
                    JSONResultParser parser = new JSONResultParser(results.getResultObject()
                            .toString());

                    resultText.append("识别结果为 :  " + parser.getText() + "\n");
                   // resultText.append("识别结果为 :  " + results.getResultObject().toString());

                    mOriginalString = parser.getText();
                    if (mOriginalString == null || mOriginalString.length() <= 0){

                        showTip("识别结果为空！");
                        btnStart.setEnabled(true);
                        btnStop.setEnabled(false);
                        mOriginalString = null;
                        return;
                    }
                    showTip("识别结果为 :  " + parser.getText() + "\n");

                    new Thread(runnable).start();   //启动子线程




                }
            }
        }

        @Override
        public void onInit(int status) {
            Log.i(Tag, "Init result " + status);
            if (status == AIConstant.OPT_SUCCESS) {

                Log.e(Tag, "初始化成功!");
                resultText.setText("初始化成功!");
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);

            } else {
                btnStart.setEnabled(false);
                btnStop.setEnabled(false);
                resultText.setText("初始化失败!code:" + status);
                Log.e(Tag, "初始化失败!code:" + status);
            }
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub
            Log.d(Tag, "录音机音频数据");
        }

    }

    private  void showTip(final String str) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mToast == null)
                {
                    mToast = Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);  //正常执行

                }
                else {
                    mToast.setText(str);  //用于覆盖前面未消失的提示信息
                }
                mToast.show();

            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mEngine != null) {
            mEngine.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEngine != null) {
            mEngine.destroy();
            mEngine = null;
        }
    }
}