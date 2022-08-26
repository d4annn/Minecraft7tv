package com.dan.minecraft7tv.client;

import com.dan.minecraft7tv.client.config.Config;
import com.dan.minecraft7tv.common.EmoteCache;
import com.dan.minecraft7tv.client.emote.Emote;
import com.dan.minecraft7tv.client.emote.EmoteRenderer;
import com.dan.minecraft7tv.client.emote.RenderableEmote;
import com.dan.minecraft7tv.client.gui.OptionsScreen;
import com.dan.minecraft7tv.client.gui.widget.DownloadingWidget;
import com.dan.minecraft7tv.client.interfaces.ChatHudAccess;
import com.dan.minecraft7tv.client.utils.FileUtils;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class Minecraft7tvClient implements ClientModInitializer {

    public static final KeyBinding OPEN_OPTIONS_MENU = new KeyBinding("text.minecraft7tv.open_options", InputUtil.Type.KEYSYM, -1, "Minecraft7tv");

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(OPEN_OPTIONS_MENU);
        MixinExtrasBootstrap.init();
        //create converter
        FileUtils.initConverter();
        EmoteRenderer.setInstance(new EmoteRenderer());
        Config.setInstance(new Config());
        FileUtils.checkConfig();
        Config.getInstance().loadConfig();
        registerEvents();
        //cache
        for (EmoteCache emote1 : Config.getInstance().emotes) {
            if (emote1.getUrl() == null || emote1.getName() == null) {
                throw new IllegalStateException();
            }
            Emote emote = new Emote(emote1.getUrl(), emote1.getName());
            if (null == emote.getBuffer() && emote.isGif()) {
                return;
            }
            EmoteRenderer.getInstance().addRenderableEmote(new RenderableEmote(emote));
        }
    }

    private void registerEvents() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (OPEN_OPTIONS_MENU.wasPressed()) {
                int preButton = MinecraftClient.getInstance().options.guiScale;
                client.setScreen(new OptionsScreen(preButton));
                MinecraftClient.getInstance().options.guiScale = 4;
                MinecraftClient.getInstance().onResolutionChanged();
            }
            if (!Config.getInstance().fpsTick) {
                if (null != client.currentScreen && client.currentScreen.getClass().getName().equals("com.dan.minecraft7tv.gui.OptionsScreen")) {
                    return;
                }
                for (String s : ((ChatHudAccess) MinecraftClient.getInstance().inGameHud.getChatHud()).getCurrent()) {
                    try {
                        EmoteRenderer.getInstance().tick(s);
                    } catch (IndexOutOfBoundsException e) {
                    }
                    ((ChatHudAccess) MinecraftClient.getInstance().inGameHud.getChatHud()).clear();
                }
            }
            if(!Config.getInstance().showDownload) return;
            if (!EmoteRenderer.getInstance().getDownloading().isEmpty()) {
                DownloadingWidget instance = EmoteRenderer.getInstance().getDownloading().get(0);
                instance.tick();
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if(!Config.getInstance().showDownload) return;
            MinecraftClient client = MinecraftClient.getInstance();
            if (!EmoteRenderer.getInstance().getDownloading().isEmpty() && (null == client.currentScreen || null != client.currentScreen && !client.currentScreen.getClass().getName().equals("com.dan.minecraft7tv.gui.OptionsScreen"))) {
                DownloadingWidget instance = EmoteRenderer.getInstance().getDownloading().get(0);
                instance.render(matrixStack);
            }
        });
    }

    //TODO: menu scroll
}
