package com.dan.minecraf7tv.utils;

import com.convertapi.client.Config;
import com.convertapi.client.ConvertApi;
import com.convertapi.client.Param;
import com.dan.minecraf7tv.emote.Buffers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class FileUtils {

    public static final File FOLDER = new File(new File(MinecraftClient.getInstance().runDirectory, "config").getPath() + "\\Minecraft7tv");
    public static final File TEMP_FODLER = new File(FOLDER.getPath() + "\\temp");

    public static void webpToGif(String in, String out) {
        try {
           ConvertApi.convert("webp", "gif",
                    new Param("Files", Paths.get(in))
            ).get().saveFilesSync(Paths.get(out));
        } catch (IOException | InterruptedException | ExecutionException e) {
        }
    }

    public static InputStream webpToIs(String in) {
        try {
            return ConvertApi.convert("webp", "gif",
                    new Param("Files", Paths.get(in))
            ).get().asStream().get();
        } catch (IOException | InterruptedException | ExecutionException e) {
        }
        return null;
    }

    public static BufferedImage resizeImage(int width, int height, BufferedImage image) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public static void checkConfig() {
        try {
            FOLDER.mkdirs();
            TEMP_FODLER.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * All STB utils here
     */

    public static ByteBuffer getIsBuffer(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        ByteBuffer bb = MemoryUtil.memAlloc(bytes.length);
        bb.put(bytes);
        bb.rewind();
        return bb;
    }

    public static Buffers getGifBuffer(@Nullable NativeImage.Format format, ByteBuffer buffer) throws IOException {
        if (format != null && !format.isWriteable()) {
            throw new UnsupportedOperationException("Don't know how to read format " + format);
        } else {
            MemoryStack memoryStack = MemoryStack.stackPush();
            try {
                System.out.println(STBImage.stbi_failure_reason());
                IntBuffer intBuffer = memoryStack.mallocInt(1);
                IntBuffer intBuffer2 = memoryStack.mallocInt(1);
                IntBuffer intBuffer3 = memoryStack.mallocInt(1);
                IntBuffer intBuffer4 = memoryStack.mallocInt(1);
                System.out.println(STBImage.stbi_failure_reason());
                PointerBuffer pointerBuffer = memoryStack.callocPointer(1);
                System.out.println(STBImage.stbi_failure_reason());
                int form = format == null ? 0 : channel(format);
                ByteBuffer byteBuffer = STBImage.stbi_load_gif_from_memory(buffer, pointerBuffer, intBuffer, intBuffer2, intBuffer3, intBuffer4, form);
                System.out.println(STBImage.stbi_failure_reason());
                if (byteBuffer == null) {
                    System.out.println(STBImage.stbi_failure_reason());
                    throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
                }
                return new Buffers(byteBuffer, intBuffer, intBuffer2, pointerBuffer, intBuffer4, form);
            } catch (Throwable var9) {
                if (memoryStack != null) {
                    try {
                        memoryStack.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }
                }
                throw var9;
            }
        }
    }

    /**
     *
     * @param width             images width
     * @param height            images height
     * @param frame_index       index of the frame you are searching
     * @param channel_count
     * @return where the desired frame starts in the buffer
     * then call MemoryUtil.memAdress(byteBuffer) + offset
     */
    public static long getOffset(int width, int height, int frame_index, int channel_count) {
        return (long) width * height * frame_index * channel_count;
    }

    public static int channel(NativeImage.Format glFormat) {
        switch (glFormat) {
            case LUMINANCE:
                return 1;
            case LUMINANCE_ALPHA:
                return 2;
            case RGB:
                return 3;
            case RGBA:
            default:
                return 4;

        }
    }

    public static NativeImage.Format fromGl(int glFormat) {
        switch (glFormat) {
            case 1:
                return NativeImage.Format.LUMINANCE;
            case 2:
                return NativeImage.Format.LUMINANCE_ALPHA;
            case 3:
                return NativeImage.Format.RGB;
            case 4:
            default:
                return NativeImage.Format.RGBA;
        }
    }

    static {
        Config.setDefaultSecret("ZF7wUzLiJTxTxb0t");
    }
}
