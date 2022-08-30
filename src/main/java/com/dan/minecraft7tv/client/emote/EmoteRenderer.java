package com.dan.minecraft7tv.client.emote;

import com.dan.minecraft7tv.client.config.Config;
import com.dan.minecraft7tv.client.gui.widget.DownloadingWidget;
import com.dan.minecraft7tv.client.utils.FileUtils;
import com.dan.minecraft7tv.client.utils.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class EmoteRenderer {

    private static final int EMOJI_FRAME_RATE = 15;
    private static EmoteRenderer INSTANCE;

    private List<RenderableEmote> emojis;
    private List<DownloadingWidget> downloadingEmotes;

    public EmoteRenderer() {
        emojis = new ArrayList<>();
        downloadingEmotes = new ArrayList<>();
    }

    public static EmoteRenderer getInstance() {
        return INSTANCE;
    }

    public static void setInstance(EmoteRenderer INSTANCE) {
        EmoteRenderer.INSTANCE = INSTANCE;
    }

    public List<DownloadingWidget> getDownloading() {
        return downloadingEmotes;
    }

    public void addDownloading(DownloadingWidget emote) {
        if (!Config.getInstance().showDownload) return;
        this.downloadingEmotes.add(emote);
    }

    public void removeDownloading(DownloadingWidget emote) {
        if (!Config.getInstance().showDownload) return;
        this.downloadingEmotes.remove(emote);
    }

    public List<RenderableEmote> getEmotes() {
        return emojis;
    }

    public void addRenderableEmote(RenderableEmote emoji) {
        this.emojis.add(emoji);
    }

    public void removeRenderableEmote(String name, boolean server) {
        int pos = -1;
        for (int i = 0; i < emojis.size(); i++) {
            if (emojis.get(i).getEmote().getName().equals(name)) {
                pos = i;
            }
        }
        if (pos != -1) {
            if (server) {
                if (emojis.get(pos).getEmote().isServer())
                    emojis.remove(pos);
            } else {
                emojis.remove(pos);
            }
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
        if (emojis.get(getIndexByName(name)).getEmote().isGif())
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

    public void render(MatrixStack matrices, String name, float x, float y, float w, float h, float transparency) {
        int index = getIndexByName(name);
        if (index == -1) {
            return;
        }//throw error
        RenderableEmote emoji = emojis.get(index);
        render(matrices, emoji, x, y, w, h, transparency);
    }

    public void render(MatrixStack matrices, RenderableEmote emote, float x, float y, float w, float h, float transparency) {
        NativeImageBackedTexture texture = null;
        if (emote.getEmote().isGif()) {
            if (emote.isImageEmpty(emote.getFrame().getCurrent())) {
                int width = emote.getEmote().getBuffer().width;
                int height = emote.getEmote().getBuffer().height;
                ByteBuffer bytes = emote.getEmote().getBuffer().gifBytes;
                int channel = emote.getEmote().getBuffer().channel;

                long offset = FileUtils.getOffset(width,
                        height,
                        emote.getFrame().getCurrent(),
                        4);

                NativeImage image = new NativeImage(FileUtils.fromGl(channel),
                        width,
                        height,
                        true,
                        MemoryUtil.memAddress(bytes) + offset);

                texture = new NativeImageBackedTexture(image);
                emote.setImage(emote.getFrame().getCurrent(), texture);
                //reset tex parameters
                GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
                GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
                texture.upload();
            } else {
                texture = emote.getImage(emote.getFrame().getCurrent());
            }
        } else {
            if (emote.isImageEmpty(0)) {
                try {
                    NativeImage image = NativeImage.read((FileUtils.getIsBuffer(org.apache.commons.io.FileUtils.openInputStream(
                            new File(FileUtils.FOLDER.getPath() + File.separator + emote.getEmote().getName() + File.separator + emote.getEmote().getName() + ".gif")))));
                    texture = new NativeImageBackedTexture(image);
                    emote.setImage(0, texture);
                    texture.getImage().upload(0, 0, 0, false);
                } catch (IOException e) {
                }
            } else {
                texture = emote.getImage(0);
            }
        }
        RenderSystem.setShaderTexture(0, texture.getGlId());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtils.renderImage(matrices.peek().getModel(), x, y, 0, 0, w, h, 128, 128, 128, 128, transparency);
//        GL30.glDeleteTextures(texture.getGlId());
    }
}

