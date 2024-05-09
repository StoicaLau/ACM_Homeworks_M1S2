package com.myapp.h2_nlpc.CoderAndDecoder;

/**
 * used for prediction
 */
public class Coder {

    /**
     * image values
     */
    private int[][] imageValues;

    /**
     * predicted value
     */
    private int[][] predictedValues;

    /**
     * error values
     */
    private int[][] errorValues;

    /**
     * quantized error values
     */
    private int[][] quantizedErrorValues;

    /**
     * dequantized error Values;
     */
    private int[][] dequantizedErrorValues;

    /**
     *
     */
    private int[][] decodedValues;


    /**
     * Constructor
     *
     * @param imageValues image values
     */
    public Coder(int[][] imageValues) {
        this.imageValues = imageValues;
        this.predictedValues = new int[256][256];
        this.errorValues = new int[256][256];
        this.quantizedErrorValues = new int[256][256];
        this.dequantizedErrorValues = new int[256][256];
        this.decodedValues = new int[256][256];

    }


    /**
     * get image values
     *
     * @return the image values
     */
    public int[][] getImageValues() {
        return this.imageValues;
    }

    /**
     * get error values of predictor
     *
     * @return error values of predictor
     */
    public int[][] getErrorValues() {
        return this.errorValues;
    }

    /**
     * get quantized error values
     *
     * @return the quantized error values
     */
    public int[][] getQuantizedErrorValues() {
        return this.quantizedErrorValues;
    }


    /**
     * get decoded  values
     *
     * @return decoded values
     */
    public int[][] getDecodedValues() {
        return this.decodedValues;
    }


    /**
     * use a predictor type 128
     *
     * @param k the near-lossless parameter
     */
    private void usePredictor128(int k) {
        for (int i = 0; i < 256; i++)
            for (int j = 0; j < 256; j++) {
                this.predictedValues[i][j] = 128;
                this.errorValues[i][j] = this.imageValues[i][j] - this.predictedValues[i][j];
                this.quantizedErrorValues[i][j] = CoderAndDecoderTools.quantizeError(this.errorValues[i][j], k);
                this.dequantizedErrorValues[i][j] = CoderAndDecoderTools.dequantizeError(this.quantizedErrorValues[i][j], k);
                this.decodedValues[i][j] = this.predictedValues[i][j] + this.dequantizedErrorValues[i][j];
                this.decodedValues[i][j] = CoderAndDecoderTools.normalizeValue(decodedValues[i][j]);
            }
    }

    /**
     * use a predictor for the image
     *
     * @param k    the near-lossless parameter
     * @param type the image type
     */
    public void encodeImage(String type, int k) {
        if (type.equals("128"))
            usePredictor128(k);
        else {
            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < 256; j++) {
                    predictedValues[i][j] = CoderAndDecoderTools.predictValue(i, j, type, this.decodedValues);
                    this.predictedValues[i][j] = CoderAndDecoderTools.normalizeValue(this.predictedValues[i][j]);
                    this.errorValues[i][j] = this.imageValues[i][j] - this.predictedValues[i][j];
                    this.quantizedErrorValues[i][j] = CoderAndDecoderTools.quantizeError(this.errorValues[i][j], k);
                    this.dequantizedErrorValues[i][j] = CoderAndDecoderTools.dequantizeError(this.quantizedErrorValues[i][j], k);
                    this.decodedValues[i][j] = this.predictedValues[i][j] + this.dequantizedErrorValues[i][j];
                    this.decodedValues[i][j] = CoderAndDecoderTools.normalizeValue(decodedValues[i][j]);
                }
            }
        }

    }


}
