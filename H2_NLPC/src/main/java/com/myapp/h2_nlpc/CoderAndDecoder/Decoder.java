package com.myapp.h2_nlpc.CoderAndDecoder;

import com.myapp.h2_nlpc.arithmeticcoder.CodingDecoding.ArithmeticDecoding;
import com.myapp.h2_nlpc.arithmeticcoder.model.AdaptiveModel;
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


        this.predictorType = CoderAndDecoderTools.PREDICTOR_TYPE[predictorTypeIndex];
        this.k = kFromFile;
        this.saveMode = CoderAndDecoderTools.SAVE_MODE[saveModeIndex];

        switch (this.saveMode) {
            case "Fixed 9b":
                this.setQuantizedErrorValuesFromFixedSaveMode(bitReader);
                break;
            case "Table":
                this.setQuantizedErrorValuesFromTableSaveMode(bitReader);
                break;
            case "Arithmetic":
                this.setQuantizedErrorValuesFromArithmeticSaveMode(bitReader);
                break;
        }
        bitReader.close();


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


                int lineBit = bitReader.readBit();
                if (lineBit == 0) {
                    this.quantizedErrorValues[i][j] = bitReader.readBit();
                } else {
                    int line = 0;
                    while (lineBit != 0) {
                        line++;
                        lineBit = bitReader.readBit();
                    }

                    int index = bitReader.readNBits(line);
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
     * set quantized error values if the format save si Arithmetic
     *
     * @param bitReader the bit reader
     */
    private void setQuantizedErrorValuesFromArithmeticSaveMode(BitReader bitReader) throws IOException {
        AdaptiveModel adaptiveModel = new AdaptiveModel();
        ArithmeticDecoding arithmeticDecoding = new ArithmeticDecoding(bitReader);

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                int symbol = arithmeticDecoding.decodeSymbol(adaptiveModel.getSums());
                this.quantizedErrorValues[i][j] = symbol - 255;
                adaptiveModel.updateModel(symbol);
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
        if (this.predictorType.equals("128")) {
            this.usePredictor128();
        } else {
            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < 256; j++) {
                    predictedValues[i][j] = CoderAndDecoderTools.predictValue(i, j, this.predictorType, this.decodedValues);
                    this.predictedValues[i][j] = CoderAndDecoderTools.normalizeValue(this.predictedValues[i][j]);
                    this.dequantizedErrorValues[i][j] = CoderAndDecoderTools.dequantizeError(this.quantizedErrorValues[i][j], this.k);
                    this.decodedValues[i][j] = this.predictedValues[i][j] + this.dequantizedErrorValues[i][j];
                    this.decodedValues[i][j] = CoderAndDecoderTools.normalizeValue(decodedValues[i][j]);
                }
            }
        }
    }


}
