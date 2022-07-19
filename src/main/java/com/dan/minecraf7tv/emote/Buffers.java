package com.dan.minecraf7tv.emote;

import org.lwjgl.PointerBuffer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Buffers {

    private ByteBuffer gifBytes;
    private IntBuffer width;
    private IntBuffer height;
    private PointerBuffer pointerBuffer;
    private IntBuffer channelCounts;
    private int channel;

    public Buffers(ByteBuffer gifBytes, IntBuffer width, IntBuffer height, PointerBuffer pointerBuffer, IntBuffer channelCounts, int channel) {
        this.gifBytes = gifBytes;
        this.width = width;
        this.height = height;
        this.pointerBuffer = pointerBuffer;
        this.channelCounts = channelCounts;
        this.channel = channel;
    }

    public ByteBuffer getGifBytes() {
        return gifBytes;
    }

    public IntBuffer getWidth() {
        return width;
    }

    public IntBuffer getHeight() {
        return height;
    }

    public PointerBuffer getPointerBuffer() {
        return pointerBuffer;
    }

    public IntBuffer getChannelCounts() {
        return channelCounts;
    }

    public int getChannel() {
        return channel;
    }
}
