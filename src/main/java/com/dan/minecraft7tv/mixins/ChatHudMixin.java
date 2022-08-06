package com.dan.minecraft7tv.mixins;

import com.dan.minecraft7tv.config.Config;
import com.dan.minecraft7tv.config.Position;
import com.dan.minecraft7tv.emote.EmoteRenderer;
import com.dan.minecraft7tv.interfaces.ChatHudAccess;
import com.dan.minecraft7tv.utils.I18nUtils;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin extends DrawableHelper implements ChatHudAccess {

    /*
     * This got much harder than it should be, but lets make the things how they are suposed
     * All this code is for letting others mixing render method
     */

    long prev = 0;
    @Shadow
    @Final
    private List<ChatHudLine<Text>> messages;
    @Shadow
    @Final
    private List<ChatHudLine<OrderedText>> visibleMessages;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scrolledLines;
    private int tickDelta = 0;
    private List<String> renderedGif = new ArrayList<>();
    private List<String> current = new ArrayList<>();
    private int gap = 0;

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract double getChatScale();

    @Shadow
    protected abstract boolean isChatFocused();

    @Shadow
    public abstract void scroll(double amount);

    @Inject(method = "render", at = @At("HEAD"))
    private void getTickDelta(MatrixStack matrices, int tickDelta, CallbackInfo ci) {
        if(!Config.getInstance().toggle) return;
        this.tickDelta = tickDelta;
    }

    @WrapWithCondition(
            method = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/OrderedText;FFI)I"))
    private boolean renderGif(TextRenderer instance, MatrixStack matrices, OrderedText text, float x, float y, int color) {
        if (!Config.getInstance().toggle) return true;
        int index = getIndex(text);
        if (index == -1) return true;
        String line = I18nUtils.textToString(text);
        List<String> namesFound = new ArrayList<>();
        EmoteRenderer.getInstance().getNames().forEach(emoji -> {
            if (line.contains(emoji)) namesFound.add(emoji);
        });
        renderEmojiLine(instance, matrices, line, namesFound, (int) x, (int) y, text, color, index);
        return false;
    }

    @WrapWithCondition(
            method = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"))
    private boolean fixBackRound(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        if(!Config.getInstance().toggle) return true;
        return false;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void reset(MatrixStack matrices, int tickDelta, CallbackInfo ci) {
        if(!Config.getInstance().toggle) return;
        if (Config.getInstance().fpsTick) {
            renderedGif.forEach(s1 -> EmoteRenderer.getInstance().tick(s1));
            renderedGif.clear();
        }
        gap = 0;
    }

    private int getIndex(OrderedText text) {
        for (int i = 0; i < this.visibleMessages.size(); i++) {
            if (this.visibleMessages.get(i).getText().equals(text)) {
                return i;
            }
        }
        return -1;
    }

    private List<Integer> getLinesWithEmoji() {
        List<Integer> lines = new ArrayList<>();
        for (int i = 0; i < this.messages.size(); i++) {
            int finalI = i;
            EmoteRenderer.getInstance().getNames().forEach(emoji -> {
                if (this.messages.get(finalI).getText().getString().contains(emoji)) {
                    lines.add(finalI);
                }
            });
        }
        return lines;
    }

    private void renderEmojiLine(TextRenderer tr, MatrixStack matrices, String line, List<String> emojis,
                                 int initX, int initY, OrderedText style, int color, int index) {
        if(Config.getInstance().chatTextColor != -16777217) {
            color = Config.getInstance().chatTextColor;
        }
        int size = (int) Config.getInstance().emoteSize;
        int n = this.tickDelta - this.visibleMessages.get(index + this.scrolledLines).getCreationTick();
        boolean hasGif = EmoteRenderer.getInstance().isEmoji(line);
        if (hasGif) {
            gap += (size + 2) - 9;
        }
        String[] words = line.split(" ");
        int currentX = initX;
        boolean bl = this.isChatFocused();
        double e = this.client.options.textBackgroundOpacity;
        double o = bl ? 1.0D : ChatHud.getMessageOpacityMultiplier(n);
        int q = (int) (255.0D * o * e);
        float f = (float) this.getChatScale();
        int k = MathHelper.ceil((float) this.getWidth() / f);
        double g = 9.0D * (this.client.options.chatLineSpacing + 1.0D);
        double s = (double) (-index) * g;
        int z = (int) (s - g);
        double w = hasGif ? z + size + 2 : s;
        int color1 = q << 24;
        if(Config.getInstance().chatBackgroundColor != 127 << 24) {
            color1 = Config.getInstance().chatBackgroundColor;
        }
        fill(matrices, -4, z - gap, k + 4, (int) w - gap, color1);
        for (String word : words) {
            if (!word.equals(words[0])) currentX += tr.getWidth(" ");
            if (emojis.contains(word)) {
                //emoji detected :eyes:
                EmoteRenderer.getInstance().render(matrices, word, currentX, initY - gap, Config.getInstance().emoteSize, Config.getInstance().emoteSize);
                if (!renderedGif.contains(word)) {
                    renderedGif.add(word);
                }
                currentX += size;
            } else {
                int y2 = initY - gap;
                if (Config.getInstance().textPos == Position.CENTER && hasGif) {
                    int space = size;
                    space -= tr.fontHeight;
                    y2 += space / 2;
                } else if (Config.getInstance().textPos == Position.BOTTOM && hasGif) {
                    y2 += size - tr.fontHeight;
                }
                tr.draw(matrices, word, currentX, y2, color);
                currentX += tr.getWidth(word);
            }
        }
    }

    @Override
    public List<String> getCurrent() {
        return this.renderedGif;
    }

    @Override
    public void clear() {
        this.current.clear();
    }
}
