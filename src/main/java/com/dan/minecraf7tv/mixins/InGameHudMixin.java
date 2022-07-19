package com.dan.minecraf7tv.mixins;

import com.dan.minecraf7tv.Minecraft7tv;
import com.dan.minecraf7tv.emote.EmojiRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if(!EmojiRenderer.getInstance().getEmojis().isEmpty()) {
            EmojiRenderer.getInstance().render(matrices);
        }
    }
}
