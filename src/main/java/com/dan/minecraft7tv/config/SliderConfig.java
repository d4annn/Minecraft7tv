package com.dan.minecraft7tv.config;

import com.dan.minecraft7tv.utils.I18nUtils;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.text.LiteralText;

public class SliderConfig {

    public static final DoubleOption EMOTE_SIZE = new DoubleOption("text.minecraft7tv.emote_size", 8.0D, 40.0D, 1.0f,
            (gameOptions) -> (double) Config.getInstance().emoteSize,
            (gameOptions, aDouble) -> {
                Config.getInstance().emoteSize = aDouble;
                Config.getInstance().saveConfig();
            },
            ((gameOptions, doubleOption) -> new LiteralText(I18nUtils.getString("text.minecraft7tv.emote_size", new Object[0]) + (int) Config.getInstance().emoteSize)));
}
