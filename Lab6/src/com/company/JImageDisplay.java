package com.company;

import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

public class JImageDisplay extends JComponent {
    private BufferedImage image;

    //  Создаём конструктор
    public JImageDisplay(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Dimension imageDimension = new Dimension(width, height);
        super.setPreferredSize(imageDimension);

    }
    public BufferedImage getImage() {
        return image;
    }
    //  Отрисовка изображения
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    }
    //  Окрашивает все пиксели в чёрный
    public void clearImage()
    {
        int[] blankArray = new int[getWidth() * getHeight()];
        image.setRGB(0, 0, getWidth(), getHeight(), blankArray, 0, 1);
    }
    //  Окрашивание пикселя в выбранный цвет
    public void drawPixel(int x, int y, int rgbColor)
    {
        image.setRGB(x, y, rgbColor);
    }
}