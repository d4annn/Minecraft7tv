package com.dan.minecraft7tv.client.gui.widget;

import com.dan.minecraft7tv.client.interfaces.Action;
import com.dan.minecraft7tv.client.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.List;

public class OptionsWidget extends SettingWidget {

    private List<String> options;
    private Action<String> onSelect;
    private TextFieldWidget text;
    private int def;
    private int current;

    public OptionsWidget(float x, float y, float width, String description, List<String> options, String start, int def, Action<String> onSelect) {
        super(x, y, width, description);
        this.options = options;
        this.onSelect = onSelect;
        int indexOfLongest = 0;
        for(int i = 0; i < options.size(); i++) {
            if(options.get(i).length() > options.get(indexOfLongest).length()) {
                indexOfLongest = i;
            }
        }
        this.text = new TextFieldWidget(super.getX() + 5 , (float) (getY() + (getHeight() - getY()) / 2 - 0.25) - 5, super.getX() + 13 + MinecraftClient.getInstance().textRenderer.getWidth(this.options.get(indexOfLongest)) * 0.6f,
                (float) (getY() + (getHeight() - getY()) / 2 - 0.25) + 5, new Color(10, 10, 29, 100), TextFieldWidget.Filter.UNWRATEABLE);
        this.text.setText(start);
        this.current = this.options.indexOf(start);
        this.def = def;
    }

    private void cycle() {
        if(current >= this.options.size() -1) {
            this.current = 0;
        } else {
            this.current++;
        }
        onValueChanged(this.options.get(this.current));
    }

    private void onValueChanged(String newValue) {
        this.onSelect.execute(newValue);
    }

    @Override
    public void render(MatrixStack matrices) {
        super.render(matrices);
        this.text.render(matrices);
        matrices.push();
        RenderUtils.positionAccurateScale(matrices, 0.45f, this.text.getWidth() + 10, this.getY() + (this.getHeight() - this.getY()) / 2 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.45);
        MinecraftClient.getInstance().textRenderer.draw(matrices, this.getDescrption(), this.text.getWidth(), this.getY() + (this.getHeight() - this.getY()) / 2 - MinecraftClient.getInstance().textRenderer.fontHeight * 0.45f + 4, Color.WHITE.getRGB());
        matrices.pop();
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        this.text.setY((float) (getY() + (getHeight() - getY()) / 2 - 0.25) - 5 + 3);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        this.text.setHeight((float) (getY() + (getHeight() - getY()) / 2 - 0.25) + 5 - 3);
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        if (mouseX >= this.text.getX() - 3 && mouseX <= this.text.getWidth() + 3 && mouseY >= this.text.getY() - 3 && mouseY <= this.text.getHeight() + 3) {
            this.cycle();
            this.text.setText(this.options.get(this.current));
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

    }

    @Override
    public void reset() {
        this.current = def;
        onValueChanged(this.options.get(this.current));
        this.text.setText(this.options.get(this.current));
    }
}
