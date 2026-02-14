package com.eanyatonic.cctvViewer;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import com.eanyatonic.cctvViewer.FileUtils;

import android.Manifest;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

// X5内核代码
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;
import java.util.Objects;

// WebView内核代码
//import android.webkit.SslErrorHandler;
//import android.webkit.WebChromeClient;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private AudioManager audioManager;

    private WebView webView0; // 导入 WebView
    private WebView webView1; // 导入备用 WebView
    private boolean canLoadX5 = false;

    private int currentWebView = 0; // 正在使用的webView
    private boolean isChanging = false; // 是否正在换台

        private final String[] liveUrls = { "https://tv.cctv.com/live/cctv1", "https://tv.cctv.com/live/cctv2", 
            "https://tv.cctv.com/live/cctv3", "https://tv.cctv.com/live/cctv4", "https://tv.cctv.com/live/cctv5", 
            "https://tv.cctv.com/live/cctv5plus", "https://tv.cctv.com/live/cctv6", "https://tv.cctv.com/live/cctv7", 
            "https://tv.cctv.com/live/cctv8", "https://tv.cctv.com/live/cctvjilu", "https://tv.cctv.com/live/cctv10", 
            "https://tv.cctv.com/live/cctv11", "https://tv.cctv.com/live/cctv12", "https://tv.cctv.com/live/cctv13", 
            "https://tv.cctv.com/live/cctvchild", "https://tv.cctv.com/live/cctv15", "https://tv.cctv.com/live/cctv16", 
            "https://tv.cctv.com/live/cctv17", "https://tv.cctv.com/live/cctveurope", "https://tv.cctv.com/live/cctvamerica", 
            "https://www.yangshipin.cn/tv/home?pid=600002264", "https://www.yangshipin.cn/tv/home?pid=600099658", 
            "https://www.yangshipin.cn/tv/home?pid=600099655", "https://www.yangshipin.cn/tv/home?pid=600099620", 
            "https://www.yangshipin.cn/tv/home?pid=600002309", "https://www.yangshipin.cn/tv/home?pid=600002521", 
            "https://www.yangshipin.cn/tv/home?pid=600002483", "https://www.yangshipin.cn/tv/home?pid=600002520", 
            "https://www.yangshipin.cn/tv/home?pid=600002475", "https://www.yangshipin.cn/tv/home?pid=600002508", 
            "https://www.yangshipin.cn/tv/home?pid=600002485", "https://www.yangshipin.cn/tv/home?pid=600002509", 
            "https://www.yangshipin.cn/tv/home?pid=600002498", "https://www.yangshipin.cn/tv/home?pid=600002506", 
            "https://www.yangshipin.cn/tv/home?pid=600002531", "https://www.yangshipin.cn/tv/home?pid=600002481", 
            "https://www.yangshipin.cn/tv/home?pid=600002516", "https://www.yangshipin.cn/tv/home?pid=600002525", 
            "https://www.yangshipin.cn/tv/home?pid=600002484", "https://www.yangshipin.cn/tv/home?pid=600002490", 
            "https://www.yangshipin.cn/tv/home?pid=600002503", "https://www.yangshipin.cn/tv/home?pid=600002505", 
            "https://www.yangshipin.cn/tv/home?pid=600002532", "https://www.yangshipin.cn/tv/home?pid=600002493", 
            "https://www.yangshipin.cn/tv/home?pid=600002513", "https://www.gdtv.cn/tvChannelDetail/43", 
            "https://www.gdtv.cn/tvChannelDetail/44", "https://www.gdtv.cn/tvChannelDetail/45", "https://www.gdtv.cn/tvChannelDetail/48", 
            "https://www.gdtv.cn/tvChannelDetail/47", "https://www.gdtv.cn/tvChannelDetail/51", "https://www.gdtv.cn/tvChannelDetail/46", 
            "https://www.gdtv.cn/tvChannelDetail/49", "https://www.gdtv.cn/tvChannelDetail/53", "https://www.gdtv.cn/tvChannelDetail/16", 
            "https://www.gdtv.cn/tvChannelDetail/54", "https://www.gdtv.cn/tvChannelDetail/66", "https://www.gdtv.cn/tvChannelDetail/42", 
            "https://www.gdtv.cn/tvChannelDetail/15", "https://www.gdtv.cn/tvChannelDetail/13", "https://www.gdtv.cn/tvChannelDetail/74", 
            "https://www.gdtv.cn/tvChannelDetail/100", "https://www.gdtv.cn/tvChannelDetail/94", "https://www.gdtv.cn/tvChannelDetail/99", 
            "https://www.gdtv.cn/tvChannelDetail/75", "https://www.gdtv.cn/tvChannelDetail/102", "https://www.gdtv.cn/tvChannelDetail/104", 
            "https://www.sztv.com.cn/pindao/index.html?id=7867", "https://www.sztv.com.cn/pindao/index.html?id=7868", 
            "https://www.sztv.com.cn/pindao/index.html?id=7880", "https://www.sztv.com.cn/pindao/index.html?id=7874", 
            "https://www.sztv.com.cn/pindao/index.html?id=7871", "https://www.sztv.com.cn/pindao/index.html?id=7872", 
            "https://www.sztv.com.cn/pindao/index.html?id=7881", "https://www.sztv.com.cn/pindao/index.html?id=7869", 
            "https://www.sztv.com.cn/pindao/index.html?id=7878", "https://www.sztv.com.cn/pindao/index.html?id=7944", 
            "https://tv.gxtv.cn/channel/channelivePlay_e7a7ab7df9fe11e88bcfe41f13b60c62.html", "https://tv.gxtv.cn/channel/channelivePlay_f3335975f9fe11e88bcfe41f13b60c62.html", 
            "https://tv.gxtv.cn/channel/channelivePlay_fdbaf085f9fe11e88bcfe41f13b60c62.html", "https://tv.gxtv.cn/channel/channelivePlay_5e923d82058e11e9ba67e41f13b60c62.html", 
            "https://tv.gxtv.cn/channel/channelivePlay_9dfd8600075811e9ba67e41f13b60c62.html", "https://tv.gxtv.cn/channel/channelivePlay_bfa17b64157f11e999f0e41f13b60c62.html", 
            "https://tv.gxtv.cn/channel/channelivePlay_ed58bc4a207811e999f0e41f13b60c62.html", "https://tv.gxtv.cn/channel/channelivePlay_78dbfd44e6b74ab687204d2d8113cbf5.html", 
            "https://tv.gxtv.cn/channel/channelivePlay_ffa6b6e1b32b4a16a73eb3ef66f8bfc7.html", "https://tv.gxtv.cn/channel/channelivePlay_80d0ffb42c114eaf9663708629ff0a3e.html", 
            "https://tv.gxtv.cn/channel/channelivePlay_67eace939278435bb4bca90800fb4225.html", "https://www.hebtv.com/19/19js/st/xdszb/index.shtml?index=0", 
            "https://www.hebtv.com/19/19js/st/xdszb/index.shtml?index=1", "https://www.hebtv.com/19/19js/st/xdszb/index.shtml?index=2", 
            "https://www.hebtv.com/19/19js/st/xdszb/index.shtml?index=3", "https://www.hebtv.com/19/19js/st/xdszb/index.shtml?index=4", 
            "https://www.hebtv.com/19/19js/st/xdszb/index.shtml?index=5", "https://www.hebtv.com/19/19js/st/xdszb/index.shtml?index=6", 
            "https://live.mgtv.com/?channelId=280", "https://live.mgtv.com/?channelId=344", "https://live.mgtv.com/?channelId=221", 
            "https://live.mgtv.com/?channelId=346", "https://live.mgtv.com/?channelId=484", "https://live.mgtv.com/?channelId=316", 
            "https://live.mgtv.com/?channelId=261", "https://live.mgtv.com/?channelId=287", "https://live.mgtv.com/?channelId=229", 
            "https://live.mgtv.com/?channelId=329", "https://live.mgtv.com/?channelId=578", "https://live.mgtv.com/?channelId=218", 
            "https://live.mgtv.com/?channelId=269", "https://live.mgtv.com/?channelId=254", "http://tc.hnntv.cn/", 
            "https://www.hnntv.cn/live.html?playType=livePlay&channelId=5&referPage=home", "https://www.hnntv.cn/live.html?playType=livePlay&channelId=1&referPage=home", 
            "https://www.hnntv.cn/live.html?playType=livePlay&channelId=3&referPage=home", "https://www.hnntv.cn/live.html?playType=livePlay&channelId=4&referPage=home", 
            "https://www.hnntv.cn/live.html?playType=livePlay&channelId=6&referPage=home", "https://www.hnntv.cn/live.html?playType=livePlay&channelId=7&referPage=home", 
            "http://live.snrtv.com/star", "http://live.snrtv.com/1", "http://live.snrtv.com/2", "http://live.snrtv.com/3", 
            "http://www.snrtv.com/snr_dssps/a/2021/08/28/19915184.html", "http://live.snrtv.com/5", "http://live.snrtv.com/7", 
            "http://live.snrtv.com/nl", "http://live.snrtv.com/11", "https://www.nmtv.cn/liveTv#0", "https://www.nmtv.cn/liveTv#1", 
            "https://www.nmtv.cn/liveTv#2", "https://www.nmtv.cn/liveTv#3", "https://www.nmtv.cn/liveTv#4", 
            "https://www.nmtv.cn/liveTv#5", "https://www.nmtv.cn/liveTv#6", "https://www.nmtv.cn/liveTv#7", 
            "https://www.yntv.cn/live.html#0", "https://www.yntv.cn/live.html#1", "https://www.yntv.cn/live.html#2", 
            "https://www.yntv.cn/live.html#3", "https://www.yntv.cn/live.html#4", "https://www.yntv.cn/live.html#5", 
            "https://www.btzx.com.cn/special/bofang/btzxjmd/index.shtml", "https://www.sxrtv.com/tv/index.shtml#0", 
            "https://www.sxrtv.com/tv/index.shtml#1", "https://www.sxrtv.com/tv/index.shtml#2", "https://www.sxrtv.com/tv/index.shtml#3", 
            "https://www.sxrtv.com/tv/index.shtml#4", "https://www.sxrtv.com/tv/index.shtml#5" };

        private final String[] channelNames = { "1 CCTV-1 综合", "2 CCTV-2 财经", "3 CCTV-3 综艺", "4 CCTV-4 中文国际（亚）", 
            "5 CCTV-5 体育", "6 CCTV-5+ 体育赛事", "7 CCTV-6 电影", "8 CCTV-7 国防军事", "9 CCTV-8 电视剧", "10 CCTV-9 纪录", 
            "11 CCTV-10 科教", "12 CCTV-11 戏曲", "13 CCTV-12 社会与法", "14 CCTV-13 新闻", "15 CCTV-14 少儿", "16 CCTV-15 音乐", 
            "17 CCTV-16 奥林匹克", "18 CCTV-17 农业农村", "19 CCTV-4 中文国际（欧）", "20 CCTV-4 中文国际（美）", "21 CCTV4k", 
            "22 CCTV风云剧场", "23 CCTV第一剧场", "24 CCTV怀旧剧场", "25 北京卫视", "26 江苏卫视", "27 东方卫视", "28 浙江卫视", 
            "29 湖南卫视", "30 湖北卫视", "31 广东卫视", "32 广西卫视", "33 黑龙江卫视", "34 海南卫视", "35 重庆卫视", "36 深圳卫视", 
            "37 四川卫视", "38 河南卫视", "39 福建东南卫视", "40 贵州卫视", "41 江西卫视", "42 辽宁卫视", "43 安徽卫视", "44 河北卫视", 
            "45 山东卫视", "46 广东卫视", "47 广东珠江", "48 广东新闻", "49 广东民生", "50 广东体育", "51 大湾区卫视", "52 大湾区卫视（海外版）", 
            "53 经济科教", "54 广东影视", "55 4K超高清", "56 广东少儿", "57 嘉佳卡通", "58 南方购物", "59 岭南戏曲", "60 现代教育", 
            "61 广东移动", "62 荔枝台", "63 纪录片", "64 GRTN健康频道", "65 GRTN文化频道", "66 GRTN生活频道", "67 GRTN教育频道", 
            "68 深圳卫视", "69 都市频道", "70 电视剧频道", "71 公共频道", "72 财经频道", "73 娱乐生活频道", "74 少儿频道", "75 移动电视", 
            "76 宜和购物频道", "77 国际频道", "78 广西卫视", "79 综艺旅游频道", "80 都市频道", "81 影视频道", "82 新闻频道", "83 国际频道", 
            "84 乐思购频道", "85 移动数字电视频道", "86 CETV1", "87 CETV2", "88 CETV4", "89 河北卫视", "90 经济生活", "91 农民频道", 
            "92 河北都市", "93 河北影视剧", "94 少儿科教", "95 河北公共", "96 湖南经视", "97 湖南娱乐", "98 湖南电影", "99 湖南都市", 
            "100 湖南电视剧", "101 金鹰纪实", "102 湖南爱晚", "103 金鹰卡通", "104 国际频道", "105 先锋乒羽", "106 茶频道", "107 快乐垂钓", 
            "108 长沙综合", "109 长沙政法", "110 海南卫视", "111 三沙卫视", "112 海南自贸", "113 海南新闻", "114 海南公共", "115 海南文旅", 
            "116 海南少儿", "117 陕西卫视", "118 新闻资讯", "119 都市青春", "120 生活频道", "121 影视频道", "122 公共频道", "123 体育休闲", 
            "124 农林卫视", "125 移动电视", "126 内蒙古卫视", "127 蒙古语卫视", "128 新闻综合", "129 经济生活", "130 少儿频道", "131 文体娱乐", 
            "132 农牧频道", "133 蒙古语文化", "134 云南卫视", "135 云南都市", "136 云南娱乐", "137 康旅频道", "138 澜湄国际", "139 云南少儿", 
            "140 兵团卫视", "141 山西卫视", "142 黄河电视台", "143 山西经济与科技", "144 山西影视", "145 山西社会与法制", "146 山西文体生活" };

    private int currentLiveIndex;

    private static final String PREF_NAME = "MyPreferences";
    private static final String PREF_KEY_LIVE_INDEX = "currentLiveIndex";

    private boolean doubleBackToExitPressedOnce = false;

    private StringBuilder digitBuffer = new StringBuilder(); // 用于缓存按下的数字键
    private static final long DIGIT_TIMEOUT = 3000; // 超时时间（毫秒）

    private TextView inputTextView; // 用于显示正在输入的数字的 TextView

    // 初始化透明的View
    private View loadingOverlay;

    // 频道显示view
    private TextView overlayTextView;

    private String info = "";

    // 在 MainActivity 中添加一个 Handler
    private final Handler handler = new Handler();

    private boolean isMenuOverlayVisible = false;
    private boolean isDrawerOverlayVisible = false;

    private LinearLayout menuOverlay;
    private LinearLayout DrawerLayout;
    private LinearLayout DrawerLayoutDetailed;
    private LinearLayout SubMenuCCTV;
    private LinearLayout SubMenuLocal;
    private TextView CoreText;

    private int menuOverlaySelectedIndex = 0;
    private int DrawerLayoutSelectedIndex = 0;
    private int SubMenuCCTVSelectedIndex = 0;
    private int SubMenuLocalSelectedIndex = 0;

    // 可自定义设置项
    private int TEXT_SIZE = 22;
    private Boolean enableDualWebView = true;
    private Boolean enableDirectChannelChange = false;
    private Boolean enableDirectBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 加载设置
        // 获取 SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // 读取字体大小
        String selectedOption = sharedPreferences.getString("text_size", "1");
        switch (selectedOption) {
            case "0":
                TEXT_SIZE = 18;
                break;
            case "1":
                TEXT_SIZE = 22;
                break;
            case "2":
                TEXT_SIZE = 25;
                break;
            case "3":
                TEXT_SIZE = 30;
                break;

        }

        // 读取直接频道切换设置
        enableDirectChannelChange = true;

        // 读取直接返回设置
        enableDirectBack = sharedPreferences.getBoolean("direct_back", true);

        // 读取双缓冲设置
        enableDualWebView = sharedPreferences.getBoolean("dual_webview", false);

        // 读取WebView设置
        Boolean forceSysWebView = sharedPreferences.getBoolean("sys_webview", true);

        // 获取 AudioManager 实例
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // 初始化 WebView
        webView0 = findViewById(R.id.webView0);
        webView1 = findViewById(R.id.webView1);

        // 初始化显示正在输入的数字的 TextView
        inputTextView = findViewById(R.id.inputTextView);

        // 初始化 loadingOverlay
        loadingOverlay = findViewById(R.id.loadingOverlay);

        // 初始化 overlayTextView
        overlayTextView = findViewById(R.id.overlayTextView);

        // 初始化 菜单
        menuOverlay = findViewById(R.id.menuOverlay);

        // 初始化 DrawerLayout
        DrawerLayout = findViewById(R.id.DrawerLayout);

        // 初始化 DrawerLayoutDetailed
        DrawerLayoutDetailed = findViewById(R.id.DrawerLayoutDetailed);

        // 初始化 CCTV 子菜单
        SubMenuCCTV = findViewById(R.id.subMenuCCTV);

        // 初始化 Local 子菜单
        SubMenuLocal = findViewById(R.id.subMenuLocal);

        // 初始化 CoreText
        CoreText = findViewById(R.id.CoreText);

        LinearLayout DrawerLayout = findViewById(R.id.DrawerLayout);

        // 中央台频道列表
        String[] firstDrawer = {
                "央视频道", "地方频道"
        };

        // 动态生成中央台按钮和地方台按钮
        for (String channel : firstDrawer) {
            Button button = new Button(this);
            button.setText(channel);
            // 创建 LayoutParams 并设置 margin
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4, 4, 4, 4);
            // 设置按钮属性
            button.setLayoutParams(layoutParams);
            button.setPadding(16, 16, 16, 16);
            button.setTextColor(getResources().getColor(android.R.color.white));
            button.setBackground(getResources().getDrawable(R.drawable.detailed_channel_selector));
            button.setTextSize(TEXT_SIZE);
            DrawerLayout.addView(button);
        }

        // 添加设置按钮
        Button SettingButton = new Button(this);
        SettingButton.setText("打开设置");
        // 创建 LayoutParams 并设置 margin
        LinearLayout.LayoutParams layoutParams0 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams0.setMargins(4, 28, 4, 4);
        // 设置按钮属性
        SettingButton.setLayoutParams(layoutParams0);
        SettingButton.setPadding(16, 16, 16, 16);
        SettingButton.setTextColor(getResources().getColor(android.R.color.white));
        SettingButton.setBackground(getResources().getDrawable(R.drawable.detailed_channel_selector));
        SettingButton.setTextSize(TEXT_SIZE);
        DrawerLayout.addView(SettingButton);

        LinearLayout subMenuCCTV = findViewById(R.id.subMenuCCTV);
        LinearLayout subMenuLocal = findViewById(R.id.subMenuLocal);

        // 中央台频道列表
                String[] cctvChannels = {
                "CCTV-1 综合", "CCTV-2 财经", "CCTV-3 综艺", "CCTV-4 中文国际（亚）", "CCTV-5 体育", "CCTV-5+ 体育赛事", 
                "CCTV-6 电影", "CCTV-7 国防军事", "CCTV-8 电视剧", "CCTV-9 纪录", "CCTV-10 科教", "CCTV-11 戏曲", 
                "CCTV-12 社会与法", "CCTV-13 新闻", "CCTV-14 少儿", "CCTV-15 音乐", "CCTV-16 奥林匹克", 
                "CCTV-17 农业农村", "CCTV-4 中文国际（欧）", "CCTV-4 中文国际（美）", "CCTV4k", "CCTV风云剧场", "CCTV第一剧场", 
                "CCTV怀旧剧场"
        };

        // 动态生成中央台按钮
        for (String channel : cctvChannels) {
            Button button = new Button(this);
            button.setText(channel);
            // 创建 LayoutParams 并设置 margin
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4, 4, 4, 4);
            // 设置按钮属性
            button.setLayoutParams(layoutParams);
            button.setPadding(16, 16, 16, 16);
            button.setTextColor(getResources().getColor(android.R.color.white));
            button.setBackground(getResources().getDrawable(R.drawable.detailed_channel_selector));
            button.setTextSize(TEXT_SIZE);
            subMenuCCTV.addView(button);
        }

        // 地方台频道列表
                String[] localChannels = {
                "北京卫视", "江苏卫视", "东方卫视", "浙江卫视", "湖南卫视", "湖北卫视", "广东卫视", "广西卫视", "黑龙江卫视", "海南卫视", 
                "重庆卫视", "深圳卫视", "四川卫视", "河南卫视", "福建东南卫视", "贵州卫视", "江西卫视", "辽宁卫视", "安徽卫视", "河北卫视", 
                "山东卫视", "广东卫视", "广东珠江", "广东新闻", "广东民生", "广东体育", "大湾区卫视", "大湾区卫视（海外版）", "经济科教", 
                "广东影视", "4K超高清", "广东少儿", "嘉佳卡通", "南方购物", "岭南戏曲", "现代教育", "广东移动", "荔枝台", "纪录片", 
                "GRTN健康频道", "GRTN文化频道", "GRTN生活频道", "GRTN教育频道", "深圳卫视", "都市频道", "电视剧频道", "公共频道", 
                "财经频道", "娱乐生活频道", "少儿频道", "移动电视", "宜和购物频道", "国际频道", "广西卫视", "综艺旅游频道", "都市频道", "影视频道", 
                "新闻频道", "国际频道", "乐思购频道", "移动数字电视频道", "CETV1", "CETV2", "CETV4", "河北卫视", "经济生活", 
                "农民频道", "河北都市", "河北影视剧", "少儿科教", "河北公共", "湖南经视", "湖南娱乐", "湖南电影", "湖南都市", "湖南电视剧", 
                "金鹰纪实", "湖南爱晚", "金鹰卡通", "国际频道", "先锋乒羽", "茶频道", "快乐垂钓", "长沙综合", "长沙政法", "海南卫视", 
                "三沙卫视", "海南自贸", "海南新闻", "海南公共", "海南文旅", "海南少儿", "陕西卫视", "新闻资讯", "都市青春", "生活频道", 
                "影视频道", "公共频道", "体育休闲", "农林卫视", "移动电视", "内蒙古卫视", "蒙古语卫视", "新闻综合", "经济生活", "少儿频道", 
                "文体娱乐", "农牧频道", "蒙古语文化", "云南卫视", "云南都市", "云南娱乐", "康旅频道", "澜湄国际", "云南少儿", "兵团卫视", 
                "山西卫视", "黄河电视台", "山西经济与科技", "山西影视", "山西社会与法制", "山西文体生活"
        };

        // 动态生成地方台按钮
        for (String channel : localChannels) {
            Button button = new Button(this);
            button.setText(channel);
            // 创建 LayoutParams 并设置 margin
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4, 4, 4, 4);
            // 设置按钮属性
            button.setLayoutParams(layoutParams);
            button.setPadding(16, 16, 16, 16);
            button.setTextColor(getResources().getColor(android.R.color.white));
            button.setBackground(getResources().getDrawable(R.drawable.detailed_channel_selector));
            button.setTextSize(TEXT_SIZE);
            subMenuLocal.addView(button);
        }

        // https://developer.android.com/reference/android/webkit/WebView.html#getCurrentWebViewPackage()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0+
            PackageInfo pkgInfo = WebView.getCurrentWebViewPackage();
            if (pkgInfo != null) {
                CoreText.setText("当前程序运行在系统WebView上，版本号：" + pkgInfo.versionName);
            }
        }

        // X5内核代码
        if (!forceSysWebView) {
            QbSdk.unForceSysWebView();
            requestPermission();

            Log.d("versionX5",String.valueOf(QbSdk.getTbsVersion(getApplicationContext())));
            canLoadX5 = QbSdk.canLoadX5(getApplicationContext());
            Log.d("canLoadX5", String.valueOf(canLoadX5));
            if (canLoadX5) {
                CoreText.setText("当前程序运行在腾讯X5内核上");
            } else {
                Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
                startActivity(intent);
                finish(); // 销毁 MainActivity
            }
        }
        else{
            QbSdk.forceSysWebView();
        }

        HashMap<String, Object> map = new HashMap<>(2);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

        // 配置 WebView 设置
        WebSettings webSettings = webView0.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadsImagesAutomatically(false); // 禁用自动加载图片
        webSettings.setBlockNetworkImage(true); // 禁用网络图片加载
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setUserAgent(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");

        // 启用缓存
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // 启用 JavaScript 自动点击功能
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 配置 WebView 设置
        WebSettings webSettings1 = webView1.getSettings();
        webSettings1.setJavaScriptEnabled(true);
        webSettings1.setDomStorageEnabled(true);
        webSettings1.setDatabaseEnabled(true);
        webSettings1.setLoadsImagesAutomatically(false); // 禁用自动加载图片
        webSettings1.setBlockNetworkImage(true); // 禁用网络图片加载
        webSettings1.setMediaPlaybackRequiresUserGesture(false);
        webSettings1.setUserAgent(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");

        // 启用缓存
        webSettings1.setCacheMode(WebSettings.LOAD_DEFAULT);

        // 启用 JavaScript 自动点击功能
        webSettings1.setJavaScriptCanOpenWindowsAutomatically(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // X5内核代码
            webSettings.setMixedContentMode(com.tencent.smtt.sdk.WebSettings.LOAD_NORMAL);
            webSettings1.setMixedContentMode(com.tencent.smtt.sdk.WebSettings.LOAD_NORMAL);
            // 系统WebView内核代码
            // webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 设置 WebViewClient 和 WebChromeClient
        webView0.setWebViewClient(new WebViewClient() {
            // X5内核代码
            @Override
            public void onReceivedSslError(com.tencent.smtt.sdk.WebView webView,
                    com.tencent.smtt.export.external.interfaces.SslErrorHandler handler,
                    com.tencent.smtt.export.external.interfaces.SslError error) {
                handler.proceed(); // 忽略 SSL 错误
            }

            // 系统Webview内核代码
            // @Override
            // public void onReceivedSslError(WebView view, SslErrorHandler handler,
            // SslError error) {
            // handler.proceed(); // 忽略 SSL 错误
            // }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 页面加载时执行 JavaScript 脚本
                view.evaluateJavascript(
                        """
                function FastLoading() {
                             const fullscreenBtn = document.querySelector('#player_pagefullscreen_yes_player') || document.querySelector('.videoFull');
                             if (fullscreenBtn) return;

                             // Hide common structural elements
                             const tagsToHide = ['header', 'nav', 'footer', 'aside'];
                             tagsToHide.forEach(tag => {
                                 Array.from(document.getElementsByTagName(tag)).forEach(el => el.style.display = 'none');
                             });

                             // Hide common class/id patterns for headers, footers, sidebars, ads
                             const keywords = [
                                 'header', 'footer', 'nav', 'menu', 'sidebar', 'banner', 'ad', 'ads', 'guanggao', 'gg',
                                 'top', 'bottom', 'cookie', 'popup', 'app', 'download', 'login', 'register', 'search'
                             ];
                             
                             // Loop through all divs and conreal if id/class contains keywords
                             // (Targeting specific known classes from analysis)
                             const specificSelectors = [
                                 '.newmap', '.newtopbz', '.newtopbzTV', '.column_wrapper', // Existing
                                 '.top-bar', '.bottom-bar', '.fixed-bar', // Common floating bars
                                 '.qr-code', '.qrcode', // QR codes
                                 '#gxtv-header', '#gxtv-footer' // Hypothetical specific ones
                             ];

                             specificSelectors.forEach(sel => {
                                  Array.from(document.querySelectorAll(sel)).forEach(el => el.style.display = 'none');
                             });

                             // 清空所有图片的 src 属性，阻止图片加载
                             Array.from(document.getElementsByTagName('img')).forEach(img => {
                                 img.src = '';
                             });

                             // 清空特定的脚本 src 属性
                             const scriptKeywords = ['login', 'index', 'daohang', 'grey', 'jquery'];
                             Array.from(document.getElementsByTagName('script')).forEach(script => {
                                 if (scriptKeywords.some(keyword => script.src.includes(keyword))) {
                                     script.src = '';
                                 }
                             });

                             // 清空具有特定 class 的 div 内容
                             const classNames = ['newmap', 'newtopbz', 'newtopbzTV', 'column_wrapper'];
                             classNames.forEach(className => {
                                 Array.from(document.getElementsByClassName(className)).forEach(div => {
                                     div.innerHTML = '';
                                 });
                             });

                             // 递归调用 FastLoading，每 4ms 触发一次
                             setTimeout(FastLoading, 4);
                         }

                         FastLoading();

                                """,
                        value -> {
                        });
                super.onPageStarted(view, url, favicon);
            }

            // 设置 WebViewClient，监听页面加载完成事件
            @Override
            public void onPageFinished(WebView view, String url) {
                if (Objects.equals(url, "about:blank")) {
                    return;
                }
                // 清空info
                info = "";

                if (currentLiveIndex <= 23) {
                    // 获取节目预告和当前节目
                    view.evaluateJavascript("document.querySelector('#jiemu > li.cur.act').innerText", value -> {
                        // 处理获取到的元素值
                        if (!value.equals("null") && !value.isEmpty()) {
                            String elementValueNow = value.replace("\"", ""); // 去掉可能的引号
                            info += elementValueNow + "\n";
                        }
                    });
                    view.evaluateJavascript("document.querySelector('#jiemu > li:nth-child(4)').innerText", value -> {
                        // 处理获取到的元素值
                        if (!value.equals("null") && !value.isEmpty()) {
                            String elementValueNext = value.replace("\"", ""); // 去掉可能的引号
                            info += elementValueNext;
                        }
                    });
                } else if (currentLiveIndex <= 145) {
                    // 获取当前节目
                    view.evaluateJavascript(
                            "document.getElementsByClassName(\"tvSelectJiemu\")[0].innerHTML + \" \" + document.getElementsByClassName(\"tvSelectJiemu\")[1].innerHTML",
                            value -> {
                                if (!value.equals("null") && !value.isEmpty()) {
                                    String elementValueNow = value.replace("\"", ""); // 去掉可能的引号
                                    info += elementValueNow;
                                }
                            });
                }
                view.evaluateJavascript(
                        """

                                     function AutoFullscreen(){
                                         var fullscreenBtn = document.querySelector('#player_pagefullscreen_yes_player')||document.querySelector('.videoFull');
                                         if(fullscreenBtn!=null){
                                            //alert(fullscreenBtn)
                                          fullscreenBtn.click();
                                          document.querySelector('video').volume=1;
                                         }else{
                                             setTimeout(
                                                ()=>{ AutoFullscreen();}
                                            ,16);
                                         }
                                     }
                                AutoFullscreen()
                                """,
                        value -> {
                        });
                new Handler().postDelayed(() -> {
                    // // 模拟触摸
                    // if (!canLoadX5) {
                    // simulateTouch(view, 0.5f, 0.5f);
                    // }
                    // 隐藏加载的 View
                    loadingOverlay.setVisibility(View.GONE);
                    webView0.setVisibility(View.VISIBLE);
                    webView1.setVisibility(View.GONE);
                    webView1.loadUrl("about:blank");

                    isChanging = false;

                    // 显示覆盖层，传入当前频道信息
                    // showOverlay(channelNames[currentLiveIndex] + "\n" + info);
                }, 500);
            }
        });

        webView1.setWebViewClient(new WebViewClient() {
            // X5内核代码
            @Override
            public void onReceivedSslError(com.tencent.smtt.sdk.WebView webView,
                    com.tencent.smtt.export.external.interfaces.SslErrorHandler handler,
                    com.tencent.smtt.export.external.interfaces.SslError error) {
                handler.proceed(); // 忽略 SSL 错误
            }

            // 系统Webview内核代码
            // @Override
            // public void onReceivedSslError(WebView view, SslErrorHandler handler,
            // SslError error) {
            // handler.proceed(); // 忽略 SSL 错误
            // }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 页面加载时执行 JavaScript 脚本
                view.evaluateJavascript(
                        """
                                function FastLoading() {
                                             const fullscreenBtn = document.querySelector('#player_pagefullscreen_yes_player') || document.querySelector('.videoFull');
                                             if (fullscreenBtn) return;

                                             // Hide common structural elements
                                             const tagsToHide = ['header', 'nav', 'footer', 'aside'];
                                             tagsToHide.forEach(tag => {
                                                 Array.from(document.getElementsByTagName(tag)).forEach(el => el.style.display = 'none');
                                             });

                                             // Hide common class/id patterns for headers, footers, sidebars, ads
                                             const keywords = [
                                                 'header', 'footer', 'nav', 'menu', 'sidebar', 'banner', 'ad', 'ads', 'guanggao', 'gg',
                                                 'top', 'bottom', 'cookie', 'popup', 'app', 'download', 'login', 'register', 'search'
                                             ];
                                             
                                             // Loop through all divs and conreal if id/class contains keywords
                                             // (Targeting specific known classes from analysis)
                                             const specificSelectors = [
                                                 '.newmap', '.newtopbz', '.newtopbzTV', '.column_wrapper', // Existing
                                                 '.top-bar', '.bottom-bar', '.fixed-bar', // Common floating bars
                                                 '.qr-code', '.qrcode', // QR codes
                                                 '#gxtv-header', '#gxtv-footer' // Hypothetical specific ones
                                             ];

                                             specificSelectors.forEach(sel => {
                                                  Array.from(document.querySelectorAll(sel)).forEach(el => el.style.display = 'none');
                                             });

                                             // 清空所有图片的 src 属性，阻止图片加载
                                             Array.from(document.getElementsByTagName('img')).forEach(img => {
                                                 img.src = '';
                                             });

                                             // 清空特定的脚本 src 属性
                                             const scriptKeywords = ['login', 'index', 'daohang', 'grey', 'jquery'];
                                             Array.from(document.getElementsByTagName('script')).forEach(script => {
                                                 if (scriptKeywords.some(keyword => script.src.includes(keyword))) {
                                                     script.src = '';
                                                 }
                                             });

                                             // 清空具有特定 class 的 div 内容
                                             const classNames = ['newmap', 'newtopbz', 'newtopbzTV', 'column_wrapper'];
                                             classNames.forEach(className => {
                                                 Array.from(document.getElementsByClassName(className)).forEach(div => {
                                                     div.innerHTML = '';
                                                 });
                                             });

                                             // 递归调用 FastLoading，每 4ms 触发一次
                                             setTimeout(FastLoading, 4);
                                         }

                                         FastLoading();

                                """,
                        value -> {
                        });
                super.onPageStarted(view, url, favicon);
            }

            // 设置 WebViewClient，监听页面加载完成事件
            @Override
            public void onPageFinished(WebView view, String url) {
                if (Objects.equals(url, "about:blank")) {
                    return;
                }
                // 清空info
                info = "";

                if (currentLiveIndex <= 23) {
                    // 获取节目预告和当前节目
                    view.evaluateJavascript("document.querySelector('#jiemu > li.cur.act').innerText", value -> {
                        // 处理获取到的元素值
                        if (!value.equals("null") && !value.isEmpty()) {
                            String elementValueNow = value.replace("\"", ""); // 去掉可能的引号
                            info += elementValueNow + "\n";
                        }
                    });
                    view.evaluateJavascript("document.querySelector('#jiemu > li:nth-child(4)').innerText", value -> {
                        // 处理获取到的元素值
                        if (!value.equals("null") && !value.isEmpty()) {
                            String elementValueNext = value.replace("\"", ""); // 去掉可能的引号
                            info += elementValueNext;
                        }
                    });
                } else if (currentLiveIndex <= 145) {
                    // 获取当前节目
                    view.evaluateJavascript(
                            "document.getElementsByClassName(\"tvSelectJiemu\")[0].innerHTML + \" \" + document.getElementsByClassName(\"tvSelectJiemu\")[1].innerHTML",
                            value -> {
                                if (!value.equals("null") && !value.isEmpty()) {
                                    String elementValueNow = value.replace("\"", ""); // 去掉可能的引号
                                    info += elementValueNow;
                                }
                            });
                }
                view.evaluateJavascript(
                        """

                                     function AutoFullscreen(){
                                         var fullscreenBtn = document.querySelector('#player_pagefullscreen_yes_player')||document.querySelector('.videoFull');
                                         if(fullscreenBtn!=null){
                                            //alert(fullscreenBtn)
                                          fullscreenBtn.click();
                                          document.querySelector('video').volume=1;
                                         }else{
                                             setTimeout(
                                                ()=>{ AutoFullscreen();}
                                            ,16);
                                         }
                                     }
                                AutoFullscreen()
                                """,
                        value -> {
                        });
                new Handler().postDelayed(() -> {
                    // // 模拟触摸
                    // if (!canLoadX5) {
                    // simulateTouch(view, 0.5f, 0.5f);
                    // }
                    // 隐藏加载的 View
                    loadingOverlay.setVisibility(View.GONE);

                    if (enableDualWebView) {
                        webView1.setVisibility(View.VISIBLE);
                        webView0.setVisibility(View.GONE);
                        webView0.loadUrl("about:blank");
                    }

                    isChanging = false;

                    // 显示覆盖层，传入当前频道信息
                    // showOverlay(channelNames[currentLiveIndex] + "\n" + info);
                }, 1000);
            }
        });

        // 禁用缩放
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings1.setSupportZoom(false);
        webSettings1.setBuiltInZoomControls(false);
        webSettings1.setDisplayZoomControls(false);

        // 在 Android TV 上，需要禁用焦点自动导航
        webView0.setFocusable(false);
        webView1.setFocusable(false);

        // 开启无图（X5内核）
        if (canLoadX5) {
            webView0.getSettingsExtension().setPicModel(IX5WebSettingsExtension.PicModel_NoPic);
            webView1.getSettingsExtension().setPicModel(IX5WebSettingsExtension.PicModel_NoPic);
        }
        // 设置 WebView 客户端
        webView0.setWebChromeClient(new WebChromeClient());
        webView1.setWebChromeClient(new WebChromeClient());

        // 按照设置关闭双缓冲
        if (!enableDualWebView) {
            webView0.destroy();
        }

        // 加载上次保存的位置
        loadLastLiveIndex();

        // 启动定时任务，每隔一定时间执行一次
        // startPeriodicTask();

    }

    // 启动自动播放定时任务
    private void startPeriodicTask() {
        // 使用 postDelayed 方法设置定时任务
        handler.postDelayed(periodicTask, 2000); // 2000 毫秒，即 2 秒钟
    }

    // 定时任务具体操作
    private final Runnable periodicTask = new Runnable() {
        @Override
        public void run() {
            // 获取 div 元素的 display 属性，并执行相应的操作
            getDivDisplayPropertyAndDoSimulateTouch();

            // 完成后再次调度定时任务
            handler.postDelayed(this, 2000); // 2000 毫秒，即 2 秒钟
        }
    };

    // 获取 div 元素的 display 属性并执行相应的操作
    private void getDivDisplayPropertyAndDoSimulateTouch() {
        if (webView0 != null) {
            if (currentLiveIndex <= 23) {
                webView0.evaluateJavascript("document.getElementById('play_or_pause_play_player').style.display",
                        value -> {
                            // 处理获取到的 display 属性值
                            if (value.equals("\"block\"")) {
                                // 执行点击操作
                                simulateTouch(webView0, 0.5f, 0.5f);
                            }
                        });
            } else if (currentLiveIndex <= 145) {
                String scriptPlay = """
                        try{
                        if(document.querySelector('.voice.on').style.display == 'none'){
                            document.querySelector('.voice.on').click();
                        }
                        document.querySelector('.play.play1').click();
                        } catch(e) {
                        }
                        """;
                webView0.evaluateJavascript(scriptPlay, null);
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI);
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_SHOW_UI);
            } else if (menuOverlay.hasFocus()) {
                // menuOverlay具有焦点
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_B) {
                    // 按下返回键
                    showMenuOverlay();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
                        || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    // 方向键,切换五个按钮选择
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (menuOverlaySelectedIndex == 0) {
                            menuOverlaySelectedIndex = 5;
                        } else {
                            menuOverlaySelectedIndex--;
                        }
                    } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (menuOverlaySelectedIndex == 5) {
                            menuOverlaySelectedIndex = 0;
                        } else {
                            menuOverlaySelectedIndex++;
                        }
                    }
                    menuOverlay.getChildAt(menuOverlaySelectedIndex).requestFocus();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // 中间键,执行按钮操作
                    switch (menuOverlaySelectedIndex) {
                        case 0:
                            // 刷新页面
                            getCurrentWebview().reload();
                            showMenuOverlay();
                            break;
                        case 1:
                            // 播放
                            if (currentLiveIndex <= 23) {
                                simulateTouch(getCurrentWebview(), 0.5f, 0.5f);
                            } else if (currentLiveIndex <= 145) {
                                String scriptPause = """
                                        try{
                                        document.querySelector('.play.play2').click();
                                        } catch(e) {
                                        document.querySelector('.play.play1').click();
                                        }
                                        """;
                                getCurrentWebview().evaluateJavascript(scriptPause, null);
                            }
                            showMenuOverlay();
                            break;
                        case 2:
                            // 切换全屏
                            String script1 = """
                                    console.log('点击全屏按钮');
                                    document.querySelector('#player_pagefullscreen_yes_player').click();
                                    """;

                            String script2 = """
                                    console.log('点击全屏按钮');
                                    if(document.querySelector('.videoFull').id == ''){
                                        document.querySelector('.videoFull').click();
                                    }else{
                                        document.querySelector('.videoFull_ac').click();
                                    }
                                    """;

                            if (currentLiveIndex <= 23) {
                                getCurrentWebview().evaluateJavascript(script1, null);
                            } else if (currentLiveIndex <= 145) {
                                new Handler().postDelayed(() -> {
                                    getCurrentWebview().evaluateJavascript(script2, null);
                                }, 500);
                            }
                            break;
                        case 3:
                            // 放大
                            String scriptZoomIn = """
                                    // 获取当前页面的缩放比例
                                    function getZoom() {
                                      return parseFloat(document.body.style.zoom) || 1;
                                    }

                                    // 设置页面的缩放比例
                                    function setZoom(zoom) {
                                      document.body.style.zoom = zoom;
                                    }

                                    // 页面放大函数
                                    function zoomIn() {
                                      var zoom = getZoom();
                                      setZoom(zoom + 0.1);
                                    }

                                    zoomIn();
                                    """;
                            getCurrentWebview().evaluateJavascript(scriptZoomIn, null);
                            break;
                        case 4:
                            // 缩小
                            String scriptZoomOut = """
                                    // 获取当前页面的缩放比例
                                    function getZoom() {
                                      return parseFloat(document.body.style.zoom) || 1;
                                    }

                                    // 设置页面的缩放比例
                                    function setZoom(zoom) {
                                      document.body.style.zoom = zoom;
                                    }

                                    // 页面缩小函数
                                    function zoomOut() {
                                      var zoom = getZoom();
                                      if (zoom > 0.2) {
                                        setZoom(zoom - 0.1);
                                      }
                                    }

                                    zoomOut();
                                    """;
                            getCurrentWebview().evaluateJavascript(scriptZoomOut, null);
                            break;
                        case 5:
                            // 打开设置
                            Intent intent = new Intent(this, SettingsActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
                return true;
            }
            if (DrawerLayout.hasFocus() && !SubMenuCCTV.hasFocus() && !SubMenuLocal.hasFocus()
                    && !DrawerLayoutDetailed.hasFocus()) {
                // DrawerLayout具有焦点
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
                        || event.getKeyCode() == KeyEvent.KEYCODE_B) {
                    // 按下返回键
                    showChannelList();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP
                        || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    // 方向键,切换频道选择
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        if (DrawerLayoutSelectedIndex == 0) {
                            DrawerLayoutSelectedIndex = 2;
                        } else {
                            DrawerLayoutSelectedIndex--;
                        }
                    } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (DrawerLayoutSelectedIndex == 2) {
                            DrawerLayoutSelectedIndex = 0;
                        } else {
                            DrawerLayoutSelectedIndex++;
                        }
                    }
                    DrawerLayout.getChildAt(DrawerLayoutSelectedIndex).requestFocus();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    // 中间键,执行按钮操作
                    switch (DrawerLayoutSelectedIndex) {
                        case 0:
                            // 中央频道
                            DrawerLayoutDetailed.setVisibility(View.VISIBLE);
                            findViewById(R.id.subMenuCCTV).setVisibility(View.VISIBLE);
                            findViewById(R.id.CCTVScroll).setVisibility(View.VISIBLE);
                            findViewById(R.id.subMenuLocal).setVisibility(View.GONE);
                            findViewById(R.id.LocalScroll).setVisibility(View.GONE);
                            SubMenuCCTV.getChildAt(SubMenuCCTVSelectedIndex).requestFocus();
                            break;
                        case 1:
                            // 地方频道
                            DrawerLayoutDetailed.setVisibility(View.VISIBLE);
                            findViewById(R.id.subMenuCCTV).setVisibility(View.GONE);
                            findViewById(R.id.CCTVScroll).setVisibility(View.GONE);
                            findViewById(R.id.subMenuLocal).setVisibility(View.VISIBLE);
                            findViewById(R.id.LocalScroll).setVisibility(View.VISIBLE);
                            SubMenuLocal.getChildAt(SubMenuLocalSelectedIndex).requestFocus();
                            break;
                        case 2:
                            // 打开设置
                            showChannelList();
                            showMenuOverlay();
                            break;
                    }
                    return true;
                }
            } else if (SubMenuCCTV.hasFocus()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_B) {
                    // 按下返回键
                    if (enableDirectBack) {
                        showChannelList();
                        return true;
                    } else {
                        DrawerLayout.getChildAt(DrawerLayoutSelectedIndex).requestFocus();
                        SubMenuCCTV.setVisibility(View.GONE);
                        DrawerLayoutDetailed.setVisibility(View.GONE);
                        findViewById(R.id.CCTVScroll).setVisibility(View.GONE);
                        return true;
                    }
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    DrawerLayout.getChildAt(DrawerLayoutSelectedIndex).requestFocus();
                    SubMenuCCTV.setVisibility(View.GONE);
                    DrawerLayoutDetailed.setVisibility(View.GONE);
                    findViewById(R.id.CCTVScroll).setVisibility(View.GONE);
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP
                        || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    // 方向键,切换频道选择
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        if (SubMenuCCTVSelectedIndex == 0) {
                            SubMenuCCTVSelectedIndex = 23;
                        } else {
                            SubMenuCCTVSelectedIndex--;
                        }
                    } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (SubMenuCCTVSelectedIndex == 23) {
                            SubMenuCCTVSelectedIndex = 0;
                        } else {
                            SubMenuCCTVSelectedIndex++;
                        }
                    }
                    SubMenuCCTV.getChildAt(SubMenuCCTVSelectedIndex).requestFocus();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    // 中间键,执行按钮操作
                    currentLiveIndex = SubMenuCCTVSelectedIndex;
                    loadLiveUrl();
                    saveCurrentLiveIndex();
                    showChannelList();
                    return true;
                }
            } else if (SubMenuLocal.hasFocus()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_B) {
                    if (enableDirectBack) {
                        showChannelList();
                        return true;
                    } else {
                        // 按下返回键
                        DrawerLayout.getChildAt(DrawerLayoutSelectedIndex).requestFocus();
                        SubMenuLocal.setVisibility(View.GONE);
                        DrawerLayoutDetailed.setVisibility(View.GONE);
                        findViewById(R.id.LocalScroll).setVisibility(View.GONE);
                        return true;
                    }
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    DrawerLayout.getChildAt(DrawerLayoutSelectedIndex).requestFocus();
                    SubMenuLocal.setVisibility(View.GONE);
                    DrawerLayoutDetailed.setVisibility(View.GONE);
                    findViewById(R.id.LocalScroll).setVisibility(View.GONE);
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP
                        || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    // 方向键,切换频道选择
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                        if (SubMenuLocalSelectedIndex == 0) {
                            SubMenuLocalSelectedIndex = 121;
                        } else {
                            SubMenuLocalSelectedIndex--;
                        }
                    } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (SubMenuLocalSelectedIndex == 121) {
                            SubMenuLocalSelectedIndex = 0;
                        } else {
                            SubMenuLocalSelectedIndex++;
                        }
                    }
                    SubMenuLocal.getChildAt(SubMenuLocalSelectedIndex).requestFocus();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    // 中间键,执行按钮操作
                    currentLiveIndex = SubMenuLocalSelectedIndex + 24;
                    loadLiveUrl();
                    saveCurrentLiveIndex();
                    showChannelList();
                    return true;
                }
            } else if (DrawerLayoutDetailed.hasFocus()) {
                return true;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && !isChanging) {
                // 执行上一个直播地址的操作
                navigateToPreviousLive();
                return true; // 返回 true 表示事件已处理，不传递给 WebView
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && !isChanging) {
                // 执行下一个直播地址的操作
                navigateToNextLive();
                return true; // 返回 true 表示事件已处理，不传递给 WebView
            } else if ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) && !isChanging) {
                // 换台菜单
                showChannelList();
                // 显示节目列表
                showOverlay(channelNames[currentLiveIndex] + "\n" + info);
                return true; // 返回 true 表示事件已处理，不传递给 WebView
            } else if ((event.getKeyCode() == KeyEvent.KEYCODE_MENU || event.getKeyCode() == KeyEvent.KEYCODE_M)
                    && !isChanging) {
                // 显示菜单
                showMenuOverlay();

                return true; // 返回 true 表示事件已处理，不传递给 WebView
            } else if ((event.getKeyCode() >= KeyEvent.KEYCODE_0 && event.getKeyCode() <= KeyEvent.KEYCODE_9)
                    && !isChanging) {
                int numericKey = event.getKeyCode() - KeyEvent.KEYCODE_0;

                // 将按下的数字键追加到缓冲区
                digitBuffer.append(numericKey);

                // 使用 Handler 来在超时后处理输入的数字
                new Handler().postDelayed(() -> handleNumericInput(), DIGIT_TIMEOUT);

                // 更新显示正在输入的数字的 TextView
                updateInputTextView();

                return true; // 事件已处理，不传递给 WebView
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_B) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    System.exit(0);
                    return true;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "再按一次返回键退出应用", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                // 如果两秒内再次按返回键，则退出应用
            }
        }
        return true; // 不处理其余事件
    }

    // 显示底部菜单
    private void showMenuOverlay() {
        if (!isMenuOverlayVisible) {
            menuOverlay.getChildAt(menuOverlaySelectedIndex).requestFocus();
            menuOverlay.setVisibility(View.VISIBLE);
            isMenuOverlayVisible = true;
        } else {
            menuOverlay.setVisibility(View.GONE);
            menuOverlaySelectedIndex = 0;
            isMenuOverlayVisible = false;
        }
    }

    // 频道选择列表
    private void showChannelList() {
        // 显示频道抽屉
        if (!isDrawerOverlayVisible) {
            DrawerLayoutDetailed.setVisibility(View.VISIBLE);
            DrawerLayout.setVisibility(View.VISIBLE);
            isDrawerOverlayVisible = true;
            if (currentLiveIndex < 24) {
                SubMenuCCTV.setVisibility(View.VISIBLE);
                findViewById(R.id.CCTVScroll).setVisibility(View.VISIBLE);
                SubMenuCCTV.getChildAt(currentLiveIndex).requestFocus();
                SubMenuCCTVSelectedIndex = currentLiveIndex;
                DrawerLayoutSelectedIndex = 0;
            } else {
                SubMenuLocal.setVisibility(View.VISIBLE);
                findViewById(R.id.LocalScroll).setVisibility(View.VISIBLE);
                SubMenuLocal.getChildAt(currentLiveIndex - 24).requestFocus();
                SubMenuLocalSelectedIndex = currentLiveIndex - 24;
                DrawerLayoutSelectedIndex = 1;
            }
        } else {
            DrawerLayout.setVisibility(View.GONE);
            SubMenuCCTV.setVisibility(View.GONE);
            SubMenuLocal.setVisibility(View.GONE);
            findViewById(R.id.LocalScroll).setVisibility(View.GONE);
            findViewById(R.id.CCTVScroll).setVisibility(View.GONE);
            DrawerLayoutDetailed.setVisibility(View.GONE);
            isDrawerOverlayVisible = false;
        }
    }

    private void showChannelListDPAD(int selectIndex) {
        // 显示频道抽屉
        if (!isDrawerOverlayVisible) {
            DrawerLayoutDetailed.setVisibility(View.VISIBLE);
            DrawerLayout.setVisibility(View.VISIBLE);
            isDrawerOverlayVisible = true;
            if (selectIndex < 24) {
                SubMenuCCTV.setVisibility(View.VISIBLE);
                findViewById(R.id.CCTVScroll).setVisibility(View.VISIBLE);
                SubMenuCCTV.getChildAt(selectIndex).requestFocus();
                SubMenuCCTVSelectedIndex = selectIndex;
                DrawerLayoutSelectedIndex = 0;
            } else {
                SubMenuLocal.setVisibility(View.VISIBLE);
                findViewById(R.id.LocalScroll).setVisibility(View.VISIBLE);
                SubMenuLocal.getChildAt(selectIndex - 24).requestFocus();
                SubMenuLocalSelectedIndex = selectIndex - 24;
                DrawerLayoutSelectedIndex = 1;
            }
        } else {
            DrawerLayout.setVisibility(View.GONE);
            SubMenuCCTV.setVisibility(View.GONE);
            SubMenuLocal.setVisibility(View.GONE);
            findViewById(R.id.LocalScroll).setVisibility(View.GONE);
            findViewById(R.id.CCTVScroll).setVisibility(View.GONE);
            DrawerLayoutDetailed.setVisibility(View.GONE);
            isDrawerOverlayVisible = false;
        }
    }

    private void handleNumericInput() {
        // 将缓冲区中的数字转换为整数
        if (digitBuffer.length() > 0) {
            int numericValue = Integer.parseInt(digitBuffer.toString());

            // 检查数字是否在有效范围内
            if (numericValue > 0 && numericValue <= liveUrls.length) {
                currentLiveIndex = numericValue - 1;
                loadLiveUrl();
                saveCurrentLiveIndex(); // 保存当前位置
            }

            // 重置缓冲区
            digitBuffer.setLength(0);

            // 取消显示正在输入的数字
            inputTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateInputTextView() {
        // 在 TextView 中显示当前正在输入的数字
        inputTextView.setVisibility(View.VISIBLE);
        inputTextView.setText("换台：" + digitBuffer.toString());
    }

    private void loadLastLiveIndex() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentLiveIndex = preferences.getInt(PREF_KEY_LIVE_INDEX, 0); // 默认值为0
        loadLiveUrl(); // 加载上次保存的位置的直播地址
    }

    private void saveCurrentLiveIndex() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_KEY_LIVE_INDEX, currentLiveIndex);
        editor.apply();
    }

    private WebView getCurrentWebview() {
        if (!enableDualWebView) {
            return webView1;
        }
        if (currentWebView == 0) {
            return webView0;
        } else {
            return webView1;
        }
    }

    private void loadLiveUrl() {
        if (currentLiveIndex >= 0 && currentLiveIndex < liveUrls.length) {
            // 显示加载的View
            // loadingOverlay.setVisibility(View.VISIBLE);

            isChanging = true;

            if (currentWebView == 0) {
                currentWebView = 1;
            } else {
                currentWebView = 0;
            }

            getCurrentWebview().setInitialScale(getMinimumScale());
            getCurrentWebview().loadUrl(liveUrls[currentLiveIndex]);
            if (currentLiveIndex > 23) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getCurrentWebview() != null) {
                            getCurrentWebview().setInitialScale(getMinimumScale());
                            getCurrentWebview().reload();
                        }
                    }
                }, 1000);
            }
        }
    }

    private void navigateToPreviousLive() {
        if (enableDirectChannelChange) {
            currentLiveIndex = (currentLiveIndex - 1 + liveUrls.length) % liveUrls.length;
            loadLiveUrl();
            saveCurrentLiveIndex(); // 保存当前位置
        } else {
            showChannelListDPAD((currentLiveIndex - 1 + liveUrls.length) % liveUrls.length);
        }
    }

    private void navigateToNextLive() {
        if (enableDirectChannelChange) {
            currentLiveIndex = (currentLiveIndex + 1 + liveUrls.length) % liveUrls.length;
            loadLiveUrl();
            saveCurrentLiveIndex(); // 保存当前位置
        } else {
            showChannelListDPAD((currentLiveIndex + 1 + liveUrls.length) % liveUrls.length);
        }
    }

    private int getMinimumScale() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // 计算缩放比例，使用 double 类型进行计算
        double scale = Math.min((double) screenWidth / 1920.0, (double) screenHeight / 1080.0) * 100;

        Log.d("scale", "scale: " + scale);
        // 四舍五入并转为整数
        return (int) Math.round(scale);
    }

    // 在需要模拟触摸的地方调用该方法
    public void simulateTouch(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;

        // 构造 ACTION_DOWN 事件
        MotionEvent downEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);
        view.dispatchTouchEvent(downEvent);

        // 构造 ACTION_UP 事件
        MotionEvent upEvent = MotionEvent.obtain(downTime, eventTime + 100, MotionEvent.ACTION_UP, x, y, 0);
        view.dispatchTouchEvent(upEvent);

        // 释放事件对象
        downEvent.recycle();
        upEvent.recycle();
    }

    private void showOverlay(String channelInfo) {
        // 设置覆盖层内容
        overlayTextView.setText(channelInfo);

        findViewById(R.id.overlayTextView).setVisibility(View.VISIBLE);

        // 使用 Handler 延时隐藏覆盖层
        new Handler().postDelayed(() -> {
            findViewById(R.id.overlayTextView).setVisibility(View.GONE);
        }, 8000);
    }

    @Override
    protected void onDestroy() {
        // 在销毁活动时，释放 WebView 资源
        if (webView0 != null) {
            webView0.destroy();
        }
        if (webView1 != null) {
            webView1.destroy();
        }
        super.onDestroy();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
}
