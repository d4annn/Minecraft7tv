package com.dan.minecraft7tv.gui.widget;

import com.dan.minecraft7tv.gui.OptionsScreen;
import com.dan.minecraft7tv.utils.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class TextFieldWidget implements Widget {

    private float x;
    private float y;
    private float width;
    private float height;
    private Color color;
    private String text;
    private boolean selecting;
    private boolean selected;
    private TextRenderer tr;
    private int selectedChar;
    private int tick;
    private Filter filter;

    public TextFieldWidget(float x, float y, float width, float height, Color color, Filter filter) {
        this.x = x + 3;
        this.y = y + 3;
        this.width = width - 3;
        this.height = height - 3;
        this.color = color;
        this.selecting = false;
        selected = false;
        tr = MinecraftClient.getInstance().textRenderer;
        tick = 0;
        this.filter = filter;
        this.text = "";
    }


    public String getSelectedText() {
        if (this.selecting) return this.text;
        return "";
    }

    @Override
    public void render(MatrixStack matrices) {
        RenderUtils.renderBoxWithRoundCorners(matrices.peek().getModel(), this.x, this.y, this.width, this.height, 3, color);
        int space = (int) (this.height - this.y);
        space -= tr.fontHeight;
        space /= 2;
        matrices.push();
        RenderUtils.positionAccurateScale(matrices, 0.6f, this.x - 1, this.y + space);
        tr.draw(matrices, text, this.x - 1, this.y + space + 1.5f, Color.WHITE.getRGB());
        matrices.pop();
        if (this.selected && tick > 15) {
            StringBuilder s = new StringBuilder(text);
            if (this.selectedChar == -1) {
                s.setLength(0);
            } else {
                if (!this.text.isEmpty())
                    s.delete(this.selectedChar, this.text.length() - 1);
            }
            float gap = this.selectedChar == -1 ? -0.1f : 0;
            float gap1 = s.length() == text.length() || s.length() == 0 ? -0.1f : 0;
            RenderUtils.renderQuad(matrices, this.x - 1 + (tr.getWidth(s.toString())) * 0.6f + 0.6f + gap, this.y + space + 1.5f, this.x - 1 + (tr.getWidth(s.toString())) * 0.6f + 1.2f + gap + gap1, this.y + space + 1.5f + tr.fontHeight * 0.6f, new Color(255, 255, 255, 255).getRGB());
        }
        String select = getSelectedText();
        if (!select.isEmpty()) {
            float x1 = (this.x + 1) - 2;
            float y1 = this.y + space + 1.5f - 1;
            float x2 = x1 + tr.getWidth(text) * 0.6f + 2;
            float y2 = y1 + 2 + tr.fontHeight * 0.6f;
            Tessellator i = Tessellator.getInstance();
            BufferBuilder bufferBuilder = i.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionShader);
            RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
            RenderSystem.disableTexture();
            RenderSystem.enableColorLogicOp();
            RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            bufferBuilder.vertex((double) x1, (double) y2, 0.0D).next();
            bufferBuilder.vertex((double) x2, (double) y2, 0.0D).next();
            bufferBuilder.vertex((double) x2, (double) y1, 0.0D).next();
            bufferBuilder.vertex((double) x1, (double) y1, 0.0D).next();
            i.draw();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableColorLogicOp();
            RenderSystem.enableTexture();
        }
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        if (isHovered(mouseX, mouseY)) {
            this.selected = true;
            this.selectedChar = this.text.length() -1;
        } else {
            this.selecting = false;
            this.selected = false;
            processPattern(true, null);
        }
        return true;
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        if (mouseX >= x - 3 && mouseX <= width + 3 && mouseY >= y - 3 && mouseY <= height + 3) {
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(!this.selected) return false;
        if (Screen.isSelectAll(keyCode)) {
            this.selecting = true;
        } else if (Screen.isCopy(keyCode)) {
            //control c
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            this.tick = 21;
        } else if (Screen.isPaste(keyCode)) {
            //control v
            String t = MinecraftClient.getInstance().keyboard.getClipboard();
            if (processPattern(false, t)) {
                StringBuilder sb = new StringBuilder(this.text);
                sb.insert(this.selectedChar, t);
                this.text = sb.toString();
                this.selectedChar += t.length();
                this.tick = 21;
                this.selecting = false;
            }
        } else if (Screen.isCut(keyCode)) {
            //Control x
            if (!getSelectedText().isEmpty()) {
                MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
                this.text = "";
                this.selectedChar = 0;
                this.tick = 21;
            }
            this.selecting = false;
        } else {
            switch (keyCode) {
                case 259:
                    if (this.text.length() != 0 && this.selectedChar != -1) {
                        StringBuilder stringBuilder = new StringBuilder(this.text);
                        if (!getSelectedText().isEmpty()) {
                            this.text = "";
                            this.selectedChar = -1;
                            this.tick = 21;
                        } else {
                            stringBuilder.deleteCharAt(this.selectedChar);
                            this.text = stringBuilder.toString();
                            this.selectedChar--;
                            this.tick = 21;
                        }
                        this.selecting = false;
                    }
                    break;
                case 268:
                    this.selectedChar = -1;
                    break;
                case 269:
                    this.selectedChar = text.length() - 1;
                    break;
                case 263:
                    if (this.selectedChar != -1) {
                        this.selectedChar--;
                        this.tick = 21;
                    }
                    break;
                case 262:
                    if (!(this.selectedChar >= this.text.length() - 1)) {
                        this.selectedChar++;
                        this.tick = 21;
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (SharedConstants.isValidChar(chr)) {
            if (this.selected && processPattern(false, Character.toString(chr))) {
                StringBuilder sb = new StringBuilder(this.text);
                if (!getSelectedText().isEmpty()) {
                    this.text = Character.toString(chr);
                    this.selecting = false;
                    this.selectedChar = -1;
                } else {
                    if (this.text.isEmpty()) {
                        this.text = Character.toString(chr);
                    } else {
                        sb.insert(this.selectedChar + 1, Character.toString(chr));
                        this.text = sb.toString();
                    }
                    this.selectedChar++;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseMoved(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void tick() {
        if (tick < 30) {
            tick++;
        } else {
            tick = 0;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private boolean processPattern(boolean textC, String processable) {
        String text = this.text;
        if (textC) {
            switch (this.filter) {
                case NUMBER_LIMIT:
                    if (this.text.isEmpty()) break;
                    if (Double.parseDouble(this.text) > Integer.MAX_VALUE) {
                        this.text = String.valueOf(this.filter.getMax());
                        break;
                    }
                    int textN = Integer.parseInt(this.text);
                    if (this.filter.getMin() > textN) {
                        this.text = String.valueOf(this.filter.getMin());
                    } else if (this.filter.getMax() < textN) {
                        this.text = String.valueOf(this.filter.getMax());
                    }
            }
        } else {
            if (this.filter.isNumber()) {
                try {
                    int number = Integer.parseInt(processable);
                    return true;
                } catch (NumberFormatException e) {
                }
            }
            if (this.filter.isWord()) {
                try {
                    int number = Integer.parseInt(processable);
                    return false;
                } catch (NumberFormatException e) {
                    return true;
                }
            }
        }
        return false;
    }

    public static enum Filter {

        NUMBER(true),
        NUMBER_LIMIT(-1, -1),
        WORD(false),
        WORD_FORMAT(""),
        UNWRATEABLE(false, false),
        NO_FILTER(true, true);

        private int min;
        private int max;
        private boolean number;
        private boolean word;
        private String pattern;

        private Filter(boolean number, boolean word) {
            this.number = number;
            this.word = word;
        }

        private Filter(boolean number) {
            this.number = number;
            this.word = !number;
        }

        private Filter(String pattern) {
            this.pattern = pattern;
            this.number = false;
            this.word = true;
        }

        private Filter(int max, int min) {
            this.min = min;
            this.max = max;
            this.word = false;
            this.number = true;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public boolean isNumber() {
            return number;
        }

        public void setNumber(boolean number) {
            this.number = number;
        }

        public boolean isWord() {
            return word;
        }

        public void setWord(boolean word) {
            this.word = word;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
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
}

