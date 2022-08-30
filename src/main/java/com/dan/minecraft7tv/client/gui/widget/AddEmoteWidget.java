package com.dan.minecraft7tv.client.gui.widget;

import com.dan.minecraft7tv.common.EmoteCache;
import com.dan.minecraft7tv.client.emote.DownloadThread;
import com.dan.minecraft7tv.client.utils.FileUtils;
import com.dan.minecraft7tv.client.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.awt.*;

public class AddEmoteWidget implements Widget {

    private final Identifier TUTORIAL = new Identifier("minecraft7tv", "textures/example.png");
    private int x;
    private int y;
    private int width;
    private int height;
    private TextFieldWidget nameField;
    private TextFieldWidget urlField;
    private boolean doneHovered;
    private boolean tutorial;
    private boolean tutorialHovered;
    private boolean linkHovered;
    private TextRenderer tr;

    public AddEmoteWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.nameField = new TextFieldWidget(this.x + 10, this.y + 20, this.width - 13, this.y + 35, new Color(10, 10, 29, 100), TextFieldWidget.Filter.NO_FILTER);
        this.urlField = new TextFieldWidget(this.x + 10, this.y + 50, this.width - 13, this.y + 65, new Color(10, 10, 29, 100), TextFieldWidget.Filter.NO_FILTER);
        tutorial = false;
        tutorialHovered = false;
        doneHovered = false;
        linkHovered = false;
        tr = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    public void render(MatrixStack matrices) {
        if (!this.tutorial) {
            nameField.render(matrices);
            urlField.render(matrices);
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.6f, this.x + 10, this.y + 20 - 2 - 1 - tr.fontHeight * 0.6f);
            tr.draw(matrices, "Name", this.x + 10, this.y + 20 - 2 - 1 - tr.fontHeight * 0.6f, Color.WHITE.getRGB());
            matrices.pop();
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.6f, this.x + 10, this.y + 50 - 1 - 2 - tr.fontHeight * 0.6f);
            tr.draw(matrices, "Url", this.x + 10, this.y + 50 - 1 - 2 - tr.fontHeight * 0.6f, Color.WHITE.getRGB());
            matrices.pop();
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.7f, this.x + 10, this.height - 20);
            tr.draw(matrices, this.doneHovered ? "§nAdd" : "Add", this.x + 10, this.height - 25, Color.WHITE.getRGB());
            matrices.pop();
        } else {
            int x = 0;
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.7f, this.x + 10, this.y + 30);
            tr.draw(matrices, "Go to ", this.x + 10, this.y + 30, Color.WHITE.getRGB());
            x += tr.getWidth("Go to ") * 0.7;
            matrices.pop();
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.7f, this.x + 10 + x, this.y + 30);
            tr.draw(matrices, "§n7tv§r,", this.x + 10 + x, this.y + 30, this.linkHovered ? new Color(95, 170, 229).getRGB() : new Color(60, 145, 203).getRGB());
            x += tr.getWidth("7tv,") * 0.7;
            matrices.pop();
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.7f, this.x + 10 + x, this.y + 30);
            tr.draw(matrices, " select your emote and follow", this.x + 10 + x, this.y + 30, Color.white.getRGB());
            matrices.pop();
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.7f, this.x + 10, this.y + 30 + tr.fontHeight * 0.7 + 2);
            tr.draw(matrices, "the image below!", this.x + 10, this.y + 30 + tr.fontHeight * 0.7f + 2, Color.WHITE.getRGB());
            matrices.pop();
            float height = this.y + 50 + tr.fontHeight * 0.7f + 2 + (this.height - 40) - (this.y + 40 + tr.fontHeight * 0.7f + 2);
            RenderSystem.setShaderTexture(0, TUTORIAL);
            RenderUtils.renderImage(matrices.peek().getModel(), this.x + 10, this.y + 50 + tr.fontHeight * 0.7f + 2, 0, 0, x + tr.getWidth(" select your emote and follow") * 0.7f, (this.height - 40) - (this.y + 40 + tr.fontHeight * 0.7f + 2), 697, 244, 697, 244, 1);
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.5f, this.x + 10, height + 4);
            tr.draw(matrices, "Recommended size 4x for higher quality", this.x + 10, height + 4, Color.WHITE.getRGB());
            matrices.pop();
        }
        matrices.push();
        RenderUtils.positionAccurateScale(matrices, 0.7f, width - 30, this.height - 20);
        String text = this.tutorial ? "Done" : "Guide";
        text = this.tutorialHovered ? "§n" + text : text;
        tr.draw(matrices, text, width - 30, this.height - 25, Color.WHITE.getRGB());
        matrices.pop();
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        nameField.onMouseClick(mouseX, mouseY);
        urlField.onMouseClick(mouseX, mouseY);
        if (this.doneHovered) {
            new Thread(new DownloadThread(new EmoteCache(this.urlField.getText(), this.nameField.getText()), false)).start();
            this.urlField.setText("");
            this.nameField.setText("");
        }
        if (this.tutorialHovered && !this.tutorial) {
            this.tutorial = true;
            this.tutorialHovered = false;
        }
        if (this.tutorialHovered) {
            this.tutorial = false;
            this.tutorialHovered = false;
        }
        if (this.linkHovered) {
            FileUtils.open("https://7tv.app/emotes");
        }
        return false;
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        nameField.keyPressed(keyCode, scanCode, modifiers);
        urlField.keyPressed(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        nameField.charTyped(chr, modifiers);
        urlField.charTyped(chr, modifiers);
        return false;
    }

    @Override
    public boolean onMouseMoved(double mouseX, double mouseY) {
        this.doneHovered = mouseX >= this.x + 10 && mouseX <= this.x + 10 + tr.getWidth("Done") * 0.7f - 3 && mouseY >= this.height - 23 && mouseY <= this.height - 23 + tr.fontHeight * 0.7;
        if (!this.tutorial) {
            this.tutorialHovered = mouseX >= width - 30 && mouseX <= width - 30 + tr.getWidth("Guide") * 0.7f - 3 && mouseY >= this.height - 23 && mouseY <= this.height - 23 + tr.fontHeight * 0.7;
        } else {
            this.tutorialHovered = mouseX >= width - 30 && mouseX <= width - 30 + tr.getWidth("Done") * 0.7f - 3 && mouseY >= this.height - 23 && mouseY <= this.height - 23 + tr.fontHeight * 0.7;
        }
        float x = +tr.getWidth("Go to ") * 0.7f;
        this.linkHovered = this.tutorial && mouseX >= this.x + 10 + x && mouseX <= this.x + 10 + x + +tr.getWidth("7tv,") * 0.7 && mouseY >= this.y + 30 && mouseY <= this.y + 30 + tr.fontHeight * 0.7f;
        return false;
    }

    @Override
    public void tick() {
        nameField.tick();
        urlField.tick();
    }
}
