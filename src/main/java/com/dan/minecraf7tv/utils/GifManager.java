package com.dan.minecraf7tv.utils;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class GifManager {

    public static void exportGif(BufferedImage[] images, File file) throws IOException {
        List<BufferedImage> imageList = Arrays.asList(images);
        file.getParentFile().mkdirs();
        try {
            ImageWriter gifWriter = getWriter();
            ImageOutputStream ios = getImageOutputStream(file.getAbsolutePath());
            IIOMetadata metadata = getMetadata(gifWriter, 10, true);
            gifWriter.setOutput(ios);
            gifWriter.prepareWriteSequence(null);
            for (BufferedImage img : imageList) {
                IIOImage temp = new IIOImage(img, null, metadata);
                gifWriter.writeToSequence(temp, null);
            }
            gifWriter.endWriteSequence();
        } catch (IOException e) {

        }
    }

    private static ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> itr = ImageIO.getImageWritersByFormatName("gif");
        if (itr.hasNext())
            return itr.next();
        throw new IIOException("GIF writer doesn't exist on this JVM!");
    }

    private static ImageOutputStream getImageOutputStream(String output) throws IOException {
        File outfile = new File(output);
        return ImageIO.createImageOutputStream(outfile);
    }

    private static IIOMetadata getMetadata(ImageWriter writer, int delay, boolean loop) throws IIOInvalidTreeException {
        ImageTypeSpecifier img_type = ImageTypeSpecifier.createFromBufferedImageType(2);
        IIOMetadata metadata = writer.getDefaultImageMetadata(img_type, null);
        String native_format = metadata.getNativeMetadataFormatName();
        IIOMetadataNode node_tree = (IIOMetadataNode) metadata.getAsTree(native_format);
        IIOMetadataNode graphics_node = getNode("GraphicControlExtension", node_tree);
        graphics_node.setAttribute("delayTime", String.valueOf(delay));
        graphics_node.setAttribute("userInputFlag", "FALSE");
        graphics_node.setAttribute("doNotDispose", "FALSE");
        graphics_node.setAttribute("restoreToBackgroundColor", "TRUE");

        if (loop)
            makeLoopy(node_tree);
        metadata.setFromTree(native_format, node_tree);
        return metadata;
    }

    private static IIOMetadataNode getNode(String node_name, IIOMetadataNode root) {
        IIOMetadataNode node = null;
        for (int i = 0; i < root.getLength(); i++) {
            if (root.item(i).getNodeName().compareToIgnoreCase(node_name) == 0) {
                node = (IIOMetadataNode) root.item(i);
                return node;
            }
        }
        node = new IIOMetadataNode(node_name);
        root.appendChild(node);
        return node;
    }


    private static void makeLoopy(IIOMetadataNode root) {
        IIOMetadataNode app_extensions = getNode("ApplicationExtensions", root);
        IIOMetadataNode app_node = getNode("ApplicationExtension", app_extensions);
        app_node.setAttribute("applicationID", "NETSCAPE");
        app_node.setAttribute("authenticationCode", "2.0");
        app_node.setUserObject(new byte[]{1, 0, 0});
        app_extensions.appendChild(app_node);
        root.appendChild(app_extensions);
    }
}
