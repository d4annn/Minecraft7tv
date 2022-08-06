package com.dan.minecraft7tv.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Range;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderUtils {


    //bind texture first
    public static void renderImage(Matrix4f matrix, float x0, float y0, int u, int v, float width, float height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        float x1 = x0 + width;
        float y1 = y0 + height;
        int z = 1;
        float u0 = (u + 0.0F) / (float) textureWidth;
        float u1 = (u + (float) regionWidth) / (float) textureWidth;
        float v0 = (v + 0.0F) / (float) textureHeight;
        float v1 = (v + (float) regionHeight) / (float) textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        bufferBuilder.vertex(matrix, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, (float) x0, (float) y0, (float) z).texture(u0, v0).next();
        Tessellator.getInstance().draw();
    }

    public static void renderQuad(MatrixStack matrices, float x1, float y1, float x2, float y2, int color) {
        BufferBuilder bf = Tessellator.getInstance().getBuffer();
        Matrix4f matrix = matrices.peek().getModel();
        float i = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float h = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(f, g, h, i).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(f, g, h, i).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(f, g, h, i).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(f, g, h, i).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void renderLine(MatrixStack matrices, float x1, float y1, float x2, float y2, int color, float width) {
        BufferBuilder bf = Tessellator.getInstance().getBuffer();
        Matrix4f matrix = matrices.peek().getModel();
        float i = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float h = (float) (color & 255) / 255.0F;
        float x1w = x1 + width;
        float x2w = x2 + width;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(f, g, h, i).next();
        bufferBuilder.vertex(matrix, (float) x1w, (float) y1, 0.0F).color(f, g, h, i).next();
        bufferBuilder.vertex(matrix, (float) x2w, (float) y2, 0.0F).color(f, g, h, i).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(f, g, h, i).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(f, g, h, i).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void renderBoxWithRoundCorners(Matrix4f matrix, double x1, double y1, double x2, double y2, int rad, int color) {
        float i = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float h = (float) (color & 255) / 255.0F;
        renderBoxWithRoundCorners(matrix, x1, y1, x2, y2, rad, new Color(f, g, h, i));
    }

    public static void renderBoxWithRoundCorners(Matrix4f matrix, double x1, double y1, double x2, double y2, int rad, Color color) {
        BufferBuilder bf = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bf.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        int initial = -90;
        double[][] map = new double[][]{
                new double[]{x2, y2},
                new double[]{x2, y1},
                new double[]{x1, y1},
                new double[]{x1, y2}
        };
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            initial += 90;
            for (int r = initial; r < (360 / 4 + initial); r += 4) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bf.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f).next();
            }
        }
        bf.end();
        BufferRenderer.draw(bf);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void renderCircle(MatrixStack matrices, Color color, float x, float y, int rad, @Range(from = 4, to = 360) int segments) {
        segments = MathHelper.clamp(segments, 4, 360);
        Matrix4f matrix = matrices.peek().getModel();
        BufferBuilder bf = Tessellator.getInstance().getBuffer();
        bf.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < 360; i += Math.min((360d / segments), 360 - i)) {
            double radians = Math.toRadians(i);
            double sin = Math.sin(radians) * rad;
            double cos = Math.cos(radians) * rad;
            bf.vertex(matrix, (float) (x + sin), (float) (y + cos), 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        }
        bf.end();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        BufferRenderer.draw(bf);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }

    public static void renderCircleWithoutCenter(MatrixStack matrices, Color color, float x, float y, int rad, int rad2, @Range(from = 4, to = 360) int segments) {
        segments = MathHelper.clamp(segments, 4, 360);
        Matrix4f matrix = matrices.peek().getModel();
        BufferBuilder bf = Tessellator.getInstance().getBuffer();
        bf.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        RenderSystem.lineWidth(12.0f);
        for (int i = 0; i < 360; i += Math.min((360d / segments), 360 - i)) {
            double radians = Math.toRadians(i);
            double sin = Math.sin(radians) * rad;
            double cos = Math.cos(radians) * rad;
            bf.vertex(matrix, (float) (x + sin), (float) (y + cos), 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        }
        for (int i = 0; i < 360; i += Math.min((360d / segments), 360 - i)) {
            double radians = Math.toRadians(i);
            double sin = Math.sin(radians) * rad2;
            double cos = Math.cos(radians) * rad2;
            bf.vertex(matrix, (float) (x + sin), (float) (y + cos), 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).next();
        }
        bf.end();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        BufferRenderer.draw(bf);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }



    //for texts
    public static void positionAccurateScale(MatrixStack stack, float scale, double x, double y) {
        stack.translate(1, 1, 1);
        stack.translate(x, y, 0);
        stack.scale(scale, scale, scale);
        stack.translate(-x, -y, 0);
    }

    /**
     * <p>Parses a single RGBA formatted integer into RGBA format</p>
     *
     * @param in The input color integer
     * @return A length 4 array containing the R, G, B and A component of the color
     */
    public static int[] RGBAIntToRGBA(int in) {
        int red = in >> (8 * 3) & 0xFF;
        int green = in >> (8 * 2) & 0xFF;
        int blue = in >> (8) & 0xFF;
        int alpha = in & 0xFF;
        return new int[]{red, green, blue, alpha};
    }

    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }
}