package com.dan.minecraft7tv.mixins;

import com.dan.minecraft7tv.config.Config;
import com.dan.minecraft7tv.config.Position;
import com.dan.minecraft7tv.emote.EmoteRenderer;
import com.dan.minecraft7tv.interfaces.ChatHudAccess;
import com.dan.minecraft7tv.utils.I18nUtils;
import com.dan.minecraft7tv.utils.LineIdentifier;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin extends DrawableHelper implements ChatHudAccess {

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
    private Map<LineIdentifier, Style> styles = new HashMap<>();
    @Shadow
    private long lastMessageAddedTime;

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract double getChatScale();

    @Shadow
    protected abstract boolean isChatFocused();

    @Shadow
    public abstract void scroll(double amount);

    @Shadow
    public abstract boolean mouseClicked(double mouseX, double mouseY);

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
    private void getStyles(Text message, int messageId, int timestamp, boolean refresh, CallbackInfo ci) {
        Object[] lineTexts = ((TranslatableText) message).getArgs();
        for (Object word : lineTexts) {
            if (word instanceof LiteralText text) {
                if (!this.styles.containsKey(new LineIdentifier(text.getString(), message.getString()))) {
                    this.styles.put(new LineIdentifier(text.getString(), message.getString()), text.getStyle());
                }
            }
        }
    }

    private List<String> getEmotes(String line) {
        List<String> result = new ArrayList<>();
        List<String> split = Arrays.asList(line.split(" "));
        EmoteRenderer.getInstance().getNames().forEach(emoji -> {
            if (split.contains(emoji)) result.add(emoji);
        });
        return result;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void getTickDelta(MatrixStack matrices, int tickDelta, CallbackInfo ci) {
        if (!Config.getInstance().toggle) return;
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
        List<String> namesFound = getEmotes(line);
//        if (namesFound.isEmpty()) return true;
        renderEmojiLine(instance, matrices, line, namesFound, (int) x, (int) y, text, color, index);
        return false;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void reset(MatrixStack matrices, int tickDelta, CallbackInfo ci) {
        if (!Config.getInstance().toggle) return;
        if (Config.getInstance().fpsTick) {
            renderedGif.forEach(s1 -> EmoteRenderer.getInstance().tick(s1));
            renderedGif.clear();
        }
        gap = 0;
    }

    @WrapWithCondition(
            method = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"))
    private boolean fixBackGround(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        return !Config.getInstance().toggle;
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
        if (Config.getInstance().chatTextColor != -16777217) {
            color = Config.getInstance().chatTextColor;
        }
        int size = (int) Config.getInstance().emoteSize;
        int n = this.tickDelta - this.visibleMessages.get(index + this.scrolledLines).getCreationTick();
        boolean hasGif = EmoteRenderer.getInstance().isEmoji(line);
        if (hasGif) {
            gap += (size + 2) - 9;
        }
        List<String> words = new ArrayList<>();
        //separating <  > from line
        Pattern pattern = Pattern.compile("(<)(.+)(>)(.+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            for (int i = 1; i < matcher.groupCount(); i++) {
                words.add(matcher.group(i));
            }
            words.addAll(Arrays.asList(matcher.group(4).split(" ")));
        } else {
            words = Arrays.asList(line.split(" "));
        }
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
        fill(matrices, -4, z - gap, k + 4, (int) w - gap, color1);
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if (words.get(0).equals("<") && words.get(2).equals(">")) {
                if (i > 3)
                    currentX += tr.getWidth(" ");
            } else {
                if (i != 0)
                    currentX += tr.getWidth(" ");
            }
            if (emojis.contains(word)) {
                //emoji detected :eyes:
                EmoteRenderer.getInstance().render(matrices, word, currentX, initY - gap, Config.getInstance().emoteSize, Config.getInstance().emoteSize, (float) o);
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

                Style style1 = Style.EMPTY;
                boolean siblings = false;
                if(Config.getInstance().chatTextColor == -16777217) {
                    for (ChatHudLine<Text> text : this.messages) {
                        if (siblings) break;
                        TranslatableText tText = (TranslatableText) text.getText();
                        if (!tText.getString().equals(line)) continue;
                        for (Object arg : tText.getArgs()) {
                            if (arg instanceof LiteralText text1) {
                                if (text1.getString().equals(word)) {
                                    if (!text1.getSiblings().isEmpty()) {
                                        //setting last real word
                                        AtomicInteger lastWordIndex = new AtomicInteger();
                                        text1.getSiblings().forEach((literalText) -> {
                                            if (!literalText.getString().isEmpty() && !literalText.getString().equals(" ")) {
                                                lastWordIndex.set(text1.getSiblings().indexOf(literalText));
                                            }
                                        });
                                        for (int j = 0; j < text1.getSiblings().size(); j++) {
                                            LiteralText lText = (LiteralText) text1.getSiblings().get(j);
                                            if (lText.getStyle().isEmpty() && !text1.getStyle().isEmpty()) {
                                                lText.setStyle(text1.getStyle());
                                            }
                                            tr.draw(matrices, lText, currentX, y2, color);
                                            currentX += tr.getWidth(lText.getString());
                                            if (j < lastWordIndex.get()) {
                                                String space = " ";
                                                if (lText.getStyle().isBold()) {
                                                    space += " ";
                                                }
                                                currentX += tr.getWidth(space);
                                            }
                                        }
                                        siblings = true;
                                    }
                                }
                            } else if (arg instanceof TranslatableText text3) {
                                for (Object arg1 : text3.getArgs()) {
                                    if (arg1 instanceof LiteralText text2) {
                                        if (text2.getString().equals(word.replaceAll("\\[|\\]", ""))) {
                                            tr.draw(matrices, new LiteralText(word).setStyle(text3.getStyle()), currentX, y2, color);
                                            currentX += tr.getWidth(word);
                                            siblings = true;
                                            break;
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
                if (!siblings) {
                    tr.draw(matrices, new LiteralText(word).setStyle(style1), currentX, y2, color);
                    currentX += tr.getWidth(word);
                }
            }
        }
    }

    public Style getStyle(String word, String line) {
        LineIdentifier l = null;
        for (LineIdentifier e : styles.keySet()) {
            if (e.getLine().equals(line) && e.getWord().equals(word)) {
                l = e;
            }
        }
        Style st = this.styles.getOrDefault(l, Style.EMPTY);
        int a = 12313 + 123;
        return st;
    }

    @Override
    public List<String> getCurrent() {
        return this.renderedGif;
    }

    @Override
    public void clear() {
        System.out.println("cl");
        this.current.clear();
    }

    @Inject(method = "clear", at = @At("HEAD"))
    private void clearCache(boolean clearHistory, CallbackInfo ci) {
        System.out.println("cl");
        this.styles.clear();
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void clearCache1(CallbackInfo ci) {
        System.out.println("cl");
        this.styles.clear();
    }
}
