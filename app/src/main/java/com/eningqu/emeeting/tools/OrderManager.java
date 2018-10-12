package com.eningqu.emeeting.tools;

public class OrderManager {
                                               //指令                                   管家回应：                            房间动作：
    private final static int  openLight = 0x01;//开房间灯                            好的，為您開房間燈                          房間燈全開
    private final static int  cloaseLight = 0x02;//关房间灯                          好的，為您關房間燈                          房間燈全關
    private final static int  openbashRoomLight = 0x03;//开浴厕灯                    好的，為您開浴廁燈                          浴廁燈開啟
    private final static int  closebashRoomLight = 0x04;//关浴厕灯                   好的，為您關浴廁燈                          浴廁燈關閉
    private final static int  openLivingRoomLight = 0x05;//开客厅灯                  好的，為您開客廳燈                          客廳燈開啟
    private final static int  closeLivingRoomLight = 0x06;//关客厅灯                 好的，為您關客廳燈                          客廳燈關閉
    private final static int  openBedsideLight = 0x07;//开床灯灯                     好的，為您開床頭燈                          床頭燈開啟
    private final static int  closeBedsideLight = 0x08;//关床头灯                     好的，為您關床頭燈                          床頭燈關閉
    private final static int  openFilmMode = 0x09;//电影模式：                        好的，為您開啟電影模式                       灯光调暗，搭配音乐，增加观赏电影氛围
    private final static int  openRomanticMode = 0x0A;//浪漫模式：                    好的，為您開啟浪漫模式                       燈光調節成柔光模式，適合放鬆舒適狀態
    private final static int  openHotWater = 0x0B;//放熱水                            好的，為您放熱水                            開啟浴室浴缸的熱水開關
    private final static int  closeHotWater = 0x0C;//關熱水                           好的，為您關熱水                            關閉浴室浴缸的熱水開關
    private final static int  setUpOpenHotWater = 0x0D;//設定XX點放熱水                好的，為您XX點放熱水                        XX點開啟浴室浴缸的熱水開
    private final static int  setUpCloseHotWater = 0x0F;//設定XX點關熱水               好的，為您XX點關熱水                        XX點關閉浴室浴缸的熱水開關
    private final static int  openTV = 0x10;//開房間電視                               好的，為您開房間電視                         房間電視開啟
    private final static int  closeTV = 0x11;//關房間電視                              好的，為您關房間電視                         房間電視關閉
    private final static int  setUpCloseTV = 0x12;//設定XX點關房間電視                  好的，為您設定XX點關房間電視                 XX點房間電視關閉
    private final static int  openLivingRoomTV = 0x13;//開客廳電視                     好的，為您開客廳電視                         客廳電視開啟
    private final static int  closeLivingRoomTV = 0x14;//關客廳電視                    好的，為您關客廳電視                         客廳電視關閉
    private final static int  openAirCondition= 0x15;//開冷氣                         好的，為您開冷氣                            空調開啟
    private final static int  closeAirCondition = 0x16;//關冷氣                       好的，為您關冷氣                            空調關閉
    private final static int  setUpCloseAirCondition = 0x17;//設定XX點開冷氣           好的，為您設定XX點開冷氣                     XX點冷氣啟動(預冷)
    private final static int  CookCoffee = 0x18;//煮咖啡                              好的，為您開啟煮咖啡機                       咖啡機電源啟動
    private final static int  openLivingRoomCurtain = 0x19;//打開客廳窗簾              好的，為您開啟客廳窗簾                       客廳窗簾打開
    private final static int  CloseLivingRoomCurtain = 0x1A;//關閉客廳窗簾             好的，為您關上客廳窗簾                       客廳窗簾關閉
    private final static int  openRoomCurtain = 0x1B;//打開房間窗簾                    好的，為您打開房間窗簾                       房間窗簾打開
    private final static int  closeRoomCurtain = 0x1C;//關閉房間窗簾                   好的，為您關閉房間窗簾                       房間窗簾關閉
    private final static int  openSound = 0x1D;//打開音響                              好的，為您打開音響                          音響開啟
    private final static int  setAlarmClock = 0x1E;//設鬧鐘每天早上7                    好的，為您設定鬧鐘每天早上7                  每天上午7點播放鬧鈴聲
    private final static int  openWashingMachine = 0x1F;//啟動洗衣機                    點好的，為您啟動洗衣機                      洗衣機啟動標準洗衣模式(需預設模式)
    private final static int  setUpOpenWashingMachine = 0x20;//設定XX點啟動洗衣機       好的，為您 設定XX點啟動洗衣機                XX點洗衣機啟動
    private final static int  openSweepingRobot = 0x21;//啟動掃地機器人                 好的， 為您啟動掃地機器人                    掃地機器人啟動
    private final static int  setUpOpenSweepingRobot = 0x22;//預約XX點啟動掃地機器人     好的， 為您預約XX點啟動掃地機器人             預約XX點掃地機器人啟動
    private final static int  openPreheatingOven = 0x23;//開啟預熱烤箱                  好的，為您開啟預熱烤箱                       烤箱預熱20分鐘啟動
    private final static int  whatElectricity = 0x24;//家裡總用電量是多少                好的，為您確認總用電量，總用電量是XX           管家透過後臺系統計告知總用電量
    private final static int  setUpCloseElectricity = 0x25;//設定X分鐘後關閉所有用電      好的，為您設定X分鐘後關閉所有用電              X分鐘後，屋內所有電源關閉 ( 不包含冰箱 )
    private final static int  setUpOpenAlarmSystem = 0x26;//設定X分鐘後啟動警報提醒系統    好的，為您設定X分鐘後啟動警報提醒系統          X分鐘後，啟動屋內警報，若有外人入侵或房內有災害發生，連結主人手機，並通知社區櫃台。
    private final static int  todayWeather = 0x27;//今天天氣                            今天天氣晴朗，溫度XX度，紫外線量過高空氣品質差   管家透過後臺雲端告知當地本日天氣
    private final static int  taiBeiWeather = 0x28;//台北天氣                           台北今天天氣晴朗，溫度XX度，紫外線量過高，      空氣品質  管家透過後臺雲端告知台北本日天氣
    private final static int  ReservationKTV = 0x29;//預約KTV室明天下午三點              好的，為您 預約KTV室明天下午三點               管家將指令傳送到櫃檯系統預約明天下午三點 KTV室
    private final static int  checkDoctorTime = 0x2A;//本周駐診醫生及時間                本週駐診醫生為XXX，時間從上午9點到中午12點      管家透過後臺系統告知社區醫生駐診時間
    private final static int  checkCommunityActivities = 0x2B;//查詢本週社區活動         好的，本週社區週三X時有XX活動，地點XX教室       管家透過後臺系統告知社區安排的活動項目與時間地點
    private final static int  CallCounter = 0x2C;//呼叫櫃台                             好的，為您呼叫櫃台                           訊號傳到櫃台，人員回電給房客
    private final static int  cleanUp = 0x2D;//請打掃                                   好的，為您預約打掃                           訊號回傳櫃台，通知哪間房等待打掃
    private final static int  DoNotDisturb = 0x2E;//勿打擾                              好的，已啟動勿擾                             啟動門口的勿擾指示燈


}
