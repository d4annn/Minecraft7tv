package com.dan.minecraft7tv.client.config;

import com.dan.minecraft7tv.client.utils.EmoteUtils;
import com.dan.minecraft7tv.client.utils.FileUtils;
import com.dan.minecraft7tv.common.EmoteCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static Config INSTANCE;
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int emoteSize;
    public Position textPos;
    public boolean fpsTick;
    public boolean toggle;
    public int chatTextColor;
    public boolean deleteCache;
    public boolean showDownload;
    public List<EmoteCache> emotes;
    public boolean showShadow;

    public boolean autoSync;
    public int emoteAmount;
    public boolean addServerEmotes;

    public Config() {
        setDefault();
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public static void setInstance(Config INSTANCE) {
        Config.INSTANCE = INSTANCE;
    }

    public void setDefault() {
        emoteSize = 15;
        textPos = Position.CENTER;
        fpsTick = false;
        toggle = true;
        chatTextColor = -16777217;
        deleteCache = false;
        showDownload = true;
        emotes = new ArrayList<>();
        showShadow = true;
        autoSync = true;
        emoteAmount = 50;
        addServerEmotes = true;
    }

    public void changeName(int index, String after) {
        emotes.get(index).setName(after);
    }

    public void removeEmote(int index) {
        if (emotes.size() > index)
            emotes.remove(index);
    }

    public boolean contains(EmoteCache emoteCache) {
        for(EmoteCache emote : emotes) {
            if(emote.getName().equalsIgnoreCase(emoteCache.getName()) && emote.getUrl().equalsIgnoreCase(emoteCache.getUrl())) {
                return true;
            }
        }
        return false;
    }

    public int getIndexByUrl(String url) {
        for (EmoteCache emote : emotes) {
            if (emote.getUrl().equals(url)) return emotes.indexOf(emote);
        }
        return -1;
    }

    public void saveConfig() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FileUtils.CONFIG));
            GSON.toJson(this, bw);
            bw.flush();
        } catch (IOException e) {
            EmoteUtils.logError("Error occurred while saving config, check logs for more info.", e.getMessage());
        }
    }

    public void loadConfig() {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(FileUtils.CONFIG));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Config cf = GSON.fromJson(sb.toString(), this.getClass());
            if (cf == null) {
                setDefault();
                saveConfig();
                setInstance(this);
            } else {
                setInstance(cf);
            }
        } catch (IOException e) {
            EmoteUtils.logError("Error occurred while loading config, check logs for more info.", e.getMessage());
        }
    }

}
