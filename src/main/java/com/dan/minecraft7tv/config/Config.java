package com.dan.minecraft7tv.config;

public class Config {

    private static Config INSTANCE;

    public double emoteSize;
    public Position textPos;

    public void setDefault() {
        emoteSize = 15;
        textPos = Position.CENTER;
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public static void setInstance(Config INSTANCE) {
        Config.INSTANCE = INSTANCE;
    }

    public Config() {
        setDefault();
    }

    public void saveConfig() {

    }

    public void loadConfig() {

    }

}
