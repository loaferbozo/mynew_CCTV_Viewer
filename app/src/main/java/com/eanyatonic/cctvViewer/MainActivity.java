package com.eanyatonic.cctvViewer;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.eanyatonic.cctvlib.CCTVWebViewFragment;
import com.eanyatonic.cctvlib.ChannelConfig;
import com.eanyatonic.cctvlib.ConfigManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CCTVWebViewFragment webViewFragment;
    private List<ChannelConfig> channels;
    private int currentChannelIndex = 0;
    
    // UI Elements
    private TextView overlayTextView;
    private TextView inputTextView;
    
    // Logic
    private StringBuilder digitBuffer = new StringBuilder();
    private Handler handler = new Handler();
    private Runnable hideOverlayRunnable = () -> {
        if (overlayTextView != null) overlayTextView.setVisibility(View.GONE);
    };
    private Runnable hideInputRunnable = () -> {
        if (inputTextView != null) {
            inputTextView.setVisibility(View.GONE);
            processInputChannel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overlayTextView = findViewById(R.id.overlayTextView);
        inputTextView = findViewById(R.id.inputTextView);

        // Load config
        channels = ConfigManager.loadChannels(this);
        if (channels.isEmpty()) {
            Toast.makeText(this, "No channels found!", Toast.LENGTH_LONG).show();
            return;
        }

        // Init Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        webViewFragment = (CCTVWebViewFragment) fragmentManager.findFragmentById(R.id.fragment_container);
        if (webViewFragment == null) {
            webViewFragment = new CCTVWebViewFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, webViewFragment)
                    .commit();
        }

        // Delay load first channel to ensure fragment view created
        handler.postDelayed(() -> loadChannel(currentChannelIndex), 1000);
    }

    private void loadChannel(int index) {
        if (channels == null || channels.isEmpty()) return;
        if (index < 0) index = channels.size() - 1;
        if (index >= channels.size()) index = 0;
        
        currentChannelIndex = index;
        ChannelConfig channel = channels.get(index);
        
        webViewFragment.loadUrl(channel.getUrl());
        showOverlay(channel.getName());
    }

    private void showOverlay(String text) {
        overlayTextView.setText(text);
        overlayTextView.setVisibility(View.VISIBLE);
        handler.removeCallbacks(hideOverlayRunnable);
        handler.postDelayed(hideOverlayRunnable, 4000);
    }

    private void processInputChannel() {
        if (digitBuffer.length() > 0) {
            try {
                 int inputIndex = Integer.parseInt(digitBuffer.toString()) - 1; // 1-based to 0-based
                 if (inputIndex >= 0 && inputIndex < channels.size()) {
                     loadChannel(inputIndex);
                 } else {
                     showOverlay("Channel " + (inputIndex + 1) + " not found");
                 }
            } catch (NumberFormatException e) {
                // Ignore
            }
            digitBuffer.setLength(0);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            
            // Channel switching
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_CHANNEL_UP) {
                loadChannel(currentChannelIndex + 1);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN) {
                loadChannel(currentChannelIndex - 1);
                return true;
            }
            
            // Numeric Input
            if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                digitBuffer.append(keyCode - KeyEvent.KEYCODE_0);
                inputTextView.setText(digitBuffer.toString());
                inputTextView.setVisibility(View.VISIBLE);
                handler.removeCallbacks(hideInputRunnable);
                handler.postDelayed(hideInputRunnable, 3000);
                return true;
            }
            
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (digitBuffer.length() > 0) {
                    handler.removeCallbacks(hideInputRunnable);
                    hideInputRunnable.run();
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
