package com.dan.minecraft7tv.gui;

import com.dan.minecraft7tv.config.Config;
import com.dan.minecraft7tv.config.Position;
import com.dan.minecraft7tv.config.SliderConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;

public class OptionsScreen extends Screen {

    private Screen parent;

    public OptionsScreen(Screen gui) {
        super(new TranslatableText("options.title"));
        this.parent = gui;
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(new ButtonWidget(5, height - 28, 80, 20, ScreenTexts.DONE, (button) -> {
            this.client.setScreen(parent);
        }));
        int j = this.width / 2 - 155;
        int k = this.height / 6 - 12;
        this.addDrawableChild(new ButtonWidget(j, k, 150, 20, new TranslatableText("text.minecraft7tv.emotes"), button -> {
            this.client.setScreen(new EmotesScreen(this.parent));
        }));
        this.addDrawableChild(new DoubleOptionSliderWidget(
                client.options, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, SliderConfig.EMOTE_SIZE, new ArrayList<>()));
        this.addDrawableChild(CyclingButtonWidget.builder(Position::getText).values(Position.values()).initially(Config.getInstance().textPos).build(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, new TranslatableText("text.minecraft7tv.text_pos"), (button, position) -> {
            Config.getInstance().textPos = position;
            Config.getInstance().saveConfig();
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void close() {
        Config.getInstance().saveConfig();
    }
}
