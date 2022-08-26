package com.dan.minecraft7tv.client.utils;

public class LineIdentifier {

    private final String word;
    private final String line;

    public LineIdentifier(String word, String line) {
        this.word = word;
        this.line = line;
    }

    public String getWord() {
        return this.word;
    }

    public String getLine() {
        return this.line;
    }
}

