package com.dan.minecraf7tv.emote;

import com.dan.minecraf7tv.utils.EmojiUtils;
import com.dan.minecraf7tv.utils.FileUtils;
import net.minecraft.client.texture.NativeImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Emoji {

    private Buffers buffer;
    private String name;
    private String url;

    public Emoji(String url, String name, boolean stack) {
        this.url = url;
        this.name = name;
        try {
            EmojiUtils.loadGif(url, name);
            File file = new File(FileUtils.TEMP_FODLER.getPath() + "\\temp_" + name + ".webp");
            this.buffer = FileUtils.getGifBuffer(NativeImage.Format.RGBA, FileUtils.getIsBuffer(org.apache.commons.io.FileUtils.openInputStream(
                    new File(FileUtils.FOLDER.getPath() + "\\" + name + "\\" + "temp_" + name + ".gif"))));
        } catch (IOException e) {
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
}
