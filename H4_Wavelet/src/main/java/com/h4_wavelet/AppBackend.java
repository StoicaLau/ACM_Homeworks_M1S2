package com.h4_wavelet;

import com.h4_wavelet.mytools.BitReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Constructor class
 */
public class AppBackend {

    /**
     * values of original image
     */
    double[][] originalImageValue;

    /**
     * values of wavelet image
     */
    double[][] waveletImageValue;

    /**
     * Constructor
     *
     * @param file the original image file
     * @throws IOException an exception
     */
    public AppBackend(File file) throws IOException {
        double[][] imageValues = new double[512][512];
        BitReader bitReader = new BitReader(file);
        int headerInfoSize = 1078;
        int imageValuesSize = (int) file.length() - headerInfoSize;

        for (int i = 0; i < headerInfoSize; i++) {
            bitReader.readNBits(8);
        }

        for (int i = 0; i < imageValuesSize; i++) {
            int value = bitReader.readNBits(8);
            imageValues[511 - i / 512][i % 512] = value;
        }
        this.originalImageValue = imageValues;
        this.waveletImageValue = imageValues;
        bitReader.close();
    }

    /**
     * get Original Image Value
     *
     * @return the values of the original image
     */
    public double[][] getOriginalImageValue() {
        return originalImageValue;
    }

    /**
     * get the wavelet image values
     *
     * @return the values of wavelet image
     */
    public double[][] getWaveletImageValue() {
        return waveletImageValue;
    }

    /**
     * draw a image depending on the type of the image
     *
     * @param type the type of the image
     */
    public WritableImage getImage(String type) {
        double[][] imageValues = new double[512][512];
        if (type.equals("Original")) {
            imageValues = this.originalImageValue;
        } else if (type.equals("Wavelet")) {
            imageValues = this.waveletImageValue;
        }

        WritableImage writableImage = new WritableImage(512, 512);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                double value = imageValues[i][j];
                Color grayColor = Color.gray(value / 255.0);
                pixelWriter.setColor(j, i, grayColor);
            }
        }

        return writableImage;

    }

    /**
     * get wavelet Level Image
     *
     * @param levelH
     * @param levelV
     * @param scale
     * @param offset
     * @return
     */
    public WritableImage getWaveletLvlImage(int levelH, int levelV, double scale, double offset) {
        int lengthH = (int) Math.pow(2, 10 - levelH - 1);
        int lengthV = (int) Math.pow(2, 10 - levelV - 1);
        double[][] imageValues = this.waveletImageValue;
        WritableImage writableImage = new WritableImage(512, 512);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < lengthV; i++) {
            for (int j = 0; j < lengthH; j++) {
                double value = imageValues[i][j];
                Color grayColor = Color.gray(value / 255.0);
                pixelWriter.setColor(j, i, grayColor);
            }
        }

        if (lengthV != 512) {
            for (int i = lengthV; i < 512; i++) {
                for (int j = 0; j < 512; j++) {
                    double value = normalizePixelValue(scale * imageValues[i][j] + offset);
                    Color grayColor = Color.gray(value / 255.0);
                    pixelWriter.setColor(j, i, grayColor);
                }
            }
        }

        if (lengthH != 512) {
            for (int i = 0; i < 512; i++) {
                for (int j = lengthH; j < 512; j++) {
                    double value = normalizePixelValue(scale * imageValues[i][j] + offset);
                    Color grayColor = Color.gray(value / 255.0);
                    pixelWriter.setColor(j, i, grayColor);
                }
            }
        }

        return writableImage;

    }

    /**
     * bringing the interval between 0 and length
     *
     * @param value  the value
     * @param length the length
     * @return the normalize value
     */
    private int normalizeValueDependingOnLength(int value, int length) {
        if (value < 0)
            return -1 * value;
        if (value >= length) {
            value = value - (length - 1);
            value = (length - 1) - value;
            return value;
        }
        return value;
    }

    /**
     * bringing the interval between 0 and length
     *
     * @param value the value
     * @return the normalize value
     */
    private double normalizePixelValue(double value) {
        if (value > 255) {
            return 255;
        }
        if (value < 0) {
            return 0;
        }
        return value;
    }


    /**
     * apply analysis horizontal algorithm
     * level 1 512
     * level 2 256
     * level 3 128
     * level 4 64
     * level 5 32
     *
     * @param level the level
     */
    public void analyzeHorizontal(int level) {
        int length = (int) Math.pow(2, 10 - level);
        for (int i = 0; i < 512; i++) {
            double[] lineValues = this.waveletImageValue[i];
            double[] result = this.analyzeValues(lineValues, length);
            System.out.println(Arrays.toString(lineValues));
            for (int j = 0; j < length; j++) {
                this.waveletImageValue[i][j] = result[j];
            }
        }
    }

    /**
     * apply analysis vertical algorithm
     *
     * @param level the level
     */
    public void analyzeVertical(int level) {
        int length = (int) Math.pow(2, 10 - level);

        for (int j = 0; j < 512; j++) {
            double[] columnValues = new double[512];
            for (int i = 0; i < 512; i++) {
                columnValues[i] = this.waveletImageValue[i][j];
            }
            double[] result = this.analyzeValues(columnValues, length);
            for (int i = 0; i < length; i++) {
                this.waveletImageValue[i][j] = result[i];
            }
        }
    }

    /**
     * apply analysis algorithm
     *
     * @param values values
     * @param length the length
     * @return the result of the algorithm
     */
    private double[] analyzeValues(double[] values, int length) {
        double[] analysisL = new double[length];
        double[] analysisH = new double[length];

        for (int i = 0; i < length; i++) {
            int[] indexes = new int[]{
                    normalizeValueDependingOnLength(i - 4, length),
                    normalizeValueDependingOnLength(i - 3, length),
                    normalizeValueDependingOnLength(i - 2, length),
                    normalizeValueDependingOnLength(i - 1, length),
                    normalizeValueDependingOnLength(i, length),
                    normalizeValueDependingOnLength(i + 1, length),
                    normalizeValueDependingOnLength(i + 2, length),
                    normalizeValueDependingOnLength(i + 3, length),
                    normalizeValueDependingOnLength(i + 4, length)
            };

            for (int j = 0; j < 9; j++) {
                analysisL[i] = analysisL[i] + values[indexes[j]] * WaveletParameters.ANALYSIS_L[j];
                analysisH[i] = analysisH[i] + values[indexes[j]] * WaveletParameters.ANALYSIS_H[j];
            }
        }


        List<Double> sampleLow = new ArrayList<>();
        List<Double> sampleHigh = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (i % 2 == 0) {
                sampleLow.add(analysisL[i]);
            } else {
                sampleHigh.add(analysisH[i]);
            }

        }

        double[] result = new double[length];
        int index = 0;
//        Collections.sort(sampleHigh);
//        Collections.sort(sampleLow);

        for (Double value : sampleLow) {
            result[index] = value;
            index++;
        }

        for (Double value : sampleHigh) {
            result[index] = value;
            index++;
        }

        return result;
    }


}
