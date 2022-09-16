package com.dan.minecraft7tv.client;

import com.dan.minecraft7tv.client.commands.RequestDataCommand;
import com.dan.minecraft7tv.client.config.Config;
import com.dan.minecraft7tv.client.emote.DownloadThread;
import com.dan.minecraft7tv.client.emote.EmoteRenderer;
import com.dan.minecraft7tv.client.emote.RenderableEmote;
import com.dan.minecraft7tv.client.gui.OptionsScreen;
import com.dan.minecraft7tv.client.gui.widget.DownloadingWidget;
import com.dan.minecraft7tv.client.interfaces.ChatHudAccess;
import com.dan.minecraft7tv.client.utils.EmoteUtils;
import com.dan.minecraft7tv.client.utils.FileUtils;
import com.dan.minecraft7tv.client.utils.datadump.DataDump;
import com.dan.minecraft7tv.common.EmoteCache;
import com.dan.minecraft7tv.common.Packets;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        //cache
        EmoteUtils.processEmoteCache(Config.getInstance().emotes, false, false);
        registerEvents();
        registerPackets();
        registerCommands();
    }

    private void registerCommands() {
        RequestDataCommand.register(ClientCommandManager.DISPATCHER);
    }

    private void registerEvents() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (OPEN_OPTIONS_MENU.wasPressed()) {
                int preButton = MinecraftClient.getInstance().options.guiScale;
                client.setScreen(new OptionsScreen());
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
            if (!Config.getInstance().showDownload) return;
            if (!EmoteRenderer.getInstance().getDownloading().isEmpty()) {
                DownloadingWidget instance = EmoteRenderer.getInstance().getDownloading().get(0);
                instance.tick();
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (!Config.getInstance().showDownload) return;
            MinecraftClient client = MinecraftClient.getInstance();
            if (!EmoteRenderer.getInstance().getDownloading().isEmpty() && (null == client.currentScreen || null != client.currentScreen && !client.currentScreen.getClass().getName().equals("com.dan.minecraft7tv.gui.OptionsScreen"))) {
                DownloadingWidget instance = EmoteRenderer.getInstance().getDownloading().get(0);
                instance.render(matrixStack);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, client) -> {
            if(!Config.getInstance().addServerEmotes) {
                for(RenderableEmote emote : EmoteRenderer.getInstance().getEmotes()) {
                   EmoteRenderer.getInstance().removeRenderableEmote(emote.getEmote().getName(), true);
                }
            }
        });
    }

    private void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(Packets.EMOTE_CACHE_LIST_PACKET_ID, (client, handler, buf, responseSender) -> {
            List<EmoteCache> emoteCaches = new GsonBuilder().create().fromJson(buf.readString(), new TypeToken<ArrayList<EmoteCache>>() {
            }.getType());
            boolean delete = buf.readBoolean();
            int during = buf.readInt();
            long bufLong = buf.readLong();
            if (bufLong == 1) {
                if (emoteCaches.size() >= 10) {
                    File file = Objects.requireNonNull(DataDump.dumpDataToFile(FileUtils.SERVER_EMOTES_FOLDER.toPath(), "server_emotes", EmoteUtils.getEmotesDump(emoteCaches).getLines())).toFile();
                    client.inGameHud.getChatHud().addMessage(Text.of("Server emote list is too long, file has been created in server_emotes folder"));
                } else {
                    for (EmoteCache emote : emoteCaches) {
                        client.inGameHud.getChatHud().addMessage(Text.of(emote.getName()));
                    }
                }
                return;
            }
            if (Config.getInstance().autoSync) {

                client.execute(() -> {
                    if (during == 0) {
                        new Thread(() -> EmoteUtils.processEmoteCache(emoteCaches, delete, true)).start();
                    } else {
                        new Thread(new DownloadThread(emoteCaches.get(0), true)).start();
                    }
                });
            }
        });
    }

    //TODO: Comprobar server emotes config
    //TODO: remove awt
}
