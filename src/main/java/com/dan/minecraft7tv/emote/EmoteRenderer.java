package com.dan.minecraft7tv.emote;

import com.dan.minecraft7tv.config.Config;
import com.dan.minecraft7tv.utils.FileUtils;
import com.dan.minecraft7tv.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class EmoteRenderer {

    private static final int EMOJI_FRAME_RATE = 15;
    private static EmoteRenderer INSTANCE;

    private List<RenderableEmote> emojis;

    public EmoteRenderer() {
        emojis = new ArrayList<>();
    }

    public static EmoteRenderer getInstance() {
        return INSTANCE;
    }

    public static void setInstance(EmoteRenderer INSTANCE) {
        EmoteRenderer.INSTANCE = INSTANCE;
    }

    public List<RenderableEmote> getEmotes() {
        return emojis;
    }

    public void addRenderableEmoji(RenderableEmote emoji) {
        this.emojis.add(emoji);
    }

    public void removeRenderableEmoji(String name) {
        int pos = -1;
        for (int i = 0; i < emojis.size(); i++) {
            if (emojis.get(i).getEmote().getName().equals(name)) {
                pos = i;
            }
        }
        if (pos != -1) {
            emojis.remove(pos);
        }
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        emojis.forEach(emoji -> names.add(emoji.getEmote().getName()));
        return names;
    }

    public RenderableEmote getRenderableEmoji(String name) {
        for (RenderableEmote emoji : emojis) {
            if (emoji.getEmote().getName().equals(name)) {
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
            if (emojis.get(i).getEmote().getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void render(MatrixStack matrices, String name, int x, int y, int w, int h) {
        int index = getIndexByName(name);
        if (index == -1) {
            return;
        }//throw error
        RenderableEmote emoji = emojis.get(index);
        int width = emoji.getEmote().getBuffer().width;
        int height = emoji.getEmote().getBuffer().height;
        ByteBuffer bytes = emoji.getEmote().getBuffer().gifBytes;
        int channel = emoji.getEmote().getBuffer().channel;

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
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtils.renderImage(matrices.peek().getModel(), x, y, 0, 0, w, h, 128, 128, 128, 128);
//        GL30.glDeleteTextures(texture.getGlId());
        RenderSystem.deleteTexture(texture.getGlId());
    }
}

