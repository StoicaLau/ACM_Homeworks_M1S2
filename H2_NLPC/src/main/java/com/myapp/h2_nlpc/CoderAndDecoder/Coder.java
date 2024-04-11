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
     * get dequantized error values
     *
     * @return the quantized error values
     */
    public int[][] getDequantizedErrorValues() {
        return this.dequantizedErrorValues;
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
            System.out.println("type = " + type);
            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < 256; j++) {
                    if (i == 0 && j == 0)
                        this.predictedValues[i][j] = 128;
                    else if (i == 0) {
                        this.predictedValues[i][j] = this.decodedValues[i][j - 1];
                    } else if (j == 0) {
                        this.predictedValues[i][j] = this.decodedValues[i - 1][j];
                    } else {
                        int a = this.decodedValues[i][j - 1];
                        int b = this.decodedValues[i - 1][j];
                        int c = this.decodedValues[i - 1][j - 1];
                        switch (type) {
                            case "A":
                                this.predictedValues[i][j] = a;
                                break;
                            case "B":
                                this.predictedValues[i][j] = b;
                                break;
                            case "C":
                                this.predictedValues[i][j] = c;
                                break;
                            case "A+B-C":
                                this.predictedValues[i][j] = a + b - c;
                                break;
                            case "(A+B-C)/2":
                                this.predictedValues[i][j] = (a + b - c) / 2;
                                break;
                            case "B+(A-C)/2":
                                this.predictedValues[i][j] = b + (a - c) / 2;
                                break;
                            case "(A+B)/2":
                                this.predictedValues[i][j] = (a + b) / 2;
                                break;
                            case "JPEGLS":
                                if (c >= Math.max(a, b))
                                    this.predictedValues[i][j] = Math.min(a, b);
                                else if (c <= Math.min(a, b)) {
                                    this.predictedValues[i][j] = Math.max(a, b);
                                } else {
                                    this.predictedValues[i][j] = a + b - c;
                                }
                                break;
                        }
//                        this.predictedValues[i][j] = CoderAndDecoderTools.normalizeValue(this.predictedValues[i][j]);
//                        this.errorValues[i][j] = this.imageValues[i][j] - this.predictedValues[i][j];
//                        this.quantizedErrorValues[i][j] = CoderAndDecoderTools.quantizeError(this.errorValues[i][j], k);
//                        this.dequantizedErrorValues[i][j] = CoderAndDecoderTools.dequantizeError(this.quantizedErrorValues[i][j], k);
//                        this.decodedValues[i][j] = this.predictedValues[i][j] + this.dequantizedErrorValues[i][j];
//                        this.decodedValues[i][j] = CoderAndDecoderTools.normalizeValue(decodedValues[i][j]);

                    }
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
