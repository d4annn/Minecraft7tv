package com.dan.minecraft7tv.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class EmojiUtils {

    public static void loadGif(String link, String name) {
        try {
            File finalGif = new File(FileUtils.FOLDER.getPath() + "\\" + name);
            if (finalGif.exists()) {
                return;
            }
            finalGif.mkdirs();
            URL url = new URL(link);
            File file = new File(FileUtils.TEMP_FODLER.getPath() + "\\temp_" + name + ".webp");
            file.mkdirs();
            file.createNewFile();
//            Files.copy(url.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            FileUtils.webpToGif(link, file, finalGif);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getFrame(String name, int index) {
        try {
            return ImageIO.read(new File(FileUtils.FOLDER.getPath() + "\\" + name + "\\" + name + index + ".png"));
        } catch (IOException e) {}
        return null;
    }

    public static int getGifSize(File gif) {
        ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
        try {
            ImageInputStream in = ImageIO.createImageInputStream(gif);
            reader.setInput(in);
            return reader.getNumImages(true);
        } catch (IOException | IllegalStateException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public static BufferedImage[] divideGif(File gif, String name, boolean write) {
        ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
        try {
            ImageInputStream in = ImageIO.createImageInputStream(gif);
            reader.setInput(in);
            BufferedImage[] images = new BufferedImage[reader.getNumImages(true)];
            for (int i = 0, count = reader.getNumImages(true); i < count; i++) {
                BufferedImage image = reader.read(i);
                images[i] = FileUtils.resizeImage(128, 128, image);
                if (write) {
                    ImageIO.write(image, "png", new File(FileUtils.FOLDER.getPath() + "\\" + name + "\\" + name + i + ".png"));
                }
            }
            return images;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BufferedImage[0];
    }

    public static void saveBufferedImageAsIdentifier(BufferedImage bufferedImage, Identifier identifier) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", stream);
            byte[] bytes = stream.toByteArray();
            ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
            data.flip();
            NativeImage img = NativeImage.read(data);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(img);
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, texture));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
