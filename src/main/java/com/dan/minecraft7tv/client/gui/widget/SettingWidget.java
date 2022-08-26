package com.dan.minecraft7tv.client.gui.widget;

import com.dan.minecraft7tv.client.utils.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public abstract class SettingWidget implements Widget{

    private float x;
    private float y;
    private float width;
    private float height;
    private String descrption;

    public SettingWidget(float x, float y, float width, String description) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = y + 20;
        this.descrption = description;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public String getDescrption() {
        return descrption;
    }

    public float getHeight() {
        return height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void render(MatrixStack matrices) {
        Color backColor = new Color(19, 19, 55, 150);
        RenderUtils.renderQuad(matrices, this.x, this.y, this.width, this.height, backColor.getRGB());
    }

    public abstract boolean onMouseClick(double mouseX, double mouseY);

    public abstract boolean onMouseReleased(double mouseX, double mouseY);

    public abstract boolean isHovered(double mouseX, double mouseY);

    public abstract boolean onMouseMoved(double mouseX, double mouseY);

    public abstract void tick();

    public abstract void reset();
}
