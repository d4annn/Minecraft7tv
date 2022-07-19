package com.dan.minecraf7tv.emote;

import com.dan.minecraf7tv.utils.EmojiUtils;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

public class RenderableEmoji {

    private Emoji emoji;
    private NativeImage image;
    private int x;
    private int y;
    private Frame frame;

    public RenderableEmoji(Emoji emoji, int x, int y) {
        this.emoji = emoji;
        this.x = x;
        this.y = y;
        frame = new Frame(0, emoji.getBuffer().getChannelCounts().get(0), 1);
//        for (int i = 0; i < emoji.getGif().length; i++) {
//            EmojiUtils.saveBufferedImageAsIdentifier(emoji.getGif()[i], new Identifier("minecraft7tv", emoji.getName() + i));
//        }
    }

    public NativeImage getImage() {
        return image;
    }

    public void nextFrame() {
        this.frame.nextFrame();
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public void setEmoji(Emoji emoji) {
        this.emoji = emoji;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }
}
