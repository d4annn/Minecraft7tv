package com.dan.minecraft7tv.client.commands;

import com.dan.minecraft7tv.common.Packets;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public class RequestDataCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("client7tv")
                .then(ClientCommandManager.literal("sync")
                        .executes(context -> executeSync()))
                .then(ClientCommandManager.literal("serverEmotes")
                        .executes(context -> executeServerEmotes())));
    }

    private static int executeServerEmotes() {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeBoolean(true);
        ClientPlayNetworking.send(Packets.REQUEST_EMOTE_CACHE_LIST_PACKET_ID, data);
        return 1;
    }

    private static int executeSync() {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeBoolean(false);
        ClientPlayNetworking.send(Packets.REQUEST_EMOTE_CACHE_LIST_PACKET_ID, data);
        return 1;
    }
}
