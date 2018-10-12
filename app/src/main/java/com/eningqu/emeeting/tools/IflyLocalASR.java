package com.eningqu.emeeting.tools;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.eningqu.emeeting.activity.MainActivity;
import com.eningqu.emeeting.utils.FucUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;

import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;

public class IflyLocalASR {

    private final static String mTag = "IflyGrammar";
    private Context mContext;

    // 语音识别对象
    private SpeechRecognizer mAsr;
    private Toast mToast;

    private String mContent;// 语法、词典临时变量

    // 本地语法文件
    private String mLocalGrammar = null;
    // 本地词典
    private String mLocalLexicon = null;

    // 本地语法构建路径
    private String grmPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/msc/test";

    // 返回结果格式，支持：xml,json
    private String mResultType = "json";

    private final String GRAMMAR_TYPE_BNF = "bnf";

    private String mEngineType = SpeechConstant.TYPE_LOCAL;
    private int mRet;
    //初始化监听器
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(mTag, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                //showTip("初始化失败,错误码：" + code);
            }else {
               // Log.e("","初始化success");

            }
        }
    };


    //构建语法监听器
    private GrammarListener mGrammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {

                //showTip("语法构建成功：" + grammarId);
            } else {
               // showTip("语法构建失败,错误码：" + error.getErrorCode());
            }

        }
    };

    //更新词典监听器
    private LexiconListener mLexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error == null) {
               // showTip("词典更新成功");
            } else {
                //showTip("词典更新失败,错误码：" + error.getErrorCode());
            }
        }
    };

    public IflyLocalASR(Context context) {
        mContext = context;
        mLocalLexicon = "小欧小欧\n";
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mAsr = SpeechRecognizer.createRecognizer(mContext, mInitListener);
        mLocalGrammar = FucUtil.readFile(context,"call.bnf", "utf-8");
        buildGrammar();
        updateLexicon();

    }

    //设置参数
    public boolean setParam() {
        boolean result = false;
        // 清空参数
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // 设置识别引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);

        // 设置本地识别资源
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        // 设置返回结果格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
        // 设置本地识别使用语法id
        mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "call");
        // 设置识别的门限值
        mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "30");
        // 使用8k音频的时候请解开注释
//       mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        result = true;
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/asr.wav");
        return result;
    }

    //获取识别资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "res/asr/common.jet"));
        //识别8k资源-使用8k的时候请解开注释
        return tempBuffer.toString();
    }

    private void showTip(final String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }

    //构建语法
    /*********************************
     *
     * 离线语法文件构建
     *
     * *******************************/
    //本地-构建语法文件，生成语法id
    private void buildGrammar() {
        // 初始化语法、命令词
        mContent = new String(mLocalGrammar);

        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // 设置文本编码格式
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        //使用8k音频的时候请解开注释
//             mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        // 设置资源路径
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        mRet = mAsr.buildGrammar(GRAMMAR_TYPE_BNF, mLocalGrammar, mGrammarListener);
        if (mRet != ErrorCode.SUCCESS) {
            showTip("语法构建失败,错误码：" + mRet);
        }
    }

    //本地-更新词典
    private void updateLexicon() {
        mContent = new String(mLocalLexicon);
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // 设置资源路径
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置语法构建路径
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
        // 设置语法名称
        mAsr.setParameter(SpeechConstant.GRAMMAR_LIST, "voiceRoom");
        // 设置文本编码格式
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        mRet = mAsr.updateLexicon("voiceRoom", mContent, mLexiconListener);
        if (mRet != ErrorCode.SUCCESS) {
            showTip("更新词典失败,错误码：" + mRet);
        }
    }
    //开始识别
    public void startRecognize(MainActivity mainActivity) {
        // 设置参数
        if (!setParam()) {
            showTip("请先构建语法。");
            return;
        }
        mRet = mAsr.startListening(mainActivity);
        if (mRet != ErrorCode.SUCCESS) {
            showTip("识别失败,错误码: " + mRet);
        }
    }

    //停止识别
    public void stopRecognize() {
        mAsr.stopListening();
       // showTip("停止识别");
    }

    //取消识别
    public void cancelRecognize() {
        mAsr.cancel();
       // showTip("取消识别");
    }

    //反初始化
    public void release() {
        if (null != mAsr) {
            // 退出时释放连接
            mAsr.cancel();
            mAsr.destroy();
        }
    }


}
