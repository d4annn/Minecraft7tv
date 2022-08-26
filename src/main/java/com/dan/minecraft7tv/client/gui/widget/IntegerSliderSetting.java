package com.dan.minecraft7tv.client.gui.widget;

import com.dan.minecraft7tv.client.interfaces.Action;
import com.dan.minecraft7tv.client.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class IntegerSliderSetting extends SettingWidget {

    private int start;
    private int min;
    private int max;
    private int step;
    private float current;
    private boolean picked;
    private double mouseX;
    private double mouseY;
    private float before;
    private int def;
    private TextFieldWidget value;
    private Action<Integer> onValueChanged;

    public IntegerSliderSetting(float x, float y, float width, int initial, int start, int min, int max, int step, int def, String description, Action<Integer> onValueChanged) {
        super(x, y, width, description);
        this.current = initial;
        this.start = start;
        this.min = min;
        this.max = max;
        this.step = step;
        this.onValueChanged = onValueChanged;
        picked = false;
        TextFieldWidget.Filter filter = TextFieldWidget.Filter.NUMBER_LIMIT;
        filter.setMin(min);
        filter.setMax(max);
        this.def = def;
        value = new TextFieldWidget(getX() + 5 + 28 + 7, (float) (getY() + (getHeight() - getY()) / 2 - 0.25) - 5, getX() + 5 + 28 + 20, (float) (getY() + (getHeight() - getY()) / 2 - 0.25) + 5, new Color(10, 10, 29, 100), filter);
        value.setText(String.valueOf((int) (this.current)));
    }

    @Override
    public void render(MatrixStack matrices) {
        super.render(matrices);
        float a = (float) (getY() + (getHeight() - getY()) / 2 - 0.5);
        float b = (float) (getY() + (getHeight() - getY()) / 2 + 0.5);
        RenderUtils.renderQuad(matrices, getX() + 5, (float) (getY() + (getHeight() - getY()) / 2 - 0.25), getX() + 5 + 28, (float) (getY() + (getHeight() - getY()) / 2 + 0.25), new Color(255, 255, 255, 200).getRGB());
        RenderUtils.renderCircle(matrices, new Color(255, 255, 255, 240), getX() + getCurrentStep() + 5, (float) (getY() + (getHeight() - getY()) / 2), 2, 220);
        this.value.render(matrices);
        matrices.push();
        RenderUtils.positionAccurateScale(matrices, 0.45f, getX() + 5 + 28 + 20 + 10, this.getY() + (this.getHeight() - this.getY()) / 2 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.45);
        MinecraftClient.getInstance().textRenderer.draw(matrices, this.getDescrption(), getX() + 5 + 28 + 20 - 7, this.getY() + (this.getHeight() - this.getY()) / 2 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.45f + 4, Color.WHITE.getRGB());
        matrices.pop();
    }

    private float getCurrentStep() {
        return MathHelper.clamp(getSteps() * (this.current - this.min), 0, 28);
    }

    private float getSteps() {
        return 28 / (float) ((this.max - this.min) * step);
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        this.value.onMouseClick(mouseX, mouseY);
        double x = getX() + getCurrentStep() + 5 - 2;
        double y = (float) (getY() + (getHeight() - getY()) / 2) - 2;
        boolean moving = false;
        if (mouseX >= x && mouseX <= x + 4 && mouseY >= y && mouseY <= y + 4) {
            moving = true;
        } else if (mouseX >= getX() + 5 && mouseX <= getX() + 5 + 28 && mouseY >= (float) (getY() + (getHeight() - getY()) / 2 - 1) && mouseY <= (float) (getY() + (getHeight() - getY()) / 2 + 1)) {
            moving = true;
            float stepped = (float) mouseX - (getX() + 5);
            this.current = stepped / getSteps() + this.min;
            onValueChanged();
        }
        if (moving) {
            this.picked = true;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.before = this.current;
        }
        return true;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY) {
        this.picked = false;
        return false;
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.value.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        this.value.charTyped(chr, modifiers);
        return true;
    }

    public void onValueChanged() {
        this.onValueChanged.execute((int) this.current);
        this.value.setText(String.valueOf((int) (this.current)));
    }

    @Override
    public boolean onMouseMoved(double mouseX, double mouseY) {
        if (this.picked) {
            float steps = (float) (mouseX - this.mouseX);
            if (steps != 0) {
                if (this.before + steps > this.max) {
                    this.current = this.max;
                } else if (this.before + steps < this.min) {
                    this.current = this.min;
                } else {
                    this.current = this.before + steps;
                }
                onValueChanged();
            }
        }

        return false;
    }

    @Override
    public void tick() {
        value.tick();
        if (this.value.getText() != "" && !(Double.parseDouble(this.value.getText()) > Integer.MAX_VALUE)) {
            if (this.current != Integer.parseInt(this.value.getText())) {
                this.current = Integer.parseInt(this.value.getText());
                onValueChanged();
            }
        }
    }

    @Override
    public void reset() {
        this.current = def;
        onValueChanged();
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        this.value.setY((float) (getY() + (getHeight() - getY()) / 2 - 0.25) - 5 + 3);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        this.value.setHeight((float) (getY() + (getHeight() - getY()) / 2 - 0.25) + 5 - 3);
    }
}
