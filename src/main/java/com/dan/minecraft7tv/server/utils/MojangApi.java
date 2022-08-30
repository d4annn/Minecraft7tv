package com.dan.minecraft7tv.server.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MojangApi {

    private static final String NAME_TO_UUID_LINK = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String UUID_TO_NAME_LINK = "https://api.mojang.com/user/profile/";
    private static final Gson GSON = new Gson();

    private static Map<String, String> nameToUuidCache;
    private static Map<String, String> uuidToNameCache;
    private static Set<String> invalidNames;
    private static Set<String> invalidUuids;

    public static String getUuidFromName(String name) {
        if (invalidNames != null && invalidNames.contains(name)) {
            return null;
        }
        String uuid = null;
        if (nameToUuidCache != null && (uuid = nameToUuidCache.get(name)) != null) {
            return uuid;
        }
        try {
            URL url = new URL(NAME_TO_UUID_LINK + name);
            String response = new BufferedReader(new InputStreamReader(url.openStream())).readLine();
            JsonObject object = GSON.fromJson(response, JsonObject.class);
            if (object == null || object.has("error")) {
                if (invalidNames == null) {
                    invalidNames = new HashSet<>();
                }
                invalidNames.add(name);
                return null;
            }
            uuid = object.get("id").getAsString();
            if (nameToUuidCache == null) {
                nameToUuidCache = new HashMap<>();
            }
            if (uuidToNameCache == null) {
                uuidToNameCache = new HashMap<>();
            }
            nameToUuidCache.put(name, uuid);
            uuidToNameCache.put(uuid, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uuid;
    }


    public static String getNameFromUuid(String uuid) {
        if (invalidUuids != null && invalidUuids.contains(uuid)) {
            return null;
        }
        String name = null;
        if (uuidToNameCache != null && (name = uuidToNameCache.get(uuid)) != null) {
            return name;
        }
        try {
            URL url = new URL(UUID_TO_NAME_LINK + uuid);
            String response = new BufferedReader(new InputStreamReader(url.openStream())).readLine();
            JsonObject object = GSON.fromJson(response, JsonObject.class);
            if (object == null || object.has("error")) {
                if (invalidUuids == null) {
                    invalidUuids = new HashSet<>();
                }
                invalidUuids.add(uuid);
                return null;
            }
            name = object.get("name").getAsString();
            if (nameToUuidCache == null) {
                nameToUuidCache = new HashMap<>();
            }
            if (uuidToNameCache == null) {
                uuidToNameCache = new HashMap<>();
            }
            nameToUuidCache.put(name, uuid);
            uuidToNameCache.put(uuid, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }
}
