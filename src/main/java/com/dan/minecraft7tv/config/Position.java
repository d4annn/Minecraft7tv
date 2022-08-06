package com.dan.minecraft7tv.config;

import net.minecraft.text.Text;

public enum Position {
    TOP("Top"),
    CENTER("Center"),
    BOTTOM("Bottom");

    private String name;

    private Position(String name) {
        this.name = name;
    }

    public Text getText() {
        return Text.of(name);
    }
}
