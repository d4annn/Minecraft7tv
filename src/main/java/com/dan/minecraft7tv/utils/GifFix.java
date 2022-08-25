package com.dan.minecraft7tv.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GifFix {

    public static void fix(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buf = fis.readAllBytes();
            int pos = 13; // Start after GIF89a & after logical screen descriptor
            int lastDispose = -1;

            if ((buf[10] & 0x80) > 0) {
                //global color table
                int size = (int) Math.round(Math.pow(2, (buf[10] & 0x07) + 1));
                pos += size * 3;
            }

            while (pos < buf.length) {
                BlockType t = getNextBlockType(buf, pos);
                int len = getBlockLength(buf, pos, t);
                if (t == BlockType.EndOfGif) {
                    break;
                } else if (t != BlockType.GraphicsControlExtension) {
                    pos += len;
                } else {
                    int curDisposal = (buf[pos + 3] & 0x1C) >> 2;
                    System.out.println("Current: " + curDisposal);
                    if (lastDispose != -1) {
                        buf[lastDispose] = (byte) ((buf[lastDispose] & 0xE3) + (curDisposal << 2));
                    }
                    lastDispose = pos + 3;
                    pos += len;
                }
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buf);
            fos.close();
        } catch (Exception e) {
            EmoteUtils.logError("Error occurred while chaging gif bytes, check logs for more info.", e.getMessage());
        }
    }

    ;

    private static BlockType getNextBlockType(byte[] buf, int pos) {
        if ((buf[pos] & 0xFF) == 0x3B) {
            return BlockType.EndOfGif;
        }
        if ((buf[pos] & 0xFF) == 0x21 && (buf[pos + 1] & 0xFF) == 0xF9) {
            return BlockType.GraphicsControlExtension;
        }
        if ((buf[pos] & 0xFF) == 0x21 && (buf[pos + 1] & 0xFF) == 0xFF) {
            return BlockType.ApplicationExtension;
        }
        if ((buf[pos] & 0xFF) == 0x21 && (buf[pos + 1] & 0xFF) == 0xFE) {
            return BlockType.CommentExtension;
        }
        if ((buf[pos] & 0xFF) == 0x2C) {
            return BlockType.ImageDescriptor;
        }
        System.out.println("Unknown block type " + buf[pos] + "/" + buf[pos + 1] + " at " + pos);
        System.exit(1);
        return null;
    }

    private static int getBlockLength(byte[] buf, int pos, BlockType t) {
        switch (t) {
            case GraphicsControlExtension -> {
                return buf[pos + 2] + 4; // +2 for header +1 for byte size +1 for block end
            }
            case ApplicationExtension -> {
                return buf[pos + 2] + 5 + buf[pos + 3 + buf[pos + 2]];
            }
            case CommentExtension -> {
                int len = 2;

                while (buf[pos + len] != 0) {
                    int size = buf[pos + len] & 0xFF;
                    len += size + 1;
                }
                return len + 1;
            }
            case ImageDescriptor -> {
                int len = 11;
                if ((buf[pos + 9] & 0x80) == 0x80) {
                    //local color table
                    int size = (int) Math.round(Math.pow(2, (buf[pos + 9] & 0x07) + 1));
                    len += 3 * size;
                }

                while (buf[pos + len] != 0) {
                    int size = buf[pos + len] & 0xFF;
                    len += size + 1;
                }
                return len + 1;
            }
        }
        return -1;
    }

    private enum BlockType {
        GraphicsControlExtension,
        ApplicationExtension,
        CommentExtension,
        ImageDescriptor,
        EndOfGif,
    }
}
