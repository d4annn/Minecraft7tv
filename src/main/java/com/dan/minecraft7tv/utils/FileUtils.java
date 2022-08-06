package com.dan.minecraft7tv.utils;

import com.dan.minecraft7tv.emote.Buffers;
import com.dan.minecraft7tv.emote.EmoteRenderer;
import com.dan.minecraft7tv.emote.RenderableEmote;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;

public class FileUtils {

    public static final File FOLDER = new File(new File(MinecraftClient.getInstance().runDirectory, "config").getPath() + "\\Minecraft7tv");
    public static final File TEMP_FODLER = new File(FOLDER.getPath() + "\\temp");
    public static final File CONFIG = new File(FOLDER.getPath() + "\\config.json");

    public static void webpToGif(String url, File in, File out) {
        try (InputStream is = new URL(url).openStream()) {
            Files.copy(is, in.toPath(), StandardCopyOption.REPLACE_EXISTING);
            convertWebpToFif(in.getAbsolutePath(), out.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void open(String url) {
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public static void convertWebpToFif(String in, String out) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (getOsName().startsWith("Windows")) {
            processBuilder.command("cmd.exe", "/c", String.format("..\\lib\\ImageMagick-7.1.0\\magick.exe %s %s ", in, out));
        } else {
            processBuilder.command("bash", "-c", String.format("..\\lib\\ImageMagick-7.1.0\\magick %s %s ", in, out));
        }
        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Succefully converted");
                //success
            }
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    private static String getOsName() {
        String OS = null;
        OS = System.getProperty("os.name");
        return OS;
    }

    /**
     * @param image
     * @return true if the image is full square, false if it has backround color 0
     */
    public static boolean hasBackround(BufferedImage image) {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getRGB(i, j) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * All STB utils here
     */

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
            CONFIG.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                IntBuffer intBuffer = memoryStack.mallocInt(1);
                IntBuffer intBuffer2 = memoryStack.mallocInt(1);
                IntBuffer intBuffer3 = memoryStack.mallocInt(1);
                IntBuffer intBuffer4 = memoryStack.mallocInt(1);
                PointerBuffer pointerBuffer = memoryStack.callocPointer(1);
                int form = format == null ? 0 : channel(format);
                ByteBuffer byteBuffer = STBImage.stbi_load_gif_from_memory(buffer, pointerBuffer, intBuffer, intBuffer2, intBuffer3, intBuffer4, form);
                if (byteBuffer == null) {
                    System.out.println(STBImage.stbi_failure_reason());
                    throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
                }
                return new Buffers(byteBuffer, intBuffer.get(0), intBuffer2.get(0), pointerBuffer.get(0), intBuffer4.get(0), form);
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

    //Call when closing the game
    public static void freeGifs() {
        for(RenderableEmote emote : EmoteRenderer.getInstance().getEmotes()) {
            STBImage.stbi_image_free(emote.getEmote().getBuffer().gifBytes);
        }
    }

    /**
     * @param width         images width
     * @param height        images height
     * @param frame_index   index of the frame you are searching
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
}
