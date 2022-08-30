package com.dan.minecraft7tv.server;

import com.dan.minecraft7tv.common.EmoteCache;
import com.dan.minecraft7tv.common.Packets;
import com.dan.minecraft7tv.server.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public class Sender {

    public static Packet<?> getPacket(EmoteCache emoteCache, boolean delete, boolean during, boolean isList) {
        PacketByteBuf passedData = PacketByteBufs.create();
        Gson gson = new GsonBuilder().create();
        if(emoteCache == null) {
            List<EmoteCache> emoteCaches = Config.getInstance().serverEmotes;
            passedData.writeString(gson.toJson(emoteCaches));
        } else {
            List<EmoteCache> list = new ArrayList<>();
            list.add(emoteCache);
            passedData.writeString(gson.toJson(list));
        }
        passedData.writeInt(during ? 1 : 0);
        passedData.writeBoolean(delete);
        passedData.writeLong(isList ? 1L : 0L);
        return ServerPlayNetworking.createS2CPacket(Packets.EMOTE_CACHE_LIST_PACKET_ID, passedData);
    }
}
