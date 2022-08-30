package com.dan.minecraft7tv.server.commands;

import com.dan.minecraft7tv.server.config.Config;
import com.dan.minecraft7tv.server.utils.MojangApi;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EmoteEditorRoleCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("target").
                then(literal("editors").
                        then(literal("add")
                                .then(argument("player", StringArgumentType.string())
                                        .executes(context -> executeAdd(context.getSource(), StringArgumentType.getString(context, "player"))))).requires((player) -> player.hasPermissionLevel(2)).
                        then(literal("remove")
                                .then(argument("player", StringArgumentType.string())
                                        .executes(context -> executeRemove(context.getSource(), StringArgumentType.getString(context, "player"))))).requires((player) -> player.hasPermissionLevel(2)));
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder1 = literal("target").
                then(literal("editors").
                        then(literal("list").executes(context -> executeList(context.getSource()))));
        dispatcher.register(literalArgumentBuilder);
        dispatcher.register(literalArgumentBuilder1);
    }

    private static int executeRemove(ServerCommandSource source, String player) {
        Config.getInstance().emoteEditors.remove(Config.getInstance().getIndexByStringEditor(MojangApi.getUuidFromName(player)));
        Config.getInstance().saveConfig();
        return 1;
    }

    private static int executeAdd(ServerCommandSource source, String player) {
        Config.getInstance().emoteEditors.add(MojangApi.getUuidFromName(player));
        Config.getInstance().saveConfig();
        return 1;
    }

    private static int executeList(ServerCommandSource source) {
        for (String uuid : Config.getInstance().emoteEditors) {
            source.sendFeedback(Text.of(MojangApi.getNameFromUuid(uuid)), false);
        }
        return 1;
    }
}
