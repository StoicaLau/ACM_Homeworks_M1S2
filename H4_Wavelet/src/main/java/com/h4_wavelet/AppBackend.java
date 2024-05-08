package com.h4_wavelet;

import com.h4_wavelet.mytools.BitReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.javatuples.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Constructor class
 */
public class AppBackend {

    /**
     * file name
     */
    String fileName;
    /**
     * values of original image
     */
    private double[][] originalImageValue;
    /**
     * values of wavelet image
     */
    private double[][] waveletImageValue;

    /**
     * Empty Constructor
     */
    public AppBackend() {

    }

    /**
     * Constructor
     *
     * @param file the original image file
     * @throws IOException an exception
     */
    public AppBackend(File file) throws IOException {
        this.fileName = file.getName();
        this.originalImageValue = new double[512][512];
        this.waveletImageValue = new double[512][512];
        BitReader bitReader = new BitReader(file);
        int headerInfoSize = 1078;
        int imageValuesSize = (int) file.length() - headerInfoSize;

        for (int i = 0; i < headerInfoSize; i++) {
            bitReader.readNBits(8);
        }

        for (int i = 0; i < imageValuesSize; i++) {
            int value = bitReader.readNBits(8);
            this.originalImageValue[511 - i / 512][i % 512] = value;
            this.waveletImageValue[511 - i / 512][i % 512] = value;
        }
        bitReader.close();
    }


    /**
     * draw the original image
     *
     * @return the original image
     */
    public WritableImage getOriginalImage() {
        double[][] imageValues = this.originalImageValue;

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
     * @param lengthH length for horizontal
     * @param lengthV length for vertical
     * @param scale   scale
     * @param offset  offset
     * @return the wavelet image
     */
    public WritableImage getWaveletLvlImage(int lengthH, int lengthV, double scale, double offset) {
        double[][] imageValues = this.waveletImageValue;
        WritableImage writableImage = new WritableImage(512, 512);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < lengthV; i++) {
            for (int j = 0; j < lengthH; j++) {
                double value = normalizePixelValue(imageValues[i][j]);
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
     * compute min and max difference between original image  and wavelet image
     *
     * @return a pair consisting of min and max
     */
    public Pair<Double, Double> computeError() {
        double minError = Integer.MAX_VALUE;
        double maxError = Integer.MIN_VALUE;
        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                double result = this.originalImageValue[i][j] - this.waveletImageValue[i][j];
                if (result < minError) {
                    minError = result;
                }
                if (result > maxError) {
                    maxError = result;
                }
            }
        }
        return new Pair<>(minError, maxError);
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
     *
     * @param level the level
     */
    public void analyzeHorizontal(int level) {
        int length = (int) Math.pow(2, 10 - level);
        for (int i = 0; i < 512; i++) {
            double[] lineValues = this.waveletImageValue[i];
            double[] result = this.analyzeValues(lineValues, length);
            if (length >= 0) System.arraycopy(result, 0, this.waveletImageValue[i], 0, length);
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

    /**
     * apply  synthesis horizontal algorithm
     *
     * @param level the level
     */
    public void synthesizeHorizontal(int level) {
        int length = (int) Math.pow(2, 10 - level - 1);
        for (int i = 0; i < 512; i++) {
            double[] lineValues = this.waveletImageValue[i];
            double[] result = this.synthesizeValues(lineValues, length);
            if (length >= 0) System.arraycopy(result, 0, this.waveletImageValue[i], 0, length);
        }
    }

    /**
     * apply synthesis vertical algorithm
     *
     * @param level the level
     */
    public void synthesizeVertical(int level) {
        int length = (int) Math.pow(2, 10 - level - 1);

        for (int j = 0; j < 512; j++) {
            double[] columnValues = new double[512];
            for (int i = 0; i < 512; i++) {
                columnValues[i] = this.waveletImageValue[i][j];
            }

            double[] result = this.synthesizeValues(columnValues, length);
            for (int i = 0; i < length; i++) {
                this.waveletImageValue[i][j] = result[i];
            }
        }
    }


    /**
     * apply synthesis algorithm
     * level 1 512
     * level 2 256
     * level 3 128
     * level 4 64
     * level 5 32
     *
     * @param values values
     * @param length the length
     * @return the result of the algorithm
     */
    private double[] synthesizeValues(double[] values, int length) {
        double[] analysisL = new double[length];
        double[] analysisH = new double[length];
        for (int i = 0; i < length / 2; i++) {
            analysisL[i * 2] = values[i];
        }
        for (int i = length / 2; i < length; i++) {
            analysisH[(i - length / 2) * 2 + 1] = values[i];
        }

        double[] synthesisL = new double[length];
        double[] synthesisH = new double[length];
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
                synthesisL[i] = synthesisL[i] + analysisL[indexes[j]] * WaveletParameters.SYNTHESIS_L[j];
                synthesisH[i] = synthesisH[i] + analysisH[indexes[j]] * WaveletParameters.SYNTHESIS_H[j];
            }
        }

        double[] result = new double[length];

        for (int i = 0; i < length; i++) {
            result[i] = synthesisL[i] + synthesisH[i];
        }

        return result;
    }

    /**
     * set Wavelet Image Value
     *
     * @param file the file
     */
    public void loadWaveletImageValues(File file) {
        this.fileName = file.getName();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            this.waveletImageValue = new double[512][512];
            for (int i = 0; i < 512; i++) {
                String[] rowElements = reader.readLine().split(" ");
                for (int j = 0; j < 512; j++) {
                    this.waveletImageValue[i][j] = Double.parseDouble(rowElements[j]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param selectedDirectory selected Directory
     * @param additionalName    additional name of the file
     */
    public void saveWaveletImageValues(File selectedDirectory, String additionalName) {
        File file = new File(selectedDirectory.getPath() + File.separator + this.fileName + "_" + additionalName + ".wvl");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < 512; i++) {
                for (int j = 0; j < 512; j++) {
                    writer.write(this.waveletImageValue[i][j] + " ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
