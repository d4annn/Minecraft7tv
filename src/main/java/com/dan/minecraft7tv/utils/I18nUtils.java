package com.dan.minecraft7tv.utils;

import net.minecraft.client.resource.language.I18n;

public class I18nUtils {

    public static String getString(String translateWord, Object... args) {
        return I18n.translate(translateWord, args);
    }
}
