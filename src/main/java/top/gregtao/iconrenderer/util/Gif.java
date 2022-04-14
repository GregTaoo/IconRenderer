package top.gregtao.iconrenderer.util;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

//https://blog.csdn.net/u013314786/article/details/102641073���иĶ�
public class Gif {

    private final ImageWriter writer;
    private final ImageWriteParam params;
    private final IIOMetadata metadata;

    /**
     * ����GIFͼ���췽��
     *
     * @param outputStream �����
     * @param imageType    ͼƬ����
     * @param delay        ����֡����ӳ٣���λ������
     * @param loop         �Ƿ�ѭ������
     */
    private Gif(ImageOutputStream outputStream, int imageType, int delay, boolean loop) throws IOException {
        this.writer = ImageIO.getImageWritersBySuffix("gif").next();
        this.params = writer.getDefaultWriteParam();
        this.metadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromBufferedImageType(imageType), this.params);
        //����Ԫ����
        this.configureRootMetadata(delay, loop);
        //���������
        writer.setOutput(outputStream);
        writer.prepareWriteSequence(null);
    }

    /**
     * ����Ԫ����
     *
     * @param delay �ӳ٣���λ������
     * @param loop  �Ƿ�ѭ������
     */
    private void configureRootMetadata(int delay, boolean loop) throws IIOInvalidTreeException {
        String metaFormatName = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "restoreToBackgroundColor");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", String.valueOf(delay));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
        IIOMetadataNode appExtensionsNode = getNode(root, "ApplicationExtensions");
        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loopContinuously = loop ? 0 : 1;
        child.setUserObject(new byte[]{0x1, (byte) (loopContinuously & 0xFF), (byte) (0)});
        appExtensionsNode.appendChild(child);
        metadata.setFromTree(metaFormatName, root);
    }

    /**
     * ��ȡԪ���ݽڵ�
     *
     * @param rootNode ���ڵ�
     * @param nodeName �ڵ�����
     * @return IIOMetadataNode
     */
    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return (node);
    }

    /**
     * ��ͼƬ����д������֡
     *
     * @param img ͼƬ����
     */
    public void writeToSequence(RenderedImage img) throws IOException {
        writer.writeToSequence(new IIOImage(img, null, metadata), params);
    }

    public void close() throws IOException {
        writer.endWriteSequence();
    }

    /**
     * ������ͼ����ת����gifͼ��д�������
     *
     * @param images       ��������ͼ
     * @param outputStream ͼ�������
     * @param delay        ֡�ӳ٣���λ������
     * @param loop         �Ƿ�ѭ������
     * @param width        ���
     * @param height       �߶�
     */
    public static void convert(BufferedImage[] images, ImageOutputStream outputStream, int delay, boolean loop, Integer width, Integer height) {
        //ͼ������
        if (images[0] == null) return;
        int imageType = images[0].getType();
        //���Ų���
        double sx = width == null ? 1.0 : ((double) width / images[0].getWidth());
        double sy = height == null ? 1.0 : ((double) height / images[0].getHeight());
        AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(sx, sy), null);
        try {
            Gif gif = new Gif(outputStream, imageType, delay, loop);
            for (BufferedImage image : images) {
                if (image == null) continue;
                gif.writeToSequence(op.filter(image, null));
            }
            gif.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("GIF convert error", e);
        }
    }

    /**
     * ������ͼ����ת����gifͼ��д�������
     *
     * @param images       ��������ͼ
     * @param outputStream ͼ�������
     * @param delay        ֡�ӳ٣���λ������
     * @param loop         �Ƿ�ѭ������
     */
    public static void convert(BufferedImage[] images, ImageOutputStream outputStream, int delay, boolean loop) {
        Gif.convert(images, outputStream, delay, loop, null, null);
    }

    /**
     * ������ͼת����gifͼ
     *
     * @param imagePaths ��������ͼ·��
     * @param gifPath    gifͼ·��
     * @param delay      ֡�ӳ٣���λ������
     * @param loop       �Ƿ�ѭ������
     * @param width      ���
     * @param height     �߶�
     */
    public static void convert(String[] imagePaths, String gifPath, int delay, boolean loop, Integer width, Integer height) {
        try {
            BufferedImage[] images = new BufferedImage[imagePaths.length];
            for (int i = 0; i < imagePaths.length; i++) {
                images[i] = ImageIO.read(new File(imagePaths[i]));
            }
            FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(new File(gifPath));
            convert(images, fileImageOutputStream, delay, loop, width, height);
        } catch (Exception e) {
            throw new RuntimeException("GIF convert error", e);
        }
    }

    public static void convert(BufferedImage[] images, String gifPath, int delay, boolean loop, Integer width, Integer height) {
        try {
            FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(new File(gifPath));
            convert(images, fileImageOutputStream, delay, loop, width, height);
        } catch (Exception e) {
            throw new RuntimeException("GIF convert error", e);
        }
    }

    /**
     * ������ͼת����gifͼ
     *
     * @param imagePaths ��������ͼ·��
     * @param gifPath    gifͼ·��
     * @param delay      ֡�ӳ٣���λ������
     * @param loop       �Ƿ�ѭ������
     */
    public static void convert(String[] imagePaths, String gifPath, int delay, boolean loop) {
        Gif.convert(imagePaths, gifPath, delay, loop, null, null);
    }

}