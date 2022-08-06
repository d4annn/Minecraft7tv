package com.dan.minecraft7tv.emote;

import com.dan.minecraft7tv.utils.EmoteUtils;
import com.dan.minecraft7tv.utils.FileUtils;
import net.minecraft.client.texture.NativeImage;

import java.io.File;

public class RenderableEmote {

    private Emote emote;
    private NativeImage image;
    private Frame frame;

    public RenderableEmote(Emote emote) {
        this.emote = emote;
        frame = new Frame(0, EmoteUtils.getGifSize(new File(
                FileUtils.FOLDER.getPath() + "\\" + emote.getName() + "\\" + emote.getName() + ".gif")) - 1, 1);
    }

    public NativeImage getImage() {
        return image;
    }

    public void nextFrame() {
        this.frame.nextFrame();
    }

    public Emote getEmote() {
        return emote;
    }

    public void setEmoji(Emote emote) {
        this.emote = emote;
    }

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }
}
