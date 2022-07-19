package com.dan.minecraf7tv.emote;

import com.dan.minecraf7tv.utils.FileUtils;
import com.dan.minecraf7tv.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

public class EmojiRenderer {

    private static final int EMOJI_FRAME_RATE = 15;
    private static EmojiRenderer INSTANCE;

    private List<RenderableEmoji> emojis;

    public static EmojiRenderer getInstance() {
        return INSTANCE;
    }

    public static void setInstance(EmojiRenderer INSTANCE) {
        EmojiRenderer.INSTANCE = INSTANCE;
    }

    public EmojiRenderer() {
        emojis = new ArrayList<>();
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

    public RenderableEmoji getRenderableEmoji(String name) {
        for (RenderableEmoji emoji : emojis) {
            if (emoji.getEmoji().getName().equals(name)) {
                return emoji;
            }
        }
        return null;
    }

    public void render(MatrixStack matrices) {
        for (RenderableEmoji emoji : emojis) {
            long offset = FileUtils.getOffset(emoji.getEmoji().getBuffer().getWidth().get(0),
                    emoji.getEmoji().getBuffer().getHeight().get(0),
                    emoji.getFrame().getCurrent(),
                    4);

            NativeImage image = new NativeImage(FileUtils.fromGl(emoji.getEmoji().getBuffer().getChannel()),
                    emoji.getEmoji().getBuffer().getWidth().get(0),
                    emoji.getEmoji().getBuffer().getHeight().get(0),
                    true,
                    MemoryUtil.memAddress(emoji.getEmoji().getBuffer().getGifBytes()) + offset);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
            texture.upload();
            RenderSystem.setShaderTexture(0, texture.getGlId());
            RenderUtils.renderImage(matrices.peek().getModel(), 0, 0, 0, 0, 100, 100, 128, 128, 128, 128);

            emoji.nextFrame();
        }
    }
}
