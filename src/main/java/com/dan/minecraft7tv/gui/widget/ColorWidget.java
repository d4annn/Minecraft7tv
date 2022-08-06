package com.dan.minecraft7tv.gui.widget;

import com.dan.minecraft7tv.gui.OptionsScreen;
import com.dan.minecraft7tv.interfaces.Action;
import com.dan.minecraft7tv.utils.EmoteUtils;
import com.dan.minecraft7tv.utils.FileUtils;
import com.dan.minecraft7tv.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorWidget extends SettingWidget {

    private int currentColor;
    private boolean selecting;
    private Identifier palette = new Identifier("minecraft7tv", "textures/paleta.png");
    private int def;
    private Action<Integer> color;

    public ColorWidget(float x, float y, float width, String description, int startColor, int def, Action<Integer> onValueChanged) {
        super(x, y, width, description);
        currentColor = startColor;
        selecting = false;
        color = onValueChanged;
        this.def = def;
        int wid = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int hei = MinecraftClient.getInstance().getWindow().getScaledHeight();
        TextFieldWidget.Filter filter = TextFieldWidget.Filter.NUMBER_LIMIT;
        filter.setMin(0);
        filter.setMax(255);
    }

    @Override
    public void render(MatrixStack matrices) {
        if (null == OptionsScreen.palette)
            super.render(matrices);
        if (!this.selecting) {
            RenderUtils.renderBoxWithRoundCorners(matrices.peek().getModel(), getX() + 10, getY() + (getHeight() - getY()) / 2 - 2, getX() + 28 + 5, getY() + (getHeight() - getY()) / 2 + 2, 3, this.currentColor);
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.45f, getX() + 28 + 5 + 3 + 5, this.getY() + (this.getHeight() - this.getY()) / 2 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.45);
            MinecraftClient.getInstance().textRenderer.draw(matrices, this.getDescrption(), getX() + 28 + 5 + 3 + 5, this.getY() + (this.getHeight() - this.getY()) / 2 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.45f + 4, Color.WHITE.getRGB());
            matrices.pop();
        } else {
            int wid = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int hei = MinecraftClient.getInstance().getWindow().getScaledHeight();
            RenderSystem.setShaderTexture(0, this.palette);
            RenderSystem.texParameter(3553, 10241, 9728);
            RenderUtils.renderImage(matrices.peek().getModel(), wid / 2 - 24, hei / 2 - 50, 0, 0, 100, 100, 256, 256, 256, 256);
        }
    }

    private void onValueChange(int valueChanged) {
            this.currentColor = valueChanged;
            this.color.execute(valueChanged);
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        if (mouseX >= getX() + 10 - 3 && mouseX <= getX() + 28 + 5 + 3 && mouseY >= getY() + (getHeight() - getY()) / 2 - 2 - 3 && mouseY <= getY() + (getHeight() - getY()) / 2 + 2 + 3) {
            this.selecting = true;
            OptionsScreen.palette = this;
        } else if (this.selecting) {
            int wid = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int hei = MinecraftClient.getInstance().getWindow().getScaledHeight();
            if (mouseX >= wid / 2 - 24&& mouseX <= wid / 2 - 24 + 100 && mouseY >= hei / 2 - 50 && mouseY <= hei / 2 - 50 + 100) {
                BufferedImage image = FileUtils.resizeImage(100, 100, EmoteUtils.bufferedImageFromIdentifier(this.palette));
                int a = (int) mouseX - -wid / 2;
                int b = (int) mouseY - (hei / 2 - 70);
                try {
                    this.currentColor = image.getRGB((int) mouseX - (wid / 2- 24), (int) mouseY - (hei / 2 - 50));
                    onValueChange(this.currentColor);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                this.selecting = false;
                OptionsScreen.palette = null;
            }
        }
        return true;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY) {
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
    }

    @Override
    public void reset() {
        this.currentColor = def;
        onValueChange(this.def);
    }
}
