package com.myapp.h2_nlpc.CoderAndDecoder;

import com.myapp.h2_nlpc.mytools.BitReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Used for decoding
 */
public class Decoder {

    /**
     * image header
     */
    private List<Integer> imageHeader;

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
     * predicted value
     */
    private int[][] predictedValues;

    private String predictorType;

    private int k;
    private String saveMode;

    /**
     * Constructor
     * 1 byte predictor type;
     * 1 byte k;
     * 1 byte save mod
     * 9 bites predictor;
     *
     * @param file the file for generating the decode table
     */
    public Decoder(File file) throws IOException {

        this.predictedValues = new int[256][256];
        this.quantizedErrorValues = new int[256][256];
        this.dequantizedErrorValues = new int[256][256];
        this.decodedValues = new int[256][256];
        this.imageHeader = new ArrayList<>();


        BitReader bitReader = new BitReader(file);

        int headerInfoSize = 1078;
        for (int i = 0; i < headerInfoSize; i++) {
            this.imageHeader.add(bitReader.readNBits(8));
        }

        int predictorTypeIndex = bitReader.readNBits(8);
        int kFromFile = bitReader.readNBits(8);
        int saveModeIndex = bitReader.readNBits(8);


        System.out.println(predictorTypeIndex);
        this.predictorType = CoderAndDecoderTools.PREDICTOR_TYPE[predictorTypeIndex];
        this.k = kFromFile;
        System.out.println(k);
        System.out.println(saveModeIndex);
        this.saveMode = CoderAndDecoderTools.SAVE_MODE[saveModeIndex];

        switch (this.saveMode) {
            case "Fixed 9b":
                this.setQuantizedErrorValuesFromFixedSaveMode(bitReader);
                break;
            case "Table":
                this.setQuantizedErrorValuesFromTableSaveMode(bitReader);
                break;
            case "Arithmetic":
                break;
        }


    }

    /**
     * get image header
     *
     * @return the image header
     */
    public List<Integer> getImageHeader() {
        return this.imageHeader;
    }

    /**
     * get the prediction type
     *
     * @return the prediction type
     */
    public String getPredictorType() {
        return this.predictorType;
    }

    /**
     * get the k
     *
     * @return the near-lossless parameter
     */
    public int getK() {
        return this.k;
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
     * get predicted value
     *
     * @return the predicted value
     */
    public int[][] getPredictedValues() {
        return this.predictedValues;
    }

    /**
     * get the save mode
     *
     * @return the save mode
     */
    public String getSaveMode() {
        return this.saveMode;
    }

    /**
     * set quantized error values if the format save si Fixed
     *
     * @param bitReader the bit reader
     */
    private void setQuantizedErrorValuesFromFixedSaveMode(BitReader bitReader) throws IOException {
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                int numberType = bitReader.readNBits(1);
                int value = bitReader.readNBits(8);
                if (numberType == 1)
                    value = -value;
                this.quantizedErrorValues[i][j] = value;
            }
        }
    }

    /**
     * set quantized error value if the format is Table
     *
     * @param bitReader the bit reader
     * @throws IOException an exception;
     */
    private void setQuantizedErrorValuesFromTableSaveMode(BitReader bitReader) throws IOException {
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {

                int lineValue = bitReader.readNBits(8);
                int index = bitReader.readNBits(9);
                if (lineValue == 0 && index == 0) {
                    this.quantizedErrorValues[i][j] = 0;
                } else {
                    int line = 0;
                    for (; lineValue > 0; lineValue = lineValue >> 1) {
                        int bit = lineValue & 1;
                        if (bit == 1)
                            line++;
                    }
                    if (index >= Math.pow(2, (line - 1))) {
                        this.quantizedErrorValues[i][j] = index;

                    } else {
                        this.quantizedErrorValues[i][j] = index - (int) Math.pow(2, line) + 1;
                    }


                }
            }
        }
    }


    /**
     * use a predictor type 128
     */
    private void usePredictor128() {
        for (int i = 0; i < 256; i++)
            for (int j = 0; j < 256; j++) {
                this.dequantizedErrorValues[i][j] = CoderAndDecoderTools.dequantizeError(this.quantizedErrorValues[i][j], this.k);
                this.predictedValues[i][j] = 128;
                this.decodedValues[i][j] = this.predictedValues[i][j] + this.dequantizedErrorValues[i][j];
                this.decodedValues[i][j] = CoderAndDecoderTools.normalizeValue(decodedValues[i][j]);
            }
    }

    /**
     * use a predictor to decode the image
     */
    public void decodeImage() {
        System.out.println("k" + this.k);
//        this.predictedValues = new int[256][256];
//        this.dequantizedErrorValues = new int[256][256];
//        this.decodedValues = new int[256][256];

        if (this.predictorType.equals("128")) {
            this.usePredictor128();
        } else {
            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < 256; j++) {
                    if (i == 0 && j == 0) {
                        this.predictedValues[i][j] = 128;
                    } else if (i == 0) {

                        this.predictedValues[i][j] = this.decodedValues[i][j - 1];
                        System.out.println();

                    } else if (j == 0) {
                        this.predictedValues[i][j] = this.decodedValues[i - 1][j];
                    } else {
                        int a = this.decodedValues[i][j - 1];
                        int b = this.decodedValues[i - 1][j];
                        int c = this.decodedValues[i - 1][j - 1];
                        switch (this.predictorType) {
                            case "A" -> this.predictedValues[i][j] = a;
                            case "B" -> this.predictedValues[i][j] = b;
                            case "C" -> this.predictedValues[i][j] = c;
                            case "A+B-C" -> this.predictedValues[i][j] = a + b - c;
                            case "A+(B-C)/2"-> this.predictedValues[i][j]=a+(b-c)/2;
                            case "(A+B-C)/2" -> this.predictedValues[i][j] = (a + b - c) / 2;
                            case "B+(A-C)/2" -> this.predictedValues[i][j] = b + (a - c) / 2;
                            case "(A+B)/2" -> this.predictedValues[i][j] = (a + b) / 2;
                            case "JPEGLS" -> {
                                if (c >= Math.max(a, b))
                                    this.predictedValues[i][j] = Math.min(a, b);
                                else if (c <= Math.min(a, b)) {
                                    this.predictedValues[i][j] = Math.max(a, b);
                                } else {
                                    this.predictedValues[i][j] = a + b - c;
                                }
                            }
                        }

                    }

                    this.predictedValues[i][j] = CoderAndDecoderTools.normalizeValue(this.predictedValues[i][j]);
                    this.dequantizedErrorValues[i][j] = CoderAndDecoderTools.dequantizeError(this.quantizedErrorValues[i][j], this.k);
                    this.decodedValues[i][j] = this.predictedValues[i][j] + this.dequantizedErrorValues[i][j];
                    this.decodedValues[i][j] = CoderAndDecoderTools.normalizeValue(decodedValues[i][j]);
                }
            }
        }
    }


}
