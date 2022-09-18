package com.dan.minecraft7tv.client.gui;

import com.dan.minecraft7tv.client.config.Config;
import com.dan.minecraft7tv.client.config.Position;
import com.dan.minecraft7tv.client.emote.EmoteRenderer;
import com.dan.minecraft7tv.client.emote.RenderableEmote;
import com.dan.minecraft7tv.client.gui.widget.*;
import com.dan.minecraft7tv.client.utils.FileUtils;
import com.dan.minecraft7tv.client.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class OptionsScreen extends Screen {

    public static ColorWidget palette;
    public static Tabs currentTab;

    private final Identifier DISCORD_IDENTIFIER = new Identifier("minecraft7tv", "textures/social/discord.png");
    private final Identifier GITHUB_IDENTIFIER = new Identifier("minecraft7tv", "textures/social/github.png");
    private final Identifier PAYPAL_IDENTIFIER = new Identifier("minecraft7tv", "textures/social/paypal.png");
    private final Color backColor = new Color(12, 15, 28, 240);
    private int preScale = 4;
    private List<CategoryWidget> categories;
    private List<FolderWidget> settings;
    private List<EmoteWidget> emotes;
    private Tabs selectedTab;
    private int folder;
    private boolean leftHovered;
    private boolean rightHovered;
    private AddEmoteWidget widget;
    private boolean resetHovered;

    public OptionsScreen() {
        super(new TranslatableText("options.title"));
        categories = new ArrayList<>();
        settings = new ArrayList<>();
        emotes = new ArrayList<>();
        folder = 1;
        leftHovered = false;
        rightHovered = false;
        resetHovered = false;
        preScale = MinecraftClient.getInstance().options.guiScale;
        MinecraftClient.getInstance().options.guiScale = 4;
        MinecraftClient.getInstance().onResolutionChanged();
    }

    @Override
    protected void init() {

        palette = null;
        widget = null;
        super.init();
        this.categories.clear();
        this.settings.clear();
        int tercio = width / 3;
        //All categories
        this.categories.add(new CategoryWidget(tercio - 5, 58, -1, 0.5f, "Emotes", true, (textWidget -> {
            this.onTabChanged(Tabs.fromName(textWidget.getText()));
        })));
        this.categories.add(new CategoryWidget(tercio - 5, 66, -1, 0.5f, "Add Emote", true, (textWidget -> {
            this.onTabChanged(Tabs.fromName(textWidget.getText()));
        })));
        this.categories.add(new CategoryWidget(tercio - 5, 74, -1, 0.5f, "Emote Settings", true, (textWidget -> {
            this.onTabChanged(Tabs.fromName(textWidget.getText()));
        })));
        this.categories.add(new CategoryWidget(tercio - 5, 82, -1, 0.5f, "Server Settings", true, (textWidget -> {
            this.onTabChanged(Tabs.fromName(textWidget.getText()));
        })));
        this.categories.add(new CategoryWidget(tercio - 5, 90, -1, 0.5f, "Settings", true, (textWidget -> {
            this.onTabChanged(Tabs.fromName(textWidget.getText()));
        })));
        onTabChanged(Tabs.EMOTES);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (CategoryWidget text : categories) {
            if (text.onMouseClick((int) mouseX, (int) mouseY)) {
                onTabChanged(Tabs.fromName(text.getText()));
            }
        }
        for (FolderWidget setting : settings) {
            setting.onMouseClick(mouseX, mouseY);
        }
        try {
            for (EmoteWidget emote : emotes) {
                emote.onMouseClick(mouseX, mouseY);
            }
        } catch (ConcurrentModificationException ignored) {
        }

        int a = (int) Math.ceil(EmoteRenderer.getInstance().getEmotes().size() / 9f);
        if (this.rightHovered && this.folder < (int) Math.ceil(EmoteRenderer.getInstance().getEmotes().size() / 9f) && this.selectedTab == Tabs.EMOTES) {
            this.folder++;
            onTabChanged(Tabs.EMOTES);
        }
        if (this.leftHovered && this.folder > 1 && this.selectedTab == Tabs.EMOTES) {
            this.folder--;
            onTabChanged(Tabs.EMOTES);
        }
        if (this.resetHovered) {
            for (FolderWidget sett : settings) {
                for (SettingWidget set : sett.getSettings()) {
                    set.reset();
                }
            }
        }
        int tercio = width / 3;
        int menuWidth = (tercio * 2 + 25) - tercio - 25;
        if (mouseX >= (tercio) + menuWidth + 16 && mouseX <= (tercio) + menuWidth + 16 + 10 && mouseY >= 39 && mouseY <= 49) {
            FileUtils.open("https://github.com/d4annn/Minecraft7tv");
        } else if (mouseX >= (tercio) + menuWidth + 5 && mouseX <= (tercio) + menuWidth + 5 + 10 && mouseY >= 39 && mouseY <= 49) {
            FileUtils.open("https://discord.gg/aUnssJDhcG");
        } else if (mouseX >= (tercio) + menuWidth - 6 && mouseX <= (tercio) + menuWidth - 6 + 10 && mouseY >= 39 && mouseY <= 49) {
            FileUtils.open("https://www.paypal.com/donate/?hosted_button_id=NRWH79PBJHPZJ");
        }
        if (null != this.widget) {
            this.widget.onMouseClick(mouseX, mouseY);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (FolderWidget setting : settings) {
            if (setting.isOpened()) {
                for (SettingWidget sett : setting.getSettings()) {
                    sett.onMouseReleased(mouseX, mouseY);
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (FolderWidget setting : settings) {
            if (setting.isOpened()) {
                setting.onMouseMoved(mouseX, mouseY);
            }
        }
        for (EmoteWidget emote : emotes) {
            emote.onMouseMoved(mouseX, mouseY);
        }
        if (null != this.widget) {
            this.widget.onMouseMoved(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (null != this.widget) {
            this.widget.keyPressed(keyCode, scanCode, modifiers);
        }
        for (EmoteWidget emote : emotes) {
            emote.keyPressed(keyCode, scanCode, modifiers);
        }

        for (FolderWidget setting : settings) {
            if (setting.isOpened()) {
                setting.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (null != this.widget) {
            this.widget.charTyped(chr, modifiers);
        }
        for (EmoteWidget emote : emotes) {
            emote.charTyped(chr, modifiers);
        }
        for (FolderWidget setting : settings) {
            if (setting.isOpened()) {
                setting.charTyped(chr, modifiers);
            }
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void tick() {
        if (null != this.widget) {
            this.widget.tick();
        }
        for (FolderWidget setting : settings) {
            setting.tick();
        }
        for (EmoteWidget emote : emotes) {
            emote.tick();
        }
        int space = 59;
        for (int i = 0; i < this.settings.size(); i++) {
            this.settings.get(i).setY(space);
            this.settings.get(i).setHeight(space + 11);
            for (SettingWidget widget : this.settings.get(i).getSettings()) {
                widget.setY(space + 11 + 2);
                widget.setHeight(space + 11 + 2 + 20);
            }
            if (this.settings.get(i).isOpened()) {
                space += 11 + 20 + 2;
            } else {
                space += +11;
            }
            space += 2;
        }
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Color c = new Color(24, 34, 62, 240);
        int tercio = width / 3;
        RenderUtils.renderBoxWithRoundCorners(matrices.peek().getModel(), tercio - 25, 40, tercio * 2 + 25, height - 40, 7, c);
        int menuWidth = (tercio * 2 + 25) - tercio - 25;
        matrices.push();
        RenderUtils.positionAccurateScale(matrices, 0.7f, (tercio - 25) + menuWidth / 2 + 23, 39);
        drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, "Minecraft7tv by §dDan", (tercio - 25) + menuWidth / 2 + 23, 39, new Color(255, 255, 255, 240).getRGB());
        matrices.pop();
        RenderSystem.setShaderTexture(0, this.DISCORD_IDENTIFIER);
        RenderUtils.renderImage(matrices.peek().getModel(), tercio + menuWidth + 5, 39, 0, 0, 10, 10, 40, 40, 40, 40, 1);
        RenderSystem.setShaderTexture(0, this.GITHUB_IDENTIFIER);
        RenderUtils.renderImage(matrices.peek().getModel(), (tercio) + menuWidth + 16, 39, 0, 0, 10, 10, 40, 40, 40, 40, 1);
        RenderSystem.setShaderTexture(0, this.PAYPAL_IDENTIFIER);
        RenderUtils.renderImage(matrices.peek().getModel(), (tercio) + menuWidth - 5, 40, 0, 0, 8, 8, 40, 40, 40, 40, 1);
        RenderUtils.renderQuad(matrices, (tercio - 25 - 4) + menuWidth / 3, 56, (tercio - 25) + (float) (menuWidth / 3 + 0.3 - 4), height - 56, new Color(255, 255, 255, 240).getRGB());
        if (this.selectedTab == Tabs.EMOTES) {
            float x = ((tercio - 25 - 4) + menuWidth / 3f + 4f + 7);
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.6f, (int) x + 46, 59 + 49 + 49 + 46);
            textRenderer.draw(matrices, Text.of("Page " + this.folder + "/" + (int) Math.ceil(EmoteRenderer.getInstance().getEmotes().size() / 9f)),
                    (int) x + 46, 59 + 49 + 49 + 46, Color.WHITE.getRGB());
            this.leftHovered = mouseX >= (int) x + 46 - 4 && mouseX <= (int) x + 45
                    && mouseY >= 59 + 49 + 49 + 46 && mouseY <= 59 + 49 + 49 + 46 + textRenderer.fontHeight * 0.6f;
            String text = this.leftHovered ? "§n<" : "<";
            textRenderer.draw(matrices, Text.of(text), (int) x + 46 - textRenderer.getWidth("<") * 0.6f - 4, 59 + 49 + 49 + 46, Color.WHITE.getRGB());
            this.rightHovered = mouseX >= (int) (x + 46 + 29 + 0.4) && mouseX <= (int) (x + 46 + 32 + 0.4) && mouseY >= 59 + 49 + 49 + 46 && mouseY <= 59 + 49 + 49 + 46 + textRenderer.fontHeight * 0.6f;
            text = this.rightHovered ? "§n>" : ">";
            textRenderer.draw(matrices, Text.of(text), (int) (x + 46 + textRenderer.getWidth("Page 1/" + (int) Math.ceil(EmoteRenderer.getInstance().getEmotes().size() / 9f)) * 0.6 + 21), 59 + 49 + 49 + 46, Color.WHITE.getRGB());
            matrices.pop();
        }
        if (this.selectedTab == Tabs.SETTINGS || this.selectedTab == Tabs.EMOTE_SETTINGS || this.selectedTab == Tabs.SERVER_SETTINGS) {
            matrices.push();
            RenderUtils.positionAccurateScale(matrices, 0.7f, tercio * 2 + 5, height - 45);
            this.resetHovered = mouseX >= tercio * 2 + 5 && mouseX <= tercio * 2 + 5 + textRenderer.getWidth("Reset") * 0.7f && mouseY >= height - 45 && mouseY <= height - 45 + textRenderer.fontHeight * 0.7;
            textRenderer.draw(matrices, this.resetHovered ? "§nReset" : "Reset", tercio * 2 + 5, height - 45, Color.WHITE.getRGB());
            matrices.pop();
        }
        for (CategoryWidget text : categories) {
            text.setUnderlined(text.isHovered(mouseX, mouseY) || Tabs.fromName(text.getText()) == this.selectedTab);
            text.render(matrices);
        }
        for (FolderWidget setting : settings) {
            if (null != palette) {
                boolean isThis = false;
                for (SettingWidget set : setting.getSettings()) {
                    if (set.equals(palette)) {
                        isThis = true;
                    }
                }
                if (!isThis) setting.setOpened(false);
            }
            setting.render(matrices);
        }
        for (EmoteWidget emote : emotes) {
            emote.render(matrices);
        }
        if (null != this.widget) {
            this.widget.render(matrices);
        }
        super.render(matrices, mouseX, mouseY, delta);
//        int i1 = MinecraftClient.getInstance().getWindow().calculateScaleFactor(client.options.guiScale, MinecraftClient.getInstance().forcesUnicodeFont());
//        MinecraftClient.getInstance().getWindow().setScaleFactor(i1);
    }

    private void onTabChanged(Tabs newTab) {
        currentTab = newTab;
        RenderSystem.disableScissor();
        if (null != this.selectedTab) {
            int preIndex = this.selectedTab.ordinal();
            this.categories.get(preIndex).setUnderlined(false);
            this.categories.get(preIndex).setColor(-1);
        }
        if (newTab != Tabs.EMOTES) {
            this.folder = 1;
        }
        this.widget = null;
        rightHovered = false;
        leftHovered = false;
        settings.clear();
        emotes.clear();
        this.selectedTab = newTab;
        int index = newTab.ordinal();
        this.categories.get(index).setUnderlined(true);
        this.categories.get(index).setColor(new Color(193, 193, 193).getRGB());
        initSettings(newTab);
    }

    private void initSettings(Tabs tab) {
        int tercio = width / 3;
        int menuWidth = (tercio * 2 + 25) - tercio - 25;
        int pages = (this.folder - 1) * 9;
        switch (tab) {
            case EMOTES:
                int xGap = 0;
                int yGap = 0;
                for (RenderableEmote emote : EmoteRenderer.getInstance().getEmotes()) {
                    if (pages == 0) {
                        emotes.add(new EmoteWidget(((tercio - 25 - 4) + menuWidth / 3 + 4f + 7) + xGap, 59 + yGap, emote, (aBoolean -> {
                            onTabChanged(Tabs.EMOTES);
                        })));
                        xGap += 38;
                        if (xGap > 110) {
                            xGap = 0;
                            yGap += 49;
                            if (yGap > 140) {
                                break;
                            }
                        }
                    } else {
                        pages--;
                    }
                }
                break;
            case ADD_EMOTE:
                this.widget = new AddEmoteWidget((tercio - 25 - 4) + menuWidth / 3, 56, tercio * 2 + 25, height - 40);
                break;
            case EMOTE_SETTINGS:
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59, tercio * 2 + 21, 59 + 11, "Emote Size"));
                settings.get(0).addSetting(new IntegerSliderSetting(settings.get(0).getX() + 4, settings.get(0).getHeight() + 2, settings.get(0).getWidth() - 4, Config.getInstance().emoteSize, 15, 8, 40, 1, 15, "Changes the emote size in the chat", (aInt -> {
                    Config.getInstance().emoteSize = aInt;
                })));
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59 + 11, tercio * 2 + 21, 59 + 22, "Fps tick"));
                settings.get(1).addSetting(new BooleanWidget(settings.get(1).getX() + 4, settings.get(1).getHeight() + 2, settings.get(1).getWidth() - 4, "Toggles tick method, more fps means faster", Config.getInstance().fpsTick, false, (bool) -> {
                    Config.getInstance().fpsTick = bool;
                }));
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59 + 22, tercio * 2 + 21, 59 + 33, "Delete cache on remove"));
                settings.get(2).addSetting(new BooleanWidget(settings.get(0).getX() + 4, settings.get(0).getHeight() + 2, settings.get(0).getWidth() - 4, "Delete emote cache when removing the emote", Config.getInstance().deleteCache, false, (bool) -> {
                    Config.getInstance().deleteCache = bool;
                }));
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59 + 33, tercio * 2 + 21, 59 + 44, "Download pop-up"));
                settings.get(3).addSetting(new BooleanWidget(settings.get(0).getX() + 4, settings.get(0).getHeight() + 2, settings.get(0).getWidth() - 4, "Toggles the downloading emote pop-up", Config.getInstance().showDownload, true, (bool) -> {
                    Config.getInstance().showDownload = bool;
                }));
                break;
            case SETTINGS:
                //scissoring for smooth scroll
                double endX = tercio * 2 + 25 + 6;
                double x = tercio - 25 - 6;
                double endY = height - 40 + 6;
                double y = 40 - 6;
                double width = endX - x;
                double height = endY - y;
                width = Math.max(0, width);
                height = Math.max(0, height);
                float d = (float) client.getWindow().getScaleFactor();
                int ay = (int) ((client.getWindow().getScaledHeight() - (y + height)) * d);
                RenderSystem.enableScissor((int) (x * d), ay, (int) (width * d), (int) (height * d));
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59, tercio * 2 + 21, 59 + 11, "Toggle"));
                settings.get(0).addSetting(new BooleanWidget(settings.get(0).getX() + 4, settings.get(0).getHeight() + 2, settings.get(0).getWidth() - 4, "Toggles the mod", Config.getInstance().toggle, true, (bool) -> {
                    Config.getInstance().toggle = bool;
                }));
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59 + 11, tercio * 2 + 21, 59 + 22, "Text Position"));
                settings.get(1).addSetting(new OptionsWidget(settings.get(1).getX() + 4, settings.get(1).getHeight() + 2, settings.get(1).getWidth() - 4, "Changes the position of the text in a emote line", List.of(Position.CENTER.getText().getString(), Position.BOTTOM.getText().getString(), Position.TOP.getText().getString()), Config.getInstance().textPos.getText().getString(), 0, (option) -> {
                    Config.getInstance().textPos = Position.valueOf(option.toUpperCase());
                }));
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59 + 33, tercio * 2 + 21, 59 + 44, "Text color"));
                settings.get(2).addSetting(new ColorWidget(settings.get(2).getX() + 4, settings.get(2).getHeight() + 2, settings.get(2).getWidth() - 4, "Changes the color of the text of the chat", Config.getInstance().chatTextColor, -16777217, (c) -> {
                    Config.getInstance().chatTextColor = c;
                }));
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59 + 44, tercio * 2 + 21, 59 + 55, "Shadow text"));
                settings.get(3).addSetting(new BooleanWidget(settings.get(0).getX() + 4, settings.get(0).getHeight() + 2, settings.get(0).getWidth() - 4, "Toggles shadow in chat text", Config.getInstance().showShadow, true, (bool) -> {
                    Config.getInstance().showShadow = bool;
                }));
                RenderSystem.disableScissor();
                break;
            case SERVER_SETTINGS:
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59, tercio * 2 + 21, 59 + 11, "Auto download"));
                settings.get(0).addSetting(new BooleanWidget(settings.get(0).getX() + 4, settings.get(0).getHeight() + 2, settings.get(0).getWidth() - 4, "Automatically download server emotes on join", Config.getInstance().autoSync, true, (bool) -> {
                    Config.getInstance().toggle = bool;
                }));
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59 + 11, tercio * 2 + 21, 59 + 22, "Max emote amount"));
                settings.get(1).addSetting(new IntegerSliderSetting(settings.get(0).getX() + 4, settings.get(0).getHeight() + 2, settings.get(0).getWidth() - 4, Config.getInstance().emoteAmount, 50, 5, 500, 1, 50, "Changes the maximum amount of emotes", (aInt -> {
                    Config.getInstance().emoteAmount = aInt;
                })));
                settings.add(new FolderWidget((tercio - 25 - 4) + menuWidth / 3 + 4, 59 + 22, tercio * 2 + 21, 59 + 33, "Add server emotes"));
                settings.get(2).addSetting(new BooleanWidget(settings.get(0).getX() + 4, settings.get(0).getHeight() + 2, settings.get(0).getWidth() - 4, "Keep server emotes on leave", Config.getInstance().addServerEmotes, true, (bool) -> {
                    Config.getInstance().addServerEmotes = bool;
                }));
        }
    }

    @Override
    public void onClose() {
        Config.getInstance().saveConfig();
        client.options.guiScale = preScale;
        MinecraftClient.getInstance().onResolutionChanged();
        currentTab = null;
        super.onClose();
    }

    public enum Tabs {

        EMOTES("Emotes"),
        ADD_EMOTE("Add Emote"),
        EMOTE_SETTINGS("Emote Settings"),
        SERVER_SETTINGS("Server Settings"),
        SETTINGS("Settings");

        private String name;

        Tabs(String name) {
            this.name = name;
        }

        static Tabs fromName(String name) {
            for (Tabs tab : Tabs.values()) {
                if (tab.getName().equals(name))
                    return tab;
            }
            return Tabs.EMOTES;
        }

        public String getName() {
            return this.name;
        }
    }
}
