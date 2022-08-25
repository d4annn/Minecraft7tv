package com.dan.minecraft7tv.emote;

import com.dan.minecraft7tv.utils.EmoteUtils;
import com.dan.minecraft7tv.utils.FileUtils;
import net.minecraft.client.texture.NativeImageBackedTexture;

import java.io.File;

public class RenderableEmote {

    private Emote emote;
    private Frame frame;
    private NativeImageBackedTexture[] images;

    public RenderableEmote(Emote emote) {
        this.emote = emote;
        if(emote.isGif()) {
            frame = new Frame(0, EmoteUtils.getGifSize(new File(
                    FileUtils.FOLDER.getPath() + File.separator + emote.getName() + File.separator + emote.getName() + ".gif")), 1);
            images = new NativeImageBackedTexture[frame.getMax()];
        } else {
            images = new NativeImageBackedTexture[1];
        }
    }

    public boolean isImageEmpty(int i) {
        return null == getImage(i);
    }

    public void setImage(int i, NativeImageBackedTexture texture) {
        try {
            if(!emote.isGif()) {
                images[0] = texture;
                return;
            }
            images[i] = texture;
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public NativeImageBackedTexture getImage(int i) {
        if(!this.emote.isGif()) return this.images[0];
        if (i > this.images.length - 1) {
            return this.images[this.images.length - 1];
        }
        return this.images[i];
    }

    public NativeImageBackedTexture[] getImages() {
        return images;
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
