package com.dan.minecraft7tv.config;

import net.minecraft.text.Text;

public enum Position {
    TOP("top"),
    CENTER("center"),
    BOTTOM("bottom");

    private String name;

    private Position(String name) {
        this.name = name;
    }

    public Text getText() {
        return Text.of(name);
    }
}
