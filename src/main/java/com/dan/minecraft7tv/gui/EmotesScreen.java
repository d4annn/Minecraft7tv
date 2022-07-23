package com.dan.minecraft7tv.gui;

import com.dan.minecraft7tv.emote.Emoji;
import com.dan.minecraft7tv.emote.EmojiRenderer;
import com.dan.minecraft7tv.emote.RenderableEmoji;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class EmotesScreen extends Screen {

    private Screen parent;
    private TextFieldWidget url;
    private TextFieldWidget name;

    public EmotesScreen(Screen gui) {
        super(new TranslatableText("text.minecraft7tv.emote_options"));
        this.parent = gui;
    }

    @Override
    protected void init() {
        super.init();
        url = this.addDrawableChild(new TextFieldWidget(textRenderer, 100, 100, 150, 20, Text.of("")));
        url.setMaxLength(999);
        name = this.addDrawableChild(new TextFieldWidget(textRenderer, 260, 100, 40, 20, Text.of("")));
        this.addDrawableChild(new ButtonWidget(310, 100, 60, 20, Text.of("Add"), button -> {
            Emoji e = new Emoji(url.getText(), name.getText());
            EmojiRenderer.getInstance().addRenderableEmoji(new RenderableEmoji(e));
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        this.client.setScreen(parent);
    }
}
