package com.eningqu.emeeting.tools;

import android.content.Context;

import com.eningqu.emeeting.utils.NetWorkUtils;

public class OrderForTTS {

    private static OrderForTTS instance = null;
    Context mcontext;

    public static OrderForTTS getInstance(final Context context) {
        if (instance == null){
            instance = new OrderForTTS();
            instance.setContext(context);
        }
        return instance;
    }
    public void setContext(Context context) {
        this.mcontext = context;
    }

    /**根据翻译结果,如果是中文就直接进行播报应答，如果是英文，那么先将英文翻译为中文，在进行播报应答*/

    public String  ttsForString(String result) {

        String transString = null;
        if (NetWorkUtils.isNetworkConnected(mcontext) == true) {

          transString =  ttsForOnLineString(result);

        }else {
           transString = ttsForOffLineString(result);
        }
        return transString;
    }

    private String  ttsForOnLineString(String result){

        String onLineString = null;

        if ((result.indexOf("设定") != -1) || (result.indexOf("设") != -1)|| (result.indexOf("预约") != -1)) {


            if (result.indexOf("预约") != -1) {
                onLineString =  "好的,为您" + result.substring(result.indexOf("预约"));
                TTSUtils.getInstance().speak(onLineString, mcontext);

            }else {
                onLineString =  "好的,为您" + result.substring(result.indexOf("设"));
                TTSUtils.getInstance().speak(onLineString, mcontext);
            }


        } else if (result.indexOf("呼叫柜台") != -1) {
            onLineString =  "好的,为您呼叫柜台";
            TTSUtils.getInstance().speak(onLineString, mcontext);

        } else if ((result.indexOf("请打扫") != -1) || (result.indexOf("打扫") != -1)) {
            onLineString = "好的,为您预约打扫";
            TTSUtils.getInstance().speak(onLineString, mcontext);

        } else if (result.indexOf("勿打扰") != -1) {
            onLineString = "好的,已启动勿扰";
            TTSUtils.getInstance().speak(onLineString, mcontext);

        } else if ((result.indexOf("今天天气") != -1) || result.indexOf("今天天气怎么样") != -1) {
            onLineString =  "今天天气晴朗,温度26度,紫外线量过高,空气品质差.";
            TTSUtils.getInstance().speak(onLineString, mcontext);

        } else if (result.indexOf("台北天气") != -1) {
            onLineString = "今天台北天气晴朗,温度26度,紫外线量过高,空气品质差.";
            TTSUtils.getInstance().speak(onLineString, mcontext);

        } else if ((result.indexOf("本周驻诊医生及时间") != -1)||(result.indexOf("驻诊医生及时间") != -1)) {
            onLineString = "好的,本周驻诊医生为老王,时间从上午九点到中午12点";
            TTSUtils.getInstance().speak(onLineString, mcontext);

        } else if ((result.indexOf("查询本周社区活动") != -1) || (result.indexOf("社区活动") != -1)) {
            onLineString = "好的,本周社区周三三时有相亲活动，地点6点半教室";
            TTSUtils.getInstance().speak(onLineString, mcontext);
        }else if (result.indexOf("家里总用电量是多少") != -1 || (result.indexOf("家里用电是多少") != -1) ||result.indexOf("家里用电是") != -1) {
            onLineString = "好的,为您确认总用电量，总用电量是138度";
            TTSUtils.getInstance().speak(onLineString, mcontext);

        }else if ((result.indexOf("开") != -1) || (result.indexOf("开启") != -1) || (result.indexOf("打开") != -1) || (result.indexOf("启动") != -1)) {

            if (result.indexOf("房间灯") != -1) {
                onLineString =  "好的,为您打开房间灯";
                TTSUtils.getInstance().speak(onLineString, mcontext);
            } else if (result.indexOf("浴厕灯") != -1) {
                onLineString = "好的,为您打开浴厕灯";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("客厅灯") != -1) {
                onLineString = "好的,为您打开客厅灯";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("床头灯") != -1) {
                onLineString = "好的,为您打开床头灯";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("电影模式") != -1) {
                onLineString = "好的,为您开启电影模式";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("浪漫模式") != -1) {
                onLineString = "好的,为您开启浪漫模式";
                TTSUtils.getInstance().speak(onLineString, mcontext);
            } else if (result.indexOf("房间电视") != -1) {
                onLineString = "好的,为您打开房间电视";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("客厅电视") != -1) {
                onLineString = "好的,为您打开客厅电视";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("冷气") != -1) {
                onLineString = "好的,为您打开冷气";
                TTSUtils.getInstance().speak(onLineString, mcontext);
            } else if (result.indexOf("客厅窗帘") != -1) {
                onLineString = "好的,为您开启客厅窗帘";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("房间窗帘") != -1) {
                onLineString = "好的,为您打开房间窗帘";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("音响") != -1) {
                onLineString = "好的,为您打开音响";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("预热烤箱") != -1) {
                onLineString = "好的,为您开启预热烤箱";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("洗衣机") != -1) {
                onLineString = "好的,为您启动洗衣机";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("扫地机器人") != -1) {
                onLineString = "好的,为您启动扫地机器人";
                TTSUtils.getInstance().speak(onLineString, mcontext);
            } else if (result.indexOf("警报系统") != -1) {
                onLineString = "好的,为您启动警报提醒系统";
                TTSUtils.getInstance().speak(onLineString, mcontext);
            }else if(result.indexOf("开") != -1){

                onLineString = "好的,为您" + result.substring(result.indexOf("开"));
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
            }
        } else if ((result.indexOf("关") != -1) || (result.indexOf("关闭") != -1)) {

            if (result.indexOf("房间灯") != -1) {
                onLineString = "好的,为您关闭房间灯";
                TTSUtils.getInstance().speak(onLineString, mcontext);
            } else if (result.indexOf("浴厕灯") != -1) {
                onLineString = "好的,为您关闭浴厕灯";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("客厅灯") != -1) {
                onLineString =  "好的,为您关闭客厅灯";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("床头灯") != -1) {
                onLineString = "好的,为您关闭床头灯";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("电影模式") != -1) {
                onLineString = "好的,为您关闭电影模式";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("浪漫模式") != -1) {
                onLineString = "好的,为您关闭浪漫模式";
                TTSUtils.getInstance().speak(onLineString, mcontext);
            } else if (result.indexOf("房间电视") != -1) {

                onLineString =  "好的,为您关闭房间电视";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("客厅电视") != -1) {
                onLineString = "好的,为您关闭客厅电视";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("冷气") != -1) {
                onLineString = "好的,为您关闭冷气";
                TTSUtils.getInstance().speak(onLineString, mcontext);
            } else if (result.indexOf("客厅窗帘") != -1) {
                onLineString = "好的,为您关闭客厅窗帘";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("房间窗帘") != -1) {
                onLineString = "好的,为您关闭房间窗帘";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("音响") != -1) {
                onLineString = "好的,为您关闭音响";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("预热烤箱") != -1) {
                onLineString = "好的,为您关闭预热烤箱";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("洗衣机") != -1) {
                onLineString = "好的,为您关闭洗衣机";
                TTSUtils.getInstance().speak(onLineString, mcontext);

            } else if (result.indexOf("扫地机器人") != -1) {
                onLineString = "好的,为您关闭扫地机器人";
                TTSUtils.getInstance().speak("好的,为您关闭扫地机器人", mcontext);
            } else if (result.indexOf("警报系统") != -1) {

                onLineString = "好的,为您关闭警报系统";
                TTSUtils.getInstance().speak(onLineString, mcontext);
            }else {
                onLineString = "好的,为您" + result.substring(result.indexOf("关"));
                TTSUtils.getInstance().speak(onLineString, mcontext);
            }

        }else  if (result.indexOf("放") != -1) {
            onLineString = "好的,为您" + result.substring(result.indexOf("放"));
            TTSUtils.getInstance().speak(onLineString, mcontext);
        }else if (result.indexOf("煮") != -1) {
            onLineString = "好的,为您开启煮咖啡机";
            TTSUtils.getInstance().speak(onLineString, mcontext);
        }else if (result.indexOf("家里总用电量是多少") != -1 || (result.indexOf("家里用电是多少") != -1) ||result.indexOf("用电") != -1) {
            onLineString = "好的,为您确认总用电量,总用电量是120度";
            TTSUtils.getInstance().speak(onLineString, mcontext);
        }
        return onLineString;
    }

    private String  ttsForOffLineString(String result){

            String onLineString = null;

            if ((result.indexOf("设定") != -1) || (result.indexOf("设") != -1)|| (result.indexOf("预约") != -1)) {

                if (result.indexOf("预约") != -1) {
                    onLineString =  "好的,为您" + result.substring(result.indexOf("预约"));
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                }else {
                    onLineString =  "好的,为您" + result.substring(result.indexOf("设"));
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                }



            } else if (result.indexOf("呼叫柜台") != -1) {
                onLineString =  "好的,为您呼叫柜台";
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

            } else if ((result.indexOf("请打扫") != -1) || (result.indexOf("打扫") != -1)) {
                onLineString = "好的,为您预约打扫";
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

            } else if (result.indexOf("勿打扰") != -1) {
                onLineString = "好的,已启动勿扰";
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

            } else if ((result.indexOf("今天天气") != -1) || result.indexOf("今天天气怎么样") != -1) {

                onLineString =  "今天天气晴朗,温度26度,紫外线量过高,空气品质差.";
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

            } else if (result.indexOf("台北天气") != -1) {
                onLineString = "今天台北天气晴朗,温度26度,紫外线量过高,空气品质差.";
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

            } else if ((result.indexOf("本周驻诊医生及时间") != -1)||(result.indexOf("驻诊医生及时间") != -1)) {
                onLineString = "好的,本周驻诊医生为老王,时间从上午九点到中午12点";
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

            } else if ((result.indexOf("查询本周社区活动") != -1) || (result.indexOf("社区活动") != -1)) {
                onLineString = "好的,本周社区周三三时有相亲活动，地点6点半教室";
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
            }else if (result.indexOf("家里总用电量是多少") != -1 || (result.indexOf("家里用电是多少") != -1) ||result.indexOf("家里用电是") != -1) {
                onLineString = "好的,为您确认总用电量，总用电量是138度";
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

            }else if ((result.indexOf("开") != -1) || (result.indexOf("开启") != -1) || (result.indexOf("打开") != -1) || (result.indexOf("启动") != -1)) {

                if (result.indexOf("房间灯") != -1) {
                    onLineString =  "好的,为您打开房间灯";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                } else if (result.indexOf("浴厕灯") != -1) {
                    onLineString = "好的,为您打开浴厕灯";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("客厅灯") != -1) {
                    onLineString = "好的,为您打开客厅灯";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("床头灯") != -1) {
                    onLineString = "好的,为您打开床头灯";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("电影模式") != -1) {
                    onLineString = "好的,为您开启电影模式";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("浪漫模式") != -1) {
                    onLineString = "好的,为您开启浪漫模式";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                } else if (result.indexOf("房间电视") != -1) {
                    onLineString = "好的,为您打开房间电视";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("客厅电视") != -1) {
                    onLineString = "好的,为您打开客厅电视";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("冷气") != -1) {
                    onLineString = "好的,为您打开冷气";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                } else if (result.indexOf("客厅窗帘") != -1) {
                    onLineString = "好的,为您开启客厅窗帘";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("房间窗帘") != -1) {
                    onLineString = "好的,为您打开房间窗帘";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("音响") != -1) {
                    onLineString = "好的,为您打开音响";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("预热烤箱") != -1) {
                    onLineString = "好的,为您开启预热烤箱";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("洗衣机") != -1) {
                    onLineString = "好的,为您启动洗衣机";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("扫地机器人") != -1) {
                    onLineString = "好的,为您启动扫地机器人";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                } else if (result.indexOf("警报系统") != -1) {
                    onLineString = "好的,为您启动警报提醒系统";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                }
            } else if ((result.indexOf("关") != -1) || (result.indexOf("关闭") != -1)) {

                if (result.indexOf("房间灯") != -1) {
                    onLineString = "好的,为您关闭房间灯";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                } else if (result.indexOf("浴厕灯") != -1) {
                    onLineString = "好的,为您关闭浴厕灯";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("客厅灯") != -1) {
                    onLineString =  "好的,为您关闭客厅灯";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("床头灯") != -1) {
                    onLineString = "好的,为您关闭床头灯";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("电影模式") != -1) {
                    onLineString = "好的,为您关闭电影模式";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("浪漫模式") != -1) {
                    onLineString = "好的,为您关闭浪漫模式";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                } else if (result.indexOf("房间电视") != -1) {

                    onLineString =  "好的,为您关闭房间电视";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("客厅电视") != -1) {
                    onLineString = "好的,为您关闭客厅电视";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("冷气") != -1) {
                    onLineString = "好的,为您关闭冷气";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                } else if (result.indexOf("客厅窗帘") != -1) {
                    onLineString = "好的,为您关闭客厅窗帘";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("房间窗帘") != -1) {
                    onLineString = "好的,为您关闭房间窗帘";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("音响") != -1) {
                    onLineString = "好的,为您关闭音响";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("预热烤箱") != -1) {
                    onLineString = "好的,为您关闭预热烤箱";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("洗衣机") != -1) {
                    onLineString = "好的,为您关闭洗衣机";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);

                } else if (result.indexOf("扫地机器人") != -1) {
                    onLineString = "好的,为您关闭扫地机器人";
                    OfflineVoiceUtils.getInstance(mcontext).speak("好的,为您关闭扫地机器人", mcontext);
                } else if (result.indexOf("警报系统") != -1) {

                    onLineString = "好的,为您关闭警报系统";
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                }else {
                    onLineString = "好的,为您" + result.substring(result.indexOf("关"));
                    OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
                }


        }else  if (result.indexOf("放") != -1) {
                onLineString = "好的,为您" + result.substring(result.indexOf("放"));
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
        }else if (result.indexOf("煮") != -1) {
            onLineString = "好的,为您开启煮咖啡机";
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
            }else if (result.indexOf("家里总用电量是多少") != -1 || (result.indexOf("家里用电是多少") != -1) ||result.indexOf("家里用电是") != -1) {
            onLineString = "好的,为您确认总用电量,总用电量是120度";
                OfflineVoiceUtils.getInstance(mcontext).speak(onLineString, mcontext);
        }
            return onLineString;

    }

            /**根据翻译结果，由管家应答播报，发送16进制指令给蓝牙串口*/
    public void ttsForIntValue(int value) {

        if (value == 0x01) {

        } else if (value == 0x02) {

        } else if (value == 0x03) {

        } else if (value == 0x04) {

        } else if (value == 0x05) {

        } else if (value == 0x06) {

        } else if (value == 0x07) {

        } else if (value == 0x08) {

        } else if (value == 0x09) {

        } else if (value == 0x0A) {

        } else if (value == 0x0B) {

        } else if (value == 0x0C) {


        } else if (value == 0x0D) {

        } else if (value == 0x0E) {

        } else if (value == 0x0F) {

        } else if (value == 0x10) {

        } else if (value == 0x11) {

        } else if (value == 0x12) {

        } else if (value == 0x13) {

        } else if (value == 0x14) {

        } else if (value == 0x15) {

        } else if (value == 0x16) {

        } else if (value == 0x17) {

        } else if (value == 0x18) {

        } else if (value == 0x19) {

        } else if (value == 0x1A) {

        } else if (value == 0x1B) {

        } else if (value == 0x1C) {

        } else if (value == 0x1D) {

        } else if (value == 0x1E) {

        } else if (value == 0x1F) {

        } else if (value == 0x20) {

        } else if (value == 0x21) {

        } else if (value == 0x22) {

        } else if (value == 0x23) {

        } else if (value == 0x24) {

        } else if (value == 0x25) {

        } else if (value == 0x26) {

        } else if (value == 0x27) {

        } else if (value == 0x28) {

        } else if (value == 0x29) {

        } else if (value == 0x2A) {

        } else if (value == 0x2B) {


        } else if (value == 0x2C) {

        } else if (value == 0x2D) {

        } else if (value == 0x2E) {



        }


    }
}