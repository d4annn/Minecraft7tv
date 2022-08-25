package com.dan.minecraft7tv.emote;

import com.dan.minecraft7tv.config.Config;

public class Frame {

    private final int frameRate;
    private int current;
    private int max;
    //by default to 2 ticks
    private int frame;

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

    }

    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }
}
