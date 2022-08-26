package com.dan.minecraft7tv.client.gui.widget;

import net.minecraft.client.util.math.MatrixStack;

public interface Widget {

    void render(MatrixStack matrices);

    boolean onMouseClick(double mouseX, double mouseY);

    boolean isHovered(double mouseX, double mouseY);

    boolean keyPressed(int keyCode, int scanCode, int modifiers);

    boolean charTyped(char chr, int modifiers);

    boolean onMouseMoved(double mouseX, double mouseY);

    void tick();
}
