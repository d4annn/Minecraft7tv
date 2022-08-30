package com.dan.minecraft7tv.server.config;

import com.dan.minecraft7tv.client.utils.FileUtils;
import com.dan.minecraft7tv.common.EmoteCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static File file = FabricLoader.getInstance().getConfigDir().resolve("Minecraft7tvServer").resolve("config.json").toFile();
    private static Config INSTANCE;
    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public List<String> emoteEditors;
    public List<EmoteCache> serverEmotes;

    public Config() {
        setDefault();
        try {
            FabricLoader.getInstance().getConfigDir().resolve("Minecraft7tvServer").toFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public static void setInstance(Config INSTANCE) {
        Config.INSTANCE = INSTANCE;
    }

    public int getIndexByStringEditor(String editor) {
        for (String uuid : this.emoteEditors) {
            if (uuid.equals(editor)) return this.emoteEditors.indexOf(uuid);
        }
        return -1;
    }

    public void setDefault() {
        this.emoteEditors = new ArrayList<>();
        this.serverEmotes = new ArrayList<>();
    }

    public void saveConfig() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            GSON.toJson(this, bw);
            bw.flush();
        } catch (IOException e) {
            System.out.println("[Minecraft7tv] Error ocurred while saving confing");
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
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
            br.close();
        } catch (IOException e) {
            System.out.println("[Minecraft7tv] Error ocurred while loading config");
            e.printStackTrace();
        }
    }
}
