package com.dan.minecraft7tv.client.gui.widget;

import com.dan.minecraft7tv.client.interfaces.Action;
import com.dan.minecraft7tv.client.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

public class CategoryWidget implements Widget{

    private static TextRenderer tr = MinecraftClient.getInstance().textRenderer;
    private int x;
    private int y;
    private int color;
    private float scale;
    private String text;
    private int[] pos;
    private boolean underlined;
    private Action<CategoryWidget> action;

    public CategoryWidget(int x, int y, int color, float scale, String text, boolean indent, Action<CategoryWidget> action) {
        this.x = x;
        this.y = y;
        setColor(color);
        this.scale = scale;
        this.text = text;
        pos = null;
        underlined = false;
        if(indent) {
            this.x -= (tr.getWidth(text) * scale) / 2;
        }
        this.action = action;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        if (null != pos) {
            if (mouseX >= pos[0] && mouseX <= pos[2] && mouseY >= pos[1] && mouseY <= pos[3]) {
                return true;
            }
        }
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
        return true;
    }

    @Override
    public void tick() {
    }

    public void render(MatrixStack matrices) {
        matrices.push();
        RenderUtils.positionAccurateScale(matrices, this.scale, x, y);
        String textToRender = text;
        if (underlined) {
            textToRender = "§n" + textToRender;
        }
        tr.drawWithShadow(matrices, Text.of(textToRender), x, y, color);
        matrices.pop();
        if (scale == 1) {
            this.pos = new int[]{x, y, tr.getWidth(text), tr.fontHeight};
        } else {
            float scaledX = x * scale * (1 / scale);
            float scaledY = y * scale * (1 / scale);
            float scaledH = tr.fontHeight * scale;
            this.pos = new int[]{(int) scaledX, (int) scaledY, (int) scaledX + (int) (tr.getWidth(textToRender) * scale), (int) (scaledY + scaledH)};
        }
    }

    public boolean onMouseClick(double mouseX, double mouseY) {
        if(isHovered(mouseX, mouseY)) {
            this.action.execute(this);
            return true;
        }
        return false;
    }

    public void setColor(int color) {
        if(color == -1) this.color = Color.WHITE.getRGB();
        this.color = color;
    }

    public boolean isUnderlined() {
        return this.underlined;
    }

    public void setUnderlined(boolean underlined) {
        this.underlined = underlined;
    }

    public String getText() {
        return text;
    }
}
