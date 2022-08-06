package com.dan.minecraft7tv.utils;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.OrderedText;

public class I18nUtils {

    public static String getString(String translateWord, Object... args) {
        return I18n.translate(translateWord, args);
    }

    public static String textToString(OrderedText text) {
        var builder = new StringBuilder();
        text.accept(((index, style, codePoint) -> {
            builder.append((char) codePoint);
            return true;
        }));
        return builder.toString();
    }
}
