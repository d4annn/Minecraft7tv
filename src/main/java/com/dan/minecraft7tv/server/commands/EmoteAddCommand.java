package com.dan.minecraft7tv.server.commands;

import com.dan.minecraft7tv.common.EmoteCache;
import com.dan.minecraft7tv.server.Sender;
import com.dan.minecraft7tv.server.config.Config;
import com.dan.minecraft7tv.server.utils.MojangApi;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EmoteAddCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("7tv").
                then(literal("emotes").
                        then(literal("add").
                                then(argument("emoteName", StringArgumentType.string()).
                                        then(argument("emoteUrl", StringArgumentType.string()).
                                                executes(context -> executeAdd(context.getSource(), StringArgumentType.getString(context, "emoteName"), StringArgumentType.getString(context, "emoteUrl"))))))
                        .then(literal("remove").
                                then(argument("emoteName", StringArgumentType.string()).
                                        executes(context -> executeRemove(context.getSource(), StringArgumentType.getString(context, "emoteName"))))));
        dispatcher.register(literalArgumentBuilder);
    }

    private static int executeRemove(ServerCommandSource source, String name) {
        String playerName = source.getName();
        if (Config.getInstance().emoteEditors.contains(MojangApi.getUuidFromName(playerName))) {
            EmoteCache emote = null;
            for (EmoteCache cache : Config.getInstance().serverEmotes) {
                if (cache.getName().equals(name)) {
                    emote = cache;
                    break;
                }
            }
            if (emote != null) {
                Config.getInstance().serverEmotes.remove(emote);
                source.getServer().getPlayerManager().sendToAll(Sender.getPacket(emote, true, true, false));
                source.getServer().getPlayerManager().broadcastChatMessage(Text.of("§a" + name + " §rwas successfully removed"), MessageType.SYSTEM, UUID.randomUUID());
                return 1;
            } else {
                source.sendError(Text.of("§cEmote was not found"));
                return 0;
            }
        }
        source.sendError(Text.of("§cEmoteEditor role needed"));
        return 0;
    }

    private static int executeAdd(ServerCommandSource source, String name, String url) {
        String playerName = source.getName();
        if (Config.getInstance().emoteEditors.contains(MojangApi.getUuidFromName(playerName))) {
            for (EmoteCache cache : Config.getInstance().serverEmotes) {
                if (cache.getName().equals(name)) {
                    source.sendError(Text.of("§cDuplicated emote name"));
                    return 0;
                }
            }
            Config.getInstance().serverEmotes.add(new EmoteCache("https://cdn.7tv.app/emote/" + url + "/4x.avif", name));
            source.getServer().getPlayerManager().sendToAll(Sender.getPacket(new EmoteCache("https://cdn.7tv.app/emote/" + url + "/4x.avif", name), false, false, false));
            source.getServer().getPlayerManager().broadcastChatMessage(Text.of("§a" + name + " §rwas successfully added"), MessageType.SYSTEM, UUID.randomUUID());
            return 1;
        } else {
            source.sendError(Text.of("§cEmoteEditor role needed"));
        }
        return 0;
    }
}
