package com.eningqu.emeeting.tools;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.eningqu.emeeting.utils.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;


public class SpeechService {

    private static final String TAG = "SpeechRecognizeService";

    public interface Config{

        String IFLYTEK_APPID = "5b0e121b";
        String VAD_BOS_VALUE = "4000";
        String VAD_EOS_VALUE = "1000";
        String ASR_PTT_VALUE = "1";
    }

    public static final int ENGINE_ONLINE = 0;
    public static final int ENGINE_OFFLINE = 1;
    public static final int ENGINE_MIX = 2;

    private Context mContext;
    private int mEngineType = ENGINE_ONLINE;
    // 语音听写对象
    private SpeechRecognizer mRecognizer;
    private SpeechServiceListener mListener;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    public void setSpeechServiceListener(SpeechServiceListener listener) {
        this.mListener = listener;
    }

    public SpeechService(Context context, int engineType){
        mContext = context;
        mEngineType = engineType;

        // 根据语音识别类型,初始化对应参数
        initParams();
    }

    /**
     * 初始化语音识别参数
     */
    private void initParams() {
        SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "=" + Config.IFLYTEK_APPID);

        mRecognizer = SpeechRecognizer.createRecognizer(mContext, mInitListener);

        String strEngineType = "";

        switch (mEngineType) {
            case ENGINE_ONLINE:
                strEngineType = SpeechConstant.TYPE_CLOUD;
                break;
            case ENGINE_OFFLINE:
                strEngineType = SpeechConstant.TYPE_LOCAL;
                break;
            case ENGINE_MIX:
                strEngineType = SpeechConstant.TYPE_MIX;
                break;
            default:
                // 默认使用混合识别模式
                strEngineType = SpeechConstant.TYPE_MIX;
                break;
        }

        if (mEngineType != ENGINE_ONLINE) {
            /**
             * 选择本地听写 判断是否安装语记,未安装则跳转到提示安装页面
             */
            if (!SpeechUtility.getUtility().checkServiceInstalled()) {
               // mInstaller.install();
            } else {
                String result = FucUtil.checkLocalResource();
                if (!TextUtils.isEmpty(result) && mListener != null) {
                    mListener.onLocalResourceError(result);
                }
            }
        }

        initCommonParams(strEngineType);
    }

    private void initCommonParams(String engineType) {
        // 清空参数
        mRecognizer.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, engineType);

        // 设置返回结果格式
        mRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        mRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 设置语言区域
        mRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mRecognizer.setParameter(SpeechConstant.VAD_BOS, Config.VAD_BOS_VALUE);

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mRecognizer.setParameter(SpeechConstant.VAD_EOS, Config.VAD_EOS_VALUE);

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mRecognizer.setParameter(SpeechConstant.ASR_PTT, Config.ASR_PTT_VALUE);

    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                if (mListener != null) {
                    mListener.onInitError("初始化失败，错误码：" + code);
                }
            }
        }
    };

    /**
     * 开始监听用户讲话
     */
    public void startListening(){
        if (mRecognizer != null && mRecognizerListener != null) {
            // 开始监听之前,需要清除map中的历史数据
            mIatResults.clear();

            mRecognizer.startListening(mRecognizerListener);
        } else {
            if (mListener != null) {
                mListener.onInitError("语音听写对象或监听器初始化失败");
            }
        }
    }


    public void speech_stopListening() {
        if (mListener != null) {
            mRecognizer.stopListening();
        }
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            if (mListener != null) {
                mListener.onRecognizerPrepared();
            }
        }

        @Override
        public void onError(SpeechError error) {
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            if (mListener != null) {
                mListener.onRecognizerError(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            if (mListener != null) {
                mListener.onSpeechFinished();
            }
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            String printResult = printResult(results);

            if (mListener != null) {
                mListener.onGetRecognizerResult(printResult);
            }

            if (isLast) {
                if (mListener != null) {
                    mListener.onGetRecognizerResultEnd(printResult);
                }
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG, "返回音频数据："+data.length);

            if (mListener != null) {
                mListener.onVolumeChanged(volume);
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    /**
     * 将语音识别结果转换为文字信息
     *
     * @param results
     * @return
     */
    private String printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        return resultBuffer.toString();
    }

    /**
     * 结束使用后请务调用此方法来释放资源
     */
    public void release() {
        if (mRecognizer != null) {
            mRecognizer.cancel();
            mRecognizer.destroy();
        }
    }

    public interface SpeechServiceListener {
        /**
         * 初始化失败的回调
         *
         * @param errorInfo 错误信息
         */
        void onInitError(String errorInfo);

        void onLocalResourceError(String result);

        /**
         * 音量发生变化时的回调
         *
         * @param volume
         */
        void onVolumeChanged(int volume);

        /**
         * 已获取最后的结果
         */
        void onGetRecognizerResultEnd(String result);

        /**
         * 获取到结果的回调
         *
         * @param
         */
        void onGetRecognizerResult(String result);

        /**
         * 用户讲话完毕的回调
         */
        void onSpeechFinished();

        /**
         * 准备完毕的回调
         */
        void onRecognizerPrepared();

        /**
         * 识别失败的回调
         *
         * @param errorInfo
         */
        void onRecognizerError(String errorInfo);
    }


}
