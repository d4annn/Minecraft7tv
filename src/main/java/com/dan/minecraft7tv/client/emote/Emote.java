package com.dan.minecraft7tv.client.emote;

import com.dan.minecraft7tv.client.utils.EmoteUtils;
import com.dan.minecraft7tv.client.utils.FileUtils;
import net.minecraft.client.texture.NativeImage;

import java.io.File;
import java.io.IOException;

public class Emote {
    private Buffers buffer;
    private String name;
    private String url;
    private boolean gif;

    public Emote(String url, String name) {
        url = url.replaceAll(".avif", "");
        this.name = name;
        this.url = url;
        try {
            EmoteUtils.loadGif(url, name);
            gif = EmoteUtils.getGifSize(new File(
                    FileUtils.FOLDER.getPath() + File.separator + getName() + File.separator + getName() + ".gif")) > 1;
            if (gif) {
                this.buffer = FileUtils.getGifBuffer(NativeImage.Format.RGBA, FileUtils.getIsBuffer(org.apache.commons.io.FileUtils.openInputStream(
                        new File(FileUtils.FOLDER.getPath() + File.separator + name + File.separator + name + ".gif"))));
            }
        } catch (IOException e) {
            EmoteUtils.logError("Error occurred while trying to create an emote, check logs for more info.", e.getMessage());
        }
    }

    public Emote(String name) {
        this.name = name;
        try {
            this.gif = EmoteUtils.getGifSize(new File(
                    FileUtils.FOLDER.getPath() + File.separator + getName() + File.separator + getName() + ".gif")) > 1;
            if (this.gif) {
                this.buffer = FileUtils.getGifBuffer(NativeImage.Format.RGBA, FileUtils.getIsBuffer(org.apache.commons.io.FileUtils.openInputStream(
                        new File(FileUtils.FOLDER.getPath() + File.separator + name + File.separator + name + ".gif"))));
            }
        } catch (IOException e) {
            EmoteUtils.logError("Error occurred while trying to create an emote, check logs for more info.", e.getMessage());
        }
    }

    public Buffers getBuffer() {
        return buffer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isGif() {
        return gif;
    }

    public boolean isServer() {
        return this instanceof ServerEmote;
    }
}
