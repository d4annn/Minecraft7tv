package com.dan.minecraft7tv.server.mixins;

import com.dan.minecraft7tv.server.Sender;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void sendEmoteCaches(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        connection.send(Sender.getPacket(null, false, false, false));
    }

}
