package com.company;

import java.awt.geom.Rectangle2D;

public class BurningShip extends FractalGenerator {
    public static final int MAX_ITERATIONS = 2000;
    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2.5;
        range.width = 4;
        range.height = 4;
    }

    @Override
    public int numIterations(double x, double y) {
        int iteration = 0;
        double zReal = 0;
        double zImaginary = 0;
        double z = zReal * zReal + zImaginary * zImaginary;
        while (iteration < MAX_ITERATIONS && z < 4) {
            double zRealNew = zReal * zReal - zImaginary * zImaginary + x;
            double zImaginaryNew = 2 * Math.abs(zReal) * Math.abs(zImaginary) + y;
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
        return "Burning Ship";
    }
}