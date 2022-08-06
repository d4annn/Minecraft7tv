package com.dan.minecraft7tv.gui.widget;

import com.dan.minecraft7tv.emote.RenderableEmote;
import com.dan.minecraft7tv.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class AddEmoteWidget implements Widget {

    private int x;
    private int y;
    private int width;
    private int height;
    private @Nullable RenderableEmote emote;
    private TextFieldWidget nameField;
    private TextFieldWidget urlField;
    private boolean doneHovered;

    public AddEmoteWidget(int x, int y, int width, int height, @Nullable RenderableEmote emote) {
        this.emote = emote;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.nameField = new TextFieldWidget(this.x + 10, this.y + 20, this.width - 13, this.y + 35, new Color(10, 10, 29, 100), TextFieldWidget.Filter.NO_FILTER);
        this.urlField = new TextFieldWidget(this.x + 10, this.y + 50, this.width - 13, this.y + 65, new Color(10, 10, 29, 100), TextFieldWidget.Filter.NO_FILTER);
    }

    @Override
    public void render(MatrixStack matrices) {
        nameField.render(matrices);
        urlField.render(matrices);
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        matrices.push();
        RenderUtils.positionAccurateScale(matrices, 0.6f, this.x + 10, this.y + 20 - 2 - 1 - tr.fontHeight * 0.6f);
        tr.draw(matrices, "Name", this.x + 10, this.y + 20 - 2 - 1 - tr.fontHeight * 0.6f, Color.WHITE.getRGB());
        matrices.pop();
        matrices.push();
        RenderUtils.positionAccurateScale(matrices, 0.6f, this.x + 10, this.y + 50 - 1 - 2 - tr.fontHeight * 0.6f);
        tr.draw(matrices, "Url", this.x + 10, this.y + 50 - 1 - 2 - tr.fontHeight * 0.6f, Color.WHITE.getRGB());
        matrices.pop();
        matrices.pop();
        RenderUtils.positionAccurateScale(matrices, 0.7f, this.x + 10, this.height - 20);
        tr.draw(matrices, "Done", this.x + 10, this.height - 20, Color.WHITE.getRGB());
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        nameField.onMouseClick(mouseX, mouseY);
        urlField.onMouseClick(mouseX, mouseY);
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
        if(mouseX >= this.x + 10 && mouseX <= && mouseY >= && mouseY <= )
        return false;
    }

    @Override
    public void tick() {
        nameField.tick();
        urlField.tick();
    }
}
