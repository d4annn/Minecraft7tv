package com.dan.minecraf7tv.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class RenderUtils {


    //bind texture first
    public static void renderImage(Matrix4f matrix, int x0, int y0, int u, int v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        int x1 = x0 + width;
        int y1 = y0 + height;
        int z = 1;

        float u0 = (u + 0.0F) / (float) textureWidth;
        float u1 = (u + (float) regionWidth) / (float) textureWidth;
        float v0 = (v + 0.0F) / (float) textureHeight;
        float v1 = (v + (float) regionHeight) / (float) textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        bufferBuilder.vertex(matrix, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, (float) x0, (float) y0, (float) z).texture(u0, v0).next();

        Tessellator.getInstance().draw();
    }
}