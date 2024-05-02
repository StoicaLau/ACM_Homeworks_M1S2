package com.h3_fractal_image_coder.usecases;

import com.h3_fractal_image_coder.coderanddecoder.Decoder;
import com.h3_fractal_image_coder.mytools.BitReader;
import com.h3_fractal_image_coder.mytools.BitWriter;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Decoder use case
 */
public class DecoderUseCase {
    /**
     * the File name
     */
    String fileName;
    /**
     * the decoder
     */
    Decoder decoder;
    /**
     * image header
     */
    private List<Integer> imageHeader;

    /**
     * constructor
     *
     * @param file image file
     * @throws IOException an exception
     */
    public DecoderUseCase(File file) throws IOException {
        this.imageHeader = new ArrayList<>();
        this.fileName = file.getName();
        int index = this.fileName.indexOf(".bmp");
        this.fileName = this.fileName.substring(0, index + 4);
        BitReader bitReader = new BitReader(file);

        int headerInfoSize = 1078;
        int imageValuesSize = (int) file.length() - headerInfoSize;

        for (int i = 0; i < headerInfoSize; i++) {
            this.imageHeader.add(bitReader.readNBits(8));
        }

        int[][] imageValues = new int[512][512];
        for (int i = 0; i < imageValuesSize; i++) {
            int value = bitReader.readNBits(8);
            imageValues[511 - i / 512][i % 512] = value;
        }

        this.decoder = new Decoder(imageValues);
        bitReader.close();
    }

    /**
     * create decoded image
     *
     * @return a file with the image
     */
    public WritableImage createDecodedImage() {
        int[][] data = this.decoder.getCurrentValues();


        WritableImage writableImage = new WritableImage(512, 512);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                int value = data[i][j];
                Color grayColor = Color.gray(value / 255.0);
                pixelWriter.setColor(j, i, grayColor);
            }

        }

        return writableImage;
    }

    /**
     * get range details from a file
     *
     * @param file the file that contains range details
     * @throws IOException an exception
     */
    public void getRangesDetailsFromFile(File file) throws IOException {
        this.decoder.getRangesDetailsFromFile(file);
    }

    /**
     * decoding current values according to domains
     */
    public void decode() {
        this.decoder.decode();
    }

    /**
     * compute Peak Signal Noise Ratio
     *
     * @param originalImageValue original image value
     * @return
     */
    public double computePSNR(int[][] originalImageValue) {
        int[][] decodedValue = this.decoder.getCurrentValues();
        double dividend = Double.MIN_VALUE;
        double divisor = 0;
        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                if (originalImageValue[i][j] > dividend) {
                    dividend = originalImageValue[i][j];
                }
                divisor += (originalImageValue[i][j] - decodedValue[i][j]);
            }
        }
        dividend = dividend * dividend;
        double result = dividend / divisor;
        return 10 * Math.log(result);
    }

    /**
     * save as fic file data about ranges details
     *
     * @param selectedDirectory directory file
     * @throws IOException an exception
     */
    public void saveDecodeImage(File selectedDirectory) throws IOException {
        File file = new File(selectedDirectory.getPath() + File.separator + this.fileName + ".bmp");
        BitWriter bitWriter = new BitWriter(file);
        for (int value : this.imageHeader) {
            bitWriter.writeNBits(value, 8);
        }

        int[][] imageValues = this.decoder.getCurrentValues();
        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                bitWriter.writeNBits(imageValues[i][j], 8);
            }
        }

        bitWriter.close();
    }
}
