package com.myapp.h2_nlpc.CoderAndDecoder;

import java.util.Arrays;

public class CoderAndDecoderTools {

    /**
     * Predictor type
     */
    public final static String[] PREDICTOR_TYPE = new String[]{"128", "A", "B", "C", "A+B-C", "A+(B-C)/2", "(A+B-C)/2", "B+(A-C)/2", "(A+B)/2", "JPEGLS"};

    public final static String[] CODER_OPTIONS = new String[]{"Original image", "Predictor error image from coder", "Q predictor error image from coder", "Decoded image from coder"};
    /**
     * Save mode of the quantized error matrix
     */
    public final static String[] SAVE_MODE = new String[]{"Fixed 9b", "Table", "Arithmetic"};

    /**
     * bring the value between 0 and 255
     *
     * @param value the value that will be normalized
     * @return normalized value
     */
    public static int normalizeValue(int value) {
        if (value < 0)
            return 0;
        if (value > 255)
            return 255;
        return value;
    }

    /**
     * quantize an error
     *
     * @param error the error that will be quantized
     * @param k     the near-lossless parameter
     * @return quantized error
     */
    public static int quantizeError(int error, int k) {
        double doubleResult = (double) (error + k) / (2 * k + 1);
        return (int) doubleResult;
    }

    /**
     * dequantize an error
     *
     * @param quantizedError the quantized error that will be dequantized
     * @param k              the near-lossless parameter
     * @return dequantized error
     */
    public static int dequantizeError(int quantizedError, int k) {
        return quantizedError * (2 * k + 1);
    }


    /**
     * get histogram from data
     *
     * @param data the data from histogram
     * @return an array of histogram
     */
    public static int[] getHistogramFromData(int[][] data) {
        int[] histogramData = new int[511];
        Arrays.fill(histogramData, 0);
        for (int i = 0; i < 256; i++)
            for (int j = 0; j < 256; j++) {
                int value = data[i][j];
                histogramData[value + 255]++;
            }
        return histogramData;
    }

    /**
     * predict the value at a specific line and specific row
     *
     * @param i             row
     * @param j             column
     * @param type          the type of predictor
     * @param decodedValues the decoded values matrix
     * @return the predicted value
     */
    public static int predictValue(int i, int j, String type, int[][] decodedValues) {
        int predictedValue = 128;
        if (i == 0 && j == 0)
            predictedValue = 128;
        else if (i == 0) {
            predictedValue = decodedValues[i][j - 1];
        } else if (j == 0) {
            predictedValue = decodedValues[i - 1][j];
        } else {
            int a = decodedValues[i][j - 1];
            int b = decodedValues[i - 1][j];
            int c = decodedValues[i - 1][j - 1];
            switch (type) {
                case "A":
                    predictedValue = a;
                    break;
                case "B":
                    predictedValue = b;
                    break;
                case "C":
                    predictedValue = c;
                    break;
                case "A+(B-C)/2":
                    predictedValue = a + (b - c) / 2;
                    break;
                case "A+B-C":
                    predictedValue = a + b - c;
                    break;
                case "(A+B-C)/2":
                    predictedValue = (a + b - c) / 2;
                    break;
                case "B+(A-C)/2":
                    predictedValue = b + (a - c) / 2;
                    break;
                case "(A+B)/2":
                    predictedValue = (a + b) / 2;
                    break;
                case "JPEGLS":
                    if (c >= Math.max(a, b))
                        predictedValue = Math.min(a, b);
                    else if (c <= Math.min(a, b)) {
                        predictedValue = Math.max(a, b);
                    } else {
                        predictedValue = a + b - c;
                    }
                    break;
            }
        }

        return predictedValue;
    }
}
