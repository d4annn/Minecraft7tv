package com.dan.minecraft7tv.client.gui.widget;

import com.dan.minecraft7tv.client.config.Config;
import com.dan.minecraft7tv.client.emote.EmoteRenderer;
import com.dan.minecraft7tv.client.emote.RenderableEmote;
import com.dan.minecraft7tv.client.interfaces.Action;
import com.dan.minecraft7tv.client.utils.EmoteUtils;
import com.dan.minecraft7tv.client.utils.FileUtils;
import com.dan.minecraft7tv.client.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
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
    private boolean topHovered;
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
        editingName.setText(emote.getEmote().getName());
        editing = false;
        confirmHovered = false;
        this.action = onValueChange;
        topHovered = false;
    }

    @Override
    public void render(MatrixStack matrices) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderUtils.renderBoxWithRoundCorners(matrices.peek().getModel(), x, y, width, height, 3, color);
        if (!editing) {
            EmoteRenderer.getInstance().render(matrices, emote.getEmote().getName(), (int) x, (int) y, 30, 30, 1);
            if (Config.getInstance().fpsTick) emote.getFrame().nextFrame();
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.5f, this.x + 15, this.y + 34);
            Screen.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, this.emote.getEmote().getName(), (int)this.x + 14, (int)this.y + 34, Color.WHITE.getRGB());
            matrices.pop();
            if(this.topHovered) {
                RenderUtils.renderQuad(matrices, this.x, this.y, this.x + 30, this.y + 10, new Color(21, 20, 20,150).getRGB());
                RenderUtils.renderQuad(matrices, this.x + 15, this.y + 1, this.x + 15.3f, this.y + 8, Color.WHITE.getRGB());
                RenderSystem.setShaderColor(255, 255, 255, 255);
                RenderSystem.setShaderTexture(0, this.EDIT);
                if (this.editHovered) RenderSystem.setShaderColor(0, 255, 0, 255);
                RenderUtils.renderImage(matrices.peek().getModel(), this.x + 5, this.y + 2, 0, 0, 5, 5, 256, 256, 256, 256, 1);
                RenderSystem.setShaderColor(255, 255, 255, 255);
                RenderSystem.setShaderTexture(0, this.DELETE);
                if (this.deleteHovered) RenderSystem.setShaderColor(255, 0, 0, 255);
                RenderUtils.renderImage(matrices.peek().getModel(), this.x + 15 + 6, this.y + 2, 0, 0, 5, 5, 256, 256, 256, 256, 1);
            }
        } else {
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.5f, this.x, this.y + 7 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.5f);
            MinecraftClient.getInstance().textRenderer.draw(matrices, Text.of("Name"), this.x + 3, this.y + 7 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.5f, Color.WHITE.getRGB());
            matrices.pop();
            this.editingName.render(matrices);
            RenderSystem.setShaderColor(255, 255, 255, 255);
            RenderSystem.setShaderTexture(0, this.TICK);
            if (this.confirmHovered) RenderSystem.setShaderColor(0, 255, 0, 255);
            RenderUtils.renderImage(matrices.peek().getModel(), this.x + (this.width - this.x) / 2 - 3, this.height - 8, 0, 0, 6, 6, 256, 256, 256, 256, 1);
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        if (this.editing) {
            this.editingName.onMouseClick(mouseX, mouseY);
            if (this.confirmHovered) {
                if (!this.editingName.getText().isEmpty()) {
                    boolean passed = true;
                    for (String name : EmoteUtils.getAllCachedEmotes()) {
                        if (this.editingName.getText().equals(name)) {
                            passed = false;
                            break;
                        }
                    }
                    if (passed) {
                        File finalGif = new File(FileUtils.FOLDER.getPath() + File.separator + this.emote.getEmote().getName() + File.separator + this.emote.getEmote().getName() + ".gif");
                        finalGif.renameTo(new File(FileUtils.FOLDER.getPath() + File.separator + this.emote.getEmote().getName() + File.separator + this.editingName.getText() + ".gif"));
                        finalGif = new File(FileUtils.FOLDER.getPath() + File.separator + this.emote.getEmote().getName());
                        finalGif.renameTo(new File(FileUtils.FOLDER.getPath() + File.separator + this.editingName.getText()));
                        this.emote.getEmote().setName(this.editingName.getText());
                        int index = Config.getInstance().getIndexByUrl(this.emote.getEmote().getUrl());
                        if (index != -1) {
                            Config.getInstance().changeName(index, this.editingName.getText());
                            Config.getInstance().saveConfig();
                        }
                    }
                }
                this.editing = false;
            }
            return true;
        }
        if (mouseX >= (int) this.x && mouseX <= (int) this.x + 30 && mouseY >= (int) this.y && mouseY <= this.y + 30 && !this.topHovered) {
            String code = this.emote.getEmote().getUrl().split("/")[4];
            FileUtils.open("https:\\7tv.app/emotes/" + code);
        } else if (this.editHovered) {
            this.editing = true;
        } else if (this.deleteHovered) {
            if (Config.getInstance().deleteCache) {
                EmoteUtils.deleteEmoteCache(this.emote.getEmote().getName());
            }
            EmoteRenderer.getInstance().removeRenderableEmote(this.emote.getEmote().getName());
            int index = Config.getInstance().getIndexByUrl(this.emote.getEmote().getUrl());
            if (index != -1) {
                Config.getInstance().removeEmote(index);
                Config.getInstance().saveConfig();
            }
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
        editHovered = mouseX >= this.x + 5 && mouseX <= this.x + 5 + 5 && mouseY >= this.y + 2 && mouseY <= this.y + 2 + 5;
        deleteHovered = mouseX >= this.x + 15 + 6 && mouseX <= this.x + 15 + 6 + 5 && mouseY >= this.y + 2 && mouseY <= this.y + 2 + 5;
        confirmHovered = this.editing && mouseX >= this.x + (this.width - this.x) / 2 - 3 && mouseX <= this.x + (this.width - this.x) / 2 - 3 + 6 && mouseY >= this.height - 8 && mouseY <= this.height - 8 + 6;
        topHovered = !this.editing && mouseX >= x && mouseX <= x + 30 && mouseY >= y && mouseY <= y + 10;
        return false;
    }

    @Override
    public void tick() {
        if (!Config.getInstance().fpsTick && emote.getEmote().isGif())
            emote.getFrame().nextFrame();
        if (this.editing) this.editingName.tick();
    }

    private void onValueChange() {
        this.action.execute(true);
    }
}
