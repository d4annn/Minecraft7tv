package com.dan.minecraf7tv.emote;

public class Frame {

    private int current;
    private int max;
    //by default to 2 ticks
    private int frame;
    private final int frameRate;

    public Frame(int current, int max, int frameRate) {
        this.current = current;
        this.max = max;
        this.frameRate = frameRate;
    }

    public void nextFrame() {
        nextFrame(1);
    }

    //frames start at 0
    public void nextFrame(int frames) {
        if (current + frames <= max) {
            this.current += frames;
            frame = 0;
            return;
        }
        current = (current + frames) - max - 1;
        frame = 0;
    }

    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }
}
