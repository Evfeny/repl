package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {

    private int maxWidth;
    private int maxHeight;
    public double maxRatio;
    private TextColorSchema schema = new Schema();

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        double ratio;
        int newWidth;
        int newHeight;

        BufferedImage img = ImageIO.read(new URL(url));

        newWidth = img.getWidth();
        newHeight = img.getHeight();

        if ((maxWidth != 0) || (maxHeight != 0)) {
            ratio = (newWidth >= newHeight) ? newWidth / maxWidth : newHeight / maxHeight;
            newWidth = (int) (newWidth / ratio);
            newHeight = (int) (newHeight / ratio);
        }

        if (maxRatio != 0) {
            ratio = Math.round((newHeight >= newWidth) ? newHeight / maxHeight : newWidth / maxWidth);
            if (ratio > maxRatio) {
                throw new BadImageSizeException(ratio, maxRatio);
            }
        }

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        WritableRaster bwRaster = bwImg.getRaster();

        StringBuilder sb = new StringBuilder();

        int[][] imgArr = new int[newHeight][newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int color = bwRaster.getPixel(j, i, new int[3])[0];
                char c = schema.convert(color);

                sb.append(c);
                sb.append(c);
                sb.append(c);
            }
            sb.append("\n");
        }
        String allImg = sb.toString();

        return allImg;
    }

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}
