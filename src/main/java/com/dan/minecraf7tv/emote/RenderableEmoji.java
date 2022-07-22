package com.dan.minecraf7tv.emote;

import com.dan.minecraf7tv.utils.EmojiUtils;
import com.dan.minecraf7tv.utils.FileUtils;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import java.io.File;

public class RenderableEmoji {

    private Emoji emoji;
    private NativeImage image;
    private Frame frame;

    public RenderableEmoji(Emoji emoji) {
        this.emoji = emoji;
        frame = new Frame(0, EmojiUtils.getGifSize(new File(FileUtils.FOLDER.getPath() + "\\" + emoji.getName() + "\\" + "temp_" + emoji.getName() + ".gif")), 1);
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

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }
}
