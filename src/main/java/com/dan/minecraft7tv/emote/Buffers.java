package com.dan.minecraft7tv.emote;

import org.lwjgl.PointerBuffer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Buffers {

    public final ByteBuffer gifBytes;
    public final int width;
    public final int height;
    public final long pointerBuffer;
    public final int channelCounts;
    public final int channel;

    public Buffers(ByteBuffer gifBytes, int    width, int  height, long pointerBuffer, int channelCounts, int channel) {
        this.gifBytes = gifBytes;
        this.width = width;
        this.height = height;
        this.pointerBuffer = pointerBuffer;
        this.channelCounts = channelCounts;
        this.channel = channel;
    }
}
