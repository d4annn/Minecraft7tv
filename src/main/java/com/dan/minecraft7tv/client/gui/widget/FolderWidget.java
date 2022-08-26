package com.dan.minecraft7tv.client.gui.widget;

import com.dan.minecraft7tv.client.gui.OptionsScreen;
import com.dan.minecraft7tv.client.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FolderWidget implements Widget {

    private final TextRenderer tr = MinecraftClient.getInstance().textRenderer;
    private int x;
    private int y;
    private int width;
    private int height;
    private String name;
    private boolean opened;
    private List<SettingWidget> settings;

    public FolderWidget(int x, int y, int width, int height, String name) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.name = name;
        this.opened = false;
        this.settings = new ArrayList<>();
    }

    @Override
    public void render(MatrixStack matrices) {
        if (OptionsScreen.palette != null) {
            for(SettingWidget set : settings) {
                if(set instanceof ColorWidget && set.equals(OptionsScreen.palette)) {
                    set.render(matrices);
                }
            }
            return;
        }
        Color backColor = new Color(13, 13, 34, 150);
        Color c = new Color(255, 255, 255, 30);
        Color w = new Color(0, 0, 0, 30);
        RenderUtils.renderQuad(matrices, this.x, this.y, this.width, this.height, backColor.getRGB());
        RenderUtils.renderQuad(matrices, this.x, this.height + 0.01f, this.width, this.height + 0.3f, w.getRGB());
        RenderUtils.renderQuad(matrices, this.x - 0.01f, this.y, this.x - 0.3f, this.y, w.getRGB());
        RenderUtils.renderQuad(matrices, this.x, this.y - 0.3f, this.width, this.y - 0.01f, c.getRGB());
        RenderUtils.renderQuad(matrices, this.width + 0.01f, this.y, this.width + 0.3f, this.height, c.getRGB());
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        matrices.push();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        if (this.opened) {
            bufferBuilder.vertex(matrices.peek().getModel(), this.x + 5.5f, this.y + 3, 1).color(1f, 1f, 1f, 240 / 255f).next();
            bufferBuilder.vertex(matrices.peek().getModel(), this.x + 1.5f, this.y + 3, 1).color(1f, 1f, 1f, 240 / 255f).next();
            bufferBuilder.vertex(matrices.peek().getModel(), (this.x + 5.5f + this.x + 1.5f) / 2, this.y + 7.5f, 1).color(1f, 1f, 1f, 240 / 255f).next();
        } else {
            bufferBuilder.vertex(matrices.peek().getModel(), this.x + 6f, this.y + (this.height - this.y) / 2, 1).color(1f, 1f, 1f, 240 / 255f).next();
            bufferBuilder.vertex(matrices.peek().getModel(), this.x + 1.5f, this.y + 3, 1).color(1f, 1f, 1f, 240 / 255f).next();
            bufferBuilder.vertex(matrices.peek().getModel(), this.x + 1.5f, this.height - 4, 1).color(1f, 1f, 1f, 240 / 255f).next();
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        matrices.pop();
        matrices.push();
        RenderUtils.positionAccurateScale(matrices, 0.6f, this.x + 8, this.y + 2);
        tr.drawWithShadow(matrices, this.name, this.x + 8, this.y + 2, new Color(255, 255, 255, 240).getRGB());
        matrices.pop();
        if (this.opened) {
            for (SettingWidget setting : settings) {
                setting.render(matrices);
            }
        }
    }

    public void addSetting(SettingWidget setting) {
        this.settings.add(setting);
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        if (this.isHovered(mouseX, mouseY) && OptionsScreen.palette == null) {
            this.opened = !opened;
            return true;
        }
        if (this.opened) {
            for (SettingWidget sett : settings) {
                if (OptionsScreen.palette == null || sett instanceof ColorWidget)
                    sett.onMouseClick(mouseX, mouseY);
            }
        }
        return false;
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        if (mouseX >= x && mouseX <= width && mouseY >= y && mouseY <= height) {
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (SettingWidget setting : settings) {
            setting.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (SettingWidget setting : settings) {
            setting.charTyped(chr, modifiers);
        }
        return false;
    }

    @Override
    public boolean onMouseMoved(double mouseX, double mouseY) {
        for (SettingWidget widget : settings) {
            widget.onMouseMoved(mouseX, mouseY);
        }
        return true;
    }

    @Override
    public void tick() {
        for (SettingWidget widget : settings) {
            widget.tick();
        }
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public List<SettingWidget> getSettings() {
        return settings;
    }
}
