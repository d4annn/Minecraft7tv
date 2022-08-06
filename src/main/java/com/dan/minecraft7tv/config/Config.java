package com.dan.minecraft7tv.config;

import com.dan.minecraft7tv.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.io.*;

public class Config {

    private static Config INSTANCE;
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int emoteSize;
    public Position textPos;
    public boolean fpsTick;
    public boolean toggle;
    public int chatBackgroundColor;
    public int chatTextColor;
    public boolean deleteCache;

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
        chatBackgroundColor = 127 << 24;
        chatTextColor = -16777217;
        deleteCache = false;
    }

    public void saveConfig() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FileUtils.CONFIG));
            GSON.toJson(this, bw);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
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

        }
    }

}
