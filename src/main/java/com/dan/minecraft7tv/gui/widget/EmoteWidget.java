package com.dan.minecraft7tv.gui.widget;

import com.dan.minecraft7tv.config.Config;
import com.dan.minecraft7tv.emote.EmoteRenderer;
import com.dan.minecraft7tv.emote.RenderableEmote;
import com.dan.minecraft7tv.interfaces.Action;
import com.dan.minecraft7tv.utils.EmoteUtils;
import com.dan.minecraft7tv.utils.FileUtils;
import com.dan.minecraft7tv.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.io.File;

public class EmoteWidget implements Widget {

    private final Identifier EDIT = new Identifier("minecraft7tv", "textures/edit.png");
    private final Identifier DELETE = new Identifier("minecraft7tv", "textures/cruz.png");
    private final Identifier TICK = new Identifier("minecraft7tv", "textures/tick.png");

    private float x;
    private float y;
    private float width;
    private float height;
    private RenderableEmote emote;
    private Color color;
    private boolean editHovered;
    private boolean deleteHovered;
    private boolean editing;
    private boolean confirmHovered;
    private TextFieldWidget editingName;
    private Action<Boolean> action;

    public EmoteWidget(float x, float y, RenderableEmote emote, Action<Boolean> onValueChange) {
        this.x = x;
        this.y = y;
        this.width = x + 30;
        height = y + 40;
        color = new Color(19, 19, 55, 150);
        this.emote = emote;
        editHovered = false;
        deleteHovered = false;
        editingName = new TextFieldWidget(this.x + 1, this.y + 10, this.width - 1, this.y + 20, new Color(10, 10, 29, 100), TextFieldWidget.Filter.NO_FILTER);
        editing = false;
        confirmHovered = false;
        this.action = onValueChange;
    }

    @Override
    public void render(MatrixStack matrices) {
        RenderUtils.renderBoxWithRoundCorners(matrices.peek().getModel(), x, y, width, height, 3, color);
        if (!editing) {
            EmoteRenderer.getInstance().render(matrices, emote.getEmote().getName(), (int) x, (int) y, 30, 30);
            if (Config.getInstance().fpsTick) emote.getFrame().nextFrame();
            RenderUtils.renderQuad(matrices, this.x + 15, this.y + 32, this.x + 15.3f, this.height + 1, Color.WHITE.getRGB());
            RenderSystem.setShaderColor(255, 255, 255, 255);
            RenderSystem.setShaderTexture(0, this.EDIT);
            if (this.editHovered) RenderSystem.setShaderColor(0, 255, 0, 255);
            RenderUtils.renderImage(matrices.peek().getModel(), this.x + 5, this.y + 34, 0, 0, 5, 5, 256, 256, 256, 256);
            RenderSystem.setShaderColor(255, 255, 255, 255);
            RenderSystem.setShaderTexture(0, this.DELETE);
            if (this.deleteHovered) RenderSystem.setShaderColor(255, 0, 0, 255);
            RenderUtils.renderImage(matrices.peek().getModel(), this.x + 15 + 6, this.y + 34, 0, 0, 5, 5, 256, 256, 256, 256);
        } else {
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.5f, this.x, this.y + 7 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.5f);
            MinecraftClient.getInstance().textRenderer.draw(matrices, Text.of("Name"), this.x + 3, this.y + 7 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.5f, Color.WHITE.getRGB());
            matrices.pop();
            this.editingName.render(matrices);
            RenderSystem.setShaderColor(255, 255, 255, 255);
            RenderSystem.setShaderTexture(0, this.TICK);
            if (this.confirmHovered) RenderSystem.setShaderColor(0, 255, 0, 255);
            RenderUtils.renderImage(matrices.peek().getModel(), this.x + (this.width - this.x) / 2 - 3, this.height - 8, 0, 0, 6, 6, 256, 256, 256, 256);
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        if (this.editing) {
            this.editingName.onMouseClick(mouseX, mouseY);
            if(this.confirmHovered) {
                if(!this.editingName.getText().isEmpty()) {
                    boolean passed = true;
                    for(String name : EmoteUtils.getAllCachedEmotes()) {
                        if(this.editingName.getText().equals(name)) {
                            passed = false;
                            break;
                        }
                    }
                    if(passed) {
                        File finalGif = new File(FileUtils.FOLDER.getPath() + "\\" + this.emote.getEmote().getName() + "\\" + this.emote.getEmote().getName() + ".gif");
                        finalGif.renameTo(new File(FileUtils.FOLDER.getPath() + "\\" + this.emote.getEmote().getName() + "\\" + this.editingName.getText() + ".gif"));
                        finalGif = new File(FileUtils.FOLDER.getPath() + "\\" + this.emote.getEmote().getName());
                        finalGif.renameTo(new File(FileUtils.FOLDER.getPath() + "\\" + this.editingName.getText()));
                        this.emote.getEmote().setName(this.editingName.getText());
                    }
                }
                this.editing = false;
            }
            return true;
        }
        if (mouseX >= (int) this.x && mouseX <= (int) this.x + 30 && mouseY >= (int) this.y && mouseY <= this.y + 30) {
            FileUtils.open(this.emote.getEmote().getUrl());
        } else if (this.editHovered) {
            this.editing = true;
        } else if (this.deleteHovered) {
            if (Config.getInstance().deleteCache) {
                EmoteUtils.deleteEmoteCache(this.emote.getEmote().getName());
            }
            EmoteRenderer.getInstance().removeRenderableEmoji(this.emote.getEmote().getName());
            this.onValueChange();
        }
        return false;
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        if (mouseX >= x - 3 && mouseX <= width + 3 && mouseY >= y - 3 && mouseY <= height + 3) {
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.editing) {
            this.editingName.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.editing) {
            this.editingName.charTyped(chr, modifiers);
        }
        return false;
    }

    @Override
    public boolean onMouseMoved(double mouseX, double mouseY) {
        editHovered = mouseX >= this.x + 5 && mouseX <= this.x + 5 + 5 && mouseY >= this.y + 34 && mouseY <= this.y + 34 + 5;
        deleteHovered = mouseX >= this.x + 15 + 6 && mouseX <= this.x + 15 + 6 + 5 && mouseY >= this.y + 34 && mouseY <= this.y + 34 + 5;
        confirmHovered = this.editing && mouseX >= this.x + (this.width - this.x) / 2 - 3 && mouseX <= this.x + (this.width - this.x) / 2 - 3 + 6 && mouseY >= this.height - 8 && mouseY <= this.height - 8 + 6;
        return false;
    }

    @Override
    public void tick() {
        if (!Config.getInstance().fpsTick)
            emote.getFrame().nextFrame();
        if(this.editing) this.editingName.tick();
    }

    private void onValueChange() {
        this.action.execute(true);
    }
}
