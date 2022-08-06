package com.dan.minecraft7tv;

import com.dan.minecraft7tv.config.Config;
import com.dan.minecraft7tv.emote.Emote;
import com.dan.minecraft7tv.emote.EmoteRenderer;
import com.dan.minecraft7tv.emote.RenderableEmote;
import com.dan.minecraft7tv.gui.OptionsScreen;
import com.dan.minecraft7tv.interfaces.ChatHudAccess;
import com.dan.minecraft7tv.utils.EmoteUtils;
import com.dan.minecraft7tv.utils.FileUtils;
import com.dan.minecraft7tv.utils.GifManager;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.File;

public class Minecraft7tv implements ClientModInitializer {

    public static final KeyBinding OPEN_OPTIONS_MENU = new KeyBinding("text.minecraft7tv.open_options", InputUtil.Type.KEYSYM, -1, "Minecraft7tv");

    @Override
    public void onInitializeClient() {
        EmoteUtils.divideGif(new File(FileUtils.FOLDER.getPath() + "\\" + "drake" + "\\" + "sex"+".gif"), "asdadadsa", true);
        KeyBindingHelper.registerKeyBinding(OPEN_OPTIONS_MENU);
        MixinExtrasBootstrap.init();
        EmoteRenderer.setInstance(new EmoteRenderer());
        Emote emote = new Emote("https://cdn.7tv.app/emote/60ae958e229664e8667aea38/4x", "gigachad");
        EmoteRenderer.getInstance().addRenderableEmoji(new RenderableEmote(emote));
        Config.setInstance(new Config());
        FileUtils.checkConfig();
        Config.getInstance().loadConfig();
        registerMainCommand(ClientCommandManager.DISPATCHER);
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (OPEN_OPTIONS_MENU.wasPressed()) {
                int preButton = MinecraftClient.getInstance().options.guiScale;
                client.setScreen(new OptionsScreen(preButton));
                MinecraftClient.getInstance().options.guiScale = 4;
                MinecraftClient.getInstance().onResolutionChanged();

            }
            if (!Config.getInstance().fpsTick) {
                for (String s : ((ChatHudAccess) MinecraftClient.getInstance().inGameHud.getChatHud()).getCurrent()) {
                    try {
                        EmoteRenderer.getInstance().tick(s);
                    } catch (IndexOutOfBoundsException e) {}
                }
                ((ChatHudAccess) MinecraftClient.getInstance().inGameHud.getChatHud()).clear();
            }
        });
    }

    private void registerMainCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("listmacros")
                .executes(context -> {
                    int preButton = MinecraftClient.getInstance().options.guiScale;
                    MinecraftClient.getInstance().setScreen(new OptionsScreen(preButton));
                    MinecraftClient.getInstance().options.guiScale = 4;
                    MinecraftClient.getInstance().onResolutionChanged();

                    return 1;
                }));
    }

    //TODO: fix hover
    //TODO: add all errors handling
    //TODO: backround gifs fix
}
