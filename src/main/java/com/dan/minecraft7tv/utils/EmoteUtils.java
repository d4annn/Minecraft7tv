package com.dan.minecraft7tv.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vector4f;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmoteUtils {

    public static void loadGif(String link, String name) {
        try {
            File finalGif = new File(FileUtils.FOLDER.getPath() + "\\" + name + "\\" + name + ".gif");
            if (finalGif.exists()) {
                return;
            }
            new File(FileUtils.FOLDER.getPath() + "\\" + name).mkdirs();
            finalGif.createNewFile();
            URL url = new URL(link);
            File file = new File(FileUtils.TEMP_FODLER.getPath() + "\\temp_" + name + ".webp");
            file.mkdirs();
            file.createNewFile();
            FileUtils.webpToGif(link, file, finalGif);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllCachedEmotes() {
        List<String> result = new ArrayList<>();
        File finalGif = new File(FileUtils.FOLDER.getPath());
        for(String file : Objects.requireNonNull(finalGif.list())) {
            if(!file.contains(".json") && !file.equals("temp")) {
                result.add(file);
            }
        }
        return result;
    }

    public static void deleteEmoteCache(String name) {
        File folder = new File(FileUtils.FOLDER.getPath() + "\\" + name);
        if(folder.exists() && folder.isDirectory()) {
            FileUtils.deleteDirectory(folder);
        }
    }

    public static BufferedImage getFrame(String name, int index) {
        try {
            return ImageIO.read(new File(FileUtils.FOLDER.getPath() + "\\" + name + "\\" + name + index + ".png"));
        } catch (IOException e) {
        }
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
            File  out = new File(FileUtils.FOLDER.getPath() + "\\" + name);
            out.mkdirs();
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

    public static BufferedImage bufferedImageFromIdentifier(Identifier identifier) {
        try {
            InputStream is = MinecraftClient.getInstance().getResourceManager().getResource(identifier).getInputStream();
            BufferedImage image = ImageIO.read(is);
            is.close();
            if (image.getType() != 6) {
                BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), 6);
                Graphics2D g2 = temp.createGraphics();
                g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
                g2.dispose();
                image = temp;
            }
            return image;
        } catch (Exception e) {
            return null;
        }
    }

    public static double[] getMatricesPos(int x, int y, int z, MatrixStack matrices) {
        if (matrices == null || matrices.isEmpty()) return null;
        Matrix4f matrix = matrices.peek().getModel();
       /* float f = x;
        float g = y;
        float h = z;
        float i = 1.0f;
        x = matrix.a00 * f + matrix.a01 * g + matrix.a02 * h + matrix.a03 * i;
        y = matrix.a10 * f + matrix.a11 * g + matrix.a12 * h + matrix.a13 * i;
        z = matrix.a20 * f + matrix.a21 * g + matrix.a22 * h + matrix.a23 * i;
        i = matrix.a30 * f + matrix.a31 * g + matrix.a32 * h + matrix.a33 * i;
        */
        Vector4f vector4f = new Vector4f(x, y, z, 1.0f);
        vector4f.transform(matrix);

        return new double[]{vector4f.getX() + 4, vector4f.getY() + 8, vector4f.getZ()};
    }
}
