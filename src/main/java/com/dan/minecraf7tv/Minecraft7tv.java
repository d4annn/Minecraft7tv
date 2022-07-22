package com.dan.minecraf7tv;

import com.dan.minecraf7tv.emote.Emoji;
import com.dan.minecraf7tv.emote.EmojiRenderer;
import com.dan.minecraf7tv.emote.RenderableEmoji;
import com.dan.minecraf7tv.utils.EmojiUtils;
import com.dan.minecraf7tv.utils.FileUtils;
import com.dan.minecraf7tv.utils.GifManager;
import com.llamalad7.mixinextras.MixinExtrasAP;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.File;
import java.io.IOException;

public class Minecraft7tv implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MixinExtrasBootstrap.init();
        EmojiRenderer.setInstance(new EmojiRenderer());
        FileUtils.checkConfig();

        Emoji e = new Emoji("https://cdn.7tv.app/emote/60ae958e229664e8667aea38/4x", "gigachad");
        EmojiRenderer.getInstance().addRenderableEmoji(new RenderableEmoji(e));
    }

    //TODO: fix hover
    //TODO: add all errors handling
    //TODO: 1x1 FIX
    //TODO: backround gifs fix
    //TODO: height options
}
