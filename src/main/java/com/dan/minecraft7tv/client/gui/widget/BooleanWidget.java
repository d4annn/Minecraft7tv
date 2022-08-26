package com.dan.minecraft7tv.client.gui.widget;

import com.dan.minecraft7tv.client.interfaces.Action;
import com.dan.minecraft7tv.client.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class BooleanWidget extends SettingWidget {

    private Action<Boolean> action;
    private boolean value;
    private long tick;
    private Color color;
    private boolean animation;
    private boolean def;

    public BooleanWidget(float x, float y, float width, String description, boolean startValue, boolean def, Action<Boolean> onValueChanged) {
        super(x, y, width, description);
        this.value = startValue;
        this.action = onValueChanged;
        tick = startValue ? -50 : 0;
        color = startValue ? Color.GREEN : Color.RED;
        animation = false;
        this.def = def;
    }

    @Override
    public void render(MatrixStack matrices) {
        super.render(matrices);
        RenderUtils.renderBoxWithRoundCorners(matrices.peek().getModel(), getX() + 22, getY() + (getHeight() - getY()) / 2, getX() + 28 + 5, getY() + (getHeight() - getY()) / 2, 3, this.color);
        int circleMove = !this.value ? Math.max((int) this.tick, 0) : Math.max(13 - (int) this.tick, 0);
        if(circleMove > 11) circleMove = 11;
        RenderUtils.renderCircle(matrices, Color.WHITE, getX() + 22 + circleMove, getY() + (getHeight() - getY()) / 2, 2, 300);
        matrices.push();
        RenderUtils.positionAccurateScale(matrices, 0.7f, getX() + 5, getY() + (getHeight() - getY()) / 2 - ((MinecraftClient.getInstance().textRenderer.fontHeight) / 2f) * 0.6f - 1);
        String text;
        if(this.color.equals(Color.GREEN)) text = "ON";
        else text = "OFF";
        MinecraftClient.getInstance().textRenderer.draw(matrices, text, getX() + 5, getY() + (getHeight() - getY()) / 2 - ((MinecraftClient.getInstance().textRenderer.fontHeight) / 2f) * 0.6f - 1, this.color.getRGB());

        if(this.getDescrption().equals("Toggles the mod")) {
            MinecraftClient.getInstance().textRenderer.draw(matrices, getDescrption(), getX() + 28 + 5 + 20, this.getY() + (this.getHeight() - this.getY()) / 2 - MinecraftClient.getInstance().textRenderer.fontHeight + 2 + 3, Color.WHITE.getRGB());
            matrices.pop();
        } else {
            matrices.pop();
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.45f, getX() + 28 + 5 + 7, this.getY() + (this.getHeight() - this.getY()) / 2 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.45f + 2);
            MinecraftClient.getInstance().textRenderer.draw(matrices, getDescrption(), getX() + 28 + 5 + 7, this.getY() + (this.getHeight() - this.getY()) / 2 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.45f + 2, Color.WHITE.getRGB());
            matrices.pop();
        }
    }

    private void changeColor() {
        this.color = value ? Color.GREEN : Color.RED;
    }

    private void onValueChange() {
        this.tick = 11;
        this.action.execute(this.value);
        animation = true;
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        if (mouseX >= getX() + 22 - 3 && mouseX <= getX() + 28 + 5 + 3 && mouseY >= getY() + (getHeight() - getY()) / 2 - 3 && mouseY <= getY() + (getHeight() - getY()) / 2 + 3) {
            this.value = !this.value;
            onValueChange();
            return true;
        }
        return false;
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
        if (--this.tick <= 0L && animation) {
            changeColor();
            animation = false;
        }
    }

    @Override
    public void reset() {
        this.value = this.def;
        tick = value ? -50 : 0;
        color = value ? Color.GREEN : Color.RED;
        onValueChange();
    }
}
