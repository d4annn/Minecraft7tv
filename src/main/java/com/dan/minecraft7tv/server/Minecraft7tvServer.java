package com.dan.minecraft7tv.server;

import com.dan.minecraft7tv.common.Packets;
import com.dan.minecraft7tv.server.commands.EmoteAddCommand;
import com.dan.minecraft7tv.server.commands.EmoteEditorRoleCommand;
import com.dan.minecraft7tv.server.config.Config;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class Minecraft7tvServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        System.out.println("Rendering emotes :eyes:");
        Config.setInstance(new Config());
        Config.getInstance().loadConfig();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            EmoteEditorRoleCommand.register(dispatcher);
            EmoteAddCommand.register(dispatcher);
        });
        ServerPlayNetworking.registerGlobalReceiver(Packets.REQUEST_EMOTE_CACHE_LIST_PACKET_ID, (server, client, handler, buf, responseSender) -> {
           responseSender.sendPacket(Sender.getPacket(null, false, false, buf.readBoolean()));
        });
    }
}
