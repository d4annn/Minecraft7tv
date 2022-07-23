package com.dan.minecraft7tv;

import com.dan.minecraft7tv.config.Config;
import com.dan.minecraft7tv.emote.Emoji;
import com.dan.minecraft7tv.emote.EmojiRenderer;
import com.dan.minecraft7tv.emote.RenderableEmoji;
import com.dan.minecraft7tv.gui.OptionsScreen;
import com.dan.minecraft7tv.utils.EmojiUtils;
import com.dan.minecraft7tv.utils.FileUtils;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class Minecraft7tv implements ClientModInitializer {

    public static final KeyBinding OPEN_OPTIONS_MENU = new KeyBinding("text.minecraft7tv.open_options", InputUtil.Type.KEYSYM, -1, "Minecraft7tv");

    @Override
    public void onInitializeClient() {
        EmojiUtils.loadGif("https://cdn.7tv.app/emote/60e6ff484af5311ddcadae45/4x", "peeposhy");
        KeyBindingHelper.registerKeyBinding(OPEN_OPTIONS_MENU);
        MixinExtrasBootstrap.init();
        EmojiRenderer.setInstance(new EmojiRenderer());
        Config.setInstance(new Config());
        FileUtils.checkConfig();
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("minecraft7tv").executes(context -> {
            MinecraftClient.getInstance().setScreen(new OptionsScreen(null));
            return 1;
        }));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (OPEN_OPTIONS_MENU.wasPressed()) {
                client.setScreen(new OptionsScreen(null));
            }
        });
    }

    //TODO: fix hover
    //TODO: add all errors handling
    //TODO: 1x1 FIX
    //TODO: backround gifs fix
    //TODO: height options
}
