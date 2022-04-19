package com.company;

import com.company.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FractalExplorer {
    private int displaySize; //«размер экрана»
    private JImageDisplay imageDisplay; //для обновления отображения в разных методах в процессе вычисления фрактала.
    private FractalGenerator fractalGenerator;// ссылка на базовый класс для отображения других видов фракталов в будущем.
    private Rectangle2D.Double range;// диапазона комплексной плоскости, которая выводится на экран.
    private JComboBox comboBox;
    private int rowsRemaining;
    private JButton buttonReset;
    private JButton buttonSave;

    private FractalExplorer(int displaySize) //конструктор, который принимает значение
//   размера отображения в качестве аргумента, затем сохраняет это значение в
    //  соответствующем поле, а также инициализирует объекты диапазона и
    //фрактального генератора
    {
        this.displaySize = displaySize;
        this.fractalGenerator = new Mandelbrot();
        this.range = new Rectangle2D.Double(0, 0, 0, 0);
        fractalGenerator.getInitialRange(this.range);
    }
    // задание интерфейса
    public void createAndShowGUI() {
        JFrame frame = new JFrame("Fractal Generator");
        JPanel jPanel_1 = new JPanel();
        JPanel jPanel_2 = new JPanel();
        JLabel label = new JLabel("Fractal:");

        imageDisplay = new JImageDisplay(displaySize, displaySize);
        imageDisplay.addMouseListener(new MouseListener());

        // список
        comboBox = new JComboBox();
        comboBox.addItem(new Mandelbrot());
        comboBox.addItem(new Tricorn());
        comboBox.addItem(new BurningShip());
        comboBox.addActionListener(new ActionHandler());

        // кнопка reset
        buttonReset = new JButton("Reset");
        buttonReset.setActionCommand("Reset");
        buttonReset.addActionListener(new ActionHandler());

        // кнопка сохранить
        buttonSave = new JButton("Save");
        buttonSave.setActionCommand("Save");
        buttonSave.addActionListener(new ActionHandler());//События от кнопки «Save Image» также должны обрабатываться
        // реализацией ActionListener.

        jPanel_1.add(label, BorderLayout.CENTER);
        jPanel_1.add(comboBox, BorderLayout.CENTER);
        jPanel_2.add(buttonReset, BorderLayout.CENTER);
        jPanel_2.add(buttonSave, BorderLayout.CENTER);

        frame.setLayout(new java.awt.BorderLayout());//содержимого окна
        frame.add(imageDisplay, BorderLayout.CENTER); //добавьте объект отображения изображения в позици BorderLayout.CENTER
        frame.add(jPanel_1, BorderLayout.NORTH);//кнопки в позиции
        frame.add(jPanel_2, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //операцию закрытия окна по умолчанию

        frame.pack(); //правильное рахмещение содержимого
        frame.setVisible(true); //сделают его видимым
        frame.setResizable(false);// запрет изменения размеров окна
    }

    // отрисовка фрактала в JImageDisplay, вывод на эеран
    private void drawFractal() {
        enableGUI(false); //интерфейс отключить
        rowsRemaining = displaySize;//значение «rows remaining» равным общему количеству строк
        for (int i = 0; i < displaySize; i++) {
            FractalWorker drawRow = new FractalWorker(i);
            drawRow.execute();
        }
    }
    // включение и отключение кнопки с выпадающим списком
    public void enableGUI(boolean b) {
        buttonSave.setEnabled(b);//метод Swing setEnabled(boolean).
        buttonReset.setEnabled(b);// обновляет состояние
        comboBox.setEnabled(b);
    }

    //отработчик всех кнопок
    public class ActionHandler implements ActionListener {//внутренний класс для обработки событий

        // java.awt.event.ActionListener от кнопки сброса
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Reset")) {
                // перерисовка фрактала
                fractalGenerator.getInitialRange(range);
                drawFractal();
            } else if (e.getActionCommand().equals("Save")) {
                // сохранение
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("PNG Images", "png");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int t = fileChooser.showSaveDialog(imageDisplay);
                if (t == JFileChooser.APPROVE_OPTION) {
                    try {
                        ImageIO.write(imageDisplay.getImage(), "png", fileChooser.getSelectedFile());//директория сохранения
                    } catch (NullPointerException | IOException ee) {
                        JOptionPane.showMessageDialog(imageDisplay, ee.getMessage(), "Cannot save image", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                fractalGenerator = (FractalGenerator) comboBox.getSelectedItem();
                range = new Rectangle2D.Double(0, 0, 0, 0);
                fractalGenerator.getInitialRange(range);
                drawFractal();
            }
        }
    }
    public class MouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            double x = FractalGenerator.getCoord(range.x, range.x + range.width, displaySize, e.getX());//пиксельные координаты щелчка
            double y = FractalGenerator.getCoord(range.y, range.y + range.width, displaySize, e.getY());
            fractalGenerator.recenterAndZoomRange(range, x, y, 0.5);//метод генератора recenterAndZoomRange() с координатами, по которым
            //щелкнули, и масштабом 0.5.
            drawFractal();
        }
    }
    public static void main(String[] args)
    {
        FractalExplorer fractalExplorer = new FractalExplorer(600);//Инициализировать новый экземпляр класса FractalExplorer с
        //  размером отображения 800.
        fractalExplorer.createAndShowGUI();//Вызовите метод createAndShowGUI () класса FractalExplorer.
        fractalExplorer.drawFractal();
    }
    //Класс FractalWorker будет отвечать за вычисление значений цвета для
//одной строки фрактала,
    public class FractalWorker extends SwingWorker<Object, Object> {//подкласс SwingWorker с именем FractalWorker, который
        // будет внутренним классом FractalExplorer


        private int y_coord;
        private int[] rgb;//массив чисел типа int для хранения
        //вычисленных значений RGB для каждого пикселя в этой строке

        //Конструктор
        public FractalWorker(int y_coord) {
            this.y_coord = y_coord;//сохранение
        }
        //будет сохранить каждое значение RGB
        protected Object doInBackground() throws Exception {
            rgb = new int[displaySize];
            for (int i = 0; i < displaySize; i++) {
                int count = fractalGenerator.numIterations(FractalGenerator.getCoord(range.x, range.x + range.width, displaySize, i),
                        FractalGenerator.getCoord(range.y, range.y + range.width, displaySize, y_coord));
                if (count == -1)
                    rgb[i] = 0;
                else {
                    double hue = 0.7f + (float) count / 200f;
                    int rgbColor = Color.HSBtoRGB((float) hue, 1f, 1f);
                    rgb[i] = rgbColor;
                }
            }

            return null;
        }

        protected void done() {
            for (int i = 0; i < displaySize; i++) {
                imageDisplay.drawPixel(i, y_coord, rgb[i]);
            }
            imageDisplay.repaint(0, 0, y_coord, displaySize, 1);//перерисовка
            rowsRemaining--;
            if (rowsRemaining == 0)
                enableGUI(true);
        }
    }
}