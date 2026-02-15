package com.eanyatonic.cctvlib;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    public static List<ChannelConfig> loadChannels(Context context) {
        List<ChannelConfig> channels = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("channels.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");
                String url = obj.getString("url");
                channels.add(new ChannelConfig(name, url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channels;
    }
}
