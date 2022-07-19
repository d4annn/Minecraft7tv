package com.dan.minecraf7tv;

import com.dan.minecraf7tv.emote.Emoji;
import com.dan.minecraf7tv.emote.EmojiRenderer;
import com.dan.minecraf7tv.emote.RenderableEmoji;
import com.dan.minecraf7tv.utils.EmojiUtils;
import com.dan.minecraf7tv.utils.FileUtils;
import com.dan.minecraf7tv.utils.GifManager;
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
        EmojiRenderer.setInstance(new EmojiRenderer());
        KeyBinding key = KeyBindingHelper.registerKeyBinding(new KeyBinding("Crafting panel", InputUtil.Type.KEYSYM, -1, "Crafting panel"));
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (key.wasPressed()) {
                Emoji e = new Emoji("https://cdn.7tv.app/emote/60b0d286726e10b664ec6082/4x", "goblim", false);
                EmojiRenderer.getInstance().addRenderableEmoji(new RenderableEmoji(e, 0, 0));
            }
        });
        FileUtils.checkConfig();
    }

    //TODO: save all emojis
    //TODO: add all errors handling
    //TODO: 1x1 FIX
    //TODO: backround gifs fix
}
