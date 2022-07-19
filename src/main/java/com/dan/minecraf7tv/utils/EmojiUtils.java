package com.dan.minecraf7tv.utils;

import com.dan.minecraf7tv.emote.Emoji;
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
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class EmojiUtils {

    public static void loadGif(String link, String name) {
        try {
            URL url = new URL(link);
            File file = new File(FileUtils.TEMP_FODLER.getPath() + "\\temp_" + name + ".webp");
            file.mkdirs();
            file.createNewFile();
            Files.copy(url.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            File finalGif = new File(FileUtils.FOLDER.getPath() + "\\" + name);
            finalGif.mkdirs();
            FileUtils.webpToGif(file.getAbsolutePath(), finalGif.getAbsolutePath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage[] divideGif(File gif, String name, File folder) {
        ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
        try {
            ImageInputStream in = ImageIO.createImageInputStream(gif);
            reader.setInput(in);
            BufferedImage[] images = new BufferedImage[reader.getNumImages(true)];
            for (int i = 0, count = reader.getNumImages(true); i < count; i++) {
                BufferedImage image = reader.read(i);
                images[i] = image;
                //256 by default
                ImageIO.write(FileUtils.resizeImage(128, 128, image), "png", new File(folder.getPath() + "\\" + name + i + ".png"));
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
