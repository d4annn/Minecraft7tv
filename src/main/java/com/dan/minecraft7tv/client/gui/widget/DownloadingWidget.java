package com.dan.minecraft7tv.client.gui.widget;

import com.dan.minecraft7tv.client.emote.EmoteRenderer;
import com.dan.minecraft7tv.client.emote.RenderableEmote;
import com.dan.minecraft7tv.client.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class DownloadingWidget implements Widget {

    private static RenderableEmote LOADING_EMOTE;
    private String name;
    private int tick;
    private boolean close;

    public DownloadingWidget(String name) {
        this.name = name;
        close = false;
        tick = 0;
    }

    public static void setLoadingEmote(RenderableEmote loadingEmote) {
        LOADING_EMOTE = loadingEmote;
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        RenderUtils.renderBoxWithRoundCorners(matrices.peek().getModel(), width - 120, 5, width - 5, 70, 3, new Color(24, 34, 62, 240));
        RenderUtils.positionAccurateScale(matrices, 1.3f, width - 41, 20);
        Screen.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, name, width - 58, 20, Color.WHITE.getRGB());
        if (!close)
            EmoteRenderer.getInstance().render(matrices, LOADING_EMOTE, width - 57.5f - 8, 36, 16, 16, 1);
        else {
            Screen.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, "Completed!", width - 57, 36, new Color(90, 186, 60, tick * 5 < 5 ? 5 : tick * 5).getRGB());
        }
        matrices.pop();
    }

    public void close() {
        this.close = true;
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    @Override
    public boolean onMouseMoved(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void tick() {
        LOADING_EMOTE.getFrame().nextFrame();
        if (this.close) tick++;
        if(this.tick > 40 ) EmoteRenderer.getInstance().removeDownloading(this);
    }
}
