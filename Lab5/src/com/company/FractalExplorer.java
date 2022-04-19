package com.company;

import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import javax.swing.JFileChooser.*;
import javax.swing.filechooser.*;
import javax.imageio.ImageIO.*;
import java.awt.image.*;

public class FractalExplorer {
    // Размер экрана в пикселях
    private int displaySize;

    private JImageDisplay display;

    private FractalGenerator fractal;

    private Rectangle2D.Double range;

    public FractalExplorer(int size)
    {
        // Задаём размер дисплея
        displaySize = size;

        // Инициализация FractalGenerator
        fractal = new Mandelbrot();
        // Задаём диапазон
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        // Создаём новый дисплей
        display = new JImageDisplay(displaySize, displaySize);
    }

    // Создание окна
    public void createAndShowGUI()
    {
        display.setLayout(new BorderLayout());
        JFrame myFrame = new JFrame("Fractal Explorer");

        myFrame.add(display, BorderLayout.CENTER);

        JButton resetButton = new JButton("Reset");

        //  Инициализация событий для кнопок
        ButtonHandler resetHandler = new ButtonHandler();
        resetButton.addActionListener(resetHandler);

        //  Инициализация нажатия клавиши мыши
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);

        //  Закрытие фреймя при выходе
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //  Создание ComboBox'а
        JComboBox myComboBox = new JComboBox();

        //  Добавления фракталов в ComboBox
        FractalGenerator mandelbrotFractal = new Mandelbrot();
        myComboBox.addItem(mandelbrotFractal);
        FractalGenerator tricornFractal = new Tricorn();
        myComboBox.addItem(tricornFractal);
        FractalGenerator burningShipFractal = new BurningShip();
        myComboBox.addItem(burningShipFractal);

        //  Вывод нужного фрактала при его выборе в ComboBox
        ButtonHandler fractalChooser = new ButtonHandler();
        myComboBox.addActionListener(fractalChooser);

        JPanel myPanel = new JPanel();
        JLabel myLabel = new JLabel("Fractal:");
        myPanel.add(myLabel);
        myPanel.add(myComboBox);
        myFrame.add(myPanel, BorderLayout.NORTH);

        //  Создание и установка кнопки сохраниния и сброса
        JButton saveButton = new JButton("Save");
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(saveButton);
        myBottomPanel.add(resetButton);
        myFrame.add(myBottomPanel, BorderLayout.SOUTH);

        //  Создание экзмепляра ButtonHandler для сохранения фрактала
        ButtonHandler saveHandler = new ButtonHandler();
        saveButton.addActionListener(saveHandler);


        myFrame.pack();
        myFrame.setVisible(true);
        myFrame.setResizable(false);
    }

    private void drawFractal()
    {
        //  Должен проходить через каждый пиксель в изображении
        for (int x=0; x<displaySize; x++){
            for (int y=0; y<displaySize; y++){
                //  Определение координат с плавающей точкой
                double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
                double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);

                int iteration = fractal.numIterations(xCoord, yCoord);
                //  Установление пикселя в чёрный цвет
                if (iteration == -1) {
                    display.drawPixel(x, y, 0);
                }
                //  Установка цвета в зависимости от кол-ва итераций
                else {

                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);

                    display.drawPixel(x, y, rgbColor);
                }

            }
        }
        // Нужно для обновления изображения
        display.repaint();
    }
    //  При нажатии кнопки сброса
    private class ResetHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            fractal.getInitialRange(range);
            drawFractal();
        }
    }
    //  Для приблежения изображения
    private class MouseHandler extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            int x = e.getX();

            double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);

            int y = e.getY();

            double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);

            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            drawFractal();
        }
    }
    private class ButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //  Получение команды
            String command = e.getActionCommand();

            //  Обработка команды на изменение фрактала
            if (e.getSource() instanceof JComboBox) {
                JComboBox mySource = (JComboBox) e.getSource();
                fractal = (FractalGenerator) mySource.getSelectedItem();
                fractal.getInitialRange(range);
                drawFractal();
            }
            //  Обработка команды на сброс
            else if (command.equals("Reset")) {
                fractal.getInitialRange(range);
                drawFractal();
            }
            //  Обработка команды на сохранение
            else if (command.equals("Save")) {

                //  Создаём экземпляр класса для работы с методом showSaveDialog
                JFileChooser chooser = new JFileChooser();

                //  Нужно для сохранения файла в PNG формате
                FileFilter extensionFilter = new FileNameExtensionFilter("PNG Images", "png");
                chooser.setFileFilter(extensionFilter);
                //  Не разрешит пользователю сохранить файл не в PNG формате
                chooser.setAcceptAllFileFilterUsed(false);

                //  Позволяет пользователю выбрать директорию для сохранения файла
                int userSelection = chooser.showSaveDialog(display);

                //  Если возращает APPROVE_OPTION можно продолжить операцию сохранения файла
                if (userSelection == JFileChooser.APPROVE_OPTION) {

                    //  Узнаём директорию, которую выбрал пользователь
                    java.io.File file = chooser.getSelectedFile();

                    String file_name = file.getName();

                    // Пытаемся сохранить файл
                    try {
                        BufferedImage displayImage = display.getImage();
                        javax.imageio.ImageIO.write(displayImage, "png", file);
                    }
                    //  Получение всех ошибок если не удалось сохранить файл
                    catch (Exception exception) {
                        JOptionPane.showMessageDialog(display,
                                exception.getMessage(), "Невозможно сохранить картинку",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                // Если возращает не APPROVE_OPTION завершает обработку события без сохранения
                else return;
            }
        }
    }

    // Точка входа
    public static void main(String[] args)
    {
        // Задаю размер изображения в пикселях
        FractalExplorer displayExplorer = new FractalExplorer(700);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}