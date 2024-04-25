package com.h3_fractal_image_coder.testcases;

import com.h3_fractal_image_coder.coderanddecoder.Coder;
import com.h3_fractal_image_coder.mytools.BitReader;
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
//TODO minim la DI la fiecare ri
public class CoderTestCase {

    /**
     * image header
     */
    private List<Integer> imageHeader;

    /**
     * the coder
     */
    private Coder coder;

    /**
     * Constructor
     *
     * @param file the image file
     * @throws IOException an exception
     */
    public CoderTestCase(File file) throws IOException {
        this.imageHeader = new ArrayList<>();
        int[][] imageValues = new int[512][512];
        BitReader bitReader = new BitReader(file);
        int headerInfoSize = 1078;
        int imageValuesSize = (int) file.length() - headerInfoSize;

        for (int i = 0; i < headerInfoSize; i++) {
            this.imageHeader.add(bitReader.readNBits(8));
        }

        for (int i = 0; i < imageValuesSize; i++) {
            int value = bitReader.readNBits(8);
            imageValues[511-i / 512][i % 512] = value;
        }

        this.coder = new Coder(imageValues);
        bitReader.close();
    }

    /**
     * get range image
     *
     * @param x x index in range table
     * @param y y index in range table
     * @return an image
     */
    public WritableImage getRangeImage(int x, int y) {
        int[][] rangeValues = this.coder.getIsometricRangeBlock(x,y,7).getValues();


        WritableImage writableImage = new WritableImage(80, 80);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int i = 0; i < 80; i++) {
            for (int j = 0; j < 80; j++) {
                int value = rangeValues[i / 10][j / 10];//i=x j=x
                Color grayColor = Color.gray(value / 255.0);
                pixelWriter.setColor(j, i, grayColor);
            }
        }

        return writableImage;
    }


    public WritableImage writeImage() {
        int[][] data = this.coder.getImageValues();


        WritableImage writableImage = new WritableImage(512, 512);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 512; j++) {
                int value = data[i][j];
                Color grayColor = Color.gray(value / 255.0);
                pixelWriter.setColor(j,  i, grayColor);
            }

        }

        return writableImage;
    }
}

