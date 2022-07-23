package com.dan.minecraft7tv.emote;

import com.dan.minecraft7tv.config.Config;
import com.dan.minecraft7tv.utils.FileUtils;
import com.dan.minecraft7tv.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class EmojiRenderer {

    private static final int EMOJI_FRAME_RATE = 15;
    private static EmojiRenderer INSTANCE;

    private List<RenderableEmoji> emojis;

    public EmojiRenderer() {
        emojis = new ArrayList<>();
    }

    public static EmojiRenderer getInstance() {
        return INSTANCE;
    }

    public static void setInstance(EmojiRenderer INSTANCE) {
        EmojiRenderer.INSTANCE = INSTANCE;
    }

    public List<RenderableEmoji> getEmojis() {
        return emojis;
    }

    public void addRenderableEmoji(RenderableEmoji emoji) {
        this.emojis.add(emoji);
    }

    public void removeRenderableEmoji(String name) {
        int pos = -1;
        for (int i = 0; i < emojis.size(); i++) {
            if (emojis.get(i).getEmoji().getName().equals(name)) {
                pos = i;
            }
        }
        if (pos != -1) {
            emojis.remove(pos);
        }
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        emojis.forEach(emoji -> names.add(emoji.getEmoji().getName()));
        return names;
    }

    public RenderableEmoji getRenderableEmoji(String name) {
        for (RenderableEmoji emoji : emojis) {
            if (emoji.getEmoji().getName().equals(name)) {
                return emoji;
            }
        }
        return null;
    }

    public boolean isEmoji(String line) {
        for (String word : line.split(" ")) {
            if (getNames().contains(word)) {
                return true;
            }
        }
        return false;
    }

    public void tick(String name) {
        emojis.get(getIndexByName(name)).nextFrame();
    }

    // -1 if it doesnt exist
    private int getIndexByName(String name) {
        for (int i = 0; i < emojis.size(); i++) {
            if (emojis.get(i).getEmoji().getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void render(MatrixStack matrices, String name, int x, int y) {
        int index = getIndexByName(name);
        if (index == -1) {
            return;
        }//throw error
        RenderableEmoji emoji = emojis.get(index);
        int width = emoji.getEmoji().getBuffer().width;
        int height = emoji.getEmoji().getBuffer().height;
        ByteBuffer bytes = emoji.getEmoji().getBuffer().gifBytes;
        int channel = emoji.getEmoji().getBuffer().channel;

        long offset = FileUtils.getOffset(width,
                height,
                emoji.getFrame().getCurrent(),
                4);

        NativeImage image = new NativeImage(FileUtils.fromGl(channel),
                width,
                height,
                true,
                MemoryUtil.memAddress(bytes) + offset);

        NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
        texture.upload();
        RenderSystem.setShaderTexture(0, texture.getGlId());
        RenderUtils.renderImage(matrices.peek().getModel(), x, y, 0, 0, (int) Config.getInstance().emoteSize, (int) Config.getInstance().emoteSize, 128, 128, 128, 128);
    }
}

