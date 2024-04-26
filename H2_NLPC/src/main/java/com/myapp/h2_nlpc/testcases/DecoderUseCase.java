package com.myapp.h2_nlpc.testcases;

import com.myapp.h2_nlpc.CoderAndDecoder.CoderAndDecoderTools;
import com.myapp.h2_nlpc.CoderAndDecoder.Decoder;
import com.myapp.h2_nlpc.mytools.BitWriter;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;

/**
 * Decoder test case
 */
//TODO give k and predictor type
public class DecoderUseCase {


    /**
     * the decoder
     */
    private Decoder decoder;

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
     * read decode file
     * init the decoder
     *
     * @param file the nlc file
     * @throws IOException an exception
     */
    public void initDecoder(File file) throws IOException {
        this.fileName = file.getName();
        int index = this.fileName.indexOf(".bmp");
        this.fileName = this.fileName.substring(0, index + 4);
        this.decoder = new Decoder(file);

    }

    /**
     * get the file Name
     *
     * @return the file name
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * get the predictor type
     *
     * @return the predictor type
     */
    public String getPredictorType() {
        return this.predictorType;
    }

    /**
     * get the near lossless parameter
     *
     * @return the near lossless parameter
     */
    public int getK() {
        return this.k;
    }

    /**
     * //     * start decoding quantized error
     * //     *
     * //     * @return a decoded writable image
     * //     * @throws IOException an exception
     * //
     */
    public WritableImage startDecoding() throws IOException {
        this.decoder.decodeImage();

        this.predictorType = this.decoder.getPredictorType();
        this.k = this.decoder.getK();

        int[][] data = this.decoder.getDecodedValues();


        WritableImage writableImage = new WritableImage(256, 256);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                int value = data[i][j];
                value = CoderAndDecoderTools.normalizeValue(value);
                Color grayColor = Color.gray(value / 255.0);
                pixelWriter.setColor(j, 255 - i, grayColor);
            }

        }

        return writableImage;
    }

    /***
     * get data from original image and create a frequency array for histogram
     * @return a frequency  array
     */
    public int[] getHistogram(String histogramSource) {
        int[][] data = switch (histogramSource) {
            case "Q predictor error image from decoder" -> this.decoder.getQuantizedErrorValues();
            case "DQ predictor error image from decoder" -> this.decoder.getDequantizedErrorValues();
            case "Image prediction from from decoder" -> this.decoder.getPredictedValues();
            case "Decoded image from decoder" -> this.decoder.getDecodedValues();
            default -> new int[256][256];
        };

        return CoderAndDecoderTools.getHistogramFromData(data);
    }

    /**
     * save the decoded image as bmp
     *
     * @param selectedDirectory
     */
    public void saveDecodedFile(File selectedDirectory) throws IOException {
        String imageName = this.fileName.replace(".bmp", "").trim();
        int predictorTypeIndex = 0;
        for (int i = 0; i < CoderAndDecoderTools.PREDICTOR_TYPE.length; i++) {
            if (CoderAndDecoderTools.PREDICTOR_TYPE[i].equals(this.predictorType)) {
                predictorTypeIndex = i;
                break;
            }

        }
        char saveModeFirstLetter = this.decoder.getSaveMode().charAt(0);

        String fileName = "decodedImageOf" + imageName + "k" + this.k + "p" + predictorTypeIndex + saveModeFirstLetter + ".bmp";
        File file = new File(selectedDirectory.getPath() + File.separator + fileName);

        BitWriter bitWriter = new BitWriter(file);
        for (int value : this.decoder.getImageHeader()) {
            bitWriter.writeNBits(value, 8);
        }

        int[][] data = this.decoder.getDecodedValues();
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                int value = data[i][j];
                bitWriter.writeNBits(value, 8);
            }

        }
        bitWriter.close();
    }


}
