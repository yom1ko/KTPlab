package com.company;

import java.awt.geom.Rectangle2D;

public class Mandelbrot extends FractalGenerator {
    public static final int MAX_ITERATIONS = 2000;
    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -1.5;
        range.width = 3;
        range.height = 3;
    }

    @Override
    public int numIterations(double x, double y) {
        int iteration = 0;
        double zReal = 0;
        double zImaginary = 0;
        double z = zReal * zReal + zImaginary * zImaginary;
        while (iteration < MAX_ITERATIONS && z < 4) {
            double zRealNew = zReal * zReal - zImaginary * zImaginary + x;
            double zImaginaryNew = 2 * zReal * zImaginary + y;
            zReal = zRealNew;
            zImaginary = zImaginaryNew;
            z = zReal * zReal + zImaginary * zImaginary;
            iteration += 1;
        }

        if (iteration == MAX_ITERATIONS)
        {
            return -1;
        }

        return iteration;
    }
    public String toString() {
        return "Mandelbrot";
    }
}