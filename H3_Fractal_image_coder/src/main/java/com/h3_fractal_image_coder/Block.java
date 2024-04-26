package com.h3_fractal_image_coder;

import java.util.Arrays;

public class Block {

    /**
     * values
     */
    int[][] values;


    /**
     * size
     */
    int size;

    /**
     * sum
     */
    int sum;

    /**
     * sumSquared;
     */
    int sumSquared;

    /**
     * Constructor
     *
     * @param size       the size of block
     * @param values     block values
     * @param sum        sum of block values
     * @param sumSquared sum of block values squared
     */
    public Block(int size, int[][] values, int sum, int sumSquared) {
        this.size = size;
        this.values = values;
        this.sum = sum;
        this.sumSquared = sumSquared;
    }

    /**
     * get values
     *
     * @return values
     */
    public int[][] getValues() {
        return values;
    }

    /**
     * get the value at specific coordinates from values matrix
     *
     * @param i line
     * @param j column
     * @return value
     */
    public int getValue(int i, int j) {
        return values[i][j];
    }

    /**
     * get size
     *
     * @return size
     */
    public int getSize() {
        return size;
    }

    /**
     * get sum
     *
     * @return sum
     */
    public int getSum() {
        return sum;
    }

    /**
     * get sum squared
     *
     * @return sum squared
     */
    public int getSumSquared() {
        return sumSquared;
    }

    @Override
    public String toString() {
        return "Block{" +
                ", size=" + size +
                ", sum=" + sum +
                ", sumSquared=" + sumSquared +
                "values=" + Arrays.deepToString(values) +
                '}';
    }
}
