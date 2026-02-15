package com.eanyatonic.cctvlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;
import java.util.Objects;

public class CCTVWebViewFragment extends Fragment {

    private WebView webView;
    private FrameLayout container;
    private OnChannelLoadListener channelLoadListener;

    public interface OnChannelLoadListener {
        void onChannelInfoLoaded(String info);
    }

    public void setOnChannelLoadListener(OnChannelLoadListener listener) {
        this.channelLoadListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.container = new FrameLayout(requireContext());
        this.container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initWebView();
        return this.container;
    }

    private void initWebView() {
        // Init X5
        HashMap<String, Object> map = new HashMap<>(2);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

        webView = new WebView(requireContext());
        container.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadsImagesAutomatically(false);
        webSettings.setBlockNetworkImage(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        if (QbSdk.canLoadX5(requireContext())) {
            webView.getSettingsExtension().setPicModel(IX5WebSettingsExtension.PicModel_NoPic);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView webView, com.tencent.smtt.export.external.interfaces.SslErrorHandler handler, com.tencent.smtt.export.external.interfaces.SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                view.evaluateJavascript(
                        "function FastLoading() {" +
                                "  const fullscreenBtn = document.querySelector('#player_pagefullscreen_yes_player') || document.querySelector('.videoFull');" +
                                "  if (fullscreenBtn) return;" +
                                "  Array.from(document.getElementsByTagName('img')).forEach(img => img.remove());" +
                                "  const scriptKeywords = ['login', 'index', 'daohang', 'grey', 'jquery'];" +
                                "  Array.from(document.getElementsByTagName('script')).forEach(script => {" +
                                "      if (scriptKeywords.some(keyword => script.src.includes(keyword))) script.src = '';" +
                                "  });" +
                                "  const classNames = ['newmap', 'newtopbz', 'newtopbzTV', 'column_wrapper'];" +
                                "  classNames.forEach(className => {" +
                                "      Array.from(document.getElementsByClassName(className)).forEach(div => div.innerHTML = '');" +
                                "  });" +
                                "  setTimeout(FastLoading, 4);" +
                                "}" +
                                "FastLoading();",
                        value -> {});
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (Objects.equals(url, "about:blank")) return;

                view.evaluateJavascript(
                        "function AutoFullscreen(){" +
                                "  var fullscreenBtn = document.querySelector('#player_pagefullscreen_yes_player')||document.querySelector('.videoFull');" +
                                "  if(fullscreenBtn!=null){" +
                                "    fullscreenBtn.click();" +
                                "    document.querySelector('video').volume=1;" +
                                "  }else{" +
                                "    setTimeout(function(){ AutoFullscreen(); }, 16);" +
                                "  }" +
                                "}" +
                                "AutoFullscreen();",
                        value -> {});
                
                // Fetch info logic can be added here if needed
                if (channelLoadListener != null) {
                    channelLoadListener.onChannelInfoLoaded("Loaded: " + url);
                }
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient());
        webView.setFocusable(false);
    }

    public void loadUrl(String url) {
        if (webView != null) {
            webView.loadUrl(url);
        }
    }
    
    public void simulateTouch() {
        if (webView != null) {
             // Simulate touch logic implementation if needed, or expose webview
             webView.evaluateJavascript("document.getElementById('play_or_pause_play_player').style.display", value -> {
                 if (value.equals("\"block\"")) {
                     // Need actual touch simulation or JS click
                 }
             });
        }
    }
    
    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
