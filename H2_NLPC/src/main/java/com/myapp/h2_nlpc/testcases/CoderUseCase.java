package com.myapp.h2_nlpc.testcases;

import com.myapp.h2_nlpc.CoderAndDecoder.Coder;
import com.myapp.h2_nlpc.CoderAndDecoder.CoderAndDecoderTools;
import com.myapp.h2_nlpc.arithmeticcoder.CodingDecoding.ArithmeticEncoding;
import com.myapp.h2_nlpc.arithmeticcoder.model.AdaptiveModel;
import com.myapp.h2_nlpc.mytools.BitReader;
import com.myapp.h2_nlpc.mytools.BitWriter;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Coder TestCase
 */
public class CoderUseCase {

    /**
     * image header
     */
    private List<Integer> imageHeader;
    /**
     * the coder
     */
    private Coder coder;

    /**
     * the file name
     */
    private String fileName;

    /**
     * near lossless parameter
     */
    private int k;

    /**
     * predictor type
     */
    private String predictorType;

    /**
     * Constructor
     * read encode file
     * init the coder
     *
     * @param file the image file
     * @throws IOException an exception
     */
    public CoderUseCase(File file) throws IOException {

        this.fileName = file.getName();
        this.imageHeader = new ArrayList<>();

        int[][] imageValues = new int[256][256];
        BitReader bitReader = new BitReader(file);
        int headerInfoSize = 1078;
        int imageValuesSize = (int) file.length() - headerInfoSize;

        for (int i = 0; i < headerInfoSize; i++) {
            this.imageHeader.add(bitReader.readNBits(8));
        }

        for (int i = 0; i < imageValuesSize; i++) {
            int value = bitReader.readNBits(8);
            imageValues[i / 256][i % 256] = value;
        }

        this.coder = new Coder(imageValues);
        bitReader.close();
    }


    /**
     * get the prediction Type
     *
     * @return the predictor type
     */
    public String getPredictorType() {
        return predictorType;
    }

    /**
     * get the file name
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * get the near-lossless parameter
     *
     * @return the near-lossless parameter
     */
    public int getK() {
        return k;
    }

    /**
     * start to encode the image
     *
     * @param predictorType predictor Type
     * @param k             near-lossless parameter
     */
    public void startEncoding(String predictorType, int k) {
        this.predictorType = predictorType;
        this.k = k;
        coder.encodeImage(predictorType, k);
    }

    /**
     * Create the error image
     *
     * @param imageType the image type
     * @param contrast  the contrast of image
     * @return a file with the image
     */
    public WritableImage createErrorImage(String imageType, double contrast) {

        int[][] data = new int[256][256];
        if (imageType.equals("prediction error"))
            data = this.coder.getErrorValues();
        if (imageType.equals("Q prediction error"))

            data = this.coder.getQuantizedErrorValues();

        WritableImage writableImage = new WritableImage(256, 256);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                int value = (int) (data[i][j] * contrast + 128);

                value = CoderAndDecoderTools.normalizeValue(value);
                Color grayColor = Color.gray(value / 255.0);
                pixelWriter.setColor(j, 255 - i, grayColor);

            }

        }
        return writableImage;
    }


    /**
     * compute min and max error( min max of Original-Decode)
     *
     * @param decodedValues decoded values
     * @return the min value, the max value
     */
    public int[] computeError(int[][] decodedValues) {
        int[] minMaxValues = new int[2];
        minMaxValues[0] = Integer.MAX_VALUE;
        minMaxValues[1] = Integer.MIN_VALUE;

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                int result = this.coder.getImageValues()[i][j] - decodedValues[i][j];
                minMaxValues[0] = Math.min(minMaxValues[0], result);
                minMaxValues[1] = Math.max(minMaxValues[1], result);
            }
        }

        return minMaxValues;
    }

    /**
     * 1 byte predictor type;
     * 1 byte k;
     * 1 byte save mod
     * 9 bites predictor;
     * save as nlc file data about quantized error
     *
     * @param selectedDirectory directory file
     * @param saveMode          the save mode
     * @throws IOException an exception
     */
    public void saveQuantizedErrorAsNLC(File selectedDirectory, String saveMode) throws IOException {

        char saveModeFirstLetter = saveMode.charAt(0);


        int predictorTypeIndex = 0;
        for (int i = 0; i < CoderAndDecoderTools.PREDICTOR_TYPE.length; i++) {
            if (CoderAndDecoderTools.PREDICTOR_TYPE[i].equals(this.predictorType)) {
                predictorTypeIndex = i;
                break;
            }

        }

        int saveModeIndex = 0;
        for (int i = 0; i < CoderAndDecoderTools.SAVE_MODE.length; i++) {
            if (CoderAndDecoderTools.SAVE_MODE[i].equals(saveMode)) {
                saveModeIndex = i;
                break;
            }

        }
        String fileName = this.fileName + "k" + this.k + "p" + predictorTypeIndex + saveModeFirstLetter + ".nlc";
        File file = new File(selectedDirectory.getPath() + File.separator + fileName);
        BitWriter bitWriter = new BitWriter(file);


        for (int value : this.imageHeader) {
            bitWriter.writeNBits(value, 8);
        }

        bitWriter.writeNBits(predictorTypeIndex, 8);
        bitWriter.writeNBits(k, 8);
        bitWriter.writeNBits(saveModeIndex, 8);


        switch (saveMode) {
            case "Fixed 9b" -> this.saveAsFixed9b(bitWriter);
            case "Table" -> this.saveAsTable(bitWriter);
            case "Arithmetic" -> this.saveAsArithmetic(bitWriter);
        }

        bitWriter.close();
    }

    /**
     * save the quantized error in fixed 9 bits
     *
     * @param bitWriter the bit writer
     * @throws IOException an exception
     */
    private void saveAsFixed9b(BitWriter bitWriter) throws IOException {
        int[][] data = this.coder.getQuantizedErrorValues();
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {

                if (data[i][j] < 0) {
                    int value = Math.abs(data[i][j]);
                    bitWriter.writeNBits(1, 1);
                    bitWriter.writeNBits(value, 8);
                } else {
                    bitWriter.writeNBits(0, 1);
                    bitWriter.writeNBits(data[i][j], 8);
                }
            }
        }
    }


    /**
     * save as coordinates of the table
     * send line, index;
     *
     * @param bitWriter the bit writer
     */
    private void saveAsTable(BitWriter bitWriter) throws IOException {
        int[][] data = this.coder.getQuantizedErrorValues();
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                if (data[i][j] == 0) {
                    bitWriter.writeBit(0);
                    bitWriter.writeBit(0);
                } else {
                    int line;
                    for (line = 0; Math.pow(2, line) <= Math.abs(data[i][j]); line++) ;

                    int kLine = line;
                    for (; line > 0; line--) {
                        bitWriter.writeBit(1);
                    }
                    bitWriter.writeBit(0);
                    int index;
                    if (data[i][j] > 0) {
                        index = data[i][j];
                    } else {
                        index = data[i][j] + (int) Math.pow(2, kLine) - 1;

                    }

                    bitWriter.writeNBits(index, kLine);
                }

            }
        }
    }

    /**
     * save the quantized error using arithmetic code
     *
     * @param bitWriter the bit writer
     * @throws IOException an exception
     */
    private void saveAsArithmetic(BitWriter bitWriter) throws IOException {

        int[][] data = this.coder.getQuantizedErrorValues();
        AdaptiveModel adaptiveModel = new AdaptiveModel();
        ArithmeticEncoding arithmeticEncoding = new ArithmeticEncoding(bitWriter);
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                int symbol = data[i][j] + 255;
                arithmeticEncoding.encodeSymbol(symbol, adaptiveModel.getSums());
                adaptiveModel.updateModel(symbol);
            }
        }
        arithmeticEncoding.encodeSymbol(adaptiveModel.getEOF_SYMBOL(), adaptiveModel.getSums());
        arithmeticEncoding.doneEncoding();
    }

    /***
     * get data from original image and create a frequency array for histogram
     * @return a frequency  array
     */
    public int[] getHistogram(String histogramSource) {
        int[][] data = switch (histogramSource) {
            case "Original image" -> this.coder.getImageValues();
            case "Predictor error image from coder" -> this.coder.getErrorValues();
            case "Q predictor error image from coder" -> this.coder.getQuantizedErrorValues();
            case "Decoded image from coder" -> this.coder.getDecodedValues();
            default -> new int[256][256];
        };

        return CoderAndDecoderTools.getHistogramFromData(data);
    }

}
